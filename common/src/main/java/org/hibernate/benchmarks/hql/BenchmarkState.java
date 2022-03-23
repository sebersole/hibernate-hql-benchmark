/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.benchmarks.hql;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.benchmarks.hql.model.Component;
import org.hibernate.benchmarks.hql.model.Component2;
import org.hibernate.benchmarks.hql.model.CompositionEntity;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * @author Andrea Boriero
 */
@State(Scope.Benchmark)
public class BenchmarkState {
	private VersionSupport versionSupport;
	private HqlSemanticTreeBuilder hqlSemanticTreeBuilder;

	public HqlSemanticTreeBuilder getHqlSemanticTreeBuilder() {
		return hqlSemanticTreeBuilder;
	}

	public Supplier<PersistenceContext> getPersistenceContextAccess() {
		return versionSupport::createPersistenceContext;
	}

	@Setup
	public void setUp() {
		try {
			final ServiceLoader<VersionSupportFactory> discoveredFactories = ServiceLoader.load( VersionSupportFactory.class );

			final Iterator<VersionSupportFactory> implItr = discoveredFactories.iterator();

			if ( ! implItr.hasNext() ) {
				throw new RuntimeException( "Could not locate VersionSupportFactory service" );
			}

			final VersionSupportFactory factory = implItr.next();

			if ( implItr.hasNext() ) {
				throw new RuntimeException( "Multiple VersionSupportFactory service impls found" );
			}

			versionSupport = factory.buildVersionSupport();

			hqlSemanticTreeBuilder = versionSupport.getHqlSemanticInterpreter();

			prepareExecutionData();
		}
		catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	private void prepareExecutionData() {
		inTransaction(
				em -> {
					{
						final Component2 subComponent = new Component2( "1 : 1", "1 : 2" );
						final Component rootComponent = new Component( "root - 1", subComponent );

						final CompositionEntity entity1 = new CompositionEntity( 1, "first", rootComponent );

						em.persist( entity1 );
					}

					{
						final Component2 subComponent = new Component2( "1 : 1", "1 : 2" );
						final Component rootComponent = new Component( "root - 1", subComponent );

						final CompositionEntity entity2 = new CompositionEntity( 2, "second", rootComponent );

						em.persist( entity2 );
					}
				}
		);
	}

	private void inTransaction(Consumer<PersistenceContext> action) {
		try ( final PersistenceContext pc = versionSupport.createPersistenceContext() ) {
			final PersistenceContext.Transaction txn = pc.getTransaction();
			txn.begin();

			try {
				action.accept( pc );

				if ( !txn.isActive() ) {
					throw new RuntimeException( "Execution of action caused managed transaction to be completed" );
				}
			}
			catch (RuntimeException e) {
				if (txn.isActive()) {
					try {
						txn.rollback();
					}
					catch (Exception ignore) {
					}
				}

				throw e;
			}

			txn.commit();
		}
	}

	@TearDown
	public void tearDown() {
		try {
			cleanUpExecutionData();
		}
		catch (Throwable e) {
			System.out.println( "Error cleaning up test data" );
			e.printStackTrace();
		}

		try {
			if ( versionSupport != null ) {
				versionSupport.shutDown();
			}
		}
		catch (Throwable e) {
			System.out.println( "Error releasing VersionSupport" );
			e.printStackTrace();
		}
	}

	private void cleanUpExecutionData() {
		inTransaction(
				em -> {
					final QueryProxy<CompositionEntity> query = em.createQuery(
							"select e from CompositionEntity e",
							CompositionEntity.class
					);

					for ( CompositionEntity entity : query.getResults() ) {
						em.remove( entity );
					}
				}
		);
	}

	public void performMultiExecutions() {
		try ( final PersistenceContext pc = versionSupport.createPersistenceContext() ) {
			final QueryProxy<CompositionEntity> query1 = pc.createQuery(
					"select e from CompositionEntity e where e.description = :description",
					CompositionEntity.class
			);

			final CompositionEntity result1 = query1.setParameter( "description", "first" ).getSingleResult();
			assert result1 != null;
			assert "first".equals( result1.getDescription() );


			final QueryProxy<String> query2 = pc.createQuery(
					"select e.description from CompositionEntity e where e.description = :description",
					String.class
			);

			final String result2 = query2.setParameter( "description", "first" ).getSingleResult();
			assert result2 != null;
			assert "first".equals( result2 );


			final QueryProxy<Component> query3 = pc.createQuery(
					"select e.component from CompositionEntity e where e.description = :description",
					Component.class
			);

			final Component result3 = query3.setParameter( "description", "first" ).getSingleResult();
			assert result3 != null;
			assert "root - 1".equals( result3.getText() );
		}
	}
}

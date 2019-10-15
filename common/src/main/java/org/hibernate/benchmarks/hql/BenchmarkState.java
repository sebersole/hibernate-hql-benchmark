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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

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

	private HibernateVersionSupport versionSupport;

	private HqlSemanticTreeBuilder hqlSemanticTreeBuilder;
	private EntityManagerFactory emf;

	@Setup
	public void setUp() {
		try {
			final ServiceLoader<VersionSupportFactory> discoveredImpls = ServiceLoader.load( VersionSupportFactory.class );

			final Iterator<VersionSupportFactory> implItr = discoveredImpls.iterator();

			if ( ! implItr.hasNext() ) {
				throw new RuntimeException( "Could not locate VersionSupportFactory service" );
			}

			final VersionSupportFactory factory = implItr.next();

			if ( implItr.hasNext() ) {
				throw new RuntimeException( "Multiple VersionSupportFactory service impls found" );
			}

			versionSupport = factory.buildHibernateVersionSupport();

			hqlSemanticTreeBuilder = versionSupport.getHqlSemanticInterpreter();
			emf = versionSupport.getEntityManagerFactory();

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

	private void inTransaction(Consumer<EntityManager> action) {
		inEntityManager(
				em -> {
					EntityTransaction txn = em.getTransaction();
					txn.begin();

					try {
						action.accept( em );

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
		);
	}

	private void inEntityManager(Consumer<EntityManager> action) {
		final EntityManager entityManager = getEntityManagerFactory().createEntityManager();

		try {
			action.accept( entityManager );
		}
		finally {
			entityManager.close();
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

		try {
			if ( emf != null ) {
				emf.close();
			}
		}
		catch (Throwable e) {
			System.out.println( "Error releasing EntityManagerFactory" );
			e.printStackTrace();
		}
	}

	private void cleanUpExecutionData() {
		inTransaction(
				em -> {
					final TypedQuery<CompositionEntity> query = em.createQuery(
							"select e from CompositionEntity e",
							CompositionEntity.class
					);

					for ( CompositionEntity entity : query.getResultList() ) {
						em.remove( entity );
					}
				}
		);
	}

	public HqlSemanticTreeBuilder getHqlSemanticTreeBuilder() {
		return hqlSemanticTreeBuilder;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	public void performMultiExecutions() {
		final EntityManager entityManager = getEntityManagerFactory().createEntityManager();

		try {
			final TypedQuery<CompositionEntity> query1 = entityManager.createQuery(
					"select e from CompositionEntity e where e.description = :description",
					CompositionEntity.class
			);

			final CompositionEntity result1 = query1.setParameter( "description", "first" ).getSingleResult();
			assert result1 != null;
			assert "first".equals( result1.getDescription() );


			final TypedQuery<String> query2 = entityManager.createQuery(
					"select e.description from CompositionEntity e where e.description = :description",
					String.class
			);

			final String result2 = query2.setParameter( "description", "first" ).getSingleResult();
			assert result2 != null;
			assert "first".equals( result2 );


			final TypedQuery<Component> query3 = entityManager.createQuery(
					"select e.component from CompositionEntity e where e.description = :description",
					Component.class
			);

			final Component result3 = query3.setParameter( "description", "first" ).getSingleResult();
			assert result3 != null;
			assert "root - 1".equals( result3.getText() );
		}
		finally {
			// in this block we know creation of the EM succeeded, so close it
			try {
				entityManager.close();
			}
			catch (Exception e) {
				// ignore this here
			}
		}

	}
}

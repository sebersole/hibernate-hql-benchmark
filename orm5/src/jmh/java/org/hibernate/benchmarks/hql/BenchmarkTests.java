/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.benchmarks.hql;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.benchmarks.hql.model.CompositionEntity;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@SuppressWarnings("unused")
public class BenchmarkTests {

//	@Benchmark
//	@BenchmarkMode(Mode.AverageTime)
//	@OutputTimeUnit(TimeUnit.MICROSECONDS)
//	public Object simpleSelect(BenchmarkState state) {
//		return getSemanticModel( "select a from Animal a", state );
//	}
//
//	@Benchmark
//	@BenchmarkMode(Mode.AverageTime)
//	@OutputTimeUnit(TimeUnit.MICROSECONDS)
//	public Object simpleSelect_multiple(BenchmarkState state) {
//		return getSemanticModel(
//				"select a.serialNumber, a.mother, a.father, a.description, a.zoo from Animal a",
//				state
//		);
//	}
//
//	@Benchmark
//	@BenchmarkMode(Mode.AverageTime)
//	@OutputTimeUnit(TimeUnit.MICROSECONDS)
//	public Object simpleWhere(BenchmarkState state) {
//		return getSemanticModel( "select a from Animal a where a.serialNumber = '1337'", state );
//	}
//
//	@Benchmark
//	@BenchmarkMode(Mode.AverageTime)
//	@OutputTimeUnit(TimeUnit.MICROSECONDS)
//	public Object simpleWhere_multiple(BenchmarkState state) {
//		return getSemanticModel(
//				"select a from Animal a where a.serialNumber = '1337' and a.zoo.address.city = 'London' and a.zoo.address.country = 'US'",
//				state
//		);
//	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public Object simplePathPredicate(BenchmarkState state) {
		return getSemanticModel(
				"select a from Animal a where a.mother.description = :description",
				state
		);
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void simplePathPredicateExecution(BenchmarkState state) {
		final EntityManager entityManager = state.getEntityManagerFactory().createEntityManager();
		final TypedQuery<CompositionEntity> query = entityManager.createQuery(
				"select e from CompositionEntity e where e.description = :description",
				CompositionEntity.class
		);
		final CompositionEntity result = query.setParameter( "description", "first" ).getSingleResult();
		assert result != null;
		assert "first".equals( result.getDescription() );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public Object nestedPathPredicate(BenchmarkState state) {
		return getSemanticModel(
				"select a from Animal a where a.mother.mother.description = :description",
				state
		);
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public Object deeplyNestedPathPredicate(BenchmarkState state) {
		return getSemanticModel(
				"select a from Animal a where a.mother.mother.mother.mother.description = :description",
				state
		);
	}

	private Object getSemanticModel(String query, BenchmarkState benchmarkState) {
		return benchmarkState.getHqlSemanticTreeBuilder().buildSemanticModel( query );
	}

	private void inSession(BenchmarkState state, Consumer<Session> action) {
		try (Session session = state.getEntityManagerFactory().unwrap( SessionFactoryImplementor.class ).openSession()) {
			action.accept( session );
		}
	}

	private void inTransaction(BenchmarkState state, Consumer<Session> action) {
		inSession(
				state,
				session -> {
					Transaction txn = session.beginTransaction();

					try {
						action.accept( session );
						if ( !txn.isActive() ) {
							throw new RuntimeException( "Execution of action caused managed transaction to be completed" );
						}
					}
					catch (RuntimeException var6) {
						if (txn.isActive()) {
							try {
								txn.rollback();
							}
							catch (Exception var5) {
							}
						}

						throw var6;
					}

					txn.commit();
				}
		);
	}


	public static void main(String... args) {
		final BenchmarkTests tests = new BenchmarkTests();

		final BenchmarkState state = new BenchmarkState();
		state.setUp();

		try {
			tests.simplePathPredicateExecution( state );
		}
		finally {
			state.tearDown();
		}
	}
}

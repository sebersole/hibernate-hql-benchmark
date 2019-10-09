/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.benchmarks.hql;

import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.benchmarks.hql.model.CompositionEntity;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BenchmarkTests {

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
		try {
			final TypedQuery<CompositionEntity> query = entityManager.createQuery(
					"select e from CompositionEntity e where e.description = :description",
					CompositionEntity.class
			);
			final CompositionEntity result = query.setParameter( "description", "first" ).getSingleResult();
			assert result != null;
			assert "first".equals( result.getDescription() );
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

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public Object nestedPathPredicate(BenchmarkState state) {
		return getSemanticModel(
				"select a from Animal a where a.mother.mother.description = :description",
				state
		);
	}

//	@Benchmark
//	@BenchmarkMode( Mode.AverageTime )
//	@OutputTimeUnit(TimeUnit.MICROSECONDS)
//	public void nestedPathPredicateExecution(BenchmarkState state) {
//		final EntityManager entityManager = state.getEntityManagerFactory().createEntityManager();
//		final TypedQuery<Animal> query = entityManager.createQuery(
//				"select a from Animal a where a.mother.mother.description = :description",
//				Animal.class
//		);
//		final List<Animal> resultList = query.getResultList();
//		for ( Animal animal : resultList ) {
//			System.out.println( "Animal : " + animal );
//		}
//	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public Object deeplyNestedPathPredicate(BenchmarkState state) {
		return getSemanticModel(
				"select a from Animal a where a.mother.mother.mother.mother.description = :description",
				state
		);
	}

//	@Benchmark
//	@BenchmarkMode( Mode.AverageTime )
//	@OutputTimeUnit(TimeUnit.MICROSECONDS)
//	public void deeplyNestedPathPredicateExecution(BenchmarkState state) {
//		final EntityManager entityManager = state.getEntityManagerFactory().createEntityManager();
//		final TypedQuery<Animal> query = entityManager.createQuery(
//				"select a from Animal a where a.mother.mother.mother.mother.description = :description",
//				Animal.class
//		);
//		final List<Animal> resultList = query.getResultList();
//		for ( Animal animal : resultList ) {
//			System.out.println( "Animal : " + animal );
//		}
//	}

	private Object getSemanticModel(String query, BenchmarkState benchmarkState) {
		return benchmarkState.getHqlSemanticTreeBuilder().buildSemanticModel( query );
	}

	/**
	 * Perform simple iterations.  Intended as a simple check whether the test should succeed
	 */
	public static void main(String... args) throws Exception {
		final BenchmarkTests tests = new BenchmarkTests();

		final BenchmarkState state = new BenchmarkState();
		state.setUp();

		try {
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
			tests.simplePathPredicateExecution( state );
		}
		finally {
			state.tearDown();
		}
	}
}

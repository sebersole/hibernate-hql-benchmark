/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sebersole.benchmarks.poc;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.sebersole.benchmarks.poc.model.Animal;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@SuppressWarnings("unused")
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

//	@Benchmark
//	@BenchmarkMode( Mode.AverageTime )
//	@OutputTimeUnit(TimeUnit.MICROSECONDS)
//	public void simplePathPredicateExecution(BenchmarkState state) {
//		final EntityManager entityManager = state.getEntityManagerFactory().createEntityManager();
//		final TypedQuery<Animal> query = entityManager.createQuery(
//				"select a from Animal a where a.mother.description = :description",
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
}

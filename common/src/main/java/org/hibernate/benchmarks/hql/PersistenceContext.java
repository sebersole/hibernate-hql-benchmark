package org.hibernate.benchmarks.hql;

/**
 * Simple access to Session/EntityManager used between ORM 5 and 6 benchmarks
 * to avoid differences in JPA 2.2 (javax) and JPA 3.0 (jakarta)
 *
 * @author Steve Ebersole
 */
public interface PersistenceContext extends AutoCloseable {
	QueryProxy<?> createQuery(String hql);
	<T> QueryProxy<T> createQuery(String hql, Class<T> resultType);

	void persist(Object entity);
	void remove(Object entity);

	<T> T load(Class<T> entityType, Object id);
	<T> T loadOrProxy(Class<T> entityType, Object id);

	Transaction getTransaction();

	@Override
	void close();

	interface Transaction {
		boolean isActive();

		void begin();

		boolean commit();
		boolean rollback();
	}
}

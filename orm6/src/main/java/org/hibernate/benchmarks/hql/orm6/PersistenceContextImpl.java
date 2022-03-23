package org.hibernate.benchmarks.hql.orm6;

import org.hibernate.benchmarks.hql.PersistenceContext;
import org.hibernate.benchmarks.hql.QueryProxy;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * @author Steve Ebersole
 */
public class PersistenceContextImpl implements PersistenceContext {
	private final SessionImplementor session;
	private TransactionImpl txn;

	public PersistenceContextImpl(SessionFactoryImplementor sf) {
		this.session = (SessionImplementor) sf.openSession();
	}

	public SessionImplementor getSession() {
		return session;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public QueryProxy<?> createQuery(String hql) {
		return new QueryProxyImpl(
				this,
				session.createSelectionQuery( hql )
		);
	}

	@Override
	public <T> QueryProxy<T> createQuery(String hql, Class<T> resultType) {
		return new QueryProxyImpl<>(
				this,
				session.createSelectionQuery( hql, resultType )
		);
	}

	@Override
	public void persist(Object entity) {
		session.persist( entity );
	}

	@Override
	public void remove(Object entity) {
		session.remove( entity );
	}

	@Override
	public Transaction getTransaction() {
		if ( txn == null ) {
			txn = new TransactionImpl( this );
		}
		return txn;
	}

	@Override
	public <T> T load(Class<T> entityType, Object id) {
		return session.byId( entityType ).load( id );
	}

	@Override
	public <T> T loadOrProxy(Class<T> entityType, Object id) {
		return session.byId( entityType ).getReference( id );
	}

	@Override
	public void close() {
		session.close();
	}
}

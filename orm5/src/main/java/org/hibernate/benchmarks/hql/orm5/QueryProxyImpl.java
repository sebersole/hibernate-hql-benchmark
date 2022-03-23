package org.hibernate.benchmarks.hql.orm5;

import java.util.List;

import org.hibernate.benchmarks.hql.QueryProxy;
import org.hibernate.query.Query;

/**
 * @author Steve Ebersole
 */
public class QueryProxyImpl<T> implements QueryProxy<T> {
	private final PersistenceContextImpl producer;
	private final Query<T> query;

	public QueryProxyImpl(PersistenceContextImpl producer, Query<T> query) {
		this.producer = producer;
		this.query = query;
	}

	@Override
	public QueryProxy<T> setParameter(String name, Object value) {
		query.setParameter( name, value );
		return this;
	}

	@Override
	public List<T> getResults() {
		return query.getResultList();
	}

	@Override
	public T getSingleResult() {
		return query.getSingleResult();
	}
}

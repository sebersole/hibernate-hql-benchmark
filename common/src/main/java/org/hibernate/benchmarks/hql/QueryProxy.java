package org.hibernate.benchmarks.hql;

import java.util.List;

/**
 * @author Steve Ebersole
 */
public interface QueryProxy<T> {
	QueryProxy<T> setParameter(String name, Object value);

	List<T> getResults();
	T getSingleResult();
}

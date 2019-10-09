/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.benchmarks.hql;

/**
 * Factory for HibernateVersionSupport instances.  Used with Java's
 * {@link java.util.ServiceLoader} support.
 *
 * @author Steve Ebersole
 */
public interface VersionSupportFactory {
	/**
	 * Build the version support object
	 */
	HibernateVersionSupport buildHibernateVersionSupport();
}

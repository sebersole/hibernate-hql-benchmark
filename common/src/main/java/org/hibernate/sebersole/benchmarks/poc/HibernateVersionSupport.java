/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sebersole.benchmarks.poc;

/**
 * The main abstraction between different versions of Hibernate
 */
public interface HibernateVersionSupport {
	/**
	 * Get a delegate for performing the second step of HQL interpretation,
	 * which is to perform initial "semantic interpretation" of the parse tree
	 */
	HqlSemanticTreeBuilder getHqlSemanticInterpreter();

	/**
	 * Close the SessionFactory, etc
	 */
	void shutDown();
}


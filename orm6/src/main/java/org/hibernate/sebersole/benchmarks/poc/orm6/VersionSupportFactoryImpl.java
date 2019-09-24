/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sebersole.benchmarks.poc.orm6;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.hibernate.sebersole.benchmarks.poc.HibernateVersionSupport;
import org.hibernate.sebersole.benchmarks.poc.VersionSupportFactory;

/**
 * @author Steve Ebersole
 */
public class VersionSupportFactoryImpl implements VersionSupportFactory {
	@Override
	public HibernateVersionSupport buildHibernateVersionSupport() {
		return new VersionSupportImpl();
	}
}

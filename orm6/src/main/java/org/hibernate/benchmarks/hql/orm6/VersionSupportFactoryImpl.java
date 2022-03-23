/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.benchmarks.hql.orm6;

import org.hibernate.benchmarks.hql.VersionSupport;
import org.hibernate.benchmarks.hql.VersionSupportFactory;

/**
 * @author Steve Ebersole
 */
public class VersionSupportFactoryImpl implements VersionSupportFactory {
	@Override
	public VersionSupport buildVersionSupport() {
		return new VersionSupportImpl();
	}
}

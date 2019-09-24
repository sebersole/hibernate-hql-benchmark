/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sebersole.benchmarks.poc;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * @author Andrea Boriero
 */
@State(Scope.Benchmark)
public class BenchmarkState {

	private HibernateVersionSupport versionSupport;

	private HqlSemanticTreeBuilder hqlSemanticTreeBuilder;

	@Setup
	public void setUp() {
		try {
			final ServiceLoader<VersionSupportFactory> discoveredImpls = ServiceLoader.load( VersionSupportFactory.class );

			final Iterator<VersionSupportFactory> implItr = discoveredImpls.iterator();

			if ( ! implItr.hasNext() ) {
				throw new RuntimeException( "Could not locate VersionSupportFactory service" );
			}

			final VersionSupportFactory factory = implItr.next();

			if ( implItr.hasNext() ) {
				throw new RuntimeException( "Multiple VersionSupportFactory service impls found" );
			}

			versionSupport = factory.buildHibernateVersionSupport();

			hqlSemanticTreeBuilder = versionSupport.getHqlSemanticInterpreter();
		}
		catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@TearDown
	public void tearDown() {
		try {
			versionSupport.shutDown();
		}
		catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	public HqlSemanticTreeBuilder getHqlSemanticTreeBuilder() {
		return hqlSemanticTreeBuilder;
	}

}

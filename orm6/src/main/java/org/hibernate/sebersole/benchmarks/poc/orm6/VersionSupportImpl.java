/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sebersole.benchmarks.poc.orm6;

import javax.persistence.EntityManagerFactory;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.sebersole.benchmarks.poc.HibernateVersionSupport;
import org.hibernate.sebersole.benchmarks.poc.HqlSemanticTreeBuilder;

/**
 * @author Steve Ebersole
 */
public class VersionSupportImpl implements HibernateVersionSupport {
	private StandardServiceRegistry serviceRegistry;
	private SessionFactoryImplementor sessionFactory;

	public VersionSupportImpl() {
		serviceRegistry = new StandardServiceRegistryBuilder().build();

		sessionFactory = (SessionFactoryImplementor) new MetadataSources( serviceRegistry )
				.addResource( "benchmark.hbm.xml" )
				.buildMetadata()
				.buildSessionFactory();
	}

	@Override
	public HqlSemanticTreeBuilder getHqlSemanticInterpreter() {
		return new HqlSemanticTreeBuilderImpl( sessionFactory );
	}

	@Override
	public void shutDown() {
		if ( sessionFactory != null ) {
			try {
				sessionFactory.close();
			}
			catch (Exception ignore) {
			}
		}

		if ( serviceRegistry != null ) {
			try {
				StandardServiceRegistryBuilder.destroy( serviceRegistry );
			}
			catch (Exception ignore) {
			}
		}
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return sessionFactory;
	}
}

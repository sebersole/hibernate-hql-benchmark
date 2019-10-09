/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.benchmarks.hql.model;

import javax.persistence.Embeddable;

/**
 * @author Steve Ebersole
 */
@Embeddable
public class Component2 {
	private String attribute1;
	private String attribute2;

	public Component2() {
	}

	public Component2(String attribute1, String attribute2) {
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
	}

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}
}

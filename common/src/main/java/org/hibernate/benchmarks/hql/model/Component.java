/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.benchmarks.hql.model;

/**
 * @author Steve Ebersole
 */
@jakarta.persistence.Embeddable
@javax.persistence.Embeddable
public class Component {
	private String text;
	private Component2 subComponent;

	public Component() {
	}

	public Component(String text) {
		this.text = text;
	}

	public Component(String text, Component2 subComponent) {
		this.text = text;
		this.subComponent = subComponent;
	}

	@jakarta.persistence.Embedded
	@javax.persistence.Embedded
	public Component2 getSubComponent() {
		return subComponent;
	}

	public void setSubComponent(Component2 subComponent) {
		this.subComponent = subComponent;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

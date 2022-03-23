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
@jakarta.persistence.Entity
@jakarta.persistence.Table( name = "composition_entity")
@javax.persistence.Entity
@javax.persistence.Table( name = "composition_entity")
public class CompositionEntity {
	@jakarta.persistence.Id
	@javax.persistence.Id
	private Integer id;

	@jakarta.persistence.Basic
	@javax.persistence.Basic
	private String description;

	@jakarta.persistence.Embedded
	@javax.persistence.Embedded
	private Component component;

	public CompositionEntity() {
	}

	public CompositionEntity(
			Integer id,
			String description,
			Component component) {
		this.id = id;
		this.description = description;
		this.component = component;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
}

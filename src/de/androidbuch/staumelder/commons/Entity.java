/**
 * 
 */
package de.androidbuch.staumelder.commons;

/**
 * Persistierbares Objekt, dessen Identiti�t durch
 * eine id sichergestellt wird.
 * 
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public abstract class Entity {
	private Long id;

	
	
	protected Entity(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		final Entity rhs = (Entity) obj;
		if (id == null) {
			return (rhs.id == null);
		}
		return id.equals(rhs.id);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == id ? 0 : id.hashCode());
		return hash;
	}

}

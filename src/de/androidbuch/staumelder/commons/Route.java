/**
 * 
 */
package de.androidbuch.staumelder.commons;

import java.io.Serializable;
import java.util.List;

/**
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public class Route extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String bezeichnung;
	private String kurzBezeichnung;
	private List<Ort> stationen;
	
	public Route(Long id, String bezeichnung, String kurzBezeichnung,
			List<Ort> stationen) {
		super(id);
		this.bezeichnung = bezeichnung;
		this.kurzBezeichnung = kurzBezeichnung;
		this.stationen = stationen;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public String getKurzBezeichnung() {
		return kurzBezeichnung;
	}

	public List<Ort> getStationen() {
		return stationen;
	}
	

}

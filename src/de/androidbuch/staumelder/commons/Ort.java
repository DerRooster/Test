/**
 * 
 */
package de.androidbuch.staumelder.commons;

import java.io.Serializable;

/**
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public class Ort extends Entity implements Serializable {

	public static final Ort UNBEKANNTER_ORT = new Ort(-1L, "unbekannt", "???", null);

	private static final long serialVersionUID = 1L;
	private final String bezeichnung;
	private final String kurzBezeichnung;
	private final GpsData gpsOrtspunkt;	

	public Ort(Long id, String bezeichnung, String kurzBezeichnung, GpsData gpsOrtspunkt) {
		super(id);
		this.bezeichnung = bezeichnung;
		this.kurzBezeichnung = kurzBezeichnung;
		this.gpsOrtspunkt = gpsOrtspunkt;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public String getKurzBezeichnung() {
		return kurzBezeichnung;
	}
	
	public GpsData getGpsOrtspunkt() {
		return gpsOrtspunkt;
	}

}

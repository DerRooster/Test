/**
 * 
 */
package de.androidbuch.staumelder.stau;

import java.util.Date;

import de.androidbuch.staumelder.commons.Entity;

/**
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public class Stau extends Entity {

	private Long stauAnfangId;
	private Long stauEndeId;
	private int gesamtLaenge;
	private String stauUrsache;
	private int gesamtWartezeit;
	private int anzahlMeldungen;
	private Date letzteAktualisierung;
	private String kurzBezeichnung;
	private long routenId;

	public Stau(Long id, Long stauAnfangId, Long stauEndeId, int gesamtLaenge,
			String stauUrsache, int gesamtWartezeit, int anzahlMeldungen,
			Date letzteAktualisierung, String kurzbezeichnung, long routenId) {
		super(id);
		this.stauAnfangId = stauAnfangId;
		this.stauEndeId = stauEndeId;
		this.gesamtLaenge = gesamtLaenge;
		this.stauUrsache = stauUrsache;
		this.gesamtWartezeit = gesamtWartezeit;
		this.anzahlMeldungen = anzahlMeldungen;
		this.letzteAktualisierung = letzteAktualisierung;		
		this.kurzBezeichnung = kurzbezeichnung;
		this.routenId = routenId;
	}

	public void setStauAnfangId(Long stauAnfangId) {
		this.stauAnfangId = stauAnfangId;
	}

	public void setStauEndeId(Long stauEndeId) {
		this.stauEndeId = stauEndeId;
	}

	/**
	 * 
	 * @return Position des Unfalls etc.
	 */
	public Long getStauAnfangId() {
		return stauAnfangId;
	}

	/**
	 * 
	 * @return das aktuellste gemeldete Stauende
	 */
	public Long getStauEndeId() {
		return stauEndeId;
	}



	public String getKurzBezeichnung() {
		return kurzBezeichnung;
	}


	/**
	 * 
	 * @return Entfernung vom Stauanfang in km
	 */
	public int getGesamtLaenge() {
		return gesamtLaenge;
	}

	public String getStauUrsache() {
		return stauUrsache;
	}

	/**
	 * 
	 * @return Bisherige durchschnittl. Wartezeit aller Meldenden in Minuten.
	 */
	public int getGesamtWartezeit() {
		return gesamtWartezeit;
	}

	public int getAnzahlMeldungen() {
		return anzahlMeldungen;
	}

	public Date getLetzteAktualisierung() {
		return letzteAktualisierung;
	}

	public void setGesamtLaenge(int gesamtLaenge) {
		this.gesamtLaenge = gesamtLaenge;
	}

	public void setStauUrsache(String stauUrsache) {
		this.stauUrsache = stauUrsache;
	}

	public void setGesamtWartezeit(int gesamtWartezeit) {
		this.gesamtWartezeit = gesamtWartezeit;
	}

	public void setAnzahlMeldungen(int anzahlMeldungen) {
		this.anzahlMeldungen = anzahlMeldungen;
	}

	public void setLetzteAktualisierung(Date letzteAktualisierung) {
		this.letzteAktualisierung = letzteAktualisierung;
	}

	public long getRoutenId() {
		return routenId;
	}

	public void setRoutenId(long routenId) {
		this.routenId = routenId;
	}
	
	public String toString() {
	  StringBuffer sb = new StringBuffer();
	  sb.append("stauAnfangId: ");
	  sb.append(this.stauAnfangId);
	  sb.append("; stauEndeId: ");
    sb.append(this.stauEndeId);
    sb.append("; gesamtLaenge: ");
    sb.append(this.gesamtLaenge);
    sb.append("; stauUrsache: ");
    sb.append(this.stauUrsache);    
    sb.append("; gesamtWartezeit: ");
    sb.append(this.gesamtWartezeit);
    sb.append("; anzahlMeldungen: ");
    sb.append(this.anzahlMeldungen);    
    sb.append("; letzteAktualisierung: ");
    sb.append(this.letzteAktualisierung);
    sb.append("; routenId: ");
    sb.append(this.routenId);
    sb.append("; kurzBezeichnung: ");
    sb.append(this.kurzBezeichnung);    
    
    return sb.toString();
	}

}

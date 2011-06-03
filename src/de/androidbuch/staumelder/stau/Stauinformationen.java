package de.androidbuch.staumelder.stau;

import java.io.Serializable;

/**
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public class Stauinformationen implements Serializable {
	private static final long serialVersionUID = 1L;

	private String streckenBezeichnung;
	private String richtung;
	private String stauende;
	private Integer gesamtLaenge;
	private String stauUrsache;
	private Integer gesamtWartezeit;
	private Integer anzahlMeldungen;

	public Stauinformationen(String streckenBezeichnung, String richtung,
			String stauende, Integer gesamtLaenge, String stauUrsache,
			Integer gesamtWartezeit, Integer anzahlMeldungen) {
		this.streckenBezeichnung = streckenBezeichnung;
		this.richtung = richtung;
		this.stauende = stauende;
		this.gesamtLaenge = gesamtLaenge;
		this.stauUrsache = stauUrsache;
		this.gesamtWartezeit = gesamtWartezeit;
		this.anzahlMeldungen = anzahlMeldungen;
	}

	public Integer getAnzahlMeldungen() {
		return anzahlMeldungen;
	}

	public void setAnzahlMeldungen(Integer anzahlMeldungen) {
		this.anzahlMeldungen = anzahlMeldungen;
	}

	public String getRichtung() {
		return richtung;
	}

	public void setRichtung(String richtung) {
		this.richtung = richtung;
	}

	public String getStreckenBezeichnung() {
		return streckenBezeichnung;
	}

	public void setStreckenBezeichnung(String streckenBezeichnung) {
		this.streckenBezeichnung = streckenBezeichnung;
	}

	public String getStauende() {
		return stauende;
	}

	public void setStauende(String stauende) {
		this.stauende = stauende;
	}

	public Integer getGesamtLaenge() {
		return gesamtLaenge;
	}

	public void setGesamtLaenge(Integer gesamtLaenge) {
		this.gesamtLaenge = gesamtLaenge;
	}

	public String getStauUrsache() {
		return stauUrsache;
	}

	public void setStauUrsache(String stauUrsache) {
		this.stauUrsache = stauUrsache;
	}

	public Integer getGesamtWartezeit() {
		return gesamtWartezeit;
	}

	public void setGesamtWartezeit(Integer gesamtWartezeit) {
		this.gesamtWartezeit = gesamtWartezeit;
	}

}

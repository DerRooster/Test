/**
 * 
 */
package de.androidbuch.staumelder.stau;

import android.database.SQLException;
import de.androidbuch.staumelder.commons.Ort;

/**
 * Verantwortlich für das Speichern und Lesen von Ortsdaten aus der lokalen
 * Datenbank.
 * 
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public interface OrteSpeicher {

	Ort ladeOrt(long ortId) throws SQLException;

	void schliesseSpeicher();

	long schreibeOrt(Ort ort) throws SQLException;

	/**
	 * Stelle den Initialzustand des Speichers wieder her. Bewegungsdaten werden
	 * gelöscht.
	 */
	void reset();

}

/**
 * 
 */
package de.androidbuch.staumelder.stau;

import android.database.Cursor;
import android.database.SQLException;

/**
 * Verantwortlich für das Speichern und Lesen
 * von Stauberichtsdaten.
 * <p>
 * Jede Activity, die auf Staudaten zugreift,
 * benötigt eine Instanz des StauSpeicher.
 * 
 * @author Marcus Pant, 2009 visionera gmbh
 *
 */
public interface StauSpeicher {
	
	/**
	 * Liefert einen Cursor auf eine Liste von
	 * (ID, KURZBESCHREIBUNG) Tupeln für den Staubericht
	 * der gegebenen Route zurück.
	 * 
	 * @param routeId
	 * @return
	 * @throws SQLException
	 */
	Cursor ladeStauberichtFuerRoute(long routeId) throws SQLException ;
	
	Stau ladeStau(long stauId) throws SQLException ;
	
	long schreibeStaumeldung(Stau staumeldung) throws SQLException ;

	boolean aendereStaumeldung(Stau staumeldung) throws SQLException ;

	boolean loescheStau(long stauId) throws SQLException ;
	
	void schliesseSpeicher();
	
	/**
	 * Stelle den Initialzustand des Repositories wieder her.
	 * Bewegungsdaten werden gelöscht.
	 */
	void reset();

	// Date aktualisiereStaubericht(long routenId) throws MalformedURLException;
	
}

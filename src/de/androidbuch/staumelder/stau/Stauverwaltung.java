package de.androidbuch.staumelder.stau;

import android.os.Handler;

/**
 * @author Arno Becker, Marcus Pant, 2009 visionera gmbh
 * 
 */
public interface Stauverwaltung {

	/**
	 * Sendet eine Staumeldung an den Server.
	 * @param threadCallbackHandler
	 * @param gpsPosition
	 * @param stauPosition
	 * @param stauUrsache
	 */
	void staumeldungAbschicken(Handler threadCallbackHandler,
	    String gpsPosition, StauPosition stauPosition, String stauUrsache);
	
	/**
	 * Sendet die Routen-Id an den Server ohne auf eine Antwort zu warten. 
	 * Der Server schickt die Stauinformation zurueck, die in einem Thread 
	 * des NetworkConnectorService empfangen und verarbeitet wird.
	 * @param rId Routen-Id
	 */
	void staudatenFuerRouteAbrufen(long rId);
	
	void setRoutenId(long routenId);
	
	long getRoutenId();
		
	/** Muss aufgerufen werden, wenn eine Activity die Stauverwaltung nicht mehr braucht */
	public void disconnectNetworkService();

}
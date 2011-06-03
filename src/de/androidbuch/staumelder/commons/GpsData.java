package de.androidbuch.staumelder.commons;

/**
 * Positionsdaten speichern
 * 
 * @author Arno Becker, 2009 visionera gmbh
 * 
 */
public class GpsData {
  public long zeitstempel;
  public float geoLaenge;
  public float geoBreite;
  public float hoehe;
  
  public GpsData(long zeitstempel, float geoLaenge, float geoBreite,
      float hoehe) {
    this.zeitstempel = zeitstempel;
    this.geoLaenge = geoLaenge;
    this.geoBreite = geoBreite;
    this.hoehe = hoehe;
  }
}

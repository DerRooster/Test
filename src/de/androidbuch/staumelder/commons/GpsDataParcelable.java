package de.androidbuch.staumelder.commons;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Positionsdaten speichern. Kann fuer die Positionsbestimmung
 * durch einen Remote-Service verwendet werden.
 * 
 * @author Arno Becker, 2009 visionera gmbh
 * 
 */
public class GpsDataParcelable implements Parcelable {
  
  public long zeitstempel;
  public float geoLaenge;
  public float geoBreite;
  public float hoehe;
  
  public GpsDataParcelable() {
  }
  
  public GpsDataParcelable(long zeitstempel, float geoLaenge, float geoBreite,
      float hoehe) {
    this.zeitstempel = zeitstempel;
    this.geoLaenge = geoLaenge;
    this.geoBreite = geoBreite;
    this.hoehe = hoehe;
  }
  
  public static final Parcelable.Creator<GpsDataParcelable> CREATOR = 
      new Parcelable.Creator<GpsDataParcelable>() {
    public GpsDataParcelable createFromParcel(Parcel in) {
        return new GpsDataParcelable(in);
    }

    public GpsDataParcelable[] newArray(int size) {
        return new GpsDataParcelable[size];
    }
  };

  private GpsDataParcelable(Parcel in) {
      readFromParcel(in);
  }
  
  public void writeToParcel(Parcel out, int flags) {
    out.writeLong(zeitstempel);
    out.writeFloat(geoLaenge);
    out.writeFloat(geoBreite);
    out.writeFloat(hoehe);
  }
  
  public void readFromParcel(Parcel in) {
    zeitstempel = in.readLong();
    geoLaenge = in.readFloat();
    geoBreite = in.readFloat();
    hoehe = in.readFloat();
  }

  public int describeContents() {
    return 0;
  }

}

package de.androidbuch.staumelder.services;

import de.androidbuch.staumelder.commons.GpsDataParcelable;

interface IServiceCallback {        
  void aktuellePosition(in GpsDataParcelable gpsData);
}

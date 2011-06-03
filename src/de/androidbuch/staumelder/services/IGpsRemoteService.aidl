package de.androidbuch.staumelder.services;

import de.androidbuch.staumelder.commons.GpsDataParcelable;
import de.androidbuch.staumelder.services.IServiceCallback;

interface IGpsRemoteService {        
  void updateGpsData(out GpsDataParcelable gpsData);
  GpsDataParcelable getGpsData();  
  void setGpsData(in GpsDataParcelable gpsData);
  
  oneway void getGpsDataAsynchron();
  void registriereCallback(IServiceCallback callback);
  void entferneCallback(IServiceCallback callback);
}


package de.androidbuch.staumelder.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import de.androidbuch.staumelder.commons.GpsData;

/**
 * 
 * @author Arno Becker, 2009 visionera gmbh
 *
 */
public class GpsLocationServiceLocal extends Service {

  private static final String TAG = GpsLocationServiceLocal.class.getSimpleName();
  
  Handler uiServiceCallbackHandler;
  
  private final IBinder gpsBinder = new GpsLocalBinder();
  private LocationManager locationManager;
  private LocationListener locationListener;
  
  private class MyLocationListener implements LocationListener  {
    public void onLocationChanged(Location location) {
      Log.d(TAG, "MyLocationListener->onLocationChanged(): entered...");
      if (location != null && uiServiceCallbackHandler != null) {                          
        Message message = new Message();
        message.obj = location;
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);
        message.setData(bundle);
        uiServiceCallbackHandler.sendMessage(message);             
      }
    }    
    public void onProviderDisabled(String provider) { }

    public void onProviderEnabled(String provider) { }

    public void onStatusChanged(String provider, int status,
        Bundle extras) { }
  };

  /**
   * Klassen, die den Binder fuer den Zugriff von Clients auf diesen 
   * Service definiert. Da dieser Service immer im gleichen Prozess
   * wie der Aufrufer laeuft, ist kein IPC notwendig.
   */
  public class GpsLocalBinder extends Binder {
    public GpsLocationServiceLocal getService() {
        return GpsLocationServiceLocal.this;
    }
    public void setCallbackHandler(Handler callbackHandler) {
      uiServiceCallbackHandler = callbackHandler;
    }
    public GpsData getGpsData() {
      if (locationManager != null) {
        Location location = 
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "getGpsData(): Providers found=" + 
            locationManager.getAllProviders());
        Log.d(TAG, "getGpsData(): GPS Enabled=" + 
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        Log.d(TAG, "getGpsData(): Last Known Location=" + 
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)); 
        
        GpsData gpsData = new GpsData(location.getTime(), (float)location.getLongitude(), 
            (float)location.getLatitude(), (float)location.getAltitude());
        return gpsData;
      }
      return null;
    }
  }

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate(): LocalService starten...");    
        
    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
    locationListener = new MyLocationListener();    
    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 
        0, 
        0, 
        locationListener);    
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy(): LocalService beenden...");        
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind(): entered...");    
    return gpsBinder;        
  }

}

package de.androidbuch.staumelder.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import de.androidbuch.staumelder.commons.GpsDataParcelable;

/**
 * 
 * @author Arno Becker, 2009 visionera gmbh
 *
 */
public class GpsLocationServiceRemote extends Service {

  private static final String TAG = GpsLocationServiceRemote.class.getSimpleName();
  
  private final RemoteCallbackList<IServiceCallback> callbacks = 
    new RemoteCallbackList<IServiceCallback>();
  
  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate(): entered...");      
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy(): entered...");      
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind(): entered...");
    return gpsBinder;      
  }

  /**
   * The IRemoteInterface is defined through IDL
   */
  private final IGpsRemoteService.Stub gpsBinder = new IGpsRemoteService.Stub() {
    public void updateGpsData(GpsDataParcelable gpsData) throws RemoteException {
      Log.d(TAG, "IGpsRemoteService.Stub->updateGpsData(): entered...");
      if (gpsData != null) {
        Log.d(TAG, "IGpsRemoteService.Stub->updateGpsData(): Longitude: " + gpsData.geoLaenge);
        Log.d(TAG, "IGpsRemoteService.Stub->updateGpsData(): Latitude: " + gpsData.geoBreite);
      }
      gpsData.geoLaenge = 22.22f;
      gpsData.geoBreite = 33.33f;
      gpsData.hoehe = 44.44f;
    }
    
    public GpsDataParcelable getGpsData() throws RemoteException {
      Log.d(TAG, "IGpsRemoteService.Stub->getGpsData(): entered...");
      GpsDataParcelable gpsData = new GpsDataParcelable(System.currentTimeMillis(), 
          50.706365f, 7.115235f, 69.746456f);      
      return gpsData;
    }
    
    public void setGpsData(GpsDataParcelable gpsData) throws RemoteException  {
      Log.d(TAG, "IGpsRemoteService.Stub->setGpsData(): entered...");
      // TODO ...
    }
    
    // oneway 
    public void getGpsDataAsynchron() throws RemoteException {
      Log.d(TAG, "IGpsRemoteService.Stub->getGpsDataAsynchron(): entered...");      
      // Hier ermittelt der Service ueber den Location-Manager die
      // aktuelle Ortsposition. 
      GpsDataParcelable gpsData = new GpsDataParcelable(System.currentTimeMillis(), 
          7.115235f, 50.706365f, 69.746456f);      
      
      int anzCallbacks = callbacks.beginBroadcast();
      Log.d(TAG, "IGpsRemoteService.Stub->getGpsDataAsynchron(): Anzahl der Callbacks im Remote-Service: " + anzCallbacks);
      for (int i = 0; i < anzCallbacks; i++) {
        Log.d(TAG, "IGpsRemoteService.Stub->getGpsDataAsynchron(): Callback-Nr.: " + i);
        try {
          callbacks.getBroadcastItem(i).aktuellePosition(gpsData);
        } 
        catch (RemoteException e) {
          e.printStackTrace();
        } 
      }  
      callbacks.finishBroadcast();
    }
        
    public void registriereCallback(IServiceCallback callback) throws RemoteException {
      Log.d(TAG, "registriereCallback(): entered...");
      if (callback != null) {
        callbacks.register(callback);
      }
    }
    
    public void entferneCallback(IServiceCallback callback) throws RemoteException {
      Log.d(TAG, "entferneCallback(): entered...");
      if (callback != null) {
        callbacks.unregister(callback);
      }
    }
  };
  
}

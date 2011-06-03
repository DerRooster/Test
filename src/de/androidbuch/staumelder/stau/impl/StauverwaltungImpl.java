package de.androidbuch.staumelder.stau.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import de.androidbuch.staumelder.services.NetworkConnectorService;
import de.androidbuch.staumelder.stau.StauPosition;
import de.androidbuch.staumelder.stau.Stauverwaltung;

/**
 * 
 * @author Arno Becker, Marcus Pant, 2009 visionera gmbh
 * 
 */
public class StauverwaltungImpl implements Stauverwaltung {
  
  private static final String TAG = StauverwaltungImpl.class.getSimpleName();
  
  private NetworkConnectorService.StauLocalBinder mNetworkServiceBinder;
  private Context context;
  
  protected StauverwaltungImpl() { }
  
  public StauverwaltungImpl(Context context) {        
    Intent intent = new Intent(context, NetworkConnectorService.class);
	context.bindService(intent, localServiceConnection, Context.BIND_AUTO_CREATE);  
	this.context = context;
  }
  
  /**
   * Sendet die Routen-Id an den Server ohne auf eine Antwort zu warten. 
   * Der Server schickt die Stauinformation zurueck, die in einem Thread 
   * des NetworkConnectorService empfangen und verarbeitet wird.
   * @param rId Routen-Id
   */
  public void staudatenFuerRouteAbrufen(long rId) {
    Log.d(TAG, "staudatenFuerRouteAbrufen(): entered..."); 
    if (mNetworkServiceBinder != null) {
      mNetworkServiceBinder.staudatenFuerRouteAbrufen(rId);
    }      
  }

  public void staumeldungAbschicken(Handler threadCallbackHandler, 
      String gpsPosition, StauPosition stauPosition, String stauUrsache) {
    Log.d(TAG, "staumeldungAbschicken(): entered..."); 
    if (mNetworkServiceBinder != null) {
      mNetworkServiceBinder.staumeldungAbschicken(threadCallbackHandler,
          gpsPosition, stauPosition, stauUrsache);
    }      
  }
  
  /**
   * Baut eine Verbindung zum lokalen Service auf. Der Service laeuft im 
   * gleichen Prozess wie diese Activity. Daher wird er automatisch beendet,
   * wenn der Prozess der Activity beendet wird.
   */
  private ServiceConnection localServiceConnection = new ServiceConnection() {    
    /**
     * Wird aufgerufen, sobald die Verbindung zum lokalen Service steht.
     */
    public void onServiceConnected(ComponentName className, IBinder binder) {        
      Log.d(TAG, "onServiceConnected(): entered..."); 
      mNetworkServiceBinder = (NetworkConnectorService.StauLocalBinder)binder;         
    }

    /**
     * Wird aufgerufen, sobald die Verbindung zum Service unterbrochen wird. 
     * Dies passiert nur, wenn der Prozess, er den Service gestartet hat, stirbt.
     * Da dies ein lokaler Service ist, läuft er im selben Prozess wie diese Activity.
     * Daher kann die Methode niemals aufgrufen werden und muss nicht implementiert
     * werden.
     */
    public void onServiceDisconnected(ComponentName className) {     
      Log.d(TAG, "onServiceDisconnected(): entered..."); 
      // bei einem lokalen Service unerreichbar...
    }
  };
  
  public void setRoutenId(long routenId) {
    mNetworkServiceBinder.setRoutenId(routenId);
  }
  
  public long getRoutenId() {
	if (mNetworkServiceBinder != null) {
	  return mNetworkServiceBinder.getRoutenId();
	}
	return -1;
  }
  
  /** Muss aufgerufen werden, wenn eine Activity die Stauverwaltung nicht mehr braucht */
  public void disconnectNetworkService() {
	  context.unbindService(localServiceConnection);
  }
  
}

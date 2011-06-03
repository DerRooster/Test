package de.androidbuch.staumelder.mobilegui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import de.androidbuch.staumelder.R;
import de.androidbuch.staumelder.commons.GpsDataParcelable;
import de.androidbuch.staumelder.services.GpsLocationServiceLocal;
import de.androidbuch.staumelder.services.IGpsRemoteService;
import de.androidbuch.staumelder.services.IServiceCallback;

/**
 * Zeigt Google Maps in einer MapActivity an. Verwendet ein
 * Overlay zur Anzeige des aktuellen Ortspunkts.
 * 
 * @author Arno Becker, 2009 visionera gmbh
 *
 */
public class StrassenkarteAnzeigen extends MapActivity {
  
  private static final String TAG = StrassenkarteAnzeigen.class.getSimpleName();
  
  private MapView mapView;
  private MapController mapController;
  private MapViewOverlay mapViewOverlay;
  
  private Paint paint = new Paint();
  
  private IGpsRemoteService service = null;
    
  private final IServiceCallback serviceCallback = new IServiceCallback.Stub() {
    public void aktuellePosition(GpsDataParcelable gpsData)
        throws RemoteException {
      Log.d(TAG, "StrassenkarteAnzeigen->aktuellePosition(): entered...");
      Log.d(TAG, "StrassenkarteAnzeigen->aktuellePosition(): " + gpsData.zeitstempel);
      Log.d(TAG, "StrassenkarteAnzeigen->aktuellePosition(): " + gpsData.geoBreite);
      Log.d(TAG, "StrassenkarteAnzeigen->aktuellePosition(): " + gpsData.geoLaenge);
      Log.d(TAG, "StrassenkarteAnzeigen->aktuellePosition(): " + gpsData.hoehe);
      
      Location location = new Location(LocationManager.GPS_PROVIDER);
      location.setLatitude(gpsData.geoBreite);
      location.setLongitude(gpsData.geoLaenge);
      location.setAltitude(gpsData.hoehe);
      Bundle bundle = new Bundle();
      bundle.putParcelable("location", location);
      Message message = new Message();
      message.obj = location;
      message.setData(bundle);
      uiThreadCallbackHandler.sendMessage(message);
    }
  };
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "StrassenkarteAnzeigen->onCreate(): entered...");
    setContentView(R.layout.strassenkarte_anzeigen);
    setTitle(R.string.text_strassenkarte_titel);
    
    // Alternative 1: Local-Service:
    // starte Local-Service zum Ermitteln der GPS-Daten:
    Intent intent = new Intent(this, GpsLocationServiceLocal.class);
    bindService(intent, localServiceConnection, Context.BIND_AUTO_CREATE);
    
    // starte Remote-Service zum Ermitteln der GPS-Daten:
    /*Intent intent = new Intent(this, GpsLocationServiceRemote.class);
    bindService(intent, remoteServiceConnection, Context.BIND_AUTO_CREATE);*/

    mapView = (MapView) findViewById(R.id.mapview_strassenkarte);
    mapController = mapView.getController();   
    
    int zoomlevel = mapView.getMaxZoomLevel(); 
    Log.d(TAG, "StrassenkarteAnzeigen->onCreate(): Max Zoom-Level: " + zoomlevel);
    mapController.setZoom(zoomlevel - 2); //zoom
    
    // fuege der MapView das Overlay hinzu:
    mapViewOverlay = new MapViewOverlay(); 
    mapView.getOverlays().add(mapViewOverlay); 
    mapView.postInvalidate();
    
    LinearLayout zoomView = (LinearLayout) mapView.getZoomControls();
    zoomView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
        ViewGroup.LayoutParams.WRAP_CONTENT));
    zoomView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); 
    mapView.addView(zoomView);
    
    //mapView.setSatellite(false); // Satellitenbild
    mapView.setStreetView(true); // Strassenansicht
    
    //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
    
  }  
  
  private final Handler uiThreadCallbackHandler = new Handler() {    
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg); 
      Log.d(TAG, "Handler->handleMessage(): entered...");
      Bundle bundle = msg.getData();
      if (bundle != null) {
        Location location = (Location)bundle.get("location");
        GeoPoint geoPoint = new GeoPoint(
            (int) (location.getLatitude() * 1E6), 
            (int) (location.getLongitude() * 1E6));
        mapController.animateTo(geoPoint);          
        mapView.invalidate();                
      }  
    } 
  }; 
  
  public class MapViewOverlay extends Overlay {
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
      super.draw(canvas, mapView, shadow);
      Log.d(TAG, "MapViewOverlay->draw(): entered...");
      
      // Wandel Geopunkt in Pixel um:
      GeoPoint gp = mapView.getMapCenter();
      Point point = new Point();
      // in Point werden die relativen Pixelkoordinaten gesetzt:
      mapView.getProjection().toPixels(gp, point);
     
      // Zeichenbereich definieren:
      RectF rect = new RectF(point.x - 5, point.y + 5,
          point.x + 5, point.y - 5);
      
      // roter Punkt fuer eigenen Standort
      paint.setARGB(255, 200, 0, 30); 
      paint.setStyle(Style.FILL);
      canvas.drawOval(rect, paint);
      
      // schwarzen Kreis um den Punkt:
      paint.setARGB(255,0,0,0);
      paint.setStyle(Style.STROKE);
      canvas.drawCircle(point.x, point.y, 5, paint);     
      
      Log.d(TAG, "MapViewOverlay->draw(): leave...");
    }
  }
  
  /**
   * Baut eine Verbindung zum lokalen Service auf. Der Service laeuft im 
   * gleichen Prozess wie diese Activity. Daher wird er automatisch beendet,
   * wenn der Prozess der Activity beendet wird.
   */
  private ServiceConnection localServiceConnection = new ServiceConnection() {
    // Wird aufgerufen, sobald die Verbindung zum lokalen Service steht. 
    public void onServiceConnected(ComponentName className, IBinder binder) {        
      Log.d(TAG, "StrassenkarteAnzeigen->onServiceConnected(): entered...");       
      ((GpsLocationServiceLocal.GpsLocalBinder)binder).setCallbackHandler(uiThreadCallbackHandler);            
    }

    // Wird aufgerufen, sobald die Verbindung zum Service unterbrochen wird. 
    // Dies passiert nur, wenn der Prozess, er den Service gestartet hat, stirbt.
    // Da dies ein lokaler Service ist, läuft er im selben Prozess wie diese Activity.
    // Daher kann die Methode niemals aufgrufen werden und muss nicht implementiert
    // werden.
    public void onServiceDisconnected(ComponentName className) {        
      // unerreichbar...
    }
  };
  
  /**
   * Baut eine Verbindung zum lokalen Service auf. Der Service laeuft im 
   * gleichen Prozess wie diese Activity. Daher wird er automatisch beendet,
   * wenn der Prozess der Activity beendet wird.
   */
  private ServiceConnection remoteServiceConnection = new ServiceConnection() {
    // Wird aufgerufen, sobald die Verbindung zum Remote-Service steht. 
    public void onServiceConnected(ComponentName className, IBinder binder) {        
      Log.d(TAG, "StrassenkarteAnzeigen->onServiceConnected(): entered..."); 
      service = IGpsRemoteService.Stub.asInterface(binder);
      
      try {
        service.registriereCallback(serviceCallback);
        
        service.getGpsDataAsynchron();
      } 
      catch (RemoteException e) {
        // diese Exception wird dann geworfen, wenn der Service beendet wurde,
        // bevor wir uns mit ihm verbinden.          
        Log.e(TAG, "StrassenkarteAnzeigen->onServiceConnected(): Fehler: " + e.toString()); 
      }        
    }

    public void onServiceDisconnected(ComponentName className) {        
      try {
        service.entferneCallback(serviceCallback);
      } 
      catch (RemoteException e) {
        Log.e(TAG, "StrassenkarteAnzeigen->onServiceDisconnected(): Fehler: " + e.toString()); 
      } 
    }
  };
    
  @Override
  protected boolean isLocationDisplayed() {
    return super.isLocationDisplayed();
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
}

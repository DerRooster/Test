package de.androidbuch.staumelder.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import de.androidbuch.staumelder.stau.Stau;
import de.androidbuch.staumelder.stau.StauPosition;
import de.androidbuch.staumelder.stau.Stauverwaltung;
import de.androidbuch.staumelder.stau.impl.StauSpeicherSqliteImpl;

/**
 * Diese Klasse stellt die Verbindung zum Staumelder-Server her.
 * 
 * Die statische Variable SERVER_IP muss evtl. angepasst werden. Sie zeigt 
 * auf den Staumelder-Server bei visionera. Installiert man den Staumelderserver
 * lokal, z.B. in einem lokalen Tomcat, muss die Router-IP-Adresse 10.0.2.2
 * verwendet werden.
 * 
 * @author Arno Becker, 2009 visionera gmbh
 *
 */
public class NetworkConnectorService extends Service {
    
  private static final String TAG = NetworkConnectorService.class.getSimpleName();
  
  // private static final String SERVER_IP = "10.0.2.2";
  private static final String SERVER_IP = "78.46.42.173";
  
  private static final String urlString = "http://" + SERVER_IP + ":8081/staumelderserver/StaumelderService";
  
  private final IBinder stauServiceBinder = new StauLocalBinder();
  
  /** Empfaengt Intent, wenn sich der Status des Netzwerks aendert connect/disconnect */
  private ConnectionBroadcastReceiver mBroadcastReceiver;
  private final IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
  
  private static StauSpeicherSqliteImpl stauSpeicher;  
  
  private boolean isBusy = false;
  
  private long routenId = -1;

  private static Socket sock;
  private static BufferedReader is;
  private static PrintWriter pw;
  
  private static final int PORTNUM = 9379;

  
  private static Handler mHandler = new Handler();
  private Runnable timerTask = new Runnable() {
    public void run() {
      staudatenFuerRouteAbrufen();     
      mHandler.postDelayed(this, 10000);
    }
  };
  
  /**
   * Klassen, die den Binder fuer den Zugriff von Clients auf diesen 
   * Service definiert. Da dieser Service immer im gleichen Prozess
   * wie der Aufrufer laeuft, ist kein IPC notwendig.
   */
  public class StauLocalBinder extends Binder implements Stauverwaltung {
    public NetworkConnectorService getService() {
        return NetworkConnectorService.this;
    }
    
    public void staudatenFuerRouteAbrufen(long rId) {
      routenId = rId;
      _staudatenFuerRouteAbrufen();
    }

    public void staumeldungAbschicken(final Handler threadCallbackHandler,
        final String gpsPosition, final StauPosition stauPosition, final String stauUrsache) {
      Log.d(TAG, "StauLocalBinder->staumeldungAbschicken(): entered...");    
      
      if (isBusy) {
        return;
      }
          
      Thread thread = new Thread() {
        @Override 
        public void run() {
          isBusy = true;
          
          long result = 
            _staumeldungAbschicken(gpsPosition, stauPosition, stauUrsache);
          Message msg = new Message();
          Bundle bundle = new Bundle();
          bundle.putLong("stauId", result);
          msg.setData(bundle);
          threadCallbackHandler.sendMessage(msg);
          isBusy = false;
        }
      };  
      
      // starte den gerade definierten Thread:
      thread.start();
    }        
    
    public void setRoutenId(long route) {
      routenId = route;
    }
    
    public long getRoutenId() {
      return routenId;
    }
    
    public void connectNetworkService() { }	
	public void disconnectNetworkService() { }
  }
  
  private static class ConnectionBroadcastReceiver extends BroadcastReceiver {             
    @Override
    public void onReceive(Context ctxt, Intent intent) { 
      try {
        Log.d(TAG, "onReceive(): entered...");    
        boolean isNotConnected = 
          intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        // Zeige Warnung, solange keine Netzwerkverbindung besteht:
        if (isNotConnected) {
          Log.d(TAG, "onReceive(): Netzwerkverbindung verloren...");
          if (sock != null && sock.isConnected()) {
            Log.w(TAG, "onReceive(): still connected!");
            sock.close();            
          }
        }
        else {
          Log.d(TAG, "onReceive(): Verbindung besteht wieder. Starte Connection neu...");          
          if (netzwerkVerbindungHerstellen()) {
            starteStaudatenListener();  
          }  
        }
      } 
      catch (Exception e) {
        Log.e(TAG, "Fehler: " + e.toString());
      }
    }
  };

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate(): NetworkConnectorService starten...");       
    
    stauSpeicher = new StauSpeicherSqliteImpl(this);
    
    mBroadcastReceiver = new ConnectionBroadcastReceiver();
    this.registerReceiver(mBroadcastReceiver, intentFilter);
    
    if (netzwerkVerbindungHerstellen()) {  
      starteStaudatenListener();       
    }     
    
    mHandler.removeCallbacks(timerTask);
    mHandler.post(timerTask);
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy(): NetworkConnectorService beenden...");   
    this.unregisterReceiver(mBroadcastReceiver);
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind(): entered...");    
    return stauServiceBinder;        
  }
  
  private void _staudatenFuerRouteAbrufen() {
    staudatenFuerRouteAbrufen();
  }

  private long _staumeldungAbschicken(final String gpsPosition, 
      final StauPosition stauPosition, final String stauUrsache) {
    Log.d(TAG, "_staumeldungAbschicken(): entered...");     
        
    HttpURLConnection httpCon = null;
    OutputStream out = null;
    try {
      StringBuffer contentBuffer = new StringBuffer();
      contentBuffer.append(gpsPosition);
      contentBuffer.append("#");
      contentBuffer.append(stauPosition.toString());
      contentBuffer.append("#");
      contentBuffer.append(stauUrsache);
      contentBuffer.append("#");
      contentBuffer.append(routenId);
      Log.d(TAG, "_staumeldungAbschicken(): Content: " + contentBuffer.toString());
      
      Log.d(TAG, "_staumeldungAbschicken(): URL: " + urlString);
      URL url = new URL(urlString);
      httpCon = (HttpURLConnection) url.openConnection();      
      byte[] buff;
      httpCon.setRequestMethod("POST");
      httpCon.setDoOutput(true);
      httpCon.setDoInput(true);
      httpCon.connect();
      out = httpCon.getOutputStream();
      buff = contentBuffer.toString().getBytes("UTF8");
      out.write(buff);
      out.flush(); 
      Log.d(TAG, "_staumeldungAbschicken(): Staumeldung verschickt...");
    } 
    catch (MalformedURLException e) {
      Log.e(TAG, "_staumeldungAbschicken(): " + e.toString());
    } 
    catch (UnsupportedEncodingException e) {
      Log.e(TAG, "_staumeldungAbschicken(): " + e.toString());
    } 
    catch (IOException e) {
      Log.e(TAG, "_staumeldungAbschicken(): " + e.toString());
    }
    finally {
      if (out != null) {
        try {
          out.close();
        } 
        catch (IOException e) {
          Log.e(TAG, "_staumeldungAbschicken(): " + e.toString());
        } 
      }  
    }
    
    // lese die Antwort vom Server:
    Log.d(TAG, "_staumeldungAbschicken(): verarbeite Serverantwort...");
    String response = null;
    if (httpCon != null) {
      InputStream in = null;
      try {
        byte[] respData = null;
        if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {  
          Log.d(TAG, "_staumeldungAbschicken(): HTTP_OK");
          in = httpCon.getInputStream();
          int length = httpCon.getContentLength();
          Log.d(TAG, "_staumeldungAbschicken(): length: " + length);
          if (length > 0) {
            respData = new byte[length];
            int total = 0;
            while( total < length ){
              total += in.read(respData, total, length - total);            
            }
          }
          else {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = in.read()) != -1) {
              bytestream.write(ch);              
            }
            respData = bytestream.toByteArray();
            bytestream.close();    
          }
          in.close();
          response = new String(respData);
          Log.d(TAG, "_staumeldungAbschicken(): Response vom Server:-" + response + "-");
        }
      } 
      catch (IOException e) {
    	Log.e(TAG, "_staumeldungAbschicken(): " + e.toString());
        e.printStackTrace();
      }
      finally {
        if (in != null) {
          try {
            in.close();
          } 
          catch (IOException e) {
            Log.e(TAG, "_staumeldungAbschicken(): " + e.toString());
          } 
        }  
        if (httpCon != null) {
          httpCon.disconnect();
        }
      }
    } // end if
    
    long result = 0;
    if (response != null) {
      try {
        result = Long.parseLong(response.trim());
      } 
      catch (NumberFormatException e) {
        Log.e(TAG, "_staumeldungAbschicken(): NumberFormatException: " + response);
      }
    }  
    Log.d(TAG, "_staumeldungAbschicken(): leave...");
    return result;        
  }
  
  private static void starteStaudatenListener() {
    Log.d(TAG,"starteStaudatenListener()->entered...");
    new Thread() {
      public void run() {        
        try {
          String line;
          //boolean routenInfoGeloescht = false;          
          // blockiert, bis eine neue Zeile ankommt:
          while (sock != null && !sock.isClosed() && 
              (line = is.readLine()) != null) {
            Log.d(TAG, "run()->Vom Server uebermittelt: " + line);
            stauSpeicher.getWritableDatabase().beginTransaction();   
            
            if (line.startsWith("DELETE#")) {
              String[] infos = line.split("#");              
              stauSpeicher.getWritableDatabase().delete(StauSpeicherSqliteImpl.TABLE_NAME, 
                  StauSpeicherSqliteImpl.COL_ROUTE_ID + "=" + Long.valueOf(infos[1]), null);
            }
            else {
              // Reihenfolge der Parameter im String:
              // id, stauAnfangId, stauEndeId, gesamtLaenge,
              // stauUrsache, gesamtWartezeit, anzahlMeldungen,
              // letzteAktualisierung, kurzbezeichnung, routenId
              String[] stauDaten = line.split("#");
              Stau staumeldung = new Stau(null, // stauId, wird intern gesetzt
                  Long.valueOf(stauDaten[0]), // stauAnfangId
                  Long.valueOf(stauDaten[1]), // stauEndeId
                  Integer.valueOf(stauDaten[2]), // gesamtLaenge
                  stauDaten[3], // stauUrsache
                  Integer.valueOf(stauDaten[4]), // gesamtWartezeit
                  Integer.valueOf(stauDaten[5]), // anzahlMeldungen
                  new Date(Long.valueOf(stauDaten[6])), // letzteAktualisierung            
                  stauDaten[7], // kurzbezeichnung
                  Long.valueOf(stauDaten[8])); // routenId
              Log.d(TAG,"run()->Staumeldung: " + staumeldung.toString());
              Log.d(TAG,"run()->Staus fuer Route empfangen: " + stauDaten[7]);
              stauSpeicher.schreibeStaumeldung(staumeldung);        
            }  
            stauSpeicher.getWritableDatabase().setTransactionSuccessful();
            stauSpeicher.getWritableDatabase().endTransaction();
          }          
        } 
        catch (IOException ex) {
          Log.d(TAG,"starteStaudatenListener->run()->Fehler: " + ex.toString());
          return;
        }
        finally {
          if (stauSpeicher.getWritableDatabase().inTransaction()) {
            stauSpeicher.getWritableDatabase().endTransaction();
          }  
        }
      }
    }.start();
    Log.d(TAG,"starteStaudatenListener()->gestartet...");
  }
  
  private void staudatenFuerRouteAbrufen() {    
    if (sock != null && !sock.isClosed() && !isBusy) {
      Log.d(TAG, "staudatenFuerRouteAbrufen()->Anfrage an Server. Routen-Id = " + routenId);
      isBusy = true;
      
      pw.println(String.valueOf(routenId));
      pw.flush();
      isBusy = false;
    }     
  }
  
  private static boolean netzwerkVerbindungHerstellen() {
    Log.d(TAG, "netzwerkVerbindungHerstellen(): Netzwerkverbindung herstellen...");
    try {
      // Neu: Socket mit Timeout, falls der Server nicht verfuegbar ist:
      sock = new Socket();      
      sock.connect(new InetSocketAddress(SERVER_IP, PORTNUM), 4000);
      is   = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      pw   = new PrintWriter(sock.getOutputStream(), true);      
    } 
    catch (UnknownHostException e) {
      Log.e(TAG, "netzwerkVerbindungHerstellen: " + e.toString());
      sock = null;
      return false;
    } 
    catch (IOException e) {
      Log.e(TAG, "netzwerkVerbindungHerstellen: " + e.toString());
      sock = null;
      return false;
    } 
    catch (Exception e) {
    	Log.e(TAG, "netzwerkVerbindungHerstellen: " + e.toString());
    	sock = null;
    	return false;
    }
    return true;
  }
  
}

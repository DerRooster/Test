/**
 * 
 */
package de.androidbuch.staumelder.mobilegui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import de.androidbuch.staumelder.R;
import de.androidbuch.staumelder.stau.StauSpeicher;
import de.androidbuch.staumelder.stau.StauserviceFactory;
import de.androidbuch.staumelder.stau.Stauverwaltung;
import de.androidbuch.staumelder.stau.impl.StauSpeicherSqliteImpl;

/**
 * Gibt die Liste aller Störungsmeldungen für die aktuelle Route aus. Dient als
 * aktive Hauptseite während der Fahrt.
 * 
 * Aus diesem Dialog sind die folgenden Operationen möglich:
 * <li> Meldungen aktualisieren</li>
 * <li> Stau melden</li>
 * <li> (eigene) Staumeldung entfernen</li>
 * <li> Karte anzeigen</li>
 * <li> Route wechseln</li>
 * <li> Beenden</li>
 * 
 * @author Arno Becker, Marcus Pant, 2009 visionera gmbh
 * 
 */
public class StauberichtAnzeigen extends ListActivity {
  
  public static final String TAG = StauberichtAnzeigen.class.getSimpleName();

  public static final String IN_PARAM_ROUTENID = "ROUTEN_ID";

  private static final int ACT_STAUMELDUNG_AKTUALISIEREN = 1;
  
  private static Context context;

  private StauSpeicher stauSpeicher;
  private List<String> staubericht = new ArrayList<String>();
  
  /** Empfaengt Intent, wenn sich der Status des Netzwerks aendert connect/disconnect */
  private ConnectionBroadcastReceiver mBroadcastReceiver;
  private final IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

  private long routenId;

  private static Handler mHandler = new Handler();  
  private Runnable timerTask = new Runnable() {
      public void run() {
        zeigeStauberichtKomplex();        
        mHandler.postDelayed(this, 3000); // FIXME: nur zum Testen, sonst 10 Sekunden
      }
  };
  
  /**
   * Dieser BroadcastReceiver reagiert auf verlorene oder wiederhergestellte
   * Internetverbindungen. Es wird ein Toast angezeigt, wenn die Verbindung
   * verloren geht oder wiederhergestelt wird. Der Titel der Activity zeigt 
   * ebenfalls nach Verbindungsverlust den Status an.
   * @author Arno Becker
   *
   */
  private static class ConnectionBroadcastReceiver extends BroadcastReceiver {             
    private Toast mToast = null;
    @Override
    public void onReceive(Context ctxt, Intent intent) { 
      try {
        Log.d(TAG, "ConnectionBroadcastReceiver->onReceive(): entered...");    
        boolean isNotConnected = 
            intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        // Zeige Warnung, solange keine Netzwerkverbindung besteht:
        if (isNotConnected) {
          if (mToast == null) {
            Log.d(TAG, "ConnectionBroadcastReceiver->onReceive(): Connection verloren!");
            mToast = Toast.makeText(context, "Offline!",
                Toast.LENGTH_LONG);            
          }  
          mToast.show();
          ((ListActivity)context).setTitle(context.getString(R.string.stauberichtanzeigen_titel) + " - Offline!");
        }
        else {
          Log.d(TAG, "ConnectionBroadcastReceiver->onReceive(): Verbindung wiederhergestellt.");
          if (mToast != null) {
        	mToast = Toast.makeText(context, "Wieder online!",
                      Toast.LENGTH_LONG);    
            ((ListActivity)context).setTitle(context.getString(R.string.stauberichtanzeigen_titel) + " - Online...");
          }
        }
      } 
      catch (Exception e) {
        Log.e(TAG, "Fehler: " + e.toString());
      }
    }
  };

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setContentView(R.layout.staubericht_anzeigen);
    setTitle(R.string.stauberichtanzeigen_titel);
    
    context = this;
    
    // TimerTask zum UI-Thread hinzufuegen:
    mHandler.post(timerTask);

    final Bundle extras = getIntent().getExtras();
    if (extras != null) {
        routenId = extras.getLong(IN_PARAM_ROUTENID);            
    }    
  }
  
  @Override
  protected void onStart() {
	  stauSpeicher = new StauSpeicherSqliteImpl(this);
	    
	  mBroadcastReceiver = new ConnectionBroadcastReceiver();
	  context.registerReceiver(mBroadcastReceiver, intentFilter);
	  
	  zeigeStauberichtKomplex();	  
      super.onStart();
  }

  @Override
  protected void onStop() {
    stauSpeicher.schliesseSpeicher();
    context.unregisterReceiver(mBroadcastReceiver);
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    getMenuInflater().inflate(R.menu.menue_staubericht_anzeigen, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
	    case R.id.opt_staumeldungErfassen: {
	      final Intent i = new Intent(this, StaumeldungErfassen.class);
	      startActivity(i);
	      return true;
	    }
	    case R.id.opt_staumeldungBearbeiten: {
	      final Intent i = new Intent(this, StaumeldungErfassen.class);
	      i.putExtra(StauinfoAnzeigen.IN_PARAM_STAUID,getSelectedItemId());
	      startActivityForResult(i, ACT_STAUMELDUNG_AKTUALISIEREN);
	      return true;
	    }
	    case R.id.opt_aktualisieren: {
	      Stauverwaltung stauVerwaltung = StauserviceFactory.getStauVerwaltung(this);
	      stauVerwaltung.staudatenFuerRouteAbrufen(routenId);	      
	      zeigeStaubericht(routenId);
	      return true;
	    }
	    case R.id.opt_routeFestlegen: {
	      final Intent i = new Intent(this, RouteFestlegen.class);
	      startActivity(i);
	      return true;
	    }
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    
    switch(requestCode) {
	    case ACT_STAUMELDUNG_AKTUALISIEREN:
	      zeigeStaubericht(routenId);
	      break;
    }
  }

  private void zeigeStauberichtKomplex() {
    Log.d(TAG, "zeigeStauberichtKomplex()->aktualisiere Staubericht fuer Route " + routenId + " um "
        + new Date(System.currentTimeMillis()));

    Cursor stauberichtCursor = stauSpeicher.ladeStauberichtFuerRoute(routenId);
    startManagingCursor(stauberichtCursor);

    // Liste der anzuzeigenden Tabellespalten...
    String[] from = new String[] { StauSpeicherSqliteImpl.COL_STAUURSACHE };
    // und der zugehörigen GUI Felder
    int[] to = new int[] { android.R.id.text1 };

    SimpleCursorAdapter dspStaubericht = new SimpleCursorAdapter(this,
        android.R.layout.simple_list_item_1, stauberichtCursor, from, to);
    setListAdapter(dspStaubericht);
  }
  
  private void zeigeStaubericht(long routenId) {
    ListAdapter dspStaubericht = 
      new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,staubericht);
    setListAdapter(dspStaubericht);
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Intent i = new Intent(this, StauinfoAnzeigen.class);
    i.putExtra(StauinfoAnzeigen.IN_PARAM_STAUID, id);
    startActivity(i);
  }

}

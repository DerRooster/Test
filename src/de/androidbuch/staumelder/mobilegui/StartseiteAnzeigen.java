/**
 * 
 */
package de.androidbuch.staumelder.mobilegui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import de.androidbuch.staumelder.R;
import de.androidbuch.staumelder.stau.StauserviceFactory;

/**
 * Stellt das Layout der Startseite bereit. Das Layout ist abhängig vom Zustand
 * der Anwendung.
 * 
 * @author Marcus Pant, Arno Becker, 2009 visionera gmbh
 * 
 */
public class StartseiteAnzeigen extends Activity {
	
  private static final String TAG = StartseiteAnzeigen.class.getSimpleName();

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    // getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);

    setContentView(R.layout.startseite_anzeigen);
    setTitle(R.string.startseiteanzeigen_titel);
    
    // initialisieren der Stauverwaltung. Startet den Netzwerk-Service:
    StauserviceFactory.getStauVerwaltung(this);

    Button sfStarteRoutenauswahl = 
      (Button) findViewById(R.id.sf_starte_routenauswahl);
    sfStarteRoutenauswahl.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View view) {
            Intent i = new Intent(
                getApplicationContext(), 
                RouteFestlegen.class);
            startActivity(i);
          }
    });
    
    Button sfStaumeldung = 
      (Button)findViewById(R.id.sf_erfasse_staumeldung);
    sfStaumeldung.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View view) {        	  
            Intent i = new Intent(
                getApplicationContext(),
                StaumeldungErfassen.class);
            startActivity(i);            
          }
    });
    
    Button butStrassenkarteAnzeigen = 
      (Button)findViewById(R.id.opt_strassenkarte_anzeigen);
    butStrassenkarteAnzeigen.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View view) {
            Intent i = new Intent(
                getApplicationContext(),
                StrassenkarteAnzeigen.class);
            startActivity(i);
          }
    });

    registerForContextMenu(sfStarteRoutenauswahl);
    registerForContextMenu(butStrassenkarteAnzeigen);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.hauptmenue, menu);
    
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
	    case R.id.opt_einstellungenAnzeigen: {    	
	      Intent i = new Intent(this, EinstellungenBearbeiten.class);
	      startActivity(i);
	      return true;
	    }
	    case R.id.opt_staumelderBeenden: {
	      finish();
	      return true;
	    }
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    if (v.getId() == R.id.sf_starte_routenauswahl) {
      getMenuInflater().inflate(R.menu.demo_langes_menue, menu);
    }
    if (v.getId() == R.id.opt_strassenkarte_anzeigen) {
      getMenuInflater().inflate(R.menu.demo_kurzes_menue, menu);
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {	    
	    case R.id.opt_deaktivieren: {
	    	Log.d(TAG, "onContextItemSelected(): Zur Uebung: Deaktiviere diesen Menuepunkt..:");
	    }
        case R.id.opt_hilfe: {
	    	Log.d(TAG, "onContextItemSelected(): Zur Uebung: starte Web-Browser zur Anzeihe einer Hilfe-Seite...");
	    }
    }
    return super.onContextItemSelected(item);
  }

}

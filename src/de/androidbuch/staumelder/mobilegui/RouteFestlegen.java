/**
 * 
 */
package de.androidbuch.staumelder.mobilegui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.androidbuch.staumelder.R;
import de.androidbuch.staumelder.stau.StauserviceFactory;
import de.androidbuch.staumelder.stau.Stauverwaltung;

/**
 * Stellt den Dialog fuer einen Routenwechsel dar.
 * 
 * Aus diesem Dialog ist die folgenden Operation moeglich:
 * <li> Starten ( -> Staubericht anzeigen )
 * 
 * @author Marcus Pant, Arno Becker, 2009 visionera gmbh
 * 
 */
public class RouteFestlegen extends Activity {
  
  private static final String TAG = RouteFestlegen.class.getSimpleName();
  
  public static final int ROUTE_STARTEN_ID = Menu.FIRST;
  static final String IN_PARAM_ROUTENID = "ROUTEN_ID";
    
  private Stauverwaltung stauVerwaltung;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);

		setContentView(R.layout.route_festlegen);
		setTitle(R.string.routefestlegen_titel);
		
		stauVerwaltung = StauserviceFactory.getStauVerwaltung(this.getApplicationContext());
		
		aktualisiereRoutenliste();
	}
	
	protected void onStop() {
		final Spinner routenAuswahl = (Spinner) findViewById(R.id.routenAuswahl);
        long routenId = routenAuswahl.getSelectedItemId() + 1;
        stauVerwaltung.setRoutenId(routenId);
		super.onStop();
	}

	private void aktualisiereRoutenliste() {
		// Listeneinträge konfigurieren (siehe /values/arrays.xml)
	    final Spinner routenAuswahl = (Spinner)findViewById(R.id.routenAuswahl);
	    final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	        this, R.array.routen, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(
	        android.R.layout.simple_spinner_dropdown_item);    
	    routenAuswahl.setAdapter(adapter);	    
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    final boolean result = super.onCreateOptionsMenu(menu);
	    menu.add(0, ROUTE_STARTEN_ID, 0, R.string.routefestlegen_routenStarten);
	    return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case ROUTE_STARTEN_ID:        
		        final Spinner routenAuswahl = (Spinner) findViewById(R.id.routenAuswahl);
		        long routenId = routenAuswahl.getSelectedItemId() + 1;
		        Log.d(TAG, "onOptionsItemSelected: zeige Staudaten fuer Route an: " + routenId);
		        stauVerwaltung.staudatenFuerRouteAbrufen(routenId);
		        final Intent i = new Intent(this, StauberichtAnzeigen.class);
		        i.putExtra(StauberichtAnzeigen.IN_PARAM_ROUTENID, routenId);
		        startActivity(i);                  
	    }
	    return super.onOptionsItemSelected(item);
    }
    
}

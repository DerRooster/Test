package de.androidbuch.staumelder.mobilegui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import de.androidbuch.staumelder.R;
import de.androidbuch.staumelder.stau.StauPosition;
import de.androidbuch.staumelder.stau.StauserviceFactory;
import de.androidbuch.staumelder.stau.Stauverwaltung;

/**
 * 
 * @author Arno Becker, Marcus Pant, 2009 visionera gmbh
 * 
 */
public class StaumeldungErfassen extends Activity {
	
	private static final String TAG = StaumeldungErfassen.class.getSimpleName();
	
	public static final String STAU_ID = "STAU_ID";	
	public static final int ABSCHICKEN_ID = Menu.FIRST;

	private long stauId = 0;

	private ProgressDialog progressDialog;
	private Stauverwaltung stauVerwaltung;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);

		setContentView(R.layout.staumeldung_erfassen);
		setTitle(R.string.staumeldungerfassen_titel);

		stauVerwaltung = StauserviceFactory.getStauVerwaltung(this);
		if (stauVerwaltung.getRoutenId() < 0) {
			final Toast hinweis = Toast.makeText(this, R.string.staumeldungerfassen_keineRoute,
                    Toast.LENGTH_LONG);
            hinweis.show();
            this.finish();
		}
		
		// Listeneinträge konfigurieren
		final Spinner stauUrsacheAuswahl = (Spinner) findViewById(R.id.stauUrsache);
		final ArrayAdapter<CharSequence> adapter = 
			ArrayAdapter.createFromResource(this, R.array.stauUrsachen,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stauUrsacheAuswahl.setAdapter(adapter);			
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final boolean result = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, ABSCHICKEN_ID, Menu.NONE, R.string.staumeldungerfassen_melden);
		return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected(): entered...");
		switch (item.getItemId()) {
			case ABSCHICKEN_ID:
				stauMelden();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void stauMelden() {
		final Spinner stauUrsache = (Spinner) findViewById(R.id.stauUrsache);
		final RadioGroup aktuellePosition = (RadioGroup) findViewById(R.id.position);
		Log.d(TAG, "stauMelden(): Stauursache: " + stauUrsache.getSelectedItem());
		Log.d(TAG, "stauMelden(): Position: " + aktuellePosition.getCheckedRadioButtonId());
		StauPosition stauPosition = StauPosition.STAUENDE;
		if (R.id.stauAnfang == aktuellePosition.getCheckedRadioButtonId()) {
			stauPosition = StauPosition.STAUANFANG;
		}
		progressDialog = ProgressDialog.show(this, "Datenübertragung...",
				"Melde Stau an Server", true, false);
		stauVerwaltung.staumeldungAbschicken(getHandler(), getCurrentGps(),
				stauPosition, stauUrsache.getSelectedItem().toString());
	}

	/** Handler für das Handling der Message-Queue */
	private Handler getHandler() {
		final Handler threadCallbackHandler = new Handler() {
			public void handleMessage(Message msg) {
				Log.d(TAG, "Handler->handleMessage(): entered...");
				Bundle bundle = msg.getData();
				stauId = bundle.getLong("stauId");
				Log.d(TAG, "Handler->handleMessage(): stauId: " + stauId);
				progressDialog.dismiss();
				final Intent intent = new Intent(getApplicationContext(),
						StauinfoAnzeigen.class);
				intent.putExtra(STAU_ID, stauId);
				startActivity(intent);
				super.handleMessage(msg);
			}
		};
		return threadCallbackHandler;
	}

	private String getCurrentGps() {
		return "Koordinaten/Teststau";
	}

}
/**
 * 
 */
package de.androidbuch.staumelder.mobilegui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import de.androidbuch.staumelder.R;

/**
 * Zeigt die Liste der verfügbaren Programmeinstellungen und ihre Werte an. Es
 * besteht die Möglichkeit, die Einstellungen zu ändern und zu speichern.
 * 
 * @author Marcus Pant, Arno Becker, 2009 visionera gmbh
 * 
 */
public class EinstellungenBearbeiten extends PreferenceActivity {
	public static final String EINSTELLUNGEN_NAME = EinstellungenBearbeiten.class.getSimpleName();

	// Menueoptionen
	private static final int EINSTELLUNG_BEARBEITEN_ID = Menu.FIRST;
	private static final int ZURUECK_ID = Menu.FIRST + 1;
	private static final int STAUMELDER_BEENDEN_ID = Menu.FIRST + 2;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);

    this.addPreferencesFromResource(R.xml.staumelder_einstellungen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, EINSTELLUNG_BEARBEITEN_ID, 0,
				R.string.app_einstellungBearbeiten);
		menu.add(0, ZURUECK_ID, 0, R.string.app_zurueck);
		menu.add(0, STAUMELDER_BEENDEN_ID, 0, R.string.app_staumelderBeenden);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case STAUMELDER_BEENDEN_ID:
				finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    SharedPreferences settings = getSharedPreferences(EINSTELLUNGEN_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	
	    editor.commit();
	}	
}

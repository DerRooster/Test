/**
 * 
 */
package de.androidbuch.staumelder.mobilegui;

import android.app.Activity;
import android.os.Bundle;
import de.androidbuch.staumelder.R;

/**
 * Dialog zur Bearbeitung oder Loeschung eines Staumeldungs-Eintrags. Nach der
 * Freigabe wird die �nderung an den Server uebertragen. Es koennen nur Stati
 * bearbeitet werden, zu denen mindestens eine Meldung vorliegt. Es d�rfen nur
 * Meldungen geloescht werden, die der aktuelle Nutzer selbst erfa�t hat.
 * 
 * @author Marcus Pant
 * 
 */
public class StaumeldungBearbeiten extends Activity {
  
  	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);

		// prinzipiell gleiches layout wie bei erfassung. aber einschr�nkungen der
		// funktionen
		setContentView(R.layout.staumeldung_erfassen);

		setTitle(R.string.staumeldungerfassen_titel);
	}

}

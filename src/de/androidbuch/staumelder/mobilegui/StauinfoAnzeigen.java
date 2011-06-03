package de.androidbuch.staumelder.mobilegui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import de.androidbuch.staumelder.R;
import de.androidbuch.staumelder.stau.Stau;
import de.androidbuch.staumelder.stau.StauSpeicher;
import de.androidbuch.staumelder.stau.impl.StauSpeicherSqliteImpl;

/**
 * Stellt den Dialog zur Anzeige aller bisher angelaufenen, vom Server
 * gemeldeten Stauinformationen für einen gegebenen Stau bereit.
 * 
 * @author Marcus Pant, Arno Becker, 2009 visionera gmbh
 * 
 */
public class StauinfoAnzeigen extends Activity {
  
    public static final String TAG = StauinfoAnzeigen.class.getSimpleName();
  
	static final String IN_PARAM_STAUID = "STAU_ID";

	private StauSpeicher stauRepository;
	private Long stauId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stauinfo_anzeigen);
		setTitle(R.string.stauinfoanzeigen_titel);

		stauId = savedInstanceState != null ? savedInstanceState.getLong(IN_PARAM_STAUID) : null;		
		if (stauId == null) {
			Bundle extras = getIntent().getExtras();
			stauId = extras != null ? extras.getLong(IN_PARAM_STAUID) : null;
		}
		else {			
			setResult(RESULT_OK);
		}
		Log.d(TAG, "onCreate(): stauId = " + stauId);
	}
	
	@Override
	protected void onStart() {
		stauRepository = new StauSpeicherSqliteImpl(this);
		zeigeStaudaten(stauId.longValue());
	    super.onStart();
	}
		
	@Override
	protected void onStop() {
        stauRepository.schliesseSpeicher();
	    super.onStop();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	  super.onSaveInstanceState(outState);
	  outState.putLong(IN_PARAM_STAUID, stauId);
	}
	  
	private void zeigeStaudaten(long stauId) {	
	    Stau stau = stauRepository.ladeStau(stauId);
	    Log.d(TAG, "onCreate(): stau = " + stau);
	    if (stau == null) {
	      return;
	    }
	    final TextView fldAnzahlMeldungen = (TextView) findViewById(R.id.anzahlMeldungen);
	    fldAnzahlMeldungen.setText(String.valueOf(stau.getAnzahlMeldungen()));
	    
	    final TextView fldStauAnfang = (TextView) findViewById(R.id.stauAnfang);
	    fldStauAnfang.setText(String.valueOf(stau.getStauAnfangId()));
	    
	    final TextView fldStauEnde = (TextView) findViewById(R.id.stauEnde);
	    fldStauEnde.setText(String.valueOf(stau.getStauEndeId()));
	    
	    final TextView fldStauUrsache = (TextView) findViewById(R.id.stauUrsache);
	    fldStauUrsache.setText(stau.getStauUrsache());
	
	    final TextView fldKurzbezeichnung = (TextView) findViewById(R.id.kurzbezeichnung);
	    fldKurzbezeichnung.setText(stau.getKurzBezeichnung());
	}
}

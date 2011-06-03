package de.androidbuch.staumelder.stau;

import android.content.Context;
import de.androidbuch.staumelder.stau.impl.StauverwaltungImpl;

/**
 *
 * @author Marcus Pant, 2009 visionera gmbh
 *
 */
public class StauserviceFactory {

	private static Stauverwaltung stauVerwaltung;

	public static Stauverwaltung getStauVerwaltung(Context context) {
		if (stauVerwaltung == null) {
      	  stauVerwaltung = new StauverwaltungImpl(context);      	  
		}
		return stauVerwaltung;
	}

}

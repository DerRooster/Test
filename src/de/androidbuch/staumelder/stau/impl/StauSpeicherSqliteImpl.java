/**
 * 
 */
package de.androidbuch.staumelder.stau.impl;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.androidbuch.staumelder.commons.StaumelderKonstanten;
import de.androidbuch.staumelder.stau.Stau;
import de.androidbuch.staumelder.stau.StauSpeicher;

/**
 * Verantwortlich für die Verwaltung der Datenbanktabelle STAU sowie die
 * Verbindung zum StaumelderService via HTTP
 * 
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public class StauSpeicherSqliteImpl extends SQLiteOpenHelper implements
		StauSpeicher {

	private static final String TAG = StauSpeicherSqliteImpl.class
			.getSimpleName();

	public static final String COL_STAU_ID = "_id";
	public static final String COL_KURZBEZEICHNUNG = "kurzbezeichnung";
	public static final String COL_ROUTE_ID = "route_id";
	public static final String COL_STAUANFANG_ID = "stauanfang_id";
	public static final String COL_STAUENDE_ID = "stauende_id";
	public static final String COL_GESAMTLAENGE = "gesamtlaenge";
	public static final String COL_STAUURSACHE = "stauursache";
	public static final String COL_WARTEZEIT = "wartezeit";
	public static final String COL_ANZAHLMELDUNGEN = "anzahlmeldungen";
	public static final String COL_LETZTEAENDERUNG = "letzteaenderung";

	public static final String TABLE_NAME = "stau";

	private static final String SQL_DATABASE_CREATE = "create table stau (_id integer primary key autoincrement, "
			+ "route_id integer not null, "
			+ "stauanfang_id integer, stauende_id integer, "
			+ "gesamtlaenge integer, stauursache text, "
			+ "wartezeit integer, anzahlmeldungen integer, "
			+ "letzteaenderung integer, kurzbezeichnung text not null);";
	
	public StauSpeicherSqliteImpl(Context context) {
		super(context, StaumelderKonstanten.DATENBANK_NAME, null,
				StaumelderKonstanten.DATENBANK_VERSION);		
	}

	public boolean aendereStaumeldung(Stau staumeldung) throws SQLException {
		return false;
	}

	public Stau ladeStau(long stauId) throws SQLException {
		Cursor c = getReadableDatabase().query(
				true,
				TABLE_NAME,
				new String[] { COL_STAU_ID, COL_KURZBEZEICHNUNG,
						COL_ANZAHLMELDUNGEN, COL_GESAMTLAENGE,
						COL_LETZTEAENDERUNG, COL_ROUTE_ID, COL_STAUANFANG_ID,
						COL_STAUENDE_ID, COL_STAUURSACHE, COL_WARTEZEIT },
				COL_STAU_ID + "=" + stauId, null, null, null, null, null);
		if (c == null) {
			return null;
		}

		c.moveToFirst();

		if (c.getCount() > 0) {
			return new Stau(stauId, c.getLong(c
					.getColumnIndexOrThrow(COL_STAUANFANG_ID)), c.getLong(c
					.getColumnIndexOrThrow(COL_STAUENDE_ID)), c.getInt(c
					.getColumnIndexOrThrow(COL_GESAMTLAENGE)), c.getString(c
					.getColumnIndexOrThrow(COL_STAUURSACHE)), c.getInt(c
					.getColumnIndexOrThrow(COL_WARTEZEIT)), c.getInt(c
					.getColumnIndexOrThrow(COL_ANZAHLMELDUNGEN)), new Date(c
					.getLong(c.getColumnIndexOrThrow(COL_LETZTEAENDERUNG))), c
					.getString(c.getColumnIndexOrThrow(COL_KURZBEZEICHNUNG)), c
					.getLong(c.getColumnIndexOrThrow(COL_ROUTE_ID)));
		}
		return null;
	}

	public Cursor ladeStauberichtFuerRoute(long routeId) throws SQLException {
		SQLiteDatabase database = getReadableDatabase();
		Log.d(TAG, "ladeStauberichtFuerRoute()->database.isOpen(): "
				+ database.isOpen());
		Cursor mCursor = database.query(true, TABLE_NAME, new String[] {
				COL_STAU_ID, COL_KURZBEZEICHNUNG, COL_GESAMTLAENGE,
				COL_STAUURSACHE }, COL_ROUTE_ID + "=" + routeId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean loescheStau(long stauId) throws SQLException {
		return false;
	}

	public void schliesseSpeicher() {
		close();
	}

	public long schreibeStaumeldung(Stau staumeldung) throws SQLException {
		return schreibeStaumeldung(staumeldung, getWritableDatabase());
	}

	private long schreibeStaumeldung(Stau staumeldung, SQLiteDatabase db)
			throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_ROUTE_ID, staumeldung.getRoutenId());
		initialValues.put(COL_STAUANFANG_ID, staumeldung.getStauAnfangId());
		initialValues.put(COL_STAUENDE_ID, staumeldung.getStauEndeId());
		initialValues.put(COL_GESAMTLAENGE, staumeldung.getGesamtLaenge());
		initialValues.put(COL_STAUURSACHE, staumeldung.getStauUrsache());
		initialValues.put(COL_WARTEZEIT, staumeldung.getGesamtWartezeit());
		initialValues
				.put(COL_ANZAHLMELDUNGEN, staumeldung.getAnzahlMeldungen());
		initialValues.put(COL_LETZTEAENDERUNG, staumeldung
				.getLetzteAktualisierung().getTime());
		initialValues
				.put(COL_KURZBEZEICHNUNG, staumeldung.getKurzBezeichnung());

		return db.insert(TABLE_NAME, null, initialValues);
	}

	public void reset() {
		writeTestdata(getWritableDatabase());
	}

	public void writeTestdata(SQLiteDatabase db) {
		db.delete(TABLE_NAME, null, null);

		Stau stau1 = new Stau(1L, 1L, 2L, 3, "Baustelle", 10, 20, new Date(
				System.currentTimeMillis() - 5000), "Ort 1 - Ort 2", 66L);
		schreibeStaumeldung(stau1, db);
		Stau stau2 = new Stau(2L, 3L, 4L, 2, "Unfall", 5, 230, new Date(System
				.currentTimeMillis() - 1000), "Ort 3 - Ort 4", 66L);
		schreibeStaumeldung(stau2, db);
		Stau stau3 = new Stau(3L, 5L, 6L, 8, "stockend", 7, 34, new Date(System
				.currentTimeMillis() - 50000), "Ort 5 - Ort 6", 66L);
		schreibeStaumeldung(stau3, db);

		// route 33
		Stau stau10 = new Stau(4L, 7L, 8L, 3, "Baustelle", 10, 20, new Date(
				System.currentTimeMillis() - 5000), "Ort 7 - Ort 8", 33L);
		schreibeStaumeldung(stau10, db);
		Stau stau20 = new Stau(5L, 9L, 10L, 2, "Baustelle", 5, 230, new Date(
				System.currentTimeMillis() - 1000), "Ort 9 - Ort 10", 33L);
		schreibeStaumeldung(stau20, db);
		Stau stau30 = new Stau(6L, 11L, 12L, 8, "stockend", 7, 34, new Date(
				System.currentTimeMillis() - 50000), "Ort 11 - Ort 12", 33L);
		schreibeStaumeldung(stau30, db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_DATABASE_CREATE);
		writeTestdata(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	
}

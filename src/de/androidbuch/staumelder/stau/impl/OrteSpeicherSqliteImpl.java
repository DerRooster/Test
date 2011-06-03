package de.androidbuch.staumelder.stau.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.androidbuch.staumelder.commons.Ort;
import de.androidbuch.staumelder.commons.StaumelderKonstanten;
import de.androidbuch.staumelder.stau.OrteSpeicher;

/**
 * @author Marcus Pant, 2009 visionera gmbh
 * 
 */
public class OrteSpeicherSqliteImpl extends SQLiteOpenHelper implements
		OrteSpeicher {

	public static final String COL_ORT_ID = "_id";
	public static final String COL_BEZEICHNUNG = "bezeichnung";
	public static final String COL_KURZBEZEICHNUNG = "kurzbezeichnung";

	private static final String TAG = OrteSpeicherSqliteImpl.class.getName();

	private static final String TABLE_NAME = "ort";

	private static final String SQL_DATABASE_CREATE = "create table ort (_id integer primary key autoincrement, "
			+ "bezeichnung text not null, " + "kurzbezeichnung text not null);";

	public OrteSpeicherSqliteImpl(Context context) {
		super(context, StaumelderKonstanten.DATENBANK_NAME, null,
				StaumelderKonstanten.DATENBANK_VERSION);
	}

	public Ort ladeOrt(long ortId) throws SQLException {
		Cursor c = getWritableDatabase()
				.query(
						true,
						TABLE_NAME,
						new String[] { COL_ORT_ID, COL_BEZEICHNUNG,
								COL_KURZBEZEICHNUNG },
						COL_ORT_ID + "=" + ortId, null, null, null, null, null);
		if (c == null) {
			return null;
		}
		c.moveToFirst();

		return new Ort(ortId, c.getString(c
				.getColumnIndexOrThrow(COL_BEZEICHNUNG)), c.getString(c
				.getColumnIndexOrThrow(COL_KURZBEZEICHNUNG)), null);
	}

	public long schreibeOrt(Ort ort) throws SQLException {
		return schreibeOrt(ort, getWritableDatabase());
	}

	private long schreibeOrt(Ort ort, SQLiteDatabase db) throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_BEZEICHNUNG, ort.getBezeichnung());
		initialValues.put(COL_KURZBEZEICHNUNG, ort.getKurzBezeichnung());

		return db.insert(TABLE_NAME, null, initialValues);
	}

	public void reset() {
		
	}

	public void schliesseSpeicher() {
		close();
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

	public void writeTestdata(SQLiteDatabase db) {
		db.delete(TABLE_NAME, null, null);

		for (int i = 1; i < 15; i++) {
			schreibeOrt(new Ort(null, "Ort Langbezeichnung " + i, "Ort " + i,
					null));
		}
	}

}

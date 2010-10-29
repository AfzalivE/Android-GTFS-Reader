package afzal.gtfsReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "agency_database";
	
	public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
			/*
			 * 	Create the agency table and populate it with sample data.
			 *  This is later moved to an XML document.
			 */
			String sql = "CREATE TABLE IF NOT EXISTS agency (" +
											"_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
											"agency_id TEXT" +
											"agency_name TEXT" +
											"agency_url TEXT" +
											"agency_timezone TEXT" +
											"agency_lang TEXT" +
											"agency_phone TEXT)";
			db.execSQL(sql);
			
			ContentValues values = new ContentValues();
			
			values.put("agency_id", "HSR");
			values.put("agency_name", "Hamilton Street Railway");
			values.put("agency_url", "http://www.hamilton.ca/hsr");
			values.put("agency_timezone", "America/Toronto");
			values.put("agency_lang", "en");
			values.put("agency_phone", "905-527-4441");
			db.insert("agency", "agency_id", values);
			
			values.put("agency_id", "go");
			values.put("agency_name", "GO Transit");
			values.put("agency_url", "http://www.gotransit.com");
			values.put("agency_timezone", "America/Toronto");
			values.put("agency_lang", "en");
			values.put("agency_phone", "1.888.438.6646");
			db.insert("agency", "agency_id", values);
											
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS agencies");
			onCreate(db);
	}
	}
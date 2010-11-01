package afzal.gtfsReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_AGENCYID = "agency_id";
	public static final String KEY_AGENCYNAME = "agency_name";
	public static final String KEY_AGENCYURL = "agency_url";
	public static final String KEY_AGENCYTIMEZONE = "agency_timezone";
	public static final String KEY_AGENCYLANG = "agency_lang";
	public static final String KEY_AGENCYPHONE = "agency_phone";
	private static final String TAG = "DBAdapter";
	
	private static final String DATABASE_NAME = "gtfs";
	private static final String DATABASE_TABLE = "agencies";
	private static final int DATABASE_VERSION = 1;
	
	private final Context context;
	
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	private static final String DATABASE_CREATE =
		"create table agencies (_id integer primary key autoincrement, "
		+ "agency_id TEXT, "
		+ "agency_name TEXT not null, "
		+ "agency_url TEXT not null, "
		+ "agency_timezone TEXT not null, "
		+ "agency_lang TEXT, "
		+ "agency_phone TEXT);";
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			
			// Add 2 agencies for testing
			ContentValues values = new ContentValues();
			values.put("agency_id", "HSR");
			values.put("agency_name", "Hamilton Street Railway");
			values.put("agency_url", "http://www.hamilton.ca/hsr");
			values.put("agency_timezone", "America/Toronto");
			values.put("agency_lang", "en");
			values.put("agency_phone", "905-527-4441");
			db.insert(DATABASE_TABLE, null , values);
			
			values.put("agency_id", "go");
			values.put("agency_name", "GO Transit");
			values.put("agency_url", "http://www.gotransit.com");
			values.put("agency_timezone", "America/Toronto");
			values.put("agency_lang", "en");
			values.put("agency_phone", "1.888.438.6646");
			db.insert(DATABASE_TABLE, null, values);
		}
		
		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS agencies");
            onCreate(db);
		}
	}
	
	// opens the database
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	// closes the database
	
	public void close () {
		DBHelper.close();
	}
	
	// insert an agency into the database
	public long insertAgency(String agency_id, String agency_name, String agency_url, String agency_timezone, String agency_lang, String agency_phone) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_AGENCYID, agency_id);
		initialValues.put(KEY_AGENCYNAME, agency_name);
		initialValues.put(KEY_AGENCYURL, agency_url);
		initialValues.put(KEY_AGENCYTIMEZONE, agency_timezone);
		initialValues.put(KEY_AGENCYLANG, agency_lang);
		initialValues.put(KEY_AGENCYPHONE, agency_phone);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	// deletes a particular agency
	public boolean deleteAgency(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	// deletes all agencies
	public boolean deleteAllAgencies() {
		return db.delete(DATABASE_TABLE, KEY_ROWID, null) > 0;
	}
	
	// retrieves all agencies
	public Cursor getAllAgencies() {
		return db.query(DATABASE_TABLE, new String[] {
				KEY_ROWID,
				KEY_AGENCYID,
				KEY_AGENCYNAME,
				KEY_AGENCYURL,
				KEY_AGENCYTIMEZONE,
				KEY_AGENCYLANG,
				KEY_AGENCYPHONE},
				null,
				null,
				null,
				null,
				null);				
	}
	
	// retrieves a particular agency
	public Cursor getAgency(long rowId) throws SQLException {
		Cursor mCursor = 
			db.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID,
				KEY_AGENCYID,
				KEY_AGENCYNAME,
				KEY_AGENCYURL,
				KEY_AGENCYTIMEZONE,
				KEY_AGENCYLANG,
				KEY_AGENCYPHONE}, 
				KEY_ROWID + "=" + rowId,
				null,
				null,
				null,
				null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	// updates an agency
		public boolean updateAgency(long rowId, String agency_id, String agency_name, String agency_url, String agency_timezone, String agency_lang, String agency_phone) {
		ContentValues args = new ContentValues();
		args.put(KEY_AGENCYID, agency_id);
		args.put(KEY_AGENCYNAME, agency_name);
		args.put(KEY_AGENCYURL, agency_url);
		args.put(KEY_AGENCYTIMEZONE, agency_timezone);
		args.put(KEY_AGENCYLANG, agency_lang);
		args.put(KEY_AGENCYPHONE, agency_phone);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}

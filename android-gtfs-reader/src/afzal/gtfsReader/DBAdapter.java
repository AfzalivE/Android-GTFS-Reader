/****** BEGIN LICENSE BLOCK *****
 *	Version: MPL 1.1
 *	
 *	The contents of this file are subject to the Mozilla Public License Version 
 *	1.1 (the "License"); you may not use this file except in compliance with 
 *	the License. You may obtain a copy of the License at 
 *	http://www.mozilla.org/MPL/
 *	
 *	Software distributed under the License is distributed on an "AS IS" basis,
 *	WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *	for the specific language governing rights and limitations under the
 *	License.
 *	
 *	The Original Code is located at http://code.google.com/p/android-gtfs-reader/
 *	
 *	The Initial Developer of the Original Code is
 *	Afzal Najam <afzal.naj@gmail.com>
 *	
 *	Portions created by the Initial Developer are Copyright (C) 2010
 *	Afzal Najam. All Rights Reserved.
 *	
 ******** END LICENSE BLOCK ******/

package afzal.gtfsReader;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DBAdapter {
	// DB File strings	
	private static final String DATABASE_NAME = "gtfs";
	private static final String AGENCIES_TABLE = "agencies";
	private static final String STOPS_TABLE = "stops";
	private static final int DATABASE_VERSION = 1;
	
	// Agency List strings
	public static final String KEY_ROWID = "_id";
	public static final String KEY_AGENCYID = "agency_id";
	public static final String KEY_AGENCYNAME = "agency_name";
	public static final String KEY_AGENCYURL = "agency_url";
	public static final String KEY_AGENCYTIMEZONE = "agency_timezone";
	public static final String KEY_AGENCYLANG = "agency_lang";
	public static final String KEY_AGENCYPHONE = "agency_phone";
	
	// Stop list strings
	public static final String KEY_STOPID = "_id";
	public static final String KEY_SAGENCYID = "sagency_id"; 
	public static final String KEY_STOPCODE = "stop_code";
	public static final String KEY_STOPNAME = "stop_name";
	public static final String KEY_STOPDESC = "stop_desc";
	public static final String KEY_STOPLAT = "stop_lat";
	public static final String KEY_STOPLON = "stop_lon";
	public static final String KEY_ZONEID = "zone_id";
	public static final String KEY_STOPURL = "stop_url";
	public static final String KEY_LOCATIONTYPE = "location_type";
	public static final String KEY_PARENTSTATION = "parent_station";

	private static final String AGENCYDB_CREATE =
		"create table agencies (_id INTEGER primary key autoincrement, "
		+ "agency_id TEXT, "
		+ "agency_name TEXT not null, "
		+ "agency_url TEXT not null, "
		+ "agency_timezone TEXT not null, "
		+ "agency_lang TEXT, "
		+ "agency_phone TEXT);";
	
	private static final String STOPSDB_CREATE =
		"create table stops (_id TEXT primary key, "
		+ "sagency_id INTEGER, "
		+ "stop_code TEXT, "
		+ "stop_name TEXT not null, "
		+ "stop_desc TEXT, "
		+ "stop_lat DOUBLE not null, "
		+ "stop_lon DOUBLE not null, "
		+ "zone_id INTEGER, "
		+ "stop_url TEXT, "
		+ "location_type BOOL, "
		+ "parent_station INTEGER);";
	
	public static class DBHelper {
		private SQLiteDatabase db;
		
		public void open() {
			File dbDir = null;
			File dbFile = null;
			
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				dbDir = new File (Environment.getExternalStorageDirectory(), /*"." + */DATABASE_NAME);
				dbFile = new File(dbDir, DATABASE_NAME);
			} else {
				// code for showing error coz there's no sdcard
			}
			if (!dbDir.exists()) {
				dbDir.mkdir();
				Log.i("SQLiteHelper", "Created directory at " + dbDir);
			}
			if (dbFile.exists()) {
				Log.i("SQLiteHelper", "Opening database at " + dbFile);
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				if (DATABASE_VERSION > db.getVersion()) {
					upgrade();
				}
			} else {
				Log.i("SQLiteHelper", "Creating database at " + dbFile);
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				Log.i("SQLiteHelper", "Opened database at " + dbFile);
				create();
			}
		}
    
	    public void create() {
			db.execSQL(AGENCYDB_CREATE);
			db.execSQL(STOPSDB_CREATE);
			
			// Add 2 agencies for testing
			ContentValues values = new ContentValues();
			values.put("_id", "0");
			values.put("agency_id", "HSR");
			values.put("agency_name", "Hamilton Street Railway");
			values.put("agency_url", "http://www.hamilton.ca/hsr");
			values.put("agency_timezone", "America/Toronto");
			values.put("agency_lang", "en");
			values.put("agency_phone", "905-527-4441");
			db.insert(AGENCIES_TABLE, null , values);
			values.clear();
			
			values.put("agency_id", "go");
			values.put("agency_name", "GO Transit");
			values.put("agency_url", "http://www.gotransit.com");
			values.put("agency_timezone", "America/Toronto");
			values.put("agency_lang", "en");
			values.put("agency_phone", "1.888.438.6646");
			db.insert(AGENCIES_TABLE, null, values);
			values.clear();
			
			values.put("_id", "355457");
			values.put("sagency_id", "0");
			values.put("stop_code", "71400");
			values.put("stop_name", "BEACH opposite MANOR");
			values.put("stop_desc", "BEACH BV & MANOR AV");
			values.put("stop_lat", "43.291883");
			values.put("stop_lon", "-79.791904");
			values.put("zone_id", "");
			values.put("stop_url", "");
			values.put("location_type", "0");
			values.put("parent_station", "");
			db.insert(STOPS_TABLE, null, values);
			values.clear();
			
			values.put("_id", "355454");
			values.put("sagency_id", "0");
			values.put("stop_code", "80301b");
			values.put("stop_name", "BAY opposite GEORGE");
			values.put("stop_desc", "BAY ST S & GEORGE ST");
			values.put("stop_lat", "43.257224");
			values.put("stop_lon", "-79.874013");
			values.put("zone_id", "");
			values.put("stop_url", "");
			values.put("location_type", "0");
			values.put("parent_station", "");
			db.insert(STOPS_TABLE, null, values);
			values.clear();
			
			values.put("_id", "medvls");
			values.put("sagency_id", "1");
			values.put("stop_code", "");
			values.put("stop_name", "Meadowvale GO Station");
			values.put("stop_desc", "");
			values.put("stop_lat", "43.5978");
			values.put("stop_lon", "-79.7542");
			values.put("zone_id", "22");
			values.put("stop_url", "http://www.gotransit.com/publicroot/en/travelling/stations.aspx?station=MDGO");
			values.put("location_type", "0");
			values.put("parent_station", "medvls0");
			db.insert(STOPS_TABLE, null, values);
			values.clear();
			
			values.put("_id", "01994");
			values.put("sagency_id", "1");
			values.put("stop_code", "");
			values.put("stop_name", "Yonge St At Bristol Rd");
			values.put("stop_desc", "");
			values.put("stop_lat", "44.0681");
			values.put("stop_lon", "-79.4835");
			values.put("zone_id", "64");
			values.put("stop_url", "http://www.gotransit.com/publicroot/en/travelling/stations.aspx?station=MDGO");
			values.put("location_type", "0");
			values.put("parent_station", "medvls0");
			db.insert(STOPS_TABLE, null, values);
			values.clear();
			
			db.setVersion(DATABASE_VERSION);
		}
		
		public void upgrade() {
			Log.w("" + this, "Upgrading database "+ db.getPath() + " from version " + db.getVersion() + " to "
	                + DATABASE_VERSION + ", which will destroy all old data");
	         db.execSQL("DROP TABLE IF EXISTS agencies");
	         db.execSQL("DROP TABLE IF EXISTS stops");
	         create();
		}
	
		// closes the database
		public void close () {
			db.close();
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
			return db.insert(AGENCIES_TABLE, null, initialValues);
		}
		
		// deletes a particular agency
		public boolean deleteAgency(long rowId) {
			int agency = db.delete(AGENCIES_TABLE, KEY_ROWID + "=" + rowId, null);
			int stops = db.delete(STOPS_TABLE, KEY_SAGENCYID + "=" + rowId, null);
			return (agency + stops > 0);
		}
		
		// deletes all agencies
		public boolean deleteAllAgencies() {
			int agency = db.delete(AGENCIES_TABLE, KEY_ROWID, null);
			int stops = db.delete(STOPS_TABLE, KEY_STOPID, null);
			return (agency + stops > 0);
		}
		
		// retrieves all agencies
		public Cursor getAllAgencies() {
			return db.query(AGENCIES_TABLE, new String[] {
					// importing only needed columns instead of all
					KEY_ROWID,
					KEY_AGENCYNAME},
					null,
					null,
					null,
					null,
					null);				
		}
		
		// retrieves a particular agency
		public Cursor getAgency(long rowId) throws SQLException {
			Cursor mCursor = 
				db.query(true, AGENCIES_TABLE, new String[] {
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
			return db.update(AGENCIES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
		}
	
	public Cursor getAllStops(long rowId) throws SQLException {
		return	db.query(STOPS_TABLE, new String[] {
				KEY_STOPID,
				KEY_SAGENCYID,
				KEY_STOPCODE,
				KEY_STOPNAME,
				KEY_STOPDESC}, 
				KEY_SAGENCYID + "=" + rowId,
				null,
				null,
				null,
				null,
				null);
		}
	}
}

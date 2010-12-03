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

import afzal.gtfsReader.DBAdapter.DBHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AgencyEdit extends Activity {
	private static final int SAVE_AGENCY = Menu.FIRST;
	private static final int CANCEL = Menu.FIRST + 1;
	private EditText AgencyName;
	private EditText AgencyURL;
	private EditText AgencyTimezone;
	private EditText AgencyPhone;
	private EditText AgencyId;
	private EditText AgencyLang;
	private Long rowId;
	private Cursor agencyCursor;
	private DBHelper db = new DBHelper();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agency_edit);
		setTitle(R.string.edit_agency);
		
		AgencyName = (EditText) findViewById(R.id.agencyname);
		AgencyURL = (EditText) findViewById(R.id.agencyurl);
		AgencyTimezone = (EditText) findViewById(R.id.agencytz);
		AgencyPhone = (EditText) findViewById(R.id.agencyph);
		AgencyId = (EditText) findViewById(R.id.agencyid);
		AgencyLang = (EditText) findViewById(R.id.agencylang);
		
		rowId = null;
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			rowId = extras.getLong(DBAdapter.KEY_AGENCYID);
			db.open();
			Log.i("AgencyEdit", "Opening database");
			agencyCursor = db.getAgency(rowId);
			startManagingCursor(agencyCursor);
			db.close();
			
			String name = agencyCursor.getString(agencyCursor.getColumnIndex("agency_name"));
			Log.i("AgencyEdit", "name obtained");
			String url = agencyCursor.getString(agencyCursor.getColumnIndex("agency_url"));
			String tz = agencyCursor.getString(agencyCursor.getColumnIndex("agency_timezone"));
			String ph = agencyCursor.getString(agencyCursor.getColumnIndex("agency_phone"));
			String id = agencyCursor.getString(agencyCursor.getColumnIndex("agency_id"));
			String lang = agencyCursor.getString(agencyCursor.getColumnIndex("agency_lang"));
			AgencyName.setText(name);
			AgencyURL.setText(url);
			AgencyTimezone.setText(tz);
			AgencyPhone.setText(ph);
			AgencyId.setText(id);
			AgencyLang.setText(lang);
		}
	}
	
    @Override
    protected void onStop() {
        stopManagingCursor(agencyCursor);
        agencyCursor.close();
        super.onStop();
    }
    
    @Override
    protected void onDestroy () {
	    stopManagingCursor(agencyCursor);
	    agencyCursor.close();
	    super.onDestroy();
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SAVE_AGENCY, 0, R.string.menu_save);
        menu.add(0, CANCEL, 0, R.string.cancel);
        return true;
    }
	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        	case SAVE_AGENCY:
                Bundle bundle = new Bundle();
                bundle.putString(DBAdapter.KEY_AGENCYNAME, AgencyName.getText().toString());
                bundle.putString(DBAdapter.KEY_AGENCYURL, AgencyURL.getText().toString());
                bundle.putString(DBAdapter.KEY_AGENCYTIMEZONE, AgencyTimezone.getText().toString());
                bundle.putString(DBAdapter.KEY_AGENCYPHONE, AgencyPhone.getText().toString());
                bundle.putString(DBAdapter.KEY_AGENCYID, AgencyId.getText().toString());
                bundle.putString(DBAdapter.KEY_AGENCYLANG, AgencyLang.getText().toString());
                if (rowId != null) {
                	bundle.putLong(DBAdapter.KEY_ROWID, rowId);
                }
                Intent i = new Intent();
                i.putExtras(bundle);
                setResult(RESULT_OK, i);
                finish();
	        case CANCEL:
	        	setResult(RESULT_CANCELED);
	        	finish();
    		
        }
        return super.onMenuItemSelected(featureId, item);
    }
}

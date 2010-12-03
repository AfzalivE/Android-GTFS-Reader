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
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AgencyList extends ListActivity {     
    private static final int ACTIVITY_CREATE =0;
    private static final int ACTIVITY_EDIT =1;
    
    private static final int EDIT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int DETAIL = Menu.FIRST + 2;
    private static final int CREATE_AGENCY = Menu.FIRST + 3;
    private static final int RESET_DB = Menu.FIRST + 4;
    
	private DBHelper db = new DBHelper();
	private Cursor agencyCursor;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // get and display all agencies
        DisplayAgencies();
        registerForContextMenu(getListView());
        
    }
    
    @Override
    protected void onPause() {
        stopManagingCursor(agencyCursor);
        
        // This is to avoid "Invalid statement in FillWindow()" Error
        // Find out how to copy the contents of SimpleCursorAdapter
        // to a temporary variable which doesn't need to close
//      agencyCursor.close();
        super.onPause();
    }
 
    @Override
    protected void onResume() {
        DisplayAgencies();
        super.onResume();
    }
   
    public void DisplayAgencies() {
        // open db
        db.open();
    	// Get all of the rows from the database
    	// and create the item list
    	agencyCursor = db.getAllAgencies();
    	startManagingCursor(agencyCursor);

    	// array to specify the fields to display in the list
    	String[] from = new String[]{DBAdapter.KEY_AGENCYNAME};
    	
    	// array of fields to bind those fields to
    	int[] to = new int[]{R.id.agency_name};
    	
    	// Simple cursor adapter; set to display
    	SimpleCursorAdapter agencies = new SimpleCursorAdapter(this, R.layout.agency_list_item, agencyCursor, from, to);
    	setListAdapter(agencies);
    	db.close();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, CREATE_AGENCY, 0, R.string.menu_new);
    	menu.add(0, RESET_DB, 0, "Reset Database");
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        	case CREATE_AGENCY:
        		createAgency();
	        case RESET_DB:
	        	db.open();
	    		db.upgrade();
	    		db.close();
	    		DisplayAgencies();   		
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createAgency() {
    	Intent i = new Intent(this,AgencyEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
		
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras;
        String id, name, url, tz, lang, ph;
        switch(requestCode) {
        case ACTIVITY_CREATE:
        	  if (resultCode == RESULT_OK) {
        		db.open();
              	extras = intent.getExtras();
	        	id = extras.getString(DBAdapter.KEY_AGENCYID);
	            name = extras.getString(DBAdapter.KEY_AGENCYNAME);
				url = extras.getString(DBAdapter.KEY_AGENCYURL);
				tz = extras.getString(DBAdapter.KEY_AGENCYTIMEZONE);
				lang = extras.getString(DBAdapter.KEY_AGENCYLANG);
				ph = extras.getString(DBAdapter.KEY_AGENCYPHONE);
	    		db.insertAgency(
	    				id, 
	    				name, 
	    				url, 
	    				tz, 
	    				lang, 
	    				ph);
	    		db.close();
	            DisplayAgencies();
	            break;
        	  }
            
        case ACTIVITY_EDIT:
            if (resultCode == RESULT_OK) {
            	extras = intent.getExtras();
                Long rowId = extras.getLong(DBAdapter.KEY_ROWID);
            	if (rowId != null) {
            		db.open();
            		id = extras.getString(DBAdapter.KEY_AGENCYID);
	            	name = extras.getString(DBAdapter.KEY_AGENCYNAME);
	    			url = extras.getString(DBAdapter.KEY_AGENCYURL);
	    			tz = extras.getString(DBAdapter.KEY_AGENCYTIMEZONE);
	    			lang = extras.getString(DBAdapter.KEY_AGENCYLANG);
	    			ph = extras.getString(DBAdapter.KEY_AGENCYPHONE);
	    			db.updateAgency(
	                		rowId, 
	                		id, 
	                		name, 
	                		url, 
	                		tz, 
	                		lang,
	                		ph);
	    			db.close();
	                DisplayAgencies();
	                break;
	            }
            }
        }
    }

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	String agencyname = agencyCursor.getString(agencyCursor.getColumnIndex("agency_name")); 
    	menu.setHeaderTitle(agencyname);
    	menu.add(0, EDIT_ID, 0, R.string.menu_edit);
    	menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    	menu.add(0, DETAIL, 0, R.string.menu_detail);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	Intent i;
    	AdapterContextMenuInfo info;
    	Cursor c;
    	switch(item.getItemId()) {
    		case EDIT_ID:
    			c = agencyCursor;
    			startManagingCursor(c);
    			info = (AdapterContextMenuInfo) item.getMenuInfo();
    			c.moveToPosition(info.position);
    	    	i = new Intent(this, AgencyEdit.class);
    	    	i.putExtra(DBAdapter.KEY_AGENCYID, info.id);
    	    	startActivityForResult(i, ACTIVITY_EDIT);
    	    	return true;
    	    	
	    	case DELETE_ID:
	    		db.open();
	    		info = (AdapterContextMenuInfo) item.getMenuInfo();
	    		db.deleteAgency(info.id);
	    		DisplayAgencies();
	    		db.close();
	    		return true;
	    		
	    	case DETAIL:
	    		c = agencyCursor;
	            startManagingCursor(c);
	    		info = (AdapterContextMenuInfo) item.getMenuInfo();
				c.moveToPosition(info.position);
	        	i = new Intent(this, AgencyDetails.class);
				i.putExtra("_id", info.id);
	            startActivity(i);    
	            return true;
    	}
    	return super.onContextItemSelected(item);
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Cursor c = agencyCursor;
        startManagingCursor(c);
    	c.moveToPosition(position);
    	Intent i = new Intent(this, StopList.class);
    	i.putExtra(DBAdapter.KEY_SAGENCYID, id);

    	// Need to display Agency Name on the title of stop list.
    	String agency_name = DBAdapter.KEY_AGENCYNAME;
    	i.putExtra(DBAdapter.KEY_AGENCYNAME, agency_name);
        startActivity(i);
    }


}

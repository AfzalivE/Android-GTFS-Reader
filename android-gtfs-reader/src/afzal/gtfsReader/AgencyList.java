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
 //     agencyCursor.close();
        super.onPause();
    }
 
    @Override
    protected void onResume() {
        DisplayAgencies();
        super.onResume();
    }
    
    @Override
    protected void onDestroy () {
//      agencyCursor.close();
    	db.close();
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
        Bundle extras = intent.getExtras();
        db.open();
        switch(requestCode) {
        case ACTIVITY_CREATE:
            String name = extras.getString(DBAdapter.KEY_AGENCYNAME);
			String url = extras.getString(DBAdapter.KEY_AGENCYURL);
			String tz = extras.getString(DBAdapter.KEY_AGENCYTIMEZONE);
    		db.insertAgency(
    				"", 
    				name, 
    				url, 
    				tz, 
    				"" , 
    				"");
            DisplayAgencies();
            break;
            
        case ACTIVITY_EDIT:
            Long rowId = extras.getLong(DBAdapter.KEY_ROWID);
            if (rowId != null) {
                String editname = extras.getString(DBAdapter.KEY_AGENCYNAME);
    			String editurl = extras.getString(DBAdapter.KEY_AGENCYURL);
    			String edittz = extras.getString(DBAdapter.KEY_AGENCYTIMEZONE);
                db.updateAgency(
                		rowId, 
                		"", 
                		editname, 
                		editurl, 
                		edittz, 
                		"", 
                		"");
            }
            DisplayAgencies();
            break;
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
    	switch(item.getItemId()) {
    		case EDIT_ID:
    			Cursor c1 = agencyCursor;
    			startManagingCursor(c1);
    			AdapterContextMenuInfo info1 = (AdapterContextMenuInfo) item.getMenuInfo();
    			c1.moveToPosition(info1.position);
    	    	Intent i1 = new Intent(this, AgencyEdit.class);
    	    	i1.putExtra("_id", info1.id);
    	    	startActivityForResult(i1, ACTIVITY_EDIT);
    	    	
	    	case DELETE_ID:
	    		db.open();
	    		AdapterContextMenuInfo info2 = (AdapterContextMenuInfo) item.getMenuInfo();
	    		db.deleteAgency(info2.id);
	    		DisplayAgencies();
	    		db.close();
	    		return true;
	    		
	    	case DETAIL:
	    		Cursor c2 = agencyCursor;
	            startManagingCursor(c2);
	    		AdapterContextMenuInfo info3 = (AdapterContextMenuInfo) item.getMenuInfo();
				c2.moveToPosition(info3.position);
	        	Intent i2 = new Intent(this, AgencyDetails.class);
				i2.putExtra("_id", info3.id);
	            startActivity(i2);    
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
    	i.putExtra("sagency_id", id);
        startActivity(i);
    }


}

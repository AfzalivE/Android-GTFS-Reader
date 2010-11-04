/****** BEGIN LICENSE BLOCK *****
	Version: MPL 1.1/GPL 2.0/LGPL 2.1
	
	The contents of this file are subject to the Mozilla Public License Version 
	1.1 (the "License"); you may not use this file except in compliance with 
	the License. You may obtain a copy of the License at 
	http://www.mozilla.org/MPL/
	
	Software distributed under the License is distributed on an "AS IS" basis,
	WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
	for the specific language governing rights and limitations under the
	License.
	
	The Original Code is located at http://code.google.com/p/android-gtfs-reader/
	
	The Initial Developer of the Original Code is
	Afzal Najam <afzal.naj@gmail.com>
	
	Portions created by the Initial Developer are Copyright (C) 2010
	Afzal Najam. All Rights Reserved.
	
	***** END LICENSE BLOCK ******/

package afzal.gtfsReader;

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
//    private static final int ACTIVITY_CREATE=0;
//    private static final int ACTIVITY_EDIT=1;
    
//  private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST;
    private static final int DETAIL = Menu.FIRST + 1;
    private static final int RESET_DB = Menu.FIRST + 2;
    
	private DBAdapter db = new DBAdapter(this);
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
    
//    @Override
//    protected void onPause() {
//        stopManagingCursor(agencyCursor);
//        agencyCursor.close();
//        super.onPause();
//    }
 
    @Override
    protected void onResume() {
        DisplayAgencies();
        super.onResume();
    }
    
    public void DisplayAgencies()
    {
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
    	// close db
    	db.close();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
    	menu.add(0, RESET_DB, 0, "Reset Database");
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case RESET_DB:
    		db.open();
    		db.deleteAllAgencies();
    		db.insertAgency(
    				"HSR", 
    				"Hamilton Street Railway", 
    				"www.hsr.com", 
    				"America/Toronto", 
    				"en" , 
    				"905-527-4441");
    		db.insertAgency(
    				"go", 
    				"GO Transit", 
    				"www.gotransit.com", 
    				"America/Toronto", 
    				"en" , 
    				"1.888.438.6646");
    		DisplayAgencies();
    		db.close();
    		
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	String agencyname = agencyCursor.getString(agencyCursor.getColumnIndex("agency_name")); 
    	menu.setHeaderTitle(agencyname);
    	menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    	menu.add(0, DETAIL, 0, R.string.menu_detail);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case DELETE_ID:
    		db.open();
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    		db.deleteAgency(info.id);
    		DisplayAgencies();
    		db.close();
    		return true;
    		
    	case DETAIL:
    		Cursor c = agencyCursor;
    		AdapterContextMenuInfo info_id = (AdapterContextMenuInfo) item.getMenuInfo();
			c.moveToPosition(info_id.position);
        	Intent i = new Intent(this, AgencyDetails.class);
			i.putExtra("_id", info_id.id);
            startActivity(i);       
            return true;
    	}
    	return super.onContextItemSelected(item);
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
    //	super.onListItemClick(l, v, position, id);
    	Cursor c = agencyCursor;
    	c.moveToPosition(position);
    	Intent i = new Intent(this, AgencyDetails.class);
        i.putExtra("_id", id);
        startActivity(i);
    }


}

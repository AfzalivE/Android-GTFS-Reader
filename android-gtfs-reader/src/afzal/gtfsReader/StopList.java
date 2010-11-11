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

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class StopList extends ListActivity {
	private DBAdapter db = new DBAdapter(this);
	private Long rowId;
	private Cursor stopCursor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        rowId = getIntent().getLongExtra(DBAdapter.KEY_SAGENCYID, 0);
        
        DisplayStops(rowId);
//    	registerForContextMenu(getListView());
        
    }
    
    @Override
    protected void onStop() {
        stopManagingCursor(stopCursor);
        stopCursor.close();
        super.onPause();
    }
    
    public void DisplayStops(long rowId) {
        // open db
        db.open();
    	// Get all of the rows from the database
    	// and create the item list
    	stopCursor = db.getStops(rowId);
    	startManagingCursor(stopCursor);

    	// array to specify the fields to display in the list
    	String[] from = new String[]{DBAdapter.KEY_STOPNAME, DBAdapter.KEY_STOPID};
    	
    	// array of fields to bind those fields to
    	int[] to = new int[]{R.id.stop_name, R.id.stop_id};
    	
    	// Simple cursor adapter; set to display
    	SimpleCursorAdapter stops = new SimpleCursorAdapter(this, R.layout.stop_list_item, stopCursor, from, to);
    	setListAdapter(stops);
    	// close db
    	db.close();
    }
}

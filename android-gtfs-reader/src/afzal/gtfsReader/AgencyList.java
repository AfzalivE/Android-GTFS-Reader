package afzal.gtfsReader;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

public class AgencyList extends ListActivity {     
//    private static final int ACTIVITY_CREATE=0;
//    private static final int ACTIVITY_EDIT=1;
    
//  private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST;
    private static final int RESET_DB = Menu.FIRST + 1;
    
	private DBAdapter db = new DBAdapter(this);
	private Cursor agencyCursor;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // open db
        db.open();        
        // get and display all agencies
        DisplayAgencies();
        registerForContextMenu(getListView());
        db.close();
    }
    
    public void DisplayAgencies()
    {
    	// Get all of the rows from the database
    	// and create the item list
    	agencyCursor = db.getAllAgencies();
    	startManagingCursor(agencyCursor);
    	
    	// array to specify the fields to display in the list
    	String[] from = new String[]{DBAdapter.KEY_AGENCYNAME, DBAdapter.KEY_AGENCYPHONE};
    	
    	// array of fields to bind those fields to
    	int[] to = new int[]{R.id.agency_name, R.id.agency_phone};
    	
    	// Simple cursor adapter; set to display
    	SimpleCursorAdapter agencies = new SimpleCursorAdapter(this, R.layout.agency_list_item, agencyCursor, from, to);
    	setListAdapter(agencies);
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
    	menu.add(0, DELETE_ID, 0, R.string.menu_delete);
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
    		}
    	return super.onContextItemSelected(item);
    }

}

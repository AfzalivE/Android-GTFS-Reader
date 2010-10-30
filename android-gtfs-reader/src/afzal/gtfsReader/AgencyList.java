package afzal.gtfsReader;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class AgencyList extends ListActivity {     
	
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
    	String[] from = new String[]{DBAdapter.KEY_AGENCYNAME};
    	
    	// array of fields to bind those fields to
    	int[] to = new int[]{R.id.agency_name};
    	
    	// Simple cursor adapter; set to display
    	SimpleCursorAdapter agencies = new SimpleCursorAdapter(this, R.layout.agency_list_item, agencyCursor, from, to);
    	setListAdapter(agencies);
    } 

}

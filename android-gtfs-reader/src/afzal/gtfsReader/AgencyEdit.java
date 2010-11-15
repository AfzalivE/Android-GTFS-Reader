package afzal.gtfsReader;

import afzal.gtfsReader.DBAdapter.DBHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AgencyEdit extends Activity {
	private static final int SAVE_AGENCY = Menu.FIRST;
	private static final int CANCEL = Menu.FIRST + 1;
	private EditText AgencyName;
	private EditText AgencyURL;
	private EditText AgencyTimezone;
	private Long rowId;
	private Cursor agencyCursor;
	private DBHelper db = new DBHelper();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agency_edit);
		
		AgencyName = (EditText) findViewById(R.id.agencyname);
		AgencyURL = (EditText) findViewById(R.id.agencyurl);
		AgencyTimezone = (EditText) findViewById(R.id.agencytz);
		
		rowId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			db.open();
			agencyCursor = db.getAgency(rowId);
			db.close();
			
			if (agencyCursor.getCount() == 1) {
				String name = agencyCursor.getString(agencyCursor.getColumnIndex("agency_name"));
				String url = agencyCursor.getString(agencyCursor.getColumnIndex("agency_url"));
				String tz = agencyCursor.getString(agencyCursor.getColumnIndex("agency_timezone"));
				AgencyName.setText(name);
				AgencyURL.setText(url);
				AgencyTimezone.setText(tz);
			}
		}
	}
	
//    @Override
//    protected void onDestroy () {
//    	db.close();
//    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SAVE_AGENCY, 0, "Save");
        menu.add(0, CANCEL, 0, "Cancel");
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
                Intent i = new Intent();
                i.putExtras(bundle);
                setResult(RESULT_OK, i);
                finish();

	        case CANCEL:
	        	finish();
    		
        }
        return super.onMenuItemSelected(featureId, item);
    }
}

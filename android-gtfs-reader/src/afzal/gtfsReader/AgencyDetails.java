package afzal.gtfsReader;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;


public class AgencyDetails extends Activity {
	
	private TextView agencyname; 
	private DBAdapter db = new DBAdapter(this);
	private Long rowId;
	private Cursor agencyCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agency_details);
		
		rowId = getIntent().getLongExtra(DBAdapter.KEY_ROWID, 0);
		db.open();
		agencyCursor = db.getAgency(rowId);
		db.close();
		
		if (agencyCursor.getCount() == 1) {
		String agency_name = agencyCursor.getString(agencyCursor.getColumnIndex("agency_name"));
			agencyname = (TextView) findViewById(R.id.agency_name);
			agencyname.setText(agency_name);
			
			String agency_url = agencyCursor.getString(agencyCursor.getColumnIndex("agency_url"));
			agencyname = (TextView) findViewById(R.id.agency_url);
			agencyname.setText(agency_url);
			
			String agency_phone = agencyCursor.getString(agencyCursor.getColumnIndex("agency_phone"));
			agencyname = (TextView) findViewById(R.id.agency_phone);
			agencyname.setText(agency_phone);
		}
	}
}

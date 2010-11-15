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

import java.util.ArrayList;
import java.util.List;

import afzal.gtfsReader.DBAdapter.DBHelper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ListActivity;

public class AgencyDetails extends ListActivity {
	
	private List<AgencyAction> actions;
	private DBHelper db = new DBHelper();
	private Long rowId;
	private Cursor agencyCursor;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		rowId = getIntent().getLongExtra(DBAdapter.KEY_ROWID, 0);
		db.open();
		agencyCursor = db.getAgency(rowId);
		db.close();
		
		if (agencyCursor.getCount() == 1) {
			String agency_name = agencyCursor.getString(agencyCursor.getColumnIndex("agency_name"));
			setTitle(agency_name);
			
			actions = new ArrayList<AgencyAction>();
			
			String agencyphone = agencyCursor.getString(agencyCursor.getColumnIndex("agency_phone"));
			actions.add(new AgencyAction(R.string.phone, agencyphone, AgencyAction.ACTION_CALL));
			
			String agencyurl = agencyCursor.getString(agencyCursor.getColumnIndex("agency_url"));
			actions.add(new AgencyAction(R.string.url, agencyurl, AgencyAction.ACTION_VIEW));

		}
		
    	AgencyActionAdapter details = new AgencyActionAdapter();
    	setListAdapter(details);

	}
	
    @Override
    protected void onStop() {
        stopManagingCursor(agencyCursor);
        agencyCursor.close();
        super.onPause();
    }
    
//    @Override
//    protected void onDestroy () {
//    	db.close();
//    }
//	
	public void onListItemClick(ListView l, View v, int position, long id) {
		AgencyAction action = actions.get(position);
		
		Intent i;
		switch (action.getType()) {
		
			case AgencyAction.ACTION_CALL:
				Uri callUri = Uri.parse("tel:" + action.getData());
				i = new Intent(Intent.ACTION_CALL, callUri);
				startActivity(i);
				break;
				
			case AgencyAction.ACTION_VIEW:
				i = new Intent(Intent.ACTION_VIEW);
				String url = action.getData();
				if (!url.startsWith("http://") && !url.startsWith("https://"))
					   url = "http://" + url;
				Uri urlUri = Uri.parse(url);
				i = new Intent(Intent.ACTION_VIEW, urlUri);
				startActivity(i);
				break; 
		}
	}
	
	class AgencyActionAdapter extends ArrayAdapter<AgencyAction> {
		AgencyActionAdapter() {
			super(AgencyDetails.this, R.layout.action_list_item, actions);
		}
		
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                AgencyAction action = actions.get(position);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.action_list_item, parent, false);
                TextView label = (TextView) view.findViewById(R.id.label);
                label.setText(action.getLabel());
                TextView data = (TextView) view.findViewById(R.id.data);
                data.setText(action.getData());
                return view;
        }

	}
}

package afzal.gtfsReader;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AgencyList extends Activity {
        
		protected SQLiteDatabase db;
		protected Cursor cursor;
		protected ListAdapter adapter;
		protected ListView agencyList;
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = (new DatabaseHelper(this)).getWritableDatabase();
        ListView agencyList = (ListView) findViewById(R.id.list);
        agencyList.setAdapter(adapter);
    }
}

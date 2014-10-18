package com.BabyTracker.Doctors;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class DoctorsListActivity extends Activity implements OnClickListener, OnItemClickListener{

	
	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	private Cursor mCursor = null;
	
	private final int REQUEST_CODE_DOCTORS = 0;
	private final int REQUEST_CODE_DOCTORS_ADD = 1;
	
	private ListView mDoctorsList;
	private SimpleCursorAdapter mDoctorsListAdapter = null;
	private Button mAddDoctor;
	private RelativeLayout _navRel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctors_list);
		
		initializeUI();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mDataBaseHelper = new BabyTrackerDataBaseHelper(DoctorsListActivity.this);
		mDataBaseHelper.openDataBase();
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.DOCTORS_TABLE);
		mCursor = mDataBaseHelper.select(query);
	
		Log.v(getClass().getSimpleName(), "mCursor "+mCursor.getCount());
		if (mCursor.getCount() > 0) {
			setData(mCursor);
		}else 
		{
			
			Intent intent = new Intent(DoctorsListActivity.this,DoctorsDetailsActivity.class);
			intent.setAction("fromList");
			startActivityForResult(intent, REQUEST_CODE_DOCTORS);
			
		}
	}
	
	public void setData(Cursor cursor)
	{
		
		String[] from = {BabyTrackerDataBaseHelper.DOCTORS_NAME,BabyTrackerDataBaseHelper.DOCTORS_PHONE};
		int[] to = {R.id.doctors_list_row_TXT_doctors_name,R.id.doctors_list_row_TXT_phone};
		
		mDoctorsListAdapter = new SimpleCursorAdapter(this, R.layout.doctors_list_row, cursor, from, to);		
		mDoctorsList.setAdapter(mDoctorsListAdapter);
	
	}
	
	public void initializeUI()
	{
		
		mDoctorsList = (ListView)findViewById(R.id.doctors_list);
		mAddDoctor = (Button)findViewById(R.id.doctors_listview_BTN_add);
		mAddDoctor.setOnClickListener(this);
		mDoctorsList.setOnItemClickListener(this);
	}
	
	/**
	 * Activity Result for medication details from home and add medication.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
        super.onActivityResult(requestCode, resultCode, data);   
        switch(requestCode) 
        {  
	        case REQUEST_CODE_DOCTORS:  
	        
	        	if (resultCode == RESULT_OK) 
	        	{
	        		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.DOCTORS_TABLE);
	        		mCursor = mDataBaseHelper.select(query);
	    			setData(mCursor);
	    			Log.v(getClass().getSimpleName(), " medication  query "+query);

	        	}
	        break;
	        
	        case REQUEST_CODE_DOCTORS_ADD:  
		        
	        	if (resultCode == RESULT_OK) 
	        	{
	        		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.DOCTORS_TABLE);
	        		mCursor = mDataBaseHelper.select(query);
	    			setData(mCursor);
	    			Log.v(getClass().getSimpleName(), "REQUEST_CODE_MEDICATION_ADD  "+query);

	        	}
	        break;
	        
	    }
    }

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		mCursor.moveToPosition(position);

		Intent intent = new Intent(DoctorsListActivity.this, DoctorsDescriptionActivity.class);
		intent.setAction("fromDoctorsList");
		intent.putExtra("name", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_NAME)));
		intent.putExtra("phone", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_PHONE)));
		intent.putExtra("email", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_EMAIL)));
		intent.putExtra("address", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_ADDRESS)));
		intent.putExtra("timings", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_TIMINGS)));
		intent.putExtra("_id", mCursor.getInt(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_ID)));
		startActivity(intent);
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
		
		case R.id.doctors_listview_BTN_add:
			Intent intent = new Intent(DoctorsListActivity.this,DoctorsDetailsActivity.class);
			startActivityForResult(intent, REQUEST_CODE_DOCTORS_ADD);
			break;

		default:
			break;
		}
	}
}

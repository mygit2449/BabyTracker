package com.BabyTracker.Medication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class MedicationListActivity extends Activity implements OnItemClickListener, OnClickListener{
	
	private SharedPreferences mSharedPreferences;
	private int baby_id;

	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	private Cursor mCursor = null;
	private final int REQUEST_CODE_MEDICATION = 0;
	private final int REQUEST_CODE_MEDICATION_ADD = 1;
	
	private ListView mMedicationList;
	private SimpleCursorAdapter mBabiesListAdapter = null;
	private Button mAddMedication;
	private RelativeLayout _navRel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medication_list);
		
		mSharedPreferences = getSharedPreferences("BabyTracker", MODE_WORLD_READABLE);
		baby_id = mSharedPreferences.getInt("baby_id", 0); // Getting the baby id from shared preferences(Saved In Home Screen).
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(MedicationListActivity.this);
		mDataBaseHelper.openDataBase();
	
		initializeUI();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		/**
		 *  checking baby id is existed or not in data base,
		 *  if not existed get those details and set to the list view.
		 */
		if (babyidExisted(baby_id)) 
		{
			
			String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.MEDICATION_TABLE+" where "+BabyTrackerDataBaseHelper.MEDICATION_BABY_ID+" = "+ baby_id);
			mCursor = mDataBaseHelper.select(query);
			setData(mCursor);
			

		}else 
		{
			
			Intent intent = new Intent(MedicationListActivity.this,MedicationActivity.class);
			intent.setAction("fromList");
			intent.putExtra("baby_id", baby_id);
			startActivityForResult(intent, REQUEST_CODE_MEDICATION);
			
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setData(mCursor);
	}

	/**
	 * Setting data to the cursor adapter.
	 * @param cursor --> which contains all medication details.
	 */
	public void setData(Cursor cursor)
	{
		
		String[] from = {BabyTrackerDataBaseHelper.MEDICATION_TYPE,BabyTrackerDataBaseHelper.MEDICATION_TIME};
		int[] to = {R.id.medication_list_row_TXT_medication_type,R.id.medication_list_row_TXT_duration};
		
		mBabiesListAdapter = new SimpleCursorAdapter(this, R.layout.medication_list_row, cursor, from, to);		
		mMedicationList.setAdapter(mBabiesListAdapter);
	
	}
	
	/**
	 * Initializing User interface here.
	 */
	public void initializeUI()
	{
		
		mMedicationList = (ListView)findViewById(R.id.medication_list);
		mAddMedication = (Button)findViewById(R.id.medication_listview_BTN_add);
		mAddMedication.setOnClickListener(this);
		mMedicationList.setOnItemClickListener(this);
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
	        case REQUEST_CODE_MEDICATION:  
	        
	        	if (resultCode == RESULT_OK) 
	        	{
	        		baby_id = data.getExtras().getInt("baby_id");
	        		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.MEDICATION_TABLE+" where "+BabyTrackerDataBaseHelper.MEDICATION_BABY_ID+" = "+ baby_id);
	    			mCursor = mDataBaseHelper.select(query);
	    			setData(mCursor);
	    			Log.v(getClass().getSimpleName(), " medication  query "+query);

	        	}
	        break;
	        
	        case REQUEST_CODE_MEDICATION_ADD:  
		        
	        	if (resultCode == RESULT_OK) 
	        	{
	        		baby_id = data.getExtras().getInt("baby_id");
	        		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.MEDICATION_TABLE+" where "+BabyTrackerDataBaseHelper.MEDICATION_BABY_ID+" = "+ baby_id);
	    			mCursor = mDataBaseHelper.select(query);
	    			setData(mCursor);
	    			Log.v(getClass().getSimpleName(), "REQUEST_CODE_MEDICATION_ADD  "+query);

	        	}
	        break;
	        
	    }
    }
	
	/**
	 *  Cursor adapter for setting all medication details.
	 * @author android
	 *
	 */
	public class MedicationAdapter extends CursorAdapter{

		@SuppressWarnings("unused")
		private Cursor mCursor;
		@SuppressWarnings("unused")
		private Context mContext;
		private final LayoutInflater mInflater;

		public MedicationAdapter(Context context, Cursor c) {
			super(context,c);
			// TODO Auto-generated constructor stub
			mInflater=LayoutInflater.from(context);
		    mContext=context;
		    mCursor = c;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			TextView mMedication_type_Txt = (TextView) view.findViewById(R.id.medication_list_row_TXT_medication_type);
			TextView mMedication_duration_Txt = (TextView) view.findViewById(R.id.medication_list_row_TXT_duration);
			
			
			mMedication_type_Txt.setText(""+cursor.getFloat(cursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_TYPE)));
			mMedication_duration_Txt.setText(""+cursor.getFloat(cursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_TIME)));
				
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			final View view=mInflater.inflate(R.layout.medication_list_row,parent,false); 
	        return view;

		} 
		
	}
	
	/* checking medication is existed in data base or not */
	public boolean babyidExisted(int baby_id)
	{
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.MEDICATION_TABLE+" where "+BabyTrackerDataBaseHelper.MEDICATION_BABY_ID+" = "+ baby_id);
		Log.v(getClass().getSimpleName(), " medication existance query "+query);
		Cursor mCursor = mDataBaseHelper.select(query);
		
		try
		{
			return mCursor.getCount() > 0 ? true : false;
		
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
			mCursor.close();
		}
		
		return false;
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		mCursor.moveToPosition(position);

		Intent intent = new Intent(MedicationListActivity.this, MedicationDescriptionActivity.class);
		intent.putExtra("dosage", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_DOSAGE)));
		intent.putExtra("medicinename", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_MEDICINE)));
		intent.putExtra("time", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_TIME)));
		intent.putExtra("medication_type", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_TYPE)));
		intent.putExtra("description", mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_DESCRIPTION)));
		intent.putExtra("medication_id", mCursor.getInt((mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_ID))));
		startActivity(intent);
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.medication_listview_BTN_add:
			Intent intent = new Intent(MedicationListActivity.this, MedicationActivity.class);
			intent.putExtra("baby_id", baby_id);
			intent.setAction("fromList");
			Log.v(getClass().getSimpleName(), " medication baby_id "+baby_id);
			startActivityForResult(intent, REQUEST_CODE_MEDICATION_ADD);
			break;

		default:
			break;
		}
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mDataBaseHelper.closeDataBase();
//		mCursor.close();
	}
}

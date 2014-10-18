package com.BabyTracker.Appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class DoctorAppointmentListActivity extends Activity implements OnClickListener, OnItemClickListener, OnCheckedChangeListener{

	private static final String LOG_TAG = DoctorAppointmentListActivity.class.getSimpleName();
	
	private final int REQUEST_CODE_APPOINTMENT = 0;

	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	
	private ListView mAppointmentList;
	private Button mAddAppointment_Btn;
	
	private TextView mBabygrowth_details_title;
	
	private Intent receiverIntent = null;
	private int baby_id, from_receiver;

	private DoctorAppointmentCursorAdapter mAppointmentCursorAdapter = null;
	
	private Cursor mAppointmentCursor = null, mCursor;
	private String query;
	
	private SharedPreferences mSharedPreferences;
	private RelativeLayout _navRel;


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctorappointment_listview);
		initializeUI();

		mDataBaseHelper = new BabyTrackerDataBaseHelper(this);
		mDataBaseHelper.openDataBase();
		
		receiverIntent = getIntent();
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mSharedPreferences = getSharedPreferences("BabyTracker",MODE_WORLD_READABLE);

		from_receiver = mSharedPreferences.getInt("baby_id", 0);
		
		if (receiverIntent.getAction().equalsIgnoreCase("fromService")) {
			baby_id = receiverIntent.getExtras().getInt("appointment_id");
			Log.v(LOG_TAG, " Service baby_id "+baby_id);
		}
		
		if (receiverIntent.getAction().equalsIgnoreCase("fromHome")) {
			baby_id = receiverIntent.getExtras().getInt("baby_id");
			Log.v(LOG_TAG, "baby id "+baby_id);
		}
		
		query =  String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.APPOINTMENT_TABLE+" where "+BabyTrackerDataBaseHelper.APPOINTMENT_BABY_ID+" = "+ baby_id);
		mCursor = mDataBaseHelper.select(query);
		
		if (receiverIntent.getAction().equalsIgnoreCase("fromReceiver")) 
		{
			Log.v(LOG_TAG, "from_receiver "+from_receiver);
			query =  String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.APPOINTMENT_TABLE+" where "+BabyTrackerDataBaseHelper.APPOINTMENT_BABY_ID+" = "+ from_receiver);
			getAppointmentDetails(from_receiver);
			
		}else if (babyidExisted(baby_id)) 
		{
			getAppointmentDetails(baby_id);
		}else
		{

			Intent intent = new Intent(DoctorAppointmentListActivity.this,DoctorAppointmentRegisterActivity.class);
			intent.putExtra("baby_id", baby_id);
			intent.setAction("fromlist");
			startActivityForResult(intent, REQUEST_CODE_APPOINTMENT);
		}
		
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		if (babyidExisted(baby_id))
			getAppointmentDetails(baby_id);
	 
	}
	
	public void initializeUI(){
		
		mAppointmentList = (ListView)findViewById(R.id.doctorappointment_listview);
		mAppointmentList.setOnItemClickListener(this);
		mAddAppointment_Btn = (Button)findViewById(R.id.doctorappointment_BTN_add);
		mAddAppointment_Btn.setOnClickListener(this);
		
		mBabygrowth_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Appointments");
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.doctorappointment_BTN_add:
			
			startActivity(new Intent(DoctorAppointmentListActivity.this, DoctorAppointmentRegisterActivity.class).setAction("add").putExtra("baby_id", baby_id));
			break;

		default:
			break;
		}
	}


	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		
		mCursor.moveToPosition(position);
		Log.v(LOG_TAG, "position "+position);
		int appointment_id = mCursor.getInt(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_ID));

		Intent intent = new Intent(DoctorAppointmentListActivity.this, DoctorAppointmentDetailsActivity.class);
		intent.setAction("edit");
		intent.putExtra("baby_id",appointment_id);
		startActivity(intent);
	}
	
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
        super.onActivityResult(requestCode, resultCode, data);   
        switch(requestCode) 
        {  
	        case REQUEST_CODE_APPOINTMENT:  
	        
	        	if (resultCode == RESULT_OK) 
	        	{
	        		baby_id = data.getExtras().getInt("appointment_id");
	        		getAppointmentDetails(baby_id);
				}
	        break;
	    }
    }
	
	
	/**
	 *  Getting the appointment details based on baby id and setting them to the adapter.
	 * @param baby_id
	 */
	private void getAppointmentDetails(int baby_id)
	{
		mAppointmentCursor = mDataBaseHelper.select(query);
		mAppointmentCursorAdapter = new DoctorAppointmentCursorAdapter(this, mAppointmentCursor);
		mAppointmentList.setAdapter(mAppointmentCursorAdapter);
	}

	/**
	 *  Cursor adapter for doctor appointment list.
	 * @author android
	 *
	 */
	private class DoctorAppointmentCursorAdapter extends CursorAdapter{


		@SuppressWarnings("unused")
		private Cursor mCursor;
		@SuppressWarnings("unused")
		private Context mContext;
		private final LayoutInflater mInflater;
		
		public DoctorAppointmentCursorAdapter(Context context, Cursor c) {
			super(context,c);
			// TODO Auto-generated constructor stub
			mInflater=LayoutInflater.from(context);
		    mContext=context;
		    this.mCursor = c;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView mDoctor_name_Txt = (TextView)view.findViewById(R.id.doctorappointment_list_row_TXT_doctorname);
			TextView mDateofAppointment = (TextView)view.findViewById(R.id.doctorappointment_list_row_TXT_datetime);
			mDoctor_name_Txt.setText(cursor.getString(cursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_DOCTORNAME)));
			mDateofAppointment.setText(cursor.getString(cursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_TIME)));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			final View view=mInflater.inflate(R.layout.doctorappointment_list_row,parent,false); 
	        return view;
		}
		
	}
	
	/* checking appointment is existed in data base or not */
	public boolean babyidExisted(int baby_id)
	{
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.APPOINTMENT_TABLE+" where "+BabyTrackerDataBaseHelper.APPOINTMENT_BABY_ID+" = "+ baby_id);
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    {
	        Intent intent = new Intent(this,Home.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mDataBaseHelper.close();
		mCursor.close();
	}
}

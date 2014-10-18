package com.BabyTracker.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class DoctorAppointmentDetailsActivity extends Activity implements OnClickListener{

	private TextView mDoctorname_Txt, mAppointdate_Txt, mPurpose_Txt, mNote_Txt, mBabygrowth_details_title,
	                 mAppointmentTime_Txt, mRemarks_Txt;
	
	private Button mEdit_Btn;
	
	private Intent receiverIntent = null;

	private static final int EDIT_ITEM = 1;
	
	private int baby_id;

	private BabyTrackerDataBaseHelper mDataBaseHelper;
	private RelativeLayout _navRel;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctorappointment_description);
		initialzeUI();
		receiverIntent = getIntent();
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(DoctorAppointmentDetailsActivity.this);
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		try
		{
			mDataBaseHelper.createDataBase();
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		
		if (receiverIntent.getAction().equalsIgnoreCase("edit")){ 
			baby_id = receiverIntent.getExtras().getInt("baby_id");
			Log.v(getClass().getSimpleName(), "app id "+baby_id);
			getAppointmentDetails(baby_id);
		}

	}
	
	private void getAppointmentDetails(int baby_id) {
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.APPOINTMENT_TABLE+" where "+BabyTrackerDataBaseHelper.APPOINTMENT_ID+" = "+ baby_id);
		Log.e("app details", "appointment "+query);
		Cursor tempcursor = mDataBaseHelper.select(query);
		
		if(tempcursor.moveToFirst()){
			SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			SimpleDateFormat mDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
			SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm");

			String mAppointmentDate_Str = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_TIME));
			
			Date mDate = null;
			try {
				mDate = mDateFormat.parse(mAppointmentDate_Str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String changeAppTime = mDateFormat1.format(mDate);
			String appointmentTime = timeFormater.format(mDate);
			
			mDoctorname_Txt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_DOCTORNAME)));
			mPurpose_Txt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_PURPOSE)));
			mNote_Txt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_NOTE)));
			mAppointmentTime_Txt.setText(appointmentTime);
			mRemarks_Txt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_REMARK)));
			mAppointdate_Txt.setText(changeAppTime);
		}
		tempcursor.close();
	}
	
	/* Initializing UI Here */
	public void initialzeUI(){
		
		mDoctorname_Txt = (TextView)findViewById(R.id.doctorappointment_description_TXT_doctorname_display);
		mAppointdate_Txt = (TextView)findViewById(R.id.doctorappointment_descriptionTXT_dateofappointment_display);
		mPurpose_Txt = (TextView)findViewById(R.id.doctorappointment_description_TXT_purpose_display);
		mNote_Txt = (TextView)findViewById(R.id.doctorappointment_description_TXT_note_display);
		
		mAppointmentTime_Txt = (TextView)findViewById(R.id.doctorappointment_descriptionTXT_timeofappointment_display);
		mRemarks_Txt = (TextView)findViewById(R.id.doctorappointment_description_TXT_note_remark);
		mEdit_Btn = (Button)findViewById(R.id.doctorappointment_description_BTN_edit);
		mEdit_Btn.setOnClickListener(this);
		
		mBabygrowth_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Appointment");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
        super.onActivityResult(requestCode, resultCode, data);   
        switch(requestCode) 
        {  
	        case EDIT_ITEM:  
	        {
	        	if (resultCode == RESULT_OK) 
	        	{
	    			baby_id = data.getExtras().getInt("baby_id");
	    			getAppointmentDetails(baby_id);
				}
	        }
	    }
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) 
		{
		case R.id.doctorappointment_description_BTN_edit:
			Log.v("details ", " baby_id "+baby_id);
			intent = new Intent(DoctorAppointmentDetailsActivity.this, DoctorAppointmentRegisterActivity.class);
			intent.putExtra("doctor_appointment", baby_id);
			intent.setAction("edit");
			startActivityForResult(intent, EDIT_ITEM);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDataBaseHelper.close();
	}
	
}

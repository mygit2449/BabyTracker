package com.BabyTracker.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.Reminder.ReminderBroadCast;
import com.BabyTracker.Settings.SettingsActivity;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class DoctorAppointmentRegisterActivity extends Activity implements OnClickListener, OnItemSelectedListener{
	
	private static String LOG_TAG = DoctorAppointmentRegisterActivity.class.getSimpleName();
	AlarmManager am;
	
	private EditText mDoctorname_ETxt, mPurpose_ETxt, mNote_ETxt, mRemarks_ETxt;
	private TextView mAppointment_date_Txt, mAppointment_time_Txt, mBabygrowth_details_title;
	private Button mSubmit_Btn, mClear_Btn, mSave_Btn, mCancel_Btn;
	private Spinner mReminder_Spn;
	
	private final int DATE_DIALOG_ID = 0;
	
	private int mDay;
	private int mMonth;
	private int mYear;
	
	private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 1;
    
	private final String[] MONTH_NAME = {"Jan", "Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};
	
	private final String[] reminder_time_inteval = {"None","1 Minute","5 Minutes","10 Minutes","15 Minutes","20 Minutes","25 Minutes",
			"30 Minutes","35 Minutes","40 Minutes","45 Minutes","1 Hour","2 Hours", "3 Hours", "12 Hours", "1 Day", "2 Days","3 Days", "1 Week"};
	
	private String mAppointmentDate_Str = "";
	private String mAppointmentTime_Str = "";
	private String mSelected_Remindertime = "";

	private BabyTrackerDataBaseHelper mDataBaseHelper;

	private String mTitle = "BabyTracker";

	private Intent receiverIntent = null;

	private RelativeLayout mSave_relative;
	
	private int baby_id = 0;
	
	private boolean mRemainder_Status = false;
	
	
	private String mAppointmentUpdateDate_Str = "", changeAppTime = "";
	
	private Date mDate = null;
	
	private SharedPreferences mAppointmentPreferences;
	private SharedPreferences.Editor mAppointmentEditor;
	private int reminder_time, reminder_id, reminder_status;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
 	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctorappointment_details);
		initializeUI();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);
		_navRel.setVisibility(View.VISIBLE);
		_TitleTxt = (TextView)findViewById(R.id.nav_text);
		_TitleTxt.setText("Back");
		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		 
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(DoctorAppointmentRegisterActivity.this);
		
		try
		{
			mDataBaseHelper.createDataBase();
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		
		mAppointmentPreferences = getSharedPreferences("BabyTracker", MODE_PRIVATE);
		mAppointmentEditor = mAppointmentPreferences.edit();
		
		reminder_time = mAppointmentPreferences.getInt("reminder_time", 0);
		reminder_id = mAppointmentPreferences.getInt("app_reminder_id", 10);
		reminder_status = mAppointmentPreferences.getInt("toggle_status", 1);
		
		Log.v(LOG_TAG, "reminder time "+reminder_time+"reminder_status "+reminder_status);
		
		ArrayAdapter<String> mReminderTimeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reminder_time_inteval);
		mReminderTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mReminder_Spn.setAdapter(mReminderTimeAdapter);
		
 		final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
         
        Calendar calender = Calendar.getInstance();
		mDay = calender.get(Calendar.DAY_OF_MONTH);
		mMonth = calender.get(Calendar.MONTH);
		mYear = calender.get(Calendar.YEAR);

		receiverIntent = getIntent();
		
		// from appointments list 
		if (receiverIntent.getAction().equalsIgnoreCase("fromlist")) 
			baby_id = receiverIntent.getExtras().getInt("baby_id");
		
		// when we want to edit the appointment details
		if (receiverIntent.getAction().equalsIgnoreCase("edit"))
		{ 

			baby_id = receiverIntent.getExtras().getInt("doctor_appointment");
			Log.v(LOG_TAG, "check baby id "+baby_id);
			mReminder_Spn.setSelection(reminder_time);
			getAppointmentDetails(baby_id);
			
		}
	
		//adding new Appointment details
		if (receiverIntent.getAction().equalsIgnoreCase("add")) 
			baby_id = receiverIntent.getExtras().getInt("baby_id");
		
	}
	
	/**
	 *  Getting the appointment details from database based on baby id.
	 *  setting appointment details to the UI.
	 * @param baby_id
	 */
	
	private void getAppointmentDetails(int baby_id) {
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.APPOINTMENT_TABLE+" where "+BabyTrackerDataBaseHelper.APPOINTMENT_ID+" = "+ baby_id);
		Log.e(LOG_TAG, "appointment "+query);
		Cursor tempcursor = mDataBaseHelper.select(query);
		
		if(tempcursor.moveToFirst()){
			SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			SimpleDateFormat mDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
			SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
			mAppointmentUpdateDate_Str = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_TIME));
			mDate = null;
			
			try {
				mDate = mDateFormat.parse(mAppointmentUpdateDate_Str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String changeAppDate = mDateFormat1.format(mDate);
			changeAppTime = timeFormater.format(mDate);
			Calendar calender = Calendar.getInstance();
			calender.setTime(mDate);
			
			mDoctorname_ETxt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_DOCTORNAME)));
			mPurpose_ETxt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_PURPOSE)));
			mNote_ETxt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_NOTE)));
			mRemarks_ETxt.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_REMARK)));
			
			mAppointment_date_Txt.setText(changeAppDate);
			mAppointment_time_Txt.setText(changeAppTime);
			mSave_relative.setVisibility(View.VISIBLE);
			
			Log.v(getClass().getSimpleName(), "changeAppTime "+changeAppTime);
			
		}
		tempcursor.close();
		tempcursor = null;
	}

	/* initializing ui here */
	public void initializeUI(){
		
		mDoctorname_ETxt = (EditText)findViewById(R.id.doctorappointment_details_ETXT_doctorname);
		mPurpose_ETxt = (EditText)findViewById(R.id.doctorappointment_details_ETXT_purpose);
		mNote_ETxt = (EditText)findViewById(R.id.doctorappointment_details_ETXT_note);
		mRemarks_ETxt = (EditText)findViewById(R.id.doctorappointment_details_ETXT_remarks);
		
		mAppointment_date_Txt = (TextView)findViewById(R.id.doctorappointment_details_TXT_dateofvisit_date);
		mAppointment_date_Txt.setOnClickListener(this);
		mAppointment_time_Txt = (TextView)findViewById(R.id.doctorappointment_details_TXT_timeofappointment_time);
		mAppointment_time_Txt.setOnClickListener(this);
		mSubmit_Btn = (Button)findViewById(R.id.doctorappointment_details_BTN_add);
		mSubmit_Btn.setOnClickListener(this);
		mClear_Btn = (Button)findViewById(R.id.doctorappointment_details_BTN_clear);
		mClear_Btn.setOnClickListener(this);
		
		mSave_Btn = (Button)findViewById(R.id.doctorappointment_details_BTN_save);
		mSave_Btn.setOnClickListener(this);
		
		mCancel_Btn = (Button)findViewById(R.id.doctorappointment_details_BTN_cancel);
		mCancel_Btn.setOnClickListener(this);
		
		mSave_relative = (RelativeLayout)findViewById(R.id.doctorappointment_details_LIN_babydetails4);
		
		mBabygrowth_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Register Appointment");
		
		mReminder_Spn = (Spinner)findViewById(R.id.doctorappointment_details_SPN_remindertime);
		mReminder_Spn.setOnItemSelectedListener(this);
		
	}

	/**
	 *  selecting date and time.
	 */
	@Override
	protected Dialog onCreateDialog(int id){
		
		switch (id) 
		{
		
		case DATE_DIALOG_ID:
			
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			
			
		case TIME_DIALOG_ID:
			
			 return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
			
		}
		return null;
		
	}
	
private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			mDay = dayOfMonth;
			mMonth = monthOfYear;
			mYear = year;
			updateDisplay();
		}
	};
	
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =  new TimePickerDialog.OnTimeSetListener(){

		     public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		     {
		    	 
		    	 mHour = hourOfDay;
		    	 mMinute = minute;
		    	 
			    if(hourOfDay>12)
			    {
	
			    	mAppointmentTime_Str = String.valueOf(pad(hourOfDay-12))+ ":"+(String.valueOf(pad(minute))+" PM");
	
			    	mAppointment_time_Txt.setText(String.valueOf(pad(hourOfDay-12))+ ":"+(String.valueOf(pad(minute))+" PM"));
			    }
			    if(hourOfDay==12)
			    {
			    	mAppointmentTime_Str = "12"+ ":"+(String.valueOf(pad(minute))+" PM");
			    	mAppointment_time_Txt.setText("12"+ ":"+(String.valueOf(pad(minute))+" PM"));
			    }
			    if(hourOfDay<12)
			    {
			    	mAppointmentTime_Str = String.valueOf(pad(hourOfDay))+ ":"+(String.valueOf(pad(minute))+" AM");
	
			    	mAppointment_time_Txt.setText(String.valueOf(pad(hourOfDay))+ ":"+(String.valueOf(pad(minute))+" AM"));
			    }
		     }
	};
	    
	
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
	    
	    
	private void updateDisplay(){
		
		mAppointmentDate_Str = new StringBuffer().append(pad(mDay)).append("-").append(MONTH_NAME[mMonth]).append("-").append(mYear).toString();
		mAppointment_date_Txt.setText( new StringBuilder().append(MONTH_NAME[mMonth]).append(" ")
				.append(mDay).append(",")
				.append(mYear));
		Log.v(LOG_TAG, "date of appointment "+mAppointmentDate_Str);
	}

	public void onClick(View v) {

		String validationStatus = "";
		SimpleDateFormat mDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date mAppointmentTimeStamp = null;
		Calendar checkCalender = null;
		SimpleDateFormat mDateFormat_reminder = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		switch (v.getId()) {
		
		case R.id.doctorappointment_details_TXT_dateofvisit_date:
			showDialog(DATE_DIALOG_ID);
			break;

		case R.id.doctorappointment_details_TXT_timeofappointment_time:
			showDialog(TIME_DIALOG_ID);
			break;
		
		case R.id.doctorappointment_details_BTN_add:
			
			long mInsertResult = 0;
			Date mAppointmentDate = null;
			
			
			try {
				mAppointmentDate = mDateFormat.parse(mAppointmentDate_Str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if (!mAppointmentDate_Str.equals(""))
			{
			
				Calendar mRemindercalender = Calendar.getInstance();
				mRemindercalender.setTime(mAppointmentDate);
				mRemindercalender.set(Calendar.HOUR_OF_DAY, mHour);
				mRemindercalender.set(Calendar.MINUTE, mMinute);
				mAppointmentTimeStamp = mRemindercalender.getTime();
				
				validationStatus = inputValidation(mAppointmentTimeStamp, mAppointmentTime_Str,mSelected_Remindertime, mDoctorname_ETxt.getText().toString(), mPurpose_ETxt.getText().toString());
	
				if (validationStatus.trim().equals("")) {
					String mDoctor_name = mDoctorname_ETxt.getText().toString();
					String mPurpose = mPurpose_ETxt.getText().toString();
					String mNote = mNote_ETxt.getText().toString();
					String mRemarks = mRemarks_ETxt.getText().toString();
					
					checkCalender = caluculateReminderTime(mSelected_Remindertime, mRemindercalender);
					
					String reminder_timeInterval = mDateFormat_reminder.format(mAppointmentTimeStamp);
					/* Inserting data into database */
					mInsertResult = mDataBaseHelper.insertAppointmentDetails(baby_id, mPurpose, mNote, mAppointmentTimeStamp.toString(), mRemainder_Status, mRemarks, mDoctor_name, reminder_timeInterval, reminder_id);
				
					mDataBaseHelper.insertNotificationdetails(baby_id, checkCalender.getTime().toString(), reminder_id, "appointment");

				}else{
					
					if (!validationStatus.trim().equals("")) 
					{
						
						alertDialogWithMessage(mTitle, validationStatus);
						
						if (mAppointmentDate_Str.trim().equals("")) 
							mAppointment_date_Txt.setHintTextColor(Color.RED);
						
						if (mAppointmentTime_Str.trim().equals("")) 
							mAppointment_time_Txt.setHintTextColor(Color.RED);
						
						if (mDoctorname_ETxt.getText().toString().equals("")) 
							mDoctorname_ETxt.setHintTextColor(Color.RED);
						
						if (mPurpose_ETxt.getText().toString().equals("")) 
							mPurpose_ETxt.setHintTextColor(Color.RED);
						
						if (mNote_ETxt.getText().toString().equals("")) 
							mNote_ETxt.setHintTextColor(Color.RED);
						
					}
				}
				
				if (mInsertResult > 0) 
				{
					
					if(reminder_status == 1)
					{
						
						setOneTimeAlarm(checkCalender, mDoctorname_ETxt.getText().toString());

						Intent data = new Intent();
						data.putExtra("appointment_id", baby_id);
						setResult(RESULT_OK, data);
						finish();
						
						reminder_id++;
						mAppointmentEditor.putInt("app_reminder_id",reminder_id);
						mAppointmentEditor.commit();
					
					}else
					{
						alertDialogWithMessageStatus(mTitle, "Please change the reminder status in to ON to get notifications");
					}
					
					setEventToCalender(checkCalender,mDoctorname_ETxt.getText().toString());

					Toast.makeText(this, "Doctor Appointment details Inserted Successfully", Toast.LENGTH_SHORT).show();
	
				}
			/*	else{
					Toast.makeText(this, "Insertion Failed ", Toast.LENGTH_SHORT).show();
				}*/
			}else{
				alertDialogWithMessage(mTitle, "Please enter valid date");
			}

			break;
			
		case R.id.doctorappointment_details_BTN_clear:
			
			mDoctorname_ETxt.setText("");
			mPurpose_ETxt.setText("");
			mNote_ETxt.setText("");
			mAppointment_date_Txt.setText("Date*");
			mAppointment_time_Txt.setText("Time*");
			mAppointment_date_Txt.setHintTextColor(Color.BLACK);
			mAppointment_time_Txt.setHintTextColor(Color.BLACK);
			mDoctorname_ETxt.setHintTextColor(Color.BLACK);
			mPurpose_ETxt.setHintTextColor(Color.BLACK);
			mNote_ETxt.setHintTextColor(Color.BLACK);
			
			break;
			
		case R.id.doctorappointment_details_BTN_save:
	
				String mDoctor_name = mDoctorname_ETxt.getText().toString();
				String mPurpose = mPurpose_ETxt.getText().toString();
				String mRemarks = mRemarks_ETxt.getText().toString();
				String mNote = mNote_ETxt.getText().toString();
			
				Date mAppointmentDateUpdate = null;
				
				Calendar updateCalender = null;
				Calendar mRemindercalenderUpdate = null;
				Date mAppointmentTimeStampUpdate = null;
				
				if (!mAppointmentDate_Str.equals("") && !mAppointmentTime_Str.equals(""))
				{
					
					
					try 
					{
						mAppointmentDateUpdate = mDateFormat.parse(mAppointmentDate_Str);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mRemindercalenderUpdate = Calendar.getInstance();
					mRemindercalenderUpdate.setTime(mAppointmentDateUpdate);
					mRemindercalenderUpdate.set(Calendar.HOUR_OF_DAY, mHour);
					mRemindercalenderUpdate.set(Calendar.MINUTE, mMinute);
					mAppointmentTimeStampUpdate = mRemindercalenderUpdate.getTime();
					mAppointmentTime_Str = mAppointment_time_Txt.getText().toString();


				}else if (!mAppointmentDate_Str.equals("")) 
				{
					
					try 
					{
						mAppointmentDateUpdate = mDateFormat.parse(mAppointmentDate_Str);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mRemindercalenderUpdate = Calendar.getInstance();
					mRemindercalenderUpdate.setTime(mAppointmentDateUpdate);
					mAppointmentTimeStampUpdate = mRemindercalenderUpdate.getTime();
					mAppointmentTime_Str = mAppointment_time_Txt.getText().toString();
					Log.v(getClass().getSimpleName(), "update date 22 "+mAppointmentTimeStampUpdate);

				}else if (!mAppointmentTime_Str.equals("")) 
				{
					
					mRemindercalenderUpdate = Calendar.getInstance();
					mRemindercalenderUpdate.setTime(mDate);
					mRemindercalenderUpdate.set(Calendar.HOUR_OF_DAY, mHour);
					mRemindercalenderUpdate.set(Calendar.MINUTE, mMinute);
					mAppointmentTimeStampUpdate = mRemindercalenderUpdate.getTime();
					mAppointmentTime_Str = mAppointment_time_Txt.getText().toString();

					Log.v(getClass().getSimpleName(), "update date 333 "+mAppointmentTimeStampUpdate);

				}
				
				else 
				{
					mRemindercalenderUpdate = Calendar.getInstance();
					mRemindercalenderUpdate.setTime(mDate);
					mAppointmentTimeStampUpdate = mRemindercalenderUpdate.getTime();
					mAppointmentTime_Str = mAppointment_time_Txt.getText().toString();
					Log.v(getClass().getSimpleName(), "update date 4444 "+mAppointmentTimeStampUpdate);

				}
				
				
				validationStatus = inputValidation(mAppointmentTimeStampUpdate, mAppointmentTime_Str, mSelected_Remindertime,mDoctorname_ETxt.getText().toString(), mPurpose_ETxt.getText().toString());
				
				if (validationStatus.trim().equals(""))
				{
				
					Log.v(LOG_TAG, " reminder time "+mSelected_Remindertime+" calender up "+mRemindercalenderUpdate);
					updateCalender = caluculateReminderTime(mSelected_Remindertime, mRemindercalenderUpdate);
					
					reminder_id++;
					mAppointmentEditor.putInt("app_reminder_id",reminder_id);
					mAppointmentEditor.commit();
					
					String updateReminder = mDateFormat_reminder.format(mAppointmentTimeStampUpdate);
					/* updating the appointment details into database */
					mDataBaseHelper.updateApppintmentdetails(mAppointmentTimeStampUpdate.toString(), mDoctor_name, mPurpose, mRemarks, mNote, updateReminder, baby_id, reminder_id);

					if(reminder_status == 1)
					{
						
//						new ReminderStatus().setOneTimeAlarm(DoctorAppointmentRegisterActivity.this, updateCalender, reminder_id, 0);
						
						setOneTimeAlarm(updateCalender, mDoctorname_ETxt.getText().toString());
						Intent data1 = new Intent();
						data1.putExtra("baby_id", baby_id);
						setResult(RESULT_OK, data1);
						finish();
					
					}else{
						alertDialogWithMessageStatus(mTitle, "Please change the reminder status in to ON to get notifications");
					}
					
					setEventToCalender(updateCalender, mDoctor_name);
				
				}else
					alertDialogWithMessage(mTitle, validationStatus);
				
			break;
			
		case R.id.doctorappointment_details_BTN_cancel:
			
			startActivity(new Intent(DoctorAppointmentRegisterActivity.this, DoctorAppointmentDetailsActivity.class).setAction("check"));

			break;
		}
	}
	
	
	/* validating the user input data */
	private String inputValidation(Date reminderTime,String reminderDuration, String appointment_time, String doctor_name, String purpose)
	{
		Date currentDate = new Date();
		
		Log.v(LOG_TAG, "current date "+currentDate+" reminderdate "+reminderTime);
		if (reminderTime.after(currentDate)) {
			if (!reminderDuration.equals("")) {
				if (!appointment_time.trim().equals("")) {
				if (!doctor_name.trim().equals("")) {
					if (!purpose.trim().equals("")) {
						return "";
					}else{
						return "Please enter purpose";
					}
				}else {
					return "Please enter doctor name";
				}
				
			}else {
				return "Please enter appointment time";
			}
			}else {
				return "Please select reminder duration";
			}	
		}else{
			return "Please Select valid Date";
		}
	}
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(DoctorAppointmentRegisterActivity.this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//finish();
				
				
			}
		})
		.create().show();
	}
	
	/* alert dialog */
	public void alertDialogWithMessageStatus(String title, String msg){
		
		new BabyTrackerAlert(DoctorAppointmentRegisterActivity.this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				startActivity(new Intent(DoctorAppointmentRegisterActivity.this, SettingsActivity.class));
//				Intent data1 = new Intent();
//				data1.putExtra("baby_id", baby_id);
//				setResult(RESULT_OK, data1);
//				finish();
				
			}
		})
		.create().show();
	}
	

	// selected reminder duration..
	public void onItemSelected(AdapterView<?> adapter, View arg1, int arg2,
			long arg3) {
		
		reminder_time = adapter.getSelectedItemPosition();
		mSelected_Remindertime = adapter.getSelectedItem().toString();
	
		Log.v(LOG_TAG, "reminder time selection "+reminder_time);

		mAppointmentEditor.putInt("reminder_time", reminder_time);
		mAppointmentEditor.commit();
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  Calculating reminder duration base on selected time
	 * @param selected_time
	 * @param appointment_date
	 * @return  calendar that will be set as alarm for notification. 
	 */
	private Calendar caluculateReminderTime(String selected_time, Calendar appointment_date){

		if (selected_time.contains("None")) {
			return appointment_date;
		}else
		{
			String[] arrDate = selected_time.split(" ");
			String duration = arrDate[0];
			int duration_int = Integer.parseInt(duration);
			
			if (selected_time.contains("Minute") || selected_time.contains("Minutes")) {
				appointment_date.add(Calendar.MINUTE, -duration_int);
			}else if(selected_time.contains("Hour") || selected_time.contains("Hours")){
				appointment_date.add(Calendar.HOUR, -duration_int);
			}else if(selected_time.contains("Day")||selected_time.contains("Days")){
				appointment_date.add(Calendar.DAY_OF_MONTH, -duration_int);
			}else if( selected_time.contains("Week"))
				appointment_date.add(Calendar.WEEK_OF_MONTH, -duration_int);
	
			Log.v(LOG_TAG, "duratoin  "+appointment_date.getTime());
		
		}
		return appointment_date;
		
	}

	public void setOneTimeAlarm(Calendar calender, String doctor_name) 
	{
		  
	  Log.v(LOG_TAG, " time "+calender.getTime());
	  
	  Intent intent = new Intent(this, ReminderBroadCast.class);
	  intent.setAction("fromAppointment");
	  intent.putExtra("doctorname", doctor_name);
	  intent.putExtra("reminder_id", reminder_id);

	  PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_ONE_SHOT);
	  am.set(AlarmManager.RTC,calender.getTimeInMillis(), pendingIntent);
		  
	}
	
	/**
	 * Setting event to the calendar
	 * @param reminderTime  ----> time of the calendar
	 * @param eventTitle    ----> title for calendar
	 */
	public void setEventToCalender(Calendar reminderTime, String eventTitle)
	{
		Calendar cal = Calendar.getInstance();
		try {

			long startTime = reminderTime.getTimeInMillis();
			
				
				final ContentResolver cr = this.getContentResolver();
		        final ContentValues values = new ContentValues();
		        values.put(CalendarContract.Events.DTSTART, startTime);
		        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis()+60*60*1000);
		        values.put(CalendarContract.Events.TITLE, "Doctor Appointment");
		        values.put(CalendarContract.Events.DESCRIPTION, "You have appointment with "+eventTitle);//+descTail);
		        values.put(CalendarContract.Events.CALENDAR_ID, 1); // TODO: why it's 3? from android developer site it's 3...
		        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getDisplayName());
		        values.put(CalendarContract.Events.HAS_ALARM, true);
		        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
		        Log.i(DoctorAppointmentDetailsActivity.class.getName(), "Uri returned=>" + uri.toString());
		// get the event ID that is the last element in the Uri
		        long eventID = Long.parseLong(uri.getLastPathSegment());
		        ContentValues reminders = new ContentValues();
		        reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
		        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
		        try {
		            cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
		        } catch (Exception e) { /* sketchy */
		            e.printStackTrace();
		        }
				
				
				
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
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
	}

	
}

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.Reminder.ReminderBroadCast;
import com.BabyTracker.Settings.SettingsActivity;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class AddReminderActivity extends Activity implements OnClickListener{

	private EditText  mNote_ETxt; 
	private TextView mAddReminder_date_Txt, mAddReminder_time_Txt, mHeading;
	
	private String mAddReminderDate_Str = "", mAddReminderTime_Str = "";
	private final String[] MONTH_NAME = {"Jan", "Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};

	private String mTitle = "Baby Tracker";
	private int mDay,mMonth, mYear,mHour, mMinute,reminder_id,  baby_id, appointment_id, reminder_status;
    
	private final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    
	private Button mSubmit_Btn, mClear_Btn, mSave_Btn, mCancel_Btn;
	
	private BabyTrackerDataBaseHelper mDataBaseHelper;
	
	private SharedPreferences mAppointmentPreferences;
	private SharedPreferences.Editor mAppointmentEditor;
	private SharedPreferences mSharedPreferences;

	private boolean mRemainder_Status = true;
	
	Date mDate = null;

	AlarmManager am;
	private Intent receiverIntent;
	private RelativeLayout mSave_relative;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_reminder);
		
		initializeUI();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);
		_navRel.setVisibility(View.VISIBLE);
		_TitleTxt = (TextView)findViewById(R.id.nav_text);
		_TitleTxt.setText("Home");
		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mHeading = (TextView) findViewById(R.id.simple_top_bar_Txt_title);
		mHeading.setText("Add Reminder");
		
		
		final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
         
        
        Calendar calender = Calendar.getInstance();
		mDay = calender.get(Calendar.DAY_OF_MONTH);
		mMonth = calender.get(Calendar.MONTH);
		mYear = calender.get(Calendar.YEAR);
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(AddReminderActivity.this);
		
		mAppointmentPreferences = getSharedPreferences("BabyTracker", MODE_PRIVATE);
		mAppointmentEditor = mAppointmentPreferences.edit();
		
		reminder_id = mAppointmentPreferences.getInt("reminder_id", 100);
		reminder_status = mAppointmentPreferences.getInt("toggle_status", 1);

		Log.v(getClass().getSimpleName(), "reminder_status "+reminder_status);

		mSharedPreferences = getSharedPreferences("BabyTracker", MODE_WORLD_READABLE);
		baby_id = mSharedPreferences.getInt("baby_id", 0);
		
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		try
		{
			mDataBaseHelper.createDataBase();
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		
		receiverIntent = getIntent();
		
		// when we want to edit the appointment details
		if (receiverIntent.getAction().equalsIgnoreCase("editReminder"))
		{ 

			appointment_id = receiverIntent.getExtras().getInt("appointment_id");
			Log.v(getClass().getSimpleName(), "check baby id "+appointment_id);
			getSelectedReminderDetails(appointment_id);
		}
		
	}

	public void initializeUI()
	{
		mNote_ETxt = (EditText)findViewById(R.id.reminders_add_ETXT_note);
		mAddReminder_date_Txt = (TextView)findViewById(R.id.reminders_add_TXT_date_reminder);
		mAddReminder_time_Txt = (TextView)findViewById(R.id.reminders_add_TXT_time_date);
		
		mAddReminder_date_Txt.setOnClickListener(this);
		mAddReminder_time_Txt.setOnClickListener(this);
		
		mSubmit_Btn = (Button)findViewById(R.id.reminders_add_BTN_add);
		mSubmit_Btn.setOnClickListener(this);
		mClear_Btn = (Button)findViewById(R.id.reminders_add_BTN_clear);
		mClear_Btn.setOnClickListener(this);
		
		mSave_relative = (RelativeLayout)findViewById(R.id.reminders_add_LIN_reminderdetails4);

		mSave_Btn = (Button)findViewById(R.id.reminders_add_BTN_save);
		mSave_Btn.setOnClickListener(this);
		
		mCancel_Btn = (Button)findViewById(R.id.reminders_add_BTN_cancel);
		mCancel_Btn.setOnClickListener(this);
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
	
			    	mAddReminderTime_Str = String.valueOf(pad(hourOfDay-12))+ ":"+(String.valueOf(pad(minute))+" PM");
	
			    	mAddReminder_time_Txt.setText(String.valueOf(pad(hourOfDay-12))+ ":"+(String.valueOf(pad(minute))+" PM"));
			    }
			    if(hourOfDay==12)
			    {
			    	mAddReminderTime_Str = "12"+ ":"+(String.valueOf(pad(minute))+" PM");
			    	mAddReminder_time_Txt.setText("12"+ ":"+(String.valueOf(pad(minute))+" PM"));
			    }
			    if(hourOfDay<12)
			    {
			    	mAddReminderTime_Str = String.valueOf(pad(hourOfDay))+ ":"+(String.valueOf(pad(minute))+" AM");
	
			    	mAddReminder_time_Txt.setText(String.valueOf(pad(hourOfDay))+ ":"+(String.valueOf(pad(minute))+" AM"));
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
		
		mAddReminderDate_Str = new StringBuffer().append(pad(mDay)).append("-").append(MONTH_NAME[mMonth]).append("-").append(mYear).toString();
		mAddReminder_date_Txt.setText( new StringBuilder().append(MONTH_NAME[mMonth]).append(" ")
				.append(mDay).append(",")
				.append(mYear));
		Log.v(getClass().getSimpleName(), "date of appointment "+mAddReminderDate_Str);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		SimpleDateFormat mDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat mDateFormat_reminder = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date mAppointmentDate = null;
		Date mAppointmentTimeStamp = null;
		String validationStatus = "";
		Calendar mRemindercalender = null;
		
		switch (v.getId()) 
		{
		case R.id.reminders_add_TXT_date_reminder:
			showDialog(DATE_DIALOG_ID);
			break;

		case R.id.reminders_add_TXT_time_date:
			showDialog(TIME_DIALOG_ID);
			break;
			
		case R.id.reminders_add_BTN_add:
			
			long mInsertResult = 0;
			
			
			if (!mAddReminderDate_Str.equals(""))
			{
			
				try
				{
					mAppointmentDate = mDateFormat.parse(mAddReminderDate_Str);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mRemindercalender = Calendar.getInstance();
				mRemindercalender.setTime(mAppointmentDate);
				mRemindercalender.set(Calendar.HOUR_OF_DAY, mHour);
				mRemindercalender.set(Calendar.MINUTE, mMinute);
				mAppointmentTimeStamp = mRemindercalender.getTime();
				
				String reminder_timeInterval = mDateFormat_reminder.format(mAppointmentTimeStamp);
				
				validationStatus = inputValidation(mAppointmentTimeStamp, mAddReminderTime_Str, mNote_ETxt.getText().toString());
	
				if (validationStatus.trim().equals("")) {
				
					String mNote = mNote_ETxt.getText().toString();
					
					mInsertResult = mDataBaseHelper.insertReminderDetails(mAppointmentTimeStamp.toString(), mNote,mRemainder_Status, reminder_id, reminder_timeInterval);

					mDataBaseHelper.insertNotificationdetails(baby_id, mAppointmentTimeStamp.toString(), reminder_id, "reminder");

				}else{
					
					if (!validationStatus.trim().equals("")) 
					{
						
						alertDialogWithMessage(mTitle, validationStatus);
						
						if (mAddReminderDate_Str.trim().equals("")) 
							mAddReminder_date_Txt.setHintTextColor(Color.RED);
						
						if (mAddReminderTime_Str.trim().equals("")) 
							mAddReminder_time_Txt.setHintTextColor(Color.RED);
						
						if (mNote_ETxt.getText().toString().equals("")) 
							mNote_ETxt.setHintTextColor(Color.RED);
						
					}
				}
				
			}
			
			if (mInsertResult > 0) 
			{
				
				if(reminder_status == 1)
				{

					setOneTimeAlarm(mRemindercalender, mNote_ETxt.getText().toString());
					Intent data = new Intent();
					setResult(RESULT_OK, data);
					finish();
					
					reminder_id++;
					mAppointmentEditor.putInt("reminder_id",reminder_id);
					mAppointmentEditor.commit();
				
				}else
				{
					alertDialogWithMessageStatus(mTitle, "Please change the reminder status in to ON to get notifications");
				}
			}
			break;
			
		case R.id.reminders_add_BTN_clear:
			
		
			mNote_ETxt.setText("");
			mNote_ETxt.setHintTextColor(Color.BLACK);
			mAddReminder_date_Txt.setText("Date*");
			mAddReminder_time_Txt.setText("Time*");
			
			break;
			
		case R.id.reminders_add_BTN_save:
			
			String note = mNote_ETxt.getText().toString();
				
			if (!mAddReminderDate_Str.equals("") && !mAddReminderTime_Str.equals(""))
			{
				
				
				try 
				{
					mAppointmentDate = mDateFormat.parse(mAddReminderDate_Str);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mRemindercalender = Calendar.getInstance();
				mRemindercalender.setTime(mAppointmentDate);
				mRemindercalender.set(Calendar.HOUR_OF_DAY, mHour);
				mRemindercalender.set(Calendar.MINUTE, mMinute);
				mAppointmentTimeStamp = mRemindercalender.getTime();
				mAddReminderTime_Str = mAddReminder_time_Txt.getText().toString();
				
				Log.v(getClass().getSimpleName(), "update date 11111 "+mAppointmentTimeStamp);

			}else if (!mAddReminderDate_Str.equals("")) 
			{
				
				try 
				{
					mAppointmentDate = mDateFormat.parse(mAddReminderDate_Str);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mRemindercalender = Calendar.getInstance();
				mRemindercalender.setTime(mAppointmentDate);
				mAppointmentTimeStamp = mRemindercalender.getTime();
				mAddReminderTime_Str = mAddReminder_time_Txt.getText().toString();
				Log.v(getClass().getSimpleName(), "update date 22 "+mAppointmentTimeStamp);

			}else if (!mAddReminderTime_Str.equals("")) 
			{
				
				mRemindercalender = Calendar.getInstance();
				mRemindercalender.setTime(mDate);
				mRemindercalender.set(Calendar.HOUR_OF_DAY, mHour);
				mRemindercalender.set(Calendar.MINUTE, mMinute);
				mAppointmentTimeStamp = mRemindercalender.getTime();
				mAddReminderTime_Str = mAddReminder_time_Txt.getText().toString();

				Log.v(getClass().getSimpleName(), "update date 333 "+mAppointmentTimeStamp);

			}
			
			else 
			{
				mRemindercalender = Calendar.getInstance();
				mRemindercalender.setTime(mDate);
				mAppointmentTimeStamp = mRemindercalender.getTime();
				mAddReminderTime_Str = mAddReminder_time_Txt.getText().toString();
				Log.v(getClass().getSimpleName(), "update date 4444 "+mAppointmentTimeStamp);

			}
			
				
			Log.v(getClass().getSimpleName(), "mAppointmentTimeStamp "+mAppointmentTimeStamp+"mAddReminderTime_Str "+mAddReminderTime_Str);
			validationStatus = inputValidation(mAppointmentTimeStamp, mAddReminderTime_Str, note);

			if (validationStatus.trim().equals("")) 
			{
			
				 String mNote = mNote_ETxt.getText().toString();
				
				 reminder_id++;
				 mAppointmentEditor.putInt("reminder_id",reminder_id);
				 mAppointmentEditor.commit();
				
				 String update_time = mDateFormat_reminder.format(mAppointmentTimeStamp);
				 mDataBaseHelper.updateReminderDetails(appointment_id,mAppointmentTimeStamp.toString(), mNote,mRemainder_Status, reminder_id, update_time);
				
				 
				 if(reminder_status == 1)
					{
						
//						new ReminderStatus().setOneTimeAlarm(AddReminderActivity.this, mRemindercalender, reminder_id, 0);
						
						Intent data1 = new Intent();
						data1.putExtra("appointment_id", appointment_id);
						setResult(RESULT_OK, data1);
						finish();
						
						reminder_id++;
						mAppointmentEditor.putInt("reminder_id",reminder_id);
						mAppointmentEditor.commit();
					
					}else
					{
						alertDialogWithMessageStatus(mTitle, "Please change the reminder status in to ON to get notifications");
					}
				 
				 setOneTimeAlarm(mRemindercalender, mNote_ETxt.getText().toString());
					
			}else
			{
				
				if (!validationStatus.trim().equals("")) 
				{
					
					alertDialogWithMessage(mTitle, validationStatus);
					
					if (mAddReminderDate_Str.trim().equals("")) 
						mAddReminder_date_Txt.setHintTextColor(Color.RED);
					
					if (mAddReminderTime_Str.trim().equals("")) 
						mAddReminder_time_Txt.setHintTextColor(Color.RED);
					
					if (mNote_ETxt.getText().toString().equals("")) 
						mNote_ETxt.setHintTextColor(Color.RED);
					
				}
			}
				
			break;
			
		case R.id.reminders_add_BTN_cancel:
			finish();
			break;
		}
	}
	
	/* validating the user input data */
	private String inputValidation(Date reminderTime, String appointment_time,  String note)
	{
		Date currentDate = new Date();
		
		Log.v(getClass().getSimpleName(), "current date "+currentDate+" reminderdate "+reminderTime);
		if (reminderTime.after(currentDate)) 
		{
				if (!appointment_time.trim().equals("")) 
				{
				
					if (!note.trim().equals("")) 
					{
						return "";
					}else
					{
						return "Please enter note";
					}
				}else 
				{
					return "Please enter reminder time";
				}
		}else 
		{
			return "Please enter valid date";
		}	
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mDataBaseHelper.close();
	}
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(AddReminderActivity.this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//finish();
			}
		})
		.create().show();
	}
	
	
	public void getSelectedReminderDetails(int baby_id)
	{

		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY,BabyTrackerDataBaseHelper.APPOINTMENT_TABLE + " where "
						+ BabyTrackerDataBaseHelper.APPOINTMENT_ID + " = " + baby_id);
		
		Log.v(getClass().getSimpleName(), "query  "+query);
		Cursor tempcursor = mDataBaseHelper.select(query);
	
		try 
		{

			if (tempcursor.moveToFirst()) 
			{
				String reminder_note = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_NOTE));
				String reminder_time = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_TIME));
		
				SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				SimpleDateFormat mDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
				SimpleDateFormat timeFormater = new SimpleDateFormat("hh:mm a");
		
				try {
					mDate = mDateFormat.parse(reminder_time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				mAddReminder_date_Txt.setText(mDateFormat1.format(mDate));
				mAddReminder_time_Txt.setText(timeFormater.format(mDate));
				mNote_ETxt.setText(reminder_note);
				mSave_relative.setVisibility(View.VISIBLE);
			}
				
		} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				tempcursor.close();
			}
		
	}	
	
	/**
	 * Setting alarm for the doctor appointment.
	 * @param calender ---> at which time give the alarm
	 * @param notetext ---> notification text.
	 */
	public void setOneTimeAlarm(Calendar calender, String notetext) 
	{
		  
	  Log.v(getClass().getSimpleName(), " time "+calender.getTime());
	  
	  Intent intent = new Intent(this, ReminderBroadCast.class);
	  intent.setAction("fromReminder");
	  intent.putExtra("note", notetext);
	  intent.putExtra("reminder_id", reminder_id);
	  PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminder_id,intent, PendingIntent.FLAG_ONE_SHOT);
	  am.set(AlarmManager.RTC,calender.getTimeInMillis(), pendingIntent);
		  
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
	
	/* alert dialog */
	public void alertDialogWithMessageStatus(String title, String msg){
		
		new BabyTrackerAlert(AddReminderActivity.this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				startActivity(new Intent(AddReminderActivity.this, SettingsActivity.class));
				
			}
		})
		.create().show();
	}
}

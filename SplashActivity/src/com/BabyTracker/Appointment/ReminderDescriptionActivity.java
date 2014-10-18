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

public class ReminderDescriptionActivity extends Activity implements OnClickListener{

	private Intent receiverIntent;
	
	private String reminder_time, reminder_note;

	private TextView reminder_time_Txt, reminder_note_Txt, reminder_date_Txt;

	private Button mEditReminder_Btn;
	
	private Date mDate = null;
	
	int appointment_id;
	private BabyTrackerDataBaseHelper mDataBaseHelper = null;

	private static final int EDIT_ITEM = 1;
	private RelativeLayout _navRel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_description);
		initializeUI();

		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		receiverIntent = getIntent();

		appointment_id = receiverIntent.getExtras().getInt("appointment_id");
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(this);
		mDataBaseHelper.openDataBase();
		
		setData(appointment_id);
		
	}

	
	public void setData(int reminder_id)
	{
		
		Log.v(getClass().getSimpleName(), "id ... "+reminder_id);
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY,BabyTrackerDataBaseHelper.APPOINTMENT_TABLE + " where "
						+ BabyTrackerDataBaseHelper.APPOINTMENT_ID + " = " + reminder_id);
		
		Log.v(getClass().getSimpleName(), "query  "+query);
		Cursor tempcursor = mDataBaseHelper.select(query);
	
		try 
		{

				if (tempcursor.moveToFirst()) 
				{
					reminder_note = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_NOTE));
					reminder_time = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_TIME));
			
					SimpleDateFormat mDateFormat = new SimpleDateFormat(
							"EEE MMM dd HH:mm:ss zzz yyyy");
					SimpleDateFormat mDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
					SimpleDateFormat timeFormater = new SimpleDateFormat("hh:mm a");
			
					try {
						mDate = mDateFormat.parse(reminder_time);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					reminder_date_Txt.setText(mDateFormat1.format(mDate));
					reminder_time_Txt.setText(timeFormater.format(mDate));
					reminder_note_Txt.setText(reminder_note);
				}
				
		} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				tempcursor.close();
			}
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
	        		Log.v(getClass().getSimpleName(), "appointment_id ... "+appointment_id);

	        		appointment_id = data.getExtras().getInt("appointment_id");
	    			setData(appointment_id);
				}
	        }
	    }
    }
	
	public void initializeUI() {

		reminder_date_Txt = (TextView)findViewById(R.id.reminder_details_TXT_reminderdate_display);
		reminder_time_Txt = (TextView) findViewById(R.id.reminder_details_TXT_remindertime_display);
		reminder_note_Txt = (TextView) findViewById(R.id.reminder_details_TXT_deccription_display);
		mEditReminder_Btn = (Button)findViewById(R.id.reminder_details_BTN_edit);
		mEditReminder_Btn.setOnClickListener(this);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId())
		{
		
		case R.id.reminder_details_BTN_edit:
			
			intent = new Intent(ReminderDescriptionActivity.this, AddReminderActivity.class);
			intent.putExtra("appointment_id", appointment_id);
			intent.setAction("editReminder");
			startActivityForResult(intent, EDIT_ITEM);
			
			break;

			
		default:
			break;
		}
	}
	
	
	
}

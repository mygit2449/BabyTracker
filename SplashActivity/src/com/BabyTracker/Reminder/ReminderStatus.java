package com.BabyTracker.Reminder;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class ReminderStatus {
	
	private BabyTrackerDataBaseHelper mBabyTrackerDataBaseHelper;

	private ArrayList<Integer> reminder_times;
	
	public void setOneTimeAlarm(Context context) 
	{

		mBabyTrackerDataBaseHelper = new BabyTrackerDataBaseHelper(context);
		mBabyTrackerDataBaseHelper.openDataBase();
		
	
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.NOTIFICATIONS_TABLE);
		
		Cursor mResetAlarm = mBabyTrackerDataBaseHelper.select(query);
		
		if (mResetAlarm.getCount() > 0)
		{
		
			int iReset = 0;
			int reminder_id = 0;
			String reminder_time = "";
			mResetAlarm.moveToPosition(iReset);
			
			do 
			{
			
				reminder_id = mResetAlarm.getInt(mResetAlarm.getColumnIndex(BabyTrackerDataBaseHelper.REMINDER_ID));
				reminder_time = mResetAlarm.getString(mResetAlarm.getColumnIndex(BabyTrackerDataBaseHelper.NOTIFICATIONS_TIME));
				
				Log.v(getClass().getSimpleName(), "reminder_id "+reminder_id+"reminder_time "+reminder_time);
				
			} while (mResetAlarm.moveToPrevious());
		}
		Log.e(" get date time ",query);
	
	}
	
	public void cancelAlarm(Context context) 
	{
	
		Intent intent = null;
		mBabyTrackerDataBaseHelper = new BabyTrackerDataBaseHelper(context);
		mBabyTrackerDataBaseHelper.openDataBase();
		
		Log.v(getClass().getSimpleName(), "reminder_times "+reminder_times.size());
		
		for (int iCtr = 0; iCtr < reminder_times.size(); iCtr++) 
		{
			 intent = new Intent(context, ReminderBroadCast.class);
			 intent.setAction("fromCancelAlarm");
			 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder_times.get(iCtr),intent, PendingIntent.FLAG_ONE_SHOT);
			 AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			 alarmManager.cancel(pendingIntent);
		}
	}
	
	
}

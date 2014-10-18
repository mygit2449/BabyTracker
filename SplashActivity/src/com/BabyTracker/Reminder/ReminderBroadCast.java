package com.BabyTracker.Reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class ReminderBroadCast extends BroadcastReceiver{


	private NotificationManager mNotificationManager;
	
	private static final int NOTIFICATION = 123;
	
	String text, note_str;
	Intent check_intent;
	
	int reminder_id, toggle_status;
	private SharedPreferences mSharedPreferences;

	private BabyTrackerDataBaseHelper mDataBaseHelper = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.v(getClass().getSimpleName(), "In broad cast receiver ");

			mDataBaseHelper = new BabyTrackerDataBaseHelper(context);
			mDataBaseHelper.openDataBase();

			mSharedPreferences = context.getSharedPreferences("BabyTracker", Context.MODE_PRIVATE);

			// checking the on/off option in settings screen if toggle status = 1 then only reminder will come.
			toggle_status = mSharedPreferences.getInt("toggle_status", 1);
			Log.v(getClass().getSimpleName(), "toggle_status "+toggle_status);

			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			if (intent.getAction().equalsIgnoreCase("fromVaccination")) 
			{
				note_str = intent.getExtras().getString("vaccinations");
				reminder_id = intent.getExtras().getInt("reminder_id");
				text = "Vaccinations for this month are "+note_str;		
				check_intent = new Intent(context, Home.class);
				if (reminderidExisted(reminder_id, "vaccination") && toggle_status == 1) {
					showNotification(context);
				}else{
					Log.v(getClass().getSimpleName(), " this reminder no more existed");
				}
			}
			
			
			if (intent.getAction().equalsIgnoreCase("fromAppointment")) 
			{
				
				note_str = intent.getExtras().getString("doctorname");
				reminder_id = intent.getExtras().getInt("reminder_id");
				text = "You have appointment with doctor."+note_str;	
				check_intent = new Intent(context, Home.class);
				check_intent.setAction("fromReceiver");
				
				if (reminderidExisted(reminder_id, "appointment") && toggle_status == 1) {
					showNotification(context);
				}else{
					Log.v(getClass().getSimpleName(), " this reminder no more existed");
				}
				
			}
			
			if (intent.getAction().equalsIgnoreCase("fromReminder")) 
			{
				note_str = intent.getExtras().getString("note");
				reminder_id = intent.getExtras().getInt("reminder_id");
				text = "This is the Reminder for "+note_str;	
				check_intent = new Intent(context, Home.class);
				
				if (reminderidExisted(reminder_id, "appointment") && toggle_status == 1) {
					showNotification(context);
				}else{
					Log.v(getClass().getSimpleName(), " this reminder no more existed");
				}
			}
			
			
		}
	
	/**
	 *  checking reminder is already existed in data base or not.
	 * @param baby_id
	 * @return true id reminder is existed other wise false if not existed.(if true only it gives the notification).
	 */
	public boolean reminderidExisted(int reminder_id, String reminder_type)
	{
		String query = "";

		if (reminder_type.equalsIgnoreCase("vaccination"))
			query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.NOTIFICATIONS_TABLE+" where "+BabyTrackerDataBaseHelper.REMINDER_ID+" = "+ reminder_id);
		else
		   query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.APPOINTMENT_TABLE+" where "+BabyTrackerDataBaseHelper.APPOINTMENT_NOTIFICATIONID+" ='" + reminder_id + "'");
		
		Log.v(getClass().getSimpleName(), " notification existance query "+query);
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

	/**
	 * Showing the notification.
	 * @param context
	 */
	public void showNotification(Context context)
	{

		String title = "BabyTracker Reminder";

		int icon = R.drawable.app_icon;
		long time = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, text, time);
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, check_intent, 0);
		notification.setLatestEventInfo(context, title, text, contentIntent);
		
		notification.flags |= Notification.FLAG_INSISTENT;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
	
		// Send the notification to the system.
		mNotificationManager.notify(NOTIFICATION, notification);
	}
}

package com.BabyTracker.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Appointment.DoctorAppointmentListActivity;
import com.BabyTracker.Appointment.RemindersListActivity;
import com.BabyTracker.BabyGrowth.BabyTrackerGrowthActivity;
import com.BabyTracker.BabyProfile.BabyProfileActivity;
import com.BabyTracker.DailyActivity.DailyActivityDetails;
import com.BabyTracker.Doctors.DoctorsListActivity;
import com.BabyTracker.Emergency.EmergencyActivity;
import com.BabyTracker.Helper.AgeCalculater;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.MedicalHistory.MedicalHistoryActivity;
import com.BabyTracker.Menu.MenuOptionsActivity;
import com.BabyTracker.Model.VaccinationModel;
import com.BabyTracker.Reminder.ReminderBroadCast;
import com.BabyTracker.Vaccination.VaccinationActivity;
import com.BabyTracker.dialog.BabyTrackerAlert;

/**
 *  This is the class which will display all the home screen details.
 *  First you need register you baby to access home screen items.
 * @author Satyasivaprasad
 *
 */
public class Home extends MenuOptionsActivity {

	private static final String LOG_TAG = Home.class.getSimpleName();

	private final int REQUEST_CODE_TRACKER = 0;
	private final int REQUEST_CODE_PROFILE = 1;
	private int baby_id, age_in_months, alarm_id;

	private boolean mAppFirstLunch = false;
	private String mAlertTitle = "BabyTacker";

	private String mAlertMessage = "Register Your Baby Details";

	private byte imageData[];

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mSharedPreferencesEditor;
	private BabyTrackerDataBaseHelper mDataBaseHelper = null;

	private ImageView img_baby_img;
	private TextView txt_baby_name, txt_baby_age, txt_baby_gender;
	private String name = "";
	private String mBaby_age = "";

	
	private ArrayList<VaccinationModel> mVaccinationTimies;
	private Calendar  calender;

	private final String[] vaccination_name = {"HepB","HepB, DTaP, PCV, Hib, Polio, RV","HepB, DTaP, PCV, Hib, Polio, RV, Influenza","MMR, DTaP, PCV, Hib, Chickenpox, HepA, Influenza"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		initializeUI();

		mDataBaseHelper = new BabyTrackerDataBaseHelper(this);
		mDataBaseHelper.openDataBase();
		
		mSharedPreferences = getSharedPreferences("BabyTracker",MODE_WORLD_READABLE);
		mSharedPreferencesEditor = mSharedPreferences.edit();
		
		mAppFirstLunch = mSharedPreferences.getBoolean("profileCheck", false);

		baby_id = mSharedPreferences.getInt("baby_id", 0);
		age_in_months = mSharedPreferences.getInt("baby_age", 0);
		

		/* checking the app is lunching for first time or not */
		if (!mAppFirstLunch)
		{
			mSharedPreferencesEditor.putBoolean("profileCheck", true);
			mSharedPreferencesEditor.commit();
			alertDialogWithMessage(mAlertTitle, mAlertMessage);
		}
		
		calender = Calendar.getInstance();
		
	}

	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		Log.v(getClass().getSimpleName(), "baby id1 " + baby_id);

		if (baby_id != 0) 
		{
			getSelectedBabyProfile(baby_id);  // Getting the baby profile details based on baby id and set them to ui.
		}
		
		if (vaccinationidExisted(baby_id)) // Checking vaccination is existed in data base or not.
		{
			Log.v(getClass().getSimpleName(), " baby id already existed");

		}else if(baby_id != 0)
		{
			setVaccinationNotifications(age_in_months);
		}
		
	}

	/**
	 * Setting vaccination Notifications based on baby age in months.
	 * @param months
	 */
	public void setVaccinationNotifications(int months)
	{
	
		mVaccinationTimies = mDataBaseHelper.getVaccinationTimes(months);  // getting vaccinations from data base.
		
		alarm_id = mSharedPreferences.getInt("alarm_id", 1000);
		String vaccinations_str = "";
		
		for (int iCtr = 0; iCtr < mVaccinationTimies.size(); iCtr++) 
		{
			
			int vaccination_month = mVaccinationTimies.get(iCtr).getVaccination_starttime();
			
			if (vaccination_month != 0) 
			{
				
				Calendar currentcalendar;
				
				long currentTime = System.currentTimeMillis();
				currentcalendar = Calendar.getInstance();
				Date date = new Date(currentTime);
				currentcalendar.setTime(date);
				currentcalendar.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
				
				if (vaccination_month == 1) {
					vaccinations_str = vaccination_name[0];
				}else if (vaccination_month == 2) {
					vaccinations_str = vaccination_name[1];
				}else if (vaccination_month == 4) {
					vaccinations_str = vaccination_name[1];
				}else if (vaccination_month == 6) {
					vaccinations_str = vaccination_name[2];
				}else if (vaccination_month == 12) {
					vaccinations_str = vaccination_name[3];
				}
				
				// if vaccination month is equal to baby age then show notification immediately.
				if (age_in_months == vaccination_month)
				{
				
					setVaccinationReminder(alarm_id, currentcalendar, vaccinations_str);
					mDataBaseHelper.insertNotificationdetails(baby_id, currentcalendar.getTime().toString(), alarm_id, "vaccination");
	
				}else
				{
					
					currentcalendar.add(Calendar.MONTH, vaccination_month - 2);  // -2 for calculating the future baby age(4ms, 6ms, 12ms).
					mDataBaseHelper.insertNotificationdetails(baby_id, currentcalendar.getTime().toString(), alarm_id, "vaccination");
					setVaccinationReminder(alarm_id, currentcalendar, vaccinations_str);
					
				}
				
				Log.v(getClass().getSimpleName(), "vaccination_month "+vaccination_month+" currentcalendar "+currentcalendar.getTime());
		
				alarm_id++;
				
				mSharedPreferencesEditor.putInt("alarm_id", alarm_id);
				mSharedPreferencesEditor.commit();
			}
			

		}
		
	}
	
	/**
	 *  Setting the reminder for the vaccination.
	 * @param uniqueId ----> for identifying the alarm intent.
	 * @param vcCaledar ----> for time alarm.
	 * @param vaccinations_details  -----> for selected vaccination details.
	 */
	
	public void setVaccinationReminder(int uniqueId, Calendar vcCaledar, String vaccinations_details)
	{
		
		Intent intent = new Intent(Home.this, ReminderBroadCast.class);
		intent.setAction("fromVaccination");
		intent.putExtra("vaccinations", vaccinations_details);
		intent.putExtra("reminder_id", uniqueId);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(Home.this, uniqueId,intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC,vcCaledar.getTimeInMillis(), pendingIntent);
	
	}
	
	/* Initializing UI Here */
	public void initializeUI() {
		img_baby_img = (ImageView) findViewById(R.id.homescreen_baby_image);
		txt_baby_name = (TextView) findViewById(R.id.homescreen_text_name);
		txt_baby_age = (TextView) findViewById(R.id.homescreen_text_babyage);
		txt_baby_gender = (TextView) findViewById(R.id.homescreen_text_baby_gender);
	}

	/* by clicking growth icon */
	public void onGrowthClicked(View v) {

		if (baby_id == 0)
			alertDialogWithMessage(mAlertTitle, "Please Register your baby");
		else
			startActivity(new Intent(this, BabyTrackerGrowthActivity.class).putExtra("baby_id", baby_id).putExtra("baby_age", mBaby_age));

	}

	/* by clicking medical history icon */
	public void onMedicalHistoryClicked(View v) {

		if (baby_id == 0)
			alertDialogWithMessage(mAlertTitle, "Please Register your baby");
		else
			startActivity(new Intent(this, MedicalHistoryActivity.class).putExtra("baby_id", baby_id).setAction("fromHome"));

	}

	/* by clicking appointment icon */
	public void onAppointmentClicked(View v) {

		if (baby_id == 0)
			alertDialogWithMessage(mAlertTitle, "Please Register your baby");
		else
			startActivity(new Intent(this, DoctorAppointmentListActivity.class).putExtra("baby_id", baby_id).setAction("fromHome"));

	}

	/* by clicking vaccination icon */
	public void onVaccinationClicked(View v) {
		if (baby_id == 0)
			alertDialogWithMessage(mAlertTitle, "Please Register your baby");
		else
			startActivity(new Intent(this, VaccinationActivity.class));
	}

	/* by clicking daily activity icon */
	public void onDailyActivityClicked(View v) {

		if (baby_id == 0)
			alertDialogWithMessage(mAlertTitle, "Please Register your baby");
		else
			startActivity(new Intent(this, DailyActivityDetails.class).putExtra("baby_id", baby_id).setAction("fromHome"));

	}

	/* by clicking tracker icon */
	public void onTrackerClicked(View v) {

		if (baby_id == 0)
			alertDialogWithMessage(mAlertTitle, "Please Register your baby");
		else
			startActivityForResult(new Intent(this, BabiesListActivity.class),REQUEST_CODE_TRACKER);

	}

	/* by clicking doctor icon */
	public void onDoctorClicked(View v) {
		startActivity(new Intent(this, DoctorsListActivity.class));
	}

	/* by clicking emergency icon */
	public void onEmergencyClicked(View v) {
		startActivity(new Intent(Home.this, EmergencyActivity.class));
	}

	/* by clicking reminder icon */
	public void onReminderClicked(View v) {
		startActivity(new Intent(Home.this, RemindersListActivity.class));
	}

	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg) {

		new BabyTrackerAlert(this, title, msg).setPositiveButton("Now",new android.content.DialogInterface.OnClickListener() 
		{

			public void onClick(DialogInterface dialog,	int which)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(Home.this,BabyProfileActivity.class);
				intent.setAction("fromHome");
				startActivityForResult(intent,REQUEST_CODE_PROFILE);
			}
		}).setIcon(android.R.drawable.ic_dialog_info).setNegativeButton("Later", null).create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_PROFILE:

			if (resultCode == RESULT_OK) 
			{
				
				baby_id = data.getExtras().getInt("baby_id");
				getSelectedBabyProfile(baby_id);
				mSharedPreferencesEditor.putInt("baby_id", baby_id);
				mSharedPreferencesEditor.commit();
				
			}

			break;

		case REQUEST_CODE_TRACKER:

			if (resultCode == RESULT_OK)
			{
				
				baby_id = data.getExtras().getInt("baby_id");
				Log.v(LOG_TAG, "baby id " + baby_id);
				getSelectedBabyProfile(baby_id);
				mSharedPreferencesEditor.putInt("baby_id", baby_id);
				mSharedPreferencesEditor.commit();
			
			}

			break;
		}
	}

	/**
	 * Getting the selected baby profile based on selected baby id.
	 * 
	 * @param baby_id
	 */
	public void getSelectedBabyProfile(int baby_id) {
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY,BabyTrackerDataBaseHelper.PROFILE_TABLE + " where "
						+ BabyTrackerDataBaseHelper.KEY_ID + " = " + baby_id);
		
		Log.v(getClass().getSimpleName(), "profile query "+query);
		Cursor tempcursor = mDataBaseHelper.select(query);

		try {

			if (tempcursor.moveToFirst())
			{
				
				name = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_NAME));
				String dob = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_DOB));
				imageData = tempcursor.getBlob(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_IMAGE));
				txt_baby_gender.setText(tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_GENDER)));
				txt_baby_name.setText(name);
				
				long currentTime = System.currentTimeMillis();
				Calendar firstDate = Calendar.getInstance();
				firstDate.setTimeInMillis(currentTime);
				
				SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				Date date_of_birth = mDateFormat.parse(dob);
				Calendar dobCal = Calendar.getInstance();
				dobCal.setTime(date_of_birth);

				age_in_months  = (int)monthsBetween(firstDate, dobCal);
				System.out.println("Diff is.." + age_in_months);
				
				mBaby_age = new AgeCalculater().calculateAge(dob);
				
				mSharedPreferencesEditor.putInt("baby_age", age_in_months);
				mSharedPreferencesEditor.commit();
				
				txt_baby_age.setText(new AgeCalculater().calculateAge(dob));
				img_baby_img.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
				
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			tempcursor.close();
		}

	}

	 public static double monthsBetween(Calendar date1, Calendar date2){
	        double monthsBetween = 0;
	        //difference in month for years
	        monthsBetween = (date1.get(Calendar.YEAR)-date2.get(Calendar.YEAR))*12;
	        //difference in month for months
	        monthsBetween += date1.get(Calendar.MONTH)-date2.get(Calendar.MONTH);
	        //difference in month for days
	        if(date1.get(Calendar.DAY_OF_MONTH)!=date1.getActualMaximum(Calendar.DAY_OF_MONTH)
	                && date1.get(Calendar.DAY_OF_MONTH)!=date1.getActualMaximum(Calendar.DAY_OF_MONTH) ){
	            monthsBetween += ((date1.get(Calendar.DAY_OF_MONTH)-date2.get(Calendar.DAY_OF_MONTH))/31d);
	        }
	        return monthsBetween;
	    }
	
	/**
	 *  checking vaccination is already existed in data base or not.
	 * @param baby_id
	 * @return true id vaccination is existed other wise false if not existed.
	 */
	public boolean vaccinationidExisted(int baby_id)
	{
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.NOTIFICATIONS_TABLE+" where "+BabyTrackerDataBaseHelper.NOTIFICATIONS_BABY_ID+" = "+ baby_id);
		Log.v(LOG_TAG, " vaccination existance query "+query);
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
}

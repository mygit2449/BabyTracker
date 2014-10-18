package com.BabyTracker.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.BabyTracker.R;
import com.BabyTracker.Model.DailyActivityModel;
import com.BabyTracker.Model.GraphModel;
import com.BabyTracker.Model.MedicalHistoryModel;
import com.BabyTracker.Model.VaccinationModel;

public class BabyTrackerDataBaseHelper extends SQLiteOpenHelper{
	
	/* Database name */
	private final static String DATABASE_NAME = "baby_tracker.sqlite";
	
	public final static String SELECT_QUERY = "select * from %s;";	
	
	private final static int DATA_VERSION = 1;
	
	private String DB_PATH; 
	
	private Context mContext;
	private SQLiteDatabase mSqLiteDatabase = null;

	
	/* Baby profile table details */
	public static final String PROFILE_TABLE = "baby";
	public static final String KEY_ID = "_id";
	public static final String  KEY_NAME = "name";	
	public static final String  KEY_DOB = "date_of_birth";
	public static final String  KEY_IMAGE = "photo";
	public static final String  KEY_GENDER = "gender";
	public static final String  KEY_BABY_HEIGHT = "baby_height";
	public static final String  KEY_BABY_WEIGHT = "baby_weight";

	/* Baby growth table details */
	public static final String GROWTH_TABLE = "growth_details";
	public static final String GROWTH_ID = "_id";
	public static final String GROWTH_BABY_ID = "baby_id";
	public static final String GROWTH_BABY_HEIGHT = "baby_height";
	public static final String GROWTH_BABY_WEIGHT = "baby_weight";
	public static final String GROWTH_BABY_CIRCUMFERENCE = "baby_circumference";
	public static final String GROWTH_DATE_OF_UPDATE = "date_of_update";
	public static final String GROWTH_BABY_AGE = "baby_age";

	
	/* Baby medicalhistory table details */
	public static final String MEDICALHISTORY_TABLE = "medical_history";
	public static final String MEDICALHISTORY_ID = "_id";
	public static final String MEDICALHISTORY_BABY_ID = "baby_id";
	public static final String MEDICALHISTORY_DATE = "date_of_visit";
	public static final String MEDICALHISTORY_DOCTORNAME = "doctor";
	public static final String MEDICALHISTORY_PURPOSE = "purpose";
	public static final String MEDICALHISTORY_REMARKS = "remark";
	public static final String MEDICALHISTORY_NOTE = "note";
	
	/* Daily Activity table details */
	public static final String DAILYACTIVITY_TABLE = "daily_activities";
	public static final String DAILYACTIVITY_ID = "_id";
	public static final String DAILYACTIVITY_BABY_ID = "baby_id";
	public static final String DAILYACTIVITY_TIME  = "time";
	public static final String DAILYACTIVITY_DIPER_STATUS = "activity";
	public static final String DAILYACTIVITY_MILK_QTY = "quantity";
	public static final String DAILYACTIVITY_SLEEPINGHOURS = "sleeping_hours";
	public static final String DAILYACTIVITY_DATE = "date";

	/* Baby appointment table details */
	public static final String APPOINTMENT_TABLE = "appointment";
	public static final String APPOINTMENT_ID = "_id";
	public static final String APPOINTMENT_BABY_ID = "baby_id";	
	public static final String APPOINTMENT_PURPOSE = "purpose";
	public static final String APPOINTMENT_NOTE = "note";
	public static final String APPOINTMENT_TIME = "time_of_appointment";
	public static final String APPOINTMENT_IS_REMINDER_SET = "is_reminder_set";
	public static final String APPOINTMENT_REMARK = "remark";
	public static final String APPOINTMENT_REMINDER_TIME = "reminder_time_interval";
	public static final String APPOINTMENT_DOCTORNAME = "doctor_name";
	public static final String APPOINTMENT_NOTIFICATIONID = "notification_id";

	
	/* Baby medication table details */
	public static final String MEDICATION_TABLE = "medication";
	public static final String MEDICATION_ID = "_id";
	public static final String MEDICATION_BABY_ID = "baby_id";	
	public static final String MEDICATION_TYPE = "medication_type";
	public static final String MEDICATION_DOSAGE = "dosage";
	public static final String MEDICATION_TIME = "time_medication";
	public static final String MEDICATION_MEDICINE = "medicine";
	public static final String MEDICATION_DESCRIPTION = "description";
	
	
	/* Baby doctors table details */
	public static final String DOCTORS_TABLE = "doctors";
	public static final String DOCTORS_ID = "_id";
	public static final String DOCTORS_NAME = "doctor_name";
	public static final String DOCTORS_PHONE = "phone_number";
	public static final String DOCTORS_EMAIL = "mail_id";
	public static final String DOCTORS_ADDRESS = "contact_address";
	public static final String DOCTORS_TIMINGS = "working_hours";
	
	
	/* Baby emergency table details */
	public  static final String EMERGENCY_TABLE = "emergency";
	public  static final String EMERGENCY_ID = "_id";
	public static final String  EMERGENCY_NAME = "contact_name";	
	public static final String  EMERGENCY_NUMBER = "contact_number";
	
	/* Baby vaccination status table details */
	public static final String VACCINATION_STATUS_TABLE = "vaccinations_status";
	public static final String VACCINATION_KEY = "_id";
	public static final String VACCINATION_STATUS_ID = "vaccination_id";
	public static final String  VACCINATION_STATUS_NAME = "vaccination_name";	
	public static final String  VACCINATION_BABY_ID = "baby_id";
	public static final String  VACCINATION_STATUS_DESCRIPTION = "about_vaccine";
	public static final String  VACCINATION_STATUS_END_TIME = "ending_time";
	public static final String  VACCINATION_STATUS_TIME = "time_vaccination";
	public static final String  VACCINATION_STATUS_COMPLETE = "vaccination_status";
	
	/* Baby vaccination status table details */
	public static final String VACCINATION_TABLE = "vaccinations_data";
	public static final String VACCINATION_ID = "_id";
	public static final String VACCINATION_NAME = "vaccination_name";	
	public static final String VACCINATION_DESCRIPTION = "about_vaccine";
	public static final String VACCINATION_START_TIME = "starting_time";
	public static final String VACCINATION_END_TIME = "ending_time";
	public static final String VACCINATION_TIME = "time_vaccination";
	public static final String VACCINATION_TYPE_ID = "vaccination_type_id";
	public static final String VACCINATION_STATUS = "vaccination_status";
	
	
	/* Baby vaccination status table details */
	public static final String NOTIFICATIONS_TABLE = "Reminder_Times";
	public static final String NOTIFICATIONS_ID = "_id";
	public static final String NOTIFICATIONS_TIME = "reminder_time";
	public static final String NOTIFICATIONS_BABY_ID = "baby_id";
	public static final String REMINDER_ID = "notification_id";
	public static final String NOTIFICATIONS_STATUS = "vaccination_status";

	
	public BabyTrackerDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATA_VERSION);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		DB_PATH="/data/data/" + context.getPackageName() + "/databases/";
        Log.v("The Database Path" , DB_PATH);
	}

	/* create an empty database if data base is not existed */
	public void createDataBase() throws IOException{
		
		
			 
	    	boolean dbExist = checkDataBase();
	 
	    	if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	 
	    		//By calling this method and empty database will be created into the default system path
	               //of your application so we are gonna be able to overwrite that database with our database.
	        	//this.getReadableDatabase();
	 
	        	try {
	    			copyDataBase();
	    			
	        	}
	        	catch(SQLiteException ex){
	        			ex.printStackTrace();
	        		throw new Error("Error copying database");
	        	
	        	} catch (IOException e) {
	        		e.printStackTrace();
	        		throw new Error("Error copying database");
	        	}
	    		
	    		try{
	    			openDataBase();
	    		}
	    		catch(SQLiteException ex){
	    			ex.printStackTrace();
	    			throw new Error("Error opening database");
	    			
	    		}catch(Exception e){
	    			e.printStackTrace();
	    			throw new Error("Error opening database");
	    		}
	    		
	    	}
	    
	}
	
	
	/* checking the data base is existed or not */
	/* return true if existed else return false */
	private boolean checkDataBase()
	{
		try
		{
		   String check_Path = DB_PATH  + DATABASE_NAME;
		   mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		return mSqLiteDatabase != null ? true : false;
	}
	
	 private void copyDataBase() throws IOException{
	    	
	    	File dbDir = new File(DB_PATH);
	        if (!dbDir.exists()) {
	            dbDir.mkdir();
	            
	        }
	    	//Open your local db as the input stream
	    	Log.v("1 Opening the assest folder db " , mContext.getAssets() + "/"+ DATABASE_NAME);
	    	InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DATABASE_NAME;
	 
	    	Log.i("2 Copy the db to the path " , outFileName);
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	    	//transfer bytes from the inputfile to the outputfile
	    	Log.i("Open the output db " , outFileName);
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	Log.i("Copied the database file" , outFileName);
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
	 
	/* Open the database */
	public void openDataBase() throws SQLException{
		
		String check_Path = DB_PATH  + DATABASE_NAME;
		if(mSqLiteDatabase!=null)
    	{        	
			mSqLiteDatabase.close();
			mSqLiteDatabase = null;
			mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);    		
    	}else{        
    		mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);        
    	}
	}
	
	public void closeDataBase(){
		
		if (mSqLiteDatabase != null) {
			mSqLiteDatabase.close();
			mSqLiteDatabase = null;
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	/* insert daily activity details */
	public long insertDailyActivitydetails(int baby_id, String activity_date, int diper_status, int milk_qty,int sleeping_hours, String date){
		ContentValues values = new ContentValues();
		values.put(DAILYACTIVITY_BABY_ID, baby_id);
		values.put(DAILYACTIVITY_TIME, activity_date);
		values.put(DAILYACTIVITY_MILK_QTY, milk_qty);
		values.put(DAILYACTIVITY_DIPER_STATUS, diper_status);
		values.put(DAILYACTIVITY_DATE, date);
		values.put(DAILYACTIVITY_SLEEPINGHOURS, sleeping_hours);
		return mSqLiteDatabase.insert(DAILYACTIVITY_TABLE, null, values);
	}
	
	
	/* insert notification details */
	public long insertNotificationdetails(int baby_id, String reminder_time, int notification_id, String vaccination_status){
		ContentValues notifcation_values = new ContentValues();
		notifcation_values.put(NOTIFICATIONS_BABY_ID, baby_id);
		notifcation_values.put(NOTIFICATIONS_TIME, reminder_time);
		notifcation_values.put(REMINDER_ID, notification_id);
		notifcation_values.put(NOTIFICATIONS_STATUS, vaccination_status);
		return mSqLiteDatabase.insert(NOTIFICATIONS_TABLE, null, notifcation_values);
	}
	
	/* insert baby growth details into database */
	public long insertMedicalHistoryDetails(int baby_id,String date_visit, String doctor_name, String purpose,String remarks, String note)
	{
		
		ContentValues values = new ContentValues();
		values.put(MEDICALHISTORY_BABY_ID, baby_id);
		values.put(MEDICALHISTORY_DATE, date_visit);
		values.put(MEDICALHISTORY_DOCTORNAME, doctor_name);
		values.put(MEDICALHISTORY_PURPOSE, purpose);
		values.put(MEDICALHISTORY_REMARKS, remarks);
		values.put(MEDICALHISTORY_NOTE, note);
		return mSqLiteDatabase.insert(MEDICALHISTORY_TABLE, null, values);
		
	}
	
	
	/* insert baby doctors details into database */
	public long insertDoctorsDetails(String doctor_name, String phonenumber, String emailid, String contactaddress, String workinghours)
	{
		
		ContentValues values = new ContentValues();
		values.put(DOCTORS_NAME, doctor_name);
		values.put(DOCTORS_PHONE, phonenumber);
		values.put(DOCTORS_EMAIL, emailid);
		values.put(DOCTORS_ADDRESS, contactaddress);
		values.put(DOCTORS_TIMINGS, workinghours);
		return mSqLiteDatabase.insert(DOCTORS_TABLE, null, values);
		
	}
	
	/* insert baby details into database */
	public long insertBabydetails(String name, String date_of_birth, String gender,byte[] baby_image, float height, float weight){
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_DOB, date_of_birth);
		values.put(KEY_GENDER, gender);
		values.put(KEY_IMAGE, baby_image);
		values.put(KEY_BABY_HEIGHT, height);
		values.put(KEY_BABY_WEIGHT, weight);
		return mSqLiteDatabase.insert(PROFILE_TABLE, null, values);
		
	}
	
	/* insert baby details into database */
	public long insertEmergencydetails(String contact_name, String contact_number){
		
		ContentValues values = new ContentValues();
		values.put(EMERGENCY_NAME, contact_name);
		values.put(EMERGENCY_NUMBER, contact_number);
		return mSqLiteDatabase.insert(EMERGENCY_TABLE, null, values);
		
	}
	

	/* update appointment details */
	public void updateBabydetails(String name, String date_of_birth, String gender,byte[] baby_image, int baby_id){
		
		ContentValues update_values = new ContentValues();
		update_values.put(KEY_NAME, name);
		update_values.put(KEY_DOB, date_of_birth);
		update_values.put(KEY_GENDER, gender);
		update_values.put(KEY_IMAGE, baby_image);
		String where = "_id='" + baby_id + "'";
		mSqLiteDatabase.update(PROFILE_TABLE, update_values, where, null);

		Log.i("update " ," "+"UPDATE baby SET name='"+ name +"'," + "date_of_birth='" + date_of_birth +"',"+ "photo=" + baby_image +","+ "gender='" + gender +"' WHERE _id='"+baby_id+"'");
		
	}
	
	
	/* update appointment details */
	public void updateBabyHeightandWeight(int baby_id, double baby_height, double baby_weight){
		
		ContentValues update_values = new ContentValues();
		update_values.put(KEY_BABY_HEIGHT, baby_height);
		update_values.put(KEY_BABY_WEIGHT, baby_weight);
		String where = "_id='" + baby_id + "'";
		mSqLiteDatabase.update(PROFILE_TABLE, update_values, where, null);

	}
	
	
	/* update doctor details */
	
	public void updateDoctorDetails(int doctor_id, String doctor_name, String email_id, String address, String timings, String phone_number){
		
		ContentValues update_values = new ContentValues();
		update_values.put(DOCTORS_NAME, doctor_name);
		update_values.put(DOCTORS_EMAIL, email_id);
		update_values.put(DOCTORS_PHONE, phone_number);
		update_values.put(DOCTORS_TIMINGS, timings);
		update_values.put(DOCTORS_ADDRESS, address);
		String where = "_id='" + doctor_id + "'";
		mSqLiteDatabase.update(DOCTORS_TABLE, update_values, where, null);

	}

	/* update appointment details */
	public void updateVaccinationStatus(int vaccination_id, String vaccination_status){
		
		ContentValues update_values = new ContentValues();
		update_values.put(VACCINATION_STATUS, vaccination_status);
		String where = "_id='" + vaccination_id + "'";
		mSqLiteDatabase.update(VACCINATION_TABLE, update_values, where, null);

	}
	
	/**
	 * Deleting the emergency contact from database.
	 * @param baby_id
	 */
	public void deleteContact(long contact_id){
		String where = "_id='" + contact_id + "'";
		mSqLiteDatabase.delete(EMERGENCY_TABLE, where, null);
		
	}
	
	/**
	 * Deleting the emergency contact from database.
	 * @param baby_id
	 */
	public void deleteAppointment(long appointment_id){
		String where = "baby_id='" + appointment_id + "'";
		mSqLiteDatabase.delete(APPOINTMENT_TABLE, where, null);	
	}
	/**
	 * Deleting the emergency contact from database.
	 * @param baby_id
	 */
	public void deleteBaby(long baby_id){
		String where = "_id='" + baby_id + "'";
		mSqLiteDatabase.delete(PROFILE_TABLE, where, null);	
	}
	public void deleteGrowth(long id){
		String where ="_id='"+id+"'";
		mSqLiteDatabase.delete(GROWTH_TABLE, where, null);	
	}
	public void delete(String query){
		mSqLiteDatabase.execSQL(query);
		
	}
	
	/**
	 * Deleting the vaccination contact from database.
	 * @param baby_id
	 */
	public void deleteVaccination(long vaccination_id){
		String where = "vaccination_id='" + vaccination_id + "'";
		mSqLiteDatabase.delete(VACCINATION_STATUS_TABLE, where, null);
		
	}
	
	/**
	 * Deleting the vaccination  from database.
	 * @param baby_id
	 */
	public void deleteBabyVaccinations(long vaccination_id){
		String where = "_id='" + vaccination_id + "'";
		mSqLiteDatabase.delete(VACCINATION_STATUS_TABLE, where, null);
		
	}
	
	
	/**
	 * Deleting the appointment from database.
	 * @param baby_id
	 */
	public void deleteBabyAppointments(long appointment_id){
		String where = "_id='" + appointment_id + "'";
		mSqLiteDatabase.delete(APPOINTMENT_TABLE, where, null);
		
	}
	
	/* insert baby growth details into database */
	public long insertBabyGrowthDetails(int baby_id, double baby_height,double baby_weight,double baby_circumference, double baby_age,String date_of_update){
		
		ContentValues values = new ContentValues();
		values.put(GROWTH_BABY_ID, baby_id);
		values.put(GROWTH_BABY_HEIGHT, baby_height);
		values.put(GROWTH_BABY_WEIGHT, baby_weight);
		values.put(GROWTH_BABY_CIRCUMFERENCE, baby_circumference);
		values.put(GROWTH_DATE_OF_UPDATE, date_of_update);		
		values.put(GROWTH_BABY_AGE, baby_age);
		return mSqLiteDatabase.insert(GROWTH_TABLE, null, values);
		
	}

	/* insert baby growth details into database */
	public long insertAppointmentDetails(int baby_id, String purpose, String note,String time_of_appointement,boolean is_reminder_set, String remark,String doctor_name, String reminder_time, int notification_id)
	{
		
		ContentValues values = new ContentValues();
		values.put(APPOINTMENT_BABY_ID, baby_id);
		values.put(APPOINTMENT_PURPOSE, purpose);
		values.put(APPOINTMENT_NOTE, note);
		values.put(APPOINTMENT_TIME, time_of_appointement);
		values.put(APPOINTMENT_IS_REMINDER_SET, is_reminder_set);
		values.put(APPOINTMENT_REMARK, remark);
		values.put(APPOINTMENT_DOCTORNAME, doctor_name);
		values.put(APPOINTMENT_REMINDER_TIME, reminder_time);
		values.put(APPOINTMENT_NOTIFICATIONID, notification_id);
		return mSqLiteDatabase.insert(APPOINTMENT_TABLE, null, values);
		
	}
	
	/* insert baby growth details into database */
	public long insertReminderDetails(String time_of_appointement, String note,boolean is_reminder_set, int notification_id, String reminder_time_intervel)
	{
		
		ContentValues values = new ContentValues();
		values.put(APPOINTMENT_NOTE, note);
		values.put(APPOINTMENT_TIME, time_of_appointement);
		values.put(APPOINTMENT_IS_REMINDER_SET, is_reminder_set);
		values.put(APPOINTMENT_NOTIFICATIONID, notification_id);
		values.put(APPOINTMENT_REMINDER_TIME, reminder_time_intervel);
		return mSqLiteDatabase.insert(APPOINTMENT_TABLE, null, values);
		
	}
	
	public long insertCompletedVaccination(int baby_id, int vaccination_id,String vaccination_name, String vaccination_description, String start_time, String vaccination_status){
		
		ContentValues vaccinationValues = new ContentValues();
		vaccinationValues.put(VACCINATION_STATUS_ID, vaccination_id);
		vaccinationValues.put(VACCINATION_BABY_ID, baby_id);
		vaccinationValues.put(VACCINATION_STATUS_NAME, vaccination_name);
		vaccinationValues.put(VACCINATION_STATUS_DESCRIPTION, vaccination_description);
		vaccinationValues.put(VACCINATION_STATUS_TIME, start_time);
		vaccinationValues.put(VACCINATION_STATUS_COMPLETE, vaccination_status);
		return mSqLiteDatabase.insert(VACCINATION_STATUS_TABLE, null, vaccinationValues);
	}
	
	public void updateReminderDetails(int appointment_id, String time_of_appointement, String note,boolean is_reminder_set, int notification_id, String reminder_time_intervel)
	{
		
		ContentValues update_values = new ContentValues();
		update_values.put(APPOINTMENT_NOTE, note);
		update_values.put(APPOINTMENT_TIME, time_of_appointement);
		update_values.put(APPOINTMENT_IS_REMINDER_SET, is_reminder_set);
		update_values.put(APPOINTMENT_NOTIFICATIONID, notification_id);
		update_values.put(APPOINTMENT_REMINDER_TIME, reminder_time_intervel);
		String where = "_id='" + appointment_id + "'";
		mSqLiteDatabase.update(APPOINTMENT_TABLE, update_values, where, null);		
	}
	
	/* insert medication details */
	public long insertMedicationDetails(int baby_id, String medication_time, String medication_type, String medicine_name, String dosage, String description){

		ContentValues medication_values = new ContentValues();
		medication_values.put(MEDICATION_BABY_ID, baby_id);
		medication_values.put(MEDICATION_TIME, medication_time);
		medication_values.put(MEDICATION_TYPE, medication_type);
		medication_values.put(MEDICATION_MEDICINE, medicine_name);
		medication_values.put(MEDICATION_DOSAGE, dosage);
		medication_values.put(MEDICATION_DESCRIPTION, description);

		return mSqLiteDatabase.insert(MEDICATION_TABLE, null, medication_values);
	}
	
	/* update medication details */
	public void updateMedicationDetails(int medication_id, String medication_time, String medication_type, String medicine_name, String dosage, String description){

		ContentValues medication_values = new ContentValues();
		medication_values.put(MEDICATION_TIME, medication_time);
		medication_values.put(MEDICATION_TYPE, medication_type);
		medication_values.put(MEDICATION_MEDICINE, medicine_name);
		medication_values.put(MEDICATION_DOSAGE, dosage);
		medication_values.put(MEDICATION_DESCRIPTION, description);
		String where = "_id='" + medication_id + "'";
		mSqLiteDatabase.update(MEDICATION_TABLE, medication_values, where, null);	
	}
	
	public Cursor select(String query){
		return mSqLiteDatabase.rawQuery(query, null);
	}
	
	
	/* get medicalhistory details from database */
	public ArrayList<MedicalHistoryModel> getDateAndDoctorname(int baby_id){
		ArrayList<MedicalHistoryModel> medicalhistorydetails = new ArrayList<MedicalHistoryModel>();
		MedicalHistoryModel mMedicalHistoryModel = null;
		Cursor mMedicalhistoryCursor = null;
		try 
		{
			mMedicalhistoryCursor = queryDataBase("select "+MEDICALHISTORY_DATE+","+MEDICALHISTORY_DOCTORNAME+" from "+MEDICALHISTORY_TABLE+" WHERE "+MEDICALHISTORY_BABY_ID+" = '" + baby_id + "'");
			Log.v("Db helper ", "select query for medical history1 "+mMedicalhistoryCursor);
			
			if (mMedicalhistoryCursor.getCount() > 0) {
				int iCtr = 0;
				mMedicalhistoryCursor.moveToPosition(iCtr);
				do 
				{
					mMedicalHistoryModel = new MedicalHistoryModel();
					mMedicalHistoryModel.setDateofvisit(mMedicalhistoryCursor.getString(mMedicalhistoryCursor.getColumnIndex(MEDICALHISTORY_DATE)));
					mMedicalHistoryModel.setDoctor_name(mMedicalhistoryCursor.getString(mMedicalhistoryCursor.getColumnIndex(MEDICALHISTORY_DOCTORNAME)));
					medicalhistorydetails.add(mMedicalHistoryModel);
					mMedicalHistoryModel = null;
					iCtr++;
				} while (mMedicalhistoryCursor.moveToNext());
			}
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();	
		}finally{
			mMedicalhistoryCursor.close();
		}
		
		return medicalhistorydetails;
	}
	
	
	
	
	
	/* get vaccination_details from database */
	public ArrayList<VaccinationModel> getVaccinationsByage(int baby_age, int baby_id){
		ArrayList<VaccinationModel> vaccination_details = new ArrayList<VaccinationModel>();
		VaccinationModel mVaccinationModel = null;

		Cursor mVaccinationCursor = null;
		
		try 
		{
			mVaccinationCursor = queryDataBase("select *  from "+VACCINATION_TABLE);
			Log.v("Db helper ", "select query for mVaccinationCursor "+mVaccinationCursor);
			
			if (mVaccinationCursor.getCount() > 0) 
			{
				
				int iCtr = 0, ageInmonths = baby_age +1;
				int vaccination_id;
				mVaccinationCursor.moveToPosition(iCtr);
				
				
				do 
				{
					
					mVaccinationModel = new VaccinationModel();
					
					int starting_time = mVaccinationCursor.getInt(mVaccinationCursor.getColumnIndex(VACCINATION_START_TIME));
					
					int endinging_time = mVaccinationCursor.getInt(mVaccinationCursor.getColumnIndex(VACCINATION_END_TIME));
					
					String vaccination_status = mVaccinationCursor.getString(mVaccinationCursor.getColumnIndex(VACCINATION_STATUS));
					
					vaccination_id = mVaccinationCursor.getInt(mVaccinationCursor.getColumnIndex(VACCINATION_ID));
					
					Log.v(getClass().getSimpleName(), " st time "+starting_time+" age "+baby_age);
					
					if (starting_time < ageInmonths && endinging_time < baby_age)
					{
						Log.v(getClass().getSimpleName(), "red  "+baby_age);
						mVaccinationModel.setBackground_resource(R.drawable.gl_red_bar);
					}else{
						Log.v(getClass().getSimpleName(), "blue  "+baby_age);
						mVaccinationModel.setBackground_resource(R.drawable.babytracker_row_vaccination);
					}
					
					mVaccinationModel.setVaccination_name(mVaccinationCursor.getString(mVaccinationCursor.getColumnIndex(VACCINATION_NAME)));
					mVaccinationModel.setVaccination_id(vaccination_id);
					mVaccinationModel.setVaccination_description(mVaccinationCursor.getString(mVaccinationCursor.getColumnIndex(VACCINATION_DESCRIPTION)));
					mVaccinationModel.setVaccination_endtime(mVaccinationCursor.getInt(mVaccinationCursor.getColumnIndex(VACCINATION_END_TIME)));
					mVaccinationModel.setVaccination_time(mVaccinationCursor.getString(mVaccinationCursor.getColumnIndex(VACCINATION_TIME)));
					mVaccinationModel.setVaccination_status(vaccination_status);
					
					if (vaccinationidExisted(vaccination_id, baby_id))
					{
						
						Log.v(getClass().getSimpleName(), " vaccination is already taken ");	
						
					}else
					{
						
						Log.v(getClass().getSimpleName(), " vaccination is not taken ");	
						vaccination_details.add(mVaccinationModel);
						
					}
					
					mVaccinationModel = null;
					iCtr++;
				
				} while (mVaccinationCursor.moveToNext());
			}
			
		}catch (Exception ex) {
			// TODO: handle exception
//			ex.printStackTrace();	
		}finally{
			mVaccinationCursor.close();
		}
		
		return vaccination_details;
	}
	
	
	public ArrayList<VaccinationModel> getVaccinationTimes(int baby_age){
		
		ArrayList<VaccinationModel> mVaccinationTimes = new ArrayList<VaccinationModel>();
		VaccinationModel mVModel = null;
		Cursor mTimings = null;
		
		try 
		{
			mTimings = queryDataBase("select DISTINCT starting_time from "+VACCINATION_TABLE+" where "+VACCINATION_START_TIME+" >= '"+baby_age+"'");
			if (mTimings.getCount() > 0) 
			{
				int Iposition = 0;
				int check_time = 0;

				mTimings.moveToPosition(Iposition);
				do 
				{
					mVModel = new VaccinationModel();
					
					int starting_time = mTimings.getInt(mTimings.getColumnIndex(VACCINATION_START_TIME));

					if ((check_time != starting_time)) 
					{
						mVModel.setVaccination_starttime(starting_time);
						check_time = starting_time;
					}
					
					mVaccinationTimes.add(mVModel);
					mVModel = null;
					Iposition++;
					
				} while (mTimings.moveToNext());
				
				
				
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		 return mVaccinationTimes;
	}
	/* checking appointment is existed in data base or not */
	public boolean vaccinationidExisted(int vaccination_status_id, int baby_id)
	{
		
		Cursor mCursor = queryDataBase("select * from "+VACCINATION_STATUS_TABLE+" WHERE "+VACCINATION_STATUS_ID+" = '" + vaccination_status_id + "'"+"AND "+VACCINATION_BABY_ID+" ='" + baby_id + "'");

		
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
	
	/* get vaccination_details from database */
	public ArrayList<VaccinationModel> getCompletedVaccinations(int baby_id){
		ArrayList<VaccinationModel> cvaccination_details = new ArrayList<VaccinationModel>();
		VaccinationModel mVaccinationModel = null;
		Cursor mCVaccinationCursor = null;
		try 
		{
			mCVaccinationCursor = queryDataBase("select *  from "+VACCINATION_STATUS_TABLE+" WHERE "+VACCINATION_BABY_ID+" ='" + baby_id + "'");
			Log.v("Db helper ", "select query for mVaccinationCursor "+mCVaccinationCursor);
			
			if (mCVaccinationCursor.getCount() > 0) 
			{
				int iCtr = 0, vaccination_id;
				
				mCVaccinationCursor.moveToPosition(iCtr);
				
				do 
				{
					
					mVaccinationModel = new VaccinationModel();
					
					mVaccinationModel.setVaccination_name(mCVaccinationCursor.getString(mCVaccinationCursor.getColumnIndex(VACCINATION_STATUS_NAME)));
					String vaccination_status = mCVaccinationCursor.getString(mCVaccinationCursor.getColumnIndex(VACCINATION_STATUS_COMPLETE));
					vaccination_id = mCVaccinationCursor.getInt(mCVaccinationCursor.getColumnIndex(VACCINATION_STATUS_ID));
 					mVaccinationModel.setVaccination_id(vaccination_id);
					mVaccinationModel.setVaccination_description(mCVaccinationCursor.getString(mCVaccinationCursor.getColumnIndex(VACCINATION_STATUS_DESCRIPTION)));
					mVaccinationModel.setVaccination_time(mCVaccinationCursor.getString(mCVaccinationCursor.getColumnIndex(VACCINATION_STATUS_TIME)));
					mVaccinationModel.setVaccination_status(vaccination_status);
					
					cvaccination_details.add(mVaccinationModel);
					mVaccinationModel = null;
					iCtr++;
				} while (mCVaccinationCursor.moveToNext());
			}
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();	
		}finally{
			mCVaccinationCursor.close();
		}
		
		return cvaccination_details;
	}
	
	public ArrayList<DailyActivityModel>   getDailyActivityDetails(int baby_id)
	{
		
		ArrayList<DailyActivityModel> mTotalDates = new ArrayList<DailyActivityModel>();
		Cursor mDatescheck = null;
		DailyActivityModel mDailyActivityModel =  null;
		
		try 
		{
			mDatescheck = queryDataBase("select * from "+DAILYACTIVITY_TABLE+" where "+DAILYACTIVITY_BABY_ID+" = "+baby_id);
			Log.v("Db helper "," milk cursor "+"select * from "+DAILYACTIVITY_TABLE+" where "+DAILYACTIVITY_BABY_ID+" = "+baby_id);
			
			if (mDatescheck.getCount() > 0) {
				
				int iCtr=0;
				mDatescheck.moveToPosition(iCtr);
				
				
				do
				{
					
					mDailyActivityModel = new DailyActivityModel();
					mDailyActivityModel.setDailyactivity_date(mDatescheck.getString(mDatescheck.getColumnIndex(DAILYACTIVITY_DATE)));
					mDailyActivityModel.setActivityDate(mDatescheck.getString(mDatescheck.getColumnIndex(DAILYACTIVITY_TIME)));
					mDailyActivityModel.setDiper_status_change(mDatescheck.getInt(mDatescheck.getColumnIndex(DAILYACTIVITY_DIPER_STATUS)));
					mDailyActivityModel.setMilk_quantity(mDatescheck.getInt(mDatescheck.getColumnIndex(DAILYACTIVITY_MILK_QTY)));
					mDailyActivityModel.setSleepingHours(mDatescheck.getInt(mDatescheck.getColumnIndex(DAILYACTIVITY_SLEEPINGHOURS)));
					mTotalDates.add(mDailyActivityModel);
					mDailyActivityModel = null;
					iCtr++;
					
				} while (mDatescheck.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			mDatescheck.close();
		}
		
		
		return mTotalDates;
	}

	
	/**
	 *  Based on the baby id getting the daily activities	
	 * @param baby_id
	 * @return array list of activities.
	 */
	public ArrayList<DailyActivityModel> getActivities(int baby_id, String date)
	{
		
		ArrayList<DailyActivityModel> mTotalActivities = new ArrayList<DailyActivityModel>();
		DailyActivityModel mDailyActivityModel = null;
		Cursor mMilkCursor = null;
		Cursor mMilkQuantityCursor = null;
		
		try 
		{
			mMilkCursor = queryDataBase("select SUM(quantity),SUM(activity),date, time,baby_id  from "+DAILYACTIVITY_TABLE+" where "+DAILYACTIVITY_DATE+" ='" + date + "'"+"AND "+DAILYACTIVITY_BABY_ID+" ='" + baby_id + "'");
			
			mMilkQuantityCursor = queryDataBase("select * from "+DAILYACTIVITY_TABLE+" where "+DAILYACTIVITY_MILK_QTY+" > 1 AND "+DAILYACTIVITY_DATE+" ='" + date + "'"+"AND "+DAILYACTIVITY_BABY_ID+" ='" + baby_id + "'");
			mDailyActivityModel = new DailyActivityModel();
			
			if (mMilkQuantityCursor.moveToFirst()) {
				mDailyActivityModel.setMilk_quantity(mMilkQuantityCursor.getCount());
				Log.v(getClass().getSimpleName(), " mMilkQuantityCursor.getCount() "+mMilkQuantityCursor.getCount());
			}
			
			if(mMilkCursor.moveToFirst()) {
				if (mMilkCursor.getInt(0) != 0 ) {
					mDailyActivityModel.setTotalMilkCount(mMilkCursor.getInt(0));
					mDailyActivityModel.setDiperstatus(mMilkCursor.getInt(1));
				    mDailyActivityModel.setActivityDate(mMilkCursor.getString(mMilkCursor.getColumnIndex(DAILYACTIVITY_TIME)));
				    mDailyActivityModel.setBaby_id(mMilkCursor.getInt(mMilkCursor.getColumnIndex(DAILYACTIVITY_BABY_ID)));
				    mDailyActivityModel.setDailyactivity_date(mMilkCursor.getString(mMilkCursor.getColumnIndex(DAILYACTIVITY_DATE)));
				}
			}
		
			mTotalActivities.add(mDailyActivityModel);
			mDailyActivityModel = null;
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			mMilkQuantityCursor.close();
			mMilkCursor.close();

		}
		return mTotalActivities;
	}

	
	public ArrayList<DailyActivityModel> getDailyActivityResult(String  date, int baby_id)
	{
		ArrayList<DailyActivityModel> mDailyActivityResult = new ArrayList<DailyActivityModel>();
		Cursor mDailyActivityCursor = null;
		DailyActivityModel mDailyActivityModel = null;
		
		try 
		{
			mDailyActivityCursor = queryDataBase("select * from "+DAILYACTIVITY_TABLE+" where "+DAILYACTIVITY_DATE+" ='" + date + "'"+"AND "+DAILYACTIVITY_BABY_ID+" ='" + baby_id + "'");
			
			if (mDailyActivityCursor.getCount() > 0) {
				int iCtr = 0;
				mDailyActivityCursor.moveToPosition(iCtr);
				
				do 
				{
					mDailyActivityModel = new DailyActivityModel();
					mDailyActivityModel.setDiper_status_change(mDailyActivityCursor.getInt(mDailyActivityCursor.getColumnIndex(DAILYACTIVITY_DIPER_STATUS)));
					mDailyActivityModel.setActivityDate(mDailyActivityCursor.getString(mDailyActivityCursor.getColumnIndex(DAILYACTIVITY_TIME)));
					mDailyActivityModel.setMilk_quantity(mDailyActivityCursor.getInt(mDailyActivityCursor.getColumnIndex(DAILYACTIVITY_MILK_QTY)));
					mDailyActivityModel.setSleepingHours(mDailyActivityCursor.getInt(mDailyActivityCursor.getColumnIndex(DAILYACTIVITY_SLEEPINGHOURS)));
					mDailyActivityResult.add(mDailyActivityModel);
					mDailyActivityModel = null;
					iCtr++;
//					Log.v(getClass().getSimpleName(), " status "+iCtr);
				} while (mDailyActivityCursor.moveToNext());
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mDailyActivityCursor.close();
		}
		return mDailyActivityResult;
	}

	
	
	/* get medicalhistory details from database */
	public ArrayList<MedicalHistoryModel> getRemarksAndPurposefromDb(String baby_id)
	{
		ArrayList<MedicalHistoryModel> addItems = new ArrayList<MedicalHistoryModel>();
		MedicalHistoryModel mMedicalHistoryModel = null;
		Cursor mRemarksCursor = null;
		try 
		{
			mRemarksCursor = queryDataBase("select "+MEDICALHISTORY_PURPOSE+","+MEDICALHISTORY_REMARKS+","+MEDICALHISTORY_NOTE+" from "+MEDICALHISTORY_TABLE+" WHERE "+MEDICALHISTORY_DOCTORNAME+" = '" + baby_id + "'");
			Log.v("Db helper ", "select query "+mRemarksCursor);
			
			if (mRemarksCursor.getCount() > 0) {
				int iCtr = 0;
				mRemarksCursor.moveToPosition(iCtr);
				do 
				{
					mMedicalHistoryModel = new MedicalHistoryModel();
					mMedicalHistoryModel.setPurpose(mRemarksCursor.getString(mRemarksCursor.getColumnIndex(MEDICALHISTORY_PURPOSE)));
					mMedicalHistoryModel.setRemarks(mRemarksCursor.getString(mRemarksCursor.getColumnIndex(MEDICALHISTORY_REMARKS)));
					mMedicalHistoryModel.setNote(mRemarksCursor.getString(mRemarksCursor.getColumnIndex(MEDICALHISTORY_NOTE)));
					addItems.add(mMedicalHistoryModel);
					mMedicalHistoryModel = null;
					iCtr++;
				} while (mRemarksCursor.moveToNext());
			}
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();	
		}finally{
			mRemarksCursor.close();
		}
		
		return addItems;
	}
	 
	
	/* update appointment details */
	public void updateApppintmentdetails(String date_appintment, String doctor_name,String purpose, String remarks,String note, String appointment_date_time, int appointmentid, int notification_id){
		
		ContentValues update_values = new ContentValues();
		update_values.put(APPOINTMENT_TIME, date_appintment);
		update_values.put(APPOINTMENT_DOCTORNAME, doctor_name);
		update_values.put(APPOINTMENT_PURPOSE, purpose);
		update_values.put(APPOINTMENT_REMARK, remarks);
		update_values.put(APPOINTMENT_NOTE, note);
		update_values.put(APPOINTMENT_REMINDER_TIME, appointment_date_time);
		update_values.put(APPOINTMENT_NOTIFICATIONID, notification_id);
		mSqLiteDatabase.execSQL("UPDATE appointment SET time_of_appointment='"+ date_appintment +"'," + "purpose='" + purpose +"',"+ "reminder_time_interval='" + appointment_date_time +"',"+ "remark='" + remarks +"'," + "note='" +note+ "'," +"doctor_name='" + doctor_name
															 + "' WHERE _id='"+appointmentid+"'");

		
		Log.i("update " ," "+"UPDATE appointment SET time_of_appointment='"+ date_appintment +"'," + "purpose='" + purpose +"',"+ "remark='" + remarks +"'," + "note='" +note+ "'," +"doctor_name='" + doctor_name
															 + "' WHERE _id='"+appointmentid+"'");
		
	}

	
	public ArrayList<GraphModel>  getDetailsforGraph(int baby_id)
	{
		
		ArrayList<GraphModel> mWeight_details = new ArrayList<GraphModel>();
		
		GraphModel mGModel = null;
		Cursor mGraphcursor = null;
		
		try 
		{
		
//			mGraphcursor = queryDataBase("select "+GROWTH_BABY_WEIGHT+","+GROWTH_BABY_AGE+","+GROWTH_BABY_HEIGHT+" from "+GROWTH_TABLE+" where "+GROWTH_BABY_ID+" = '"+baby_id+"'");
			
			mGraphcursor = queryDataBase("select "+GROWTH_BABY_WEIGHT+","+GROWTH_BABY_AGE+","+GROWTH_BABY_HEIGHT+" from "+GROWTH_TABLE+" where "+GROWTH_BABY_ID+" = '"+baby_id+"'"+" AND "+GROWTH_DATE_OF_UPDATE+" = "+"("+"select MAX(date_of_update) from"+GROWTH_TABLE+")");

			Log.v(getClass().getSimpleName(), " graph "+"select "+GROWTH_BABY_WEIGHT+","+GROWTH_BABY_AGE+","+GROWTH_BABY_HEIGHT+" from "+GROWTH_TABLE+" where "+GROWTH_BABY_ID+" = '"+baby_id+"'"+" AND "+GROWTH_DATE_OF_UPDATE+" = "+"("+"select MAX(date_of_update) from "+GROWTH_TABLE+")");
			if (mGraphcursor.getCount() > 0) 
			{
			
				int iGr = 0;
				mGraphcursor.moveToPosition(iGr);
				
				do 
				{
					
					mGModel = new GraphModel();
					mGModel.setBaby_age(mGraphcursor.getDouble(mGraphcursor.getColumnIndex(GROWTH_BABY_AGE)));
					mGModel.setWeight(mGraphcursor.getDouble(mGraphcursor.getColumnIndex(GROWTH_BABY_WEIGHT)));
					mGModel.setHeight(mGraphcursor.getDouble(mGraphcursor.getColumnIndex(GROWTH_BABY_HEIGHT)));
					mWeight_details.add(mGModel);
					mGModel = null;
					
				} while (mGraphcursor.moveToNext());
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			mGraphcursor.close();
		}
		
		return mWeight_details;
	}
	
	public Cursor queryDataBase(String query)
	{
		
		BabyTrackerDataBaseHelper mBabyTrackerDataBaseHelper = new BabyTrackerDataBaseHelper(mContext);
		
		mSqLiteDatabase = mBabyTrackerDataBaseHelper.getReadableDatabase();
		
		Log.v("query ", query);
		Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
		return cursor;
	}
	
	
	@Override
    public synchronized void close() {
        if(mSqLiteDatabase != null){
        	mSqLiteDatabase.close();
        super.close();
        }   
    }

}

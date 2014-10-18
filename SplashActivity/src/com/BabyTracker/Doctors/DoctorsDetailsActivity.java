package com.BabyTracker.Doctors;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class DoctorsDetailsActivity extends Activity implements OnClickListener {

	EditText mDoctor_name_edtTxt, mPhone_edtTxt, mEmailid_edtTxt,
			mContactAddress_edtTxt;
	TextView mFrom_Txt, mTo_Txt;
	Button mSubmit_Btn, mClear_Btn, mSave_Btn, mCancel_Btn;

	private TextView mDoctors_details_title;
	private int mHour, mMinute, doctor_id;

	static final int TIME_DIALOG_ID = 1, TIME_DIALOG_TO = 2;
	private String mFromtime_Str = "", mTotime_Str;
	private BabyTrackerDataBaseHelper mDataBaseHelper;

	private String mTitle = "BabyTracker";

	private Intent receiverIntent;
	
	private RelativeLayout mSave_relative;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctors);
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
		receiverIntent = getIntent();
		
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		mDataBaseHelper = new BabyTrackerDataBaseHelper(
				DoctorsDetailsActivity.this);

		try {
			mDataBaseHelper.createDataBase();
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}

		if (null!=receiverIntent.getAction()&&receiverIntent.getAction().equalsIgnoreCase("edit")) 
		{
			doctor_id = receiverIntent.getExtras().getInt("doctor_id");
			getDoctordetails(doctor_id);
		}
		
	}

	private void initializeUI() {

		mDoctor_name_edtTxt = (EditText) findViewById(R.id.doctors_details_ETXT_doctorname);
		mPhone_edtTxt = (EditText) findViewById(R.id.doctors_details_ETXT_phone);
		mEmailid_edtTxt = (EditText) findViewById(R.id.doctors_details_ETXT_email);
		mContactAddress_edtTxt = (EditText) findViewById(R.id.doctors_details_ETXT_contactAddress);

		mFrom_Txt = (TextView) findViewById(R.id.doctors_txt_from);
		mFrom_Txt.setOnClickListener(this);

		mTo_Txt = (TextView) findViewById(R.id.doctors_txt_to);
		mTo_Txt.setOnClickListener(this);

		mSubmit_Btn = (Button) findViewById(R.id.doctors_details_BTN_add);
		mSubmit_Btn.setOnClickListener(this);
		mClear_Btn = (Button) findViewById(R.id.doctors_details_BTN_clear);
		mClear_Btn.setOnClickListener(this);

		mDoctors_details_title = (TextView) findViewById(R.id.simple_top_bar_Txt_title);
		mDoctors_details_title.setText("Doctors");
		
		mSave_relative = (RelativeLayout)findViewById(R.id.doctors_details_LIN_babydetails4);
		
		mSave_Btn = (Button)findViewById(R.id.doctors_details_BTN_save);
		mSave_Btn.setOnClickListener(this);
		
		mCancel_Btn = (Button)findViewById(R.id.doctors_details_BTN_cancel);
		mCancel_Btn.setOnClickListener(this);

	}

	
	public void getDoctordetails(int doctor_id)
	{
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.DOCTORS_TABLE+" where "+BabyTrackerDataBaseHelper.DOCTORS_ID+" = "+doctor_id);
		
		Log.v(getClass().getSimpleName(), "doctor query "+query);
		
		Cursor mCursor = mDataBaseHelper.select(query);
	
		if (mCursor.moveToFirst()) 
		{
			mContactAddress_edtTxt.setText(mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_ADDRESS)));
			mDoctor_name_edtTxt.setText(mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_NAME)));
			mPhone_edtTxt.setText(mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_PHONE)));
			mEmailid_edtTxt.setText(mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_EMAIL)));
			
			String timings = mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.DOCTORS_TIMINGS));
			
			String[] times = timings.split(" to ");
			mFrom_Txt.setText(times[0]);
			mTo_Txt.setText(times[1]);
			
			mFromtime_Str = times[0];
			mTotime_Str = times[1];
			
			mSave_relative.setVisibility(View.VISIBLE);
		}
		Log.v(getClass().getSimpleName(), "doctors cursor "+query);
	}
	/**
	 * selecting date and time.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case TIME_DIALOG_TO:

			return new TimePickerDialog(this, mTimeSetListenerTo, mHour,
					mMinute, false);

		case TIME_DIALOG_ID:

			return new TimePickerDialog(this, mTimeSetListenerFrom, mHour,
					mMinute, false);

		}
		return null;

	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListenerFrom = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			mHour = hourOfDay;
			mMinute = minute;

			if (hourOfDay > 12) {

				mFromtime_Str = String.valueOf(pad(hourOfDay - 12)) + ":"
						+ (String.valueOf(pad(minute)) + " PM");

				mFrom_Txt.setText(String.valueOf(pad(hourOfDay - 12)) + ":"
						+ (String.valueOf(pad(minute)) + " PM"));
			}
			if (hourOfDay == 12) {
				mFromtime_Str = "12" + ":"
						+ (String.valueOf(pad(minute)) + " PM");
				mFrom_Txt.setText("12" + ":"
						+ (String.valueOf(pad(minute)) + " PM"));
			}
			if (hourOfDay < 12) {
				mFromtime_Str = String.valueOf(pad(hourOfDay)) + ":"
						+ (String.valueOf(pad(minute)) + " AM");

				mFrom_Txt.setText(String.valueOf(pad(hourOfDay)) + ":"
						+ (String.valueOf(pad(minute)) + " AM"));
			}
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListenerTo = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			mHour = hourOfDay;
			mMinute = minute;

			if (hourOfDay > 12) {

				mTotime_Str = String.valueOf(pad(hourOfDay - 12)) + ":"
						+ (String.valueOf(pad(minute)) + " PM");

				mTo_Txt.setText(String.valueOf(pad(hourOfDay - 12)) + ":"
						+ (String.valueOf(pad(minute)) + " PM"));
			}
			if (hourOfDay == 12) {
				mTotime_Str = "12" + ":"
						+ (String.valueOf(pad(minute)) + " PM");
				mTo_Txt.setText("12" + ":"
						+ (String.valueOf(pad(minute)) + " PM"));
			}
			if (hourOfDay < 12) {
				mTotime_Str = String.valueOf(pad(hourOfDay)) + ":"
						+ (String.valueOf(pad(minute)) + " AM");

				mTo_Txt.setText(String.valueOf(pad(hourOfDay)) + ":"
						+ (String.valueOf(pad(minute)) + " AM"));
			}
		}
	};

	private static String pad(int c) {

		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		String validationStatus = "";
		switch (v.getId()) {

		case R.id.doctors_txt_from:
			showDialog(TIME_DIALOG_ID);
			break;

		case R.id.doctors_txt_to:
			showDialog(TIME_DIALOG_TO);
			break;

		case R.id.doctors_details_BTN_add:

			long insertResult = 0;

			validationStatus = inputValidation(mDoctor_name_edtTxt.getText().toString(), mPhone_edtTxt.getText().toString()
						,mContactAddress_edtTxt.getText().toString(), mEmailid_edtTxt.getText().toString());

			if (validationStatus.trim().equals("")) {

				insertResult = mDataBaseHelper.insertDoctorsDetails(mDoctor_name_edtTxt.getText().toString(), mPhone_edtTxt.getText().toString(), 
						mEmailid_edtTxt.getText().toString(), mContactAddress_edtTxt.getText().toString(), mFromtime_Str +" to "+ mTotime_Str);
			} else {

				if (!validationStatus.trim().equals("")) {

					alertDialogWithMessage(mTitle, validationStatus);

					if (mDoctor_name_edtTxt.getText().toString().equals(""))
						mDoctor_name_edtTxt.setHintTextColor(Color.RED);

					if (mPhone_edtTxt.getText().toString().equals(""))
						mPhone_edtTxt.setHintTextColor(Color.RED);

					if (mContactAddress_edtTxt.getText().toString().equals(""))
						mContactAddress_edtTxt.setHintTextColor(Color.RED);

				}
			}

			if (insertResult > 0) {

				Toast.makeText(DoctorsDetailsActivity.this,"DoctorsDetails stored successfully", Toast.LENGTH_SHORT).show();
				Intent data = new Intent();
				setResult(RESULT_OK, data);
				finish();
			} else {
				Toast.makeText(DoctorsDetailsActivity.this,"DoctorsDetails insertion failed.", Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.doctors_details_BTN_clear:
			
			mContactAddress_edtTxt.setText("");
			mDoctor_name_edtTxt.setText("");
			mPhone_edtTxt.setText("");
			mEmailid_edtTxt.setText("");
			mFrom_Txt.setText("from");
			mTo_Txt.setText("to");
			mDoctor_name_edtTxt.setHintTextColor(Color.RED);
			mPhone_edtTxt.setHintTextColor(Color.RED);
			mContactAddress_edtTxt.setHintTextColor(Color.RED);
			
			break;
			
		case R.id.doctors_details_BTN_save:
			
			String doctorName_str = mDoctor_name_edtTxt.getText().toString();
			String doctorEmail_str = mEmailid_edtTxt.getText().toString();
			String doctorAddress_str = mContactAddress_edtTxt.getText().toString();
			String doctorPhone_str = mPhone_edtTxt.getText().toString();
			String doctorTimes_str = mFromtime_Str +" to "+ mTotime_Str;

			validationStatus = inputValidation(doctorName_str, doctorPhone_str,doctorAddress_str, doctorEmail_str);

		if (validationStatus.trim().equals("")) {

			mDataBaseHelper.updateDoctorDetails(doctor_id, doctorName_str, doctorEmail_str, doctorAddress_str, mFromtime_Str +" to "+ mTotime_Str, doctorPhone_str);
		
			Intent data = new Intent();
			data.putExtra("doctor_id", doctor_id);
			data.putExtra("name",doctorName_str);
			data.putExtra("phone",doctorPhone_str);
			data.putExtra("email",doctorEmail_str);
			data.putExtra("address",doctorAddress_str);
			data.putExtra("timings",doctorTimes_str);
			data.putExtra("_id", doctor_id);
			setResult(RESULT_OK, data);
			finish();
			
		} else {

			if (!validationStatus.trim().equals("")) {

				alertDialogWithMessage(mTitle, validationStatus);

				if (doctorName_str.equals(""))
					mDoctor_name_edtTxt.setHintTextColor(Color.RED);

				if (doctorPhone_str.equals(""))
					mPhone_edtTxt.setHintTextColor(Color.RED);

				if (doctorAddress_str.equals(""))
					mContactAddress_edtTxt.setHintTextColor(Color.RED);

			}
		}
			break;
			
			
		case R.id.doctors_details_BTN_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg) {

		new BabyTrackerAlert(DoctorsDetailsActivity.this, title, msg)
				.setPositiveButton("Ok",
						new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// finish();
							}
						}).create().show();
	}

	/* validating the user input data */
	private String inputValidation(String doctor_name, String phonenumber,String contactaddress, String email_id) 
	{
		if (validateEmail(email_id)) 
		{
		
			if (!doctor_name.trim().equals("")) 
			{
				if (!phonenumber.trim().equals("") && !(phonenumber.length() <= 10)) 
				{
					if (!contactaddress.trim().equals("")) 
					{
						return "";
					} else 
					{
						return "Please enter valid address";
					}
	
				} else 
				{
					return "Please enter valid phone number";
				}
	
			} else 
			{
				return "Please enter valid doctor name";
			}
		}else {
			
			return "Please enter valid email address";
		}
	}
	
	/**
	 * Validating the email address 
	 * @param userEmailId
	 * @return true ----> valid 
	 * @return false ---> invalid
	 */
	private boolean validateEmail(String userEmailId)
	{
		
		Pattern patternEmail = null;
		Matcher matcher = null;
		String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		
		patternEmail = Pattern.compile(EMAIL_PATTERN);
			 
		matcher = patternEmail.matcher(userEmailId);
		return matcher.matches();

	}

}

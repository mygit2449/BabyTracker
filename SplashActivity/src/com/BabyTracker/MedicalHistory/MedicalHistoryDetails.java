package com.BabyTracker.MedicalHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class MedicalHistoryDetails extends Activity implements OnClickListener{

	private static String LOG_TAG = MedicalHistoryDetails.class.getSimpleName();
	
	EditText mDoctor_name_edtTxt, mRemarks_edtTxt, mPurpose_edtTxt, mNote_edtTxt;
	TextView mDateofVisit_Txt;
	Button mSubmit_Btn, mClear_Btn;
	
	private final int DATE_DIALOG_ID = 0;
	private int mDay;
	private int mMonth;
	private int mYear;
	
	private String mTitle = "BabyTracker";

	private final String[] MONTH_NAME = {"Jan", "Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};

	private String mDateofVisit_Str = "";
	private BabyTrackerDataBaseHelper mDataBaseHelper;
	
	private Intent receiverIntent;
	
	private TextView mBabygrowth_details_title;
	
	private int baby_id;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medicalhistory_details);
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
		mDataBaseHelper = new BabyTrackerDataBaseHelper(MedicalHistoryDetails.this);

		receiverIntent = getIntent();
		
		if (receiverIntent.getAction().equals("fromList")) {
			baby_id = receiverIntent.getExtras().getInt("baby_id");
		}
		
		Calendar calender = Calendar.getInstance();
		mDay = calender.get(Calendar.DAY_OF_MONTH);
		mMonth = calender.get(Calendar.MONTH);
		mYear = calender.get(Calendar.YEAR);
	}
	
	private void initializeUI(){
		
		mDoctor_name_edtTxt = (EditText)findViewById(R.id.medicalhistory_details_ETXT_doctorname);
		mRemarks_edtTxt = (EditText)findViewById(R.id.medicalhistory_details_ETXT_remarks);
		mPurpose_edtTxt = (EditText)findViewById(R.id.medicalhistory_details_ETXT_purpose);
		mNote_edtTxt = (EditText)findViewById(R.id.medicalhistory_details_ETXT_note);
		
		mDateofVisit_Txt = (TextView)findViewById(R.id.medicalhistory_details_TXT_dateofvisit_date);
		mDateofVisit_Txt.setOnClickListener(this);
		mSubmit_Btn = (Button)findViewById(R.id.medicalhistory_details_BTN_add);
		mSubmit_Btn.setOnClickListener(this);
		mClear_Btn = (Button)findViewById(R.id.medicalhistory_details_BTN_clear);
		mClear_Btn.setOnClickListener(this);
		
		mBabygrowth_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Medical History");
	}

	@Override
	protected Dialog onCreateDialog(int id){
		
		switch (id) 
		{
		
		case DATE_DIALOG_ID:
			
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			
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
	
	private void updateDisplay(){
		
		mDateofVisit_Str = new StringBuffer().append(mMonth + 1).append("/").append(mDay).append("/").append(mYear).toString();
		mDateofVisit_Txt.setText( new StringBuilder().append(MONTH_NAME[mMonth]).append(" ")
				.append(mDay).append(",")
				.append(mYear));
		Log.v(LOG_TAG, "date of birth "+mDateofVisit_Str);
	}
	
	
	/* validating the user input data */
	private String inputValidation(Date dateofvisit, String dateofvisit_str, String doctor_name, String purpose)
	{
		Date currentDate = new Date();

		if(dateofvisit != null){
		
			if (dateofvisit.after(currentDate)) 
			{
				if (!dateofvisit_str.trim().equals("")) 
				{
					if (!doctor_name.trim().equals(""))
					{
						if (!purpose.trim().equals(""))
						{
							return "";
						}else {
							return "Please enter purspose";
						}		
					}else{
						return "Please enter doctor name";
					}
				}else{
					return "Please select date of visit";
				}
			}else{
				return "Please select valid date";
			}
		}else{
			return "Please select valid date";
		}
	}
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SimpleDateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyyy");

		switch (v.getId()) {
		
		case R.id.medicalhistory_details_BTN_add:

			long mInsertResult = 0;
			mDataBaseHelper.getWritableDatabase();
			
			try
			{
				mDataBaseHelper.createDataBase();
			}catch (Exception ex) {
				// TODO: handle exception
				ex.printStackTrace();
			}
			
			Date mDateofvisit = null;
			
			
			try {
				mDateofvisit = mDateFormat.parse(mDateofVisit_Str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String validationStatus = inputValidation(mDateofvisit,mDateofVisit_Str, mDoctor_name_edtTxt.getText().toString(), mPurpose_edtTxt.getText().toString());
			
			if (validationStatus.equalsIgnoreCase("")) 
			{
				String doctor_name = mDoctor_name_edtTxt.getText().toString();
				String purpose_str = mPurpose_edtTxt.getText().toString();
				String remarks_str = mRemarks_edtTxt.getText().toString();
				String note_str = mNote_edtTxt.getText().toString();
				mInsertResult = mDataBaseHelper.insertMedicalHistoryDetails(baby_id,mDateofVisit_Str, doctor_name, purpose_str, remarks_str, note_str);
			}else {
				if (!validationStatus.trim().equalsIgnoreCase("")) {
					alertDialogWithMessage(mTitle, validationStatus);
					
					if (mDoctor_name_edtTxt.getText().toString().equals("")) 
						mDoctor_name_edtTxt.setHintTextColor(Color.RED);
					
					if (mDateofVisit_Txt.getText().toString().equals("")) 
						mDateofVisit_Txt.setHintTextColor(Color.RED);
					
					if(mPurpose_edtTxt.getText().toString().equals(""))
						mPurpose_edtTxt.setHintTextColor(Color.RED);
				}
			}
			
			if (mInsertResult > 0) {
				Toast.makeText(MedicalHistoryDetails.this, "MedicalHistoryDetails  Inserted Successfully", Toast.LENGTH_SHORT).show();
				
				Log.v(LOG_TAG, " appointment id "+baby_id);
				Intent data = new Intent();
				data.putExtra("appointment_id", baby_id);
				setResult(RESULT_OK, data);
				finish();
			}else{
				Toast.makeText(MedicalHistoryDetails.this, "MedicalHistoryDetails Insertion Failed", Toast.LENGTH_SHORT).show();
			}
			
			break;

		case R.id.medicalhistory_details_BTN_clear:
			
			mDoctor_name_edtTxt.setText("");
			mPurpose_edtTxt.setText("");
			mRemarks_edtTxt.setText("");
			mNote_edtTxt.setText("");
			mDateofVisit_Txt.setText("Dateofvisit*");
			mDoctor_name_edtTxt.setHintTextColor(Color.RED);
			mDateofVisit_Txt.setHintTextColor(Color.RED);
			mPurpose_edtTxt.setHintTextColor(Color.RED);
			
			break;
			
		case R.id.medicalhistory_details_TXT_dateofvisit_date:
			showDialog(DATE_DIALOG_ID);
			break;
		}
	}
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(MedicalHistoryDetails.this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//finish();
			}
		})
		.create().show();
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

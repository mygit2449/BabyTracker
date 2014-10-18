package com.BabyTracker.Medication;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class MedicationActivity extends Activity implements OnClickListener{

	private EditText mMedicationType_edtTxt, mMedicineName_edtTxt, mDosage_edtTxt, mProblemDescription_edtTxt;
	private TextView mMedicationTime_Txt;
	private Button mSubmit_Btn, mClear_Btn, mAddMedicine_btn, mSave_Btn, mCancel_Btn;

	private TextView mMedication_title;
	private ArrayList<EditText> mAdd_Medicines;
	private int layout_id = 0;
	private LinearLayout mAdd_Medicine_Linear;
	static final int TIME_DIALOG_ID = 1;
	ArrayList<Integer> ids_check;
	private int mHour;
    private int mMinute;
    
    private SharedPreferences mSharedPreferences;
	
    private String mTitle = "Baby Tracker";
	private BabyTrackerDataBaseHelper mDataBaseHelper;

    private int baby_id, medication_id, time_duration = 0,duration = 1, et_pos = -1;
	private List<Boolean> mExistMedicines;

    
    private String[] duration_time = {"Day","Week", "Month", "Year"};
    
	private RelativeLayout mSave_relative;
	private Intent receiverIntent;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medication);
		initializeUI();
		
		mSharedPreferences = getSharedPreferences("BabyTracker", MODE_WORLD_READABLE);
		baby_id = mSharedPreferences.getInt("baby_id", 0);

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
		
		Log.v(getClass().getSimpleName(), " baby id "+baby_id);
		
		final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        mDataBaseHelper = new BabyTrackerDataBaseHelper(MedicationActivity.this);
		
        ids_check = new ArrayList<Integer>();
		try
		{
			mDataBaseHelper.createDataBase();
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		
		receiverIntent = getIntent();
		
		if (receiverIntent.getAction().equalsIgnoreCase("edit")) {
			medication_id = receiverIntent.getExtras().getInt("medication_id");
			getMedicationDetails(medication_id);
		}
		
	}
	
	
	private void initializeUI(){
		
		mMedicationType_edtTxt = (EditText)findViewById(R.id.medication_details_ETXT_doctorname);
		mMedicineName_edtTxt = (EditText)findViewById(R.id.medication_details_ETXT_medicineName);
		mDosage_edtTxt = (EditText)findViewById(R.id.medication_details_ETXT_dosage);
		mProblemDescription_edtTxt = (EditText)findViewById(R.id.medication_details_ETXT_purpose);
		mAdd_Medicines = new ArrayList<EditText>();
		mAdd_Medicines.add(mMedicineName_edtTxt);
		
		mExistMedicines = new ArrayList<Boolean>();
		mExistMedicines.add(false);
		
		mMedicationTime_Txt = (TextView)findViewById(R.id.medication_details_TXT_dateofvisit_date);
		mMedicationTime_Txt.setOnClickListener(this);
		mSubmit_Btn = (Button)findViewById(R.id.medication_details_BTN_add);
		mSubmit_Btn.setOnClickListener(this);
		mClear_Btn = (Button)findViewById(R.id.medication_details_BTN_clear);
		mClear_Btn.setOnClickListener(this);
		
		mAdd_Medicine_Linear = (LinearLayout)findViewById(R.id.medication_details_LL_add);
		mAddMedicine_btn = (Button)findViewById(R.id.medication_add_medicine);
		mAddMedicine_btn.setOnClickListener(this);
		
		mMedication_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mMedication_title.setText("Medication");
		
		mSave_relative = (RelativeLayout)findViewById(R.id.medication_details_LIN_babydetails4);
		
		mSave_Btn = (Button)findViewById(R.id.medication_details_BTN_save);
		mSave_Btn.setOnClickListener(this);
		
		mCancel_Btn = (Button)findViewById(R.id.medication_details_BTN_cancel);
		mCancel_Btn.setOnClickListener(this);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.medication_add_medicine:

				addMedicineLayout(1, "");
			
			break;

		case R.id.medication_details_TXT_dateofvisit_date:
		
			Context mContext = MedicationActivity.this;
			
			AlertDialog.Builder dialogBuilder;
			AlertDialog alertDailog;
			
			View medication_duration_dailogView = getLayoutInflater().inflate(R.layout.medication_duration_dialog, null);
			
			Button duration_increment = (Button)medication_duration_dailogView.findViewById(R.id.medication_btn_duration_increment);
			Button duration_decrement = (Button)medication_duration_dailogView.findViewById(R.id.medication_btn_duration_decrement);
			Button time_increment = (Button)medication_duration_dailogView.findViewById(R.id.medication_btn_time_increment);
			Button time_decrement = (Button)medication_duration_dailogView.findViewById(R.id.medication_btn_time_decrement);
			
			
			final TextView duration_txt = (TextView)medication_duration_dailogView.findViewById(R.id.medication_txt_duration);
			final TextView time_txt = (TextView)medication_duration_dailogView.findViewById(R.id.medication_txt_day);
			duration_txt.setText(""+duration);
			time_txt.setText("Day");
			duration_increment.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (duration < 6) {
						duration = duration + 1;
						duration_txt.setText(""+duration);
					} 
				}
			});
			
			duration_decrement.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (duration != 0) {
						duration = duration - 1;
						duration_txt.setText(""+duration);
					} 
				}
			});
			
			time_increment.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if (time_duration < duration_time.length - 1) {
						time_duration = time_duration + 1;
						time_txt.setText(""+duration_time[time_duration]);
					}
				}
			});
			
			time_decrement.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (time_duration != 0) {
						time_duration = time_duration - 1;
						time_txt.setText(""+duration_time[time_duration]);
					}
				}
			});
			
			
			dialogBuilder =new AlertDialog.Builder(mContext);
			dialogBuilder.setView(medication_duration_dailogView);
			
			dialogBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					String time_str  = "";
					
					time_str = (duration == 1) ? time_txt.getText().toString() : time_txt.getText().toString() +"s";
				
					mMedicationTime_Txt.setText(duration_txt.getText()+" "+ time_str);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			alertDailog = dialogBuilder.create();
			alertDailog.show();
			
			break;
			
		case R.id.medication_details_BTN_add:
			
			Log.v(getClass().getSimpleName(), " on save add button click ");
			long mInsertResult = 0;
			@SuppressWarnings("unused")
			Date medicationDate = null;
			String medicine_name = "";
			String name = "";
			
			String validationStatus = inputValidation(mMedicationTime_Txt.getText().toString(), 
					                  mMedicationType_edtTxt.getText().toString(), mMedicineName_edtTxt.getText().toString(), 
					                  mDosage_edtTxt.getText().toString(),
					                  mProblemDescription_edtTxt.getText().toString());
			
			Calendar mMedicationCalender = Calendar.getInstance();
			mMedicationCalender.set(Calendar.HOUR_OF_DAY, mHour);
			mMedicationCalender.set(Calendar.MINUTE, mMinute);
			medicationDate = mMedicationCalender.getTime();
			
			if (validationStatus.equalsIgnoreCase("")) 
			{
				String medication_type = mMedicationType_edtTxt.getText().toString();
				String medicine_dosage = mDosage_edtTxt.getText().toString();
				String problemdescription = mProblemDescription_edtTxt.getText().toString();
				
				
				for (int iCtr = 0; iCtr < mAdd_Medicines.size(); iCtr++) {
					
					if (mExistMedicines.get(iCtr))
					{
						
						Log.v(getClass().getSimpleName(), "This medicine is deleted");
						
					}else
					{
						name =  mAdd_Medicines.get(iCtr).getText().toString();
						medicine_name += name + "," ;
						
						Log.v(getClass().getSimpleName(), "111 "+mExistMedicines.get(iCtr)+"mAname "+mAdd_Medicines.get(iCtr).getText().toString());
					}
					
				}
				
				mInsertResult = mDataBaseHelper.insertMedicationDetails(baby_id,mMedicationTime_Txt.getText().toString(),medication_type, medicine_name, medicine_dosage, problemdescription);
				
				if (mInsertResult > 0) 
				{
					
					Toast.makeText(getApplicationContext(),	"MedicationDetails Inserted successfully", Toast.LENGTH_SHORT).show();
					Intent data = new Intent();
					data.putExtra("baby_id", baby_id);
					setResult(RESULT_OK, data);
					finish();
					
				}else{
					Toast.makeText(this, "MedicationDetails Insertion Failed", Toast.LENGTH_SHORT).show();

				}
				
				
			}else {
				
				if (!validationStatus.trim().equalsIgnoreCase("")) 
				{
					alertDialogWithMessage(mTitle, validationStatus);
					
					if (mMedicationTime_Txt.getText().toString().equals("")) 
						mMedicationTime_Txt.setHintTextColor(Color.RED);
					
					if (mMedicineName_edtTxt.getText().toString().equals("")) 
						mMedicineName_edtTxt.setHintTextColor(Color.RED);
					
					if(mMedicationType_edtTxt.getText().toString().equals(""))
						mMedicationType_edtTxt.setHintTextColor(Color.RED);
					
					if(mDosage_edtTxt.getText().toString().equals(""))
						mDosage_edtTxt.setHintTextColor(Color.RED);
					
					if(mProblemDescription_edtTxt.getText().toString().equals(""))
						mProblemDescription_edtTxt.setHintTextColor(Color.RED);
			
				}
				
			}
			
			break;
			
			case R.id.medication_details_BTN_clear:
					
				mMedicationTime_Txt.setHintTextColor(Color.BLACK);
				mMedicineName_edtTxt.setHintTextColor(Color.BLACK);
				mMedicationType_edtTxt.setHintTextColor(Color.BLACK);
				mDosage_edtTxt.setHintTextColor(Color.BLACK);
				
				mMedicationTime_Txt.setText("time of medication*");
				mMedicineName_edtTxt.setText("Medication name*");
				mMedicationType_edtTxt.setText("Medication Type*");
				mDosage_edtTxt.setText("Mg*");
				mProblemDescription_edtTxt.setText("Description*");
				
				break;
				
				
			case R.id.medication_details_BTN_save:
				
				Log.v(getClass().getSimpleName(), "111 save click ");
				
				String names = "";
				
				validationStatus = inputValidation(mMedicationTime_Txt.getText().toString(), mMedicationType_edtTxt.getText().toString(), 
								                   mMedicineName_edtTxt.getText().toString(), mDosage_edtTxt.getText().toString(), mProblemDescription_edtTxt.getText().toString());
				
				mMedicationCalender = Calendar.getInstance();
				mMedicationCalender.set(Calendar.HOUR_OF_DAY, mHour);
				mMedicationCalender.set(Calendar.MINUTE, mMinute);
				medicationDate = mMedicationCalender.getTime();
				
				if (validationStatus.equalsIgnoreCase("")) 
				{
					String medication_type = mMedicationType_edtTxt.getText().toString();
					String medicine_dosage = mDosage_edtTxt.getText().toString();
					String problemdescription = mProblemDescription_edtTxt.getText().toString();
					

					for (int iCtr = 0; iCtr < mAdd_Medicines.size(); iCtr++) 
					{
						
						Log.v(getClass().getSimpleName(), "111 "+mExistMedicines.get(iCtr));

						if (mExistMedicines.get(iCtr))
						{
							
							Log.v(getClass().getSimpleName(), "This medicine is deleted");
							
						}else
						{
							name =  mAdd_Medicines.get(iCtr).getText().toString();
							names += name + "," ;
						}	
						
					}
					
					mDataBaseHelper.updateMedicationDetails(medication_id, mMedicationTime_Txt.getText().toString(), medication_type, names, medicine_dosage, problemdescription);
					
					Intent data = new Intent();
					data.putExtra("dosage", medicine_dosage);
					data.putExtra("medicinename", names);
					data.putExtra("time", mMedicationTime_Txt.getText().toString());
					data.putExtra("medication_type", medication_type);
					data.putExtra("description", problemdescription);
					data.putExtra("medication_id", baby_id);
					setResult(RESULT_OK, data);
					finish();
					
					
				}else {
					
					if (!validationStatus.trim().equalsIgnoreCase("")) 
					{
						alertDialogWithMessage(mTitle, validationStatus);
						
						if (mMedicationTime_Txt.getText().toString().equals("")) 
							mMedicationTime_Txt.setHintTextColor(Color.RED);
						
						if (mMedicineName_edtTxt.getText().toString().equals("")) 
							mMedicineName_edtTxt.setHintTextColor(Color.RED);
						
						if(mMedicationType_edtTxt.getText().toString().equals(""))
							mMedicationType_edtTxt.setHintTextColor(Color.RED);
						
						if(mDosage_edtTxt.getText().toString().equals(""))
							mDosage_edtTxt.setHintTextColor(Color.RED);
						
						if(mProblemDescription_edtTxt.getText().toString().equals(""))
							mProblemDescription_edtTxt.setHintTextColor(Color.RED);
				
					}
					
				}
				
				break;
				
			case R.id.medication_details_BTN_cancel:
				finish();  
				break;
		}
	}
	
	public void addMedicineLayout(final int type, String name)
	{
		et_pos++;
		final LinearLayout mMedicineLayout_main = new LinearLayout(MedicationActivity.this);
		mMedicineLayout_main.setOrientation(LinearLayout.HORIZONTAL);
		mMedicineLayout_main.setId(layout_id);
//		mMedicineLayout_main.setGravity(Gravity.RIGHT);
		
		ImageView mRemove_medicine = new ImageView(MedicationActivity.this);
		mRemove_medicine.setId(layout_id);
		mRemove_medicine.setImageResource(R.drawable.btn_minus);
		
		Log.v(getClass().getSimpleName(), "mAdd_Medicines "+mAdd_Medicines.size());
		mRemove_medicine.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAdd_Medicine_Linear.removeView(mMedicineLayout_main);
				
				
				if (type == 0) {
					
					mExistMedicines.remove(et_pos + 1);
					mExistMedicines.add(et_pos + 1, true);
				}else{
					
					mExistMedicines.remove(et_pos);
					mExistMedicines.add(et_pos, true);
				}
				et_pos--;
				Log.v(getClass().getSimpleName(), "v.getId() "+v.getId());
			}
		});
		
		LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		img_params.topMargin = 10;
		mRemove_medicine.setLayoutParams(img_params);
		
		final EditText mAddMedicine_edt;
		
		mAddMedicine_edt = new EditText(MedicationActivity.this);
		mAddMedicine_edt.setSingleLine(true);
		mAddMedicine_edt.setTag(et_pos);
		mAddMedicine_edt.setBackgroundResource(R.drawable.textfield_dynamic);
		
		LinearLayout.LayoutParams edittext_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		edittext_params.topMargin = 5;
		mAddMedicine_edt.setLayoutParams(edittext_params);
		
		if (type == 0) 
		{
			mAddMedicine_edt.setText(name);
			
		}	
		
		mMedicineLayout_main.addView(mAddMedicine_edt);
		mMedicineLayout_main.addView(mRemove_medicine);
		
		mAdd_Medicine_Linear.addView(mMedicineLayout_main);
		mAdd_Medicines.add(mAddMedicine_edt);
		mExistMedicines.add(false);
		ids_check.add(layout_id);
		layout_id++;
		
	}
	
	public void getMedicationDetails(int baby_id){
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.MEDICATION_TABLE+" where "+BabyTrackerDataBaseHelper.MEDICATION_ID+" = "+baby_id);
		
		Log.v(getClass().getSimpleName(), "medication query "+query);
		
		Cursor mCursor = mDataBaseHelper.select(query);
	
		if (mCursor.moveToFirst()) 
		{
			String medicine_name = mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_MEDICINE));
			
			String[] arrMedicine = medicine_name.split(",");
			
			for (int iCtr = 0; iCtr < arrMedicine.length; iCtr++) 
			{
				
				if (iCtr == 0) 
					mMedicineName_edtTxt.setText(arrMedicine[iCtr]);
				else
					addMedicineLayout(0, arrMedicine[iCtr]);
			}
			
			mMedicationTime_Txt.setText(mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_TIME)));
			mMedicationType_edtTxt.setText( mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_TYPE)));
			mDosage_edtTxt.setText(mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_DOSAGE)));
			mProblemDescription_edtTxt.setText(mCursor.getString(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.MEDICATION_DESCRIPTION)));
		
			mSave_relative.setVisibility(View.VISIBLE);
		}
		
	}
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(MedicationActivity.this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//finish();
			}
		})
		.create().show();
	}
	
	/* validating the user input data */
	private String inputValidation(String medication_time, String medication_type, String medicine_name, String dosage, String description)
	{
		if (!medication_time.trim().equals("")) 
		{
			if (!medication_type.trim().equals("")) 
			{
				if (!medicine_name.trim().equals(""))
				{
					return "";
				}else {
					return "Please enter medicine name";
				}
				
			}else {
				return "Please enter medication type";
			}
			
		}else{
			return "Please enter medication time";
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

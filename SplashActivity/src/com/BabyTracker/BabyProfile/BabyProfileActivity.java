package com.BabyTracker.BabyProfile;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

/**
 * This is the class which is used for registering baby details
 * @author android
 *
 */
public class BabyProfileActivity extends Activity implements OnClickListener {

	private static String LOG_TAG = BabyProfileActivity.class.getSimpleName();

	private static final int SELECT_PICTURE = 1;
	private int baby_id ;
	private EditText name_edtTxt;
	private Button addProfile_Btn, clear_Btn,  mSave_Btn, mCancel_Btn;
	private ImageButton addbaby_Img;

	private BabyTrackerDataBaseHelper mDataBaseHelper;
	byte[] babyImage;
	private String mTitle = "BabyTracker", mGender_Str = "";
	private TextView mHeading, mBabydateofbirth_Txt;

	private final int DATE_DIALOG_ID = 0;
	private int mDay;
	private int mMonth;
	private int mYear;
	private String name = "";

	private final String[] MONTH_NAME = { "Jan", "Feb", "Mar", "Apr", "May",
											"June", "July", "Aug", "Sep", "Oct", "Nov", "Dec" };

	private String mBabydateofbirth_Str = "", baby_name = "";
	private RadioButton mRadio_male, mRadio_female;

	private byte imageData[];

	private Intent receiverIntent;
	
	Date date_of_birth = null;
	SimpleDateFormat formatter = null;
	private RelativeLayout mSave_relative;
	Date mDate = null;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.babyprofile_details);
		initializeUI();
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(BabyProfileActivity.this); 
		
		mDataBaseHelper.openDataBase();

		formatter = new SimpleDateFormat("dd-MM-yyyy");
		
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
		
		Calendar calender = Calendar.getInstance();
		mDay = calender.get(Calendar.DAY_OF_MONTH);
		mMonth = calender.get(Calendar.MONTH);
		mYear = calender.get(Calendar.YEAR);
		
		receiverIntent = getIntent();
		
		if (receiverIntent.getAction().equalsIgnoreCase("fromHome")){
			
		}else if (receiverIntent.getAction().equalsIgnoreCase("edit_profile")){ 
				baby_id = receiverIntent.getExtras().getInt("settings_babyid");
				getSelectedBabyProfile(baby_id);
		}
		
	}

	/* Initializing UI Here */
	private void initializeUI() {

		name_edtTxt = (EditText) findViewById(R.id.babyprofile_details_ETXT_name);

		addProfile_Btn = (Button) findViewById(R.id.babyprofile_details_BTN_add);
		addProfile_Btn.setOnClickListener(this);

		clear_Btn = (Button) findViewById(R.id.babyprofile_details_BTN_clear);
		clear_Btn.setOnClickListener(this);

		addbaby_Img = (ImageButton) findViewById(R.id.babyprofile_details_IMG_babyimage);
		addbaby_Img.setOnClickListener(this);

		mBabydateofbirth_Txt = (TextView) findViewById(R.id.babyprofile_details_TXT_babydate);
		mBabydateofbirth_Txt.setOnClickListener(this);

		mHeading = (TextView) findViewById(R.id.simple_top_bar_Txt_title);
		mHeading.setText("Baby Profile");
		
		mSave_Btn = (Button)findViewById(R.id.babyprofile_details_BTN_save);
		mSave_Btn.setOnClickListener(this);
		
		mCancel_Btn = (Button)findViewById(R.id.babyprofile_details_BTN_cancel);
		mCancel_Btn.setOnClickListener(this);
		
		mSave_relative = (RelativeLayout)findViewById(R.id.babyprofile_details_LIN_babydetails4);
		
		mRadio_male = (RadioButton)findViewById(R.id.radioMale);
		mRadio_female = (RadioButton)findViewById(R.id.radioFemale);
		mRadio_male.setOnClickListener(this);
		mRadio_female.setOnClickListener(this);
		mRadio_male.setChecked(true);

	}

	public void onRadioButtonClicked(View v) {
		// Perform action on clicks
		RadioButton rb = (RadioButton) v;
		mGender_Str = (String) rb.getText();
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay);

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
        
	private void updateDisplay() {

		mBabydateofbirth_Str = new StringBuffer().append(mDay).append("-")
				.append(mMonth + 1).append("-").append(mYear).toString();
		mBabydateofbirth_Txt.setText(new StringBuilder()
				.append(MONTH_NAME[mMonth]).append(" ").append(mDay)
				.append(",").append(mYear));
		Log.v(LOG_TAG, "date of birth " + mBabydateofbirth_Str);
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch (requestCode) {
		case SELECT_PICTURE: {
			if (resultCode == RESULT_OK) {
				Uri uri = imageReturnedIntent.getData();
				String[] projection = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(uri, projection,null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(projection[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				options.inPurgeable = true;
				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath,options);
				Drawable d = new BitmapDrawable(yourSelectedImage);

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 90,outputStream);
				babyImage = outputStream.toByteArray();
				addbaby_Img.setImageDrawable(d);
				addbaby_Img.invalidate();
			}// for if
		  }// for case
		}// for switch
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
			
		case R.id.babyprofile_details_BTN_add:

			if (mRadio_male.isChecked())
				mGender_Str = (String) mRadio_male.getText();
			else if (mRadio_female.isChecked()) 
				mGender_Str = (String) mRadio_female.getText();
			
			
			long insertResult = 0;

			
			if (!mBabydateofbirth_Str.trim().equals("")) 
			{

				try 
				{
					date_of_birth = formatter.parse(mBabydateofbirth_Str);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			String inputValidationStatus = validation(name_edtTxt.getText().toString(), date_of_birth, babyImage);

			if (inputValidationStatus.trim().equals(""))
			{

				baby_name = name_edtTxt.getText().toString();

				Log.v(getClass().getSimpleName(), "insert "+babyImage);

				insertResult = mDataBaseHelper.insertBabydetails(baby_name,date_of_birth.toString(), mGender_Str, babyImage,0,0);
				
				if(insertResult > 0)
				{
					
					String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.PROFILE_TABLE+" where "+BabyTrackerDataBaseHelper.KEY_NAME+" = '"+baby_name+"'");
					Log.e("create profile",query);
					Cursor mCursor = mDataBaseHelper.select(query);
					if(mCursor.moveToFirst()){
						baby_id = mCursor.getInt(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_ID));
					}
					mCursor.close();
					mCursor = null;
					
				}
				
			} else 
			{
				if (!inputValidationStatus.trim().equals("")) 
				{
					alertDialogWithMessage(mTitle, inputValidationStatus);

					if (name_edtTxt.getText().toString().equals(""))
						name_edtTxt.setHintTextColor(Color.RED);
				}
			}

			if (insertResult > 0) 
			{
				
				Toast.makeText(getApplicationContext(),	"BabyProfile Details Inserted successfully", Toast.LENGTH_SHORT).show();
				Intent data = new Intent();
				data.putExtra("baby_id", baby_id);
				setResult(RESULT_OK, data);
				finish();
				
			} else {
				Toast.makeText(getApplicationContext(),"BabyProfile Details Failed ", Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.babyprofile_details_BTN_clear:

			name_edtTxt.setText("");
			mBabydateofbirth_Txt.setText("");
			name_edtTxt.setHintTextColor(Color.BLACK);

			break;

		case R.id.babyprofile_details_IMG_babyimage:

			Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, SELECT_PICTURE);

			break;

		case R.id.babyprofile_details_TXT_babydate:
			showDialog(DATE_DIALOG_ID);
			break;
			
		case R.id.babyprofile_details_BTN_save:
			
				if (mRadio_male.isChecked())
					mGender_Str = (String) mRadio_male.getText();
				else if (mRadio_female.isChecked()) 
					mGender_Str = (String) mRadio_female.getText();

				baby_name = name_edtTxt.getText().toString();

				if (!mBabydateofbirth_Str.trim().equals("")) {

					try 
					{
						mDate = formatter.parse(mBabydateofbirth_Str);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else
				{		
					
				}
				
				String inputValidationStatus_update = validation(name_edtTxt.getText().toString(), mDate, imageData);
				if (inputValidationStatus_update.trim().equals("")) 
				{
					
					mDataBaseHelper.updateBabydetails(baby_name, mDate.toString(), mGender_Str, babyImage, baby_id);
					
					Intent data1 = new Intent();
					data1.putExtra("baby_id", baby_id);
					setResult(RESULT_OK, data1);
					finish();
				
				} else if (!inputValidationStatus_update.trim().equals("")) {
					
					alertDialogWithMessage(mTitle, inputValidationStatus_update);

					if (name_edtTxt.getText().toString().equals(""))
						name_edtTxt.setHintTextColor(Color.RED);
				}
			
			break;
			
		case R.id.babyprofile_details_BTN_cancel:
			
			Intent data1 = new Intent();
			data1.putExtra("baby_id", baby_id);
			setResult(RESULT_OK, data1);
			finish();
			
			break;
			
		}

	}

	private String validation(String baby_name, Date birthday_date,byte[] image_Array) 
	{
		
		Date currentDate = new Date();
		Log.v(LOG_TAG, " current date "+currentDate+" date of birth "+birthday_date);
		if (!baby_name.trim().equals(""))
		{
			if (birthday_date!= null)
			{
				if (currentDate.after(birthday_date)) 
				{
					if (image_Array != null) 
					{
						return "";
					} else
					{
						return "Please select baby image";
					}
				} else 
				{
					return "Please select Valid Date";
				}

			}else
			{
				return "Please select Valid Date";
			}
		} else 
		{
			return "Please enter baby name";
		}

	}

	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg) {

		new BabyTrackerAlert(BabyProfileActivity.this, title, msg)
				.setPositiveButton("Ok",
						new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,	int which) {
								// TODO Auto-generated method stub
								// finish();
							}
						}).create().show();
	}

	/**
	 *  Getting the selected baby profile based on selected baby id.
	 * @param baby_id
	 */
	public void getSelectedBabyProfile(int baby_id){
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.PROFILE_TABLE+" where "+BabyTrackerDataBaseHelper.KEY_ID+" = "+ baby_id);
		Cursor tempcursor = mDataBaseHelper.select(query);
		
		try {
			
			if(tempcursor.moveToFirst()){
				
				name = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_NAME));
				String dob  = tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_DOB));
				imageData = tempcursor.getBlob(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_IMAGE));
				
				if (tempcursor.getString(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_GENDER)).equalsIgnoreCase("male")){
					mRadio_male.setChecked(true);

					}else{
					mRadio_female.setChecked(true);
				}
				
				
				Log.v(getClass().getSimpleName(), "dob "+dob);
				
				SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				SimpleDateFormat mDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
				
				
				try {
					mDate = mDateFormat.parse(dob);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String changeAppTime = mDateFormat1.format(mDate);
				
				mBabydateofbirth_Txt.setText(""+changeAppTime);
				name_edtTxt.setText(name);
				babyImage = tempcursor.getBlob(tempcursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_IMAGE));
				addbaby_Img.setImageBitmap(BitmapFactory.decodeByteArray(babyImage, 0, babyImage.length));
				Log.v(getClass().getSimpleName(), "addbaby_Img "+babyImage);
				mSave_relative.setVisibility(View.VISIBLE);

			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			tempcursor.close();
		}
		
		
	}	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDataBaseHelper != null) {
			mDataBaseHelper.close();
		}
	}
}

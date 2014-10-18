package com.BabyTracker.BabyGrowth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class GrowthdetailsActivity extends Activity implements OnClickListener {

	private static String LOG_TAG = GrowthdetailsActivity.class.getSimpleName();

	private EditText mBabyheight_edtTxt, mBabyweight_edtTxt,
			mBabycircumference_edtTxt;
	private TextView mBabygrowth_details_title;
	private Button mSubmit_Btn, mClear_Btn;

	private int age_in_months;

	private SharedPreferences mSharedPreferences;

	private int growth_id;
	private String mTitle = "BabyTracker";

	private Intent receiverIntent;

	private BabyTrackerDataBaseHelper mDataBaseHelper = null;

	private double mBaby_height, mBaby_weight, mBaby_circumference;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.babygrowth_details);
		initializeUI();
		receiverIntent = getIntent();

		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);
		_navRel.setVisibility(View.VISIBLE);
		_TitleTxt = (TextView)findViewById(R.id.nav_text);
		_TitleTxt.setText("Home");
		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GrowthdetailsActivity.this, Home.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		mDataBaseHelper = new BabyTrackerDataBaseHelper(GrowthdetailsActivity.this);

		mSharedPreferences = getSharedPreferences("BabyTracker",MODE_WORLD_READABLE);

		age_in_months = mSharedPreferences.getInt("baby_age", 0);
		growth_id = receiverIntent.getExtras().getInt("baby_id");

	}

	/* Initialing UserInterface here */
	public void initializeUI() {

		mBabyheight_edtTxt = (EditText) findViewById(R.id.babygrowth_details_ETXT_height);
		mBabyweight_edtTxt = (EditText) findViewById(R.id.babygrowth_details_ETXT_weight);
		mBabycircumference_edtTxt = (EditText) findViewById(R.id.babygrowth_details_ETXT_circumference);

		mSubmit_Btn = (Button) findViewById(R.id.babygrowth_details_BTN_add);
		mSubmit_Btn.setOnClickListener(this);
		mClear_Btn = (Button) findViewById(R.id.babygrowth_details_BTN_clear);
		mClear_Btn.setOnClickListener(this);

		mBabygrowth_details_title = (TextView) findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Growth");

	}

	/* validating the user input data */
	private String inputValidation(String baby_dateofbirth, String baby_height,
			String baby_weight, String baby_circumference) {

		if (!baby_dateofbirth.trim().equals("")) {
			if (!baby_height.trim().equals("")) {
				if (!baby_weight.trim().equals("")) {
					if (!baby_circumference.trim().equals("")) {
						return "";
					} else {
						return "Please Enter all the information";
					}
				} else {
					return "Please Enter all the information";
				}
			} else {
				return "Please Enter all the information";
			}

		} else {
			return "Please Enter all the information";
		}

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.babygrowth_details_BTN_add:

			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");

			long mInsertResult = 0;
			mDataBaseHelper.openDataBase();

			try {
				mDataBaseHelper.createDataBase();
			} catch (Exception ex) {
				// TODO: handle exception
				ex.printStackTrace();
			}

			Calendar calender = Calendar.getInstance();
			Date currentDate = calender.getTime();

			String currentDate_Str = dateFormatter.format(currentDate);

			String validationStatus = inputValidation(currentDate_Str,mBabyheight_edtTxt.getText().toString(), 
					mBabyweight_edtTxt.getText().toString(), mBabycircumference_edtTxt.getText().toString());

			if (validationStatus.equalsIgnoreCase("")) 
			{
				
				mBaby_height = Double.parseDouble(mBabyheight_edtTxt.getText().toString());
				mBaby_weight = Double.parseDouble(mBabyweight_edtTxt.getText().toString());
				mBaby_circumference = Double.parseDouble(mBabycircumference_edtTxt.getText().toString());
				double baby_age = Double.parseDouble(Integer.toString(age_in_months));
				Log.v(getClass().getSimpleName(), "age_in_months " + baby_age);

				/* Inserting details into the data base. */
				mInsertResult = mDataBaseHelper.insertBabyGrowthDetails(growth_id, mBaby_height, mBaby_weight,mBaby_circumference, baby_age, currentDate_Str);

			} else {

				if (!validationStatus.trim().equals("")) {
					alertDialogWithMessage(mTitle, validationStatus);

					if (mBabyheight_edtTxt.getText().toString().equals(""))
						mBabyheight_edtTxt.setHintTextColor(Color.RED);

					if (mBabyweight_edtTxt.getText().toString().equals(""))
						mBabyweight_edtTxt.setHintTextColor(Color.RED);

					if (mBabycircumference_edtTxt.getText().toString()
							.equals(""))
						mBabycircumference_edtTxt.setHintTextColor(Color.RED);

				}
			}

			if (mInsertResult > 0) {

				Toast.makeText(GrowthdetailsActivity.this,"Baby details Inserted Successfully",Toast.LENGTH_SHORT).show();

				mDataBaseHelper.updateBabyHeightandWeight(growth_id,mBaby_height, mBaby_weight);

				Log.v(LOG_TAG, " growth id " + growth_id);
				Intent data = new Intent();
				data.putExtra("growth_id", growth_id);
				setResult(RESULT_OK, data);
				finish();

			} else {
				// Toast.makeText(GrowthdetailsActivity.this,
				// "Insertion Failed ", Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.babygrowth_details_BTN_clear:

			// mBabyname_edtTxt.setText("");
			mBabycircumference_edtTxt.setText("");
			mBabyheight_edtTxt.setText("");
			mBabyweight_edtTxt.setText("");
			mBabyheight_edtTxt.setHintTextColor(Color.BLACK);
			mBabyweight_edtTxt.setHintTextColor(Color.BLACK);
			mBabycircumference_edtTxt.setHintTextColor(Color.BLACK);

			break;

		}

	}

	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg) {

		new BabyTrackerAlert(GrowthdetailsActivity.this, title, msg)
				.setPositiveButton("Ok",
						new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// finish();
							}
						}).create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, Home.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDataBaseHelper.close();
	}

}

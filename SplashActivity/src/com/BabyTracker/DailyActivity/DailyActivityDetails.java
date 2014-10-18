package com.BabyTracker.DailyActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class DailyActivityDetails extends Activity implements OnClickListener{

	private TextView mMilk_Qty_Txt, mSl_Txt;
	private TextView mBabygrowth_details_title;

	private ImageView mQty_Increase_Img, mQty_Decrease_Img, mSl_Increase_Img, mSl_Decrease_Img;
	private RadioButton mDiperchange_Yes_Radio, mDiperchange_No_Radio;
	private Button mSave_Btn, mCancel_Btn;
	
	private Intent receiverIntent;
	
	private int baby_id, mDiperStatus, mSleepinghours = 0;
	int mMilkQty = 0;
	
	private BabyTrackerDataBaseHelper mBabyTrackerDataBaseHelper;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dailyactivity_details);
		initializeUI();
		
		mBabyTrackerDataBaseHelper = new BabyTrackerDataBaseHelper(this);

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
		if (receiverIntent.getAction().equalsIgnoreCase("fromHome")) {
			baby_id = receiverIntent.getExtras().getInt("baby_id");
			Log.v(getClass().getSimpleName(), " baby id "+baby_id);
		}
		
	}

	
	public void initializeUI()
	{
		
		mMilk_Qty_Txt = (TextView)findViewById(R.id.dailyactivity_details_TXT_milkquantity);
		mSl_Txt = (TextView)findViewById(R.id.dailyactivity_details_TXT_sleepingHours);
		
		mQty_Decrease_Img = (ImageView)findViewById(R.id.dailyactivity_details_IMG_dec);
		mQty_Increase_Img = (ImageView)findViewById(R.id.dailyactivity_details_IMG_inc);
		
		mSl_Increase_Img = (ImageView)findViewById(R.id.dailyactivity_details_IMG_inc_sleepingHours);
		mSl_Decrease_Img = (ImageView)findViewById(R.id.dailyactivity_details_IMG_dec_sleepingHours);
		
		mDiperchange_Yes_Radio = (RadioButton)findViewById(R.id.radioYes);
		mDiperchange_No_Radio = (RadioButton)findViewById(R.id.radioNo);
		
		mSave_Btn    = (Button)findViewById(R.id.dailyactivity_details_BTN_add);
		mCancel_Btn  = (Button)findViewById(R.id.dailyactivity_details_BTN_dailyActivities);
		
		mBabygrowth_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Daily Activity");
		
		mQty_Decrease_Img.setOnClickListener(this);
		mQty_Increase_Img.setOnClickListener(this);
		mSl_Increase_Img.setOnClickListener(this);
		mSl_Decrease_Img.setOnClickListener(this);
		
		mSave_Btn.setOnClickListener(this);
		mCancel_Btn.setOnClickListener(this);
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
		
			case R.id.dailyactivity_details_IMG_inc:
					
					if (mMilkQty <= 1000) {
						mMilkQty = mMilkQty + 1;
						mMilk_Qty_Txt.setText(""+mMilkQty+" ml");
					}
				
				break;
	
			case R.id.dailyactivity_details_IMG_dec:
				
					if (mMilkQty != 0) {
						mMilkQty = mMilkQty - 1;
						mMilk_Qty_Txt.setText(""+mMilkQty+" ml");
					}
					
				break;
							
			case R.id.dailyactivity_details_IMG_inc_sleepingHours:
				
				if (mSleepinghours < 24) {
					mSleepinghours = mSleepinghours + 1;
					mSl_Txt.setText(""+mSleepinghours+" Hours");
				}
			
			break;

		case R.id.dailyactivity_details_IMG_dec_sleepingHours:
			
				if (mSleepinghours != 0) {
					mSleepinghours = mSleepinghours - 1;
					mSl_Txt.setText(""+mSleepinghours+" Hours");
				}
				
			break;	
			case R.id.dailyactivity_details_BTN_add:
				
				long insertStatus = 0;
				String dailyacvity_date = "";
				
					if (mDiperchange_Yes_Radio.isChecked() || mDiperchange_No_Radio.isChecked()) {
						if (mDiperchange_Yes_Radio.isChecked()) {
							mDiperStatus = 1;
						}else if (mDiperchange_No_Radio.isChecked()) {
							mDiperStatus = 0;
						}
						
					}else{
						Toast.makeText(getApplicationContext(), "Please select Diper status ", Toast.LENGTH_SHORT).show();
					}
					
					Calendar calender = Calendar.getInstance();
					Date current_date = calender.getTime();
					
					try 
					{
						mBabyTrackerDataBaseHelper.createDataBase();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					SimpleDateFormat mDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
					dailyacvity_date =mDateFormat.format(current_date);
					
					insertStatus = mBabyTrackerDataBaseHelper.insertDailyActivitydetails(baby_id, current_date.toString(), mDiperStatus, mMilkQty, mSleepinghours,dailyacvity_date);
					
					if (insertStatus > 0) {
						alertDialogWithMessage("Baby Tracker", "Daily activity details are saved succefully");
					}else{
						Toast.makeText(this, "Daily activity details Insertion Failed ", Toast.LENGTH_SHORT).show();
					}
					
				break;
				
			case R.id.dailyactivity_details_BTN_dailyActivities:
				startActivity(new Intent(this, DailyActivityList.class).putExtra("baby_id", baby_id).setAction("fromDetails"));
				break;
		}
	}
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
						
		 String current_date = null;
		String dailyacvity_date = null;
		mBabyTrackerDataBaseHelper.insertDailyActivitydetails(baby_id, current_date, mDiperStatus, mMilkQty=0 , mSleepinghours=0,dailyacvity_date);
				mMilk_Qty_Txt.setText("0 ml");
				mSl_Txt.setText("0 Hours");
				
			}
		}).setIcon(android.R.drawable.ic_dialog_info)
		.create().show();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBabyTrackerDataBaseHelper.close();
	}
}
						
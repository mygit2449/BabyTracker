package com.BabyTracker.Vaccination;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class VaccinationDescription extends Activity implements OnClickListener{

	private TextView mVaccinationName_Txt, mVaciinationDescription_Txt, mVaccinationTime_Txt, mVaccinationStatus_Txt;
	
	private CheckBox mVaccinationStatus_Chkb;
	
	private Intent receiverIntent;
	private int vaccination_id, check = 0;
	
	private String vaccination_name_str, vaccination_description_str, vaccination_status_str,
				   vaccination_time_str, vaccinations_type_str ="";
	
	private Button mDone_Btn;
	
	private int  baby_id;
	
	private SharedPreferences mVaccinationPreferences;
	
	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	private RelativeLayout _navRel;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccination_description);
		initializeUI();
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(this);		
	    mDataBaseHelper.openDataBase();
	    
		receiverIntent = getIntent();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		vaccination_id = receiverIntent.getExtras().getInt("vaccination_id");
		vaccination_name_str = receiverIntent.getExtras().getString("vaccination_name");
		vaccination_description_str = receiverIntent.getExtras().getString("vaccination_description");
		vaccination_status_str = receiverIntent.getExtras().getString("vaccination_status");
		vaccination_time_str = receiverIntent.getExtras().getString("vaccination_time");
		vaccinations_type_str = receiverIntent.getExtras().getString("for_completed");
		
		mVaccinationPreferences = getSharedPreferences("BabyTracker", MODE_PRIVATE);
		
		baby_id = mVaccinationPreferences.getInt("baby_id", 0);
		
		settingsData();
	}

	private void settingsData() 
	{
		// TODO Auto-generated method stub
		mVaccinationName_Txt.setText(vaccination_name_str);
		mVaciinationDescription_Txt.setText(vaccination_description_str);
		mVaccinationTime_Txt.setText(vaccination_time_str);
		mVaccinationStatus_Txt.setText(vaccination_status_str);

		if(vaccination_status_str.equalsIgnoreCase("Given"))
		{
			mVaccinationStatus_Chkb.setChecked(true);
		}else
		{
			mVaccinationStatus_Chkb.setChecked(false);
		}
		
	}

	public void initializeUI()
	{
		
		mVaccinationName_Txt = (TextView)findViewById(R.id.vaccination_details_TXT_babyname_display);
		mVaciinationDescription_Txt = (TextView)findViewById(R.id.vaccination_details_TXT_description_display);
		mVaccinationTime_Txt = (TextView)findViewById(R.id.vaccination_details_TXT_time_display);
		mVaccinationStatus_Txt = (TextView)findViewById(R.id.vaccination_details_TXT_status_display);
		
		mDone_Btn = (Button)findViewById(R.id.vaccination_details_description_BTN_done);
		mDone_Btn.setOnClickListener(this);
		
		mVaccinationStatus_Chkb = (CheckBox)findViewById(R.id.vaccination_status_chkb);
		
		mVaccinationStatus_Chkb.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		
			case R.id.vaccination_status_chkb:
			
				if(mVaccinationStatus_Chkb.isChecked())
				{
					
					mVaccinationStatus_Txt.setText("Given");
					vaccination_status_str = "Given";
					check = 1;
					
				}else
				{
					
					mVaccinationStatus_Txt.setText("Not Given");
					vaccination_status_str = "Not Given";
					check = 1;
				}

				break;

			case R.id.vaccination_details_description_BTN_done:
			
				if (check == 1)
				{
					
					if (vaccinations_type_str.equalsIgnoreCase("pending") && vaccination_status_str.equalsIgnoreCase("Given")) 
					{
						alertDialogWithMessage("BabyTracker", "Do you want to change the vaccination status ?", 0);
					}else if (vaccinations_type_str.equalsIgnoreCase("completed_vaccination")
							&& vaccination_status_str.equalsIgnoreCase("Not Given"))
					{
						alertDialogWithMessage("BabyTracker", "Do you want to change the vaccination status ?", 1);
					}else
					{
						startActivity(new Intent(VaccinationDescription.this, VaccinationActivity.class));
					}
		
				}else
				{
					startActivity(new Intent(VaccinationDescription.this, VaccinationActivity.class));
				}
					
				break;
			
			default:
				break;
		}
	}
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg, final int type) 
	{

		new BabyTrackerAlert(this, title, msg)
				.setPositiveButton("Yes",new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								
								
								if (type == 0) 
								{
									
									Log.v(getClass().getSimpleName(), "update vaccination_id "+vaccination_id+"mVaccinationStatus_Txt "+mVaccinationStatus_Txt.getText().toString());
									mDataBaseHelper.insertCompletedVaccination(baby_id,vaccination_id, vaccination_name_str, vaccination_description_str, vaccination_time_str, mVaccinationStatus_Txt.getText().toString());
								
								}
								
								if (type == 1) 
								{
									Log.v(getClass().getSimpleName(), "delete vaccination_id "+vaccination_id+"mVaccinationStatus_Txt "+mVaccinationStatus_Txt.getText().toString());
									mDataBaseHelper.deleteVaccination(vaccination_id);
								}
								
								startActivity(new Intent(VaccinationDescription.this, VaccinationActivity.class));
								
							}
						}).setIcon(android.R.drawable.ic_dialog_info)
				.setNegativeButton("No", null).create().show();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mDataBaseHelper.close();
	}
	
}

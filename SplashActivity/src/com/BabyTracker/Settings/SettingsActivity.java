package com.BabyTracker.Settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.BabyTracker.R;
import com.BabyTracker.BabyProfile.BabyProfileActivity;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class SettingsActivity extends Activity implements OnClickListener{

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor editor;
	private int baby_id;
	private static final int REQUEST_CODE_PROFILE_EDIT = 0;

	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	
	private TextView txt_baby_name;
	private String name = "";
	
	private String mAlertTitle = "BabyTacker";
	
	private String mAlertMessage = "Register Your Baby Details";
	private TextView mSettings_details_title;
	
	private ToggleButton mReminderStatus_toggle;
	private int toggle_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		initializeUI();
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(this);		
	    mDataBaseHelper.openDataBase();
	     
		mSharedPreferences = getSharedPreferences("BabyTracker", MODE_WORLD_READABLE);
		editor = mSharedPreferences.edit();
		baby_id = mSharedPreferences.getInt("baby_id", 0);
		toggle_status = mSharedPreferences.getInt("toggle_status", 1);
		
		Log.v(getClass().getSimpleName(), "toggle_status "+toggle_status);

		if (baby_id != 0) {
			getSelectedBabyProfile(baby_id);
		}else{
			alertDialogWithMessage(mAlertTitle, mAlertMessage);
		}
		
		if(toggle_status == 1)
    		mReminderStatus_toggle.setChecked(true);
    	
	}

	/* Initializing UI Here */
    public void initializeUI()
    {
    	txt_baby_name = (TextView)findViewById(R.id.settings_layout_text_name);
    	mSettings_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
    	mSettings_details_title.setText("Settings");
    	
    	mReminderStatus_toggle = (ToggleButton)findViewById(R.id.settings_layout_reminder_status);
    	
    	mReminderStatus_toggle.setOnClickListener(this);
    	
    	
    }
    
    public void onEditProfileClick(View v){
    	
    	Intent intent = new Intent(SettingsActivity.this, BabyProfileActivity.class);
		intent.putExtra("settings_babyid", baby_id);
		intent.setAction("edit_profile");
		startActivityForResult(intent, REQUEST_CODE_PROFILE_EDIT);
    	
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
        super.onActivityResult(requestCode, resultCode, data);   
        switch(requestCode) 
        {  
	        case REQUEST_CODE_PROFILE_EDIT:  
	        {
	        	if (resultCode == RESULT_OK) 
	        	{
	    			baby_id = data.getExtras().getInt("baby_id");
	    			Log.v(getClass().getSimpleName(), " baby_id "+baby_id);
	    			getSelectedBabyProfile(baby_id);
				}
	        }
	    }
    }
    
    /* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(this, title, msg).setPositiveButton("Now", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,BabyProfileActivity.class);  
				startActivityForResult(intent, REQUEST_CODE_PROFILE_EDIT);
			}
		}).setIcon(android.R.drawable.ic_dialog_info)
		.setNegativeButton("Later", null).create().show();
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
				txt_baby_name.setText(name);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			tempcursor.close();
		}
		
		
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		mDataBaseHelper.close();
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.settings_layout_reminder_status:
			
			if (mReminderStatus_toggle.isChecked()) {
				Log.v(getClass().getSimpleName(), " toggle checked on");
				toggle_status = 1;
				editor.putInt("toggle_status", toggle_status);
				editor.commit();
//				new ReminderStatus().setOneTimeAlarm(SettingsActivity.this);
			}else{
				Log.v(getClass().getSimpleName(), " toggle checked off");
//				new ReminderStatus().cancelAlarm(SettingsActivity.this);
				toggle_status = 0;
				editor.putInt("toggle_status", toggle_status);
				editor.commit();
			}
		break;

		default:
			break;
		}
	}	
}

package com.BabyTracker.Menu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.BabyTracker.R;
import com.BabyTracker.BabyProfile.BabyProfileActivity;
import com.BabyTracker.Medication.MedicationListActivity;
import com.BabyTracker.Settings.SettingsActivity;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class MenuOptionsActivity extends Activity{

    private SharedPreferences mSharedPreferences;
    private int baby_id;
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		
		mSharedPreferences = getSharedPreferences("BabyTracker", MODE_WORLD_READABLE);
		baby_id = mSharedPreferences.getInt("baby_id", 0);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){	
		switch (menuItem.getItemId()) 
		{
			case R.id.settings:
				startActivity(new Intent(MenuOptionsActivity.this, SettingsActivity.class));
			return true;
			
			case R.id.miscellaneous:
				
				if (baby_id == 0)
					alertDialogWithMessage("Baby Tracker", "Please Register your baby");
				else
				 startActivity(new Intent(MenuOptionsActivity.this, MedicationListActivity.class).setAction("fromMenu"));
			return true;
			
			case R.id.help:
				
			return true;

			default:
				
			return super.onOptionsItemSelected(menuItem);
			
		}
		
	}
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg) {

		new BabyTrackerAlert(this, title, msg).setPositiveButton("Now",new android.content.DialogInterface.OnClickListener() 
		{

			public void onClick(DialogInterface dialog,	int which)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(MenuOptionsActivity.this,BabyProfileActivity.class);
				intent.setAction("fromMenu");
			}
		}).setIcon(android.R.drawable.ic_dialog_info).setNegativeButton("Later", null).create().show();
	}
}

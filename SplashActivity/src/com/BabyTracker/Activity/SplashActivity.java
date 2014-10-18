package com.BabyTracker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class SplashActivity extends Activity{

	ImageView mSplashImage;
	private BabyTrackerDataBaseHelper mBabyTrackerDataBaseHelper = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashview);
		mBabyTrackerDataBaseHelper = new BabyTrackerDataBaseHelper(this);
		mSplashImage = (ImageView)findViewById(R.id.splashview_IMG);
		new Thread(){
		
			@Override
			public void run(){
				try{
				mBabyTrackerDataBaseHelper.createDataBase();
				sleep(3000);
				}catch (Exception e) {
					// TODO: handle exception
				}finally{
					startActivity(new Intent(SplashActivity.this, Home.class).setAction("splash"));
					finish();
				}
			}
		}.start();
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBabyTrackerDataBaseHelper.close();
	}
}

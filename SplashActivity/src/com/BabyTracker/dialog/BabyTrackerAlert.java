package com.BabyTracker.dialog;

import com.BabyTracker.R;

import android.app.AlertDialog;
import android.content.Context;


public class BabyTrackerAlert extends AlertDialog.Builder{

	Context contex;
	public BabyTrackerAlert(Context context, String title, String msg) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setTitle(title);
		this.setMessage(msg);
		this.setIcon(R.drawable.login_alert);
		this.setCancelable(true);
	}

}

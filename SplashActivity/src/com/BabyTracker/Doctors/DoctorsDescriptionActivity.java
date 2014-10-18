package com.BabyTracker.Doctors;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;

public class DoctorsDescriptionActivity extends Activity implements OnClickListener{

	private TextView mDoctorname_Txt,  mEmailid_Txt, mPhonenumber_txt, mAddress_Txt, mWorkingHours_Txt;

	private Intent receiverIntent = null;
	
	private String doctorname_str, emailid_str, phone_number_str, contactaddress_str, workinghours_str;
	
	private ImageView dailImage;
	
	private int doctor_id;
	
	private Button mEdit_Btn;
	private static final int EDIT_ITEM = 1;
	private RelativeLayout _navRel;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctors_details);
		initialzeUI();
		
		receiverIntent = getIntent();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		if (receiverIntent.getAction().equalsIgnoreCase("fromDoctorsList")) 
		{
		
			doctorname_str = receiverIntent.getExtras().getString("name");
			emailid_str = receiverIntent.getExtras().getString("email");
			phone_number_str = receiverIntent.getExtras().getString("phone");
			contactaddress_str = receiverIntent.getExtras().getString("address");
			workinghours_str = receiverIntent.getExtras().getString("timings");
			
			doctor_id = receiverIntent.getExtras().getInt("_id");
		}
		
		mDoctorname_Txt.setText(doctorname_str);
		mEmailid_Txt.setText(emailid_str);
		mPhonenumber_txt.setText(phone_number_str);
		mAddress_Txt.setText(contactaddress_str);
		mWorkingHours_Txt.setText(workinghours_str);
		
		dailImage.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String uri = "tel:" + phone_number_str.trim() ;
			    Intent intent = new Intent(Intent.ACTION_CALL);
			    intent.setData(Uri.parse(uri));
			    startActivity(intent);
			}
		});
	}

	/* Initializing UI Here */
	public void initialzeUI()
	{
		
		mDoctorname_Txt = (TextView)findViewById(R.id.doctors_details_TXT_doctorname_display);
		mEmailid_Txt = (TextView)findViewById(R.id.doctors_details_TXT_emailid_display);
		mPhonenumber_txt = (TextView)findViewById(R.id.doctors_details_TXT_phone_display);
		mAddress_Txt = (TextView)findViewById(R.id.doctors_details_TXT_address_display);
		mWorkingHours_Txt = (TextView)findViewById(R.id.doctors_details_TXT_workinghours_display);
		dailImage = (ImageView)findViewById(R.id.doctors_dailer_Img);
		
		mEdit_Btn = (Button)findViewById(R.id.doctors_details_BTN_edit);
		mEdit_Btn.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
        super.onActivityResult(requestCode, resultCode, data);   
        switch(requestCode) 
        {  
	        case EDIT_ITEM:  
	        {
	        	if (resultCode == RESULT_OK) 
	        	{
	        		
	        		doctorname_str = data.getExtras().getString("name");
	    			emailid_str = data.getExtras().getString("email");
	    			phone_number_str = data.getExtras().getString("phone");
	    			contactaddress_str = data.getExtras().getString("address");
	    			workinghours_str = data.getExtras().getString("timings");
	    			
	    			doctor_id = data.getExtras().getInt("_id");
	    			
	        		mDoctorname_Txt.setText(doctorname_str);
	        		mEmailid_Txt.setText(emailid_str);
	        		mPhonenumber_txt.setText(phone_number_str);
	        		mAddress_Txt.setText(contactaddress_str);
	        		mWorkingHours_Txt.setText(workinghours_str);
				}
	        }
	    }
    }
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.doctors_details_BTN_edit:
			
			Log.v("details ", " doctor_id "+doctor_id);
			intent = new Intent(DoctorsDescriptionActivity.this, DoctorsDetailsActivity.class);
			intent.putExtra("doctor_id", doctor_id);
			intent.setAction("edit");
			startActivityForResult(intent, EDIT_ITEM);
			
			break;

		default:
			break;
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
}

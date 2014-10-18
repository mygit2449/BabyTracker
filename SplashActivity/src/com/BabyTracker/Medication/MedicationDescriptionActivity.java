package com.BabyTracker.Medication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class MedicationDescriptionActivity extends Activity implements OnClickListener{

	private TextView mDosage_Txt,  mMedicineNames_Txt, mDescription_Txt, mMedicationType_Txt, mMedicationDuration_Txt;

	private Intent receiverIntent = null;
	
	private String duration, dosage, medication_type, medicine_name, medication_description, withoutLastcomma;

	private BabyTrackerDataBaseHelper mDataBaseHelper;
	private static final int EDIT_ITEM = 1;
	private Button mEdit_Btn;

	private int medication_id;
	private RelativeLayout _navRel;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medication_details);
		initialzeUI();
		receiverIntent = getIntent();
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(MedicationDescriptionActivity.this);
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		try
		{
			mDataBaseHelper.createDataBase();
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
		
		duration = receiverIntent.getExtras().getString("time");
		dosage = receiverIntent.getExtras().getString("dosage");
		medication_type = receiverIntent.getExtras().getString("medication_type");
		medicine_name = receiverIntent.getExtras().getString("medicinename");
		medication_description = receiverIntent.getExtras().getString("description");
		medication_id = receiverIntent.getExtras().getInt("medication_id");
		
		withoutLastcomma = medicine_name.substring(0, medicine_name.length() - 1);
		mMedicationType_Txt.setText(medication_type);
		mDosage_Txt.setText(dosage);
		mMedicineNames_Txt.setText(withoutLastcomma);
		mMedicationDuration_Txt.setText(duration);
		mDescription_Txt.setText(medication_description);

	}
	
	
	
	/* Initializing UI Here */
	public void initialzeUI()
	{
		
		mDosage_Txt = (TextView)findViewById(R.id.medication_details_TXT_dosage_display);
		mMedicineNames_Txt = (TextView)findViewById(R.id.medication_details_TXT_medicinename_display);
		mDescription_Txt = (TextView)findViewById(R.id.medication_details_TXT_note_display);
		mMedicationType_Txt = (TextView)findViewById(R.id.medication_details_TXT_medicationtype_display);
		mMedicationDuration_Txt = (TextView)findViewById(R.id.medication_details_TXT_duration_display);
		
		mEdit_Btn = (Button)findViewById(R.id.medication_details_BTN_edit);
		mEdit_Btn.setOnClickListener(this);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mDataBaseHelper.close();
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
	        		
	        		duration = data.getExtras().getString("time");
	        		dosage = data.getExtras().getString("dosage");
	        		medication_type = data.getExtras().getString("medication_type");
	        		medicine_name = data.getExtras().getString("medicinename");
	        		medication_description = data.getExtras().getString("description");
	    			
	        		withoutLastcomma = medicine_name.substring(0, medicine_name.length() - 1);
	        		mMedicationType_Txt.setText(medication_type);
	        		mDosage_Txt.setText(dosage);
	        		mMedicineNames_Txt.setText(withoutLastcomma);
	        		mMedicationDuration_Txt.setText(duration);
	        		mDescription_Txt.setText(medication_description);
				}
	        }
	    }
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) 
		{
		
		case R.id.medication_details_BTN_edit:
			
			intent = new Intent(MedicationDescriptionActivity.this, MedicationActivity.class);
			intent.setAction("edit");
			intent.putExtra("medication_id", medication_id);
			startActivityForResult(intent, EDIT_ITEM);
			
			break;

		default:
			break;
		}
	}
}

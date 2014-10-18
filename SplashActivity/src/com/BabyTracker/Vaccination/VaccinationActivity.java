package com.BabyTracker.Vaccination;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.Model.VaccinationModel;

public class VaccinationActivity extends Activity implements OnClickListener, OnItemClickListener{

	ArrayList<VaccinationModel>  mCVaccinations, mVaccinationSortedNames;

	private RadioButton mRadio_Pre, mRadio_Post;
	
	private VaccinationAdapter mVaccinationAdapter = null; 
	private ListView mVaccinationList;
	private TextView  mVaccination_details_title;
	private SharedPreferences mSharedPreferences;
	
	private int baby_age;
	
	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	
	private int baby_id;
	private String vaccination_type = "";

	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccination);
		
		initializeUI();
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
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(this);		
	    mDataBaseHelper.openDataBase();
	    
		mSharedPreferences = getSharedPreferences("BabyTracker",MODE_WORLD_READABLE);

	    baby_id = mSharedPreferences.getInt("baby_id", 0);

		mVaccinationSortedNames = new ArrayList<VaccinationModel>();
		
		mSharedPreferences = getSharedPreferences("BabyTracker",MODE_WORLD_READABLE);

		baby_age = mSharedPreferences.getInt("baby_age", 0);

		mVaccinationSortedNames = mDataBaseHelper.getVaccinationsByage(baby_age, baby_id);
		
		mVaccinationAdapter = new VaccinationAdapter(VaccinationActivity.this, mVaccinationSortedNames,0);
		mVaccinationList.setAdapter(mVaccinationAdapter);
		
		Log.v(getClass().getSimpleName(), "mVaccinationSortedNames "+mVaccinationSortedNames.size());
		
	}

	public void initializeUI()
	{
		
		vaccination_type = "pending";
		
		mVaccinationList = (ListView)findViewById(R.id.vaccination_listview);
		mVaccinationList.setOnItemClickListener(this);
		
		mRadio_Post = (RadioButton)findViewById(R.id.vaccination_radio_post);
		mRadio_Pre = (RadioButton)findViewById(R.id.vaccination_radio_pre);
		
		mRadio_Post.setChecked(true);
		
		mRadio_Post.setOnClickListener(this);
		mRadio_Pre.setOnClickListener(this);
		
		mVaccination_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mVaccination_details_title.setText("Vaccination");
	}
	
	
	
	public class VaccinationAdapter extends BaseAdapter{

		@SuppressWarnings("unused")
		private Context mContext;
		private ArrayList<VaccinationModel> mVaccinations;
		private int mType;
		public VaccinationAdapter(Context context, ArrayList<VaccinationModel> arrItems, int type){
			
			this.mVaccinations = arrItems;
			this.mContext = context;
			this.mType=type;
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return mVaccinations.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			if (convertView == null) 
			{
				LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.vaccination_row, null);
			}
			
			TextView mVaccination_name_txt = (TextView)convertView.findViewById(R.id.vaccination_txt_name);
			
			if (mType == 0) {
				
				RelativeLayout vaccination_background = (RelativeLayout)convertView.findViewById(R.id.vaccination_list_row_REL);
				
				vaccination_background.setBackgroundResource(mVaccinations.get(position).getBackground_resource());
				
				mVaccination_name_txt.setText(mVaccinations.get(position).getVaccination_name());
				
			}else
			{
				mVaccination_name_txt.setText(mVaccinations.get(position).getVaccination_name());
			}
			

			return convertView;
		}
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) 
		{
		
			case R.id.vaccination_radio_post:
				
				vaccination_type = "pending";
				
				mVaccinationAdapter = new VaccinationAdapter(VaccinationActivity.this, mVaccinationSortedNames,0);
				mVaccinationList.setAdapter(mVaccinationAdapter);
				
				break;
				
			case R.id.vaccination_radio_pre:
			
				vaccination_type = "completed";
				
				mCVaccinations = new ArrayList<VaccinationModel>();
				mCVaccinations = mDataBaseHelper.getCompletedVaccinations(baby_id);
				
				mVaccinationAdapter = new VaccinationAdapter(VaccinationActivity.this, mCVaccinations,1);
				mVaccinationList.setAdapter(mVaccinationAdapter);

				Log.v(getClass().getSimpleName(), "pre "+mCVaccinations.size());
				
				break;
		
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		Intent vaccination_intent = new Intent(VaccinationActivity.this, VaccinationDescription.class);
		
		Log.v(getClass().getSimpleName(), "vaccination_type "+vaccination_type);
		if (vaccination_type.equalsIgnoreCase("pending")) 
		{
			
			vaccination_intent.putExtra("vaccination_id", mVaccinationSortedNames.get(arg2).getVaccination_id());
			vaccination_intent.putExtra("vaccination_name", mVaccinationSortedNames.get(arg2).getVaccination_name());
			vaccination_intent.putExtra("vaccination_description", mVaccinationSortedNames.get(arg2).getVaccination_description());
			vaccination_intent.putExtra("vaccination_time", mVaccinationSortedNames.get(arg2).getVaccination_time());
			vaccination_intent.putExtra("vaccination_status", mVaccinationSortedNames.get(arg2).getVaccination_status());
			vaccination_intent.putExtra("for_completed", "pending");
		
		}else 
		{
			
			vaccination_intent.putExtra("vaccination_id", mCVaccinations.get(arg2).getVaccination_id());
			vaccination_intent.putExtra("vaccination_name", mCVaccinations.get(arg2).getVaccination_name());
			vaccination_intent.putExtra("vaccination_description", mCVaccinations.get(arg2).getVaccination_description());
			vaccination_intent.putExtra("vaccination_time", mCVaccinations.get(arg2).getVaccination_time());
			vaccination_intent.putExtra("vaccination_status", mCVaccinations.get(arg2).getVaccination_status());
			vaccination_intent.putExtra("for_completed", "completed_vaccination");
		}
		

		startActivity(vaccination_intent);
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
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mDataBaseHelper.close();
	}
}

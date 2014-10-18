package com.BabyTracker.MedicalHistory;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.Model.MedicalHistoryModel;

public class MedicalHistoryActivity extends Activity implements OnClickListener{


	private static String LOG_TAG = MedicalHistoryActivity.class.getSimpleName();

	private final int REQUEST_CODE_MEDICALHISTORY = 0;
	private final int REQUEST_CODE_MEDICALHISTORY_ADD = 1;
	
	private Button mAddrecord_Btn;
	private ExpandableListView mMedicalhistory_listview;
	private BabyTrackerDataBaseHelper mBabyTrackerDataBaseHelper = null;
	
	private ArrayList<MedicalHistoryModel> mMedicalhistory_groupItems = null, mSublist = null;
	
	private ArrayList<ArrayList<MedicalHistoryModel>> mMedicalhistory_childItems = null;
	
	private MedicalHistoryExpandableAdapter medicalHistoryExpandableAdapter = null;
	
	private TextView mBabygrowth_details_title;
	
	private DisplayMetrics metrics;
	
	private int width, baby_id;
	
	private Intent intent = null;
	
	private Intent receiverIntent = null;
	private RelativeLayout mNodataLayout;
	private RelativeLayout _navRel;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medicalhistory_listview);
		initializeUI();
		
		receiverIntent = getIntent();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		if (receiverIntent.getAction().equalsIgnoreCase("fromHome")) {
			baby_id = receiverIntent.getExtras().getInt("baby_id");
		}
		
		mBabyTrackerDataBaseHelper = new BabyTrackerDataBaseHelper(MedicalHistoryActivity.this);
		
		try 
		{
			mBabyTrackerDataBaseHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (babyidExisted(baby_id)) {
			settingMedicalHistory(baby_id);
		}else{
			intent = new Intent(MedicalHistoryActivity.this, MedicalHistoryDetails.class);
			intent.setAction("fromList");
			intent.putExtra("baby_id", baby_id);
			startActivityForResult(intent, REQUEST_CODE_MEDICALHISTORY);
		}
		
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (babyidExisted(baby_id)) 
			mNodataLayout.setVisibility(View.INVISIBLE);
		else
			mNodataLayout.setVisibility(View.VISIBLE);
		
	}
	/**
	 *  Getting medical history details from database.
	 *  Setting to Expandable list view.
	 * @param baby_id
	 */
	private void settingMedicalHistory(int baby_id) {

		mSublist = new ArrayList<MedicalHistoryModel>();
		mMedicalhistory_childItems = new ArrayList<ArrayList<MedicalHistoryModel>>();
		mMedicalhistory_groupItems = mBabyTrackerDataBaseHelper.getDateAndDoctorname(baby_id);
		
		for (int iCtr = 0; iCtr < mMedicalhistory_groupItems.size(); iCtr++) 
		{
			String chile_baby_id = mMedicalhistory_groupItems.get(iCtr).getDoctor_name();
			mSublist = mBabyTrackerDataBaseHelper.getRemarksAndPurposefromDb(chile_baby_id);
			mMedicalhistory_childItems.add(mSublist);
		}
		
		metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        mMedicalhistory_listview.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));
	
        medicalHistoryExpandableAdapter =new  MedicalHistoryExpandableAdapter(MedicalHistoryActivity.this, mMedicalhistory_groupItems, mMedicalhistory_childItems);
		mMedicalhistory_listview.setAdapter(medicalHistoryExpandableAdapter);
	}


	public int GetDipsFromPixel(float pixels)
    {
     // Get the screen's density scale
     final float scale = getResources().getDisplayMetrics().density;
     // Convert the dps to pixels, based on density scale
     return (int) (pixels * scale + 0.5f);
    }
	
	/* Initializing UI Here */
	private void initializeUI(){
		
		mAddrecord_Btn = (Button)findViewById(R.id.medicalhistory_BTN_add);
		mAddrecord_Btn.setOnClickListener(this);
		
		mMedicalhistory_listview = (ExpandableListView)findViewById(R.id.medicalhistory_listView);
		
		mBabygrowth_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Medical History");
		
		mNodataLayout = (RelativeLayout)findViewById(R.id.medicalhistory_display_REL_nodata);
	}

	public void onClick(View v) {
	
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.medicalhistory_BTN_add:
			intent = new Intent(MedicalHistoryActivity.this, MedicalHistoryDetails.class);
			intent.setAction("fromList");
			intent.putExtra("baby_id", baby_id);
			startActivityForResult(intent, REQUEST_CODE_MEDICALHISTORY_ADD);
			break;

		default:
			break;
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) 
		{
		
		case REQUEST_CODE_MEDICALHISTORY_ADD:
			
			if (resultCode == RESULT_OK) {
			
				Log.v(LOG_TAG, " medical history id "+data.getExtras().getInt("appointment_id"));
				baby_id = data.getExtras().getInt("appointment_id");
				settingMedicalHistory(baby_id);
				
			}
			break;

		case REQUEST_CODE_MEDICALHISTORY:
			
			if (resultCode == RESULT_OK) {
			
				Log.v(LOG_TAG, " medical history id "+data.getExtras().getInt("appointment_id"));
				baby_id = data.getExtras().getInt("appointment_id");
				settingMedicalHistory(baby_id);
				
			}
			break;

		default:
			break;
		}
	}


	public class MedicalHistoryExpandableAdapter extends BaseExpandableListAdapter{

		private ArrayList<MedicalHistoryModel> mGroupItem;
		private ArrayList<ArrayList<MedicalHistoryModel>> mChildItems;
		private Context mContext;
		
		public MedicalHistoryExpandableAdapter(Context context, ArrayList<MedicalHistoryModel> groupItems, ArrayList<ArrayList<MedicalHistoryModel>> childItems){
			this.mContext = context;
			this.mGroupItem = groupItems;
			this.mChildItems = childItems;
		}
		
		@Override
        public boolean areAllItemsEnabled()
        {
            return true;
        }
		
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return  mChildItems.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mChildItems.get(groupPosition).size();
		}
		
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			
			MedicalHistoryModel child_item = (MedicalHistoryModel) getChild(groupPosition, childPosition);
			
			if (convertView == null) {
			    LayoutInflater inflater =  (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    convertView = inflater.inflate(R.layout.medicalhistory_childlayout, null);
			   }
			
			TextView mPurpose_Txt = (TextView)convertView.findViewById(R.id.medicalhistory_childlayout_TXT_purposedetails);
			TextView mRemarksTxt = (TextView)convertView.findViewById(R.id.medicalhistory_childlayout_TXT_remarksdetails);
			TextView mNoteTxt = (TextView)convertView.findViewById(R.id.medicalhistory_childlayout_TXT_notedetails);
			
			mPurpose_Txt.setText(child_item.getPurpose());
			mRemarksTxt.setText(child_item.getRemarks());
			mNoteTxt.setText(child_item.getNote());
			
			return convertView;
		}

		

		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mGroupItem.get(groupPosition);
		}

		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mGroupItem.size();
		}

		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			if (convertView == null) {
	            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = infalInflater.inflate(R.layout.medicalhistory_grouplayout, null);
	        }

			TextView mDateofvisit_Txt = (TextView)convertView.findViewById(R.id.medicalhistory_grouplayout_TXT_displaydate);
			TextView mDoctorname = (TextView)convertView.findViewById(R.id.medicalhistory_grouplayout_TXT_displaypurpose);
			
			mDateofvisit_Txt.setText(mGroupItem.get(groupPosition).getDateofvisit());
			mDoctorname.setText(mGroupItem.get(groupPosition).getDoctor_name());
			return convertView;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
	
	/* checking baby is existed in data base or not */
	public boolean babyidExisted(int baby_id)
	{
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.MEDICALHISTORY_TABLE+" where "+BabyTrackerDataBaseHelper.APPOINTMENT_BABY_ID+" = "+ baby_id);
		Log.v(LOG_TAG, " growth existance query "+query);
		Cursor mCursor = mBabyTrackerDataBaseHelper.select(query);
		
		try
		{
			return mCursor.getCount() > 0 ? true : false;
		
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
			mCursor.close();
		}
		
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBabyTrackerDataBaseHelper.close();
	}
}

package com.BabyTracker.DailyActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.Model.DailyActivityModel;
import com.BabyTracker.Model.DailyChildModel;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class DailyActivityList extends Activity{


	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	
	private ExpandableListView mDailyActivitiesList;
	
	ArrayList<DailyActivityModel> mTotalActivities = null, mSublist, mDailyActivityDates, finalList, mExpDetails, mGroupDetails, mItemsList, mCheckSubList;
	
	private ArrayList<ArrayList<DailyActivityModel>>  mChilditems = null;;
	
	private TextView mBabygrowth_details_title;
	
	private Intent receiverIntent;
	private int baby_id, width;
	
	private String mTitle = "BabyTracker";
	private String mMessage = "No Activity Details Found.";
	private DisplayMetrics metrics;
	
	private DailyActivityModel mDailyActivityModel = null;
	
	private DailyActivityExpandableAdapter mDailyActivityExpandableAdapter = null;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dailyactivity_list);
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
		mTotalActivities = new ArrayList<DailyActivityModel>();
		finalList = new ArrayList<DailyActivityModel>();
		
		mItemsList = new ArrayList<DailyActivityModel>();
		
		mExpDetails = new ArrayList<DailyActivityModel>();
		
		mGroupDetails = new ArrayList<DailyActivityModel>();
		
		mCheckSubList = new ArrayList<DailyActivityModel>();
		
		mChilditems = new ArrayList<ArrayList<DailyActivityModel>>();
		
		mDailyActivityDates = new ArrayList<DailyActivityModel>();
		
		receiverIntent = getIntent();
		
		if (receiverIntent.getAction().equalsIgnoreCase("fromDetails")) {
			baby_id = receiverIntent.getExtras().getInt("baby_id");
		}
		
		mDailyActivityModel = new DailyActivityModel();
		
		settingDailyActivityDetails();	
		
	}

	/* Initializing UI Here */
	public void initializeUI()
	{
		mBabygrowth_details_title = (TextView)findViewById(R.id.simple_top_bar_Txt_title);
		mBabygrowth_details_title.setText("Daily Activity");
		mDailyActivitiesList = (ExpandableListView)findViewById(R.id.activities_list);
		
		metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        mDailyActivitiesList.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));
        
	}
		
	/**
	 *  Getting daily activity details from database.
	 *  Setting to Expandable list view.
	 * @param baby_id
	 */

	public int GetDipsFromPixel(float pixels)
    {
     // Get the screen's density scale
     final float scale = getResources().getDisplayMetrics().density;
     // Convert the dps to pixels, based on density scale
     return (int) (pixels * scale + 0.5f);
    }
	
	/**
	 * Getting the details from the database and set them to the expandable listview.
	 */
	public void settingDailyActivityDetails()
	{
		
		mExpDetails = mDataBaseHelper.getDailyActivityDetails(baby_id);
	
		if(mExpDetails.size() == 0)
		{
			alertDialogWithMessage(mTitle, mMessage);
		}else
		{
				
			int milkCount = 0, iCtr = 0, diper_status = 0, feedCount = 0, milkQuantity = 0, sleepinghours = 0;
		
			String recordDate = "";
		
		
			String mAppointmentDate_Str = mExpDetails.get(iCtr).getDailyactivity_date();
	
			for (int iCheck = 0; iCheck <	mExpDetails.size(); iCheck++) 
			{
				
				String mTempDate = mExpDetails.get(iCheck).getDailyactivity_date();
				
				recordDate = mExpDetails.get(iCheck).getActivityDate();
				milkQuantity = mExpDetails.get(iCheck).getMilk_quantity();
				
				Log.v(getClass().getSimpleName(), " r date "+recordDate);
				
				if(mExpDetails.get(iCheck).getMilk_quantity() > 0)
					feedCount++;
				
				milkCount += mExpDetails.get(iCheck).getMilk_quantity();
				sleepinghours += mExpDetails.get(iCheck).getSleepingHours();
				diper_status += mExpDetails.get(iCheck).getDiper_status_change();
				
				if (mAppointmentDate_Str.equals(mTempDate)) 
				{
					
					mDailyActivityModel.setTotalMilkCount(milkCount);
					mDailyActivityModel.setDiperstatus(diper_status);
					mDailyActivityModel.setFeedCount(feedCount);
					mDailyActivityModel.setDailyactivity_date(mTempDate);
					mDailyActivityModel.setActivityDate(recordDate);
					mDailyActivityModel.setMilk_quantity(milkQuantity);
					mDailyActivityModel.setSleepingHours(sleepinghours);
					
				}else{
					
						mItemsList.add(mDailyActivityModel);
						
						mDailyActivityModel = null;
						
						mDailyActivityModel = new DailyActivityModel();
						mDailyActivityModel.mChildItems = new ArrayList<DailyChildModel>();
	
						mGroupDetails.addAll(mItemsList);
						mItemsList.clear();
						
						milkCount = 0;
						diper_status = 0;
						feedCount = 0;
						milkQuantity = 0;
						sleepinghours = 0;
						
						if(mExpDetails.get(iCheck).getMilk_quantity() > 0)
							feedCount++;
						
						milkCount += mExpDetails.get(iCheck).getMilk_quantity();
						diper_status += mExpDetails.get(iCheck).getDiper_status_change();
						sleepinghours += mExpDetails.get(iCheck).getSleepingHours();

						mDailyActivityModel.setTotalMilkCount(milkCount);
						mDailyActivityModel.setDiperstatus(diper_status);
						mDailyActivityModel.setFeedCount(feedCount);
						mDailyActivityModel.setDailyactivity_date(mTempDate);
						mDailyActivityModel.setActivityDate(recordDate);
						mDailyActivityModel.setMilk_quantity(milkQuantity);
						
						mAppointmentDate_Str = mTempDate;
						
				}
				
			}
	
			mItemsList.add(mDailyActivityModel);
			mGroupDetails.addAll(mItemsList);
			mItemsList.clear();
	
			if (mGroupDetails.size() > 0) 
			{
				
				for (int iChild = 0; iChild < mGroupDetails.size(); iChild++) 
				{
					
					String chile_baby_date = mGroupDetails.get(iChild).getDailyactivity_date();
					Log.v(getClass().getSimpleName(), "date "+chile_baby_date);
					mCheckSubList = mDataBaseHelper.getDailyActivityResult(chile_baby_date, baby_id);
					mChilditems.add(mCheckSubList);
					
				}
			
				Log.v(getClass().getSimpleName(), "mCheckSubList size "+mChilditems.size());
				
				mDailyActivityExpandableAdapter = new DailyActivityExpandableAdapter(DailyActivityList.this, mGroupDetails, mChilditems);
			    mDailyActivitiesList.setAdapter(mDailyActivityExpandableAdapter);
	
			}
			
		}// else for the details available or not..
		
	}
	
	
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(this, title, msg).setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		}).setIcon(android.R.drawable.ic_dialog_info)
		.create().show();
	}
	
	public class DailyActivityExpandableAdapter extends BaseExpandableListAdapter{

		private ArrayList<DailyActivityModel> mGroupItem;
		private ArrayList<ArrayList<DailyActivityModel>> mChildItems;
		private Context mContext;
		
		public DailyActivityExpandableAdapter(Context context, ArrayList<DailyActivityModel> groupItems, ArrayList<ArrayList<DailyActivityModel>> childItems){
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
			
			
			DailyActivityModel child_item = (DailyActivityModel) getChild(groupPosition, childPosition);
			
			if (convertView == null) {
			    LayoutInflater inflater =  (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    convertView = inflater.inflate(R.layout.dailyactivity_childlayout, null);
			   }
			
			TextView mDiperStatus_Txt = (TextView)convertView.findViewById(R.id.dailyactivity_childlayout_TXT_diaperstatusdetails);
			TextView mMilkQuantityTxt = (TextView)convertView.findViewById(R.id.dailyactivity_childlayout_TXT_milkdetails);
			TextView mSleepingHoursTxt = (TextView)convertView.findViewById(R.id.dailyactivity_childlayout_TXT_sleepinghoursdetails);
			TextView mTimeTxt = (TextView)convertView.findViewById(R.id.dailyactivity_childlayout_TXT_timedetails);
			
			if(child_item.getDiper_status_change() == 1)
			    mDiperStatus_Txt.setText("Yes");
			else
				mDiperStatus_Txt.setText("No");
			
			if(child_item.getMilk_quantity() == 0)
				mMilkQuantityTxt.setText("No Feed");
			else
				mMilkQuantityTxt.setText(""+child_item.getMilk_quantity()+" ml");
			
			
			if(child_item.getSleepingHours() == 0)
				mSleepingHoursTxt.setText("0 hours");
			else
				mSleepingHoursTxt.setText(""+child_item.getSleepingHours()+" Hours");
			
			SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			SimpleDateFormat mDateFormat1 = new SimpleDateFormat("hh:mm:ss a");

		    Log.v(getClass().getSimpleName(), "mCheckSubList date "+child_item.getActivityDate());

		    
			String mAppointmentDate_Str = child_item.getActivityDate();
			
			Date mDate = null;
			try 
			{
				mDate = mDateFormat.parse(mAppointmentDate_Str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mTimeTxt.setText(""+mDateFormat1.format(mDate));
			
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
			
			if(convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.dailyactivity_list_row, null);
			}
			
			TextView mDiperStatus_Txt = (TextView)convertView.findViewById(R.id.dailyactivity_list_row_TXT_diperStatus);
			TextView mFeedcount_Txt = (TextView)convertView.findViewById(R.id.dailyactivity_list_row_TXT_feedcountStatus);
			TextView mMilkQty_Txt = (TextView)convertView.findViewById(R.id.dailyactivity_list_row_TXT_MilkQtyStatus);
			TextView mSleepinghours_Txt = (TextView)convertView.findViewById(R.id.dailyactivity_list_row_TXT_sleepinghoursStatus);
			TextView mActivityDate_Txt = (TextView)convertView.findViewById(R.id.dailyactivity_list_row_TXT_activityDateDisplay);
			
			if(mGroupItem.get(groupPosition).getDiperstatus() == 1)
			    mDiperStatus_Txt.setText(""+mGroupItem.get(groupPosition).getDiperstatus()+" time");
			else
				 mDiperStatus_Txt.setText(""+mGroupItem.get(groupPosition).getDiperstatus()+" times");
			
			mMilkQty_Txt.setText(""+mGroupItem.get(groupPosition).getTotalMilkCount()+" ml");
			
			if(mGroupItem.get(groupPosition).getMilk_quantity() == 1)
			    mFeedcount_Txt.setText(""+mGroupItem.get(groupPosition).getFeedCount()+" time");
			else
				mFeedcount_Txt.setText(""+mGroupItem.get(groupPosition).getFeedCount()+" times");
			
			if(mGroupItem.get(groupPosition).getSleepingHours() == 1)
				mSleepinghours_Txt.setText(""+mGroupItem.get(groupPosition).getSleepingHours()+" Hour");
			else
				mSleepinghours_Txt.setText(""+mGroupItem.get(groupPosition).getSleepingHours()+" Hours");
			
			String mAppointmentDate_Str = mGroupItem.get(groupPosition).getDailyactivity_date();

			mActivityDate_Txt.setText(mAppointmentDate_Str);
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDataBaseHelper.close();
	}
	
}

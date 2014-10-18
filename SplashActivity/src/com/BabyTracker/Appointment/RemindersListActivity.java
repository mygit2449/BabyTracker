package com.BabyTracker.Appointment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;

public class RemindersListActivity extends Activity implements OnClickListener, OnItemClickListener{

	private BabyTrackerDataBaseHelper mDataBaseHelper = null;

	private ListView mRemindersListview;
	private RelativeLayout mNodataAvailableLayout;

	private Button mAddReminder_Btn;
	
	private static final int ADD_REMINDER = 1;
	final int CONTEXT_MENU_DELETE_ITEM =1;
	private String query, mCurrentDate_Str;
	private Cursor mCursor = null;
	private Date mCurrentDate = null;
	private RemindersCursorAdapter mRCursorAdapter = null;
	SimpleDateFormat mDateFormat_reminder;
	private RelativeLayout _navRel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminders_listview);
		
		initializeUI();
		
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(this);		
	    mDataBaseHelper.openDataBase();
		
		mDateFormat_reminder = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	    mCurrentDate = new Date();
		mCurrentDate_Str = mDateFormat_reminder.format(mCurrentDate);
		
		loadList();
	    
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mCursor.requery();
		if (mCursor.getCount() > 0)
			mNodataAvailableLayout.setVisibility(View.INVISIBLE);
		else 
			mNodataAvailableLayout.setVisibility(View.VISIBLE);
	}
	private void loadList(){
		// Query for getting only future reminders...
	    query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.APPOINTMENT_TABLE+" where "
	    		+BabyTrackerDataBaseHelper.APPOINTMENT_REMINDER_TIME+" > '"+mCurrentDate_Str+"'"+" order by "+BabyTrackerDataBaseHelper.APPOINTMENT_REMINDER_TIME);
	    Log.v(getClass().getSimpleName(), " reminder query "+query);
	    mCursor = mDataBaseHelper.select(query);
	   
		mRCursorAdapter = new  RemindersCursorAdapter(RemindersListActivity.this, mCursor);
		Log.v(getClass().getSimpleName(), "query "+query);
	    mRemindersListview.setAdapter(mRCursorAdapter);
	}
	
	public void initializeUI()
	{
		
	    mRemindersListview = (ListView)findViewById(R.id.reminders_listview);

	    registerForContextMenu(mRemindersListview);
	    mNodataAvailableLayout = (RelativeLayout)findViewById(R.id.reminders_display_REL_nodata);
	    mAddReminder_Btn = (Button)findViewById(R.id.reminders_BTN_add);
	    mAddReminder_Btn.setOnClickListener(this);
	    mRemindersListview.setOnItemClickListener(this);

	}

	@Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
	  menu.add(Menu.NONE, CONTEXT_MENU_DELETE_ITEM, Menu.NONE, "Delete");
	  Log.v(getClass().getSimpleName(), " view id  "+v.getTag());
	 }
	
	
	@Override
	 public boolean onContextItemSelected(MenuItem item) {
	 
	      AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	      
	      Long id = mRemindersListview.getAdapter().getItemId(info.position);
	      Log.v(getClass().getSimpleName(), "id  "+id);
	      
		      switch (item.getItemId()) 
		      {
		      
	              case CONTEXT_MENU_DELETE_ITEM:
	            	  
	            	  String query="delete from appointment where _id='"+id+"'";
            		  mDataBaseHelper.delete(query);//Deleting the Record from the data base based on the id.
                	  Log.v(getClass().getSimpleName(), "on delete click"+id);
                	 // mCursor.requery();
                	  loadList();
	          
	               return(true);
		              
		      }
   	 
	  return(super.onOptionsItemSelected(item));
	  
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
        super.onActivityResult(requestCode, resultCode, data);   
        switch(requestCode) 
        {  
	        case ADD_REMINDER:  
	        
	        	if (resultCode == RESULT_OK) 
	        	{
	        		mCursor.requery();
	        		Log.v(getClass().getSimpleName(), "on activity result ");
				}
	        break;
	    }
    }
	
	
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) 
	{

		mCursor.moveToPosition(position);
		int reminder_status = mCursor.getInt(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_IS_REMINDER_SET));
		
		if (reminder_status == 0)
		{
			int appointment_baby_id = mCursor.getInt(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_ID));
			Intent intent = new Intent(RemindersListActivity.this, DoctorAppointmentDetailsActivity.class);
			intent.setAction("edit");
			intent.putExtra("baby_id",appointment_baby_id);
			startActivity(intent);
			Log.v(getClass().getSimpleName(), "this is doctor appointment");
		}else{
			
			int appointment_id = mCursor.getInt(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_ID));
			Intent reminder_intent = new Intent(RemindersListActivity.this, ReminderDescriptionActivity.class);
		
			reminder_intent.putExtra("appointment_id", appointment_id);
			startActivity(reminder_intent);
			Log.v(getClass().getSimpleName(), "this is general reminder");
			
		}
		
	}

	
	/**
	 * Cursor Adapter for displaying the data on list view..
	 * @author android
	 *
	 */
	public class RemindersCursorAdapter extends CursorAdapter{

		@SuppressWarnings("unused")
		private Cursor mCursor;
		@SuppressWarnings("unused")
		private Context mContext;
		private final LayoutInflater mInflater;

		Date babydate = null;
		
		public RemindersCursorAdapter(Context context, Cursor c) {
			super(context,c);
			// TODO Auto-generated constructor stub
			mInflater=LayoutInflater.from(context);
		    mContext=context;
		    mCursor = c;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			TextView mAppointment_type_txt = (TextView)view.findViewById(R.id.reminders_list_row_TXT_reminderType);
			TextView mAppointment_at_txt = (TextView)view.findViewById(R.id.reminders_list_row_TXT_datetime);
			int reminder_type = cursor.getInt(cursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_IS_REMINDER_SET));
			Log.v(getClass().getSimpleName(), "type "+reminder_type);
			
			if(reminder_type == 1)
				mAppointment_type_txt.setText("General Reminder");
			else
				mAppointment_type_txt.setText("Doctor Appointment");

			mAppointment_at_txt.setText(cursor.getString(cursor.getColumnIndex(BabyTrackerDataBaseHelper.APPOINTMENT_TIME)));

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			final View view=mInflater.inflate(R.layout.reminders_list,parent,false); 
	        return view;

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
		case R.id.reminders_BTN_add:
			
			startActivityForResult(new Intent(RemindersListActivity.this, AddReminderActivity.class).setAction("fromlist"), ADD_REMINDER);
			
			break;

		default:
			break;
		}
	}
}

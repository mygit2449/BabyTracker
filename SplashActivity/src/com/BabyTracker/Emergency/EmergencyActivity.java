package com.BabyTracker.Emergency;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BabyTracker.R;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class EmergencyActivity extends Activity implements OnClickListener{

	final int CONTEXT_MENU_DELETE_ITEM =1;
	
	private Dialog dialog;
	
	private Context mContext;
	private AlertDialog alertDialog;
	
	private Button mAddEmergency_btn, mSave_btn, mCancel_btn;
	
	private EditText mContactname_Edt, mContactnumber_Edt;
	
	private Cursor mCursor = null;
	private ListView emergency_listview;

	private String query;

	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	private EmergencyCursorAdapter mEmergencyCursorAdapter = null;
	private RelativeLayout _navRel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emergency);
		initializeUI();
		
		mContext = getApplicationContext();
		_navRel = (RelativeLayout)findViewById(R.id.nav_rel);

		_navRel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(EmergencyActivity.this);
		mDataBaseHelper.openDataBase();
		
		query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.EMERGENCY_TABLE);
		mCursor = mDataBaseHelper.select(query);
		
		mEmergencyCursorAdapter = new EmergencyCursorAdapter(this, mCursor);
		emergency_listview.setAdapter(mEmergencyCursorAdapter);
	
		registerForContextMenu(emergency_listview);
	}
	
	@Override
	public void onResume()
	{
		
		super.onResume();
		mCursor.requery();
		mEmergencyCursorAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
	  menu.add(Menu.NONE, CONTEXT_MENU_DELETE_ITEM, Menu.NONE, "Delete");
	  Log.v(getClass().getSimpleName(), " view id  "+v.getTag());
	 }
	
	@Override
	 public boolean onContextItemSelected(MenuItem item) {
	 
	      AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	      
	      Long id = emergency_listview.getAdapter().getItemId(info.position);
	      Log.v(getClass().getSimpleName(), "id  "+id);
	      
	      if (id != 0) 
    	  {
	    	  
		      switch (item.getItemId()) 
		      {
		      
	              case CONTEXT_MENU_DELETE_ITEM:
	            	  
	            		  mDataBaseHelper.deleteContact(id);//Deleting the Record from the data base based on the id.
	                	  Log.v(getClass().getSimpleName(), "on delete click"+id);
	                	  mCursor.requery();
	                	  
	                	  return(true);
		              
		      }
		      
    	  }else {
				alertDialogWithMessage("Baby Tracker", "Deletion of this contact is not posible");
			}
	  return(super.onOptionsItemSelected(item));
	  
	}
	
	/**
	 * Initializing the user interface.
	 */
	public void initializeUI()
	{
		
		emergency_listview = (ListView)findViewById(R.id.emergency_listView);
		mAddEmergency_btn = (Button)findViewById(R.id.emergency_BTN_add);
		mAddEmergency_btn.setOnClickListener(this);
		
	}

	/**
	 * Cursor Adapter for displaying the data on list view..
	 * @author android
	 *
	 */
	public class EmergencyCursorAdapter extends CursorAdapter{

		@SuppressWarnings("unused")
		private Cursor mCursor;
		@SuppressWarnings("unused")
		private Context mContext;
		private final LayoutInflater mInflater;

		Date babydate = null;
		
		public EmergencyCursorAdapter(Context context, Cursor c) {
			super(context,c);
			// TODO Auto-generated constructor stub
			mInflater=LayoutInflater.from(context);
		    mContext=context;
		    mCursor = c;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			TextView mContact_name = (TextView)view.findViewById(R.id.emergency_Txt_contact_name);
			TextView mContact_number = (TextView)view.findViewById(R.id.emergency_Txt_contact_number);
			
			mContact_name.setText(cursor.getString(cursor.getColumnIndex(BabyTrackerDataBaseHelper.EMERGENCY_NAME)));
			
			final String contact_number = cursor.getString(cursor.getColumnIndex(BabyTrackerDataBaseHelper.EMERGENCY_NUMBER));
			mContact_number.setText(contact_number);
			Log.v(getClass().getSimpleName(), "contact_number "+contact_number); 

			ImageView mPhoneCall_Img = (ImageView)view.findViewById(R.id.emergency_dailer_Img);
			
			mPhoneCall_Img.setOnClickListener(new OnClickListener() 
			{
				
				public void onClick(View v)
				{
					 // TODO Auto-generated method stub
					 TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
					
					 if(telephonyManager.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE)
					 {
					  //coming here if Tablet 
					  Toast.makeText(EmergencyActivity.this, "TELEPHONY_SERVICE feature is not available in this device", Toast.LENGTH_SHORT).show();
					 }
					 else
					 {
					  //coming here if phone
						String uri = "tel:" + contact_number.trim() ;
					    Intent intent = new Intent(Intent.ACTION_CALL);
					    intent.setData(Uri.parse(uri));
					    startActivity(intent);
						Log.v(getClass().getSimpleName(), " image click "+contact_number);
					 }

				}
			});

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			final View view=mInflater.inflate(R.layout.emergency_list_row,parent,false); 
	        return view;

		} 
		
	}
	
	public void alertDialogWithMessage(String title, String msg) {

		new BabyTrackerAlert(EmergencyActivity.this, title, msg)
				.setPositiveButton("Ok",
						new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// finish();
								mContactnumber_Edt.setText(""); 
							}
						}).create().show();
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
		
			case R.id.emergency_BTN_add:
				
				Log.v(getClass().getSimpleName(), "on + click");
				AlertDialog.Builder builder = null;
				dialog = new Dialog(mContext);
			    dialog.setContentView(R.layout.add_emergency);
			    
				Context mContext = getApplicationContext();
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.add_emergency, (ViewGroup) findViewById(R.id.add_emergency_Rel));

				builder = new AlertDialog.Builder(this);
				builder.setTitle("Emergency Contact");
				builder.setView(layout);
				
				mContactname_Edt = (EditText)layout.findViewById(R.id.add_emergency_edt_contact_name);
				mContactnumber_Edt = (EditText)layout.findViewById(R.id.add_emergency_edt_number);
				
				mSave_btn = (Button)layout.findViewById(R.id.add_emergency_Btn_ok);
				mCancel_btn= (Button)layout.findViewById(R.id.add_emergency_Btn_cancel);
				
				
				mSave_btn.setOnClickListener(this);
				mCancel_btn.setOnClickListener(this);
				
				alertDialog = builder.show();
				
				break;
	
			case R.id.add_emergency_Btn_ok:
				
					long insertCheck = 0;
					
					Log.v(getClass().getSimpleName(), "mContactname_Edt "+mContactname_Edt.getText().toString()); 
					
					if (!mContactname_Edt.getText().toString().equalsIgnoreCase("") && !mContactnumber_Edt.getText().toString().equalsIgnoreCase("") && mContactnumber_Edt.getText().length() <= 10)
						insertCheck = mDataBaseHelper.insertEmergencydetails(mContactname_Edt.getText().toString(), mContactnumber_Edt.getText().toString());
					else
						alertDialogWithMessage("BabyTracker", "Please enter valid information");
					
					if (insertCheck > 0)
					{
						Toast.makeText(this, "Emergence Contact details are inserted successfully ", Toast.LENGTH_SHORT).show();
						mCursor.requery();
						mEmergencyCursorAdapter.notifyDataSetChanged();
						alertDialog.dismiss();
					}
					
				break;
				
			case R.id.add_emergency_Btn_cancel:
				alertDialog.dismiss();
				break;
				
		}
	}

}

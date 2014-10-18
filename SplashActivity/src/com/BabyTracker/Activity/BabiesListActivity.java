package com.BabyTracker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.BabyProfile.BabyProfileActivity;
import com.BabyTracker.Helper.AgeCalculater;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.Menu.MenuOptionsActivity;

/**
 * This is the class which shows the list of babies those are registered.
 * When you select a particular baby it redirect to home screen.
 * When you long press on list item it gives option to delete baby.
 * @author android
 *
 */
public class BabiesListActivity extends MenuOptionsActivity implements OnItemClickListener{

	private Cursor mCursor = null;
	private ListView babies_listview;
	final int CONTEXT_MENU_DELETE_ITEM = 1;

	private SimpleCursorAdapter mBabiesListAdapter = null;

	private String query;
	private byte imageData[];
	
	private TextView mHeading;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mSharedPreferencesEditor;
	private BabyTrackerDataBaseHelper mDataBaseHelper = null;
	private RelativeLayout _navRel;
	private TextView _TitleTxt;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.babies_listview);
		
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
		
		babies_listview = (ListView)findViewById(R.id.babies_list);
		View footerView = getLayoutInflater().inflate(R.layout.add_baby, null);
		babies_listview.addFooterView(footerView);
		
		mDataBaseHelper = new BabyTrackerDataBaseHelper(BabiesListActivity.this);
		mDataBaseHelper.openDataBase();
		
		query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.PROFILE_TABLE);
		mCursor = mDataBaseHelper.select(query);

		mHeading = (TextView) findViewById(R.id.simple_top_bar_Txt_title);
		mHeading.setText("Baby Tracker");
		
		mSharedPreferences = getSharedPreferences("BabyTracker",MODE_WORLD_READABLE);
		mSharedPreferencesEditor = mSharedPreferences.edit();
		
		String[] from = {BabyTrackerDataBaseHelper.KEY_NAME,BabyTrackerDataBaseHelper.KEY_DOB,BabyTrackerDataBaseHelper.KEY_IMAGE,
				BabyTrackerDataBaseHelper.KEY_BABY_HEIGHT, BabyTrackerDataBaseHelper.KEY_BABY_WEIGHT};
		
		int[] to = {R.id.babies_listview_TXT_babyName,R.id.babies_listview_TXT_dateofbirth,R.id.babies_listview_IMG_baby, R.id.babies_listview_row_TXT_weight, R.id.babies_listview_TXT_babyheight};
		
		mBabiesListAdapter = new SimpleCursorAdapter(this, R.layout.babies_listview_row, mCursor, from, to);		
		mBabiesListAdapter.setViewBinder(VIEW_BINDER);
		babies_listview.setAdapter(mBabiesListAdapter);
		babies_listview.setOnItemClickListener(this);
		
		registerForContextMenu(babies_listview);
		
	}
	
	public final ViewBinder VIEW_BINDER = new ViewBinder() {
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// TODO Auto-generated method stub
		if(columnIndex == cursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_BABY_HEIGHT))
		{
			if(cursor.getFloat(columnIndex) > 0)
			((TextView)view).setText(""+cursor.getFloat(columnIndex)+" Cm"); 
			else{
				((TextView)view).setVisibility(View.GONE);
			}
			return true;
		}else if (columnIndex == cursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_BABY_WEIGHT)) {
			if(cursor.getFloat(columnIndex) > 0)
				((TextView)view).setText(""+cursor.getFloat(columnIndex)+" Kgs"); 
				else{
					((TextView)view).setVisibility(View.GONE);
				}
			return true;
		}else if(columnIndex == cursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_IMAGE)){
				imageData = cursor.getBlob(columnIndex);
				((ImageView)view).setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
				return true;				
			}else if(columnIndex == cursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_DOB)){
				try {					

					((TextView)view).setText(new AgeCalculater().calculateAge(cursor.getString(columnIndex))); 
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
				return true;
			}
			else {
				return false;
			}
		}
	};

	@Override
	public void onResume(){
		super.onResume();
		mCursor.requery();
	}
	
	
	@Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
	  menu.add(Menu.NONE, CONTEXT_MENU_DELETE_ITEM, Menu.NONE, "Delete");
	  Log.v(getClass().getSimpleName(), " view id  "+v.getTag());
	 }
	
	
	@Override
	 public boolean onContextItemSelected(MenuItem item) {
	 
	      AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	      
	      Long id = babies_listview.getAdapter().getItemId(info.position);
	      Log.v(getClass().getSimpleName(), "id  "+id);
	    
	      switch (item.getItemId()) 
	      {
	      
              case CONTEXT_MENU_DELETE_ITEM:

            	  	  mSharedPreferencesEditor.remove("baby_id");
	            	  mSharedPreferencesEditor.commit();
            	  
            		  mDataBaseHelper.deleteBaby(id);//Deleting the Record from the data base based on the id.
            		  mDataBaseHelper.deleteBabyVaccinations(id);//Deleting vaccinations of the baby.
            		  mDataBaseHelper.deleteAppointment(id);//Deleting the appointments of the baby.
            		  
            		  Log.v(getClass().getSimpleName(), "on delete click"+id);
                	  mCursor.requery();
                	  
                	  return(true);
	              
	      }
 
	  return(super.onOptionsItemSelected(item));
	  
	}
	
	// adding new baby.
	public void onfooterClick(View v){
		startActivity(new Intent(BabiesListActivity.this, BabyProfileActivity.class).setAction("fromHome"));
    }	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, Home.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if (mDataBaseHelper != null) 
		{
			
			mDataBaseHelper.close();
		}
		
		if (mCursor != null) {
			mCursor.close();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		mCursor.moveToPosition(position);
		int baby_id = mCursor.getInt(mCursor.getColumnIndex(BabyTrackerDataBaseHelper.KEY_ID));
		Intent intent = new Intent();
		intent.putExtra("baby_id",baby_id);
		setResult(RESULT_OK,intent);
		this.finish();
	}

}

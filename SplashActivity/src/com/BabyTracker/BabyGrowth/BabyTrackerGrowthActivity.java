 package com.BabyTracker.BabyGrowth;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BabyTracker.R;
import com.BabyTracker.Activity.Home;
import com.BabyTracker.Helper.BabyTrackerDataBaseHelper;
import com.BabyTracker.Model.GraphModel;
import com.BabyTracker.dialog.BabyTrackerAlert;

public class BabyTrackerGrowthActivity extends Activity implements android.view.View.OnClickListener{
	
	private static String LOG_TAG = BabyTrackerGrowthActivity.class.getSimpleName();
	
	private final int REQUEST_CODE_GROWTH = 0;
	private final int REQUEST_CODE_ADDGROWTH = 1;
	
	private TextView mBabyheight_txt, mBabyweight_txt, mBabycircumference;
	
	private Button mGrothlist_Btn, mAddGrowth_details_Btn, mGrowth_weight_GBtn, mGrowth_height_GBtn;
	
	private BabyTrackerDataBaseHelper mDataBaseHelper;

	private Intent receiverIntent = null;
	private int baby_id;
	private String  mBaby_name = "";
	
	private String mAlertTitle = "BabyTacker";
	
	private Cursor growthcursor = null;
	
	ArrayList<GraphModel>  mGraph_list = null;
	List<double[]> mGraph_values, mAges_values;
	double[] graph_values, age_values;
	private RelativeLayout _navRel;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.babygrowth_display);
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
        
		receiverIntent = getIntent();
			
		baby_id = receiverIntent.getExtras().getInt("baby_id");
		mBaby_name = receiverIntent.getExtras().getString("baby_name");
		
		Log.v(LOG_TAG, "baby_id "+baby_id);
		
		mAges_values = new ArrayList<double[]>();
		mGraph_list = new ArrayList<GraphModel>();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		/**
		 *  checking baby id is existed or not in data base,
		 *  if not existed it show an alert to enter growth of baby details.
		 */
		if (babyidExisted(baby_id)) 
		{
			getBabygrowthDetails(baby_id);
			getGraphdetails();
		}else 
		{

			Intent intent = new Intent(BabyTrackerGrowthActivity.this,GrowthdetailsActivity.class);
			intent.setAction("fromHome");
			intent.putExtra("baby_name", mBaby_name);
			intent.putExtra("baby_id", baby_id);
			startActivityForResult(intent, REQUEST_CODE_GROWTH);
		}
	}

	/* Initializing UI Here */
	private void initializeUI(){
		
		mGrothlist_Btn = (Button)findViewById(R.id.babygrowth_display_BTN_growthlist);
		mGrothlist_Btn.setOnClickListener(this);
		
		mAddGrowth_details_Btn = (Button)findViewById(R.id.babygrowth_display_BTN_addgrowth);
		mAddGrowth_details_Btn.setOnClickListener(this);
		
		mBabyheight_txt = (TextView)findViewById(R.id.babygrowth_display_TXT_babyheight);
		mBabyweight_txt = (TextView)findViewById(R.id.babygrowth_display_TXT_babyweight);
		mBabycircumference = (TextView)findViewById(R.id.babygrowth_display_TXT_babycircumference);
		mGrowth_weight_GBtn = (Button)findViewById(R.id.weight_graph_btn);
		mGrowth_weight_GBtn.setOnClickListener(this);
		
		mGrowth_height_GBtn = (Button)findViewById(R.id.height_graph_btn);
		mGrowth_height_GBtn.setOnClickListener(this);

	}
	
	/* alert dialog */
	public void alertDialogWithMessage(String title, String msg){
		
		new BabyTrackerAlert(BabyTrackerGrowthActivity.this, title, msg).setPositiveButton("Now", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BabyTrackerGrowthActivity.this,GrowthdetailsActivity.class);
				intent.setAction("fromHome");
				intent.putExtra("baby_name", mBaby_name);
				intent.putExtra("baby_id", baby_id);
				startActivityForResult(intent, REQUEST_CODE_GROWTH);
			}
		})
		.setNegativeButton("Later", new android.content.DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//finish();
			}
		}).setIcon(android.R.drawable.ic_dialog_info)
		.create().show();
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
        super.onActivityResult(requestCode, resultCode, data);   
        switch(requestCode) 
        {  
	        case REQUEST_CODE_GROWTH:  
	        
	        	if (resultCode == RESULT_OK) 
	        	{
	        		baby_id = data.getExtras().getInt("growth_id");
	        		getBabygrowthDetails(baby_id);
	        		getGraphdetails();
	        	}
	        break;
	        
	        case REQUEST_CODE_ADDGROWTH:
	        	
	        	if(resultCode == RESULT_OK){
	        		baby_id = data.getExtras().getInt("growth_id");
	        		getBabygrowthDetails(baby_id); 	
	        		getGraphdetails();
	        	}
	        break;
	    }
    }
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
		
			case R.id.babygrowth_display_BTN_growthlist:
				
				Intent growthListIntent = new Intent(this, GrowthdetailsListActivity.class);
				Log.v(LOG_TAG, "growth id "+baby_id);
				growthListIntent.setAction("forGrowthList");
				growthListIntent.putExtra("growth_id", baby_id);
				startActivity(growthListIntent);
				
				break;
	
			case R.id.babygrowth_display_BTN_addgrowth:
				
				Intent mAddGrowth = new Intent(this, GrowthdetailsActivity.class);
				mAddGrowth.putExtra("baby_name", mBaby_name);
				mAddGrowth.putExtra("baby_id", baby_id);
				startActivityForResult(mAddGrowth, REQUEST_CODE_ADDGROWTH);
				
				break;	
			case R.id.weight_graph_btn:
			
				if (mGraph_list.size() > 0)
				{
					
					for (int iCtr = 0; iCtr < mGraph_list.size(); iCtr++) 
					{
						Log.v(getClass().getSimpleName(), "weight "+mGraph_list.get(iCtr).getWeight()+"height "+mGraph_list.get(iCtr).getBaby_age());
						graph_values[iCtr] = mGraph_list.get(iCtr).getWeight();
						age_values[iCtr] = mGraph_list.get(iCtr).getBaby_age();
					}
					
					mGraph_values.add(graph_values);
					mAges_values.add(age_values);
					
					Intent weight_graph_intent = null;
					weight_graph_intent = execute(BabyTrackerGrowthActivity.this, mAges_values, mGraph_values, "weight");
					startActivity(weight_graph_intent);
				
				}else {
					alertDialogWithMessage(mAlertTitle, "Please enter growth details first");
				}
				
				break;
				
			case R.id.height_graph_btn:
				
				if (mGraph_list.size() > 0) 
				{
					
					for (int iCtr = 0; iCtr < mGraph_list.size(); iCtr++) 
					{
						Log.v(getClass().getSimpleName(), "height1 "+mGraph_list.get(iCtr).getHeight());

						graph_values[iCtr] = mGraph_list.get(iCtr).getHeight();
						age_values[iCtr] = mGraph_list.get(iCtr).getBaby_age();
					}
					
					mGraph_values.add(graph_values);
					mAges_values.add(age_values);
					
					Log.v(LOG_TAG, ""+graph_values.length+"mGraph_list "+mGraph_list.size());
					
					Intent height_graph_intent = null;
					height_graph_intent = execute(BabyTrackerGrowthActivity.this, mAges_values, mGraph_values, "height");
					startActivity(height_graph_intent);
					
				}else {
					alertDialogWithMessage(mAlertTitle, "Please enter growth details first");
				}
				
				break;	
		}
	}
	
	/* checking growth is existed in data base or not */
	public boolean babyidExisted(int baby_id)
	{
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.GROWTH_TABLE+" where "+BabyTrackerDataBaseHelper.GROWTH_BABY_ID+" = "+ baby_id);
		Log.v(LOG_TAG, " growth existance query "+query);
		Cursor mCursor = mDataBaseHelper.select(query);
		
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
	
	/**
	 * Getting the graph details from the data base based on baby id.
	 */
	private void getGraphdetails()
	{
		
		mGraph_list = mDataBaseHelper.getDetailsforGraph(baby_id);
		
		if (mGraph_list.size() > 0)
		{
			
			mGraph_values = new ArrayList<double[]>();
			graph_values = new double[mGraph_list.size()];
			age_values = new double[mGraph_list.size()];

		}
		
		Log.v(getClass().getSimpleName(), "mGraph_list "+mGraph_list.size());
	}
	
	/**
	 * Getting Growth details from the database based on baby id, and set them to UI.
	 * @param baby_id
	 */
	private void getBabygrowthDetails(int baby_id) {
		
		String query = String.format(BabyTrackerDataBaseHelper.SELECT_QUERY, BabyTrackerDataBaseHelper.GROWTH_TABLE+" where "+BabyTrackerDataBaseHelper.GROWTH_BABY_ID+" = "+ baby_id+" order by "+BabyTrackerDataBaseHelper.GROWTH_DATE_OF_UPDATE+" DESC LIMIT 1");
		Log.e(LOG_TAG, query);
		growthcursor = mDataBaseHelper.select(query);
	
		try
		{
			
			if(growthcursor.moveToFirst())
			{
				
				Float baby_height = growthcursor.getFloat(growthcursor.getColumnIndex(BabyTrackerDataBaseHelper.GROWTH_BABY_HEIGHT));
				Float baby_weight = growthcursor.getFloat(growthcursor.getColumnIndex(BabyTrackerDataBaseHelper.GROWTH_BABY_WEIGHT));
				Float baby_circumference = growthcursor.getFloat(growthcursor.getColumnIndex(BabyTrackerDataBaseHelper.GROWTH_BABY_CIRCUMFERENCE));
			
				mBabyheight_txt.setText(""+baby_height+" Cms");
				mBabyweight_txt.setText(""+baby_weight+" Kgs");
				mBabycircumference.setText(""+baby_circumference+" Cms");

			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			growthcursor.close();
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
	
	/**
	   * Builds an XY multiple data set using the provided values.
	   * 
	   * @param titles the series titles
	   * @param xValues the values for the X axis
	   * @param yValues the values for the Y axis
	   * @return the XY multiple data set
	   */
	  protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,  List<double[]> yValues) {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    addXYSeries(dataset, titles, xValues, yValues, 0);
	    return dataset;
	  }

	  public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues, List<double[]> yValues, int scale) {
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	      XYSeries series = new XYSeries(titles[i], scale);
	      double[] xV = xValues.get(i);
	      double[] yV = yValues.get(i);
	      int seriesLength = xV.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(xV[k], yV[k]);
	      }
	      dataset.addSeries(series);
	    }
	  }
	  
	  
  /**
   * Builds an XY multiple series renderer.
   * 
   * @param colors the series rendering colors
   * @param styles the series point styles
   * @return the XY multiple series renderers
   */
  protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    setRenderer(renderer, colors, styles);
	    return renderer;
	  }

  protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) 
  {
	  
    renderer.setAxisTitleTextSize(16);
    renderer.setChartTitleTextSize(20);
    renderer.setLabelsTextSize(15);
    renderer.setLegendTextSize(15);
    renderer.setPointSize(5f);
    renderer.setMargins(new int[] { 20, 30, 15, 20 });
    int length = colors.length;
    
    for (int i = 0; i < length; i++)
    {
      XYSeriesRenderer r = new XYSeriesRenderer();
      r.setColor(colors[i]);
      r.setPointStyle(styles[i]);
      renderer.addSeriesRenderer(r);
    }
    
  }
		  
  /**
   * Executes the ChartFactory.
   * 
   * @param context the context
   * @param x values for the x axis in the graph
   * @param values ---> values for displaying the graph.
   * @param graphType ----> weight graph or height graph
   * @return the built intent
   */		  
  public Intent execute(Context context, List<double[]> x,  List<double[]> values, String graphType) 
  {
	  
	  	String title = "", xTitle = "", yTitle = "", title_str ="";
	  	int yAxisValue = 0;
	  	
	    int[] colors = new int[] {Color.MAGENTA};
	    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    int length = renderer.getSeriesRendererCount();

	    for (int i = 0; i < length; i++) 
	    {
	      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
	    }
	    
	    if(graphType.equalsIgnoreCase("weight"))
	    {
	    
	    	title = "Weight Graph";
	    	xTitle = "Month";
	    	yTitle = "Weight(Kgs)";
	    	yAxisValue = 10;
	    	title_str = "weight";
	    	
	    }else
	    {
	    	title = "Height Graph";
	    	xTitle = "Month";
	    	yTitle = "Height(Cms)";
	    	yAxisValue = 100;
	    	title_str = "height";
		}
	    
	    String[] titles = new String[] {title_str};
	    setChartSettings(renderer, title, xTitle, yTitle, 0, 12.5, 0, yAxisValue, Color.BLACK, Color.BLACK);
	    
	    renderer.setXLabels(12);
	    renderer.setYLabels(10);
	    renderer.setShowGrid(true);
	    renderer.setShowCustomTextGrid(true);
	    renderer.setApplyBackgroundColor(true);
	    renderer.setBackgroundColor(Color.rgb(57,150,255));
	    renderer.setMarginsColor(Color.rgb(57,150,255));
	    renderer.setXLabelsAlign(Align.RIGHT);
	    renderer.setYLabelsAlign(Align.RIGHT);
	    renderer.setZoomButtonsVisible(true);
	    renderer.setPanLimits(new double[] { 0, 40, 0, 100 });
	    renderer.setZoomLimits(new double[] { 0, 20, 0, 40 });

	    Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),renderer, title);
	    return intent;
	    
  }
		  
  /**
   * Sets a few of the series renderer settings.
   * 
   * @param renderer the renderer to set the properties to
   * @param title the chart title
   * @param xTitle the title for the X axis
   * @param yTitle the title for the Y axis
   * @param xMin the minimum value on the X axis
   * @param xMax the maximum value on the X axis
   * @param yMin the minimum value on the Y axis
   * @param yMax the maximum value on the Y axis
   * @param axesColor the axes color
   * @param labelsColor the labels color
   */
  protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,  int labelsColor) 
  {
	  
    renderer.setChartTitle(title);
    renderer.setXTitle(xTitle);
    renderer.setYTitle(yTitle);
    renderer.setXAxisMin(xMin);
    renderer.setXAxisMax(xMax);
    renderer.setYAxisMin(yMin);
    renderer.setYAxisMax(yMax); 

    renderer.setAxesColor(axesColor);
    renderer.setLabelsColor(labelsColor);
	    
  }
  
    @Override
	public void onDestroy(){
		super.onDestroy();
		mDataBaseHelper.close();
	}
  
}

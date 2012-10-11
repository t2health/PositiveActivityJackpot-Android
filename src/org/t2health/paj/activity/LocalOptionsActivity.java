/*
 * 
 * Positive Activity Jackpot
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: PositiveActivityJackpot001
 * Government Agency Original Software Title: Positive Activity Jackpot
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.paj.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.ContentSearch;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;
import org.t2health.paj.classes.PAJActivity;
import org.t2health.paj.classes.ScheduleClient;

import t2.paj.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Localoptions activity queries local (google places, geonames) data relevant to activity.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class LocalOptionsActivity extends ABSCustomTitleActivity implements OnClickListener
{

	//Longpress context menu
	protected static final int CONTEXTMENU_NAVIGATE = 0;
	protected static final int CONTEXTMENU_CALL = 1;

	//Controls
	private TextView txtSummaryTitle;
	private TextView txtSummaryActivityTitle;
	private ImageView ivIcon;
	//private Button btnShareActivity;
	private Button btnSave;
	private ProgressDialog m_ProgressDialog = null; 
	private ListView lvResults;
	//	private List<String[]> activityList;
	//	private int lastClickedItem = 0;

	//Data
	//private boolean visitedInvite = false;
	private DatabaseProvider db = new DatabaseProvider(this);
	ArrayList<ArrayList<String>> googlePlaces;
	ArrayList<String> geoPlaces;
	private ActivitiesAdapter adapter;
	private ScheduleClient scheduleClient;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localoptions);

		this.onEvent("LocalOptions-Open");
		
		//Show Main Menu
		this.SetMenuVisibility(1);

		//Attach to schedule service so we can set a notification
		scheduleClient = new ScheduleClient(getBaseContext());
		scheduleClient.doBindService();

		//Map Controls
		lvResults = (ListView)this.findViewById(R.id.lv_results);
		lvResults.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelectedPosition(position);
			}
		});
		initContextMenu();

		txtSummaryTitle = (TextView)this.findViewById(R.id.txt_Summary_title);
		txtSummaryActivityTitle = (TextView)this.findViewById(R.id.txt_Summary_activitytitle);
		ivIcon = (ImageView)this.findViewById(R.id.summary_icon);
		//btnShareActivity = (Button)this.findViewById(R.id.btn_Summary_Share);
		//btnShareActivity.setOnClickListener(this);
		btnSave = (Button)this.findViewById(R.id.btn_Summary_Save);
		btnSave.setOnClickListener(this);

		//If this is a road-trip, use geonames lookup
		if(Global.selectedCategory.equals("Road Trip"))
		{
			try
			{
				//Show progress bar
				m_ProgressDialog = ProgressDialog.show(this, "Please wait...", "  Loading available activities...", true);

				//Query Google results in new thread
				Runnable myRunnable = new Runnable()
				{
					public void run() 
					{
						QueryGeoNames();
					}
				};

				Thread thread =  new Thread(null, myRunnable, "GoogleThread");
				thread.start();

			}
			catch(Exception ex)
			{
				Global.Log.v("SUMMARYSEARCHERROR", ex.toString());
			}
		}
		else
		{
			try
			{
				//Show progress bar
				m_ProgressDialog = ProgressDialog.show(this, "Please wait...", "  Loading available activities...", true);

				//Query Google results in new thread
				Runnable myRunnable = new Runnable()
				{
					public void run() 
					{
						QueryGooglePlaces();
					}
				};

				Thread thread =  new Thread(null, myRunnable, "GoogleThread");
				thread.start();


			}
			catch(Exception ex)
			{
				Global.Log.v("SUMMARYSEARCHERROR", ex.toString());
			}
		}

		//Set display info for activity
		if(Global.selectedAttendance.equals("a"))
			txtSummaryTitle.setText(Global.selectedCategory + " Solo Activity");
		else if(Global.selectedAttendance.equals("c"))
			txtSummaryTitle.setText(Global.selectedCategory + " Pair Activity");
		else if(Global.selectedAttendance.equals("g"))
			txtSummaryTitle.setText(Global.selectedCategory + " Group Activity");

		//Set the display icon for this activity
		int resID = getResources().getIdentifier(Global.selectedCategory.toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
		ivIcon.setImageResource(resID);



		txtSummaryActivityTitle.setText(Global.selectedActivity);

	}

	private void initContextMenu() {
		lvResults.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("Activity Menu");
				menu.add(0, CONTEXTMENU_NAVIGATE, 1, "Navigate");
				menu.add(0, CONTEXTMENU_CALL, 0, "Call");

			}
		});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int selectedItem = menuInfo.position;

		switch (item.getItemId()) {
		case CONTEXTMENU_NAVIGATE:
			String address = googlePlaces.get(selectedItem).get(2);
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + address)); 
			startActivity(i);
			break;
		case CONTEXTMENU_CALL:
			String phone = googlePlaces.get(selectedItem).get(3);
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + phone));
			startActivity(callIntent);
			break;
		}
		return false;
	}

	//	@Override
	//	public void onStart()
	//	{
	//		ShowHelpTip("localoptionsactivity", getString(R.string.tips_localoption));
	//		super.onStart();
	//	}

	public void QueryGooglePlaces()
	{
		//Setup message pump
		Looper.prepare();

		//Lookup
		ContentSearch searcher = new ContentSearch(this.getBaseContext());
		googlePlaces = null;

		try 
		{
			//If the selected activity has a google places type, do the lookup
			String atype = db.selectActivityType(Integer.parseInt(Global.selectedActivityID));
			if(!atype.toLowerCase().startsWith("google:"))
			{
				googlePlaces = searcher.SearchPlaces(searcher.getPlacesURLByTypes("", URLEncoder.encode(atype, "utf-8")));
			}
			else
			{
				googlePlaces = searcher.SearchPlaces(searcher.getPlacesURLByKeyword(URLEncoder.encode(atype.replace("google:", "").replace("_", ""), "utf-8")));			
			}

		} catch (Exception e) 
		{
			Global.Log.v("SUMMARYQueryGoogleERROR", e.toString());
		}

		Runnable googleRunnable = new Runnable()
		{
			public void run() 
			{
				DisplayGoogleResults();
			}
		};

		runOnUiThread(googleRunnable);
		m_ProgressDialog.dismiss();
	}

	public void QueryGeoNames()
	{
		//Setup message pump
		Looper.prepare();

		//Lookup
		ContentSearch searcher = new ContentSearch(this.getBaseContext());
		geoPlaces = null;

		try 
		{
			String[] locSplit = Global.selectedActivityID.split("[|]");
			Double lat = Double.parseDouble(locSplit[0]);
			Double lng = Double.parseDouble(locSplit[1]);
			geoPlaces = searcher.getGeoPOI(lat, lng);

		} 
		catch (Exception e) 
		{
			Global.Log.v("SUMMARYQueryGoogleERROR", e.toString());
		}

		Runnable googleRunnable = new Runnable()
		{
			public void run() 
			{
				DisplayGeoResults();
			}
		};

		runOnUiThread(googleRunnable);
		m_ProgressDialog.dismiss();
	}

	public void DisplayGoogleResults()
	{
		if(googlePlaces != null)
		{
			int len = googlePlaces.size();
			PAJActivity[] outArray = new PAJActivity[len];
			for(int p=0; p< len; p++)
			{
				float rating = 0;
				String name = googlePlaces.get(p).get(0);
				String iconURL = googlePlaces.get(p).get(1);
				String address = googlePlaces.get(p).get(2);
				String phone = googlePlaces.get(p).get(3);
				//String tRating = googlePlaces.get(p).get(4);
				try
				{
					rating = new Float(googlePlaces.get(p).get(4));
				}
				catch(Exception ex){}

				outArray[p] = new PAJActivity();
				outArray[p].title = name;
				int resID =0;
				try{
					resID = getResources().getIdentifier(Global.selectedCategory.toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
				}
				catch(Exception ex){
					resID = 0;

				}
				if (resID == 0)
					resID = getResources().getIdentifier("icon", "drawable", getPackageName());
				outArray[p].icon = resID;
				outArray[p].rating = rating;
				outArray[p].address = address;
				outArray[p].phone = phone;
				outArray[p].SetIconDrawable(iconURL);
			}

			adapter = new ActivitiesAdapter(this, R.layout.list_item_localoptions, outArray);
			lvResults.setAdapter(adapter);
		}
	}

	public void DisplayGeoResults()
	{
		if(geoPlaces != null)
		{
			int len = geoPlaces.size();
			PAJActivity[] outArray = new PAJActivity[len];
			for(int p=0; p< len; p++)
			{
				//float rating = 0;
				String name = geoPlaces.get(p);
				//String iconURL = "";
				//String address = geoPlaces.get(p);
				//String phone = geoPlaces.get(p);
				//String tRating = googlePlaces.get(p).get(4);


				outArray[p] = new PAJActivity();
				outArray[p].title = name;
				int resID =0;
				try{
					resID = getResources().getIdentifier(Global.selectedCategory.toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
				}
				catch(Exception ex){
					resID = 0;

				}
				if (resID == 0)
					resID = getResources().getIdentifier("icon", "drawable", getPackageName());
				outArray[p].icon = resID;
				outArray[p].rating = 0;
				outArray[p].address = "";
				outArray[p].phone = "";
				//outArray[p].SetIconDrawable(iconURL);
			}

			adapter = new ActivitiesAdapter(this, R.layout.list_item_localoptions, outArray);
			lvResults.setAdapter(adapter);
		}
	}

	@Override
	protected void onStop()
	{
		if(scheduleClient != null)
			scheduleClient.doUnbindService();
		super.onStop();
	}

	public void SaveActivity()
	{

		//Get selected local option
		int idx = 0;
		try
		{
			idx = adapter.selectedPos;
		}
		catch(Exception ex)
		{}

		String LocalName = "";
		String LocalAddress = "";
		String LocalPhone = "";
		if(idx >= 0)
		{
			//TODO: this is a hack for road trip
			if(Global.selectedCategory.equals("Road Trip"))
			{
				LocalName = "RT:" + geoPlaces.get(idx) + "^" + Global.selectedActivity;
				LocalAddress = "";
				LocalPhone = "";
			}
			else
			{
				try
				{
					LocalName = googlePlaces.get(idx).get(0);
					LocalAddress = googlePlaces.get(idx).get(2);
					LocalPhone = googlePlaces.get(idx).get(3);
				}
				catch(Exception ex){}
			}
		}

		//Schedule a notification to be displayed to the user that activities should be rated.
		try
		{
			Calendar c = Calendar.getInstance();
			if(Global.DebugOn)
				c.add(Calendar.MINUTE, 1);
			else
				c.add(Calendar.HOUR, 24);
			scheduleClient.setAlarmForNotification(c);
		}
		catch(Exception ex)
		{
			Global.Log.v("Notification error", ex.toString());
		}

		//Get Contacts
		String resultMessage = "";
		String SelectedContacts = "";
		for(int a=0; a< Global.selectedContacts.size(); a++)
		{
			SelectedContacts += Global.selectedContacts.get(a) + "|";
		}

		//Save Activity Data
		if(db.insertSavedActivity(Global.selectedAttendance, SelectedContacts, Global.selectedActivityID, LocalName, LocalAddress, LocalPhone))
		{
			resultMessage = "Activity saved successfully.";
		}
		else
		{
			resultMessage = "Failed to save activity!";
			new AlertDialog.Builder(this)
			.setMessage(resultMessage)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//Finish();
				}
			})
			.create()
			.show();
		}

		Global.selectedActivityID = "" + db.selectLastActivityID();
		//Launch summary activity
		this.startActivity(ActivityFactory.getSummaryActivity(this));
		this.finish();


	}

	public void Finish()
	{
		this.finish();
	}

	@Override
	public void onClick(View v) 
	{
		super.onClick(v);
		switch(v.getId()) 
		{
		case R.id.btn_Summary_Save:
			SaveActivity();
			break;
		}
	}

	private class ActivitiesAdapter extends ArrayAdapter<PAJActivity>{

		Context context; 
		int layoutResourceId;    
		PAJActivity data[] = null;
		private int selectedPos = -1;

		public ActivitiesAdapter(Context context, int layoutResourceId, PAJActivity[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}

		public void setSelectedPosition(int pos){
			selectedPos = pos;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ActivitiesHolder holder = null;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new ActivitiesHolder();
				holder.imgIcon = (ImageView)row.findViewById(R.id.rate_list_icon);
				holder.txtTitle = (TextView)row.findViewById(R.id.rate_list_title);
				holder.txtAddress = (TextView)row.findViewById(R.id.summary_address);
				holder.rbRating = (RatingBar)row.findViewById(R.id.summary_rating);

				row.setTag(holder);
			}
			else
			{
				holder = (ActivitiesHolder)row.getTag();
			}
			//
			PAJActivity curActivity = data[position];
			holder.txtTitle.setText(curActivity.title);
			holder.txtAddress.setText(curActivity.address);

			try
			{
				holder.imgIcon.setImageDrawable(curActivity.iconDrawable);
				holder.imgIcon.setBackgroundResource(R.drawable.buttongreen);
			}
			catch(Exception ex)
			{
				holder.imgIcon.setImageResource(curActivity.icon);
			}

			holder.rbRating.setRating(curActivity.rating);

			if(selectedPos == position){
				row.setBackgroundResource(R.drawable.blueboxbutton);
			}else{
				row.setBackgroundResource(R.drawable.grayboxbutton);
			}

			return row;
		}


	}

	static class ActivitiesHolder
	{
		ImageView imgIcon;
		TextView txtTitle;
		TextView txtAddress;
		RatingBar rbRating;
	}


}




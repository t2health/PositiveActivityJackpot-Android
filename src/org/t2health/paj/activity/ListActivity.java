package org.t2health.paj.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

import org.t2health.paj.classes.Accessibility;
import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;

import t2.paj.R;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The List activity steps the user through the Positive activity selection process
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class ListActivity extends ABSCustomTitleActivity implements	OnClickListener, SensorEventListener, OnTouchListener
{

	// Wheel Controls
	private WheelView activityWheel;

	// Controls
	private TextView txtListSummary;
	private Button btnBottomPrev;
	private Button btnBottomNext;
	private ImageView jackpotHandle;
	private AnimationDrawable frameAnimation;
	//private ProgressDialog m_ProgressDialog = null; 

	// Data
	int currentStep = 0;
	private DatabaseProvider db = new DatabaseProvider(this);
	private String socialStatus = "";
	private String contactStatus = "";

	// Variables for Shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private static final int SHAKE_THRESHOLD = 300;
	private long lastUpdate = -1;
	private float x;
	private float last_x;
	private boolean spinning = false;
	private Random rnd;

	public void onResume() 
	{
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		Global.createMode = true;

	}

	@Override
	public void onStart()
	{
		super.onStart();
		ShowHelpTip("listactivity", getString(R.string.tips_list));
		Accessibility.speak(Toast.makeText(this, getString(R.string.tips_list), Toast.LENGTH_LONG));
	}

	protected void onPause() 
	{
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pa_list);

		this.onEvent("ManualActivity(list)-Open");
		
		// Setup the shake sensor
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Main Menu
		this.SetMenuVisibility(1);
		this.btnMainCreate.setChecked(true);

		// Control Mappings
		txtListSummary = (TextView) this.findViewById(R.id.txt_List_Summary);
		btnBottomPrev = (Button) this.findViewById(R.id.btn_List_BottomPrev);
		btnBottomPrev.setOnClickListener(this);
		btnBottomNext = (Button) this.findViewById(R.id.btn_List_BottomNext);
		btnBottomNext.setOnClickListener(this);

		jackpotHandle = (ImageView) this.findViewById(R.id.list_wheelright);
		jackpotHandle.setOnTouchListener(this);
		jackpotHandle.setOnClickListener(this);
		jackpotHandle.setBackgroundResource(R.anim.anim_handle_down);
		frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();

		// Casino-style wheel
		activityWheel = (WheelView) this.findViewById(R.id.list_activityWheel);
		activityWheel.setEnabled(true);
		activityWheel.setCyclic(true);
		activityWheel.setVisibleItems(3);
		activityWheel.ShowIconsText = true;
		activityWheel.setBackgroundResource(R.drawable.rollerbackfull);
		activityWheel.setForegroundResource(R.drawable.highlightbarfull);
		activityWheel.setOnClickListener(this);
		activityWheel.addScrollingListener(new OnWheelScrollListener() 
		{
			public void onScrollingStarted(WheelView wheel) {}
			public void onScrollingFinished(WheelView wheel) 
			{
				// Refresh
				refreshStatus();
			}
		});
		activityWheel.addClickingListener(new OnWheelClickedListener() 
		{
			public void onItemClicked(WheelView wheel, int itemIndex) 
			{
				try 
				{
					// Update checkbox selections
					if (activityWheel.WheelItemsChecked.get(itemIndex))
						activityWheel.WheelItemsChecked.set(itemIndex, false);
					else
						activityWheel.WheelItemsChecked.set(itemIndex, true);

					// Refresh data display
					activityWheel.refreshViewAdapter();
					activityWheel.setCurrentItem(itemIndex);
					refreshStatus();
				} 
				catch (Exception ex) 
				{
					Global.Log.v("ERR:LISTACTIVITY-rwheelclicklistener",ex.toString());
				}
			}
		});

		// Seed for random
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());

		// Start with attendance selections
		PopulateAttendance();
		refreshStatus();
	}

	public void WheelRefresh() 
	{
		activityWheel.setEnabled(true);
		activityWheel.setVisibility(View.VISIBLE);
		spinning = false;
	}

	public void refreshStatus() 
	{
		spinning = false;

		// Status refresh
		if (currentStep == Global.SOCIAL) 
		{
			// Social
			btnBottomPrev.setBackgroundResource(R.drawable.grayboxbutton);
			btnBottomNext.setText("Next");

			if (activityWheel.WheelItems.get(activityWheel.getCurrentItem()).equals("Solo")) 
			{
				Global.selectedAttendance = "a";
				socialStatus = "I'm going to do an activity by myself";
				contactStatus = "";
			} 
			else if (activityWheel.WheelItems.get(activityWheel.getCurrentItem()).equals("Pair")) 
			{
				Global.selectedAttendance = "c";
				socialStatus = "I'm going to do an activity with one other person";
				contactStatus = "";
			} 
			else if (activityWheel.WheelItems.get(activityWheel.getCurrentItem()).equals("Group")) 
			{
				Global.selectedAttendance = "g";
				socialStatus = "I'm going to do an activity with a group";
				contactStatus = "";
			}
		} 
		else if (currentStep == Global.CONTACTS) 
		{
			// Contacts
			btnBottomPrev.setBackgroundResource(R.drawable.blueboxbutton);
			btnBottomNext.setText("Next");

			Global.selectedContacts = new ArrayList<String>();
			contactStatus = "";

			if (Global.selectedAttendance.equals("c")) 
			{
				// Single select
				try 
				{
					contactStatus = activityWheel.WheelItems.get(activityWheel.getCurrentItem());
					Global.selectedContacts.add(contactStatus);
				} 
				catch (Exception ex) 
				{
					// This should not happen, as contact import is checked before this activity is started.
					currentStep = Global.SOCIAL;
					PopulateAttendance();
				}
			} 
			else 
			{
				// Multi-select checkboxes
				int len = activityWheel.WheelItemsChecked.size();
				for (int a = 0; a < len; a++) 
				{
					try 
					{
						if (activityWheel.WheelItemsChecked.get(a)) 
						{
							contactStatus = contactStatus.replace("(?)", "");
							contactStatus += activityWheel.WheelItems.get(a)
									+ ", ";
							Global.selectedContacts
							.add(activityWheel.WheelItems.get(a));
						}
					} 
					catch (Exception ex) 
					{
						Global.Log.v("ERR:LISTACTIVITY-refreshstatus-contacts",ex.toString());
					}
				}
			}
			socialStatus = "I'm going to do an activity with " + contactStatus;
			//			if (contactStatus.length() > 20)
			//				contactStatus = "(" + contactStatus.substring(0, 19) + "...)";
		} 
		else if (currentStep == Global.CATEGORY) 
		{

			// Category
			btnBottomPrev.setBackgroundResource(R.drawable.blueboxbutton);
			btnBottomNext.setText("Next");
			try 
			{
				Global.selectedCategory = activityWheel.WheelItems.get(activityWheel.getCurrentItem());
				int resID = getResources().getIdentifier( Global.selectedCategory.toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
				if (resID == 0)
					resID = getResources().getIdentifier("icon", "drawable", getPackageName());

			} 
			catch (Exception ex) 
			{
				Global.Log.v("ERR:LISTACTIVITY-refreshstatus-categories",ex.toString());
			}

			socialStatus = "I would like to do a " + Global.selectedCategory + " activity.";

		} 
		else if (currentStep == Global.ACTIVITY) 
		{
			// Subcategory
			btnBottomPrev.setBackgroundResource(R.drawable.blueboxbutton);
			btnBottomNext.setText("Done");
			try 
			{
				Global.selectedActivity = activityWheel.WheelItems.get(activityWheel.getCurrentItem());
				Global.selectedActivityID = activityWheel.WheelItemIDs.get(activityWheel.getCurrentItem());
			} 
			catch (Exception ex) 
			{
				Global.Log.v("ERR:LISTACTIVITY-refreshstatus-categories",ex.toString());
			}

			socialStatus = "I am going to " + Global.selectedActivity;
		}

		// Update Status Text
		txtListSummary.setText(socialStatus);
		Accessibility.speak(Toast.makeText(this, socialStatus, Toast.LENGTH_LONG));

	}

	public void LoadWheelData() 
	{

		if (currentStep == Global.SOCIAL) 
		{

			// Social
			PopulateAttendance();
		} 
		else if (currentStep == Global.CONTACTS) 
		{
			// Contacts
			PopulateContacts();
		} 
		else if (currentStep == Global.CATEGORY) 
		{
			// Category
			PopulateCategories();
		} 
		else if (currentStep == Global.ACTIVITY) 
		{
			if(Global.selectedCategory.equals("Road Trip"))
				PopulateRoadTripDestinations();
			else
				// Subcategory
				PopulateActivities();
		}

	}

	public void PopulateAttendance() 
	{
		// Attendance not stored in DB, populate manually
		activityWheel.WheelItems = new ArrayList<String>();
		activityWheel.WheelItemsChecked = new ArrayList<Boolean>();
		activityWheel.WheelItemImages = new ArrayList<Integer>();

		activityWheel.WheelItems.add("Solo");
		activityWheel.WheelItemImages.add(getResources().getIdentifier("solo", "drawable", getPackageName()));
		activityWheel.WheelItemsChecked.add(false);

		activityWheel.WheelItems.add("Pair");
		activityWheel.WheelItemImages.add(getResources().getIdentifier("pair", "drawable", getPackageName()));
		activityWheel.WheelItemsChecked.add(false);

		activityWheel.WheelItems.add("Group");
		activityWheel.WheelItemImages.add(getResources().getIdentifier("group", "drawable", getPackageName()));
		activityWheel.WheelItemsChecked.add(false);

		activityWheel.ShowCheckBoxesImages = false;
		activityWheel.refreshViewAdapter();
		activityWheel.refreshDrawableState();
		activityWheel.setCurrentItem(0);

		Global.selectedContacts = new ArrayList<String>();
		Global.selectedActivity = "";
		Global.selectedActivityID = "";
		Global.selectedCategory = "";

		refreshStatus();

	}

	public void PopulateCategories() 
	{

		activityWheel.scroller.scroll(50 * 4000, 45600);

		// Get categories from DB
		activityWheel.WheelItems = new ArrayList<String>();
		activityWheel.WheelItemsChecked = new ArrayList<Boolean>();
		activityWheel.WheelItemImages = new ArrayList<Integer>();
		List<String> tlist = db.selectCategories();

		int len = tlist.size();
		for (int a = 0; a < len; a++) 
		{
			//Hack - taking out thoughts if solo
			if((tlist.get(a).equals("Thoughts")) && (!Global.selectedAttendance.equals("a")))
			{
			}
			else
			{
				activityWheel.WheelItems.add(tlist.get(a));
				int resID = getResources().getIdentifier(tlist.get(a).toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
				if (resID == 0)
					resID = getResources().getIdentifier("icon", "drawable", getPackageName());
				activityWheel.WheelItemImages.add(resID);
				activityWheel.WheelItemsChecked.add(false);
			}
		}

		activityWheel.ShowCheckBoxesImages = false;
		activityWheel.refreshViewAdapter();
		activityWheel.refreshDrawableState();
		activityWheel.setCurrentItem(0);

		Global.selectedActivity = "";
		Global.selectedActivityID = "";

		activityWheel.stopScrolling();
		refreshStatus();

	}

	public void PopulateRoadTripDestinations() 
	{

		activityWheel.scroller.scroll(50 * 4000, 45600);

		activityWheel.WheelItems = new ArrayList<String>();
		activityWheel.WheelItemIDs = new ArrayList<String>();
		activityWheel.WheelItemImages = new ArrayList<Integer>();
		activityWheel.WheelItemsChecked = new ArrayList<Boolean>();

		int len = Global.RoadTripList.size();
		for (int a = 0; a < len; a++) 
		{
			activityWheel.WheelItemIDs.add(Global.RoadTripList.get(a)[1] + "|" + Global.RoadTripList.get(a)[2]);

			activityWheel.WheelItems.add(Global.RoadTripList.get(a)[0]);
			activityWheel.WheelItemsChecked.add(false);
		}

		activityWheel.ShowCheckBoxesImages = false;
		activityWheel.refreshViewAdapter();
		activityWheel.refreshDrawableState();
		activityWheel.setCurrentItem(0);

		Global.selectedActivity = "";
		Global.selectedActivityID = "";

		activityWheel.stopScrolling();
		refreshStatus();
	}

	public void PopulateContacts() 
	{

		activityWheel.scroller.scroll(50 * 4000, 45600);

		// Get contacts from DB
		activityWheel.WheelItems = new ArrayList<String>();
		activityWheel.WheelItemImages = new ArrayList<Integer>();
		activityWheel.WheelItemsChecked = new ArrayList<Boolean>();
		List<String> tlist = db.selectContacts();

		int len = tlist.size();
		int heads = 1;
		for (int a = 0; a < len; a++) 
		{//
			activityWheel.WheelItems.add(tlist.get(a));
			activityWheel.WheelItemsChecked.add(false);
			activityWheel.WheelItemImages.add(getResources().getIdentifier("heads" + heads, "drawable", getPackageName()));
			heads++;
			if(heads > 8) heads = 1;
		}

		if (Global.selectedAttendance.equals("c"))
			activityWheel.ShowCheckBoxesImages = false;
		else 
			activityWheel.ShowCheckBoxesImages = true;

		activityWheel.refreshViewAdapter();
		activityWheel.refreshDrawableState();
		activityWheel.setCurrentItem(0);

		activityWheel.stopScrolling();
		refreshStatus();

	}

	public void PopulateActivities() 
	{

		activityWheel.scroller.scroll(50 * 4000, 45600);

		// Get categories from DB
		activityWheel.WheelItems = new ArrayList<String>();
		activityWheel.WheelItemIDs = new ArrayList<String>();
		activityWheel.WheelItemImages = new ArrayList<Integer>();
		activityWheel.WheelItemsChecked = new ArrayList<Boolean>();
		List<String> tlist = db.selectEnabledActivities(Global.selectedCategory, Global.selectedAttendance);

		int len = tlist.size();
		int generic = 1;
		for (int a = 0; a < len; a++) 
		{
			activityWheel.WheelItemIDs.add(tlist.get(a));

			activityWheel.WheelItemImages.add(getResources().getIdentifier("generic" + generic, "drawable", getPackageName()));
			generic++;
			if(generic > 15)
				generic = 1;

			activityWheel.WheelItems.add(db.selectActivityName(tlist.get(a)));
			activityWheel.WheelItemsChecked.add(false);
		}

		activityWheel.ShowCheckBoxesImages = false;
		activityWheel.refreshViewAdapter();
		activityWheel.refreshDrawableState();
		activityWheel.setCurrentItem(0);

		Global.selectedActivity = "";
		Global.selectedActivityID = "";

		activityWheel.stopScrolling();
		refreshStatus();
	}

	public void DisplayGoogleResults() 
	{
		// Load google search results into wheel
		activityWheel.WheelItemsChecked = new ArrayList<Boolean>();

		int len = activityWheel.WheelItems.size();
		for (int a = 0; a < len; a++) 
		{
			activityWheel.WheelItemsChecked.add(false);
		}
		activityWheel.refreshViewAdapter();

		activityWheel.setCurrentItem(0, true);
		activityWheel.stopScrolling();

	}

	public void randomActivity() {
		// Randomize the unlocked wheels
		if (!spinning) 
		{
			// Start the animation
			frameAnimation.stop();
			frameAnimation.start();

			spinning = true;
			try 
			{
				int sel = rnd.nextInt(50);
				if(Accessibility.AccessibilityEnabled(this))
					activityWheel.scroller.scroll(100, 100);
				else
					activityWheel.scroller.scroll(sel * 2000, 1400);

			} 
			catch (Exception ex) 
			{
				Global.Log.v("ERROR:randomActivity", ex.toString());
			}
		}
	}

	public void selectActivity() 
	{
		Global.Log.v("CURRENTSTEP", "" + currentStep);
		Global.Log.v("NEEDEDSTEP", "" + Global.ACTIVITY);
		if (currentStep == Global.ACTIVITY) 
			this.startActivity(ActivityFactory.getLocalOptionsActivity(this));

		this.finish();
	}

	// Handle UI clicks
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
		case R.id.list_activityWheel:

			//Only listen to clicks when accessibility is enabled
			if(Accessibility.AccessibilityEnabled(this))
			{

				// Update checkbox selections
				if (activityWheel.WheelItemsChecked.get(activityWheel.getCurrentItem()))
					activityWheel.WheelItemsChecked.set(activityWheel.getCurrentItem(), false);
				else
					activityWheel.WheelItemsChecked.set(activityWheel.getCurrentItem(), true);

				// Refresh data display
				activityWheel.refreshViewAdapter();
				//activityWheel.setCurrentItem(itemIndex);
				refreshStatus();
			}



			break;
		case R.id.list_wheelright:
			if(Accessibility.AccessibilityEnabled(this))	
			{
				jackpotHandle.setBackgroundResource(R.anim.anim_handle_up);
				frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
				randomActivity();
			}
			break;
		case R.id.btn_List_BottomPrev:
			if (currentStep > Global.SOCIAL)
			{
				currentStep--;

				// If solo selected, skip contacts
				if ((currentStep == Global.CONTACTS) && (Global.selectedAttendance == "a"))
					currentStep = Global.SOCIAL;

				LoadWheelData();
			}
			break;
		case R.id.btn_List_BottomNext:
			if (currentStep < Global.ACTIVITY)
			{
				currentStep++;

				// If solo selected, skip contacts
				if ((currentStep == Global.CONTACTS) && (Global.selectedAttendance == "a"))
					currentStep = Global.CATEGORY;

				LoadWheelData();
			}
			else
			{
				selectActivity();
			}
			break;
		}
	}

	public void onSensorChanged(SensorEvent se) 
	{
		// Handle randomization on shake
		if (se.sensor.getType() == 1) 
		{
			long curTime = System.currentTimeMillis();

			if ((curTime - lastUpdate) > 200) 
			{
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				x = se.values[SensorManager.DATA_X];

				float speed = Math.abs(x - last_x) / diffTime * 10000;

				if (speed > SHAKE_THRESHOLD) 
				{
					jackpotHandle.setBackgroundResource(R.anim.anim_handle_up);
					frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
					randomActivity();
				}
				last_x = x;
			}

		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		switch ( event.getAction() ) 
		{
		case MotionEvent.ACTION_DOWN: 
			if (!spinning) 
			{
				jackpotHandle.setBackgroundResource(R.anim.anim_handle_down);
				frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
				frameAnimation.stop();
				frameAnimation.start();
			}
			break;
		case MotionEvent.ACTION_UP: 
			if(v.getId() == R.id.list_wheelright)
			{
				jackpotHandle.setBackgroundResource(R.anim.anim_handle_up);
				frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
				randomActivity();
			}
			break;
		}
		return true;
	}
}

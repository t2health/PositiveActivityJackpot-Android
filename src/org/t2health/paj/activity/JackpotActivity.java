package org.t2health.paj.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

import org.t2health.paj.classes.Accessibility;
import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;
import org.t2health.paj.classes.SharedPref;
import org.t2health.paj.classes.ToggledButton;

import t2.paj.R;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Jackpot activity selects random positive activites using a casino style jackpot
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class JackpotActivity extends ABSCustomTitleActivity implements OnClickListener, SensorEventListener, OnWheelScrollListener, OnTouchListener
{

	// Wheel Controls
	private WheelView activityWheel1;
	private WheelView activityWheel2;
	private WheelView activityWheel3;
	private WheelView activityWheel4;

	// Controls
	private TextView txtListSummary;
	private Button btnSelectActivity;
	private ToggledButton btn_List_Social;
	private ToggledButton btn_List_Category;
	private ToggledButton btn_List_Activity;
	private ImageView btnLock1;
	private ImageView btnLock2;
	private ImageView btnLock3;
	private ImageView btnLock4;
	private ImageView jackpotHandle;

	private AnimationDrawable frameAnimation;

	private boolean locked1 = false;
	private boolean locked2 = false;
	private boolean locked3 = false;
	private boolean locked4 = false;

	private boolean spunOnce = false;

	private int lastSpinningWheel = 0;

	String socialStatus = "";
	String contactStatus = "";

	// Data
	//private int currentStep = 0;
	private DatabaseProvider db = new DatabaseProvider(this);

	// Variables for Shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private static final int SHAKE_THRESHOLD = 300;
	private long lastUpdate = -1;
	private float x;
	private float last_x;
	private Random rnd;
	private boolean spinning = false;

	private boolean hasSelection = false;

	private Vibrator vib;
	private static long[] pattern = { 60, 80, 60, 80, 80, 80, 80, 80, 100, 80, 100, 80, 150, 80, 150, 80, 200, 80, 200, 80, 250, 80, 250, 80, 300, 80, 300, 80, 350, 80, 350, 80, 400, 80, 400, 80, 450, 80, 450, 80, 500, 80, 500, 80, 550, 80, 650, 80, 600, 80, 600, 80 };

	private static MediaPlayer mp;

	public void onResume() 
	{
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		Global.createMode = true;
	}

	//	@Override
	//	public void onStart()
	//	{
	//		ShowHelpTip("jackpotactivity", getString(R.string.tips_create));
	//		super.onStart();
	//	}

	protected void onPause() 
	{
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jackpot);
		this.onEvent("Jackpot-Open");
		
		Accessibility.speak(Toast.makeText(this, getString(R.string.jackpot_instruct),	Toast.LENGTH_LONG));

		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mp = MediaPlayer.create(this, R.raw.slot);
		mp.setLooping(true);

		// Setup the shake sensor
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Volume controls adjust media only
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Main Menu
		this.SetMenuVisibility(1);
		this.btnMainCreate.setChecked(true);

		// Control Mappings
		txtListSummary = (TextView) this.findViewById(R.id.txt_jackpot_Summary);
		txtListSummary.setText(getString(R.string.jackpot_instruct));
		txtListSummary.setMovementMethod(new ScrollingMovementMethod());

		btnSelectActivity = (Button) this.findViewById(R.id.btn_jackpot_SelectActivity);
		btnSelectActivity.setOnClickListener(this);

		btnLock1 = (ImageView)this.findViewById(R.id.btn_jackpot_Lock1);
		btnLock1.setOnClickListener(this);

		btnLock2 = (ImageView)this.findViewById(R.id.btn_jackpot_Lock2);
		btnLock2.setOnClickListener(this);

		btnLock3 = (ImageView)this.findViewById(R.id.btn_jackpot_Lock3);
		btnLock3.setOnClickListener(this);

		btnLock4 = (ImageView)this.findViewById(R.id.btn_jackpot_Lock4);
		btnLock4.setOnClickListener(this);

		jackpotHandle = (ImageView) this.findViewById(R.id.jackpot_wheelrighthandle);
		jackpotHandle.setOnTouchListener(this);
		jackpotHandle.setOnClickListener(this);
		jackpotHandle.setBackgroundResource(R.anim.anim_handle_down);
		frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();

		// Casino-style wheels
		activityWheel1 = (WheelView) this.findViewById(R.id.jackpot_activityWheel1);
		activityWheel1.setEnabled(false);
		activityWheel1.setCyclic(true);
		activityWheel1.setBackgroundResource(R.drawable.rollerbackleft);
		activityWheel1.setForegroundResource(R.drawable.highlightbarleft);
		activityWheel1.ShowShadows = true;
		activityWheel1.addScrollingListener(this);

		activityWheel2 = (WheelView) this.findViewById(R.id.jackpot_activityWheel2);
		activityWheel2.setEnabled(false);
		activityWheel2.setCyclic(true);
		activityWheel2.setBackgroundResource(R.drawable.rollerbackmiddle);
		activityWheel2.setForegroundResource(R.drawable.highlightbarmiddle);
		activityWheel2.ShowShadows = true;
		activityWheel2.addScrollingListener(this);

		activityWheel3 = (WheelView) this.findViewById(R.id.jackpot_activityWheel3);
		activityWheel3.setEnabled(false);
		activityWheel3.setCyclic(true);
		activityWheel3.setBackgroundResource(R.drawable.rollerbackmiddle);
		activityWheel3.setForegroundResource(R.drawable.highlightbarmiddle);
		activityWheel3.ShowShadows = true;
		activityWheel3.addScrollingListener(this);

		activityWheel4 = (WheelView) this.findViewById(R.id.jackpot_activityWheel4);
		activityWheel4.setEnabled(false);
		activityWheel4.setCyclic(true);
		activityWheel4.setBackgroundResource(R.drawable.rollerbackright);
		activityWheel4.setForegroundResource(R.drawable.highlightbarright);
		activityWheel4.ShowShadows = true;
		activityWheel4.addScrollingListener(this);

		// Plant Seeds
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());

		Global.selectedAttendance = "a";

		// Start with attendance selections
		try
		{
			PopulateAttendance(activityWheel1);
			PopulateContacts(activityWheel2);
			PopulateCategories(activityWheel3);
			PopulateActivities(activityWheel4);
		}
		catch(Exception ex)
		{
			finish();
		}

	}

	private void wheelRefresh(int wheelID) 
	{


		switch (wheelID) 
		{
		case R.id.jackpot_activityWheel1:

			socialStatus = "";
			contactStatus = "";

			Global.selectedContacts = new ArrayList<String>();

			if (activityWheel1.WheelItems.get(activityWheel1.getCurrentItem()).equals("Solo")) 
			{
				Global.selectedAttendance = "a";
				socialStatus = "I am going ";
			} 
			else if (activityWheel1.WheelItems.get(activityWheel1.getCurrentItem()).equals("Pair")) 
			{
				Global.selectedAttendance = "c";
				socialStatus = "I am going with ";
			} 
			else if (activityWheel1.WheelItems.get(activityWheel1.getCurrentItem()).equals("Group")) 
			{
				Global.selectedAttendance = "g";
				socialStatus = "I am going with ";
				contactStatus = "";
			}
			break; 

		case R.id.jackpot_activityWheel2:
			if (!Global.selectedAttendance.equals("a")) 
			{
				Global.selectedContacts.clear();
				contactStatus = "";

				if (Global.selectedAttendance.equals("g")) 
				{

					int numSelections = ((rnd.nextInt(5)) + 1);
					for (int l = 0; l < numSelections; l++) 
					{
						int tSel = rnd.nextInt(activityWheel2.WheelItems.size());
						Global.selectedContacts.add(activityWheel2.WheelItems.get(tSel));
						contactStatus += activityWheel2.WheelItems.get(tSel) + ", ";
					}
				}
				if (Global.selectedAttendance.equals("c")) 
				{
					int tSel = rnd.nextInt(activityWheel2.WheelItems.size());
					Global.selectedContacts.add(activityWheel2.WheelItems.get(tSel));
					contactStatus = activityWheel2.WheelItems.get(tSel);
				}
			}
			break; 

		case R.id.jackpot_activityWheel3:
			Global.selectedCategory = activityWheel3.WheelItems.get(activityWheel3.getCurrentItem());
			PopulateActivities(activityWheel4);
			//int sel = rnd.nextInt(50);
			//activityWheel4.scroller.scroll(sel * 10000, 200);
			break;

		case R.id.jackpot_activityWheel4:
			try
			{
				Global.selectedActivity = activityWheel4.WheelItems.get(activityWheel4.getCurrentItem());
				Global.selectedActivityID = activityWheel4.WheelItemIDs.get(activityWheel4.getCurrentItem());
			}
			catch(Exception ex)
			{
				Global.Log.v("ERROR", Global.selectedActivityID);
			}
			break;

		}

		if(wheelID == lastSpinningWheel)
		{
			if(SharedPref.getVibrationOn())
				vib.cancel();

			if(SharedPref.getAudioOn())
				mp.pause();

			spinning = false;
			hasSelection = true;
			txtListSummary.setText(socialStatus + contactStatus + " to " + Global.selectedActivity.toLowerCase() + ".");
			btnSelectActivity.setBackgroundResource(R.drawable.blueboxbutton);
			Accessibility.speak(Toast.makeText(this, socialStatus + contactStatus + " to " + Global.selectedActivity.toLowerCase() + ".", Toast.LENGTH_LONG));

		}

	}

	public void PopulateAttendance(WheelView inWheel) 
	{
		// Get Attendance from DB
		inWheel.WheelItems = new ArrayList<String>();
		inWheel.WheelItemsChecked = new ArrayList<Boolean>();
		List<String> tlist = db.selectAttendanceWeighted();

		int len = tlist.size();
		for (int a = 0; a < len; a++) 
		{

			inWheel.WheelItems.add(tlist.get(a));
			int resID = getResources().getIdentifier( tlist.get(a).toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
			if (resID == 0)
				resID = getResources().getIdentifier("icon", "drawable", getPackageName());

			inWheel.WheelItemImages.add(resID);
			inWheel.WheelItemsChecked.add(false);

		}

		inWheel.ShowCheckBoxesImages = false;
		inWheel.ShowIcons = true;
		inWheel.refreshViewAdapter();
		inWheel.refreshDrawableState();
		inWheel.setCurrentItem(0);

	}

	public void PopulateCategories(WheelView inWheel) 
	{

		// Get categories from DB
		inWheel.WheelItems = new ArrayList<String>();
		inWheel.WheelItemsChecked = new ArrayList<Boolean>();
		List<String> tlist = db.selectCategoriesWeighted();

		int len = tlist.size();
		for (int a = 0; a < len; a++) 
		{

			//Hack - taking out thoughts and road trip since they cannot be used while not solo
			if(tlist.get(a).equals("Thoughts"))
			{
			}
			else if(tlist.get(a).equals("Road Trip"))
			{
			}
			else
			{
				inWheel.WheelItems.add(tlist.get(a));
				int resID = getResources().getIdentifier( tlist.get(a).toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
				if (resID == 0)
					resID = getResources().getIdentifier("icon", "drawable", getPackageName());

				inWheel.WheelItemImages.add(resID);
				inWheel.WheelItemsChecked.add(false);

			}
		}
		inWheel.ShowCheckBoxesImages = false;
		inWheel.ShowIcons = true;
		inWheel.refreshViewAdapter();
		inWheel.refreshDrawableState();
		inWheel.setCurrentItem(0);

	}

	public void PopulateActivities(WheelView inWheel) 
	{

		// Get categories from DB
		inWheel.WheelItems = new ArrayList<String>();
		inWheel.WheelItemIDs = new ArrayList<String>();
		inWheel.WheelItemsChecked = new ArrayList<Boolean>();

		if(Global.selectedCategory.trim().equals("")) Global.selectedCategory = "Indoor";
		if(Global.selectedAttendance.trim().equals("")) Global.selectedCategory = "a";
		List<String> tlist = db.selectEnabledActivitiesWeighted(Global.selectedCategory, Global.selectedAttendance);

		int len = tlist.size();

		//TODO: hack - if road trip was selected, previously- clear it.
		if(len <= 0)
		{
			Global.selectedCategory = "Indoor";
			tlist = db.selectEnabledActivitiesWeighted(Global.selectedCategory, Global.selectedAttendance);
			len = tlist.size();
		}

		int generic = 1;
		for (int a = 0; a < len; a++) 
		{
			//Filter out inappropriate items
			if(!db.selectActivityAttendance(tlist.get(a)).contains("i"))
			{

				inWheel.WheelItemIDs.add(tlist.get(a));
				inWheel.WheelItems.add(db.selectActivityName(tlist.get(a)));


				inWheel.WheelItemImages.add(getResources().getIdentifier("generic" + generic, "drawable", getPackageName()));
				inWheel.WheelItemsChecked.add(false);

				generic++;
				if(generic > 15)
					generic = 1;
			}
		}

		inWheel.ShowCheckBoxesImages = false;
		inWheel.ShowIcons = true;
		inWheel.refreshViewAdapter();
		inWheel.refreshDrawableState();
		inWheel.setCurrentItem(0);
	}

	public void PopulateContacts(WheelView inWheel) 
	{

		// Get contacts from DB
		inWheel.WheelItems = new ArrayList<String>();
		inWheel.WheelItemsChecked = new ArrayList<Boolean>();
		List<String> tlist = db.selectContactsWeighted();

		int len = tlist.size();
		int heads = 1;
		for (int a = 0; a < len; a++) 
		{
			inWheel.WheelItems.add(tlist.get(a));

			inWheel.WheelItemImages.add(getResources().getIdentifier("heads" + heads, "drawable", getPackageName()));
			heads++;
			if(heads > 8) heads = 1;

			inWheel.WheelItemsChecked.add(false);
		}

		inWheel.ShowCheckBoxesImages = false;
		inWheel.ShowIcons = true;
		inWheel.refreshViewAdapter();
		inWheel.refreshDrawableState();
		inWheel.setCurrentItem(0);

	}

	public void ClearButtons() 
	{
		// Reset buttons
		btn_List_Category.setChecked(false);
		btn_List_Social.setChecked(false);
		btn_List_Activity.setChecked(false);
	}

	public void randomActivity() 
	{
		this.onEvent("Jackpot-HandlePulled");
		// If we are not currently spinning...
		if (!spinning) 
		{
			spunOnce = true;
			// Start the animation
			frameAnimation.stop();
			frameAnimation.start();

			spinning = true;

			btnSelectActivity.setBackgroundResource(R.drawable.grayboxbutton);

			// Randomize the unlocked wheels
			try 
			{
				if (!txtListSummary.getText().toString().contains("FREE SPIN"))
					txtListSummary.setText(getString(R.string.jackpot_rolling));

				int sel = rnd.nextInt(50);

				if(!locked1)
				{
					activityWheel1.scroller.scroll(sel * 3000, 3000);
					lastSpinningWheel = activityWheel1.getId();
				}
				if(!locked2)
				{
					activityWheel2.scroller.scroll(sel * 10000, 3500);
					lastSpinningWheel = activityWheel2.getId();
				}
				if(!locked3)
				{
					activityWheel3.scroller.scroll(sel * 10000, 4500);
					lastSpinningWheel = activityWheel3.getId();
				}
				if(!locked4)
				{
					activityWheel4.scroller.scroll(sel * 10000, 5500);
					lastSpinningWheel = activityWheel4.getId();
				}

				if(SharedPref.getVibrationOn())
					vib.vibrate(pattern, 0);

				if(SharedPref.getAudioOn())
					mp.start();
			} 
			catch (Exception ex) 
			{
				Global.Log.v("ERROR:randomActivity-jp-", ex.toString());
			}
		}
	}

	public void selectActivity() 
	{
		if(hasSelection)
		{
			this.startActivity(ActivityFactory.getLocalOptionsActivity(this));
			this.finish();
		}
		else
			txtListSummary.setText(getString(R.string.jackpot_warnselect));

	}

	// Handle UI clicks
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
		case R.id.jackpot_wheelrighthandle:
			if(Accessibility.AccessibilityEnabled(this))
			{
				jackpotHandle.setBackgroundResource(R.anim.anim_handle_up);
				frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
				randomActivity();				
			}
			break;
		case R.id.btn_jackpot_SelectActivity:
			if(spunOnce)
				selectActivity();
			break;

		case R.id.btn_jackpot_Lock1:
			if(spunOnce)
			{
				if(locked1)
				{
					if(!locked4)
					{
						if(locked2)
						{
							locked2 = false;
							btnLock2.setImageResource(R.drawable.ulock);

						}
						locked1 = false;
						btnLock1.setImageResource(R.drawable.ulock);

					}
				}
				else
				{
					locked1 = true;
					btnLock1.setImageResource(R.drawable.lock);
					if(Global.selectedAttendance.equals("a"))
					{
						locked2 = true;
						btnLock2.setImageResource(R.drawable.lock);					
					}
				}
			}
			break;
		case R.id.btn_jackpot_Lock2:
			if(spunOnce)
			{
				if(locked2)
				{
					if((locked1) && (Global.selectedAttendance.equals("a")))
					{}
					else
					{
						locked2 = false;
						btnLock2.setImageResource(R.drawable.ulock);
					}
				}
				else
				{
					locked2 = true;
					btnLock2.setImageResource(R.drawable.lock);
					locked1 = true;
					btnLock1.setImageResource(R.drawable.lock);
				}
			}
			break;
		case R.id.btn_jackpot_Lock3:
			if(spunOnce)
			{
				if(locked3)
				{
					if(!locked4)
					{
						locked3 = false;
						btnLock3.setImageResource(R.drawable.ulock);
					}
				}
				else
				{
					locked3 = true;
					btnLock3.setImageResource(R.drawable.lock);
				}
			}
			break;
		case R.id.btn_jackpot_Lock4:
			if(spunOnce)
			{
				if(locked4)
				{
					locked4 = false;
					btnLock4.setImageResource(R.drawable.ulock);
				}
				else
				{
					locked4 = true;
					btnLock4.setImageResource(R.drawable.lock);
					locked3 = true;
					btnLock3.setImageResource(R.drawable.lock);
					locked1 = true;
					btnLock1.setImageResource(R.drawable.lock);
					if(Global.selectedAttendance.equals("a"))
					{
						locked2 = true;
						btnLock2.setImageResource(R.drawable.lock);					
					}
				}
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
			if ((curTime - lastUpdate) > 200) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				x = se.values[SensorManager.DATA_X];

				float speed = Math.abs(x - last_x) / diffTime * 10000;

				if (speed > SHAKE_THRESHOLD) {
					if(!(locked1 && locked2 && locked3 && locked4))
					{
						jackpotHandle.setBackgroundResource(R.anim.anim_handle_up);
						frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
						randomActivity();
					}
				}
				last_x = x;
			}

		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onScrollingStarted(WheelView wheel) {}

	@Override
	public void onScrollingFinished(WheelView wheel)
	{
		// Refresh
		wheelRefresh(wheel.getId());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		switch ( event.getAction() ) 
		{
		case MotionEvent.ACTION_DOWN: 
			if (!spinning) 
			{
				if(!(locked1 && locked2 && locked3 && locked4))
				{
					jackpotHandle.setBackgroundResource(R.anim.anim_handle_down);
					frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
					frameAnimation.stop();
					frameAnimation.start();
				}
			}
			break;
		case MotionEvent.ACTION_UP: 
			if(v.getId() == R.id.jackpot_wheelrighthandle)
			{
				if(!(locked1 && locked2 && locked3 && locked4))
				{
					jackpotHandle.setBackgroundResource(R.anim.anim_handle_up);
					frameAnimation = (AnimationDrawable) jackpotHandle.getBackground();
					randomActivity();
				}
			}
			break;
		}
		return true;
	}

}

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

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

import org.t2health.paj.classes.Accessibility;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;

import t2.paj.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Let the user deselect certain activities they do not want to participate in.
 * Deselected activities will not appear in the customize screen or the Jackpot screen.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class SetupCategoriesActivity extends ABSCustomTitleActivity implements OnClickListener 
{

	// Wheel widget
	private WheelView categoryWheel;
	private WheelView activityWheel;
	private DatabaseProvider db = new DatabaseProvider(this);
	public Button btnSelectActivities;

	public Button btn_ChangeCategory;
	public Button btn_ChangeActivity;
	public Button btn_ToggleActivity;

	public TextView tv_infobox;
	public LinearLayout ll_accessibility;
	
	public int curCSelection = 0;
	public int curASelection = 0;

	//public CheckBox chkToggle;
	List<String> clist;
	List<String> alist;

	private ProgressDialog m_ProgressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupcategories);
		this.onEvent("SetupCategories-Open");
		
		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);

		// Button Mappings
		btnSelectActivities = (Button) this.findViewById(R.id.btn_Setup_SelectActivities);
		btnSelectActivities.setOnClickListener(this);

		btn_ChangeCategory = (Button) this.findViewById(R.id.btn_ChangeCategory);
		btn_ChangeCategory.setOnClickListener(this);
		btn_ChangeActivity = (Button) this.findViewById(R.id.btn_ChangeActivity);
		btn_ChangeActivity.setOnClickListener(this);
		btn_ToggleActivity = (Button) this.findViewById(R.id.btn_ToggleActivity);
		btn_ToggleActivity.setOnClickListener(this);

		tv_infobox = (TextView) this.findViewById(R.id.tv_infobox);
		ll_accessibility = (LinearLayout) this.findViewById(R.id.ll_accessibility);

		// Setup scroll events on the wheels
		categoryWheel = (WheelView) this.findViewById(R.id.category_activityWheel);
		categoryWheel.setVisibleItems(3);
		categoryWheel.setBackgroundResource(R.drawable.rollerbackleft);
		categoryWheel.setForegroundResource(R.drawable.highlightbarleft);
		categoryWheel.ShowShadows = true;
		categoryWheel.setCyclic(true);
		categoryWheel.addScrollingListener(new OnWheelScrollListener() 
		{
			public void onScrollingStarted(WheelView wheel) {}
			public void onScrollingFinished(WheelView wheel) 
			{
				// Refresh
				PopulateActivities();
			}
		});
		categoryWheel.addClickingListener(new OnWheelClickedListener() 
		{
			public void onItemClicked(WheelView wheel, int itemIndex) 
			{
				if (categoryWheel.WheelItemsChecked.get(itemIndex))
					categoryWheel.WheelItemsChecked.set(itemIndex, false);
				else
					categoryWheel.WheelItemsChecked.set(itemIndex, true);

				categoryWheel.refreshViewAdapter();
			}
		});

		activityWheel = (WheelView) this.findViewById(R.id.activity_activityWheel);
		activityWheel.setVisibleItems(3);
		activityWheel.setBackgroundResource(R.drawable.rollerbackright);
		activityWheel.setForegroundResource(R.drawable.highlightbarright);
		activityWheel.ShowShadows = true;
		activityWheel.ShowCheckBoxes = true;
		activityWheel.addChangingListener(new OnWheelChangedListener() 
		{
			public void onChanged(WheelView wheel, int oldValue, int newValue) {}
		});
		activityWheel.addClickingListener(new OnWheelClickedListener() 
		{
			public void onItemClicked(WheelView wheel, int itemIndex) 
			{
				if (activityWheel.WheelItemsChecked.get(itemIndex))
				{
					activityWheel.WheelItemsChecked.set(itemIndex, false);
					db.updateActivityEnabled(Integer.parseInt(activityWheel.WheelItemIDs.get(itemIndex)), 0);
				}
				else
				{
					activityWheel.WheelItemsChecked.set(itemIndex, true);
					db.updateActivityEnabled(Integer.parseInt(activityWheel.WheelItemIDs.get(itemIndex)), 1);
				}

				activityWheel.refreshViewAdapter();
			}
		});

		//If accessibility enabled, make some usability changes
		if(Accessibility.AccessibilityEnabled(this))
		{
			categoryWheel.setFocusable(false);
			activityWheel.setFocusable(false);
			activityWheel.setCyclic(true);
			ll_accessibility.setVisibility(View.VISIBLE);
		}
		else
			ll_accessibility.setVisibility(View.GONE);

		//		chkToggle = (CheckBox) this.findViewById(R.id.chkToggle);
		//		chkToggle.setChecked(true);
		//		chkToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
		//		{
		//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		//			{
		//				if ( isChecked )
		//				{
		//					//check everything
		//					db.updateAllActivitiesEnabled();
		//					PopulateCategoriesThreaded();
		//				}
		//				else
		//				{
		//					//Uncheck everything
		//					db.updateAllActivitiesDisabled();
		//					PopulateCategoriesThreaded();
		//				}
		//
		//			}
		//		});

		PopulateCategoriesThreaded();
	}

	//	@Override 
	//	public void onStart()
	//	{
	//		super.onStart();
	//
	//		ShowHelpTip("setupcategoriesactivity", getString(R.string.tips_category));
	//
	//	}

	public void PopulateCategoriesThreaded()
	{
		m_ProgressDialog = ProgressDialog.show(this, "Please wait...", " Loading activities ...", true);

		Runnable myRunnable = new Runnable() 
		{
			public void run() 
			{
				clist = db.selectCategories();
				runOnUiThread(PopulateCategoriesResult);
			}
		};

		Thread thread = new Thread(null, myRunnable, "ActivitiesThread");
		thread.start();
	}

	private Runnable PopulateCategoriesResult = new Runnable() {
		public void run() {
			PopulateCategories() ;
		}
	};

	public void PopulateCategories() 
	{
		try
		{
			categoryWheel.scroller.scroll(50 * 4000, 45600);

			// Get categories from DB
			categoryWheel.WheelItems = new ArrayList<String>();
			categoryWheel.WheelItemsChecked = new ArrayList<Boolean>();
			categoryWheel.WheelItemImages = new ArrayList<Integer>();


			int len = clist.size();
			for (int a = 0; a < len; a++) 
			{
				if(!clist.get(a).contains("Road Trip"))
				{
					categoryWheel.WheelItems.add(clist.get(a));
					int resID = getResources().getIdentifier(clist.get(a).toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
					if (resID == 0)
						resID = getResources().getIdentifier("icon", "drawable",getPackageName());

					categoryWheel.WheelItemsChecked.add(false);
				}
			}

			categoryWheel.ShowCheckBoxes = false;
			categoryWheel.refreshViewAdapter();
			categoryWheel.refreshDrawableState();
			categoryWheel.setCurrentItem(0);

			Global.selectedActivity = "";
			Global.selectedActivityID = "";

			categoryWheel.stopScrolling();
		}
		catch(Exception ex){}

	}

	public void PopulateActivities() 
	{
		try{
			Global.selectedCategory = categoryWheel.WheelItems.get(categoryWheel.getCurrentItem());

			// Get categories from DB
			activityWheel.WheelItems = new ArrayList<String>();
			activityWheel.WheelItemIDs = new ArrayList<String>();
			activityWheel.WheelItemImages = new ArrayList<Integer>();
			activityWheel.WheelItemsChecked = new ArrayList<Boolean>();
			List<String> alist = db.selectAllActivities(Global.selectedCategory, "%");

			int len = alist.size();
			for (int a = 0; a < len; a++) 
			{
				activityWheel.WheelItemIDs.add(alist.get(a));
				activityWheel.WheelItems.add(db.selectActivityName(alist.get(a)));
				activityWheel.WheelItemsChecked.add(db.selectActivityEnabled(alist.get(a)));
			}
			activityWheel.ShowCheckBoxes = false;
			activityWheel.refreshViewAdapter();
			activityWheel.refreshDrawableState();
			activityWheel.setCurrentItem(0);

			Global.selectedActivity = "";
			Global.selectedActivityID = "";

			activityWheel.ShowCheckBoxes = true;
			activityWheel.refreshViewAdapter();
			//activityWheel.scroller.scroll(100, 10);
			m_ProgressDialog.dismiss();
		}
		catch(Exception ex){
			Global.Log.v("PopulateActivities", ex.toString());
			m_ProgressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) 
	{
		super.onClick(v);
		switch (v.getId()) 
		{
		case R.id.btn_Setup_SelectActivities:
			//make sure at least ** activity is enabled
			List<String> clist = db.checkMinActivity();
			if(clist.size() >= 10)
			{
				Global.walkthroughStep = 2;
				this.finish();
			}
			else
				ShowAlert();

			break;
		case R.id.btn_ChangeCategory:
			curCSelection++;
			if(curCSelection >= categoryWheel.WheelItems.size()) curCSelection = 0;
			categoryWheel.scroll(1, 550);
			//categoryWheel.scroller.scroll(50, 50);
			Accessibility.speak(Toast.makeText(this, categoryWheel.WheelItems.get(curCSelection), Toast.LENGTH_SHORT));
			curASelection = 0;
			break;
		case R.id.btn_ChangeActivity:
			curASelection++;
			if(curASelection >= activityWheel.WheelItems.size()) curASelection = 0;
			activityWheel.scroll(1, 550);
			//activityWheel.scroller.scroll(50, 50);
			Accessibility.speak(Toast.makeText(this, "" + activityWheel.WheelItems.get(curASelection), Toast.LENGTH_SHORT));
			break;
		case R.id.btn_ToggleActivity:
			if (activityWheel.WheelItemsChecked.get(activityWheel.getCurrentItem()))
			{
				activityWheel.WheelItemsChecked.set(activityWheel.getCurrentItem(), false);
				db.updateActivityEnabled(Integer.parseInt(activityWheel.WheelItemIDs.get(activityWheel.getCurrentItem())), 0);
				Accessibility.speak(Toast.makeText(this, activityWheel.WheelItems.get(activityWheel.getCurrentItem()) + " unchecked", Toast.LENGTH_SHORT));
			}
			else
			{
				activityWheel.WheelItemsChecked.set(activityWheel.getCurrentItem(), true);
				db.updateActivityEnabled(Integer.parseInt(activityWheel.WheelItemIDs.get(activityWheel.getCurrentItem())), 1);
				Accessibility.speak(Toast.makeText(this, activityWheel.WheelItems.get(activityWheel.getCurrentItem()) + " checked", Toast.LENGTH_SHORT));
			}

			activityWheel.refreshViewAdapter();
			
			break;

		}

	}

	public void ShowAlert()
	{
		//Alert the results
		new AlertDialog.Builder(this)
		.setMessage(getString(R.string.setup_minactivities))
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		})
		.create()
		.show();
	}

}

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

import java.util.List;

import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;

import t2.paj.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Rating screen lets the user rate previous activies that they have performed. 
 * The resulting data is then used to weight the future selection frequency on the Jackpot screen.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class RateActivity extends ABSCustomTitleActivity implements	OnClickListener 
{

	private int currentActivity = 0;
	private List<String[]> activityList;
	private List<String[]> contactList;
	private DatabaseProvider db = new DatabaseProvider(this);
	//private String selectedDate = "";
	//private String selectedLocal = "";
	//private String selectedContacts = "";

	private static float socialRatingValue = 0;
	private static float contactsRatingValue = 0;
	private static float categoryRatingValue = 0;
	private static float activityRatingValue = 0;

	public static final int menuShowRated = Menu.FIRST + 1;
	private static int selectedRating = 0;
	private static String selectedTitle = "";

	// Controls
	private ImageView socialIcon;
	private TextView socialTitle;
	private Button socialRate;
	private static RatingBar socialRating;

	private ImageView contactsIcon;
	private TextView contactsTitle;
	private Button contactsRate;
	private RatingBar contactsRating;

	private ImageView categoryIcon;
	private TextView categoryTitle;
	private Button categoryRate;
	private RatingBar categoryRating;

	private ImageView activityIcon;
	private TextView activityTitle;
	private Button activityRate;
	private RatingBar activityRating;

	private Button btnDone;
	//	private Button btnPrevious;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate);
		this.onEvent("RateActivity-Open");
		
		this.SetMenuVisibility(1);
		this.btnMainPrevious.setChecked(true);

		// Control Mappings
		socialIcon = (ImageView) this.findViewById(R.id.rate_social_icon);
		socialTitle = (TextView) this.findViewById(R.id.rate_social_title);
		socialRate = (Button) this.findViewById(R.id.rate_social_button);
		socialRate.setOnClickListener(this);
		socialRating = (RatingBar) findViewById(R.id.rate_social_ratingbar);

		contactsIcon = (ImageView) this.findViewById(R.id.rate_contacts_icon);
		contactsTitle = (TextView) this.findViewById(R.id.rate_contacts_title);
		contactsRate = (Button) this.findViewById(R.id.rate_contacts_button);
		contactsRate.setOnClickListener(this);
		contactsRating = (RatingBar) findViewById(R.id.rate_contacts_ratingbar);

		categoryIcon = (ImageView) this.findViewById(R.id.rate_category_icon);
		categoryTitle = (TextView) this.findViewById(R.id.rate_category_title);
		categoryRate = (Button) this.findViewById(R.id.rate_category_button);
		categoryRate.setOnClickListener(this);
		categoryRating = (RatingBar) findViewById(R.id.rate_category_ratingbar);

		activityIcon = (ImageView) this.findViewById(R.id.rate_activity_icon);
		activityTitle = (TextView) this.findViewById(R.id.rate_activity_title);
		activityRate = (Button) this.findViewById(R.id.rate_activity_button);
		activityRate.setOnClickListener(this);
		activityRating = (RatingBar) findViewById(R.id.rate_activity_ratingbar);

		btnDone = (Button) this.findViewById(R.id.btn_rate_done);
		btnDone.setOnClickListener(this);
		//		btnPrevious = (Button) this.findViewById(R.id.btn_rate_Prev);
		//		btnPrevious.setOnClickListener(this);

		// Query db for saved activities
		LoadActivities();

	}

	private void LoadActivities() 
	{
		// DB Query
		activityList = db.selectSavedActivity(Integer.parseInt(Global.selectedActivityID)); 
		contactList = db.selectSavedActivityContacts(Integer.parseInt(Global.selectedActivityID)); 
		currentActivity = 0;
		DisplayActivity();
		//refreshButtons();
	}

//	@Override
//	public void onStart()
//	{
//		super.onStart();
//
//		ShowHelpTip("rateactivity", getString(R.string.tips_rate));
//
//	}
	
	private void DisplayActivity() 
	{
		String Contacts = "";
		int len = contactList.size();
		for (int a = 0; a < len; a++) 
		{
			Contacts += contactList.get(a)[0] + "\r\n";
		}

		// Make sure we're not out of bounds somehow
		if (activityList.size() > currentActivity) 
		{
			// Parse all the saved data
			Global.selectedActivityID = activityList.get(currentActivity)[0];
			Global.selectedAttendance = activityList.get(currentActivity)[1];
			Global.selectedCategory = activityList.get(currentActivity)[2];
			Global.selectedActivity = activityList.get(currentActivity)[3];
			
			//TODO: Road trip hack
			if(Global.selectedCategory == null)
			{
				String localOption = activityList.get(currentActivity)[5];
				if(localOption.startsWith("RT:"))
				{
					String[] rtsplit = localOption.split("\\^");
					localOption = rtsplit[0].replace("RT:", "");
					Global.selectedCategory = "Road Trip";
					Global.selectedActivity = rtsplit[1] + "\r\n" + localOption;
					categoryRate.setContentDescription("Rate Road Trip Category");
				}
				
			}
			else
			{
				//Add local option display
				Global.selectedActivity += "\r\n" + activityList.get(currentActivity)[5];
				categoryRate.setContentDescription("Rate " + Global.selectedCategory + " Category");
			}
			
			try
			{
			socialRatingValue = new Float(activityList.get(currentActivity)[6]);
			socialRating.setRating(socialRatingValue);
			}
			catch(Exception ex){}
			try
			{
			categoryRatingValue = new Float(activityList.get(currentActivity)[7]);
			categoryRating.setRating(categoryRatingValue);
			}
			catch(Exception ex){}
			try
			{
			activityRatingValue = new Float(activityList.get(currentActivity)[8]);
			activityRating.setRating(activityRatingValue);
			}
			catch(Exception ex){}
			try
			{
			contactsRatingValue = new Float(activityList.get(currentActivity)[9]);
			contactsRating.setRating(contactsRatingValue);
			}
			catch(Exception ex){}
			
			// Data Display
			int resID = 0;
			String titleString = "";
			if(Global.selectedAttendance.equals("a"))
			{
				resID = R.drawable.solo;
				LinearLayout llContacts = (LinearLayout) this.findViewById(R.id.rate_contacts_box);
				llContacts.setVisibility(View.GONE);
				titleString = "Solo Activity";
				socialRate.setContentDescription("Rate Solo Activity Type");
			}
			else if(Global.selectedAttendance.equals("c"))
			{
				resID = R.drawable.pair;
				titleString = "Pair Activity";
				socialRate.setContentDescription("Rate Pair Activity Type");

			}
			else if(Global.selectedAttendance.equals("g"))
			{
				resID = R.drawable.group;
				titleString = "Group Activity";
				socialRate.setContentDescription("Rate Group Activity Type");

			}
			socialIcon.setImageResource(resID);
			socialTitle.setText(titleString);
			
			contactsIcon.setImageResource(R.drawable.heads2);
			contactsTitle.setText(Contacts);
			contactsRate.setContentDescription("Rate Contacts: " + Contacts);


			resID = getResources().getIdentifier(Global.selectedCategory.toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
			categoryIcon.setImageResource(resID);
			categoryTitle.setText(Global.selectedCategory);

			activityIcon.setImageResource(resID);
			activityTitle.setText(Global.selectedActivity);
			activityRate.setContentDescription("Rate Activity: " + Global.selectedActivity);

		}
	}

	private void SaveRating() 
	{
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.rate_dialog, (ViewGroup) findViewById(R.id.root_layout));
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		final RatingBar rb = (RatingBar) layout.findViewById(R.id.dialogratingbar);
		final TextView tv = (TextView) layout.findViewById(R.id.dialogtitle);
		tv.setText(selectedTitle);

		dialog.setView(layout).setCancelable(true)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				switch (selectedRating) {
				case R.id.rate_social_button:
					socialRating.setNumStars(5);
					socialRating.setRating(rb.getRating());
					db.insertRating(Integer.parseInt(Global.selectedActivityID), "AttendanceRating", rb.getRating() + "");
					break;
				case R.id.rate_contacts_button:
					contactsRating.setNumStars(5);
					contactsRating.setRating(rb.getRating());
					db.insertRating(Integer.parseInt(Global.selectedActivityID), "ContactRating", rb.getRating() + "");
					break;
				case R.id.rate_category_button:
					categoryRating.setNumStars(5);
					categoryRating.setRating(rb.getRating());
					db.insertRating(Integer.parseInt(Global.selectedActivityID), "CategoryRating", rb.getRating() + "");
					break;
				case R.id.rate_activity_button:
					activityRating.setNumStars(5);
					activityRating.setRating(rb.getRating());
					db.insertRating(Integer.parseInt(Global.selectedActivityID), "ActivityRating", rb.getRating() + "");
					break;
				}

				dialog.cancel();
			}
		})
		.show();
	}    

	// Handle UI clicks
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
		case R.id.rate_social_button:
			selectedRating = R.id.rate_social_button;
			selectedTitle = "Social Rating";
			SaveRating();
			break;
		case R.id.rate_contacts_button:
			selectedRating = R.id.rate_contacts_button;
			selectedTitle = "Contacts Rating";
			SaveRating();
			break;
		case R.id.rate_category_button:
			selectedRating = R.id.rate_category_button;
			selectedTitle = "Category Rating";
			SaveRating();
			break;
		case R.id.rate_activity_button:
			selectedRating = R.id.rate_activity_button;
			selectedTitle = "Activity Rating";
			SaveRating();
			break;
		case R.id.btn_rate_done:
			finish();
			break;
		}

	}

}

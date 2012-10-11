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

import org.t2health.paj.classes.ActivityFactory;

import t2.paj.R;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Setup activity is a navigation screen presenting all options that affect application functionality.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class SetupActivity extends ABSCustomTitleActivity 
{

	public ImageView ivlearn;
	public TextView tvlearn;

	public ImageView ivchoosecontacts;
	public TextView tvchoosecontacts;

	public ImageView ivchooseactivities;
	public TextView tvchooseactivities;

	public ImageView ivpreferences;
	public TextView tvpreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);

		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);
		this.onEvent("SetupScreen-Open");

		ivlearn = (ImageView)this.findViewById(R.id.ivlearn);
		ivlearn.setOnClickListener(this);
		tvlearn = (TextView)this.findViewById(R.id.tvlearn);
		tvlearn.setOnClickListener(this);

		ivchoosecontacts = (ImageView)this.findViewById(R.id.ivchoosecontacts);
		ivchoosecontacts.setOnClickListener(this);
		tvchoosecontacts = (TextView)this.findViewById(R.id.tvchoosecontacts);
		tvchoosecontacts.setOnClickListener(this);

		ivchooseactivities = (ImageView)this.findViewById(R.id.ivchooseactivities);
		ivchooseactivities.setOnClickListener(this);
		tvchooseactivities = (TextView)this.findViewById(R.id.tvchooseactivities);
		tvchooseactivities.setOnClickListener(this);

		ivpreferences = (ImageView)this.findViewById(R.id.ivpreferences);
		ivpreferences.setOnClickListener(this);
		tvpreferences = (TextView)this.findViewById(R.id.tvpreferences);
		tvpreferences.setOnClickListener(this);
	}

//	@Override 
//	public void onStart()
//	{
//		super.onStart();
//		ShowHelpTip("setupactivity", getString(R.string.tips_setup));
//	}
	
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
		case R.id.ivlearn:
			this.startActivity(ActivityFactory.getLearnActivity(this));
			break;
		case R.id.tvlearn:
			this.startActivity(ActivityFactory.getLearnActivity(this));
			break;
		case R.id.ivchoosecontacts:
			this.startActivity(ActivityFactory.getSetupContactsActivity(this));
			break;
		case R.id.tvchoosecontacts:
			this.startActivity(ActivityFactory.getSetupContactsActivity(this));
			break;
		case R.id.ivchooseactivities:
			this.startActivity(ActivityFactory.getSetupCategoriesActivity(this));
			break;
		case R.id.tvchooseactivities:
			this.startActivity(ActivityFactory.getSetupCategoriesActivity(this));
			break;
		case R.id.ivpreferences:
			this.startActivity(ActivityFactory.getPreferencesActivity(this));
			break;
		case R.id.tvpreferences:
			this.startActivity(ActivityFactory.getPreferencesActivity(this));
			break;
		}
	}

}

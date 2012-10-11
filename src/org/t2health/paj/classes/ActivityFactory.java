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
package org.t2health.paj.classes;

import org.t2health.paj.activity.ARActivity;
import org.t2health.paj.activity.CreateActivity;
import org.t2health.paj.activity.EulaActivity;
import org.t2health.paj.activity.JackpotActivity;
import org.t2health.paj.activity.LearnActivity;
import org.t2health.paj.activity.ListActivity;
import org.t2health.paj.activity.LocalOptionsActivity;
import org.t2health.paj.activity.PreferencesActivity;
import org.t2health.paj.activity.RateActivity;
import org.t2health.paj.activity.SavedActivities;
import org.t2health.paj.activity.SetupActivity;
import org.t2health.paj.activity.SetupCategoriesActivity;
import org.t2health.paj.activity.SetupContactsActivity;
import org.t2health.paj.activity.SplashActivity;
import org.t2health.paj.activity.SummaryActivity;
import org.t2health.paj.activity.WalkthroughActivity;
import org.t2health.paj.activity.WebViewActivity;

import t2.paj.R;
import android.content.Context;
import android.content.Intent;

/**
 * Can generate all Intents available in Activity Jackpot
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class ActivityFactory 
{

	// Splash
	public static Intent getSplashActivity(Context c) 
	{
		Intent intent = new Intent(c, SplashActivity.class);
		return intent;
	}

	// About
	public static Intent getAboutActivity(Context c) 
	{
		Intent intent = new Intent(c, WebViewActivity.class);
		intent.putExtra(WebViewActivity.EXTRA_TITLE_ID, "About");
		intent.putExtra(WebViewActivity.EXTRA_CONTENT_ID, R.string.about_content);
		return intent;
	}

	public static Intent getEULAActivity(Context c) 
	{
		Intent intent = new Intent(c, EulaActivity.class);
		return intent;
	}

	// Main
	public static Intent getWalkthroughActivity(Context c) 
	{
		Intent intent = new Intent(c, WalkthroughActivity.class);
		return intent;
	}

	// ARIntent (mixare)
	public static Intent getARActivity(Context c) 
	{
		Intent i = new Intent(c, ARActivity.class);
		return i;
	}

	public static Intent getARActivityFull(Context c) 
	{
		//Reset the places types to the whole list
		Global.selectedPlaceTypes = Global.positivePlaceTypes;
		Intent i = new Intent(c, ARActivity.class);
		return i;
	}

	// Main
	public static Intent getCreateActivity(Context c) 
	{
		Intent intent = new Intent(c, CreateActivity.class);
		return intent;
	}

	// Learn
	public static Intent getLearnActivity(Context c) 
	{
		Intent intent = new Intent(c, LearnActivity.class);
		return intent;
	}

	// Coach
	public static Intent getCoachActivity(Context c) 
	{
		Intent intent = new Intent(c, SetupActivity.class);
		return intent;
	}

	// My Activities
	public static Intent getPreviousActivity(Context c) 
	{
		Intent intent = new Intent(c, SavedActivities.class);
		return intent;
	}

	// Rate
	public static Intent getRateActivity(Context c) 
	{
		Intent intent = new Intent(c, RateActivity.class);
		return intent;
	}

	// Activity List
	public static Intent getListActivity(Context c) 
	{
		Intent intent = new Intent(c, ListActivity.class);
		return intent;
	}

	// Jackpot List
	public static Intent getJackpotActivity(Context c) 
	{
		Intent intent = new Intent(c, JackpotActivity.class);
		return intent;
	}

	// Local Options
	public static Intent getLocalOptionsActivity(Context c) 
	{
		Intent intent = new Intent(c, LocalOptionsActivity.class);
		return intent;
	}

	// Summary
	public static Intent getSummaryActivity(Context c) 
	{
		Intent intent = new Intent(c, SummaryActivity.class);
		return intent;
	}

	// Preferences
	public static Intent getPreferencesActivity(Context c) 
	{
		Intent intent = new Intent(c, PreferencesActivity.class);
		return intent;
	}

	// Setup Contacts
	public static Intent getSetupContactsActivity(Context c) 
	{
		Intent intent = new Intent(c, SetupContactsActivity.class);
		return intent;
	}

	// Setup Categories
	public static Intent getSetupCategoriesActivity(Context c)
	{
		Intent intent = new Intent(c, SetupCategoriesActivity.class);
		return intent;
	}

}

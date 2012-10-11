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

import org.t2health.paj.classes.Global;
import org.t2health.paj.classes.SharedPref;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.os.Bundle;

/**
 * Flurry class provides analytics and should be the base class for all activities.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class FlurryActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(SharedPref.getSendAnnonData())
		{
			FlurryAgent.onStartSession(this, Global.FLURRY_KEY);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(SharedPref.getSendAnnonData())
		{
			FlurryAgent.onEndSession(this);
		}
	}

	public void onEvent(String event) 
	{
		if(SharedPref.getSendAnnonData())
		{
			Global.Log.v("", "onEvent:"+event);

			FlurryAgent.logEvent(event);
		}
	}

	public void onError(String arg0, String arg1, String arg2)
	{
		if(SharedPref.getSendAnnonData())
		{
			FlurryAgent.onError(arg0, arg1, arg2);
		}
	}

	public void onPageView() 
	{
		if(SharedPref.getSendAnnonData())
		{
			FlurryAgent.onPageView();
		}
	}
}

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
import org.t2health.paj.classes.Global;
import org.t2health.paj.classes.SharedPref;

import t2.paj.R;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * This activity is only used the first time a user runs the application or until the 
 * setup has been completed. It walks the user through customization of the app.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class WalkthroughActivity extends ABSCustomTitleActivity implements	OnClickListener
{

	//Controls
	private TextView tvDescription;
	private Button btnBottom;
	
	CheckBox cbAudio;
	private static MediaPlayer mp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkthrough);
		this.onEvent("Walkthrough-Open");
		
		// Main Menu
		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);

		//Map Controls
		tvDescription = (TextView)this.findViewById(R.id.tv_description);
		tvDescription.setScrollbarFadingEnabled(false);

		btnBottom = (Button)this.findViewById(R.id.btn_Walkthrough_bottom);
		btnBottom.setOnClickListener(this);

		mp = MediaPlayer.create(this, R.raw.intro);
		mp.setLooping(false);
		
		cbAudio = (CheckBox)this.findViewById(R.id.cb_audio);
		cbAudio.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( isChecked )
		        {
		        	if(!mp.isPlaying())
		        		mp.start();
		        }
		        else
		        {
		        	if(mp.isPlaying())
		        	{
		        		mp.pause();
		        		mp.seekTo(0);
		        	}
		        }

		    }
		});
		
			if(!mp.isPlaying())
        		mp.start();
		
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		DisplayDescription();

		
		//mPlayer.start();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if(mp.isPlaying())
    	{
    		mp.pause();
    		mp.seekTo(0);
    	}
	}
	
	public void DisplayDescription()
	{
		//Build the HTML here and set to webview
		try
		{

			String results = "<HTML>";
			
			if(Global.walkthroughStep == 0)
				results += GetStepZero();
			else if(Global.walkthroughStep == 1)
				results += GetStepOne();
			else if(Global.walkthroughStep == 2)
				results += GetStepTwo();


			results += "</BODY>";
			results += "</HTML>";

//			wvDescription.setWebChromeClient(new WebChromeClient());
//			wvDescription.setBackgroundColor(Color.TRANSPARENT); 
//			wvDescription.setVerticalScrollBarEnabled(true);
//			WebSettings settings = wvDescription.getSettings();
//			settings.setJavaScriptEnabled(true);
//			wvDescription.loadDataWithBaseURL("", results, "text/html", "utf-8", null);
//			wvDescription.setVisibility(View.VISIBLE);
			
			tvDescription.setText(Html.fromHtml(results));
			tvDescription.setContentDescription(Html.fromHtml(results));
			
		}
		catch(Exception ex){}

	}

	public String GetStepZero()
	{
		btnBottom.setText("Setup Contacts");
		String output = "";
		output += "";
		output += "<B><center>Welcome to Positive Activity Jackpot!</center></B><P>Positive Activity Jackpot, or PAJ, helps you find fun, positive activities that you can do alone, in a pair or with a group. PAJ lets you either: choose from a list of over 375 possible activities (under the tab named Customize my Activity), have the app choose a random activity for you (under the tab named Jackpot), or shows you activities in your immediate surroundings using augmented reality technology (under the tab named Find Something Nearby).";
		output += "<P>The first step is to deselect all the individuals from your contact list that you donâ€™t think youâ€™d want to ever do an activity with.";
		output += "<P>Click the button below to begin.";

		return output;
	}

	public String GetStepOne()
	{
		btnBottom.setText("Setup Activities");
		String output = "";
		output += "";
		output += "<B><center>Welcome to Positive Activity Jackpot!</center></B>";
		output += "<P>Now you need to deselect all activities you would not like to try. Donâ€™t worry, if you decide you want to change any of these settings in the future, you can go back to the Settings tab and change this.";

		return output;
	}

	public String GetStepTwo()
	{
		btnBottom.setText("Done!");
		String output = "";
		output += "<B><center>Welcome to Positive Activity Jackpot!</center></B>";
		output += "<P>Thats it!";
		output += "<P>Click the button below to head to the main screen where you can start creating Positive Activities!";

		return output;
		//
	}

	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
		case R.id.btn_Walkthrough_bottom:
			if(Global.walkthroughStep == 0)
				this.startActivity(ActivityFactory.getSetupContactsActivity(this));
			else if(Global.walkthroughStep == 1)
				this.startActivity(ActivityFactory.getSetupCategoriesActivity(this));
			else if(Global.walkthroughStep == 2)
			{
				SharedPref.setCoached(true);
				this.startActivity(ActivityFactory.getCreateActivity(this));
				this.finish();
			}
			break;
		}
	}


}

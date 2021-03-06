/*
 * 
 * Positive Activity Jackpot
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
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

import org.t2health.paj.classes.SharedPref;

import t2.paj.R;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

/**
 * Controls the user modifiable application settings
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class PreferencesActivity extends ABSCustomTitleActivity implements OnCheckedChangeListener
{
	private ToggleButton tbAudio;
	private ToggleButton tbVibration;
	private ToggleButton tbData;
	//private ToggleButton tbTips;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);
		this.onEvent("PrefsScreen-Open");
		
		// Main Menu
		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);
		
		tbAudio = (ToggleButton)this.findViewById(R.id.toggle_audio);
		tbAudio.setChecked(SharedPref.getAudioOn());
		tbAudio.setOnCheckedChangeListener(this);

		tbVibration = (ToggleButton)this.findViewById(R.id.toggle_vibration);
		tbVibration.setChecked(SharedPref.getVibrationOn());
		tbVibration.setOnCheckedChangeListener(this);

		tbData = (ToggleButton)this.findViewById(R.id.toggle_anondata);
		tbData.setChecked(SharedPref.getSendAnnonData());
		tbData.setOnCheckedChangeListener(this);

//		tbTips = (ToggleButton)this.findViewById(R.id.toggle_tips);
//		tbTips.setChecked(SharedPref.getTipsOn());
//		tbTips.setOnCheckedChangeListener(this);

	}

//	@Override
//	public void onStart()
//	{
//		super.onStart();
//		ShowHelpTip("preferencesactivity", getString(R.string.tips_prefs));
//	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		SharedPref.setAudioOn(tbAudio.isChecked());
		SharedPref.setVibrationOn(tbVibration.isChecked());
		SharedPref.setSendAnnonData(tbData.isChecked());
		//SharedPref.setTipsOn(tbTips.isChecked());
	}

}

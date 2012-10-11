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

import org.t2health.paj.classes.SharedPref;

import t2.paj.R;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Learn activity uses a webview to display further information regarding the therapy behind positive activities
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class LearnActivity  extends ABSCustomTitleActivity
{

	CheckBox cbAudio;
	private static MediaPlayer mp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn);
		this.onEvent("LearnScreen-Open");
		
		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);
		
		mp = MediaPlayer.create(this, R.raw.about);
		mp.setLooping(false);
		
		cbAudio = (CheckBox)this.findViewById(R.id.cb_audio);
		cbAudio.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( isChecked )
		        {
		        	SharedPref.setLearnAudio(true);
		        	if(!mp.isPlaying())
		        		mp.start();
		        }
		        else
		        {
		        	SharedPref.setLearnAudio(false);
		        	if(mp.isPlaying())
		        	{
		        		mp.pause();
		        		mp.seekTo(0);
		        	}
		        }

		    }
		});
		cbAudio.setChecked(SharedPref.getLearnAudio());
		
		TextView tv = (TextView)this.findViewById(R.id.tv_learn);
		tv.setText(Html.fromHtml(getString(R.string.learn_content)));
		tv.setContentDescription(Html.fromHtml(getString(R.string.learn_content)));
		
		if(SharedPref.getLearnAudio())
			if(!mp.isPlaying())
        		mp.start();
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
	
}

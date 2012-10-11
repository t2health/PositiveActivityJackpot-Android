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

import t2.paj.R;

import org.t2health.paj.classes.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Displays an EULA screen
 * 
 */
public class EulaActivity extends ABSWebViewActivity 
{
	Button btnAccept;
	Button btnDeny;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.onEvent("EULAScreen-Open");
		
		btnAccept = (Button) this.findViewById(R.id.leftButton);
		btnAccept.setText(getString(R.string.eula_accept));
		btnAccept.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				AcceptPressed();
			}
		});

		btnDeny = (Button) this.findViewById(R.id.rightButton);
		btnDeny.setText("Decline");
		btnDeny.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				DeclinePressed();
			}
		});
		
		this.setTitle(getString(R.string.eula_title));
		this.setContent(getString(R.string.eula_content));
	}

	public void AcceptPressed() 
	{
		this.onEvent("EULA-Accepted");
		SharedPref.setIsEulaAccepted(true);
		if (SharedPref.getCoached()) 
		{
			this.startActivity(ActivityFactory.getCreateActivity(this));
		}
		else
		{
			this.startActivity(ActivityFactory.getWalkthroughActivity(this));
		}
		
		this.finish();
	}

	public void DeclinePressed() 
	{
		this.onEvent("EULA-Declined");
		this.setResult(0);
		this.finish();
	}

}

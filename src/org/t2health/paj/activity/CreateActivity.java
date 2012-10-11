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

import org.t2health.paj.classes.Accessibility;
import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;

import t2.paj.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Create screen provides navigation to all activity choosing functionality
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class CreateActivity extends ABSCustomTitleActivity
{
	public ImageView ivjackpot;
	public TextView tvjackpot;

	public ImageView ivlist;
	public TextView tvlist;

	public ImageView ivar;
	public TextView tvar;

	public LinearLayout llar;

	private DatabaseProvider db = new DatabaseProvider(this);

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		this.onEvent("CreateScreen-Open");
		
		this.SetMenuVisibility(1);
		this.btnMainCreate.setChecked(true);

		ivjackpot = (ImageView)this.findViewById(R.id.ivjackpot);
		ivjackpot.setOnClickListener(this);
		tvjackpot = (TextView)this.findViewById(R.id.tvjackpot);
		tvjackpot.setOnClickListener(this);

		ivlist = (ImageView)this.findViewById(R.id.ivlist);
		ivlist.setOnClickListener(this);
		tvlist = (TextView)this.findViewById(R.id.tvlist);
		tvlist.setOnClickListener(this);

		ivar = (ImageView)this.findViewById(R.id.ivar);
		ivar.setOnClickListener(this);
		tvar = (TextView)this.findViewById(R.id.tvar);
		tvar.setOnClickListener(this);

		llar = (LinearLayout) this.findViewById(R.id.llar);

		//No GPS, NO AR...
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			llar.setBackgroundResource(R.drawable.grayboxbutton);
		}

		try
		{
			Bundle extras = getIntent().getExtras();
			String isNotify = extras.getString("screen");
			if(isNotify.equals("savedactivities"))
				this.startActivity(ActivityFactory.getPreviousActivity(this));
		}
		catch(Exception ex){}

	}

	@Override
	public void onStart()
	{

		if(Accessibility.AccessibilityEnabled(this))
		{
			if(!Global.accessibleNavigationWarning)
			{
				new AlertDialog.Builder(this)
				.setMessage("This Android device uses a four direction D-Pad for navigation.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.create()
				.show();

				Global.accessibleNavigationWarning = true;
			}
		}
		ShowHelpTip("createactivity", getString(R.string.tips_create));

		super.onStart();
	}

	public void ShowContactsAlert()
	{
		//Alert the results
		new AlertDialog.Builder(this)
		.setMessage(getString(R.string.home_setupfirst))
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

	public void StartJackpot()
	{
		//Populate previously saved contacts
		List<String> clist = db.checkContacts();
		if(clist.size() > 0)
			this.startActivity(ActivityFactory.getJackpotActivity(this));
		else
			ShowContactsAlert();
	}

	public void StartList()
	{
		//Populate previously saved contacts
		List<String> clist = db.checkContacts();
		if(clist.size() > 0)
			this.startActivity(ActivityFactory.getListActivity(this));
		else
			ShowContactsAlert();
	}

	@Override
	public void onClick(View v) 
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		
		super.onClick(v);

		switch(v.getId()) 
		{
		case R.id.ivjackpot:
			StartJackpot();
			break;
		case R.id.tvjackpot:
			StartJackpot();
			break;
		case R.id.ivar:
			
			if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
				alertGPS();
			else
				warnAR();
			break;
		case R.id.tvar:
			if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
				alertGPS();
			else
				warnAR();
			break;
		case R.id.ivlist:
			StartList();
			break;
		case R.id.tvlist:
			StartList();
			break;
		}
	}

	private void warnAR()
	{
		if(!Accessibility.AccessibilityEnabled(this))
			this.startActivity(ActivityFactory.getARActivityFull(this));
		else
		{

			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
			myAlertDialog.setTitle("Accessibility");
			myAlertDialog.setMessage(getString(R.string.home_accessibility));
			myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					launchAR();
				}});
			myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					// do nothing
				}});
			myAlertDialog.show();

		}
	}
	
	private void alertGPS()
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) 
			this.startActivity(ActivityFactory.getARActivityFull(this));
		else
		{

			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
			myAlertDialog.setTitle("GPS Required");
			myAlertDialog.setMessage(getString(R.string.home_nogps));
			myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					
				}});
			myAlertDialog.show();

		}
	}

	private void launchAR()
	{
		this.startActivity(ActivityFactory.getARActivityFull(this));
	}

}

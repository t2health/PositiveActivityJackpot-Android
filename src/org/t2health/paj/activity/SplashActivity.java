package org.t2health.paj.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.ContentSearch;
import org.t2health.paj.classes.Global;
import org.t2health.paj.classes.SharedPref;

import t2.paj.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Opens a (non-dismissible) timed splash screen
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class SplashActivity extends ABSCustomTitleActivity implements OnClickListener 
{
	private Timer timeoutTimer;

	private Handler startHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			stopTimer();
			startStartActivity();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.onEvent("Splashscreen-Open");
		
		this.setContentView(R.layout.splash_activity);
		int splashDelay = 5000;
		if(Global.DebugOn)
			splashDelay = 500;

		timeoutTimer = new Timer();
		timeoutTimer.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				startHandler.sendEmptyMessage(0);
			}
		}, splashDelay);

		//Query road trip destinations
		try
		{

			//Query Google results in new thread
			Runnable myRunnable = new Runnable()
			{
				public void run() 
				{
					QueryRoatTripDestinations();
				}
			};

			Thread thread =  new Thread(null, myRunnable, "RTThread");
			thread.start();


		}
		catch(Exception ex)
		{
			Global.Log.v("SUMMARYSEARCHERROR", ex.toString());
		}

	}

	public void QueryRoatTripDestinations()
	{
		Looper.prepare();
		ContentSearch searcher = new ContentSearch(this.getBaseContext());
		try 
		{
			Global.RoadTripList = searcher.geoNamesOneDegreeCities();
		} 
		catch (Exception e) 
		{
			Global.Log.v("ERROR", e.toString());
		}

	}

	private void startStartActivity() 
	{
		if (SharedPref.getIsEulaAccepted()) 
		{
			startMainActivity();
			this.finish();
		} 
		else
		{
			startEulaActivity();
		}
	}

	private void startEulaActivity() 
	{
		this.stopTimer();
		this.startActivity(ActivityFactory.getEULAActivity(this));
		this.finish();
	}

	private void startMainActivity() 
	{
		this.stopTimer();
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

	@Override
	protected void onStop() 
	{
		super.onStop();
		this.stopTimer();
	}

	private void stopTimer() 
	{
		if (timeoutTimer != null) 
		{
			timeoutTimer.cancel();
			timeoutTimer = null;
		}
	}

	@Override
	public void onClick(View v) 
	{
		startStartActivity();
	}
}

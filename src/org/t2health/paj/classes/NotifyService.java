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

import org.t2health.paj.activity.CreateActivity;

import t2.paj.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class NotifyService extends Service {

	public class ServiceBinder extends Binder {
		NotifyService getService() {
			return NotifyService.this;
		}
	}

	private static final int NOTIFICATION = 123;
	public static final String INTENT_NOTIFY = "org.t2health.paar.INTENT_NOTIFY";
	private NotificationManager mNM;
	private DatabaseProvider db = new DatabaseProvider(this);

	@Override
	public void onCreate() {
		Log.i("NotifyService", "onCreate()");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);

		if(intent.getBooleanExtra(INTENT_NOTIFY, false))
			showNotification();		

		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new ServiceBinder();

	private void showNotification() {
		if(db.checkUnratedActivites())
		{
			CharSequence title = "Positive Activity Jackpot";
			int icon = R.drawable.icon;
			CharSequence text = "You've got some activities to rate!";		
			long time = System.currentTimeMillis();

			Notification notification = new Notification(icon, text, time);
			Intent pajIntent = new Intent(this, CreateActivity.class);
			pajIntent.putExtra("screen", "savedactivities");
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, pajIntent, 0);
			notification.setLatestEventInfo(this, title, text, contentIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			

			mNM.notify(NOTIFICATION, notification);
		}
		stopSelf();
	}
}

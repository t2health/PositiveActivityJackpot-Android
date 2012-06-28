package org.t2health.paj.classes;

import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ScheduleClient {

	private ScheduleService mBoundService;
	private Context mContext;
	private boolean mIsBound;

	public ScheduleClient(Context context) {
		mContext = context;
	}
	
	public void doBindService() {
		mContext.bindService(new Intent(mContext, ScheduleService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((ScheduleService.ServiceBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

	public void setAlarmForNotification(Calendar c){
		mBoundService.setAlarm(c);
	}
	
	public void doUnbindService() {
		if (mIsBound) {
			mContext.unbindService(mConnection);
			mIsBound = false;
		}
	}
}
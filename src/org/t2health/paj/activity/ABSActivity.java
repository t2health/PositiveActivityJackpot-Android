package org.t2health.paj.activity;


import java.util.List;

import org.t2health.paj.classes.Global;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public abstract class ABSActivity extends FlurryActivity
{

	
	@SuppressWarnings("unused")
	private boolean isContentViewSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Global.sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		this.onPageView();
	}



	@Override
	public void setContentView(int layoutResID) 
	{
		super.setContentView(layoutResID);
		this.isContentViewSet = true;
	}

	@Override
	public void setContentView(View view, LayoutParams params) 
	{
		super.setContentView(view, params);
		this.isContentViewSet = true;
	}

	@Override
	public void setContentView(View view) 
	{
		super.setContentView(view);
		this.isContentViewSet = true;
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}

	protected boolean isCallable(Intent intent) 
	{
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
}

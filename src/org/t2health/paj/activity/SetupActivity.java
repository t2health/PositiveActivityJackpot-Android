package org.t2health.paj.activity;

import org.t2health.paj.classes.ActivityFactory;

import t2.paj.R;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Setup activity is a navigation screen presenting all options that affect application functionality.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class SetupActivity extends ABSCustomTitleActivity 
{

	public ImageView ivlearn;
	public TextView tvlearn;

	public ImageView ivchoosecontacts;
	public TextView tvchoosecontacts;

	public ImageView ivchooseactivities;
	public TextView tvchooseactivities;

	public ImageView ivpreferences;
	public TextView tvpreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);

		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);
		this.onEvent("SetupScreen-Open");

		ivlearn = (ImageView)this.findViewById(R.id.ivlearn);
		ivlearn.setOnClickListener(this);
		tvlearn = (TextView)this.findViewById(R.id.tvlearn);
		tvlearn.setOnClickListener(this);

		ivchoosecontacts = (ImageView)this.findViewById(R.id.ivchoosecontacts);
		ivchoosecontacts.setOnClickListener(this);
		tvchoosecontacts = (TextView)this.findViewById(R.id.tvchoosecontacts);
		tvchoosecontacts.setOnClickListener(this);

		ivchooseactivities = (ImageView)this.findViewById(R.id.ivchooseactivities);
		ivchooseactivities.setOnClickListener(this);
		tvchooseactivities = (TextView)this.findViewById(R.id.tvchooseactivities);
		tvchooseactivities.setOnClickListener(this);

		ivpreferences = (ImageView)this.findViewById(R.id.ivpreferences);
		ivpreferences.setOnClickListener(this);
		tvpreferences = (TextView)this.findViewById(R.id.tvpreferences);
		tvpreferences.setOnClickListener(this);
	}

//	@Override 
//	public void onStart()
//	{
//		super.onStart();
//		ShowHelpTip("setupactivity", getString(R.string.tips_setup));
//	}
	
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
		case R.id.ivlearn:
			this.startActivity(ActivityFactory.getLearnActivity(this));
			break;
		case R.id.tvlearn:
			this.startActivity(ActivityFactory.getLearnActivity(this));
			break;
		case R.id.ivchoosecontacts:
			this.startActivity(ActivityFactory.getSetupContactsActivity(this));
			break;
		case R.id.tvchoosecontacts:
			this.startActivity(ActivityFactory.getSetupContactsActivity(this));
			break;
		case R.id.ivchooseactivities:
			this.startActivity(ActivityFactory.getSetupCategoriesActivity(this));
			break;
		case R.id.tvchooseactivities:
			this.startActivity(ActivityFactory.getSetupCategoriesActivity(this));
			break;
		case R.id.ivpreferences:
			this.startActivity(ActivityFactory.getPreferencesActivity(this));
			break;
		case R.id.tvpreferences:
			this.startActivity(ActivityFactory.getPreferencesActivity(this));
			break;
		}
	}

}

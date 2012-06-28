package org.t2health.paj.activity;

import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.SharedPref;
import org.t2health.paj.classes.ToggledButton;

import t2.paj.R;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ABSCustomTitleActivity extends ABSActivity implements OnClickListener
{
	private boolean initialized = false;
	private String title;

	private LinearLayout llMainMenu;
	public ToggledButton btnMainCreate;
	public ToggledButton btnMainPrevious;
	public ToggledButton btnMainSettings;

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void setContentView(int layoutResID) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(layoutResID);
		this.initCustomTitle();
	}
	//
	@Override
	public void setContentView(View view, LayoutParams params) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(view, params);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(view);
		this.initCustomTitle();
	}

	public void refreshContentView(int layoutResID) 
	{
		super.setContentView(layoutResID);
	}

	@Override
	public void setTitle(CharSequence title) 
	{
		if(initialized) 
		{
			super.setTitle(title);
		} 
		else 
		{
			this.title = title+"";
		}
	}

	@Override
	public void setTitle(int titleId) 
	{
		if(initialized) 
		{
			super.setTitle(titleId);
		} 
		else 
		{
			this.title = getString(titleId);
		}
	}

	protected void initCustomTitle() 
	{
		initialized = true;
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		if(this.title != null) 
		{
			super.setTitle(this.title);
		}

		llMainMenu = (LinearLayout)this.findViewById(R.id.llMainMenu);

		btnMainCreate = (ToggledButton)this.findViewById(R.id.btnMainCreate);
		btnMainCreate.setOnClickListener(this);

		btnMainPrevious = (ToggledButton)this.findViewById(R.id.btnMainPrevious);
		btnMainPrevious.setOnClickListener(this);

		btnMainSettings = (ToggledButton)this.findViewById(R.id.btnMainSettings);
		btnMainSettings.setOnClickListener(this);

	}

	protected void setStatus(String inText)
	{
		//TextView stat = (TextView) this.findViewById(R.id.StatusText);
		//stat.setText(inText);
	}

	protected boolean isCustomTitleInitialized() 
	{
		return initialized;
	}

	public void SetMenuVisibility(int visibility)
	{
		llMainMenu.setVisibility(visibility);
		llMainMenu.refreshDrawableState();
	}

	@Override
	public void onClick(View v) 
	{
		if (SharedPref.getCoached()) 
		{
			switch(v.getId()) 
			{
			case R.id.btnMainCreate:
				this.startActivity(ActivityFactory.getCreateActivity(this));
				if((this instanceof CreateActivity == false))
					this.finish();
				break;
			case R.id.btnMainPrevious:
				if(!(this instanceof SavedActivities))
				{
					this.startActivity(ActivityFactory.getPreviousActivity(this));
					if((this instanceof CreateActivity == false))
						this.finish();
				}
				break;
			case R.id.btnMainSettings:
				if(!(this instanceof SetupActivity))
				{
					this.startActivity(ActivityFactory.getCoachActivity(this));
					if((this instanceof CreateActivity == false))
						this.finish();
				}
				break;
			}
		}

	}

	public void ShowHelpTip(String key, String tipText)
	{
		final String fKey = key;
		if((SharedPref.getTipsOn(key)) && (!tipText.trim().equals("")))
		{
			//set up the "helpful" tip dialog
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.infopop);
			dialog.setTitle("Instructions:");
			dialog.setCancelable(true);

			TextView text = (TextView) dialog.findViewById(R.id.dialogbody);
			text.setText(tipText.trim());
			final CheckBox chk = (CheckBox) dialog.findViewById(R.id.dontShowAgain);
			Button button = (Button) dialog.findViewById(R.id.btnOK);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(chk.isChecked())
						SharedPref.setTipsOn(fKey, false);
					dialog.cancel();
				}
			});
			dialog.show();
		}
	}
}

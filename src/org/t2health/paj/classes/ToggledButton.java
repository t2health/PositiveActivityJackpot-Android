package org.t2health.paj.classes;

import t2.paj.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

public class ToggledButton extends Button 
{

	private AccessibilityManager aManager;
	private boolean isChecked = false;
	private int[] initialState;
	//
	public ToggledButton(Context context) 
	{
		super(context);
		this.init();
	}

	public ToggledButton(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.init();
	}

	public ToggledButton(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() 
	{
		aManager = (AccessibilityManager)this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
		initialState = super.getDrawableState();
		this.setTextColor(Color.BLACK);
	}

	public void setChecked(boolean isChecked) 
	{
		this.isChecked = isChecked;
		if(this.isChecked)
		{
			this.setTextColor(Color.WHITE);
			this.setBackgroundResource(R.drawable.togglebtnback_on);
		}
		else
		{
			this.setTextColor(Color.BLACK);
			this.setBackgroundResource(R.drawable.togglebtnback_off);
		}

		this.refreshDrawableState();
	}

	public boolean isChecked()
	{
		return isChecked;
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) 
	{
		int[] states;

		if(this.isChecked()) 
		{
			states = Button.PRESSED_WINDOW_FOCUSED_STATE_SET;
		} 
		else 
		{
			if(super.hasFocus()) 
			{
				states = super.onCreateDrawableState(extraSpace);
			} 
			else 
			{
				states = initialState;
			}
		}
//
		return states;
	}

	@SuppressWarnings("unused")
	private void speakText(String text, int queueMode) 
	{
		if(aManager.isEnabled()) 
		{
			aManager.interrupt();

			AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_FOCUSED);
			event.setPackageName(this.getClass().getPackage().toString());
			event.setClassName(this.getClass().getSimpleName());
			event.setContentDescription(text);
			event.setEventTime(System.currentTimeMillis());
			aManager.sendAccessibilityEvent(event);
		}
	}

}

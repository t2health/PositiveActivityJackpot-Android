/*
 * 
 * Positive Activity Jackpot
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
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
package kankan.wheel.widget;

import org.t2health.paj.classes.Global;

import t2.paj.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

	//public boolean ShowCheckBoxes = false;

	/** Text view resource. Used as a default view for adapter. */
	public final static int TEXT_VIEW_ITEM_RESOURCE = -1;

	/** No resource constant. */
	protected final static int NO_RESOURCE = 0;

	/** Default text color */
	public final int DEFAULT_TEXT_COLOR = 0xFF101010;

	/** Default text color */
	public final int LABEL_COLOR = 0xFF700070;

	/** Default text size */
	public final int DEFAULT_TEXT_SIZE = 24;

	// Text settings
	private int textColor = DEFAULT_TEXT_COLOR;
	private int textSize = DEFAULT_TEXT_SIZE;

	// Current context
	protected Context context;
	// Layout inflater
	protected LayoutInflater inflater;

	// Items resources
	protected int itemResourceId;
	protected int itemTextResourceId;

	// Empty items resources
	protected int emptyItemResourceId;

	/**
	 * Constructor
	 * @param context the current context
	 */
	protected AbstractWheelTextAdapter(Context context) {
		this(context, TEXT_VIEW_ITEM_RESOURCE);
	}

	/**
	 * Constructor
	 * @param context the current context
	 * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
	 */
	protected AbstractWheelTextAdapter(Context context, int itemResource) {
		this(context, itemResource, NO_RESOURCE);
	}

	/**
	 * Constructor
	 * @param context the current context
	 * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
	 * @param itemTextResource the resource ID for a text view in the item layout
	 */
	protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource) {
		this.context = context;
		itemResourceId = itemResource;
		itemTextResourceId = itemTextResource;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Gets text color
	 * @return the text color
	 */
	public int getTextColor() {
		return textColor;
	}

	/**
	 * Sets text color
	 * @param textColor the text color to set
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	/**
	 * Gets text size
	 * @return the text size
	 */
	public int getTextSize() {
		return textSize;
	}

	/**
	 * Sets text size
	 * @param textSize the text size to set
	 */
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	/**
	 * Gets resource Id for items views
	 * @return the item resource Id
	 */
	public int getItemResource() {
		return itemResourceId;
	}

	/**
	 * Sets resource Id for items views
	 * @param itemResourceId the resource Id to set
	 */
	public void setItemResource(int itemResourceId) {
		this.itemResourceId = itemResourceId;
	}

	/**
	 * Gets resource Id for text view in item layout 
	 * @return the item text resource Id
	 */
	public int getItemTextResource() {
		return itemTextResourceId;
	}

	/**
	 * Sets resource Id for text view in item layout 
	 * @param itemTextResourceId the item text resource Id to set
	 */
	public void setItemTextResource(int itemTextResourceId) {
		this.itemTextResourceId = itemTextResourceId;
	}

	/**
	 * Gets resource Id for empty items views
	 * @return the empty item resource Id
	 */
	public int getEmptyItemResource() {
		return emptyItemResourceId;
	}

	/**
	 * Sets resource Id for empty items views
	 * @param emptyItemResourceId the empty item resource Id to set
	 */
	public void setEmptyItemResource(int emptyItemResourceId) {
		this.emptyItemResourceId = emptyItemResourceId;
	}


	/**
	 * Returns text for specified item
	 * @param index the item index
	 * @return the text of specified items
	 */
	protected abstract CharSequence getItemText(int index);

	protected abstract boolean getItemChecked(int index);
	protected abstract int getItemImage(int index);

	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		if (index >= 0 && index < getItemsCount()) {
			if (convertView == null) {
				convertView = getView(itemResourceId, parent);
			}
			TextView textView = getTextView(convertView, itemTextResourceId);

			try
			{
				ImageView iView = getImageView(convertView, R.id.list_image1);
				iView.setImageResource(getItemImage(index));
				
			}
			catch(Exception ex){
				//Global.Log.v("ERROR:abstractWTAdapter-getItem", ex.toString());
			}
			
			try
			{
				CheckBox cbView = getCBView(convertView, R.id.checkbox);
				cbView.setChecked(getItemChecked(index));
				cbView.setVisibility(View.VISIBLE);
			}
			catch(Exception ex){}

			if (textView != null) {
				String text = (String) getItemText(index);
				if (text == null) {
					text = "";
				}


				textView.setText(text);

				if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
					configureTextView(textView);
				}
			}
			return convertView;
		}
		return null;
	}

	@Override
	public View getEmptyItem(View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getView(emptyItemResourceId, parent);
		}
		if (emptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE && convertView instanceof TextView) {
			configureTextView((TextView)convertView);
		}

		return convertView;
	}

	/**
	 * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
	 * @param view the text view to be configured
	 */
	protected void configureTextView(TextView view) {
		view.setTextColor(textColor);
		view.setGravity(Gravity.CENTER);
		view.setTextSize(textSize);
		//view.setLines(1);
		view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
	}

	/**
	 * Loads a text view from view
	 * @param view the text view or layout containing it
	 * @param textResource the text resource Id in layout
	 * @return the loaded text view
	 */
	private TextView getTextView(View view, int textResource) {
		TextView text = null;
		try {
			if (textResource == NO_RESOURCE && view instanceof TextView) {
				text = (TextView) view;
			} else if (textResource != NO_RESOURCE) {
				text = (TextView) view.findViewById(textResource);
			}
		} catch (ClassCastException e) {
			Global.Log.v("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"AbstractWheelAdapter requires the resource ID to be a TextView", e);
		}

		return text;
	}

	private CheckBox getCBView(View view, int textResource) {
		CheckBox text = null;
		try {
			if (textResource == NO_RESOURCE && view instanceof CheckBox) {
				text = (CheckBox) view;
			} else if (textResource != NO_RESOURCE) {
				text = (CheckBox) view.findViewById(textResource);
			}
		} catch (ClassCastException e) {
			Global.Log.v("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"AbstractWheelAdapter requires the resource ID to be a TextView", e);
		}

		return text;
	}
	
	private ImageView getImageView(View view, int textResource) {
		ImageView text = null;
		try {
			if (textResource == NO_RESOURCE && view instanceof ImageView) {
				text = (ImageView) view;
			} else if (textResource != NO_RESOURCE) {
				text = (ImageView) view.findViewById(textResource);
			}
		} catch (ClassCastException e) {
			Global.Log.v("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"AbstractWheelAdapter requires the resource ID to be a TextView", e);
		}

		return text;
	}

	/**
	 * Loads view from resources
	 * @param resource the resource Id
	 * @return the loaded view or null if resource is not set
	 */
	private View getView(int resource, ViewGroup parent) {
		switch (resource) {
		case NO_RESOURCE:
			return null;
		case TEXT_VIEW_ITEM_RESOURCE:
			return new TextView(context);
		default:
			return inflater.inflate(resource, parent, false);    
		}
	}
}
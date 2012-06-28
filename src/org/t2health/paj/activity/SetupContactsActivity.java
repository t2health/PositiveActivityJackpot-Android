package org.t2health.paj.activity;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.WheelView;

import org.t2health.paj.classes.Accessibility;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;

import t2.paj.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Lets the user deselect contacts that they do not wish to have as a participant option during activity selections.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class SetupContactsActivity extends ABSCustomTitleActivity implements OnClickListener 
{

	// Wheel widget
	private WheelView activityWheel;
	private DatabaseProvider db = new DatabaseProvider(this);
	public Button btnSelectContacts;
	public CheckBox chkToggle;

	public Button btn_prevcontact;
	public Button btn_nextcontact;
	public Button btn_ToggleContact;

	public int curSelection = 0;
	
	public LinearLayout ll_accessibility;

	private ProgressDialog m_ProgressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupcontacts);

		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);
		this.onEvent("SetupContacts-Open");
		
		// Button Mappings
		btnSelectContacts = (Button) this.findViewById(R.id.btn_Setup_SelectContacts);
		btnSelectContacts.setOnClickListener(this);

		btn_prevcontact = (Button) this.findViewById(R.id.btn_prevcontact);
		btn_prevcontact.setOnClickListener(this);
		btn_nextcontact = (Button) this.findViewById(R.id.btn_nextcontact);
		btn_nextcontact.setOnClickListener(this);
		btn_ToggleContact = (Button) this.findViewById(R.id.btn_ToggleContact);
		btn_ToggleContact.setOnClickListener(this);

		ll_accessibility = (LinearLayout) this.findViewById(R.id.ll_accessibility);

		// Setup scroll events on the wheel
		activityWheel = (WheelView) this.findViewById(R.id.Setup_activityWheel);
		activityWheel.setVisibleItems(3);
		activityWheel.DrawSelectionHighlight = false;
		activityWheel.ShowShadows = true;
		activityWheel.addClickingListener(new OnWheelClickedListener() 
		{
			public void onItemClicked(WheelView wheel, int itemIndex) 
			{
				if (activityWheel.WheelItemsChecked.get(itemIndex))
					activityWheel.WheelItemsChecked.set(itemIndex, false);
				else
					activityWheel.WheelItemsChecked.set(itemIndex, true);

				activityWheel.refreshViewAdapter();
			}
		});

		//If accessibility enabled, make some usability changes
		if(Accessibility.AccessibilityEnabled(this))
		{
			activityWheel.setFocusable(false);
			ll_accessibility.setVisibility(View.VISIBLE);
		}
		else
			ll_accessibility.setVisibility(View.GONE);

		chkToggle = (CheckBox) this.findViewById(R.id.chkToggle);
		chkToggle.setChecked(true);
		chkToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				
				if ( isChecked )
				{
					//check everything
					for (int a = 0; a < activityWheel.WheelItemsChecked.size(); a++) 
					{
						activityWheel.WheelItemsChecked.set(a, true);
					}
				}
				else
				{
					//Uncheck everything
					for (int a = 0; a < activityWheel.WheelItemsChecked.size(); a++) 
					{
						activityWheel.WheelItemsChecked.set(a, false);
					}
				}
				activityWheel.refreshViewAdapter();
				activityWheel.setCurrentItem(0);
			}
		});

		PopulateContacts();
	}

	//	@Override 
	//	public void onStart()
	//	{
	//		super.onStart();
	//
	//		ShowHelpTip("setupcontactsactivity", getString(R.string.tips_contacts));
	//
	//	}

	public void PopulateContacts() 
	{
		// Get categories from DB
		activityWheel.WheelItems = new ArrayList<String>();
		activityWheel.WheelItemsChecked = new ArrayList<Boolean>();
		activityWheel.WheelItemImages = new ArrayList<Integer>();

		//Populate all contacts from the phones contact list
		List<String> tlist = db.selectPhoneContacts(getBaseContext());

		//Populate previously saved contacts
		List<String> clist = db.selectContacts();
		int curclen = clist.size();
		int heads = 1;
		for (int a = 0; a < tlist.size(); a++) 
		{
			activityWheel.WheelItems.add(tlist.get(a));
			if(curclen > 0)
			{
				//This is not the first time they have saved contacts
				//Do not check any contacts that may have been deselected earlier

				if(clist.contains(tlist.get(a)))
					activityWheel.WheelItemsChecked.add(true);
				else
					activityWheel.WheelItemsChecked.add(false);
			}
			else
				activityWheel.WheelItemsChecked.add(true);

			activityWheel.WheelItemImages.add(getResources().getIdentifier("heads" + heads, "drawable", getPackageName()));
			heads++;
			if(heads > 8) heads = 1;
		}

		activityWheel.ShowCheckBoxesImages = true;
		activityWheel.refreshViewAdapter();
		activityWheel.setCurrentItem(0);
		
		//btn_ToggleContact.setContentDescription("Toggle " + activityWheel.WheelItems.get(0) + " checked");
	}

	public void SaveContacts() 
	{
		m_ProgressDialog = ProgressDialog.show(this, "Please wait...", " Saving your selected contacts ...", true);
		Global.walkthroughStep = 1;

		Runnable myRunnable = new Runnable() 
		{
			public void run() 
			{
				SaveContactsThreaded();
			}
		};

		Thread thread = new Thread(null, myRunnable, "ContactsThread");
		thread.start();

	}

	public void SaveContactsThreaded() 
	{
		Looper.prepare();

		ArrayList<String> clist = new ArrayList<String>();
		for (int a = 0; a < activityWheel.WheelItemsChecked.size(); a++) 
		{
			if (activityWheel.WheelItemsChecked.get(a)) 
			{
				clist.add(activityWheel.WheelItems.get(a));
			}
		}

		db.insertContacts(clist);
		m_ProgressDialog.dismiss();
		this.finish();
	}

	public void ShowAlert()
	{
		//Alert the results
		new AlertDialog.Builder(this)
		.setMessage(getString(R.string.setup_mincontacts))
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

	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
		case R.id.btn_Setup_SelectContacts:
			int savedCount = 0;
			for (int a = 0; a < activityWheel.WheelItemsChecked.size(); a++) 
			{
				if (activityWheel.WheelItemsChecked.get(a)) 
				{

					savedCount++;
				}
			}

			if(savedCount >= 5)
			{
				SaveContacts();
			}
			else
			{
				ShowAlert();
			}

			break;

		case R.id.btn_prevcontact:
			curSelection--;
			if(curSelection < 0) curSelection =0;
			activityWheel.scroll(-1, 550);
			//activityWheel.scroller.scroll(-50, 50);
			activityWheel.refreshViewAdapter();
			Accessibility.speak(Toast.makeText(this, activityWheel.WheelItems.get(curSelection), Toast.LENGTH_LONG));
			break;
		case R.id.btn_nextcontact:
			curSelection++;
			if(curSelection >= activityWheel.WheelItems.size()) curSelection = activityWheel.WheelItems.size() - 1;
			activityWheel.scroll(1, 550);
			//activityWheel.scroller.scroll(50, 50);
			activityWheel.refreshViewAdapter();
			Accessibility.speak(Toast.makeText(this, activityWheel.WheelItems.get(curSelection), Toast.LENGTH_LONG));
			break;
		case R.id.btn_ToggleContact:
			if (activityWheel.WheelItemsChecked.get(activityWheel.getCurrentItem()))
			{
				activityWheel.WheelItemsChecked.set(activityWheel.getCurrentItem(), false);
				//btn_ToggleContact.setContentDescription("Toggle " + activityWheel.WheelItems.get(curSelection) + " unchecked");
				Accessibility.speak(Toast.makeText(this, activityWheel.WheelItems.get(curSelection) + " unchecked",	Toast.LENGTH_LONG));
			}
			else
			{
				activityWheel.WheelItemsChecked.set(activityWheel.getCurrentItem(), true);
				//btn_ToggleContact.setContentDescription("Toggle " + activityWheel.WheelItems.get(curSelection) + " checked");
				Accessibility.speak(Toast.makeText(this, activityWheel.WheelItems.get(curSelection) + " checked",	Toast.LENGTH_LONG));
			}
			activityWheel.refreshViewAdapter();
			//btn_ToggleContact.requestFocus();
			break;

		}

	}

}

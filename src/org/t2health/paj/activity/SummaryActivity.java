package org.t2health.paj.activity;

import java.util.Calendar;
import java.util.List;

import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;

import t2.paj.R;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This activity is displayed (with context sensitive content) after the activity selection process,
 * and also when viewing saved activities. It provides all the data regarding the activity and presents
 * actions that are available. (navigation, phone call, invites, ratings)
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class SummaryActivity extends ABSCustomTitleActivity implements	OnClickListener
{

	private int currentActivity = 0;
	private List<String[]> activityList;
	//private List<String[]> contactList;
	private DatabaseProvider db = new DatabaseProvider(this);
	private String selectedEmails;
	private String selectedPhones = "";
	//private String selectedDate = "";
	private String selectedLocalName = "";
	private String selectedLocalAddress = "";
	private String selectedLocalPhone = "";
	List<String[]> contactList;

	//Controls
	private TextView tvDescription;
	private ImageView btnNav;
	private ImageView btnPhone;
	private ImageView btnShare;
	private ImageView btnCalendar;
	private ImageView btnRate;
	private Button btnDone;

	public static final int Menu1 = Menu.FIRST + 1;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary);

		this.onEvent("SummaryScreen-Open");
		
		// Main Menu
		this.SetMenuVisibility(1);

		if(Global.createMode)
			this.btnMainCreate.setChecked(true);
		else
			this.btnMainPrevious.setChecked(true);

		//Map Controls
		tvDescription = (TextView)this.findViewById(R.id.tv_description);
		tvDescription.setScrollbarFadingEnabled(false);
		tvDescription.setOnClickListener(this);
		btnNav = (ImageView)this.findViewById(R.id.iv_Summary_nav);
		btnNav.setOnClickListener(this);
		btnPhone = (ImageView)this.findViewById(R.id.iv_Summary_phone);
		btnPhone.setOnClickListener(this);
		btnShare = (ImageView)this.findViewById(R.id.iv_Summary_share);
		btnShare.setOnClickListener(this);
		btnCalendar = (ImageView)this.findViewById(R.id.iv_Summary_calendar);
		btnCalendar.setOnClickListener(this);
		btnRate = (ImageView)this.findViewById(R.id.iv_Summary_rate);
		btnRate.setOnClickListener(this);
		btnDone = (Button)this.findViewById(R.id.btn_Summary_Done);
		btnDone.setOnClickListener(this);

	}



	//	@Override
	//	public void onStart()
	//	{
	//		super.onStart();
	//		ShowHelpTip("summaryactivity", getString(R.string.tips_summary));
	//	}

	private void LoadActivity() 
	{
		// DB Query
		activityList = db.selectSavedActivity(Integer.parseInt(Global.selectedActivityID)); 
		contactList = db.selectSavedActivityContacts(Integer.parseInt(Global.selectedActivityID)); 
		currentActivity = 0;

		//Lookup selected contact emails and phones
		try
		{
			List<String[]> tmpEmails = db.selectSavedActivityContacts(Integer.parseInt(Global.selectedActivityID)); 

			int conLen = tmpEmails.size();
			selectedEmails = "";
			for(int a=0; a< conLen; a++)
			{
				String[] contactData = db.selectContactEmailPhone(this, tmpEmails.get(a)[0]);
				selectedPhones += contactData[0] + ";";
				selectedEmails += contactData[1] + ";";
			}
		}
		catch(Exception ex)
		{

		}

		Global.selectedActivityID = activityList.get(currentActivity)[0];
		Global.selectedAttendance = activityList.get(currentActivity)[1];
		Global.selectedCategory = activityList.get(currentActivity)[2];
		Global.selectedActivity = activityList.get(currentActivity)[3];
		//selectedDate = activityList.get(currentActivity)[4];
		selectedLocalName = activityList.get(currentActivity)[5];
		selectedLocalAddress = activityList.get(currentActivity)[10];
		selectedLocalPhone = activityList.get(currentActivity)[11];

		DisplayDescription();
		//refreshButtons();
	}

	public void DisplayDescription()
	{
		//Build the HTML here and set to webview
		try
		{

			String results = "<HTML>";
			//			results += "<head>";
			//
			//			results += "<style type='text/css'>";
			//
			//
			//			results += "table.button{";
			//			results +=  "   color:white;";
			//			results +=   "   text-decoration:none;";
			//			results +=     "text-shadow:1px 1px 0 #145982;";
			//			results +=     "font-family:Arial,Helvetica,sans-serif;";
			//			results +=     "font-size:12px;";
			//			results +=     "font-weight:bold;";
			//			results +=     "text-align:center;";
			//			results +=     "border:1px solid #000000;";
			//			results +=     "margin:10px auto;";
			//			results +=     "float:left;";
			//			results += "clear:both;";
			//			results += "background-color: #59aada;";
			//			results += "background-image: -moz-linear-gradient(#F902B6, #FFA4E6);";
			//			results += "background-image: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#999999), to(#555555));";
			//			results += "box-shadow:3px 3px 3px #000000;";
			//			results += "-moz-box-shadow:3px 3px 3px #000000;";
			//			results += "-webkit-box-shadow:3px 3px 3px #000000;";
			//			results += "border:1px solid #000000;";
			//			results += "border-radius:6px;";
			//			results += "-moz-border-radius:6px;";
			//			results += "-webkit-border-radius:6px;";
			//			results += "}";
			//
			//			results += 		"</style></head>";

			results += "<BODY>";
			results += "";

			String att = "";
			if(Global.selectedAttendance.equals("a"))
				att = " Solo Activity";
			else if(Global.selectedAttendance.equals("c"))
				att = " Pair Activity";
			else if(Global.selectedAttendance.equals("g"))
				att = " Group Activity";

			//TODO: this is a hack for road trip
			if(Global.selectedCategory == null)
			{
				results += "<center><b>Road Trip " + att + "</b></center>";
			}
			else
			{
				results += "<center><b>" + Global.selectedCategory + att + "</b></center><br>";
				results += "<center><b>" + Global.selectedActivity + "</center>";
				if(!selectedLocalName.trim().equals(""))
				{
					results += "<br><center>" + selectedLocalName + "</center>";
				}

				if(!Global.selectedAttendance.equals("a"))
				{
					results += "<P>Going with: <br>";

					int len = contactList.size();
					for(int i = 0; i < len; i++)
					{
						results += "\t* " + contactList.get(i)[0] +"<br>";
					}
					results += "</b><br>";
				}
				results += "</b></center>";
			}

			try
			{
				if(!selectedLocalName.trim().equals(""))
				{
					//TODO: this is a hack for road trip
					if(selectedLocalName.contains("RT:"))
					{
						String[] tmpSplit = selectedLocalName.split("\\^");
						results += "<center><b>" + tmpSplit[0].replace("RT:", "to ") + " in " + tmpSplit[1] +"</b></center>";
						selectedLocalAddress = tmpSplit[1];

						//Hide the phone functionality
						btnPhone.setVisibility(View.GONE);
					}
				}
				else
				{
					//Hide the phone functionality
					btnPhone.setVisibility(View.GONE);
					btnNav.setVisibility(View.GONE);
				}
			}
			catch(Exception ex){}

			results += "<P>The icons below let you perform actions for this activity (from left to right):<br>";

			if(!selectedLocalName.trim().equals(""))
			{
				if(!selectedLocalName.contains("RT:"))
				{
					results += "<br>\t* Navigate: Launches your navigation app to your selected destination.<br>";
					results += "<br>\t* Call: Calls the listed phone number of your destination.<br>";
				}
			}

			if(!Global.selectedAttendance.trim().equals(""))
				results += "<br>\t* Invite: Call/text/email/post on social media the other participants.<br>";

			if(!Global.createMode)
			{
				results += "<br>\t* Rate: You can rate each part of an activity. The higher the rating, the more likely they are to show up in your jackpot!<br>";
			}


			if(Global.createMode)
			{
				results += "<P>Once you're done here either go and do your chosen activity or save it for later.";
			}

			results += "</BODY>";
			results += "</HTML>";

			//			wvDescription.setWebChromeClient(new WebChromeClient());
			//			wvDescription.setBackgroundColor(Color.TRANSPARENT); 
			//			wvDescription.setVerticalScrollBarEnabled(true);
			//			WebSettings settings = wvDescription.getSettings();
			//			settings.setJavaScriptEnabled(true);
			//			wvDescription.loadDataWithBaseURL("", results, "text/html", "utf-8", null);
			//			wvDescription.setVisibility(View.VISIBLE);

			if(Global.createMode)
			{
				//Hide the rate functionality
				btnRate.setVisibility(View.GONE);
			}

			tvDescription.setText(Html.fromHtml(results));
			tvDescription.setContentDescription(Html.fromHtml(results));

			//wvDescription.setContentDescription(accessibleContent);
		}
		catch(Exception ex){}

	}

	public void ShareActivity()
	{
		this.onEvent("SummaryScreen-ShareActivity");
		String[] emailArray = selectedEmails.split(";"); 
		try
		{
			//Send action_send to OS
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/*");

			//populate phone#'s for sms option
			shareIntent.putExtra("address", selectedPhones);

			//populate content for message body and email recipients
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lets do an activity together!");
			shareIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailArray);
			if(!selectedLocalName.trim().equals(""))
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I'm going to " + Global.selectedActivity + " at " + selectedLocalName + ". Do you want to come too?");
			else
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I'm going to " + Global.selectedActivity + ". Do you want to come too?");

			startActivity(Intent.createChooser(shareIntent, "Send an invitation"));
		}
		catch(Exception ex)
		{}
	}

	private void AddToCalendar()
	{
		this.onEvent("SummaryScreen-Add2Calendar");
		//Not accessible below 4.0
		Calendar c = Calendar.getInstance(); 
		Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", c.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("eventLocation", selectedLocalName);
        intent.putExtra("title", Global.selectedActivity);
        startActivity(intent);
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		LoadActivity();

	}

	@Override
	public void onClick(View v) 
	{
		super.onClick(v);
		switch(v.getId()) 
		{
		case R.id.iv_Summary_nav:
			if(!selectedLocalAddress.trim().equals(""))
			{
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + selectedLocalAddress)); 
				startActivity(i);
			}
			break;
		case R.id.iv_Summary_phone:
			if(!selectedLocalPhone.trim().equals(""))
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + selectedLocalPhone));
				startActivity(callIntent);
			}
			break;
		case R.id.iv_Summary_calendar:
			AddToCalendar();
			break;
		case R.id.iv_Summary_share:
			ShareActivity();
			break;
		case R.id.iv_Summary_rate:
			startActivity(ActivityFactory.getRateActivity(this));
			break;
		case R.id.btn_Summary_Done:
			startActivity(ActivityFactory.getPreviousActivity(this));
			finish();
			break;
		}
	}

	public void populateMenu(Menu menu) {

		if(!Global.createMode)
		{
		menu.setQwertyMode(true);

		MenuItem item1 = menu.add(0, Menu1, 0, "Delete this Activity");
		{
			//item1.setAlphabeticShortcut('a');
			item1.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		}
		}
	}

	public boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case Menu1:
			ShowAlert();
			break;
		}
		return false;
	}

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** when menu button option selected */
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return applyMenuChoice(item) || super.onOptionsItemSelected(item);
	}
	
	public void ShowAlert()
	{
		//Alert the results
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to delete this activity?")
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				DeleteActivity();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		})
		.create()
		.show();
	}
	
	public void DeleteActivity()
	{
		db.DeleteActivity(Integer.parseInt(Global.selectedActivityID));
		this.startActivity(ActivityFactory.getPreviousActivity(this));
			this.finish();
	}

}

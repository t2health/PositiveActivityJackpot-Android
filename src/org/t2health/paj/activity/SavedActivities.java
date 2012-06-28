package org.t2health.paj.activity;

import java.util.List;

import org.t2health.paj.classes.ActivityFactory;
import org.t2health.paj.classes.DatabaseProvider;
import org.t2health.paj.classes.Global;
import org.t2health.paj.classes.PAJActivity;

import t2.paj.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Shows a list of previously saved activies. Clicking on activity brings up the rating activity.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class SavedActivities extends ABSCustomTitleActivity implements OnClickListener 
{

	private ListView lvSaved;
	private List<String[]> activityList;
	//private ProgressDialog m_ProgressDialog = null;
	private DatabaseProvider db = new DatabaseProvider(this);
	private Context ctx;

	public static final int Menu1 = Menu.FIRST + 1;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.savedactivities);
		this.onEvent("SavedActivities-Open");
		
		ctx = this;

		this.SetMenuVisibility(1);
		this.btnMainPrevious.setChecked(true);

		lvSaved = (ListView) this.findViewById(R.id.setup_listview);
		lvSaved.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Global.selectedActivityID = activityList.get(position)[0];
				//startActivity(ActivityFactory.getRateActivity(ctx));
				startActivity(ActivityFactory.getSummaryActivity(ctx));
			}
		});

		PopulateSavedActivities();
	}

	@Override
	public void onStart() 
	{
		super.onStart();
		Global.createMode = false;
		ShowHelpTip("savedactivity", getString(R.string.tips_saved));

	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		PopulateSavedActivities();
	}

	private void PopulateSavedActivities()
	{
		activityList = db.selectSavedActivities();

		if (activityList.size() <= 0) 
		{
			lvSaved.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_1_line, R.id.list_text1, new String[] {getString(R.string.saved_emptylist)}));
			lvSaved.setEnabled(false);
		}
		else
		{
			int len = activityList.size();
			PAJActivity[] outArray = new PAJActivity[len];
			for (int a = 0; a < len; a++) 
			{
				//String ActivityID = activityList.get(a)[0];
				//String Attendance = activityList.get(a)[1];

				String Category = activityList.get(a)[2];
				String Activity = activityList.get(a)[3];
				String selectedDate = activityList.get(a)[4];
				String localOption = "";
				Boolean rated = false;

				try{if(!activityList.get(a)[5].trim().equals(""))
					rated = true;}catch(Exception ex){}
				try{if(!activityList.get(a)[6].trim().equals(""))
					rated = true;}catch(Exception ex){}
				try{if(!activityList.get(a)[7].trim().equals(""))
					rated = true;}catch(Exception ex){}
				try{if(!activityList.get(a)[8].trim().equals(""))
					rated = true;}catch(Exception ex){}

				try
				{
					localOption = activityList.get(a)[9];
					if(localOption.startsWith("RT:"))
					{
						String[] rtsplit = localOption.split("\\^");
						localOption = rtsplit[0].replace("RT:", "");
						Category = "Road Trip";
						Activity = rtsplit[1];
					}

				}
				catch(Exception ex){}

				outArray[a] = new PAJActivity();
				outArray[a].title = selectedDate;
				if(!rated)
					outArray[a].title += " (unrated)";
				outArray[a].title += "\r\n" + Category + "\r\n" + Activity;
				if(!localOption.trim().equals(""))
					outArray[a].title += "\r\n" + localOption;
				int resID =0;
				try{
					resID = getResources().getIdentifier(Category.toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", getPackageName());
				}
				catch(Exception ex){
					resID = 0;

				}
				if (resID == 0)
					resID = getResources().getIdentifier("icon", "drawable", getPackageName());
				outArray[a].icon = resID;
			}

			ActivitiesAdapter adapter = new ActivitiesAdapter(this, R.layout.list_item_savedactivities, outArray);
			lvSaved.setAdapter(adapter);
		}
	}

	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{

		}

	}

	private class ActivitiesAdapter extends ArrayAdapter<PAJActivity>{

		Context context; 
		int layoutResourceId;    
		PAJActivity data[] = null;

		public ActivitiesAdapter(Context context, int layoutResourceId, PAJActivity[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ActivitiesHolder holder = null;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new ActivitiesHolder();
				holder.imgIcon = (ImageView)row.findViewById(R.id.rate_list_icon);
				holder.txtTitle = (TextView)row.findViewById(R.id.rate_list_title);

				row.setTag(holder);
			}
			else
			{
				holder = (ActivitiesHolder)row.getTag();
			}

			PAJActivity curActivity = data[position];
			holder.txtTitle.setText(curActivity.title);
			holder.imgIcon.setImageResource(curActivity.icon);

			return row;
		}


	}

	static class ActivitiesHolder
	{
		ImageView imgIcon;
		TextView txtTitle;
	}

	public void ClearSavedActivities()
	{
		db.updateClearSavedActivities();
		PopulateSavedActivities();
	}

	public void ShowAlert()
	{
		//Alert the results
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to delete your saved activities?")
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				ClearSavedActivities();
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

	public void populateMenu(Menu menu) {

		menu.setQwertyMode(true);

		MenuItem item1 = menu.add(0, Menu1, 0, "Delete Saved Activities");
		{
			//item1.setAlphabeticShortcut('a');
			item1.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
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
}

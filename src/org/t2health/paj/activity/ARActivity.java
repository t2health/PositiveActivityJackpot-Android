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
package org.t2health.paj.activity;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.mixare.ARData;
import org.mixare.AugmentedReality;
import org.mixare.GooglePlacesDataSource;
import org.mixare.Marker;
import org.mixare.NetworkDataSource;
import org.t2health.paj.classes.Global;
import org.t2health.paj.classes.SharedPref;

import t2.paj.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * This class extends the AugmentedReality class and sets up to show data from Google Places.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */
public class ARActivity extends AugmentedReality 
{

	private static final String locale = Locale.getDefault().getLanguage();
	private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
	private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);
	private static final Map<String, NetworkDataSource> sources = new ConcurrentHashMap<String, NetworkDataSource>();
	private static boolean markerDialogShowing = false;
	public static final int Menu1 = Menu.FIRST + 1;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.onEvent("AR-Opened");

		// Add Google Places datasource
		NetworkDataSource places = new GooglePlacesDataSource(this.getResources());
		sources.put("Places", places);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStart() 
	{
		super.onStart();

		Location last = ARData.getCurrentLocation();
		updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());

		ShowHelpTip("aractivity", this.getString(R.string.ar_popup));
	}

	public void ToggleCamera()
	{
		try
		{
		if(SharedPref.getCameraOn())
		{
			//Turn it off
			SharedPref.setCameraOn(false);
			SetSurfaceView();
			this.onEvent("AR-CameraOff");
		}
		else
		{
			//Turn it on
			SharedPref.setCameraOn(true);
			SetSurfaceView();
			this.onEvent("AR-CameraON");
		}
		}
		catch(Exception ex){}
	}
	
	public void ShowHelpTip(String key, String tipText)
	{
		final String fKey = key;
		if((SharedPref.getTipsOn(key)) && (!tipText.trim().equals("")))
		{
			//set up the "helpful" tip dialog
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.infopop);
			dialog.setTitle("Tip:");
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLocationChanged(Location location) 
	{
		super.onLocationChanged(location);

		updateData(location.getLatitude(), location.getLongitude(),	location.getAltitude());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void markerTouched(Marker marker) 
	{

		//Show the information dialog
		showLocationDialog(marker);
	}

	private void showLocationDialog(Marker marker)
	{
		if(!markerDialogShowing)
		{
			markerDialogShowing = true;

			//set up dialog
			WebView wv=new WebView(this);
			AlertDialog.Builder markerDialog = new AlertDialog.Builder(this);
			markerDialog.setView(wv);
			markerDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					markerDialogShowing = false;
				}
			});

			//Set some default text incase there is no data
			String detailsText = "Unable to get detailed information.";

			try 
			{
				//String icon = marker.pIcon;
				String address = marker.pAddress;
				String phone = marker.pPhone;
				String svImage = "http://maps.googleapis.com/maps/api/streetview?size=128x128&location=" + marker.lat + "," + marker.lon + "&sensor=true";
				Bitmap check = Global.getBitmapFromURL(svImage);
				int smpl = check.getPixel(0, 0);
				if(smpl == -1053713)
				{
					svImage = marker.pIcon;
				}
				check.recycle();

				//			String websiteURL = marker.pWebsite;
				//			String websiteName = marker.pWebsite;
				//			
				//			if(websiteURL.trim().equals(""))
				//			{
				//				websiteURL = marker.pURL;
				//				websiteName = "Google Places";
				//			}

				//Build information HTML
				String results = "<HTML>";
				results += "<head>";

				results += "<BODY>";
				results += "";


				results += "<Table class='button' width='90%'><tr><td valign=top><img width=128 height=128 src='" + svImage + "'></td><td valign=top>";
				results += "<B>" + marker.getName() + "</B><BR>" + address + "<BR><u>" + phone + "</u></td>";
				results += "<td valign='top'><a href='tel:" + phone + "'><img width=48 height=48 src='http://www.wired.com/images_blogs/epicenter/2011/01/2009-07-15_google_voice_fluid_preview.png'></a><br><br><a href='geo:0,0?q=" + address.replace(' ','+') + "'><img width=48 height=48 src='http://www.google.com/mobile/images/mgc3/navigation48.png'></a></td></tr></table>";


				results += "</BODY>";
				results += "</HTML>";
				detailsText = results;

			} 
			catch (Exception e) 
			{
				Global.Log.v("ERROR:ARActivity", e.toString());
			}

			//Setup Browser control
			int color = 0xffffff;
			StringBuffer contentBuffer = new StringBuffer();
			contentBuffer.append("\n<style type=\"text/css\">\n");
			contentBuffer.append("\tbody,a {\n");
			contentBuffer.append("\t\tcolor:rgb(");
			contentBuffer.append(Color.red(color));
			contentBuffer.append(",");
			contentBuffer.append(Color.green(color));
			contentBuffer.append(",");
			contentBuffer.append(Color.blue(color));
			contentBuffer.append(");\n");
			contentBuffer.append("\t}\n");
			contentBuffer.append("</style>");
			contentBuffer.append(detailsText);

			wv.setWebChromeClient(new WebChromeClient());
			wv.setBackgroundColor(Color.TRANSPARENT); 

			WebSettings settings = wv.getSettings();

			settings.setJavaScriptEnabled(true);

			wv.loadDataWithBaseURL("", contentBuffer.toString(), "text/html", "utf-8", null);

			markerDialog.show();
		}

	}

	//	/**
	//	 * {@inheritDoc}
	//	 */
	//	@Override
	//	protected void updateDataOnZoom() 
	//	{
	//		super.updateDataOnZoom();
	//		Location last = ARData.getCurrentLocation();
	//		updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());
	//	}

	private void updateData(final double lat, final double lon, final double alt) 
	{
		try 
		{
			exeService.execute(new Runnable() 
			{
				@Override
				public void run() 
				{
					for (NetworkDataSource source : sources.values())
						download(source, lat, lon, alt);
				}
			});
		} 
		catch (RejectedExecutionException rej) 
		{
			Global.Log.v("ARActivity", "Not running new download Runnable, queue is full.");
		} 
		catch (Exception e) 
		{
			Global.Log.v("Exception running download Runnable.", e.toString());
		}
	}

	//
	private static boolean download(NetworkDataSource source, double lat, double lon, double alt) 
	{
		if (source == null)
			return false;

		String url = null;
		try 
		{
			url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);
		} 
		catch (NullPointerException e) 
		{
			return false;
		}

		List<Marker> markers = null;
		try 
		{
			markers = source.parse(url);
			ARData.addMarkers(markers);
		} 
		catch (NullPointerException e) 
		{
			Global.Log.v("Error Loading Markers", e.toString());
		}

		return true;
	}
	
	public void populateMenu(Menu menu) {

	  	  menu.setQwertyMode(true);

	  	  MenuItem item1 = menu.add(0, Menu1, 0, "Toggle Camera");
	  	  {
	  	    item1.setIcon(android.R.drawable.ic_menu_camera);
	  	  }
	    }
	
	public boolean applyMenuChoice(MenuItem item) {
	  switch (item.getItemId()) {
	    case Menu1:
	    	ToggleCamera();
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
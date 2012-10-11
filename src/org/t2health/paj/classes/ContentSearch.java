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
package org.t2health.paj.classes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Handles all (non AR) JSON lookups. Used to pull place data from Google Places, and POI data from geoNames WS.
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class ContentSearch extends ContextWrapper 
{

	public ContentSearch(Context base) 
	{
		super(base);
	}

	LocationListener locationListener;

	public String getPlacesURLByTypes(String name, String Types) 
	{

		String outString = Global.gPlacesURL + Global.pAPIKEY;

		if (!name.trim().equals(""))
			outString += "&name=" + name;
		if (!Types.trim().equals(""))
			outString += "&types=" + Types;

		outString += "&location=" + GetLocation(0);

		return outString;
	}
	
	public String getPlacesURLByKeyword(String Keyword) 
	{

		String outString = Global.gPlacesURL + Global.pAPIKEY + "&types=establishment";

		if (!Keyword.trim().equals(""))
			outString += "&keyword=" + Keyword;

		outString += "&location=" + GetLocation(0);

		return outString;
	}

	public ArrayList<ArrayList<String>> SearchPlaces(String uri) throws Exception 
	{
		
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        return null;
	    }
	    
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		JSONObject jsonResults;

		// Get the data from google
		HttpGet httpGet = new HttpGet(uri);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(httpGet);
		BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;

		while ((s = r.readLine()) != null) 
		{
			sb.append(s);
		}

		jsonResults = new JSONObject(sb.toString());

		// Parse the JSON into an arraylist
		JSONArray dataArray = null;
		JSONObject jsonEntry;
		dataArray = jsonResults.getJSONArray("results");
		if (dataArray != null) 
		{

			int top = Math.min(100, dataArray.length());

			for (int i = 0; i < top; i++) 
			{

				jsonEntry = dataArray.getJSONObject(i);
				results.add(GetPlaceDetails(jsonEntry.getString("reference")));
			}
		}

		return results;

	}

	public ArrayList<String[]> geoNamesOneDegreeCities() throws Exception 
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        return null;
	    }
	    
		ArrayList<String[]> results = new ArrayList<String[]>();

		//This is used for the "Road Trip" activity.
		//Since geonames only allows city lookups in a 30km radius, we need to adjust the center-point one degree each direction (independently) and get results for each.

		//Save current lat & lng
		Double curlat = Double.parseDouble(GetLocation(1));
		Double curlng = Double.parseDouble(GetLocation(2));
		Double lat = 0d;
		Double lng = 0d;

		lat = curlat + 1d;
		lng = curlng;
		results.addAll(getGeoNamesCities(lat,lng));
		lat = curlat;
		lng = curlng + 1d;
		results.addAll(getGeoNamesCities(lat,lng));
		lat = curlat + 1d;
		lng = curlng + 1d;
		results.addAll(getGeoNamesCities(lat,lng));

		lat = curlat - 1d;
		lng = curlng;
		results.addAll(getGeoNamesCities(lat,lng));
		lat = curlat;
		lng = curlng - 1d;
		results.addAll(getGeoNamesCities(lat,lng));
		lat = curlat - 1d;
		lng = curlng - 1d;
		results.addAll(getGeoNamesCities(lat,lng));


		return results;


	}

	public ArrayList<String[]> getGeoNamesCities(Double lat, Double lng) throws Exception 
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        return null;
	    }
	    
		ArrayList<String[]> results = new ArrayList<String[]>();
		JSONObject jsonResults;

		String uri = Global.geoNamesURL + "&lat=" + lat.toString() + "&lng=" + lng.toString();

		// Get the data from geonames
		HttpGet req = new HttpGet(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) 
		{
			sb.append(s);
		}

		jsonResults = new JSONObject(sb.toString());

		// Parse the JSON into an arraylist
		JSONArray dataArray = null;
		JSONObject jsonEntry;
		dataArray = jsonResults.getJSONArray("postalCodes");
		if (dataArray != null) 
		{

			int top = Math.min(100, dataArray.length());

			for (int i = 0; i < top; i++) 
			{

				jsonEntry = dataArray.getJSONObject(i);
				results.add(new String[] {jsonEntry.getString("placeName"),jsonEntry.getString("lat") ,jsonEntry.getString("lng") });
			}
		}

		return results;

	}

	public ArrayList<String> getGeoPOI(Double lat, Double lng) throws Exception 
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        return null;
	    }
	    
		ArrayList<String> results = new ArrayList<String>();
		JSONObject jsonResults;

		String uri = Global.geoWikiURL + "&lat=" + lat.toString() + "&lng=" + lng.toString();

		// Get the data from geonames
		HttpGet req = new HttpGet(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) 
		{
			sb.append(s);
		}

		jsonResults = new JSONObject(sb.toString());

		// Parse the JSON into an arraylist
		JSONArray dataArray = null;
		JSONObject jsonEntry;
		dataArray = jsonResults.getJSONArray("geonames");
		if (dataArray != null) {

			int top = Math.min(100, dataArray.length());

			for (int i = 0; i < top; i++) 
			{

				jsonEntry = dataArray.getJSONObject(i);
				try
				{
					String feature = jsonEntry.getString("feature");
					if((feature != null) && (!feature.equals("city")))
						results.add(jsonEntry.getString("title"));
				}
				catch(Exception ex)
				{}
			}
		}

		return results;

	}

	public ArrayList<String> GetPlaceDetails(String reference) throws Exception 
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        return null;
	    }
	    
		ArrayList<String> results = new ArrayList<String>();
		JSONObject jsonResults;

		String uri = Global.gPlaceDetailURL + "reference=" + reference + "&sensor=true&key=" + Global.pAPIKEY;

		// Get the data from google
		HttpGet req = new HttpGet(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) 
		{
			sb.append(s);
		}

		jsonResults = new JSONObject(sb.toString());

		// Parse the JSON object
		JSONObject jsonEntry;
		jsonEntry = jsonResults.getJSONObject("result");
		if (jsonEntry != null) 
		{

			try{results.add(jsonEntry.getString("name"));}catch(Exception ex){results.add("");}
			try{results.add(jsonEntry.getString("icon"));}catch(Exception ex){results.add("");}
			try{results.add(jsonEntry.getString("formatted_address"));}catch(Exception ex){results.add("");}
			try{results.add(jsonEntry.getString("formatted_phone_number"));}catch(Exception ex){results.add("");}
			try{results.add(jsonEntry.getString("rating"));}catch(Exception ex){results.add("");}
			try{results.add(jsonEntry.getString("website"));}catch(Exception ex){results.add("");}
			try{results.add(jsonEntry.getString("url"));}catch(Exception ex){results.add("");}
		}

		return results;

	}

	public String GetLocation(int outStyle) 
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        return null;
	    }
	    
		Location curLoc = null;
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Only update if its been longer than 60 seconds, or we have travelled
		// a certain distance (roughly a mile = 1690meters)
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,locListener);

		Criteria c = new Criteria();
		// try to use the coarse provider first to get a rough position
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		String coarseProvider = lm.getBestProvider(c, true);
		try 
		{
			lm.requestLocationUpdates(coarseProvider, 0, 0, locListener);
		} 
		catch (Exception e) 
		{
			Global.Log.v("", "Could not initialize the coarse provider");
		}

		// Update to fine provider if avail
		c.setAccuracy(Criteria.ACCURACY_FINE);
		String fineProvider = lm.getBestProvider(c, true);
		try 
		{
			lm.requestLocationUpdates(fineProvider, 0, 0, locListener);
		} 
		catch (Exception e) 
		{
			Global.Log.v("", "Could not initialize the bounce provider");
		}

		// frequency and minimum distance for update
		// this values will only be used after there's a good GPS fix
		long lFreq = 60000; // 60 seconds
		float lDist = 50; // 20 meters
		try 
		{
			lm.requestLocationUpdates(fineProvider, lFreq, lDist, locListener);
		} 
		catch (Exception e) 
		{
			Global.Log.v("", "Could not initialize the normal provider");
		}

		try 
		{
			Location lastFinePos = lm.getLastKnownLocation(fineProvider);
			Location lastCoarsePos = lm.getLastKnownLocation(coarseProvider);
			if (lastFinePos != null)
				curLoc = lastFinePos;
			else if (lastCoarsePos != null)
				curLoc = lastCoarsePos;

		} 
		catch (Exception ex){}

		if(outStyle == 1)
		{
			String outLoc = "" + curLoc.getLatitude();
			return outLoc;
		}
		else if(outStyle == 2)
		{
			String outLoc = "" + curLoc.getLongitude();
			return outLoc;
		}
		else
		{
			String outLoc = "" + curLoc.getLatitude() + "," + curLoc.getLongitude();
			return outLoc;
		}
	}

	private LocationListener locListener = new LocationListener() 
	{

		@Override
		public void onLocationChanged(Location location) 
		{
			try 
			{
				Global.Log.v(
						"",
						"Location Changed: " + location.getProvider()
						+ " lat: " + location.getLatitude() + " lon: "
						+ location.getLongitude() + " alt: "
						+ location.getAltitude() + " acc: "
						+ location.getAccuracy());
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {}

		@Override
		public void onProviderEnabled(String arg0) {}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

	};

}



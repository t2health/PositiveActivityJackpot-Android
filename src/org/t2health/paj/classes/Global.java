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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Application-Wide settings
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class Global 
{

	//Enables logging to console
	public static final boolean DebugOn = false;

	public static final int reminderID = 66166;
	public static boolean createMode = false;
	
	//Flurry
	public static final String FLURRY_KEY = "NXXTBFYJC1Y273U88XF1";
	
	public static SharedPreferences sharedPref;
	public static boolean accessibleNavigationWarning = false;
	
	//Steps in list activity selection
	public static final int SOCIAL = 0;
	public static final int CONTACTS = 1;
	public static final int CATEGORY = 2;
	public static final int ACTIVITY = 3;
	//public static final int ACTIVITY = 4;
	public static boolean artWheel = true;
	
	public static int walkthroughStep = 0;
	
	//Current selections
	public static String selectedAttendance = "a";
	public static ArrayList<String> selectedContacts;
	public static String selectedCategory = "";
	public static String selectedActivity = "";
	public static String selectedActivityID = "";
	public static String selectedPlaceTypes = "";

	public static ArrayList<String[]> RoadTripList = null;
	
	//public static PAJActivity selectedPAJActivity = null;
	public static String selectedAddress = "";

	//public static String helpTip = "";

	//Google Places API
	public static final Double localLAT =  34.0522;//47.103397;
	public static final Double localLON = 118.2428;//-122.54263;
	public static final String testString = "https://maps.googleapis.com/maps/api/place/search/json?radius=30000&sensor=true&key=AIzaSyBk1F6TcnL03jrKiFMAFhZ-Eix19q6AEds&location=47.103397,-122.54263&types=food";
	public static final String gPlacesURL = "https://maps.googleapis.com/maps/api/place/search/json?radius=30000&sensor=true&key=";
	public static final String gPlaceDetailURL = "https://maps.googleapis.com/maps/api/place/details/json?";
	public static final String pAPIKEY = "AIzaSyCBwSnMoHgndBcgrsCDuhQOUXJyt-OwM7c"; //"AIzaSyBk1F6TcnL03jrKiFMAFhZ-Eix19q6AEds";
	public static final String mAPIffKEY = "0sGS2htjz3olx7jpQVQDfeHPKFAeL85NT4UeCLg"; //Unused
	public static final String positivePlaceTypes = "amusement_park|aquarium|art_gallery|bakery|beauty_salon|bicycle_store|book_store|bowling_alley|cafe|campground|clothing_store|department_store|florist|food|furniture_store|gym|hair_care|hardware_store|home_goods_store|jewelry_store|library|meal_delivery|meal_takeaway|movie_rental|movie_theater|museum|park|pet_store|restaurant|rv_park|shopping_mall|spa|stadium|store|university|zoo";	
	
	//GeoNames
	public static final String geoNamesURL = "http://ws.geonames.org/findNearbyPostalCodesJSON?radius=30&maxRows=10";
	public static final String geoWikiURL = "http://ws.geonames.org/findNearbyWikipediaJSON?radius=20";

	//Logging class
	public static class Log
	{
		public static void v(String tag, String msg)
		{
			if(DebugOn)
				android.util.Log.v(tag, msg);
		}
	}
	
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}


}

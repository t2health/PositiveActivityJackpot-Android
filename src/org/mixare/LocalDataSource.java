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
package org.mixare;

import java.util.ArrayList;
import java.util.List;

import t2.paj.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * This class should be used as a example local data source. It is an example of
 * how to add data programatically. You can add data either programatically,
 * SQLite or through any other source.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class LocalDataSource extends DataSource {
	private List<Marker> cachedMarkers = new ArrayList<Marker>();
	private static Bitmap icon = null;

	public LocalDataSource(Resources res) {
		if (res == null)
			throw new NullPointerException();

		createIcon(res);
	}

	protected void createIcon(Resources res) {
		if (res == null)
			throw new NullPointerException();

		icon = BitmapFactory.decodeResource(res, R.drawable.icon);
	}

	public List<Marker> getMarkers() {
		Marker atl = new Marker("ATL", 39.931269, -75.051261, 0,
				Color.DKGRAY, icon, "");
		cachedMarkers.add(atl);

		Marker home = new Marker("Mt Laurel", 39.95, -74.9, 0, Color.YELLOW, null, "");
		cachedMarkers.add(home);

		/*
		 * Marker lon = new IconMarker(
		 * "I am a really really long string which should wrap a number of times on the screen."
		 * , 39.95335, -74.9223445, 0, Color.MAGENTA, icon);
		 * cachedMarkers.add(lon); Marker lon2 = new IconMarker(
		 * "2: I am a really really long string which should wrap a number of times on the screen."
		 * , 39.95334, -74.9223446, 0, Color.MAGENTA, icon);
		 * cachedMarkers.add(lon2);
		 */

		/*
		 * for (int i=0; i<10; i++) { Marker marker = null; if (i%2==0) marker =
		 * new Marker("Test-"+i, 39.99, -75.33, 0, Color.LTGRAY); marker = new
		 * IconMarker("Test-"+i, 39.99, -75.33, 0, Color.LTGRAY, icon);
		 * cachedMarkers.add(marker); }
		 */

		return cachedMarkers;
	}
}
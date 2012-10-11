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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import t2.paj.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * This class extends DataSource to fetch data from Wikipedia.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class WikipediaDataSource extends NetworkDataSource {
	private static final String BASE_URL = "http://ws.geonames.org/findNearbyWikipediaJSON";

	private static Bitmap icon = null;

	public WikipediaDataSource(Resources res) {
		if (res == null)
			throw new NullPointerException();

		createIcon(res);
	}

	protected void createIcon(Resources res) {
		if (res == null)
			throw new NullPointerException();

		icon = BitmapFactory.decodeResource(res, R.drawable.wikipedia);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createRequestURL(double lat, double lon, double alt,
			float radius, String locale) {
		return BASE_URL + "?lat=" + lat + "&lng=" + lon + "&radius=" + radius
				+ "&maxRows=40" + "&lang=" + locale;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Marker> parse(JSONObject root) {
		if (root == null)
			return null;

		JSONObject jo = null;
		JSONArray dataArray = null;
		List<Marker> markers = new ArrayList<Marker>();

		try {
			if (root.has("geonames"))
				dataArray = root.getJSONArray("geonames");
			if (dataArray == null)
				return markers;
			int top = Math.min(MAX, dataArray.length());
			for (int i = 0; i < top; i++) {
				jo = dataArray.getJSONObject(i);
				Marker ma = processJSONObject(jo);
				if (ma != null)
					markers.add(ma);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return markers;
	}

	private Marker processJSONObject(JSONObject jo) {
		if (jo == null)
			return null;

		Marker ma = null;
		if (jo.has("title") && jo.has("lat") && jo.has("lng")
				&& jo.has("elevation")) {
			try {
				ma = new Marker(jo.getString("title"), jo.getDouble("lat"),
						jo.getDouble("lng"), jo.getDouble("elevation"),
						Color.WHITE, icon, "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ma;
	}
}
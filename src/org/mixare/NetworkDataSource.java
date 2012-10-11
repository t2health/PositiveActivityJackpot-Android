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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This abstract class should be extended for new data sources. It has many
 * methods to get and parse data from numerous web sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class NetworkDataSource extends DataSource {
	protected static final int MAX = 5;
	protected static final int READ_TIMEOUT = 10000;
	protected static final int CONNECT_TIMEOUT = 10000;

	protected List<Marker> markersCache = null;

	public abstract String createRequestURL(double lat, double lon, double alt,
			float radius, String locale);

	public abstract List<Marker> parse(JSONObject root);

	/**
	 * This method get the Markers if they have already been downloaded once.
	 * 
	 * @return List of Marker objects or NULL if not downloaded yet.
	 */
	public List<Marker> getMarkers() {
		return markersCache;
	}

	protected static InputStream getHttpGETInputStream(String urlStr) {
		if (urlStr == null)
			throw new NullPointerException();

		InputStream is = null;
		URLConnection conn = null;

		try {
			if (urlStr.startsWith("file://"))
				return new FileInputStream(urlStr.replace("file://", ""));

			URL url = new URL(urlStr);
			conn = url.openConnection();
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setConnectTimeout(CONNECT_TIMEOUT);

			is = conn.getInputStream();

			return is;
		} catch (Exception ex) {
			try {
				is.close();
			} catch (Exception e) {
				// Ignore
			}
			try {
				if (conn instanceof HttpURLConnection)
					((HttpURLConnection) conn).disconnect();
			} catch (Exception e) {
				// Ignore
			}
			ex.printStackTrace();
		}

		return null;
	}

	protected String getHttpInputString(InputStream is) {
		if (is == null)
			throw new NullPointerException();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is),
				8 * 1024);
		StringBuilder sb = new StringBuilder();

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Parse the given URL for JSON Objects.
	 * 
	 * @param url
	 *            URL to parse.
	 * @return List of Marker's from the URL.
	 * @throws NullPointerException
	 *             if the String URL is NULL.
	 */
	public List<Marker> parse(String url) {
		if (url == null)
			throw new NullPointerException();

		InputStream stream = null;
		stream = getHttpGETInputStream(url);
		if (stream == null)
			throw new NullPointerException();

		String string = null;
		string = getHttpInputString(stream);
		if (string == null)
			throw new NullPointerException();

		JSONObject json = null;
		try {
			json = new JSONObject(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (json == null)
			throw new NullPointerException();

		return parse(json);
	}
}
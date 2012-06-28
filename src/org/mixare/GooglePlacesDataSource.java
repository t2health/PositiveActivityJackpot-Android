package org.mixare;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.t2health.paj.classes.Global;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;


/**
 * This class extends DataSource to fetch data from Google Places.
 * 
 * @author Steve Ody
 */
public class GooglePlacesDataSource extends NetworkDataSource {

	//private static Bitmap icon = null;

	public GooglePlacesDataSource(Resources res) {
		if (res == null)
			throw new NullPointerException();
		//createIcon(res);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createRequestURL(double lat, double lon, double alt,
			float radius, String locale) {
		String outString = Global.gPlacesURL + Global.pAPIKEY;
		outString += "&types=" + Global.selectedPlaceTypes;

		outString += "&location=" + lat + "," + lon;

		return outString;

	}

	public void GetPlaceDetails(Marker ma) throws Exception {
		JSONObject jsonResults;

		String uri = Global.gPlaceDetailURL + "reference=" + ma.reference + "&sensor=true&key=" + Global.pAPIKEY;

		// Get the data from google
		HttpGet req = new HttpGet(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
				.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) {
			sb.append(s);
		}

		jsonResults = new JSONObject(sb.toString());

		// Parse the JSON object
		JSONObject jsonEntry;
		jsonEntry = jsonResults.getJSONObject("result");
		if (jsonEntry != null) {

			try{ma.pIcon = jsonEntry.getString("icon");}catch(Exception ex){}
			try{ma.pAddress = jsonEntry.getString("formatted_address");}catch(Exception ex){}
			try{ma.pPhone = jsonEntry.getString("formatted_phone_number");}catch(Exception ex){}
			try{ma.pRating = jsonEntry.getString("rating");}catch(Exception ex){}
			try{ma.pWebsite = jsonEntry.getString("website");}catch(Exception ex){}
			try{ma.pURL = jsonEntry.getString("url");}catch(Exception ex){}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Marker> parse(JSONObject root) {
		if (root == null)
			throw new NullPointerException();

		JSONObject jo = null;
		JSONArray dataArray = null;
		List<Marker> markers = new ArrayList<Marker>();

		try {

			dataArray = root.getJSONArray("results");
			if (dataArray == null)
				return markers;
			int top = dataArray.length();
			for (int i = 0; i < top; i++) 
			{
				jo = dataArray.getJSONObject(i);
				Marker ma = processJSONObject(jo);
				if (ma != null)
				{
					try {
						GetPlaceDetails(ma);
						markers.add(ma);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return markers;
	}

//	private void createIcon(Resources res) {
//		if (res == null)
//			throw new NullPointerException();
//
//		try
//		{
//			int resID = res.getIdentifier(Global.selectedCategory.toLowerCase().replace(' ', '_').replace('/', '_'), "drawable", "t2.paarmixare");
//			icon = BitmapFactory.decodeResource(res, resID);
//
//			if(icon == null)
//				icon = BitmapFactory.decodeResource(res, R.drawable.icon);
//		}
//		catch (Exception ex)
//		{
//			icon = BitmapFactory.decodeResource(res, R.drawable.icon);
//		}
//	}

	private Marker processJSONObject(JSONObject jo) {
		if (jo == null)
			throw new NullPointerException();

		Marker ma = null;

		if (jo.has("geometry")) {
			Double lat = null, lng = null;

			if (!jo.isNull("geometry")) {
				try {
					JSONObject geo = jo.getJSONObject("geometry");
					JSONObject coordinates = geo.getJSONObject("location");
					lat = coordinates.getDouble("lat");
					lng = coordinates.getDouble("lng");
					Bitmap icon = Global.getBitmapFromURL(jo.getString("icon"));

					String reference = "";
					reference = jo.getString("reference");
					ma = new Marker(unescapeHTML(jo.getString("name"), 0),
							lat, lng, 0, Color.GREEN, icon, reference);
				} catch (Exception ex) {
				}
			}

		}
		return ma;
	}

	public String unescapeHTML(String source, int start) {
		int i, j;

		i = source.indexOf("&", start);
		if (i > -1) {
			j = source.indexOf(";", i);
			if (j > i) {
				String entityToLookFor = source.substring(i, j + 1);
				String value = (String) htmlEntities.get(entityToLookFor);
				if (value != null) {
					source = new StringBuffer().append(source.substring(0, i))
							.append(value).append(source.substring(j + 1))
							.toString();
					return unescapeHTML(source, i + 1); // recursive call
				}
			}
		}
		return source;
	}

	private static HashMap<String, String> htmlEntities;
	static {
		htmlEntities = new HashMap<String, String>();
		htmlEntities.put("&lt;", "<");
		htmlEntities.put("&gt;", ">");
		htmlEntities.put("&amp;", "&");
		htmlEntities.put("&quot;", "\"");
		htmlEntities.put("&agrave;", "ÃƒÂ ");
		htmlEntities.put("&Agrave;", "Ãƒâ‚¬");
		htmlEntities.put("&acirc;", "ÃƒÂ¢");
		htmlEntities.put("&auml;", "ÃƒÂ¤");
		htmlEntities.put("&Auml;", "Ãƒâ€ž");
		htmlEntities.put("&Acirc;", "Ãƒâ€š");
		htmlEntities.put("&aring;", "ÃƒÂ¥");
		htmlEntities.put("&Aring;", "Ãƒâ€¦");
		htmlEntities.put("&aelig;", "ÃƒÂ¦");
		htmlEntities.put("&AElig;", "Ãƒâ€ ");
		htmlEntities.put("&ccedil;", "ÃƒÂ§");
		htmlEntities.put("&Ccedil;", "Ãƒâ€¡");
		htmlEntities.put("&eacute;", "ÃƒÂ©");
		htmlEntities.put("&Eacute;", "Ãƒâ€°");
		htmlEntities.put("&egrave;", "ÃƒÂ¨");
		htmlEntities.put("&Egrave;", "ÃƒË†");
		htmlEntities.put("&ecirc;", "ÃƒÂª");
		htmlEntities.put("&Ecirc;", "ÃƒÅ ");
		htmlEntities.put("&euml;", "ÃƒÂ«");
		htmlEntities.put("&Euml;", "Ãƒâ€¹");
		htmlEntities.put("&iuml;", "ÃƒÂ¯");
		htmlEntities.put("&Iuml;", "Ãƒï¿½");
		htmlEntities.put("&ocirc;", "ÃƒÂ´");
		htmlEntities.put("&Ocirc;", "Ãƒâ€�");
		htmlEntities.put("&ouml;", "ÃƒÂ¶");
		htmlEntities.put("&Ouml;", "Ãƒâ€“");
		htmlEntities.put("&oslash;", "ÃƒÂ¸");
		htmlEntities.put("&Oslash;", "ÃƒËœ");
		htmlEntities.put("&szlig;", "ÃƒÅ¸");
		htmlEntities.put("&ugrave;", "ÃƒÂ¹");
		htmlEntities.put("&Ugrave;", "Ãƒâ„¢");
		htmlEntities.put("&ucirc;", "ÃƒÂ»");
		htmlEntities.put("&Ucirc;", "Ãƒâ€º");
		htmlEntities.put("&uuml;", "ÃƒÂ¼");
		htmlEntities.put("&Uuml;", "ÃƒÅ“");
		htmlEntities.put("&nbsp;", " ");
		htmlEntities.put("&copy;", "\u00a9");
		htmlEntities.put("&reg;", "\u00ae");
		htmlEntities.put("&euro;", "\u20a0");
	}

}
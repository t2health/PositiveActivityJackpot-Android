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


import android.location.Location;

/**
 * This class is used to represent a physical locations which have a latitude,
 * longitude, and alitude.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PhysicalLocation {
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double altitude = 0.0;

	private static float[] x = new float[1];
	private static double y = 0.0d;
	private static float[] z = new float[1];

	public PhysicalLocation() {
	}

	public PhysicalLocation(PhysicalLocation pl) {
		if (pl == null)
			throw new NullPointerException();

		set(pl.latitude, pl.longitude, pl.altitude);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param latitude
	 *            Latitude of the location in decimal format (example
	 *            39.931269).
	 * @param longitude
	 *            Longitude of the location in decimal format (example
	 *            -75.051261).
	 * @param altitude
	 *            Altitude of the location in meters (>0 is above sea level).
	 */
	public void set(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/**
	 * Set the Latitude of the PhysicalLocation.
	 * 
	 * @param latitude
	 *            Latitude of the location in decimal format (example
	 *            39.931269).
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Get the Latitude of the PhysicalLocation.
	 * 
	 * @return double representation the latitude of the location in decimal
	 *         format (example 39.931269).
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Set the Longitude of the PhysicalLocation.
	 * 
	 * @param longitude
	 *            Longitude of the location in decimal format (example
	 *            -75.051261).
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get the Longitude of the PhysicalLocation.
	 * 
	 * @return double representation the longitude of the location in decimal
	 *         format (example -75.051261).
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Set the Altitude of the PhysicalLocation.
	 * 
	 * @param altitude
	 *            Altitude of the location in meters (>0 is above sea level).
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * Get the Altitude of the PhysicalLocation.
	 * 
	 * @return double representation the altitude of the location in meters (>0
	 *         is above sea level).
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Converts a Location to a Vector given a PhysicalLocation.
	 * 
	 * @param org
	 *            Origin Location.
	 * @param gp
	 *            Current PhysicalLocation.
	 * @param v
	 *            Vector to populate.
	 * @throws NullPointerException
	 *             if Location, PhysicalLocation, or Vector is NULL.
	 */
	public static synchronized void convLocationToVector(Location org,
			PhysicalLocation gp, Vector v) {
		if (org == null || gp == null || v == null)
			throw new NullPointerException(
					"Location, PhysicalLocation, and Vector cannot be NULL.");

		Location.distanceBetween(org.getLatitude(), org.getLongitude(),
				gp.getLatitude(), org.getLongitude(), z);

		Location.distanceBetween(org.getLatitude(), org.getLongitude(),
				org.getLatitude(), gp.getLongitude(), x);
		y = gp.getAltitude() - org.getAltitude();
		if (org.getLatitude() < gp.getLatitude())
			z[0] *= -1;
		if (org.getLongitude() > gp.getLongitude())
			x[0] *= -1;

		v.set(x[0], (float) y, z[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "(lat=" + latitude + ", lng=" + longitude + ", alt=" + altitude
				+ ")";
	}
}
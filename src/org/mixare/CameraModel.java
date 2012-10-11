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


/**
 * Represents the camera and it's view. It also allows a user to project a point
 * given this camera's view.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class CameraModel {
	private static final float[] tmp1 = new float[3];
	private static final float[] tmp2 = new float[3];

	private int width = 0;
	private int height = 0;
	private float viewAngle = 0F;
	private float distance = 0F;

	public static final float DEFAULT_VIEW_ANGLE = (float) Math.toRadians(45);

	public CameraModel(int width, int height) {
		this(width, height, true);
	}

	public CameraModel(int width, int height, boolean init) {
		set(width, height, init);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param width
	 *            Width of the model.
	 * @param height
	 *            Height of the model.
	 * @param init
	 *            Should initialize the internal transform and lco objects.
	 */
	public void set(int width, int height, boolean init) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Width of the camera view.
	 * 
	 * @return int representing the width of camera view.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Height of the camera view.
	 * 
	 * @return int representing height of the camera view.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Set the View Angle of the camera model.
	 * 
	 * @param viewAngle
	 *            float representing the camera model.
	 */
	public void setViewAngle(float viewAngle) {
		this.viewAngle = viewAngle;
		this.distance = (this.width / 2) / (float) Math.tan(viewAngle / 2);
	}

	/**
	 * Set the View Angle of the camera model.
	 * 
	 * @param width
	 *            Width of the camera model.
	 * @param height
	 *            Height of the camera model.
	 * @param viewAngle
	 *            float representing the camera model.
	 */
	public void setViewAngle(int width, int height, float viewAngle) {
		this.viewAngle = viewAngle;
		this.distance = (width / 2) / (float) Math.tan(viewAngle / 2);
	}

	/**
	 * Project point from the origin Vector to the projected Vector.
	 * 
	 * @param orgPoint
	 *            Vector representing the origin.
	 * @param prjPoint
	 *            Vector representing the projected point.
	 * @param addX
	 *            Add X to the projected point.
	 * @param addY
	 *            Add Y to the projected point.
	 */
	public void projectPoint(Vector orgPoint, Vector prjPoint, float addX,
			float addY) {
		orgPoint.get(tmp1);
		tmp2[0] = (distance * tmp1[0] / -tmp1[2]);
		tmp2[1] = (distance * tmp1[1] / -tmp1[2]);
		tmp2[2] = (tmp1[2]);
		tmp2[0] = (tmp2[0] + addX + width / 2);
		tmp2[1] = (-tmp2[1] + addY + height / 2);
		prjPoint.set(tmp2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "CAM(" + width + "," + height + "," + viewAngle + "," + distance
				+ ")";
	}
}
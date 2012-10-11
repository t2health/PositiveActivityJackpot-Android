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
 * This class is used mostly as a utility to calculate relative positions.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class ScreenPosition {
	private float x = 0f;
	private float y = 0f;

	public ScreenPosition() {
		set(0, 0);
	}

	/**
	 * Set method for X and Y. Should be used instead of creating new objects.
	 * 
	 * @param x
	 *            X position.
	 * @param y
	 *            Y position.
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the X position.
	 * 
	 * @return Float X position.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Set the X position.
	 * 
	 * @param x
	 *            Float X position.
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Get the Y position.
	 * 
	 * @return Float Y position.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Set the Y position.
	 * 
	 * @param y
	 *            Float Y position.
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Rotate the positions around the angle t.
	 * 
	 * @param t
	 *            Angle to rotate around.
	 */
	public void rotate(double t) {
		float xp = (float) Math.cos(t) * x - (float) Math.sin(t) * y;
		float yp = (float) Math.sin(t) * x + (float) Math.cos(t) * y;

		x = xp;
		y = yp;
	}

	/**
	 * Add the X and Y to the positions X and Y.
	 * 
	 * @param x
	 *            Float X to add to X.
	 * @param y
	 *            Float Y to add to Y.
	 */
	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "< x=" + x + " y=" + y + " >";
	}
}
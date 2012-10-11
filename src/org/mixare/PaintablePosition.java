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

import android.graphics.Canvas;

/**
 * This class extends PaintableObject and adds the ability to rotate and scale.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintablePosition extends PaintableObject {
	private float width = 0, height = 0;
	private float objX = 0, objY = 0, objRotation = 0, objScale = 0;
	private PaintableObject obj = null;

	public PaintablePosition(PaintableObject drawObj, float x, float y,
			float rotation, float scale) {
		set(drawObj, x, y, rotation, scale);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param drawObj
	 *            Object to set for this Position.
	 * @param x
	 *            X coordinate of the Position.
	 * @param y
	 *            Y coordinate of the Position.
	 * @param rotation
	 *            Rotation of the Position.
	 * @param scale
	 *            Scale of the Position.
	 * @throws NullPointerException
	 *             if PaintableObject is NULL.
	 */
	public void set(PaintableObject drawObj, float x, float y, float rotation,
			float scale) {
		if (drawObj == null)
			throw new NullPointerException();

		this.obj = drawObj;
		this.objX = x;
		this.objY = y;
		this.objRotation = rotation;
		this.objScale = scale;
		this.width = obj.getWidth();
		this.height = obj.getHeight();
	}

	/**
	 * Move the object.
	 * 
	 * @param x
	 *            New X coordinate of the Position.
	 * @param y
	 *            New Y coordinate of the Position.
	 */
	public void move(float x, float y) {
		objX = x;
		objY = y;
	}

	/**
	 * X coordinate of the Object.
	 * 
	 * @return float X coordinate.
	 */
	public float getObjectsX() {
		return objX;
	}

	/**
	 * Y coordinate of the Object.
	 * 
	 * @return float Y coordinate.
	 */
	public float getObjectsY() {
		return objY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Canvas canvas) {
		if (canvas == null || obj == null)
			throw new NullPointerException();

		paintObj(canvas, obj, objX, objY, objRotation, objScale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getWidth() {
		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getHeight() {
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "< objX=" + objX + " objY=" + objY + " width=" + width
				+ " height=" + height + " >";
	}
}
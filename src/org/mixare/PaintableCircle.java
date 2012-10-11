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
 * This class extends PaintableObject to draw a circle with a given radius.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableCircle extends PaintableObject {
	private int color = 0;
	private float radius = 0;
	private boolean fill = false;

	public PaintableCircle(int color, float radius, boolean fill) {
		set(color, radius, fill);
	}

	/**
	 * Set the objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param color
	 *            Color of the circle.
	 * @param radius
	 *            Radius of the circle.
	 * @param fill
	 *            Fill color of the circle.
	 */
	public void set(int color, float radius, boolean fill) {
		this.color = color;
		this.radius = radius;
		this.fill = fill;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Canvas canvas) {
		if (canvas == null)
			throw new NullPointerException();

		setFill(fill);
		setColor(color);
		paintCircle(canvas, 0, 0, radius);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getWidth() {
		return radius * 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getHeight() {
		return radius * 2;
	}
}
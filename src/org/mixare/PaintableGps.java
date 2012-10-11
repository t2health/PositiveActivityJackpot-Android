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
 * This class extends PaintableObject to draw a circle with a given radius and a
 * stroke width.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableGps extends PaintableObject {
	private float radius = 0;
	private float strokeWidth = 0;
	private boolean fill = false;
	private int color = 0;

	public PaintableGps(float radius, float strokeWidth, boolean fill, int color) {
		set(radius, strokeWidth, fill, color);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param radius
	 *            Radius of the circle representing the GPS position.
	 * @param strokeWidth
	 *            Stroke width of the text representing the GPS position.
	 * @param fill
	 *            Fill color of the circle representing the GPS position.
	 * @param color
	 *            Color of the circle representing the GPS position.
	 */
	public void set(float radius, float strokeWidth, boolean fill, int color) {
		this.radius = radius;
		this.strokeWidth = strokeWidth;
		this.fill = fill;
		this.color = color;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Canvas canvas) {
		if (canvas == null)
			throw new NullPointerException();

		setStrokeWidth(strokeWidth);
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
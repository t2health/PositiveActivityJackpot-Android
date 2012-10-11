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
 * This class extends PaintableObject and draws a small rectangle.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintablePoint extends PaintableObject {
	private static int width = 6;
	private static int height = 6;

	private int color = 0;
	private boolean fill = false;

	public PaintablePoint(int color, boolean fill) {
		set(color, fill);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param color
	 *            Color to set the rectangle representing this Point.
	 * @param fill
	 *            Fill color to set the rectangle representing this Point.
	 */
	public void set(int color, boolean fill) {
		this.color = color;
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
		paintRect(canvas, -1, -1, width, height);
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
}
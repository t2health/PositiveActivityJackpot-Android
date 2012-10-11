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
import android.graphics.Color;

/**
 * This class extends PaintableObject to draw an outlined box.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableBox extends PaintableObject {
	private float width = 0, height = 0;
	private int borderColor = Color.rgb(255, 255, 255);
	private int backgroundColor = Color.argb(128, 0, 0, 0);

	public PaintableBox(float width, float height) {
		this(width, height, Color.rgb(255, 255, 255), Color.argb(128, 0, 0, 0));
	}

	public PaintableBox(float width, float height, int borderColor, int bgColor) {
		set(width, height, borderColor, bgColor);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param width
	 *            width of the box.
	 * @param height
	 *            height of the box.
	 */
	public void set(float width, float height) {
		set(width, height, borderColor, backgroundColor);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param width
	 *            width of the box.
	 * @param height
	 *            height of the box.
	 * @param borderColor
	 *            Color of the border.
	 * @param bgColor
	 *            Background color of the surrounding box.
	 */
	public void set(float width, float height, int borderColor, int bgColor) {
		this.width = width;
		this.height = height;
		this.borderColor = borderColor;
		this.backgroundColor = bgColor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Canvas canvas) {
		if (canvas == null)
			throw new NullPointerException();

		setFill(true);
		setColor(backgroundColor);
		paintRect(canvas, 0, 0, width, height);

		setFill(false);
		setColor(borderColor);
		paintRect(canvas, 0, 0, width, height);
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
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
 * This class extends PaintableObject to draw text.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableText extends PaintableObject {
	private static final float WIDTH_PAD = 4;
	private static final float HEIGHT_PAD = 2;

	private String text = null;
	private int color = 0;
	private int size = 0;
	private float width = 0;
	private float height = 0;
	private boolean bg = false;

	public PaintableText(String text, int color, int size,
			boolean paintBackground) {
		set(text, color, size, paintBackground);
	}

	/**
	 * Set this objects parameters. This should be used instead of creating new
	 * objects.
	 * 
	 * @param text
	 *            String representing this object.
	 * @param color
	 *            Color of the object.
	 * @param size
	 *            Size of the object.
	 * @param paintBackground
	 *            Should the background get rendered.
	 * @throws NullPointerException
	 *             if String param is NULL.
	 */
	public void set(String text, int color, int size, boolean paintBackground) {
		if (text == null)
			throw new NullPointerException();

		this.text = text;
		this.bg = paintBackground;
		this.color = color;
		this.size = size;
		this.width = getTextWidth(text) + WIDTH_PAD * 2;
		this.height = getTextAsc() + getTextDesc() + HEIGHT_PAD * 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Canvas canvas) {
		if (canvas == null || text == null)
			throw new NullPointerException();

		setColor(color);
		setFontSize(size);
		if (bg) {
			setColor(Color.rgb(0, 0, 0));
			setFill(true);
			paintRect(canvas, -(width / 2), -(height / 2), width, height);
			setColor(Color.rgb(255, 255, 255));
			setFill(false);
			paintRect(canvas, -(width / 2), -(height / 2), width, height);
		}
		paintText(canvas, (WIDTH_PAD - width / 2),
				(HEIGHT_PAD + getTextAsc() - height / 2), text);
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
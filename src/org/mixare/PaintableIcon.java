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

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This class extends PaintableObject to draw an icon.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableIcon extends PaintableObject {
	private Bitmap bitmap = null;

	public PaintableIcon(Bitmap bitmap, int width, int height) {
		set(bitmap, width, height);
	}

	/**
	 * Set the bitmap. This should be used instead of creating new objects.
	 * 
	 * @param bitmap
	 *            Bitmap that should be rendered.
	 * @throws NullPointerException
	 *             if Bitmap is NULL.
	 */
	public void set(Bitmap bitmap, int width, int height) {
		if (bitmap == null)
			throw new NullPointerException();

		this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Canvas canvas) {
		if (canvas == null || bitmap == null)
			throw new NullPointerException();

		paintBitmap(canvas, bitmap, -(bitmap.getWidth() / 2),
				-(bitmap.getHeight() / 2));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getWidth() {
		return bitmap.getWidth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getHeight() {
		return bitmap.getHeight();
	}
}
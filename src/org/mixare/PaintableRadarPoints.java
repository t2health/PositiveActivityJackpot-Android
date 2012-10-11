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
 * This class extends PaintableObject to draw all the Markers at their
 * appropriate locations.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableRadarPoints extends PaintableObject {
	private final float[] locationArray = new float[3];
	private PaintablePoint paintablePoint = null;
	private PaintablePosition pointContainer = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Canvas canvas) {
		if (canvas == null)
			throw new NullPointerException();

		// Draw the markers in the circle
		float range = ARData.getRadius() * 1000;
		float scale = range / Radar.RADIUS;
		for (Marker pm : ARData.getMarkers()) {
			pm.getLocation().get(locationArray);
			float x = locationArray[0] / scale;
			float y = locationArray[2] / scale;
			if ((x * x + y * y) < (Radar.RADIUS * Radar.RADIUS)) {
				if (paintablePoint == null)
					paintablePoint = new PaintablePoint(pm.getColor(), true);
				else
					paintablePoint.set(pm.getColor(), true);

				if (pointContainer == null)
					pointContainer = new PaintablePosition(paintablePoint, (x
							+ Radar.RADIUS - 1), (y + Radar.RADIUS - 1), 0, 1);
				else
					pointContainer.set(paintablePoint, (x + Radar.RADIUS - 1),
							(y + Radar.RADIUS - 1), 0, 1);

				pointContainer.paint(canvas);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getWidth() {
		return Radar.RADIUS * 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getHeight() {
		return Radar.RADIUS * 2;
	}
}
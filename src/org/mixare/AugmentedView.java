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

import java.text.DecimalFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.View;

/**
 * This class extends the View class and is designed draw the zoom bar, radar
 * circle, and markers on the View.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedView extends View {
	private static final AtomicBoolean drawing = new AtomicBoolean(false);
	private static final DecimalFormat FORMAT = new DecimalFormat("#");
	private static Paint loadingpaint;
	private static final int fontSize = 14;
	private static final int startLabelX = 4;
	private static final int endLabelX = 89;
	private static final int labelY = 107;
	private static final String startKM = "0km";
	private static final String endKM = FORMAT.format(AugmentedReality.MAX_ZOOM) + "km";
	private static final int leftBound = 12;
	private static final int rightBound = 79;
	private static final int conflictHeight = 82;
	private static final Radar radar = new Radar();

	private static PaintablePosition startTxtContainter = null;
	private static PaintablePosition endTxtContainter = null;
	private static PaintablePosition currentTxtContainter = null;
	private static int lastZoom = 0;

	public AugmentedView(Context context) {
		super(context);
		loadingpaint = new Paint();
		loadingpaint.setColor(Color.BLUE);
		loadingpaint.setStyle(Style.FILL);
		loadingpaint.setTextSize(20);
		loadingpaint.setTypeface(Typeface.DEFAULT_BOLD); 

	}

	private static PaintablePosition generateCurrentZoom(Canvas canvas) {
		lastZoom = ARData.getZoomProgress();
		PaintableBoxedText currentTxtBlock = new PaintableBoxedText(
				ARData.getZoomLevel(), fontSize, 30);
		int x = canvas.getWidth() / 100 * lastZoom;
		int y = canvas.getHeight() / 100 * labelY;
		if (lastZoom < leftBound || lastZoom > rightBound) {
			y = canvas.getHeight() / 30 * conflictHeight;
			if (lastZoom < leftBound)
				x = canvas.getWidth() / 30 * startLabelX;
			else
				x = canvas.getWidth() / 30 * endLabelX;
		}
		PaintablePosition container = new PaintablePosition(currentTxtBlock, x,
				y, 0, 1);
		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (canvas == null)
			return;

		if (drawing.compareAndSet(false, true)) {
			if (startTxtContainter == null) {
				PaintableBoxedText startTextBlock = new PaintableBoxedText(
						startKM, fontSize, 30);
				startTxtContainter = new PaintablePosition(startTextBlock,
						10,
						(canvas.getHeight() -50 ), 0, 1);
			}
			startTxtContainter.paint(canvas);

			if (endTxtContainter == null) {
				PaintableBoxedText endTextBlock = new PaintableBoxedText(endKM,
						fontSize, 30);
				endTxtContainter = new PaintablePosition(endTextBlock,
						(canvas.getWidth() -75),
						(canvas.getHeight()-50), 0, 1);
			}
			endTxtContainter.paint(canvas);

			// Re-factor zoom text, if it has changed.
			if (lastZoom != ARData.getZoomProgress())
				currentTxtContainter = generateCurrentZoom(canvas);
			currentTxtContainter.paint(canvas);

			List<Marker> collection = ARData.getMarkers();
			int column = 0;
			int index0 = 0;
			int index1 = 0;
			
			// Draw AR markers (limit to 10)

			int numDrawn = 0;
			ListIterator<Marker> iter = collection.listIterator(0);
			while (iter.hasNext()) {
				Marker marker = iter.next();

				// Calculate the visibility of this Marker
				marker.update(canvas, 0, 0);

				//Set radar point color
				int tcolor = 255 - ((int)(marker.distance / 255) * 2); //235 is highest value for our 30km distance limit
				marker.color = Color.argb(tcolor,0, 0, tcolor);
				
				if (marker.isInView)
				{
					//split into two columns 
					if(column == 0)
					{
						marker.index = index0;
						marker.column = 0;
						column = 1;
						index0++;
					}
					else
					{
						marker.index = index1;
						marker.column = 1;
						column = 0;
						index1++;
					}
					marker.draw(canvas);
					numDrawn++;

					if(numDrawn >= 10)
						break;
				}

			}
			//
			if(collection.size() <= 0)
			{
				String loadingText = "Please wait, loading...";
				canvas.drawText(loadingText, (canvas.getWidth() /2) - (loadingpaint.measureText(loadingText) /2), (canvas.getHeight() /2) + 20, loadingpaint);
			}
			// Radar circle and radar markers
			radar.draw(canvas);
			drawing.set(false);
		}
	}


}

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
 * This class implements a low-pass filter. A low-pass filter is an electronic
 * filter that passes low-frequency signals but attenuates (reduces the
 * amplitude of) signals with frequencies higher than the cutoff frequency. The
 * actual amount of attenuation for each frequency varies from filter to filter.
 * It is sometimes called a high-cut filter, or treble cut filter when used in
 * audio applications.
 * 
 * @author Justin Wetherell (phishman3579@gmail.com)
 */
public class LowPassFilter {

	/*
	 * Time smoothing constant for low-pass filter See:
	 * http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
	 */
	private static final float ALPHA = 0.333f;

	private LowPassFilter() {
	}

	/**
	 * Filter the given input against the previous values and return a low-pass
	 * filtered result.
	 * 
	 * @param input
	 *            float array to smooth.
	 * @param prev
	 *            float array representing the previous values.
	 * @return float array smoothed with a low-pass filter.
	 */
	public static float[] filter(float[] input, float[] prev) {
		if (input == null || prev == null)
			throw new NullPointerException(
					"input and prev float arrays must be non-NULL");
		if (input.length != prev.length)
			throw new IllegalArgumentException(
					"input and prev must be the same length");

		for (int i = 0; i < input.length; i++) {
			prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
		}
		return prev;
	}
}

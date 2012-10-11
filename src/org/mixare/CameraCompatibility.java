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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.hardware.Camera;

/**
 * Ensures compatibility with older and newer versions of the API. See the SDK
 * docs for comments
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class CameraCompatibility {
	private static Method getSupportedPreviewSizes = null;

	static {
		initCompatibility();
	};

	/**
	 * This will fail on older phones (Android version < 2.0)
	 */
	private static void initCompatibility() {
		try {
			getSupportedPreviewSizes = Camera.Parameters.class.getMethod(
					"getSupportedPreviewSizes", new Class[] {});
			/* success, this is a newer device */
		} catch (NoSuchMethodException nsme) {
			/* failure, must be older device */
		}
	}

	/**
	 * If it's running on a new phone, let's get the supported preview sizes,
	 * before it was fixed to 480 x 320
	 * 
	 * @param params
	 *            Camera's parameters.
	 * @return List of supported Camera viewer sizes.
	 * @throws RuntimeException
	 *             if supported size fails.
	 */
	@SuppressWarnings("unchecked")
	public static List<Camera.Size> getSupportedPreviewSizes(
			Camera.Parameters params) {
		List<Camera.Size> retList = null;

		try {
			Object retObj = getSupportedPreviewSizes.invoke(params);
			if (retObj != null) {
				retList = (List<Camera.Size>) retObj;
			}
		} catch (InvocationTargetException ite) {
			/* unpack original exception when possible */
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				/* unexpected checked exception; wrap and re-throw */
				throw new RuntimeException(ite);
			}
		} catch (IllegalAccessException ie) {
			ie.printStackTrace();
		}
		return retList;
	}
}
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

/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file was an original part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */



/**
 * A state class used to calculate bearing and pitch given a Matrix.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PitchAzimuthCalculator {
    private static final Vector looking = new Vector();
    private static final float[] lookingArray = new float[3];

    private static volatile float azimuth = 0;

    private static volatile float pitch = 0;

    private PitchAzimuthCalculator() {};

    public static synchronized float getAzimuth() {
        return PitchAzimuthCalculator.azimuth;
    }
    public static synchronized float getPitch() {
        return PitchAzimuthCalculator.pitch;
    }

    public static synchronized void calcPitchBearing(Matrix rotationM) {
        if (rotationM==null) return;

        looking.set(0, 0, 0);
        rotationM.transpose();
        looking.set(1, 0, 0);
        looking.prod(rotationM);
        looking.get(lookingArray);
        PitchAzimuthCalculator.azimuth = ((Utilities.getAngle(0, 0, lookingArray[0], lookingArray[2])  + 360 ) % 360);

        rotationM.transpose();
        looking.set(0, 1, 0);
        looking.prod(rotationM);
        looking.get(lookingArray);
        PitchAzimuthCalculator.pitch = -Utilities.getAngle(0, 0, lookingArray[1], lookingArray[2]);
    }
}
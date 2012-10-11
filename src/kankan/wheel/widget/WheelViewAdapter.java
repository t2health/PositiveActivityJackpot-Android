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

package kankan.wheel.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * Wheel items adapter interface
 */
public interface WheelViewAdapter {
        /**
         * Gets items count
         * @return the count of wheel items
         */
        public int getItemsCount();
        
        /**
         * Get a View that displays the data at the specified position in the data set
         * 
         * @param index the item index
         * @param convertView the old view to reuse if possible
         * @param parent the parent that this view will eventually be attached to
         * @return the wheel item View
         */
        public View getItem(int index, View convertView, ViewGroup parent);

        /**
         * Get a View that displays an empty wheel item placed before the first or after
         * the last wheel item.
         * 
         * @param convertView the old view to reuse if possible
     * @param parent the parent that this view will eventually be attached to
         * @return the empty item View
         */
        public View getEmptyItem(View convertView, ViewGroup parent);
        
        /**
         * Register an observer that is called when changes happen to the data used by this adapter.
         * @param observer the observer to be registered
         */
        public void registerDataSetObserver(DataSetObserver observer);
        
        /**
         * Unregister an observer that has previously been registered
         * @param observer the observer to be unregistered
         */
        void unregisterDataSetObserver (DataSetObserver observer);
}
/**
 * Starting point for Augmented Reality application
 * Just a test of Mixare for now.
 * 
 * @author Steve Ody
 *
 */

package org.t2health.paj.activity;

import org.t2health.paj.classes.ActivityFactory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/**
 * Main activity. Launches splash screen then exits.
 * 
 * @author Steve Ody (stephen.ody@gmail.com)
 */

public class StartupActivity extends Activity {
    /** Called when the activity is first created. */
	
	Button btnStart;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        this.startActivity(ActivityFactory.getSplashActivity(this));
        this.finish();
    }
}
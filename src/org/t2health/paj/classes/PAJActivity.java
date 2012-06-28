package org.t2health.paj.classes;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class PAJActivity {
    public int icon;
    public String title;
    public String address;
    public String phone;
    public float rating;
    public String website;
    public String URL;
    public Drawable iconDrawable;
    
    public PAJActivity(){

    }
    
    public PAJActivity(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }
    
    public void SetIconDrawable(String URL)
    {
    	iconDrawable = new BitmapDrawable(Global.getBitmapFromURL(URL));
    }
}
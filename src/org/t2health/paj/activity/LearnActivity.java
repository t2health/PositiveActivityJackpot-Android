package org.t2health.paj.activity;

import org.t2health.paj.classes.SharedPref;

import t2.paj.R;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Learn activity uses a webview to display further information regarding the therapy behind positive activities
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class LearnActivity  extends ABSCustomTitleActivity
{

	CheckBox cbAudio;
	private static MediaPlayer mp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn);
		this.onEvent("LearnScreen-Open");
		
		this.SetMenuVisibility(1);
		this.btnMainSettings.setChecked(true);
		
		mp = MediaPlayer.create(this, R.raw.about);
		mp.setLooping(false);
		
		cbAudio = (CheckBox)this.findViewById(R.id.cb_audio);
		cbAudio.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( isChecked )
		        {
		        	SharedPref.setLearnAudio(true);
		        	if(!mp.isPlaying())
		        		mp.start();
		        }
		        else
		        {
		        	SharedPref.setLearnAudio(false);
		        	if(mp.isPlaying())
		        	{
		        		mp.pause();
		        		mp.seekTo(0);
		        	}
		        }

		    }
		});
		cbAudio.setChecked(SharedPref.getLearnAudio());
		
		TextView tv = (TextView)this.findViewById(R.id.tv_learn);
		tv.setText(Html.fromHtml(getString(R.string.learn_content)));
		tv.setContentDescription(Html.fromHtml(getString(R.string.learn_content)));
		
		if(SharedPref.getLearnAudio())
			if(!mp.isPlaying())
        		mp.start();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if(mp.isPlaying())
    	{
    		mp.pause();
    		mp.seekTo(0);
    	}
	}
	
}

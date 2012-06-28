package org.t2health.paj.activity;

import org.t2health.paj.classes.Global;

import android.content.Intent;
import android.os.Bundle;

public class WebViewActivity extends ABSWebViewActivity 
{

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_CONTENT = "content";

	public static final String EXTRA_TITLE_ID = "titleId";
	public static final String EXTRA_CONTENT_ID = "contentId";

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();

		int contentId = intent.getIntExtra(EXTRA_CONTENT_ID, -1);

		String contentString = intent.getStringExtra(EXTRA_CONTENT);

		Global.Log.v("Webview", "contentId:"+contentId);
		if(contentString == null && contentId == -1) 
		{
			this.finish();
			return;
		}

		if(contentId != -1) 
		{
			contentString = getString(contentId);
		}

		this.setContent(contentString);
	}
}
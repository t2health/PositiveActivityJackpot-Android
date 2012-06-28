package org.t2health.paj.activity;

import t2.paj.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class ABSWebViewActivity extends FlurryActivity 
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.webview_layout);

	}

	protected void setContent(String contentString) 
	{
		WebView wv = (WebView)this.findViewById(R.id.webview);
		formatWebViewText(this, wv, contentString, android.R.color.primary_text_dark);
	}

	public static void formatWebViewText(Activity c, WebView wv, String contentString, int textColor) 
	{
		if(contentString == null) 
		{
			contentString = "<span></span>";
		}

		TextView tv = new TextView(c);
		DisplayMetrics metrics = new DisplayMetrics();
		c.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int dpi = metrics.densityDpi;
		float textSizePixels = tv.getTextSize();
		int webViewFontSizePoints = (int)(textSizePixels * 72 / dpi * 2.75);
		int color = textColor;

		StringBuffer contentBuffer = new StringBuffer();
		contentBuffer.append("\n<style type=\"text/css\">\n");
		contentBuffer.append("\tbody,a {\n");
		contentBuffer.append("\t\tcolor:rgb(");
		contentBuffer.append(Color.red(color));
		contentBuffer.append(",");
		contentBuffer.append(Color.green(color));
		contentBuffer.append(",");
		contentBuffer.append(Color.blue(color));
		contentBuffer.append(");\n");
		contentBuffer.append("\t}\n");
		contentBuffer.append("</style>");
		contentBuffer.append(contentString);

		wv.setWebChromeClient(new WebChromeClient());
		wv.setBackgroundColor(Color.TRANSPARENT); // make the bg transparent

		WebSettings settings = wv.getSettings();
		settings.setDefaultFontSize(webViewFontSizePoints);
		settings.setDefaultFixedFontSize(webViewFontSizePoints);
		settings.setJavaScriptEnabled(true);

		wv.loadDataWithBaseURL("", contentBuffer.toString(), "text/html", "utf-8", null);
	}
}

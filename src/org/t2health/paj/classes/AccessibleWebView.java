package org.t2health.paj.classes;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import t2.paj.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A WebView that is designed to read its contents. In order to get the screen
 * reading to work properly, you must enable accessibility in system settings,
 * enabled a screen reader such as TalkBack and properly configure the provider
 * for this webview.
 * The later can be done by adding the following to your Manifest file.
 *		<provider android:name="org.t2health.lib.accessibility.TtsContentProvider"
 *			android:authorities="<YOUR APP CLASSPATH HERE>.TtsContentProvider" />
 * @author robbiev
 * @TODO remove all accessibility functionality if this is Honeycomb or higher.
 * As newer versions of android have webview accessibility.
 */
public class AccessibleWebView extends WebView 
{
	private static final String TAG = AccessibleWebView.class.getSimpleName();

	private static final int WHAT_START_DOCUMENT_REACHED = 0;
	private static final int WHAT_END_DOCUMENT_REACHED = 1;

	private AccessibilityManager aManager;
	private JSInterface jsInterface;
	private JSInterfaceHandler jsInterfaceHandler;
	private boolean mEndOfDocReached = false;

	private AccessibleWebViewClient webViewClient;

	public AccessibleWebView(Context context) 
	{
		super(context);
		this.init();
	}

	public AccessibleWebView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.init();
	}

	public AccessibleWebView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		this.init();
	}

	/**
	 * Init the webview and the necessary fields.
	 */
	private void init() 
	{
		aManager = (AccessibilityManager)this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
		jsInterface = new JSInterface();
		jsInterfaceHandler = new JSInterfaceHandler();
		webViewClient = new AccessibleWebViewClient();

		this.setWebViewClient(webViewClient);
		this.setWebChromeClient(new WebChromeClient());
		this.setBackgroundColor(Color.WHITE); // make the bg transparent

		WebSettings settings = this.getSettings();
		settings.setJavaScriptEnabled(true);

		// register a class to handle communication between JS and this class.
		this.addJavascriptInterface(jsInterface, "T2AWV_INTERFACE");
	}

	@Override
	public void loadData(String data, String mimeType, String encoding) 
	{
		StringBuffer sb = new StringBuffer();

		// add the TTS code to the HTML if everything is configured well.
		if(aManager.isEnabled()) 
		{
			appendJS(sb);
		}

		sb.append(data);
		super.loadData(sb.toString(), mimeType, encoding);
	}

	@Override
	public void loadDataWithBaseURL(String baseUrl, String data,
			String mimeType, String encoding, String historyUrl) 
	{
		StringBuffer sb = new StringBuffer();

		// add the TTS code to the HTML if everything is configured well.
		if(aManager.isEnabled()) {
			appendJS(sb);
		}

		sb.append(data);
		super.loadDataWithBaseURL(baseUrl, sb.toString(), mimeType, encoding, historyUrl);
	}

	/**
	 * Handles the appending of the code. The JS is read from a file in the raw
	 * resources.
	 * @param sb
	 */
	private void appendJS(StringBuffer sb) 
	{
		sb.append("<script type=\"text/javascript\">\n");
		try 
		{
			BufferedReader is = new BufferedReader(new InputStreamReader(this.getContext().getResources().openRawResource(R.raw.ideal_tts_aggregated)));
			String line = null;
			while((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} 
		catch (IOException e) 
		{
			// ignore
		}
		sb.append("</script>\n");
	}

	private boolean shiftFocus(int direction) 
	{
		View v = focusSearch(direction);
		if(v != null) 
		{
			return v.requestFocus();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(aManager.isEnabled()) 
		{
			if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) 
			{
				super.onKeyDown(KeyEvent.KEYCODE_SPACE, changeKeyCode(event, KeyEvent.KEYCODE_SPACE));
				return true;

			} 
			else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) 
			{
				if(mEndOfDocReached) 
				{
					return true;
				}
				super.onKeyDown(KeyEvent.KEYCODE_P, changeKeyCode(event, KeyEvent.KEYCODE_P));
				return true;

			} 
			else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) 
			{
				mEndOfDocReached = false;
				super.onKeyDown(KeyEvent.KEYCODE_Q, changeKeyCode(event, KeyEvent.KEYCODE_Q));
				return true;

			} 
			else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 
			{
				if(!shiftFocus(View.FOCUS_RIGHT)) 
				{
					shiftFocus(View.FOCUS_DOWN);
				}
				return true;

			} else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) 
			{
				if(!shiftFocus(View.FOCUS_LEFT)) 
				{
					shiftFocus(View.FOCUS_UP);
				}
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) 
	{
		if(aManager.isEnabled()) 
		{
			if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) 
			{
				super.onKeyDown(KeyEvent.KEYCODE_SPACE, changeKeyCode(event, KeyEvent.KEYCODE_SPACE));
				return true;

			} 
			else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) 
			{
				if(mEndOfDocReached) 
				{
					return true;
				}
				super.onKeyUp(KeyEvent.KEYCODE_P, changeKeyCode(event, KeyEvent.KEYCODE_P));
				return true;

			} 
			else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) 
			{
				mEndOfDocReached = false;
				super.onKeyUp(KeyEvent.KEYCODE_Q, changeKeyCode(event, KeyEvent.KEYCODE_Q));
				return true;

			} 
			else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 
			{
				if(!shiftFocus(View.FOCUS_RIGHT)) 
				{
					shiftFocus(View.FOCUS_DOWN);
				}
				return true;

			} 
			else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) 
			{
				if(!shiftFocus(View.FOCUS_LEFT)) 
				{
					shiftFocus(View.FOCUS_UP);
				}
				return true;
			}
		}

		return super.onKeyUp(keyCode, event);
	}

	private static KeyEvent changeKeyCode(KeyEvent base, int newKeyCode) 
	{
		return new KeyEvent(
				base.getDownTime(),
				base.getEventTime(),
				base.getAction(),
				newKeyCode,
				base.getRepeatCount(),
				base.getMetaState(),
				base.getDeviceId(),
				base.getScanCode(),
				base.getFlags()
				);
	}

	private void speakText(String text, int queueMode) 
	{
		if(aManager.isEnabled()) 
		{
			aManager.interrupt();

			AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_FOCUSED);
			event.setPackageName(this.getClass().getPackage().toString());
			event.setClassName(this.getClass().getSimpleName());
			event.setContentDescription(text);
			event.setEventTime(System.currentTimeMillis());
			aManager.sendAccessibilityEvent(event);
		}
	}

	/**
	 * Handles the communication between the JS and this class.
	 * @author robbiev
	 *
	 */
	private class JSInterface {
		@SuppressWarnings("unused")
		public void startOfDocumentReached() 
		{
			jsInterfaceHandler.sendEmptyMessage(WHAT_START_DOCUMENT_REACHED);
		}

		@SuppressWarnings("unused")
		public void endOfDocuemntReached() 
		{
			jsInterfaceHandler.sendEmptyMessage(WHAT_END_DOCUMENT_REACHED);
		}
	}

	private class JSInterfaceHandler extends Handler 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what) 
			{
			// we are at the start of the doc, shift focus to the view above.
			case WHAT_START_DOCUMENT_REACHED:
				if(!shiftFocus(FOCUS_UP)) 
				{
					shiftFocus(FOCUS_LEFT);
				}
				break;
				// we are at the start of the doc, shift focus to the view below.
			case WHAT_END_DOCUMENT_REACHED:
				mEndOfDocReached = true;
				if(!shiftFocus(FOCUS_DOWN)) 
				{
					shiftFocus(FOCUS_RIGHT);
				}
				break;
			}
		}
	}

	private class AccessibleWebViewClient extends WebViewClient 
	{
		@Override
		public void onLoadResource(WebView view, String url) 
		{
			// If accessibility is enabled, then try to work with the URL.
			if(aManager.isEnabled()) 
			{
				Uri uri = Uri.parse(url);
				List<String> segments = uri.getPathSegments();

				if(uri.getScheme().equals("content") && uri.getHost().equals("com.ideal.webaccess.tts") && segments.size() > 2) 
				{
					String modeStr = segments.get(0);
					//String date = segments.get(1);
					StringBuffer message = new StringBuffer(segments.get(2));
					for(int i = 3; i < segments.size(); ++i) 
					{
						message.append("/");
						message.append(segments.get(i));
					}

					int mode = TextToSpeech.QUEUE_FLUSH;
					if(modeStr.startsWith("1")) 
					{
						mode = TextToSpeech.QUEUE_ADD;
					}
					Global.Log.v(TAG, "DD");
					speakText(message.toString(), mode);

					return;
				}
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) 
		{
			// Load the url
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			((Activity)getContext()).startActivity(intent);

			return false;
		}
	}
}

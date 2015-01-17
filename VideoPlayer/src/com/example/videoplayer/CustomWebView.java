package com.example.videoplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * Youtube 拡大ボタン対応
 * 
 * @author mshibata
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CustomWebView extends WebView {

	private Context context;

	// private FrameLayout customViewContainer;
	private FrameLayout mContentView;
	private FrameLayout mBrowserFrameLayout;
	private FrameLayout mLayout;
	// Activity mActivity;

	static final String TAG = CustomWebView.class.getSimpleName();

	private void init(Context context) {
		this.context = context;
		Activity a = (Activity) context;

		mLayout = new FrameLayout(context);

		mBrowserFrameLayout = (FrameLayout) LayoutInflater.from(a).inflate(
				R.layout.custom_screen, null);
		mContentView = (FrameLayout) mBrowserFrameLayout
				.findViewById(R.id.main_content);
		// FrameLayout customViewContainer = (FrameLayout) mBrowserFrameLayout
		// .findViewById(R.id.fullscreen_custom_content);

		mLayout.addView(mBrowserFrameLayout, MainActivity.COVER_SCREEN_PARAMS);

		// Configure the webview
		WebSettings s = getSettings();
		s.setBuiltInZoomControls(true);
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(true);
		// s.setSavePassword(true);
		s.setSaveFormData(true);
		s.setJavaScriptEnabled(true);
		s.setPluginState(PluginState.ON);
		// customWebChromeClient = new CustomWebChromeClient();
		// setWebChromeClient(customWebChromeClient);

		setWebViewClient(new WebViewClient());

		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		// enable navigator.geolocation
		// s.setGeolocationEnabled(true);
		// s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");

		// enable Web Storage: localStorage, sessionStorage
		s.setDomStorageEnabled(true);

		mContentView.addView(this);
	}

	private void syncStaticSettings(WebSettings settings) {
		// settings.setDefaultFontSize(16);
		// settings.setDefaultFixedFontSize(13);

		// WebView inside Browser doesn't want initial focus to be set.
		settings.setNeedInitialFocus(false);
		// Browser supports multiple windows
		settings.setSupportMultipleWindows(true);
		// enable smooth transition for better performance during panning or
		// zooming
		settings.setEnableSmoothTransition(true);
		// WebView should be preserving the memory as much as possible.
		// However, apps like browser wish to turn on the performance mode which
		// would require more memory.
		// disable content url access

		settings.setAllowContentAccess(false);

		// HTML5 API flags
		settings.setAppCacheEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);

		// HTML5 configuration parametersettings.
		// settings.setAppCacheMaxSize(3 * 1024 *
		// 1024);//getWebStorageSizeManager().getAppCacheMaxSize());
		settings.setAppCachePath(context.getDir("appcache", 0).getPath());
		settings.setAppCacheEnabled(true);
		settings.setDatabasePath(context.getDir("databases", 0).getPath());
		settings.setGeolocationDatabasePath(context.getDir("geolocation", 0)
				.getPath());
		// origin policy for file access
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			settings.setAllowUniversalAccessFromFileURLs(false);
			settings.setAllowFileAccessFromFileURLs(false);
		}
	}

	public CustomWebView(Context context) {
		super(context);
		init(context);
	}

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FrameLayout getLayout() {
		return mLayout;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((MainActivity.customView == null) && canGoBack()) {
				goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setContentVisibility(int view_flag) {
		mContentView.setSystemUiVisibility(view_flag);
	}

	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(
					android.R.color.black));
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}

	}

}
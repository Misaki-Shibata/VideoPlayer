package com.example.videoplayer;

import com.example.videoplayer.CustomWebView.FullscreenHolder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * @author mshibata Since WebChromeClient is Activity and close , I implement
 *         the Activity side
 */
public class MainActivity extends Activity {
	private Context context;
	public static View customView;
	private CustomWebView customWebView;
	private CustomWebChromeClient customWebChromeClient;
	private WebChromeClient.CustomViewCallback customViewCallback;
	public static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	private class CustomWebChromeClient extends WebChromeClient {
		private View videoProgressView;
		private FullscreenHolder fullscreenContainer;
		private int originalOrientation;

		@Override
		public void onShowCustomView(View view,
				WebChromeClient.CustomViewCallback callback) {
			// if a view already exists then immediately terminate the new one
			if (customView != null) {
				callback.onCustomViewHidden();
				return;
			}

			originalOrientation = getRequestedOrientation();
			FrameLayout decor = (FrameLayout) getWindow().getDecorView();
			fullscreenContainer = new FullscreenHolder(getApplicationContext());
			fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
			decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
			customView = view;
			setFullscreen(true);
			customWebView.setVisibility(View.INVISIBLE);
			customViewCallback = callback;
			int requestedOrientation = getRequestedOrientation();
			// android.util.Log.i(getClass().getSimpleName(),
			// "setting orientation to : " + requestedOrientation);
			setRequestedOrientation(requestedOrientation);
		}

		@Override
		public void onHideCustomView() {
			customWebView.setVisibility(View.VISIBLE);
			if (customView == null)
				return;
			setFullscreen(false);
			FrameLayout decor = (FrameLayout) getWindow().getDecorView();
			decor.removeView(fullscreenContainer);
			fullscreenContainer = null;
			customView = null;

			customViewCallback.onCustomViewHidden();
			setRequestedOrientation(originalOrientation);
		}

		@Override
		public View getVideoLoadingProgressView() {
			if (videoProgressView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				videoProgressView = inflater.inflate(
						R.layout.video_loading_progress, null);
			}
			return videoProgressView;
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			setTitle(title);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
					newProgress * 100);
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		customWebView = new CustomWebView(this);
		// customWebView.mActivity = this;
		if (savedInstanceState != null) {
			customWebView.restoreState(savedInstanceState);
		} else {
			customWebView
					.loadUrl("https://www.youtube.com/watch?v=Fn9DEPD7Rko#t=30");
		}
		customWebChromeClient = new CustomWebChromeClient();
		customWebView.setWebChromeClient(customWebChromeClient);
		setContentView(customWebView.getLayout());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		customWebView.saveState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
		customWebView.stopLoading();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (inCustomView()) {
				hideCustomView();
				// customWebView.goBack();
				// customWebView.goBack();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean inCustomView() {
		return (customView != null);
	}

	public void hideCustomView() {
		customWebChromeClient.onHideCustomView();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setFullscreen(boolean enabled) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (enabled) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
			if (customView != null) {
				customView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			} else {
				customWebView.setContentVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			}
		}
		win.setAttributes(winParams);
	}
}

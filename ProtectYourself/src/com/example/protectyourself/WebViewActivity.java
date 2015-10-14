package com.example.protectyourself;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Fragment {

	private WebView webView;
	ProgressDialog pd;
	
	public static WebViewActivity newInastance(String content){
		WebViewActivity fragment = new WebViewActivity();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.activity_web_view,container,false);
		pd = ProgressDialog.show(getActivity(), "Loding...", "Please wait");
		
		webView = (WebView) view.findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyCustomWebViewClient());
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.loadUrl("http://www.workerswallet.com.au/test/manil.html");
		webView.clearCache(true);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		
		return view;
	}
	 private class MyCustomWebViewClient extends WebViewClient {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            view.loadUrl(url);
	            return true;
	        }
	        
	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        super.onPageStarted(view, url, favicon);
	          pd.show();
	        }
	        
	        @Override
	        public void onPageFinished(WebView view, String url) {
	        super.onPageFinished(view, url);
	        pd.dismiss();
	        }
	    }
}

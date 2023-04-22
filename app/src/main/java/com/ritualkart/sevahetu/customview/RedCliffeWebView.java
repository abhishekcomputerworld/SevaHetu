package com.ritualkart.sevahetu.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.ritualkart.sevahetu.ui.dashboard.webview.IWebViewListener;

public class RedCliffeWebView extends WebView {

    IWebViewListener webViewListener;

    public RedCliffeWebView(Context context) {
        super(context);
        webViewListener = (IWebViewListener) context;
    }
    public RedCliffeWebView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);

    }
    @Override
    public void loadUrl(String url) {
            if(!url.contains("client=android")) {
                if (url.contains("?")) {
                    url = url + "&client=android";
                } else {
                    url = url + "?client=android";
                }
        }
        super.loadUrl(url);
    }
}
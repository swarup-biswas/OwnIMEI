package com.example.ownimei.webclint;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebClint extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}

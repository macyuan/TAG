package com.ccxt.whl.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ccxt.whl.R;

/**
 * Created by Apple on 15/10/24.
 */
public class WebActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = (WebView)findViewById(R.id.wv);
        webView.getSettings().setJavaScriptEnabled(true);
        Intent it = getIntent();
        String link = it.getStringExtra("link");
        webView.loadUrl("http://www.sirpan.com/index.php/Products/single/gid/"+link+".html");
        webView.setWebViewClient(new EShopClient());
    }

    private class EShopClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

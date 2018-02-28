package me.rishabhkhanna.messingwebview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
                                     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                     @Override
                                     public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                                         Log.d(TAG, "shouldInterceptRequest: " + request.getUrl().getPath());
                                         if(request.getUrl().getPath().equals("/favicon.ico")) {
                                             Log.d(TAG, "favicon hit");
                                             try {
                                                 return new WebResourceResponse("image/png", null, new BufferedInputStream(view.getContext().getAssets().open("empty_favicon.ico")));
                                              } catch (IOException e) {
                                                 e.printStackTrace();
                                             }
                                         }
                                         Log.d(TAG, "shouldInterceptRequest:  " + request.getRequestHeaders());
                                         Log.d(TAG, "shouldInterceptRequest:  " + request.getUrl());
                                         return super.shouldInterceptRequest(view, request);
                                     }
                                 }


        );

        webView.loadUrl("http://tinder.com");
    }
}

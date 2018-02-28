package me.rishabhkhanna.messingwebview;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import java.io.BufferedInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String target_url = "http://www.facebook.com";
    private static final String target_url_prefix = "www.facebook.com";
    public static final String TAG = "MainActivity";
    WebView webView;
    private WebView mWebviewPop;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        frameLayout = findViewById(R.id.frame_layout);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webView.setWebChromeClient(new UriChromeClient());
//        webView.setWebViewClient(new UriWebViewClient());
        webView.setWebViewClient(new WebViewClient() {
                                     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                     @Override
                                     public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                                         Log.d(TAG, "shouldInterceptRequest: " + request.getUrl().getPath());
                                         if (request.getUrl().getPath().equals("/favicon.ico")) {
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

                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         String host = Uri.parse(url).getHost();
                                         //Log.d("shouldOverrideUrlLoading", url);
                                         if (host.equals(target_url_prefix)) {
                                             // This is my web site, so do not override; let my WebView load
                                             // the page
                                             if (mWebviewPop != null) {
                                                 mWebviewPop.setVisibility(View.GONE);
                                                 frameLayout.removeView(mWebviewPop);
                                                 mWebviewPop = null;
                                             }
                                             return false;
                                         }

                                         if (host.equals("m.facebook.com") || host.equals("tinder.com")
                                                 || host.equals("api.gotinder.com")) {
                                             return false;
                                         }
                                         // Otherwise, the link is not for a page on my site, so launch
                                         // another Activity that handles URLs
                                         Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                         startActivity(intent);
                                         return true;
                                     }

                                     @Override
                                     public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                                                    SslError error) {
                                         Log.d("onReceivedSslError", "onReceivedSslError");
                                         //super.onReceivedSslError(view, handler, error);
                                     }

                                 }
        );

        webView.loadUrl("http://tinder.com");
    }

    private class UriWebViewClient extends WebViewClient {


    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(MainActivity.this);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setSavePassword(false);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            frameLayout.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

    }
}



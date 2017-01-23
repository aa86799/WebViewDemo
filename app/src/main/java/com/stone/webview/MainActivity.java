package com.stone.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.stone.webview.bean.User;

import java.util.Map;

import static android.R.attr.description;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 06/01/2017 17 37
 */
public class MainActivity extends Activity {

    private WebView mWebView;
    private String mUrl;
    private TextView mTvTitle;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    // 这里的mUrl 一般可能是登录功能的url

                    String cookie = msg.obj.toString();
//                   CookieSyncManager: This class was deprecated in API level 21.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        CookieManager.getInstance().flush(); //强制刷新
                    } else {
                        CookieSyncManager.createInstance(getApplicationContext());
                        CookieManager.getInstance().setAcceptCookie(true);
                        CookieManager.getInstance().setCookie(mUrl, cookie);
                        CookieSyncManager.getInstance().sync();
                    }
                    mWebView.loadUrl(mUrl);
                }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.wv_main);
        mTvTitle = (TextView) findViewById(R.id.tv_url);

        mUrl = "http://www.baidu.com";
        mUrl = "http://down.znds.com/apk/tool/2014/0411/449.html";//apk下载
//        mUrl = "http://Not.found.this.page.";//Not found this page.

        mWebView.loadUrl(mUrl);

        /*
        WebViewClient主要帮助WebView处理各种通知、请求事件的，比如：
            onLoadResource  onPageStart onPageFinish    onReceiveError  onReceivedHttpAuthRequest
         */
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*
                返回值， true：当返回true时，任何链接都是需要自己处理的
                       false：webView会自己跳转
                 */
//                return super.shouldOverrideUrlLoading(view, url);

                if (url.endsWith("?prop=aaa")) {//符合这个规则
                    view.loadUrl("http://..."); //这就相当于自己处理了
                }
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override //api24 7.0
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                String method = request.getMethod();
                Map<String, String> requestHeaders = request.getRequestHeaders();
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                System.out.println(errorCode);
//                if (errorCode == 404) {
                    view.loadUrl("file:///android_asset/error_404.html");
//                }
            }

            @TargetApi(23)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @TargetApi(23)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }
        });

        /*
         WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等比如
            onCloseWindow   onCreateWindow  onJsAlert  onJsPrompt  onJsConfirm
            onProgressChanged   onReceivedIcon  onReceivedTitle
         */
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                mTvTitle.setText(title + " " + view.getUrl());
            }


        });


        //文件下载监听
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                System.out.println("url---->" + url);
                System.out.println("userAgent---->" + userAgent);
                System.out.println("contentDisposition---->" + contentDisposition);
                System.out.println("mimetype---->" + mimetype);
                System.out.println("contentLength---->" + contentLength);

                if (url.endsWith(".apk")) {
                    new ApkDownloadThread(url).start();
                }
            }
        });

        testCookie();

        testJs();
    }

    /*
    本地浏览器 或者 支持该Intent的组件 来加载url
     */
    private void openLocalBrowser() {
        Uri uri = Uri.parse("https://developer.android.google.cn");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    /*
    当不设置WebViewClient时，会打开系统的浏览器加载url
    只要设置了，哪怕不重写任何方法，也会在自身中加载
     */
    private void loadUrl(WebView webView) {
        String url = "http://www.baidu.com";
        webView.loadUrl(url);
    }

    private void testCookie() {
        new HttpCookieThread(mUrl, mHandler).start();
//        CookieManager.getInstance().removeAllCookie(); 清除所有cookie
    }

    private void testJs() {
        /*
        js调用注意点：
        @JavascriptInterface
        被js调用的方法不能被混淆
         */
        mWebView.loadUrl("file:///android_asset/js_test.html");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new User(this, "stone"), "user");
    }
    public void refresh(View view) {
        mWebView.reload();
    }

    public void back(View view) {
        finish();
    }

}

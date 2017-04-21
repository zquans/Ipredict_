package com.woyuce.activity.Controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import java.util.Set;

/**
 * Created by Administrator on 2016/9/22
 */
public class WebGetPushActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitle;
    private WebView web;
    private ImageView imgClose, imgBack;

    private String local_URL = "http://iyuce.com/";

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
        }
        return true;
    }

    private void goBack() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            WebGetPushActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            Set<String> keySet = bun.keySet();
            for (String key : keySet) {
                if (TextUtils.equals(key, "url")) {
                    String value = bun.getString(key);
                    local_URL = value;
                    LogUtil.e("value = " + value);
                    break;
                }
            }
        }
        //获取到参数后再初始化操作
        initEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initView();

        //另一种接收推送的方式
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(final Context context, UMessage msg) {
//                //TODO 接收到推送后自定义打开的界面或者其他
//                ToastUtil.showMessage(context, "收到通知：" + msg.custom + "||" + msg.extra + ",并执行相应操作");
//                LogUtil.e(msg.extra.toString());
//            }
//        };
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.web_title);
        web = (WebView) findViewById(R.id.web);
        imgClose = (ImageView) findViewById(R.id.img_close);
        imgBack = (ImageView) findViewById(R.id.img_back);

        imgClose.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

//    /**
//     * 将cookie同步到WebView,用于用户系统
//     *
//     * @param url    WebView要加载的url
//     * @param cookie 要同步的cookie
//     * @return true 同步cookie成功，false同步cookie失败
//     * @Author JPH
//     */
//    public static boolean syncCookie(String url, String cookie) {
//         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//         CookieSyncManager.createInstance(context);
//         }
//        CookieManager cookieManager = CookieManager.getInstance();
//        String oldcookie = cookieManager.getCookie(url);
//        LogUtil.e("oldcookie = " + oldcookie);
//        cookieManager.setCookie(url, cookie.substring(0, cookie.indexOf("1")));
//        //如果没有特殊需求，这里只需要将session, id以 "key=value" 形式作为cookie即可
//        String newCookie = cookieManager.getCookie(url);
//        LogUtil.e("newCookie = " + newCookie);
//        return TextUtils.isEmpty(newCookie) ? false : true;
//    }

    private void initEvent() {
        progressdialogshow(this);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        web.getSettings().setLoadWithOverviewMode(true);

        // H5处理localstrage
        web.getSettings().setDomStorageEnabled(true);
        // H5的缓存打开
        web.getSettings().setAppCacheEnabled(true);
        // 根据setAppCachePath(String appCachePath)提供的路径,在H5使用缓存过程中生成的缓存文件。
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        web.getSettings().setAppCachePath(appCachePath);
        // 设置缓冲大小8M
        web.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        web.getSettings().setAllowFileAccess(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //本地错误预留页面
                view.loadUrl("file:///android_asset/index.html");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressdialogcancel();
                LogUtil.e("onPageFinished");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.e("onPageStarted");
            }
        });
        web.loadUrl(local_URL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                goBack();
                break;
            case R.id.img_close:
                WebGetPushActivity.this.finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
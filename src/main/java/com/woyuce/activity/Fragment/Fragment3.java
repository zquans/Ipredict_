package com.woyuce.activity.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.woyuce.activity.Act.WebActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.JsInterface;
import com.woyuce.activity.Utils.PreferenceUtil;

/**
 * Created by Administrator on 2016/10/14.
 */
public class Fragment3 extends Fragment implements View.OnClickListener {

    private Button btn_apply;
    private ImageView imgLoading;
    private WebView web;

    private String URL_CAM = "http://www.iyuce.com/m/appjxy.html?v=" + System.currentTimeMillis();

    //JS交互
    private JsInterface jsInterface;

    @Override
    public void onStart() {
        super.onStart();
        // initWebView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_, container, false);

        initView(view);
        initWebView();
        return view;
    }

    private void initView(View view) {
        web = (WebView) view.findViewById(R.id.web_tab3);
        imgLoading = (ImageView) view.findViewById(R.id.img_tab3_loading);
        btn_apply = (Button) view.findViewById(R.id.btn_tab3_apply);

        btn_apply.setOnClickListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        web.loadUrl(URL_CAM);
        // 允许JS交互
        web.getSettings().setJavaScriptEnabled(true);
        // 实例化接口JsInterface
        jsInterface = new JsInterface(getActivity());
        // 设置JS的接口
        web.addJavascriptInterface(jsInterface, "woyuce");

        //设置浏览器标识
        String localVersion = PreferenceUtil.getSharePre(getActivity()).getString("localVersion", "2.8");
        web.getSettings().setUserAgentString(web.getSettings().getUserAgentString() + "; woyuce/" + localVersion);

        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        web.getSettings().setLoadWithOverviewMode(true);

		/* 设置缓存相关 */
        // web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // web.getSettings().setDomStorageEnabled(true);
        // web.getSettings().setDatabaseEnabled(true);
        // String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() +
        // "/webcachetab2";
        // LogUtil.e("tab3 cache", "cacheDirPath=" + cacheDirPath);
        // web.getSettings().setDatabasePath(cacheDirPath);
        // web.getSettings().setAppCachePath(cacheDirPath);
        // web.getSettings().setAppCacheEnabled(true);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("file:///android_asset/index.html");
//                view.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                web.setVisibility(View.VISIBLE);
                imgLoading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra("URL", "http://www.iyuce.com/m/appfbbsq");
        intent.putExtra("TITLE", "集训营申请");
        intent.putExtra("COLOR", "#f7941d");
        startActivity(intent);
    }
}

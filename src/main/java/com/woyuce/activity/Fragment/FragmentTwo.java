package com.woyuce.activity.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.woyuce.activity.R;

public class FragmentTwo extends Fragment {

    private WebView web;
    private ImageView imgLoading;
    private String URL = "http://bbs.iyuce.com/bar/bbspullrefresh";


    @Override
    public void onStart() {
        super.onStart();
        initEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2, container, false);

        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        web = (WebView) view.findViewById(R.id.web_tab2);
        imgLoading = (ImageView) view.findViewById(R.id.img_tab2_loading);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initEvent() {

        web.loadUrl(URL);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
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
//                view.loadUrl("file:///android_asset/index.html");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                web.setVisibility(View.VISIBLE);
                imgLoading.setVisibility(View.GONE);
            }
        });
    }
}
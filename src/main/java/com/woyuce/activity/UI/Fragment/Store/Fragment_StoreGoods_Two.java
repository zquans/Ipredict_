package com.woyuce.activity.UI.Fragment.Store;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.woyuce.activity.UI.Fragment.BaseFragment;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

public class Fragment_StoreGoods_Two extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_two, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        LogUtil.i("mImgList = " + getArguments().getString("mImgList"));
        WebView web = (WebView) view.findViewById(R.id.web_test);

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
        String appCachePath = getActivity().getApplicationContext().getCacheDir().getAbsolutePath();
        web.getSettings().setAppCachePath(appCachePath);
        // 设置缓冲大小8M
        web.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        web.getSettings().setAllowFileAccess(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("file:///android_asset/index.html");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        web.loadUrl(getArguments().getString("mImgList").toString().trim());
    }
}
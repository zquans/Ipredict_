package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

public class Fragment_StoreGoods_Two extends Fragment {

    private LinearLayout mLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_two, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_fragment_goods_two);

        //真实数据
        LogUtil.i("mImgList = " + getArguments().getString("mImgList"));
        //伪数据
        WebView mWeb = (WebView) view.findViewById(R.id.web_test);
        mWeb.loadUrl(getArguments().getString("mImgList"));
    }
}
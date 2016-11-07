package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StoreCarActivity extends BaseActivity {

    //TODO 此Activity主要作一些移除/修改ListView中Item的工作

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storecar);

        initView();
    }

    private void initView() {

        LogUtil.i("getAll = " + getIntent().getStringExtra("id")
                + getIntent().getStringExtra("name")
                + getIntent().getIntExtra("num", -1)
                + getIntent().getIntExtra("price", -1)
                + getIntent().getStringExtra("goodsid"));

//         intent.putExtra("id", PreferenceUtil.getSharePre(this).getString("userId", ""));
//        intent.putExtra("name", mList.get(0));
//        intent.putExtra("num", StoreNum);
//        intent.putExtra("price", 88);
//        intent.putExtra("goodsid", getIntent().getStringExtra("goods_id"));
//        startActivity(intent);
    }

    public void toPay(View view) {
        startActivity(new Intent(this,StorePayActivity.class));
    }
}

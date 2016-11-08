package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.woyuce.activity.Adapter.StoreCarAdapter;
import com.woyuce.activity.Bean.StoreMenu;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StoreCarActivity extends BaseActivity implements StoreCarAdapter.OnMyClickListener {

    //TODO 此Activity主要作一些移除/修改ListView中Item的工作

    private TextView mTxtTotalNum, mTxtTotalPrice, mTxtFinalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storecar);

        initView();
    }


    private ListView mListView;
    private ArrayList<StoreMenu> mList = new ArrayList<>();
    private StoreCarAdapter mAdapter;

    private void initView() {
        PreferenceUtil.save(this, getIntent().getStringExtra("name"),
                getIntent().getStringExtra("id") + ","
                        + getIntent().getStringExtra("name") + ","
                        + getIntent().getIntExtra("num", -1) + ","
                        + getIntent().getIntExtra("price", -1) + ","
                        + getIntent().getStringExtra("goodsid"));

        LogUtil.i("pre_util = " + PreferenceUtil.getSharePre(this).getString(getIntent().getStringExtra("name"), null));

        mListView = (ListView) findViewById(R.id.listview_activity_store_car);
        mTxtTotalNum = (TextView) findViewById(R.id.txt_storecar_total_num);
        mTxtTotalPrice = (TextView) findViewById(R.id.txt_storecar_total_price);
        mTxtFinalPrice = (TextView) findViewById(R.id.txt_storecar_final_price);

        //TODO 这个商品详情数组要怎么做
        StoreMenu storemenu;
        for (int i = 0; i < 5; i++) {
            storemenu = new StoreMenu();
            storemenu.setGoodsid(getIntent().getStringExtra("id"));
            storemenu.setName(getIntent().getStringExtra("name"));
            storemenu.setNum(getIntent().getIntExtra("num", -1) + "");
            storemenu.setPrice(getIntent().getIntExtra("price", -1) + "");
            storemenu.setGoodsid(getIntent().getStringExtra("goodsid"));
            mList.add(storemenu);
        }
        LogUtil.i("mList = " + mList);
        mAdapter = new StoreCarAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);

        mTxtTotalNum.setText(mList.size() + "件");
        mTxtTotalPrice.setText(Integer.parseInt(mList.get(0).getPrice()) * mList.size() + "元");
        mTxtFinalPrice.setText(Integer.parseInt(mList.get(0).getPrice()) * mList.size() + "元");
    }

    //TODO 先做出列表，Item中的加减事件再另做，(参数数组)String to Array,

    public void toPay(View view) {
        startActivity(new Intent(this, StorePayActivity.class));
    }

    /**
     * 帮助类
     * 动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //TODO 回调方法

    @Override
    public void OnMyAddClick() {
        ToastUtil.showMessage(this, "add");
    }

    @Override
    public void OnMyMinusClick() {
        ToastUtil.showMessage(this, "minus");
    }
}
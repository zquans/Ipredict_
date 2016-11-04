package com.woyuce.activity.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.woyuce.activity.Fragment.Fragment_StoreGoods_One;
import com.woyuce.activity.Fragment.Fragment_StoreGoods_Three;
import com.woyuce.activity.Fragment.Fragment_StoreGoods_Two;
import com.woyuce.activity.R;

public class StoreGoodsActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTxtTabOne, mTxtTabTwo, mTxtTabThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storegoods);

        initView();
    }

    private void initView() {
        mTxtTabOne = (TextView) findViewById(R.id.txt_storegoods_tab_one);
        mTxtTabTwo = (TextView) findViewById(R.id.txt_storegoods_tab_two);
        mTxtTabThree = (TextView) findViewById(R.id.txt_storegoods_tab_three);

        mTxtTabOne.setOnClickListener(this);
        mTxtTabTwo.setOnClickListener(this);
        mTxtTabThree.setOnClickListener(this);

        resetTxtTab();
        mTxtTabOne.setTextColor(Color.parseColor("#f7941d"));

        Bundle bundle = new Bundle();
        Fragment_StoreGoods_One mFrgOne = new Fragment_StoreGoods_One();
        bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
        bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
        mFrgOne.setArguments(bundle);
        //传递参数给Fragment，始终保持数据最新
        getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgOne).commit();
    }

    /**
     * 重设Tab样式
     */
    private void resetTxtTab() {
        mTxtTabOne.setTextColor(Color.parseColor("#a7a7a7"));
        mTxtTabTwo.setTextColor(Color.parseColor("#a7a7a7"));
        mTxtTabThree.setTextColor(Color.parseColor("#a7a7a7"));
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.txt_storegoods_tab_one:
                resetTxtTab();
                mTxtTabOne.setTextColor(Color.parseColor("#f7941d"));

                Fragment_StoreGoods_One mFrgOne = new Fragment_StoreGoods_One();
                bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                mFrgOne.setArguments(bundle);
                //传递参数给Fragment，始终保持数据最新
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgOne).commit();
                break;
            case R.id.txt_storegoods_tab_two:
                resetTxtTab();
                mTxtTabTwo.setTextColor(Color.parseColor("#f7941d"));

                Fragment_StoreGoods_Two mFrgTwo = new Fragment_StoreGoods_Two();
                //传递参数给Fragment，始终保持数据最新
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgTwo).commit();
                break;
            case R.id.txt_storegoods_tab_three:
                resetTxtTab();

                Fragment_StoreGoods_Three mFrgThree = new Fragment_StoreGoods_Three();
                bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                mFrgThree.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgThree).commit();
                mTxtTabThree.setTextColor(Color.parseColor("#f7941d"));
                break;
        }
    }
}
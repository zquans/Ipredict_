package com.woyuce.activity.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Fragment.Fragment_StoreGoods_One;
import com.woyuce.activity.Fragment.Fragment_StoreGoods_Three;
import com.woyuce.activity.Fragment.Fragment_StoreGoods_Two;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreGoodsActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTxtTabOne, mTxtTabTwo, mTxtTabThree;

    //存放数据
    private List<String> mList = new ArrayList<>();
    private String mImgList = null;//或者是个数组

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Bundle bundle = new Bundle();
                    Fragment_StoreGoods_One mFrgOne = new Fragment_StoreGoods_One();
                    bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                    bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                    mFrgOne.setArguments(bundle);
                    bundle.putStringArrayList("mList", (ArrayList<String>) mList);
                    //传递参数给Fragment，始终保持数据最新
                    getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgOne).commit();
                    break;
            }
        }
    };

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

        //请求数据
        requestData();

        resetTxtTab(mTxtTabOne);
        mTxtTabOne.setTextColor(Color.parseColor("#f7941d"));
    }

    private String URL = "http://api.iyuce.com/v1/store/goods";

    private void requestData() {
        URL = URL + "?goodsid=" + getIntent().getStringExtra("goods_id") + "&skuid="
                + getIntent().getStringExtra("goods_sku_id") + "&userid="
                + "1357775107029";
        StringRequest goodsDetialRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtil.i(s.toString());
                        try {
                            JSONObject obj;
                            JSONArray arr;
                            obj = new JSONObject(s);
                            if (obj.getString("code").equals("0")) {
                                obj = obj.getJSONObject("good");
                                //将商品名放到轮播图数组中一同传递
                                mList.add(obj.getString("goods_title"));
                                //给Tab的商品详情多图
                                mImgList = obj.getString("goods_desc");
                                //填充轮播图数据
                                arr = obj.getJSONArray("goods_albums");
                                for (int i = 0; i < arr.length(); i++) {
                                    obj = arr.getJSONObject(i);
                                    mList.add(obj.getString("original_img"));
                                }
                                //TODO 修改what为静态常量
                                Message msg = new Message();
                                msg.what = 0;
                                mHandler.sendMessage(msg);
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
        goodsDetialRequest.setTag("goodsDetialRequest");
        AppContext.getHttpQueue().add(goodsDetialRequest);
    }

    /**
     * 重设Tab样式
     */
    private void resetTxtTab(TextView txt) {
        mTxtTabOne.setTextColor(Color.parseColor("#a7a7a7"));
        mTxtTabTwo.setTextColor(Color.parseColor("#a7a7a7"));
        mTxtTabThree.setTextColor(Color.parseColor("#a7a7a7"));
        txt.setTextColor(Color.parseColor("#f7941d"));
    }

    //直接打开购物车Activity，通过SharePre去获取商品信息
    public void goToCar(View view) {
        Intent intent = new Intent(this, StoreCarActivity.class);
        startActivity(intent);
    }

    int StoreNum = 0;//购买的商品数量

    //参数加入SharePre,不打开购物车Activity
    public void putInCar(View view) {
        StoreNum = StoreNum + 1;
        //TODO 保存这些参数到sharePreference，给结账用（需要先调出数据查看，Preference是否冲突字段）
//        intent.putExtra("id", PreferenceUtil.getSharePre(this).getString("userId", ""));
//        intent.putExtra("name", mList.get(0));
//        intent.putExtra("num", 8);
//        intent.putExtra("price", 88);
//        intent.putExtra("goodsid", getIntent().getStringExtra("goods_id"));
    }

    //参数传递过去,打开购物车Activity
    public void buyNow(View view) {
        StoreNum = StoreNum + 1;

        Intent intent = new Intent(this, StoreCarActivity.class);
//                "id":list[i].id,
//                "name":list[i].name,
//                "num":list[i].num == null ? 0 : list[i].num,
//                "price":list[i].price,
//                "goodsid":list[i].goodsid

        intent.putExtra("id", PreferenceUtil.getSharePre(this).getString("userId", ""));
        intent.putExtra("name", mList.get(0));
        intent.putExtra("num", StoreNum);
        intent.putExtra("price", 88);
        intent.putExtra("goodsid", getIntent().getStringExtra("goods_id"));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.txt_storegoods_tab_one:
                resetTxtTab(mTxtTabOne);
                Fragment_StoreGoods_One mFrgOne = new Fragment_StoreGoods_One();
                bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                bundle.putStringArrayList("mList", (ArrayList<String>) mList);
                mFrgOne.setArguments(bundle);
                //传递参数给Fragment，始终保持数据最新
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgOne).commit();
                break;
            case R.id.txt_storegoods_tab_two:
                resetTxtTab(mTxtTabTwo);
                Fragment_StoreGoods_Two mFrgTwo = new Fragment_StoreGoods_Two();
                bundle.putString("mImgList", mImgList);
                mFrgTwo.setArguments(bundle);
                //传递参数给Fragment，始终保持数据最新
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgTwo).commit();
                break;
            case R.id.txt_storegoods_tab_three:
                resetTxtTab(mTxtTabThree);
                Fragment_StoreGoods_Three mFrgThree = new Fragment_StoreGoods_Three();
                bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                mFrgThree.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgThree).commit();
                break;
        }
    }
}
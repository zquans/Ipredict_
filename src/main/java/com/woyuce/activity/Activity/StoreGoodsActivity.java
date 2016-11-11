package com.woyuce.activity.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
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
    private Button mBtnGoToCar, mBtnPutIntoCar, mBtnBuyNow;

    //存放数据
    private List<String> mList = new ArrayList<>();
    private String mImgList = null;//或者是个数组

    private static final int OPEN_FRAGMENT = 0x001;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN_FRAGMENT:
                    Bundle bundle = new Bundle();
                    Fragment_StoreGoods_One mFrgOne = new Fragment_StoreGoods_One();
                    bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                    bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                    bundle.putString("goods_title", getIntent().getStringExtra("goods_title"));
                    bundle.putString("sales_price", getIntent().getStringExtra("sales_price"));
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
        mBtnGoToCar = (Button) findViewById(R.id.btn_activity_storegoods_tocar);
        mBtnPutIntoCar = (Button) findViewById(R.id.btn_activity_storegoods_putincar);
        mBtnBuyNow = (Button) findViewById(R.id.btn_activity_storegoods_buynow);
        mBtnGoToCar.setOnClickListener(this);
        mBtnPutIntoCar.setOnClickListener(this);
        mBtnBuyNow.setOnClickListener(this);
        mTxtTabOne.setOnClickListener(this);
        mTxtTabTwo.setOnClickListener(this);
        mTxtTabThree.setOnClickListener(this);

        //请求数据
        requestData();
        //重设Tab的样式
        resetTxtTab(mTxtTabOne);
        mTxtTabOne.setTextColor(Color.parseColor("#f7941d"));
    }

    /**
     * 获取的是轮播图、商品详情的数据
     */
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
                                //给Tab的商品详情多图
                                mImgList = obj.getString("goods_desc");
                                //填充轮播图数据
                                arr = obj.getJSONArray("goods_albums");
                                for (int i = 0; i < arr.length(); i++) {
                                    obj = arr.getJSONObject(i);
                                    mList.add(obj.getString("original_img"));
                                }
                                Message msg = new Message();
                                msg.what = OPEN_FRAGMENT;
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

    /**
     * 开数据库建一张表
     */
    private void saveStoreInfo(String id, String goodsid, String name, String num, String price) {
        SQLiteDatabase mDatabase = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
        mDatabase.execSQL(
                "create table if not exists storetb(_id integer primary key autoincrement," +
                        "id text not null,goodsid text not null,name text not null," +
                        "num text not null,price text not null)");
        ContentValues mValues = new ContentValues();
        mValues.put("id", id);
        mValues.put("goodsid", goodsid);
        mValues.put("name", name);
        mValues.put("num", num);
        mValues.put("price", price);
        mDatabase.insert("storetb", null, mValues);
        mValues.clear();
        mDatabase.close();
        //权宜之计，做个标识给FavoriteActivity用
        PreferenceUtil.save(this, "storetb_is_exist", "yes");
    }

    @Override
    public void onClick(View v) {//前三个case是顶部导航栏，后三个是底部导航栏
        //这些参数是传递给底部操作栏的，跟购物车相关
        Bundle bundle = new Bundle();
        String local_id = PreferenceUtil.getSharePre(this).getString("userId", "");
        String local_goodsid = getIntent().getStringExtra("goods_id");
        String local_name = getIntent().getStringExtra("goods_title");
        String local_num = String.valueOf(1);
        String local_price = getIntent().getStringExtra("sales_price");
        switch (v.getId()) {
            case R.id.txt_storegoods_tab_one:
                resetTxtTab(mTxtTabOne);
                Fragment_StoreGoods_One mFrgOne = new Fragment_StoreGoods_One();
                bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                bundle.putString("goods_title", getIntent().getStringExtra("goods_title"));
                bundle.putString("sales_price", getIntent().getStringExtra("sales_price"));
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
            case R.id.btn_activity_storegoods_buynow:
                //保存进数据库
                saveStoreInfo(local_id, local_goodsid, local_name, local_num, local_price);
                startActivity(new Intent(this, StoreCarActivity.class));
                break;
            case R.id.btn_activity_storegoods_putincar:
                //保存进数据库
                saveStoreInfo(local_id, local_goodsid, local_name, local_num, local_price);
                break;
            case R.id.btn_activity_storegoods_tocar:
                startActivity(new Intent(this, StoreCarActivity.class));
                break;
        }
    }
}
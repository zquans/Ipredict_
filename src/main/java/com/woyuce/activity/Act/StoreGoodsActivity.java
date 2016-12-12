package com.woyuce.activity.Act;

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
import com.woyuce.activity.Fragment.Fragment_StoreGoods_One_;
import com.woyuce.activity.Fragment.Fragment_StoreGoods_Three;
import com.woyuce.activity.Fragment.Fragment_StoreGoods_Two;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

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

    Fragment_StoreGoods_One_ mFrgOne;

    private static final int OPEN_FRAGMENT = 0x001;

    private String local_goods_title;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN_FRAGMENT:
                    Bundle bundle = new Bundle();
                    mFrgOne = new Fragment_StoreGoods_One_();
                    bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                    bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                    bundle.putString("goods_title", getIntent().getStringExtra("goods_title"));
                    bundle.putString("sales_price", getIntent().getStringExtra("sales_price"));
                    bundle.putString("total_sales_volume", total_sales_volume);
                    bundle.putString("total_good_volume", total_good_volume);
                    bundle.putString("total_show_order_volume", total_show_order_volume);
                    mFrgOne.setArguments(bundle);
                    bundle.putStringArrayList("mList", (ArrayList<String>) mList);
                    //传递参数给Fragment，始终保持数据最新
                    getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgOne).disallowAddToBackStack().commit();
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
        local_goods_title = getIntent().getStringExtra("goods_title");
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
    private String total_sales_volume, total_good_volume, total_bad_volume, total_medium_volume, total_show_order_volume;
    private String URL = "http://api.iyuce.com/v1/store/goods";

    private void requestData() {
        StringRequest goodsDetialRequest = new StringRequest(Request.Method.GET,
                URL + "?goodsid=" + getIntent().getStringExtra("goods_id") + "&skuid="
                        + getIntent().getStringExtra("goods_sku_id") + "&userid="
                        + PreferenceUtil.getSharePre(this).getString("userId", ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject obj;
                            JSONArray arr;
                            obj = new JSONObject(s);
                            if (obj.getString("code").equals("0")) {
                                //TODO 保存这个money，到购物车结算时可以抵充金币
                                String store_user_money = obj.getString("user_money");
                                LogUtil.i("user_money = " + store_user_money);
                                PreferenceUtil.save(StoreGoodsActivity.this, "store_user_money", store_user_money);
                                obj = obj.getJSONObject("good");
                                //给Tab2的商品详情多图
                                mImgList = obj.getString("goods_desc");
                                total_sales_volume = obj.getString("total_sales_volume");
                                total_good_volume = obj.getString("total_good_volume");
                                total_bad_volume = obj.getString("total_bad_volume");
                                total_medium_volume = obj.getString("total_medium_volume");
                                total_show_order_volume = obj.getString("total_show_order_volume");
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
                                ToastUtil.showMessage(getApplicationContext(), "获取商品信息失败");
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
    private void saveStoreInfo(String id, String goodsid, String goodsskuid, String name, String specname, String num, String price) {
        SQLiteDatabase mDatabase = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
        mDatabase.execSQL(
                "create table if not exists storetb(_id integer primary key autoincrement," +
                        "id text not null,goodsskuid text not null,name text not null," +
                        "specname text not null,num text not null,price text not null)");
        ContentValues mValues = new ContentValues();
        mValues.put("id", id);
        mValues.put("goodsskuid", goodsskuid);
        mValues.put("name", name);
        mValues.put("specname", specname);
        mValues.put("num", num);
        mValues.put("price", price);
        mDatabase.insert("storetb", null, mValues);
        mValues.clear();
        mDatabase.close();
        //权宜之计，做个标识给FavoriteActivity用
        PreferenceUtil.save(this, "storetb_is_exist", "yes");
    }

    public void back(View view) {
        StoreGoodsActivity.this.finish();
    }

    @Override
    public void onClick(View v) {//前三个case是顶部导航栏，后三个是底部导航栏
        if (mFrgOne == null) {
            return;
        }
        //这些参数是传递给底部操作栏的，跟购物车相关
        Bundle bundle = new Bundle();
        String local_id = PreferenceUtil.getSharePre(this).getString("userId", "");
        //这些应该从Fragment中获取
        String local_goodsid = mFrgOne.returenGoodsId();
        String local_goods_sku_id = mFrgOne.returenGoodsSkuId();
        String local_name = local_goods_title;
        String local_specname = mFrgOne.returenGoodsSpecName();
        String local_price = mFrgOne.returenGoodsPrice();
        String local_num = "1";
        switch (v.getId()) {
            case R.id.txt_storegoods_tab_one:
                resetTxtTab(mTxtTabOne);
                Fragment_StoreGoods_One_ mFrgOne = new Fragment_StoreGoods_One_();
                bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                bundle.putString("goods_title", getIntent().getStringExtra("goods_title"));
                bundle.putString("sales_price", getIntent().getStringExtra("sales_price"));
                bundle.putString("total_sales_volume", total_sales_volume);
                bundle.putString("total_good_volume", total_good_volume);
                bundle.putString("total_show_order_volume", total_show_order_volume);
                bundle.putStringArrayList("mList", (ArrayList<String>) mList);
                mFrgOne.setArguments(bundle);
                //传递参数给Fragment，始终保持数据最新
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgOne).disallowAddToBackStack().commit();
                break;
            case R.id.txt_storegoods_tab_two:
                resetTxtTab(mTxtTabTwo);
                Fragment_StoreGoods_Two mFrgTwo = new Fragment_StoreGoods_Two();
                bundle.putString("mImgList", mImgList);
                mFrgTwo.setArguments(bundle);
                //传递参数给Fragment，始终保持数据最新
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgTwo).disallowAddToBackStack().commit();
                break;
            case R.id.txt_storegoods_tab_three:
                resetTxtTab(mTxtTabThree);
                Fragment_StoreGoods_Three mFrgThree = new Fragment_StoreGoods_Three();
                bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                bundle.putString("total_sales_volume", total_sales_volume);
                bundle.putString("total_good_volume", total_good_volume);
                bundle.putString("total_bad_volume", total_bad_volume);
                bundle.putString("total_medium_volume", total_medium_volume);
                bundle.putString("total_show_order_volume", total_show_order_volume);
                mFrgThree.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgThree).disallowAddToBackStack().commit();
                break;
            case R.id.btn_activity_storegoods_buynow:
                //保存进数据库
                saveStoreInfo(local_id, local_goodsid, local_goods_sku_id, local_name, local_specname, local_num, local_price);
                startActivity(new Intent(this, StoreCarActivity.class));
                break;
            case R.id.btn_activity_storegoods_putincar:
                //保存进数据库
                saveStoreInfo(local_id, local_goodsid, local_goods_sku_id, local_name, local_specname, local_num, local_price);
                ToastUtil.showMessage(this, "您的商品放入购物车啦!");
                break;
            case R.id.btn_activity_storegoods_tocar:
                startActivity(new Intent(this, StoreCarActivity.class));
                break;
        }
    }
}
package com.woyuce.activity.Act.Store;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Act.Common.CustomServiceActivity;
import com.woyuce.activity.Act.MainActivity;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Fragment.Store.Fragment_StoreGoods_One_;
import com.woyuce.activity.Fragment.Store.Fragment_StoreGoods_Three;
import com.woyuce.activity.Fragment.Store.Fragment_StoreGoods_Two;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreGoodsActivity extends FragmentActivity implements View.OnClickListener {

    private TabLayout mTab;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private ArrayList<Fragment> mFragmentList;
    private ArrayList<String> mTabList = new ArrayList<>();

    private TextView mTxtToCustom;
    private Button mBtnGoToCar, mBtnPutIntoCar, mBtnBuyNow;

    //存放数据
    private List<String> mList = new ArrayList<>();
    private String mImgList = null;//或者是个数组

    private Fragment_StoreGoods_One_ mFrgOne;

    private static final int GET_DATA_DONE = 0;

    private String local_goods_title;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA_DONE:
                    Bundle bundle = new Bundle();
                    //Tab1需要的
                    bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
                    bundle.putString("goods_sku_id", getIntent().getStringExtra("goods_sku_id"));
                    bundle.putString("goods_title", getIntent().getStringExtra("goods_title"));
                    bundle.putString("sales_price", getIntent().getStringExtra("sales_price"));
                    bundle.putString("total_sales_volume", total_sales_volume);
                    bundle.putString("total_good_volume", total_good_volume);
                    bundle.putString("total_show_order_volume", total_show_order_volume);
                    bundle.putStringArrayList("mList", (ArrayList<String>) mList);
                    //Tab2需要的
                    bundle.putString("mImgList", mImgList);
                    //Tab3需要的
//                    bundle.putString("goods_id", getIntent().getStringExtra("goods_id"));
//                    bundle.putString("total_sales_volume", total_sales_volume);
//                    bundle.putString("total_good_volume", total_good_volume);
//                    bundle.putString("total_show_order_volume", total_show_order_volume);
                    bundle.putString("total_bad_volume", total_bad_volume);
                    bundle.putString("total_medium_volume", total_medium_volume);

                    //FragmentOne不做局部变量的原因是需要给底部栏操作相应参数
                    mFrgOne = new Fragment_StoreGoods_One_();
                    Fragment_StoreGoods_Two mFrgTwo = new Fragment_StoreGoods_Two();
                    Fragment_StoreGoods_Three mFrgThree = new Fragment_StoreGoods_Three();
                    mFrgOne.setArguments(bundle);
                    mFrgTwo.setArguments(bundle);
                    mFrgThree.setArguments(bundle);

                    //商品的标题
                    mTabList.add("商品");
                    mTabList.add("详情");
                    mTabList.add("晒单评价");
                    mTab.setTabMode(TabLayout.MODE_FIXED);
                    mTab.setupWithViewPager(mViewPager);

                    mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                        @Override

                        public Fragment getItem(int position) {
                            return mFragmentList.get(position);
                        }

                        @Override
                        public int getCount() {
                            return mFragmentList.size();
                        }

                        @Override
                        public CharSequence getPageTitle(int position) {
                            return mTabList.get(position);
                        }
                    };
                    mFragmentList = new ArrayList<>();
                    mFragmentList.add(mFrgOne);
                    mFragmentList.add(mFrgTwo);
                    mFragmentList.add(mFrgThree);
                    mViewPager.setAdapter(mAdapter);
                    mViewPager.setOffscreenPageLimit(2);
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

        mTab = (TabLayout) findViewById(R.id.tab_activity_storegoods_fragment);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_storegoods_fragment);
        mBtnGoToCar = (Button) findViewById(R.id.btn_activity_storegoods_tocar);
        mBtnPutIntoCar = (Button) findViewById(R.id.btn_activity_storegoods_putincar);
        mBtnBuyNow = (Button) findViewById(R.id.btn_activity_storegoods_buynow);
        mTxtToCustom = (TextView) findViewById(R.id.txt_store_to_custom);
        mTxtToCustom.setOnClickListener(this);
        mBtnGoToCar.setOnClickListener(this);
        mBtnPutIntoCar.setOnClickListener(this);
        mBtnBuyNow.setOnClickListener(this);

        //请求数据
        requestData();
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
                                msg.what = GET_DATA_DONE;
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
        doBack();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doBack();
    }

    private void doBack() {
        StoreGoodsActivity.this.finish();
        if (TextUtils.isEmpty(getIntent().getStringExtra("can_go_store_back")))
            startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View v) {//前三个case是顶部导航栏，后三个是底部导航栏
        if (mFrgOne == null) {
            return;
        }
        //这些参数是传递给底部操作栏的，跟购物车相关
        String local_id = PreferenceUtil.getSharePre(this).getString("userId", "");
        String local_goodsid = mFrgOne.returenGoodsId();
        String local_goods_sku_id = mFrgOne.returenGoodsSkuId();
        String local_name = local_goods_title;
        String local_specname = mFrgOne.returenGoodsSpecName();
        String local_price = mFrgOne.returenGoodsPrice();
        String local_num = "1";
        switch (v.getId()) {
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
                if (!PreferenceUtil.getSharePre(this).getString("storetb_is_exist", "no").equals("yes")) {
                    ToastUtil.showMessage(this, "您的购物车空空哒，快去添加商品吧！");
                    return;
                }
                startActivity(new Intent(this, StoreCarActivity.class));
                break;
            case R.id.txt_store_to_custom:
                startActivity(new Intent(this, CustomServiceActivity.class));
                break;
        }
    }
}
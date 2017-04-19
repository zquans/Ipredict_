package com.woyuce.activity.Controller.Store;

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
import android.widget.ImageView;

import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.Controller.Mine.CustomServiceActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.DbUtil;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
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

    private ImageView mImgToCustom;
    private Button mBtnGoToCar, mBtnPutIntoCar, mBtnBuyNow;

    //存放数据
    private List<String> mList = new ArrayList<>();
    private String mImgList = null;//或者是个数组

    private FragmentStoreGoodsOne_ mFrgOne;

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
                    mFrgOne = new FragmentStoreGoodsOne_();
                    FragmentStoreGoodsTwo mFrgTwo = new FragmentStoreGoodsTwo();
                    FragmentStoreGoodsThree mFrgThree = new FragmentStoreGoodsThree();
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
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.removeTag(Constants.ACTIVITY_STORE_GOODS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_goods);

        initView();
    }

    private void initView() {
        local_goods_title = getIntent().getStringExtra("goods_title");

        mTab = (TabLayout) findViewById(R.id.tab_activity_storegoods_fragment);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_storegoods_fragment);
        mBtnGoToCar = (Button) findViewById(R.id.btn_activity_storegoods_tocar);
        mBtnPutIntoCar = (Button) findViewById(R.id.btn_activity_storegoods_putincar);
        mBtnBuyNow = (Button) findViewById(R.id.btn_activity_storegoods_buynow);
        mImgToCustom = (ImageView) findViewById(R.id.img_store_to_custom);
        mImgToCustom.setOnClickListener(this);
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
//    private String URL = "http://api.iyuce.com/v1/store/goods";

    private void requestData() {
        HttpUtil.get(Constants.URL_GET_STORE_GOODS + "?goodsid=" + getIntent().getStringExtra("goods_id") + "&skuid="
                        + getIntent().getStringExtra("goods_sku_id") + "&userid="
                        + PreferenceUtil.getSharePre(this).getString("userId", ""),
                Constants.ACTIVITY_STORE_GOODS, new RequestInterface() {
                    @Override
                    public void doSuccess(String result) {
                        try {
                            JSONObject obj;
                            JSONArray arr;
                            obj = new JSONObject(result);
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
                });
    }

    /**
     * 开数据库建一张表
     */
    private void saveIntoDatabase(String goodsid, String goodsskuid, String name, String specname, String price) {
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, Constants.DATABASE_IYUCE).getWritableDatabase();
        mDatabase.execSQL("create table if not exists "
                + Constants.TABLE_CART + "("
                + Constants.COLUMN_GOODS_SPEC_ID + " integer primary key ,"
                + Constants.COLUMN_GOODS_ID + " text,"
                + Constants.COLUMN_NAME + " text,"
                + Constants.COLUMN_SPEC_NAME + " text,"
                + Constants.COLUMN_COUNT + " integer,"
                + Constants.COLUMN_PRICE + " text)");

        //String sql_replace = "REPLACE INTO cart_table(Goods_spec_id,Goods_id,Name,Spec_name,Price,Count) VALUES(1,\"47\",\"454aaa\",\"specnameeeee\",\"num111\",ifnull((select Count from cart_table where Goods_spec_id= 1 ),0)+1)\n";
        String sql_replace = "REPLACE INTO " + Constants.TABLE_CART + "("
                + Constants.COLUMN_GOODS_SPEC_ID + ","
                + Constants.COLUMN_GOODS_ID + ","
                + Constants.COLUMN_NAME + ","
                + Constants.COLUMN_SPEC_NAME + ","
                + Constants.COLUMN_PRICE + ","
                + Constants.COLUMN_COUNT + ") VALUES("
                + goodsskuid + ",\""
                + goodsid + "\",\""
                + name + "\",\""
                + specname + "\",\""
                + price + "\",ifnull(("
                + "select " + Constants.COLUMN_COUNT + " from " + Constants.TABLE_CART
                + " where " + Constants.COLUMN_GOODS_SPEC_ID + "= " + goodsskuid + " ),0)+1)\n";
        mDatabase.execSQL(sql_replace);
        mDatabase.close();
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
        String local_goods_sku_id = mFrgOne.returenGoodsSkuId();
        String local_goodsid = mFrgOne.returenGoodsId();
        String local_name = local_goods_title;
        String local_specname = mFrgOne.returenGoodsSpecName();
        String local_price = mFrgOne.returenGoodsPrice();
        switch (v.getId()) {
            case R.id.btn_activity_storegoods_buynow:
                saveIntoDatabase(local_goodsid, local_goods_sku_id, local_name, local_specname, local_price);
                startActivity(new Intent(this, StoreCartActivity.class));
                break;
            case R.id.btn_activity_storegoods_putincar:
                saveIntoDatabase(local_goodsid, local_goods_sku_id, local_name, local_specname, local_price);
                ToastUtil.showMessage(this, "您的商品放入购物车啦!");
                break;
            case R.id.btn_activity_storegoods_tocar:
                SQLiteDatabase mDatabase = DbUtil.getHelper(this, Constants.DATABASE_IYUCE).getWritableDatabase();
                String isNone = DbUtil.queryToExist(mDatabase, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_CART);
                mDatabase.close();
                if (!isNone.equals(Constants.NONE)) {
                    startActivity(new Intent(this, StoreCartActivity.class));
                    break;
                }
                ToastUtil.showMessage(this, "您的购物车空空哒，快去添加商品吧！");
                break;
            case R.id.img_store_to_custom:
                startActivity(new Intent(this, CustomServiceActivity.class));
                break;
        }
    }
}
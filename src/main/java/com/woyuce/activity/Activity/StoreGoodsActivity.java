package com.woyuce.activity.Activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Fragment.Fragment_Store_Three;
import com.woyuce.activity.Fragment.Fragment_Store_Two;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreGoodsActivity extends BaseActivity implements View.OnClickListener {

    private ViewFlipper mFlipper;
    private FrameLayout mFramelayout;
    private TextView mTxtGoodsTitle, mTxtTotalSale, mTxtTotalGood, mTxtShowOrder,
            mTxtTabOne, mTxtTabTwo, mTxtTabThree;


    //存放数据
    private List<String> mList = new ArrayList<>();

    private String URL = "http://api.iyuce.com/v1/store/goods";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                            .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
                            .bitmapConfig(Bitmap.Config.RGB_565).build();

                    for (int i = 0; i < mList.size(); i++) {
                        ImageView img = new ImageView(StoreGoodsActivity.this);
                        ImageLoader.getInstance().displayImage(mList.get(i), img, options);
                        mFlipper.addView(img);
                    }
                    mFlipper.setInAnimation(StoreGoodsActivity.this, R.anim.left_in);
                    mFlipper.setOutAnimation(StoreGoodsActivity.this, R.anim.left_out);
                    mFlipper.startFlipping();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storegoods);

        initView();
        requestData();
    }

    private void initView() {
        mFlipper = (ViewFlipper) findViewById(R.id.viewflip_activity_storegoods);
        mFramelayout = (FrameLayout) findViewById(R.id.frame_activity_storegoods_fragment);

        mTxtGoodsTitle = (TextView) findViewById(R.id.txt_activity_storegoods_goodstitle);
        mTxtTotalSale = (TextView) findViewById(R.id.txt_activity_storegoods_total_sales_volume);
        mTxtTotalGood = (TextView) findViewById(R.id.txt_activity_storegoods_total_good_volume);
        mTxtShowOrder = (TextView) findViewById(R.id.txt_activity_storegoods_total_show_order_volume);

        mTxtTabOne = (TextView) findViewById(R.id.txt_storegoods_tab_one);
        mTxtTabTwo = (TextView) findViewById(R.id.txt_storegoods_tab_two);
        mTxtTabThree = (TextView) findViewById(R.id.txt_storegoods_tab_three);

        mTxtTabOne.setOnClickListener(this);
        mTxtTabTwo.setOnClickListener(this);
        mTxtTabThree.setOnClickListener(this);
    }

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
                                mTxtGoodsTitle.setText(obj.getString("goods_title"));
                                mTxtTotalSale.setText("销量：" + obj.getString("total_sales_volume"));
                                mTxtTotalGood.setText("好评：" + obj.getString("total_good_volume"));
                                mTxtShowOrder.setText("晒单：" + obj.getString("total_show_order_volume"));
                                //填充轮播图数据
                                arr = obj.getJSONArray("goods_albums");
                                for (int i = 0; i < arr.length(); i++) {
                                    obj = arr.getJSONObject(i);
                                    mList.add(obj.getString("original_img"));
                                }
                                Message msg = new Message();
                                msg.what = 1;
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
    private void resetTxtTab() {
        mTxtTabOne.setTextColor(Color.parseColor("#a7a7a7"));
        mTxtTabTwo.setTextColor(Color.parseColor("#a7a7a7"));
        mTxtTabThree.setTextColor(Color.parseColor("#a7a7a7"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_storegoods_tab_one:
                resetTxtTab();
                mTxtTabOne.setTextColor(Color.parseColor("#f7941d"));
                break;
            case R.id.txt_storegoods_tab_two:
                resetTxtTab();
                mTxtTabTwo.setTextColor(Color.parseColor("#f7941d"));

                Fragment_Store_Two mFrgTwo = new Fragment_Store_Two();
                //传递参数给Fragment，始终保持数据最新
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgTwo).commit();
                break;
            case R.id.txt_storegoods_tab_three:
                resetTxtTab();

                Fragment_Store_Three mFrgThree = new Fragment_Store_Three();
                getFragmentManager().beginTransaction().replace(R.id.frame_activity_storegoods_fragment, mFrgThree).commit();
                mTxtTabThree.setTextColor(Color.parseColor("#f7941d"));
                break;
        }
    }


}
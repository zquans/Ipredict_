package com.woyuce.activity.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.StoreHomeAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreBean;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_StoreHome_Tab1 extends Fragment {

    private ViewFlipper mViewFlipper;

    private RecyclerView mRecycler;
    private RecyclerView.Adapter mAdapter;
    private List<String> mData = new ArrayList<>();
    private List<StoreBean> mList = new ArrayList<>();

    private String URL = "http://api.iyuce.com/v1/store/homegoodslist";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                            .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
                            .bitmapConfig(Bitmap.Config.RGB_565).build();

                    for (int i = 0; i < mData.size(); i++) {
                        ImageView mImg = new ImageView(getActivity());
                        ImageLoader.getInstance().displayImage(mData.get(i), mImg, options);
                        mViewFlipper.addView(mImg);
                    }
                    LogUtil.i("mData = " + mData.toString());
                    mViewFlipper.setInAnimation(getActivity(), R.anim.left_in);
                    mViewFlipper.setOutAnimation(getActivity(), R.anim.left_out);
                    mViewFlipper.startFlipping();
                    break;
                case 2:
                    mAdapter = new StoreHomeAdapter(getActivity(), mList);
                    mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecycler.setAdapter(mAdapter);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_tab_1, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        mViewFlipper = (ViewFlipper) view.findViewById(R.id.viewflip_fragment_store_tab1);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler_fragment_store_tab1);

        requestData();
    }

    private void requestData() {
        StringRequest goodsrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj;
                    //轮播图数据
                    JSONArray arr_menu;
                    //商品数据
                    JSONArray arr_data;
                    obj = new JSONObject(s);
                    arr_menu = obj.getJSONArray("menudata");
                    arr_data = obj.getJSONArray("data");
                    for (int i = 0; i < arr_menu.length(); i++) {
                        obj = arr_menu.getJSONObject(i);
                        mData.add(obj.getString("icon_mobile_url"));
                    }
                    Message msg1 = new Message();
                    msg1.what = 1;
                    msg1.obj = mData;
                    mHandler.sendMessage(msg1);

                    StoreBean store;
                    JSONArray arr_goods_result;
                    JSONObject obj_goods;
                    for (int i = 0; i < arr_data.length(); i++) {
                        store = new StoreBean();
                        obj = arr_data.getJSONObject(i);
                        //"goods_result"
                        arr_goods_result = obj.getJSONArray("goods_result");
                        List<StoreGoods> goodsList = new ArrayList<>();
                        StoreGoods goods;
                        for (int j = 0; j < arr_goods_result.length(); j++) {
                            goods = new StoreGoods();
                            obj_goods = arr_goods_result.getJSONObject(j);
                            goods.setSales_price(obj_goods.getString("sales_price"));
                            goods.setThumb_img(obj_goods.getString("original_img"));
                            goods.setGoods_id(obj_goods.getString("goods_id"));
                            goods.setGoods_sku_id(obj_goods.getString("goods_sku_id"));
                            goodsList.add(goods);
                        }
                        store.setGoods_result((ArrayList<StoreGoods>) goodsList);
                        // "menu"
                        obj = obj.getJSONObject("menu");
                        store.setTitle(obj.getString("title"));
//                        LogUtil.i("obj.getString(\"menu\") = " + obj.getString("title"));
                        mList.add(store);
                    }
                    Message msg2 = new Message();
                    msg2.what = 2;
                    msg2.obj = mList;
                    mHandler.sendMessage(msg2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("volleyError = " + volleyError.toString());
            }
        });
        goodsrequest.setTag("goodsrequest");
        AppContext.getHttpQueue().add(goodsrequest);
    }
}
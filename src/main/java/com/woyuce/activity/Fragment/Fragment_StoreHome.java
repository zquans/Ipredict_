package com.woyuce.activity.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Act.CustomServiceActivity;
import com.woyuce.activity.Act.StoreCarActivity;
import com.woyuce.activity.Act.StoreGoodsActivity;
import com.woyuce.activity.Adapter.StoreHomeAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreBean;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_StoreHome extends Fragment implements View.OnClickListener {

    private ImageButton mBtnToCustom, mBtnToStoreCar;
    private ViewFlipper mViewFlipper;

    private RecyclerView mRecycler;
    private RecyclerView.Adapter mAdapter;
    private List<StoreBean> mImgData = new ArrayList<>();
    private List<StoreBean> mList = new ArrayList<>();

    private String URL = "http://api.iyuce.com/v1/store/homegoodslist";

    private static final int FLAG_VIEWFLIPPER = 1;
    private static final int FLAG_RECYCLERVIEW = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FLAG_VIEWFLIPPER:
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error_horizon)
                            .showImageOnFail(R.mipmap.img_error_horizon).cacheInMemory(true).cacheOnDisk(true)
                            .bitmapConfig(Bitmap.Config.RGB_565).build();

                    for (int i = 0; i < mImgData.size(); i++) {
                        ImageView mImg = new ImageView(getActivity());
                        mImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(mImgData.get(i).getIcon_mobile_url(), mImg, options);
                        final int finalI = i;
                        mImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtil.i("mImgData.get(finalI).getGoods_id() = " + mImgData.get(finalI).getGoods_id());
                                if (!TextUtils.isEmpty(mImgData.get(finalI).getGoods_id())) {
                                    Intent intent = new Intent(getActivity(), StoreGoodsActivity.class);
                                    intent.putExtra("goods_id", mImgData.get(finalI).getGoods_id());
                                    intent.putExtra("goods_sku_id", mImgData.get(finalI).getGoods_sku_id());
                                    intent.putExtra("goods_title", mImgData.get(finalI).getGoods_title());
                                    intent.putExtra("sales_price", mImgData.get(finalI).getSales_price());
                                    getActivity().startActivity(intent);
                                }
                            }
                        });
                        mViewFlipper.addView(mImg);
                    }
                    LogUtil.i("mData = " + mImgData.toString());
                    mViewFlipper.setInAnimation(getActivity(), R.anim.left_in);
                    mViewFlipper.setOutAnimation(getActivity(), R.anim.left_out);
                    mViewFlipper.startFlipping();
                    break;
                case FLAG_RECYCLERVIEW:
                    mAdapter = new StoreHomeAdapter(getActivity(), mList);
                    mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecycler.setAdapter(mAdapter);
//                    FullLinearLayoutManager linearLayoutManager = new FullLinearLayoutManager(getActivity());
//                    mRecycler.setNestedScrollingEnabled(false);
//                    //设置布局管理器
//                    mRecycler.setLayoutManager(linearLayoutManager);
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
        mBtnToCustom = (ImageButton) view.findViewById(R.id.imgbtn_store_toCustom);
        mBtnToStoreCar = (ImageButton) view.findViewById(R.id.imgbtn_store_toStoreCar);
        mBtnToCustom.setOnClickListener(this);
        mBtnToStoreCar.setOnClickListener(this);

        mViewFlipper = (ViewFlipper) view.findViewById(R.id.viewflip_fragment_store_tab1);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler_fragment_store_tab1);
        requestData();
    }

    private void requestData() {
        StringRequest goodsrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj, obj_menu;
                    //轮播图数据
                    JSONArray arr_menu, arr_menu_result;
                    //首页商品数据
                    JSONArray arr_data;
                    obj = new JSONObject(s);
                    arr_menu = obj.getJSONArray("menudata");
                    arr_data = obj.getJSONArray("data");
                    StoreBean storeBean;
                    //轮播图Bean
                    for (int i = 0; i < arr_menu.length(); i++) {
                        storeBean = new StoreBean();
                        obj = arr_menu.getJSONObject(i);
                        obj_menu = obj.getJSONObject("menu");
                        storeBean.setIcon_mobile_url(obj_menu.getString("icon_mobile_url"));
                        arr_menu_result = obj.getJSONArray("goods_result");
                        if (arr_menu_result.length() > 0) {
                            obj = arr_menu_result.getJSONObject(0);
                            storeBean.setGoods_id(obj.getString("goods_id"));
                            storeBean.setGoods_sku_id(obj.getString("goods_sku_id"));
                            storeBean.setGoods_title(obj.getString("goods_title"));
                            storeBean.setSales_price(obj.getString("sales_price"));
                        }
                        mImgData.add(storeBean);
                    }
                    Message msg1 = new Message();
                    msg1.what = FLAG_VIEWFLIPPER;
                    msg1.obj = mImgData;
                    mHandler.sendMessage(msg1);
                    // 首页商品数据Bean
                    //TODO 应该是这里的obj和上面的obj冲突了
                    StoreBean store;
                    JSONArray arr_goods_result;
                    JSONObject obj_goods;
                    for (int i = 0; i < arr_data.length(); i++) {
                        store = new StoreBean();
                        obj = arr_data.getJSONObject(i);
                        //"goods_result"
                        arr_goods_result = obj.getJSONArray("goods_result");
                        ArrayList<StoreGoods> goodsList = new ArrayList<>();
                        StoreGoods goods;
                        for (int j = 0; j < arr_goods_result.length(); j++) {
                            goods = new StoreGoods();
                            obj_goods = arr_goods_result.getJSONObject(j);
                            goods.setGoods_title(obj_goods.getString("goods_title"));
                            goods.setSales_price(obj_goods.getString("sales_price"));
                            goods.setThumb_img(obj_goods.getString("original_img"));
                            goods.setGoods_id(obj_goods.getString("goods_id"));
                            goods.setGoods_sku_id(obj_goods.getString("goods_sku_id"));
                            goodsList.add(goods);
                        }
                        store.setGoods_result(goodsList);
                        // "menu"
                        obj = obj.getJSONObject("menu");
                        store.setTitle(obj.getString("title"));
//                        LogUtil.i("obj.getString(\"menu\") = " + obj.getString("title"));
                        mList.add(store);
                    }
                    Message msg2 = new Message();
                    msg2.what = FLAG_RECYCLERVIEW;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_store_toCustom:
                startActivity(new Intent(getActivity(), CustomServiceActivity.class));
                break;
            case R.id.imgbtn_store_toStoreCar:
                if (!PreferenceUtil.getSharePre(getActivity()).getString("storetb_is_exist", "no").equals("yes")) {
                    ToastUtil.showMessage(getActivity(), "您的购物车空空哒，快去添加商品吧！");
                    return;
                }
                startActivity(new Intent(getActivity(), StoreCarActivity.class));
                break;
        }
    }
}
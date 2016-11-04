package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_StoreGoods_One extends Fragment {

    private ViewFlipper mFlipper;
    private FrameLayout mFramelayout;

    private TextView mTxtGoodsTitle;

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
                        ImageView img = new ImageView(getActivity());
                        ImageLoader.getInstance().displayImage(mList.get(i), img, options);
                        mFlipper.addView(img);
                    }
                    mFlipper.setInAnimation(getActivity(), R.anim.left_in);
                    mFlipper.setOutAnimation(getActivity(), R.anim.left_out);
                    mFlipper.startFlipping();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_one, null);
        initView(view);
        requestData();
        return view;
    }

    private void initView(View view) {
        mFlipper = (ViewFlipper) view.findViewById(R.id.viewflip_activity_storegoods);
        mFramelayout = (FrameLayout) view.findViewById(R.id.frame_activity_storegoods_fragment);

        mTxtGoodsTitle = (TextView) view.findViewById(R.id.txt_activity_storegoods_goodstitle);
    }

    private void requestData() {
        URL = URL + "?goodsid=" + getArguments().getString("goods_id") + "&skuid="
                + getArguments().getString("goods_sku_id") + "&userid="
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
}
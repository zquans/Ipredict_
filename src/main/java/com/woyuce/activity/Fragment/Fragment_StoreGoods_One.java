package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.StoreSpcAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 两部分，一：将传递到的数据直接放上视图，二：请求规格获取数据
 */
public class Fragment_StoreGoods_One extends Fragment implements AdapterView.OnItemClickListener {

    private ViewFlipper mFlipper;

    private TextView mTxtGoodsTitle, mTxtGoodsPrice, mTxtTotalSale, mTxtGoodComment, mTxtShowOrder;

    private String URL = "http://api.iyuce.com/v1/store/goodsdetail";
    private String URL_SPC;//可以做成局部变量

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_one, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mFlipper = (ViewFlipper) view.findViewById(R.id.viewflip_activity_storegoods);

        mTxtGoodsTitle = (TextView) view.findViewById(R.id.txt_activity_storegoods_goodstitle);
        mTxtGoodsPrice = (TextView) view.findViewById(R.id.txt_activity_storegoods_goodsprice);
        mTxtTotalSale = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_salenum);
        mTxtGoodComment = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_good);
        mTxtShowOrder = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_showorder);

        mGridGoal = (GridView) view.findViewById(R.id.gridview_fragment_store_goal);
        mGridArea = (GridView) view.findViewById(R.id.gridview_fragment_store_area);
        mGridTicket = (GridView) view.findViewById(R.id.gridview_fragment_store_ticket);
        mGridGoal.setOnItemClickListener(this);
        mGridArea.setOnItemClickListener(this);
        mGridTicket.setOnItemClickListener(this);

        //做第一部分，设置View上的数据
        setView();
        //做第二部分，请求规格参数
        URL_SPC = URL + "?goodsid=" + getArguments().getString("goods_id") + "&skuid=" + getArguments().getString("goods_sku_id");
        requestGoodsSpe(URL_SPC, false);
    }

    /**
     * 第一部分:将上一级获取到的数据设置在View上
     */
    private void setView() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        //将获取到的数据设置到View上
        ArrayList<String> mList = getArguments().getStringArrayList("mList");
        mTxtGoodsTitle.setText(getArguments().getString("goods_title"));
        mTxtGoodsPrice.setText(getArguments().getString("sales_price"));
        mTxtTotalSale.setText("销量" + getArguments().getString("total_sales_volume"));
        mTxtGoodComment.setText("好评" + getArguments().getString("total_good_volume"));
        mTxtShowOrder.setText("晒单" + getArguments().getString("total_show_order_volume"));

        //查看是否有轮播图
        if (mList.size() == 0) {
            mFlipper.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < mList.size(); i++) {
                ImageView img = new ImageView(getActivity());
                ImageLoader.getInstance().displayImage(mList.get(i), img, options);
                mFlipper.addView(img);
            }
            mFlipper.setInAnimation(getActivity(), R.anim.left_in);
            mFlipper.setOutAnimation(getActivity(), R.anim.left_out);
            mFlipper.startFlipping();
        }
    }

    /**
     * 第二部分:请求规格参数
     */

    //规格相关View
    private GridView mGridGoal, mGridArea, mGridTicket;
    private ArrayList<StoreGoods> mListGoal = new ArrayList<>();
    private ArrayList<StoreGoods> mListArea = new ArrayList<>();
    private ArrayList<StoreGoods> mListTickt = new ArrayList<>();
    private StoreSpcAdapter mGoalAdapter, mAreaAdapter, mTicketAdapter;

    private void requestGoodsSpe(String url, final boolean need_notify) {
        StringRequest goodsSpeRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj, obj_goal, obj_area, obj_ticket;
                    JSONArray arr, arr_goal, arr_area, arr_ticket;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("goods_sku");
                        LogUtil.i("test need =" + obj.getString("spec_texts") + "以及其他的价格、数量等等");//Test

                        arr = obj.getJSONArray("all_specs");
                        obj_goal = arr.getJSONObject(0);
                        obj_area = arr.getJSONObject(1);
                        obj_ticket = arr.getJSONObject(2);
                        if (need_notify == true) {
                            //如果不是第一次请求，则做数据刷新，先清除数据
                            mListGoal.clear();
                            mListArea.clear();
                            mListTickt.clear();
                        }
                        //获取item数据的数组
                        arr_goal = obj_goal.getJSONArray("spec_values");
                        arr_area = obj_area.getJSONArray("spec_values");
                        arr_ticket = obj_ticket.getJSONArray("spec_values");
                        getDataList(arr_goal, mListGoal);
                        getDataList(arr_area, mListArea);
                        getDataList(arr_ticket, mListTickt);
                        //如果不是第一次加载，就刷新
                        if (need_notify == true) {
                            mGoalAdapter.notifyDataSetChanged();
                            mAreaAdapter.notifyDataSetChanged();
                            mTicketAdapter.notifyDataSetChanged();
                        } else {
                            mGoalAdapter = new StoreSpcAdapter(getActivity(), mListGoal);
                            mGridGoal.setAdapter(mGoalAdapter);
                            mGridGoal.setNumColumns(mListGoal.size());//设置每行显示的Item数

                            mAreaAdapter = new StoreSpcAdapter(getActivity(), mListArea);
                            mGridArea.setAdapter(mAreaAdapter);
                            mGridArea.setNumColumns(mListArea.size());

                            mTicketAdapter = new StoreSpcAdapter(getActivity(), mListTickt);
                            mGridTicket.setAdapter(mTicketAdapter);
                            mGridTicket.setNumColumns(2);
                        }
                    } else {
                        ToastUtil.showMessage(getActivity(), obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        goodsSpeRequest.setTag("goodsSpeRequest");
        AppContext.getHttpQueue().add(goodsSpeRequest);
    }

    /**
     * 抽象出方法获取数组
     */
    private void getDataList(JSONArray arr, ArrayList<StoreGoods> list) throws JSONException {
        StoreGoods storeGoods;
        for (int i = 0; i < arr.length(); i++) {
            storeGoods = new StoreGoods();
            storeGoods.setAttr_id(arr.getJSONObject(i).getString("attr_id"));
            storeGoods.setAttr_text(arr.getJSONObject(i).getString("attr_text"));
            list.add(storeGoods);
        }
    }

    /**
     * 重设选中的Item及全部的Item
     *
     * @param parent
     * @param view
     */
    private void resetItemView(AdapterView<?> parent, View view, ArrayList<StoreGoods> list) {
        for (int i = 0; i < list.size(); i++) {
            parent.getChildAt(i).findViewById(R.id.txt_storegoods_spc).setBackgroundColor(Color.parseColor("#f0f2f5"));
        }
        view.findViewById(R.id.txt_storegoods_spc).setBackgroundColor(Color.parseColor("#cccccc"));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridview_fragment_store_goal:
                resetItemView(parent, view, mListGoal);

                URL_SPC = URL + "?goodsid=" + getArguments().getString("goods_id") + "&skuid="
                        + "&selected_specs=" + mListGoal.get(position).getAttr_id();
                requestGoodsSpe(URL_SPC, true);
                break;
            case R.id.gridview_fragment_store_area:
                resetItemView(parent, view, mListArea);

                URL_SPC = URL + "?goodsid=" + getArguments().getString("goods_id") + "&skuid="
                        + "&selected_specs=" + mListArea.get(position).getAttr_id() + ",7";
                requestGoodsSpe(URL_SPC, true);
                break;
            case R.id.gridview_fragment_store_ticket:
                resetItemView(parent, view, mListTickt);
                break;
        }
    }
}
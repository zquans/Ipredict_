package com.woyuce.activity.Controller.Store;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.woyuce.activity.Adapter.Store.StoreGoodsCommentAdapter;
import com.woyuce.activity.Adapter.Store.StoreShowOrderAdapter;
import com.woyuce.activity.BaseFragment;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Store.StoreGoods;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentStoreGoodsThree extends BaseFragment implements View.OnClickListener {

    private TextView mTxtAll, mTxtGood, mTxtMiddle, mTxtBad, mTxtShowOrder;

    private ListView mListView;
    private List<StoreGoods> mDataList = new ArrayList<>();
    private StoreGoodsCommentAdapter mAdapter;

    //请求数据
//    private String URL = "http://api.iyuce.com/v1/store/goodscommentsbygoodsid";
    private String URL_ShowOrder = "http://api.iyuce.com/v1/store/showordersbygoodsid";

    @Override
    public void onStop() {
        super.onStop();
//        AppContext.getHttpQueue().cancelAll("goodsCommentRequest");
        HttpUtil.removeTag(Constants.ACTIVITY_STORE_GOODS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_goods_three, null);
        initView(view);
        requestData();
        return view;
    }

    private void initView(View view) {
        mTxtAll = (TextView) view.findViewById(R.id.txt_storegoods_fragment3_all);
        mTxtGood = (TextView) view.findViewById(R.id.txt_storegoods_fragment3_good);
        mTxtMiddle = (TextView) view.findViewById(R.id.txt_storegoods_fragment3_middle);
        mTxtBad = (TextView) view.findViewById(R.id.txt_storegoods_fragment3_bad);
        mTxtShowOrder = (TextView) view.findViewById(R.id.txt_storegoods_fragment3_showorder);

        mTxtAll.setOnClickListener(this);
        mTxtGood.setOnClickListener(this);
        mTxtMiddle.setOnClickListener(this);
        mTxtBad.setOnClickListener(this);
        mTxtShowOrder.setOnClickListener(this);

//        mTxtAll.setText("全部(" + getArguments().getString("total_sales_volume") + ")");
        mTxtAll.setText("全部");
        mTxtGood.setText("好评(" + getArguments().getString("total_good_volume") + ")");
        mTxtBad.setText("差评(" + getArguments().getString("total_bad_volume") + ")");
        mTxtMiddle.setText("中评(" + getArguments().getString("total_medium_volume") + ")");
        mTxtShowOrder.setText("晒单(" + getArguments().getString("total_show_order_volume") + ")");

        //列表填充
        mListView = (ListView) view.findViewById(R.id.listview_store_fragment_three);
    }

    private void requestData() {
        HttpUtil.get(Constants.URL_GET_STORE_GOODS_THREE_COMMENT + "?goodsid=" + getArguments().getString("goods_id") + "&pageindex=" + "1" + "&pagesize=" + "30"
                , Constants.ACTIVITY_STORE_GOODS, new RequestInterface() {
                    @Override
                    public void doSuccess(String result) {
                        try {
                            JSONObject obj;
                            JSONArray arr;
                            obj = new JSONObject(result);
                            if (obj.getString("code").equals("0")) {
                                obj = obj.getJSONObject("goods_comment");
                                arr = obj.getJSONArray("goods_comments");
                                StoreGoods storegoods;
                                for (int i = 0; i < arr.length(); i++) {
                                    storegoods = new StoreGoods();
                                    obj = arr.getJSONObject(i);
                                    storegoods.setComment_text(obj.getString("comment_text"));
                                    storegoods.setCreate_at(obj.getString("create_at"));
                                    storegoods.setCreate_by_name(obj.getString("create_by_name"));
                                    storegoods.setSatisfaction(obj.getString("satisfaction"));
                                    mDataList.add(storegoods);
                                }
                                mAdapter = new StoreGoodsCommentAdapter(getActivity(), mDataList);
                                mListView.setAdapter(mAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private StoreShowOrderAdapter mShowOrderAdapter;
    private ArrayList<StoreGoods> mShowOrderList = new ArrayList<>();

    /**
     * 请求晒单数据
     */
    private void requestShowOrder(boolean isfirst) {
        if (!isfirst) {
            mShowOrderAdapter = new StoreShowOrderAdapter(getActivity(), mShowOrderList);
            mListView.setAdapter(mShowOrderAdapter);
            return;
        }
        URL_ShowOrder = URL_ShowOrder + "?goodsid=" + getArguments().getString("goods_id");
//                + "&pageIndex={pageIndex}&pageSize={pageSize}";
        HttpUtil.get(URL_ShowOrder, Constants.ACTIVITY_STORE_GOODS, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    JSONArray arr;
                    obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("goods_show_order");
                        arr = obj.getJSONArray("goods_show_orders");
                        StoreGoods storegoods;
                        for (int i = 0; i < arr.length(); i++) {
                            storegoods = new StoreGoods();
                            obj = arr.getJSONObject(i);
                            storegoods.setComment_text(obj.getString("comment_text"));
                            storegoods.setCreate_by_name(obj.getString("create_by_name"));
                            storegoods.setShow_at(obj.getString("show_at"));
                            storegoods.setImg_url(obj.getString("img_url"));
                            mShowOrderList.add(storegoods);
                        }
                        mShowOrderAdapter = new StoreShowOrderAdapter(getActivity(), mShowOrderList);
                        mListView.setAdapter(mShowOrderAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<StoreGoods> mFilterList = new ArrayList<>();
    private boolean isfirst = true;

    @Override
    public void onClick(View v) {
        mFilterList.clear();
        switch (v.getId()) {
            case R.id.txt_storegoods_fragment3_all:
                resetTextColor(mTxtAll);
                doFilter(mDataList, "全部");
                break;
            case R.id.txt_storegoods_fragment3_good:
                resetTextColor(mTxtGood);
                doFilter(mFilterList, "好评");
                break;
            case R.id.txt_storegoods_fragment3_middle:
                resetTextColor(mTxtMiddle);
                doFilter(mFilterList, "中评");
                break;
            case R.id.txt_storegoods_fragment3_bad:
                resetTextColor(mTxtBad);
                doFilter(mFilterList, "差评");
                break;
            case R.id.txt_storegoods_fragment3_showorder:
                resetTextColor(mTxtShowOrder);
                requestShowOrder(isfirst);
                isfirst = false;
                break;
        }
    }

    /**
     * 重设选中后的字体颜色
     */
    private void resetTextColor(TextView textview) {
        mTxtAll.setTextColor(Color.parseColor("#cccccc"));
        mTxtGood.setTextColor(Color.parseColor("#cccccc"));
        mTxtMiddle.setTextColor(Color.parseColor("#cccccc"));
        mTxtBad.setTextColor(Color.parseColor("#cccccc"));
        mTxtShowOrder.setTextColor(Color.parseColor("#cccccc"));
        textview.setTextColor(Color.parseColor("#f7941d"));
    }

    /**
     * 过滤数据，避免再请求网络
     */
    private void doFilter(List<StoreGoods> mList, String comment) {
        if (mList == mDataList) {
            mAdapter = new StoreGoodsCommentAdapter(getActivity(), mList);
            mListView.setAdapter(mAdapter);
            return;
        }
        //循环过滤
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getSatisfaction().equals(comment)) {
                mFilterList.add(mDataList.get(i));
            }
        }
        mAdapter = new StoreGoodsCommentAdapter(getActivity(), mList);
        mListView.setAdapter(mAdapter);
    }
}
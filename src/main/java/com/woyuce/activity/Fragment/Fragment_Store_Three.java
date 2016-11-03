package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Store_Three extends Fragment implements View.OnClickListener {

    private TextView mTxtAll, mTxtGood, mTxtMiddle, mTxtBad, mTxtShowOrder;

    private ListView mListView;
    private ArrayList<String> mList = new ArrayList<>();

    //请求数据
    private String URL = "http://api.iyuce.com/v1/store/goodscommentsbygoodsid";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_three, null);
        initView(view);
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

        //列表填充
        mListView = (ListView) view.findViewById(R.id.listview_store_fragment_three);
        //数据请求
        requestData();
        //适配器
    }

    private void requestData() {
        URL = URL + "?goodsid=" + "2" + "&pageindex=" + "1" + "&pagesize=" + "10";
        StringRequest goodsCommentRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtil.i(s.toString());
                        try {
                            JSONObject obj;
                            JSONArray arr;
                            obj = new JSONObject(s);
                            if (obj.getString("code").equals("0")) {
                                obj = obj.getJSONObject("goods_comment");
                                arr = obj.getJSONArray("goods_comments");
                                for (int i = 0; i < arr.length(); i++) {
                                    obj = arr.getJSONObject(i);
                                    mList.add(obj.getString("comment_text"));
                                }
                                ArrayAdapter mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mList);
                                mListView.setAdapter(mAdapter);
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
        goodsCommentRequest.setTag("goodsCommentRequest");
        AppContext.getHttpQueue().add(goodsCommentRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_storegoods_fragment3_all:
                break;
            case R.id.txt_storegoods_fragment3_good:
                break;
            case R.id.txt_storegoods_fragment3_middle:
                break;
            case R.id.txt_storegoods_fragment3_bad:
                break;
            case R.id.txt_storegoods_fragment3_showorder:
                break;
        }
    }
}
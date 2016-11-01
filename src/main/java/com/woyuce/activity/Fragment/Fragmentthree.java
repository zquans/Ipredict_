package com.woyuce.activity.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Activity.WeiboGroupActivity;
import com.woyuce.activity.Activity.WeiboInfoActivity;
import com.woyuce.activity.Activity.WeiboPublishActivity;
import com.woyuce.activity.Adapter.WeiboRecommandAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.WeiboBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragmentthree extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ViewFlipper mFlipper;
    private LinearLayout mLinearOne, mLinearTwo, mLinearThree, mLinearFour;

    private ImageButton mImgBack;
    private Button mBtnToPulish;
    private TextView mTxtChangeTab;

    private GridView mGridView;
    private List<WeiboBean> dataList = new ArrayList<>();
    private WeiboRecommandAdapter mAdapter;

    private String localtoken;
    private String URL = "http://api.iyuce.com/v1/bbs/weibolist";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mImgBack = (ImageButton) view.findViewById(R.id.imgbt_tab3_back);
        mBtnToPulish = (Button) view.findViewById(R.id.btn_fragmenttab3_topulish);
        mTxtChangeTab = (TextView) view.findViewById(R.id.txt_tab3_changetab);
        mLinearOne = (LinearLayout) view.findViewById(R.id.ll_tab3_one);
        mLinearTwo = (LinearLayout) view.findViewById(R.id.ll_tab3_two);
        mLinearThree = (LinearLayout) view.findViewById(R.id.ll_tab3_three);
        mLinearFour = (LinearLayout) view.findViewById(R.id.ll_tab3_four);

        mImgBack.setOnClickListener(this);
        mBtnToPulish.setOnClickListener(this);
        mLinearOne.setOnClickListener(this);
        mLinearTwo.setOnClickListener(this);
        mLinearThree.setOnClickListener(this);
        mLinearFour.setOnClickListener(this);

        //ViewFlipper加入轮播图
        mFlipper = (ViewFlipper) view.findViewById(R.id.vf_fragmenttab3_weibo);
        for (int i = 0; i < 4; i++) {
            ImageView mImg = new ImageView(getActivity());
            mImg.setBackgroundResource(R.mipmap.background_music);
            mFlipper.addView(mImg);
        }
        mFlipper.setInAnimation(getActivity(), R.anim.left_in);
        mFlipper.setOutAnimation(getActivity(), R.anim.left_out);
        mFlipper.startFlipping();

        //GridView填充数据
        mGridView = (GridView) view.findViewById(R.id.gv_fragmenttab3_weibo);
        mGridView.setOnItemClickListener(this);
        requestJson();
    }

    private void requestJson() {
        StringRequest weiboDataRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(response);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        WeiboBean weibo;
                        JSONArray imgarr;
                        JSONObject imgobj;
                        ArrayList<String> imgList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            weibo = new WeiboBean();
                            weibo.body = obj.getString("body");
                            weibo.avatar_url = obj.getString("avatar_url");
                            weibo.date_created = obj.getString("date_created");
                            weibo.author = obj.getString("author");
                            weibo.reply_count = obj.getString("reply_count");
                            weibo.microblog_id = obj.getInt("microblog_id");
//                            if (obj.getString("has_photo").equals("true")) {
                            imgarr = obj.getJSONArray("imglist");
                            for (int j = 0; j < imgarr.length(); j++) {
                                imgobj = imgarr.getJSONObject(j);
                                weibo.mImgList.add(imgobj.getString("img_url"));
                                imgList.add(weibo.pulish_image);
                            }
//                            } else {
//                                weibo.pulish_image = obj.getString("source_url");
//                            }
                            dataList.add(weibo);
                            LogUtil.i("dataList = " + weibo.toString());
                        }
                    } else {
                        LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + obj.getString("message"));
                    }
                    // 将数据放到适配器中
                    mAdapter = new WeiboRecommandAdapter(getActivity(), dataList);
                    mGridView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(getActivity()).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }
        };
        weiboDataRequest.setTag("tab3");
        AppContext.getHttpQueue().add(weiboDataRequest);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbt_tab3_back:
                Snackbar.make(v, "别逗了,这能返回去哪里", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btn_fragmenttab3_topulish:
                Intent intent2publish = new Intent(getActivity(), WeiboPublishActivity.class);
                intent2publish.putExtra("localtoken", localtoken);
                startActivity(intent2publish);
                break;
            case R.id.ll_tab3_one:
                mTxtChangeTab.setText("精选动态");
                break;
            case R.id.ll_tab3_two:
                mTxtChangeTab.setText("推荐动态");
                break;
            case R.id.ll_tab3_three:
                break;
            case R.id.ll_tab3_four:
                Intent intent = new Intent(getActivity(), WeiboGroupActivity.class);
                intent.putExtra("local_token", localtoken);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), WeiboInfoActivity.class);
        intent.putExtra("local_body", dataList.get(position).body);
        intent.putExtra("local_headurl", dataList.get(position).avatar_url);
        intent.putExtra("local_time", dataList.get(position).date_created);
        intent.putExtra("local_author", dataList.get(position).author);
        intent.putExtra("local_reply_count", dataList.get(position).reply_count);
        intent.putExtra("local_microblog_id", dataList.get(position).microblog_id);
        intent.putExtra("local_token", localtoken);
        intent.putStringArrayListExtra("mImgList", dataList.get(position).mImgList);
        startActivity(intent);
    }
}
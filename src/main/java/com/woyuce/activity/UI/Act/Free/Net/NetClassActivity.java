package com.woyuce.activity.UI.Act.Free.Net;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.UI.Act.BaseActivity;
import com.woyuce.activity.UI.Act.Store.StoreGoodsActivity;
import com.woyuce.activity.UI.Act.Common.WebNoCookieActivity;
import com.woyuce.activity.Adapter.Free.Net.NetClassCourseAdapter;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.Bean.Free.Net.NetBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/21.
 */

public class NetClassActivity extends BaseActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private ImageView imgback;
    private TextView txtcontent;
    private Button btn_zhibo, btn_lubo;
    private Spinner spntime, spnclass;
    private GridView gridview;
    private NetClassCourseAdapter webcourseAdapter;

    private ArrayAdapter<String> adapterdate, adapterclass;
    private String localdate, localclass;
    private List<String> dateList = new ArrayList<>();
    private List<String> classnameList = new ArrayList<>();
    private List<String> classidList = new ArrayList<>();

    private List<NetBean> wcgList = new ArrayList<>();
    private static final String URL_TIME = "http://api.iyuce.com/v1/exam/notifydropdownlist";
    private static final String URL_NOTICE = "http://api.iyuce.com/v1/exam/notifycontent";
    private static final String URL_WebCourse = "http://api.iyuce.com/v1/exam/webcoursegroup";
    private String localtoken;

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("wangluoban");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netclass);

        initView();
        getSpinnerJson();
        getWebCourse();
    }

    private void initView() {
        localtoken = PreferenceUtil.getSharePre(NetClassActivity.this).getString("localtoken", "");

        imgback = (ImageView) findViewById(R.id.arrow_back);
        txtcontent = (TextView) findViewById(R.id.txt_wangluoban_content);
        btn_zhibo = (Button) findViewById(R.id.btn_wangluoban_zhibo);
        btn_lubo = (Button) findViewById(R.id.btn_wangluoban_lubo);
        gridview = (GridView) findViewById(R.id.gridview_activity_wangluoban);
        spntime = (Spinner) findViewById(R.id.spn_wangluoban_time);
        spnclass = (Spinner) findViewById(R.id.spn_wangluoban_class);

        imgback.setOnClickListener(this);
        btn_zhibo.setOnClickListener(this);
        btn_lubo.setOnClickListener(this);
        gridview.setOnItemClickListener(this);
    }

    /* 获取notice的数据，初始传值为null */
    private void getNoticeJson() {
        StringRequest strinRequest = new StringRequest(Request.Method.POST, URL_NOTICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("getNoticeJson = ");
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(response);
                    arr = obj.getJSONArray("data");
                    if (arr.length() >= 1) {
                        txtcontent.setText(arr.getString(0));
                    } else {
                        txtcontent.setText("还没有复习计划，敬请关注哦，亲!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("exam_time", localdate);
                map.put("class_type", localclass);
                return map;
            }
        };
        strinRequest.setTag("wangluoban");
        AppContext.getHttpQueue().add(strinRequest);
    }

    /* 获取spn的数据 */
    private void getSpinnerJson() {
        StringRequest strinrequest = new StringRequest(Request.Method.POST, URL_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("getSpinnerJson = ");
                JSONArray arr;
                JSONArray examtime;
                JSONObject obj;
                String examdate;
                try {
                    obj = new JSONObject(response);
                    int result = obj.getInt("code");
                    if (result == 0) {
                        obj = obj.getJSONObject("data");
                        arr = obj.getJSONArray("class_type");
                        examtime = obj.getJSONArray("exam_time");
                        /* 遍历取网络班时间 */
                        for (int i = 0; i < examtime.length(); i++) {
                            examdate = examtime.getString(i);
                            dateList.add(examdate);
                        }
                        /* 遍历取网络班类型 */
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            classnameList.add(obj.getString("wcg_name"));
                            classidList.add(obj.getString("wcg_id"));
                        }
                    } else {
                        // Log.e("Code Error", "code spnTimewrong" + response);
                    }
                    //数据加载完后放入
                    setSpnDate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }
        };
        strinrequest.setTag("wangluoban");
        AppContext.getHttpQueue().add(strinrequest);
    }

    /**
     * 数据加载完后放入 ,spnner初始化
     */
    private void setSpnDate() {
        adapterdate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateList);
        adapterdate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spntime.setAdapter(adapterdate);
        spntime.setOnItemSelectedListener(this);

        adapterclass = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classnameList);
        adapterclass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnclass.setAdapter(adapterclass);
        spnclass.setOnItemSelectedListener(this);
    }

    /**
     * 获取spn的数据
     */
    private void getWebCourse() {
        progressdialogshow(this);
        StringRequest strinrequest = new StringRequest(Request.Method.POST, URL_WebCourse, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("getWebCourse = ");
                JSONArray arr;
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    int result = obj.getInt("code");
                    if (result == 0) {
                        arr = obj.getJSONArray("data");
                        NetBean wcgbean;
                        for (int i = 0; i < arr.length(); i++) {
                            wcgbean = new NetBean();
                            obj = arr.getJSONObject(i);
                            wcgbean.setWcg_name(obj.getString("wcg_name"));
                            wcgbean.setWcg_id(obj.getString("wcg_id"));
                            wcgbean.setWcg_powerid(obj.getString("wcg_powerid"));
                            wcgbean.setMonthId(obj.getString("month_id"));
                            wcgbean.setImgUrl(obj.getString("img_url"));
                            wcgList.add(wcgbean);
                        }
                    } else {
                        // Log.e("Code Error", "code spnTimewrong" + response);
                    }
                    /* gridview设置adapter */
                    webcourseAdapter = new NetClassCourseAdapter(NetClassActivity.this, wcgList);
                    gridview.setAdapter(webcourseAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressdialogcancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialogcancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }
        };
        strinrequest.setTag("wangluoban");
        AppContext.getHttpQueue().add(strinrequest);
    }

    /**
     * 获取商城商品信息
     */
    private void getactivegoods() {
        StringRequest getGoodsRequest = new StringRequest(Request.Method.GET, Constants.URL_GetGoods, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("data");
                        Intent intent = new Intent(NetClassActivity.this, StoreGoodsActivity.class);
                        intent.putExtra("goods_id", obj.getString("goods_id"));
                        intent.putExtra("goods_sku_id", obj.getString("goods_sku_id"));
                        intent.putExtra("goods_title", obj.getString("goods_title"));
                        intent.putExtra("sales_price", obj.getString("sales_price"));
                        intent.putExtra("can_go_store_back", "yes");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        getGoodsRequest.setTag("wangluobanlesson");
        AppContext.getHttpQueue().add(getGoodsRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.btn_wangluoban_zhibo:
                Intent intent = new Intent(this, WebNoCookieActivity.class);
                intent.putExtra("URL", Constants.URL_WEB_ZHIBO);
                intent.putExtra("TITLE", "网络班直播报名");
                intent.putExtra("COLOR", "#1e87e2");
                startActivity(intent);
                break;
            case R.id.btn_wangluoban_lubo:
                //跳转商城商品
                getactivegoods();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_wangluoban_time:
                localdate = dateList.get(position);
                getNoticeJson();
                break;
            case R.id.spn_wangluoban_class:
                localclass = classidList.get(position);
                getNoticeJson();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 传递相应的localwcg_id
        String localwcg_id = wcgList.get(position).getWcg_id();
        String localwcg_pid = wcgList.get(position).getWcg_powerid();
        String localwcg_mid = wcgList.get(position).getMonthId();
        String localwcg_name = wcgList.get(position).getWcg_name();

        Intent intent = new Intent(NetClassActivity.this, NetClassLessonActivity.class);
        intent.putExtra("localwcg_id", localwcg_id);
        intent.putExtra("localwcg_pid", localwcg_pid);
        intent.putExtra("localwcg_mid", localwcg_mid);
        intent.putExtra("localwcg_name", localwcg_name);
        startActivity(intent);
    }
}
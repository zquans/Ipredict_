package com.woyuce.activity.UI.Act.Speaking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.UI.Act.BaseActivity;
import com.woyuce.activity.UI.Act.MainActivity;
import com.woyuce.activity.Adapter.Speaking.SpeakingVoteCountAdapter;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.Bean.Speaking.SpeakingVoteCount;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingStatisActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    //顶部导航条
    private LinearLayout llBack;
    private TextView mTxtToStastis, mTxtToShare;
    private ImageView mImgBack;

    private ListView listViewVote;
    private Spinner spnPart, spnDate, spnCity;
    private Button btnMore;
    private TextView txtSearch;
    private AutoCompleteTextView autoTxt;

    private String URL_CITY = "http://iphone.ipredicting.com/kyCityApi.aspx";
    private String URL_VOTE = "http://iphone.ipredicting.com/kysubOrder.aspx";
    private int localpartid, localdateid, localcityid;

    private ArrayAdapter<String> partAdapter, dateAdapter, cityAdapter;
    private List<String> partList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();
    private List<String> cityList = new ArrayList<>();
    private List<String> cityidList = new ArrayList<>();
    private List<SpeakingVoteCount> votenoList = new ArrayList<>();
    private SpeakingVoteCountAdapter votenoadapter;

    private boolean isPartFirst = true;
    private boolean isDateFirst = true;
    private boolean isAllCity = false;

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("statis");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingstatis);

        initView();
        getCityList();
    }

    private void initView() {
        mImgBack = (ImageView) findViewById(R.id.img_back);
        llBack = (LinearLayout) findViewById(R.id.ll_speaking_share);
        mTxtToStastis = (TextView) findViewById(R.id.txt_speaking_stastis);
        mTxtToShare = (TextView) findViewById(R.id.txt_speaking_share);
        mTxtToStastis.setTextColor(Color.parseColor("#2299cc"));
        mTxtToStastis.setBackgroundResource(R.drawable.buttonstyle_bluestroke);
        mTxtToShare.setTextColor(Color.parseColor("#ffffff"));
        mTxtToShare.setBackgroundResource(R.drawable.buttonstyle_whitestroke);

        autoTxt = (AutoCompleteTextView) findViewById(R.id.autotxt_statis);
        btnMore = (Button) findViewById(R.id.btn_statis_more);
        listViewVote = (ListView) findViewById(R.id.listview_statis_vote);
        txtSearch = (TextView) findViewById(R.id.txt_statis_search);
        spnPart = (Spinner) this.findViewById(R.id.spinner_statis_part);
        spnDate = (Spinner) this.findViewById(R.id.spinner_statis_date);
        spnCity = (Spinner) this.findViewById(R.id.spinner_statis_city);

        mImgBack.setOnClickListener(this);
        llBack.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        txtSearch.setOnClickListener(this);
        listViewVote.setOnItemClickListener(this);
        spnPart.setOnItemSelectedListener(this);
        spnDate.setOnItemSelectedListener(this);
        spnCity.setOnItemSelectedListener(this);
    }

    private void initEvent() {
        partList.add("part1");
        partList.add("part2");
        dateList.add("30天");
        dateList.add("120天");

        partAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, partList);
        partAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPart.setAdapter(partAdapter);

        dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateList);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDate.setAdapter(dateAdapter);

        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCity.setAdapter(cityAdapter);
    }

    private void getCityList() {
        StringRequest strinRequest = new StringRequest(Request.Method.POST, URL_CITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                String cityname;
                String cityid;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        /*城市列表中没有这一项，所以在起初加入该数据*/
                        cityidList.add("0");
                        cityList.add("全国");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            cityid = jsonObject.getString("cityid");
                            cityidList.add(cityid); // 读取城市ID
                            cityname = jsonObject.getString("cityname");
                            cityList.add(cityname); // 读取城市
                        }
                    } else {
                        LogUtil.e("code!=0 DATA_BACK", "读取页面失败： " + jsonObject.getString("message"));
                    }
                    initEvent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, voteErrorListener());
        strinRequest.setTag("statis");
        AppContext.getHttpQueue().add(strinRequest);
    }

    /**
     * 抽出Volley中的访问成功voteResponseListener方法 (仅用于getVote()及相关方法)
     */
    private Response.Listener<String> voteResponseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                SpeakingVoteCount voteno;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            voteno = new SpeakingVoteCount();
                            voteno.subid = jsonObject.getString("subid");
                            voteno.categoryName = jsonObject.getString("categoryName");
                            voteno.votetotal = jsonObject.getString("votetotal");
                            votenoList.add(voteno);
                        }
                    } else {
                        String message = jsonObject.getString("message");
                        LogUtil.e("code!=0 DATA_BACK", "读取页面失败： " + message);
                    }
                    votenoadapter = new SpeakingVoteCountAdapter(SpeakingStatisActivity.this, votenoList, isAllCity);
                    listViewVote.setAdapter(votenoadapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 抽出Volley中访问失败voteErrorListener方法 (本类通用)
     */
    private Response.ErrorListener voteErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-BACK", "连接错误原因： " + error.getMessage()); // 可以做错误处理
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        votenoList.clear();
        switch (parent.getId()) {
            case R.id.spinner_statis_part:
            /*判断是否为第一次自动加载，若为第一次，则不加载，并设定localpartid==2，默认为先读取该值*/
                if (isPartFirst == true) {
                    isPartFirst = false;
                    localpartid = 1;
                    break;
                }
//			isAllCity = false;
            /*错位赋值*/
                if (position == 0) {
                    localpartid = 1;
                } else if (position == 1) {
                    localpartid = 2;
                }
                StringRequest strinRequest = new StringRequest(Request.Method.POST, URL_VOTE, voteResponseListener(),
                        voteErrorListener()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> hashMap = new HashMap<>();
                        hashMap.put("partid", localpartid + "");
                        hashMap.put("cityid", localcityid + "");
                        hashMap.put("days", localdateid + "");
                        LogUtil.e("id", "partid = " + localpartid + ",dateid = " + localdateid + ",cityid" + localcityid);
                        return hashMap;
                    }
                };
                strinRequest.setTag("statis");
                AppContext.getHttpQueue().add(strinRequest);
                break;
            case R.id.spinner_statis_date:
            /*判断是否为第一次自动加载，若为第一次，则不加载*/
                if (isDateFirst == true) {
                    isDateFirst = false;
                    localdateid = 0;
                    break;
                }
//			isAllCity = false;
                localdateid = position;
                StringRequest strinRequest1 = new StringRequest(Request.Method.POST, URL_VOTE, voteResponseListener(),
                        voteErrorListener()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("partid", localpartid + "");
                        hashMap.put("cityid", localcityid + "");
                        hashMap.put("days", localdateid + "");
                        LogUtil.e("id", "dateid = " + localdateid + ",cityid" + localcityid + ",partid = " + localpartid);
                        return hashMap;
                    }
                };
                strinRequest1.setTag("statis");
                AppContext.getHttpQueue().add(strinRequest1);
                break;
            case R.id.spinner_statis_city:
                localcityid = Integer.parseInt(cityidList.get(position));
                if (position == 0) {
                    isAllCity = true;
                } else {
                    isAllCity = false;
                }
                StringRequest strinRequest2 = new StringRequest(Request.Method.POST, URL_VOTE, voteResponseListener(),
                        voteErrorListener()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("partid", localpartid + "");
                        hashMap.put("cityid", localcityid + "");
                        hashMap.put("days", localdateid + "");
                        LogUtil.e("id", "cityid" + localcityid + ",partid = " + localpartid + ",dateid = " + localdateid);
                        return hashMap;
                    }
                };
                strinRequest2.setTag("statis");
                AppContext.getHttpQueue().add(strinRequest2);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpeakingVoteCount voteno = votenoList.get(position);
        String localsubid = voteno.subid;
        Intent it_subContent = new Intent(this, SpeakingContentActivity.class);
        it_subContent.putExtra("localsubid", localsubid);
        startActivity(it_subContent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_speaking_share:
                startActivity(new Intent(this, SpeakingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.btn_statis_more:
                Intent it_more = new Intent(this, SpeakingMoreActivity.class);
                startActivity(it_more);
                overridePendingTransition(0, 0);
                break;
            case R.id.img_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.txt_statis_search:
                String localsearch = autoTxt.getText().toString();
                Intent it_search = new Intent(SpeakingStatisActivity.this, SpeakingSearchActivity.class);
                it_search.putExtra("localsearch", localsearch);
                startActivity(it_search);
                overridePendingTransition(0, 0);
                break;
        }
    }
}
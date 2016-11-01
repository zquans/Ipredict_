package com.woyuce.activity.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.SpeakingMoreAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.SpeakingMore;
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
public class SpeakingMoreActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    //顶部导航条
    private ImageView mImgBack;
    private TextView mTxtToStastis, mTxtToShare;
    private LinearLayout llBack;

    private GridView gridView;
    private Button btnPart1, btnPart2;
    private ImageView mGuidemap;

    private String URL = "http://iphone.ipredicting.com/kysubCategoryApi.aspx";
    private int localPartid = 1;
    private List<SpeakingMore> categoryList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("category");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingmore);

        initView();
        initGuidemap();
        getJson();
    }

    private void initView() {
        //顶部导航条
        mImgBack = (ImageView) findViewById(R.id.img_back);
        llBack = (LinearLayout) findViewById(R.id.ll_speaking_share);
        mTxtToStastis = (TextView) findViewById(R.id.txt_speaking_stastis);
        mTxtToShare = (TextView) findViewById(R.id.txt_speaking_share);
        mTxtToStastis.setTextColor(Color.parseColor("#2299cc"));
        mTxtToStastis.setBackgroundResource(R.drawable.buttonstyle_bluestroke);
        mTxtToShare.setTextColor(Color.parseColor("#ffffff"));
        mTxtToShare.setBackgroundResource(R.drawable.buttonstyle_whitestroke);

        btnPart1 = (Button) findViewById(R.id.btn_category_part1);
        btnPart2 = (Button) findViewById(R.id.btn_category_part2);
        gridView = (GridView) findViewById(R.id.gridview_category);
        mGuidemap = (ImageView) findViewById(R.id.img_category_guidemap);

        llBack.setOnClickListener(this);
        btnPart2.setOnClickListener(this);
        btnPart1.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
    }

    // 初始化重点标识图
    private void initGuidemap() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage("http://www.iyuce.com/res/images/ky.jpg", mGuidemap, options);
    }

    public void getJson() {
        StringRequest strinRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                SpeakingMore category;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            category = new SpeakingMore();
                            category.subCategoryid = jsonObject.getString("subCategoryid");
                            category.subCategoryname = jsonObject.getString("subCategoryname");
                            category.fontColor = jsonObject.getString("fontColor");
                            categoryList.add(category);
                        }
                    } else {
                        LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + jsonObject.getString("message"));
                    }
                    SpeakingMoreAdapter adapter = new SpeakingMoreAdapter(SpeakingMoreActivity.this, categoryList);
                    gridView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong_BACK", "联接错误原因： " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("partid", localPartid + "");
                return hashMap;
            }
        };
        strinRequest.setTag("category");
        AppContext.getHttpQueue().add(strinRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpeakingMore category = categoryList.get(position);
        String localsubCategoryid = category.subCategoryid;
        Intent it_subContent = new Intent(this, SpeakingContentActivity.class);
        it_subContent.putExtra("localsubCategoryid", localsubCategoryid);
        startActivity(it_subContent);
    }

    public void back(View view) {
        finish();
        overridePendingTransition(0, 0);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_speaking_share:
                Intent it_speaking = new Intent(this, SpeakingActivity.class);
                startActivity(it_speaking);
                overridePendingTransition(0, 0);
                break;
            case R.id.img_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btn_category_part1:
                categoryList.clear();
                btnPart1.setBackgroundResource(com.woyuce.activity.R.drawable.buttonstyle_blue);
                btnPart1.setTextColor(Color.parseColor("#ffffff"));
                btnPart2.setBackgroundResource(com.woyuce.activity.R.drawable.buttonstyle);
                btnPart2.setTextColor(Color.parseColor("#3399ff"));
                localPartid = 1;
                getJson();
                break;
            case R.id.btn_category_part2:
                categoryList.clear();
                btnPart1.setBackgroundResource(com.woyuce.activity.R.drawable.buttonstyle);
                btnPart1.setTextColor(Color.parseColor("#3399ff"));
                btnPart2.setBackgroundResource(com.woyuce.activity.R.drawable.buttonstyle_blue);
                btnPart2.setTextColor(Color.parseColor("#ffffff"));
                localPartid = 2;
                getJson();
                break;
        }
    }
}
package com.woyuce.activity.UI.Activity.Speaking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.woyuce.activity.Adapter.Speaking.SpeakingSearchAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Speaking.SpeakingSearch;
import com.woyuce.activity.R;
import com.woyuce.activity.UI.Activity.MainActivity;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    //顶部导航条
    private ImageView mImgBack;
    private LinearLayout llback;
    private TextView mTxtToStastis, mTxtToShare;

    private ListView lvSearch;
    private TextView txtNull;

    private String localsearch;
    private List<SpeakingSearch> searchList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_SPEAKING_SEARCH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingsearch);

        initView();
        getJson();
    }

    private void initView() {
        //顶部导航条
        mImgBack = (ImageView) findViewById(R.id.img_back);
        llback = (LinearLayout) findViewById(R.id.ll_speaking_share);
        mTxtToStastis = (TextView) findViewById(R.id.txt_speaking_stastis);
        mTxtToShare = (TextView) findViewById(R.id.txt_speaking_share);
        mTxtToStastis.setTextColor(Color.parseColor("#2299cc"));
        mTxtToStastis.setBackgroundResource(R.drawable.buttonstyle_bluestroke);
        mTxtToShare.setTextColor(Color.parseColor("#ffffff"));
        mTxtToShare.setBackgroundResource(R.drawable.buttonstyle_whitestroke);

        //读取数据
        Intent it_search = getIntent();
        localsearch = it_search.getStringExtra("localsearch");

        txtNull = (TextView) findViewById(R.id.txt_search_null);
        lvSearch = (ListView) findViewById(R.id.listview_search);

        mImgBack.setOnClickListener(this);
        llback.setOnClickListener(this);
        lvSearch.setOnItemClickListener(this);
    }

    public void getJson() {
        HttpParams params = new HttpParams();
        params.put("key", localsearch);
        OkGo.post(Constants.URL_POST_SPEAKING_SEARCH).tag(Constants.ACTIVITY_SPEAKING_SEARCH).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
        JSONObject jsonObject;
        SpeakingSearch search;
        try {
            jsonObject = new JSONObject(response);
            int result = jsonObject.getInt("code");
            if (result == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() == 0) {
                    txtNull.setText("没有找到您需要的内容呢，亲...");
                }
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    search = new SpeakingSearch();
                    search.subCategoryid = jsonObject.getString("subCategoryid");
                    search.subname = jsonObject.getString("subname");
                    searchList.add(search);
                }
            } else {
                LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + jsonObject.getString("message"));
                txtNull.setText("您没有输入搜索内容哦，亲!");
            }
            SpeakingSearchAdapter adapter = new SpeakingSearchAdapter(SpeakingSearchActivity.this, searchList);
            lvSearch.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpeakingSearch search = searchList.get(position);
        String localsubCategoryid = search.subCategoryid;
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
        }
    }
}
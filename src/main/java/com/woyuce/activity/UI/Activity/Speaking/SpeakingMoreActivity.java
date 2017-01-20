package com.woyuce.activity.UI.Activity.Speaking;

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

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Speaking.SpeakingMoreAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Speaking.SpeakingMore;
import com.woyuce.activity.R;
import com.woyuce.activity.UI.Activity.MainActivity;
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
public class SpeakingMoreActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    //顶部导航条
    private ImageView mImgBack;
    private TextView mTxtToStastis, mTxtToShare;
    private LinearLayout llBack;

    private GridView gridView;
    private Button btnPart1, btnPart2;
    private ImageView mGuidemap;

    private int localPartid = 1;
    private List<SpeakingMore> categoryList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_SPEAKING_MORE);
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
        ImageLoader.getInstance().displayImage(Constants.URL_GUIDE_IMG_SPEAKING, mGuidemap, options);
    }

    public void getJson() {
        HttpParams params = new HttpParams();
        params.put("partid", localPartid + "");
        OkGo.post(Constants.URL_POST_SPEAKIGN_MORE).tag(Constants.ACTIVITY_SPEAKING_MORE).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
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
            }
            SpeakingMoreAdapter adapter = new SpeakingMoreAdapter(SpeakingMoreActivity.this, categoryList);
            gridView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
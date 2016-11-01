package com.woyuce.activity.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.WitContent;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;

public class WitContentActivity extends BaseActivity implements OnClickListener {

    private TextView txtContent, txtTitle;
    private Button btnBack, btnToAnswer;
    private ImageView mImgview;
    private PhotoView photoView;

    private String URL = "http://iphone.ipredicting.com/xzsubContent.aspx";
    private String localsubCategoryid, localname, localsubid, localimgUrl, localanswerUrl;

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("witcontent");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witcontent);

        initView();
        getJson();
    }

    private void initView() {
        Intent it = getIntent();
        localsubCategoryid = it.getStringExtra("localsubCategoryid");
        localname = it.getStringExtra("localname");
        localsubid = it.getStringExtra("localsubid");

        mImgview = (ImageView) findViewById(R.id.arrow_back);
        txtTitle = (TextView) findViewById(R.id.txt_witcontent_title);
        txtContent = (TextView) findViewById(R.id.txt_witcontent_content);
        photoView = (PhotoView) findViewById(R.id.photoview_witcontent);
        btnToAnswer = (Button) findViewById(R.id.btn_witcontent_toAnswer);
        btnBack = (Button) findViewById(R.id.button_witcontent_back);

        mImgview.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnToAnswer.setOnClickListener(this);

        if (localname != null) { // 设置Title
            txtTitle.setText(localname);
        } else {
            txtTitle.setText("查看内容");
        }
    }

    private void getJson() {
        progressdialogshow(WitContentActivity.this);
        StringRequest strinrequest = new StringRequest(Method.POST, URL, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonobj;
                WitContent witcontent;
                try {
                    jsonobj = new JSONObject(response);
                    int result = jsonobj.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonobj.getJSONArray("data");
                        jsonobj = data.getJSONObject(0);
                        witcontent = new WitContent();
                        witcontent.subname = jsonobj.getString("subname");
                        witcontent.subimg = jsonobj.getString("subimg");
                        witcontent.answerUrl = jsonobj.getString("answerUrl");
                        localanswerUrl = witcontent.answerUrl; // 拿到了答案地址
                        localimgUrl = witcontent.subimg; // 拿到了图片地址
                        txtContent.setText(witcontent.subname);
                        progressdialogcancel();
                    } else {
                        LogUtil.e("Code Wrong", "请求失败 =" + response);
                    }
                    showImage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Request Wrong", "请求失败 =" + error);
                progressdialogcancel();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                if (localsubid == null) {
                    hashMap.put("subCategoryid", localsubCategoryid);
                } else {
                    hashMap.put("subid", localsubid);
                }
                return hashMap;
            }
        };
        strinrequest.setTag("witcontent");
        AppContext.getHttpQueue().add(strinrequest);
    }

    private void showImage() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(localimgUrl, photoView, options);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.button_witcontent_back:
                finish();
                break;
            case R.id.btn_witcontent_toAnswer:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("URL", localanswerUrl);
                intent.putExtra("CODE", "witcontent");
                intent.putExtra("TITLE", "写作答案");
                intent.putExtra("COLOR", "#9f6eaf");
                startActivity(intent);
                break;
        }
    }
}

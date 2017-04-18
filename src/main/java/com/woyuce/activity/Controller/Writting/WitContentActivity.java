package com.woyuce.activity.Controller.Writting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.WebActivity;
import com.woyuce.activity.Model.Writting.WitContent;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.co.senab.photoview.PhotoView;

public class WitContentActivity extends BaseActivity implements OnClickListener {

    private TextView txtContent, txtTitle;
    private Button btnBack, btnToAnswer;
    private ImageView mImgview;
    private PhotoView photoView;

    //    private String URL = "http://iphone.ipredicting.com/xzsubContent.aspx";
    private String localsubCategoryid, localname, localsubid, localimgUrl, localanswerUrl;

    @Override
    protected void onStop() {
        super.onStop();
//        AppContext.getHttpQueue().cancelAll("witcontent");
        HttpUtil.removeTag(Constants.ACTIVITY_WIT_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writting_content);

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
        HashMap<String, String> params = new HashMap<>();
        if (localsubid == null) {
            params.put("subCategoryid", localsubCategoryid);
        } else {
            params.put("subid", localsubid);
        }
        HttpUtil.post(Constants.URL_POST_WRITTING_CONTENT, params, Constants.ACTIVITY_WIT_CONTENT, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                LogUtil.e("getNoticeJson = ");
                try {
                    JSONObject jsonobj;
                    WitContent witcontent;
                    jsonobj = new JSONObject(result);
                    if (jsonobj.getInt("code") == 0) {
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
                    }
                    showImage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showImage() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_loading)
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
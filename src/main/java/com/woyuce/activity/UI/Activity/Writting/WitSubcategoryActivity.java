package com.woyuce.activity.UI.Activity.Writting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Writting.WitSubcategoryAdapter;
import com.woyuce.activity.Adapter.Writting.WitspnAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Writting.WitCategory;
import com.woyuce.activity.Bean.Writting.WitSubcategory;
import com.woyuce.activity.R;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class WitSubcategoryActivity extends BaseActivity
        implements OnClickListener, OnItemClickListener, OnItemSelectedListener {

    private TextView txtTitle;
    private AutoCompleteTextView autoTxt;
    private ImageView imgBack, mGuidemap;
    private Button btnSearch;
    private GridView gridView;
    private Spinner spnCategory;

    private List<WitSubcategory> witsubcategoryList = new ArrayList<>();
    private List<WitCategory> witcategoryList = new ArrayList<>();
    private String localid, localname;
    private String localsubCategoryid;
    private boolean isfirst = true;

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_WIT_SUBCATEGORY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witsubcategory);

        initView();
        initGuidemap();
        getSpinnerJson();
        getJson();
    }

    private void initView() {
        Intent it_witSubcategory = getIntent();
        localid = it_witSubcategory.getStringExtra("localid");
        localname = it_witSubcategory.getStringExtra("localname");

        txtTitle = (TextView) findViewById(R.id.txt_witsubcategory_title);
        autoTxt = (AutoCompleteTextView) findViewById(R.id.auto_witsubcategory);
        imgBack = (ImageView) findViewById(R.id.arrow_back);
        mGuidemap = (ImageView) findViewById(R.id.img_witsubcategory_guidemap);
        btnSearch = (Button) findViewById(R.id.btn_witsubcategory_search);
        gridView = (GridView) findViewById(R.id.gridview_witsubCategory);
        spnCategory = (Spinner) findViewById(R.id.spn_witsubcategory);

        txtTitle.setText(localname);
        btnSearch.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
    }

    private void setspnDate() {
        WitspnAdapter spnadapter = new WitspnAdapter(this, witcategoryList);
        spnCategory.setAdapter(spnadapter);
        spnCategory.setOnItemSelectedListener(this);
    }

    private void initGuidemap() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(Constants.URL_GUIDE_IMG_WRITTING, mGuidemap, options);
    }

    private void getSpinnerJson() {
        OkGo.post(Constants.URL_POST_WRITTING_TOTAL).tag(Constants.ACTIVITY_WIT_SUBCATEGORY)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        doSpannerSuccess(s);
                    }
                });
    }

    private void doSpannerSuccess(String response) {
        JSONObject jsonobj;
        WitCategory witcategory;
        try {
            jsonobj = new JSONObject(response);
            int result = jsonobj.getInt("code");
            if (result == 0) {
                JSONArray data = jsonobj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonobj = data.getJSONObject(i);
                    witcategory = new WitCategory();
                    witcategory.name = jsonobj.getString("name");
                    witcategory.id = jsonobj.getString("id");
                    witcategoryList.add(witcategory);
                }
            }
            setspnDate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJson() {
        HttpParams params = new HttpParams();
        params.put("categoryid", localid);
        OkGo.post(Constants.URL_POST_WRITTING_SUBCATEGORY).tag(Constants.ACTIVITY_WIT_SUBCATEGORY).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
        JSONObject jsonobj;
        WitSubcategory witsubcategory;
        try {
            jsonobj = new JSONObject(response);
            int result = jsonobj.getInt("code");
            if (result == 0) {
                JSONArray data = jsonobj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonobj = data.getJSONObject(i);
                    witsubcategory = new WitSubcategory();
                    witsubcategory.bgColor = jsonobj.getString("bgColor").trim();
                    witsubcategory.name = jsonobj.getString("name");
                    witsubcategory.subCategoryid = jsonobj.getString("subCategoryid");
                    witsubcategoryList.add(witsubcategory);
                }
            }
            WitSubcategoryAdapter adapter = new WitSubcategoryAdapter(WitSubcategoryActivity.this,
                    witsubcategoryList);
            gridView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.btn_witsubcategory_search:
                String localkey = autoTxt.getText().toString();
                Intent it_search = new Intent(this, WitSearchActivity.class);
                it_search.putExtra("localid", localid);
                it_search.putExtra("localkey", localkey);
                startActivity(it_search);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WitSubcategory witsubcategory = witsubcategoryList.get(position);
        localsubCategoryid = witsubcategory.subCategoryid;
        String localname = witsubcategory.name;
        Intent intent = new Intent(this, WitContentActivity.class);
        intent.putExtra("localsubCategoryid", localsubCategoryid);
        intent.putExtra("localname", localname);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isfirst == true) {
            isfirst = false;
        } else {
            WitCategory witcategory = witcategoryList.get(position);
            localid = witcategory.id;
            localname = witcategory.name;
            txtTitle.setText(localname);
            witsubcategoryList.clear();
            getJson();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
package com.woyuce.activity.Controller.Writting;

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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Writting.WitSubcategoryAdapter;
import com.woyuce.activity.Adapter.Writting.WitspnAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Writting.WitCategory;
import com.woyuce.activity.Model.Writting.WitSubcategory;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private boolean isfirst = true;

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_WIT_SUBCATEGORY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writting_category);

        initView();
        initGuidemap();
        getSpinnerJson();
        requestListJson();
    }

    private void initView() {
        localid = getIntent().getStringExtra("localid");
        localname = getIntent().getStringExtra("localname");

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

    private void seSpinnerData() {
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
        HttpUtil.get(Constants.URL_POST_WRITTING_TOTAL, Constants.ACTIVITY_WIT_SUBCATEGORY, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonobj;
                    WitCategory witcategory;
                    jsonobj = new JSONObject(result);
                    if (jsonobj.getInt("code") == 0) {
                        JSONArray data = jsonobj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonobj = data.getJSONObject(i);
                            witcategory = new WitCategory();
                            witcategory.name = jsonobj.getString("name");
                            witcategory.id = jsonobj.getString("id");
                            witcategoryList.add(witcategory);
                        }
                    }
                    seSpinnerData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestListJson() {
        HashMap<String, String> params = new HashMap<>();
        params.put("categoryid", localid);
        HttpUtil.post(Constants.URL_POST_WRITTING_SUBCATEGORY, params, Constants.ACTIVITY_WIT_SUBCATEGORY, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonobj;
                    WitSubcategory witsubcategory;
                    jsonobj = new JSONObject(result);
                    if (jsonobj.getInt("code") == 0) {
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
                    WitSubcategoryAdapter adapter = new WitSubcategoryAdapter(WitSubcategoryActivity.this, witsubcategoryList);
                    gridView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.btn_witsubcategory_search:
                Intent intent = new Intent(this, WitSearchActivity.class);
                intent.putExtra("localid", localid);
                intent.putExtra("localkey", autoTxt.getText().toString());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, WitContentActivity.class);
        intent.putExtra("localsubCategoryid", witsubcategoryList.get(position).subCategoryid);
        intent.putExtra("localname", witsubcategoryList.get(position).name);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isfirst) {
            isfirst = false;
        } else {
            localid = witcategoryList.get(position).id;
            localname = witcategoryList.get(position).name;
            txtTitle.setText(localname);
            witsubcategoryList.clear();
            requestListJson();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
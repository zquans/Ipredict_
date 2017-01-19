package com.woyuce.activity.UI.Activity.Writting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.woyuce.activity.Adapter.Writting.WittingAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Writting.WitCategory;
import com.woyuce.activity.R;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

public class WitActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private GridView gvCategory;
    private ImageView imgBack;
    private Button btnSearch;
    private AutoCompleteTextView autoTxt;

    private String localid, localname, localkey;
    private ArrayList<WitCategory> witcategoryList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_WIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witting);

        initView();
        getJson();
    }

    private void initView() {
        gvCategory = (GridView) findViewById(R.id.gridview_writting);
        imgBack = (ImageView) findViewById(R.id.arrow_back);
        btnSearch = (Button) findViewById(R.id.btn_writting_search);
        autoTxt = (AutoCompleteTextView) findViewById(R.id.auto_writting);

        imgBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        gvCategory.setOnItemClickListener(this);
    }

    private void getJson() {
        OkGo.get(Constants.URL_POST_WRITTING_CATEGORY).tag(Constants.ACTIVITY_WIT)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
        WitCategory witcategory;
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            int result = obj.getInt("code");
            if (result == 0) {
                JSONArray data = obj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    obj = data.getJSONObject(i);
                    witcategory = new WitCategory();
                    witcategory.name = obj.getString("name");
                    witcategory.id = obj.getString("id");
                    witcategoryList.add(witcategory);
                }
            }
            WittingAdapter adapter = new WittingAdapter(WitActivity.this, witcategoryList);
            gvCategory.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_writting_search:
                localkey = autoTxt.getText().toString();
                Intent intent = new Intent(this, WitSearchActivity.class);
                intent.putExtra("localkey", localkey);
                startActivity(intent);
                break;
            case R.id.arrow_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WitCategory witcategory = witcategoryList.get(position);
        localid = witcategory.id;
        localname = witcategory.name;
        Intent it_witSubcategory = new Intent(this, WitSubcategoryActivity.class);
        it_witSubcategory.putExtra("localid", localid);
        it_witSubcategory.putExtra("localname", localname);
        startActivity(it_witSubcategory);
    }
}
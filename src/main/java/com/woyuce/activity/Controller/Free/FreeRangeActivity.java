package com.woyuce.activity.Controller.Free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Free.FreeRangeAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Free.FreeRange;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FreeRangeActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    private ImageView mBack, mGuide;
    private Button mBtnClear;
    private ListView mListView;

    private String localtoken;
    private List<FreeRange> rangeList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_RANGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_range);

        initView();
        requestData();
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.arrow_back);
        mGuide = (ImageView) findViewById(R.id.img_range_guide);
        mBtnClear = (Button) findViewById(R.id.btn_range_clearcache);
        mListView = (ListView) findViewById(R.id.listview_activity_rang);

        mBack.setOnClickListener(this);
        mGuide.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mListView.setOnItemClickListener(this);

        localtoken = PreferenceUtil.getSharePre(FreeRangeActivity.this).getString("localtoken", "");
        // 第一次引导的动画
        boolean b = PreferenceUtil.getSharePre(FreeRangeActivity.this).contains("imgclearguide");
        if (b) {
            mGuide.setVisibility(View.GONE);
        }
    }

    private void requestData() {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HttpUtil.post(Constants.URL_POST_FREE_RANGE, headers, null, Constants.ACTIVITY_RANGE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                onSuccess(result);
            }
        });
    }

    /**
     * 请求成功后调用
     */
    private void onSuccess(String response) {
        try {
            JSONObject jsonObject;
            FreeRange range;
            jsonObject = new JSONObject(response);
            int result = jsonObject.getInt("code");
            if (result == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    range = new FreeRange();
                    range.setId(jsonObject.getString("month_id"));
                    range.setTitle(jsonObject.getString("title"));
                    rangeList.add(range);
                }
            }
            // 第二步，将数据放到适配器中
            FreeRangeAdapter adapter = new FreeRangeAdapter(FreeRangeActivity.this, rangeList);
            mListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressdialogcancel();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, FreeLessonActivity.class);
        intent.putExtra("FreeRange", rangeList.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_range_clearcache:
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                ToastUtil.showMessage(FreeRangeActivity.this, "清除缓存,更新书籍成功");
                break;
            case R.id.img_range_guide:
                mGuide.setVisibility(View.GONE);
                PreferenceUtil.save(FreeRangeActivity.this, "imgclearguide", "guided");
                break;
            default:
                finish();
                break;
        }
    }
}
package com.woyuce.activity.Controller.Free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Free.FreeBookAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Free.FreeBook;
import com.woyuce.activity.Model.Free.FreeLesson;
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

/**
 * Created by Administrator on 2016/9/21
 */
public class FreeBookActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TextView mTitle;
    private Button mBtnClearcache;
    private ImageView mBack;
    private GridView mGridView;

    private FreeLesson mFreeLesson;
    private String localtoken, localMid;
    private List<FreeBook> bookList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_BOOK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_book);

        initView();
        requestData();
    }

    private void initView() {
        localtoken = PreferenceUtil.getSharePre(FreeBookActivity.this).getString("localtoken", "");
        localMid = getIntent().getStringExtra("localMid");
        mFreeLesson = (FreeLesson) getIntent().getSerializableExtra("FreeLesson");

        mTitle = (TextView) findViewById(R.id.txt_book_typeName);
        mBack = (ImageView) findViewById(R.id.arrow_back);
        mBtnClearcache = (Button) findViewById(R.id.btn_book_clearcache);
        mGridView = (GridView) findViewById(R.id.gridview_book);

        mBack.setOnClickListener(this);
        mBtnClearcache.setOnClickListener(this);
        mGridView.setOnItemClickListener(this);

        mTitle.setText(mFreeLesson.title);
    }

    private void requestData() {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("month_id", localMid);
        params.put("user_power_type_id", mFreeLesson.user_power_type_id);
        params.put("type_id", mFreeLesson.type_id);
        HttpUtil.post(Constants.URL_POST_FREE_BOOK, headers, params, Constants.ACTIVITY_BOOK, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                onSuccess(result);
            }
        });
    }

    private void onSuccess(String response) {
        try {
            JSONObject jsonObject;
            FreeBook book;
            jsonObject = new JSONObject(response);
            if (jsonObject.getInt("code") == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    book = new FreeBook();
                    book.unit_name = jsonObject.getString("unit_name");
                    book.unit_id = jsonObject.getString("unit_id");
                    book.show_type_id = jsonObject.getString("show_type_id");
                    book.img_path = jsonObject.getString("img_path");
                    bookList.add(book);
                }
            }
            // 第二步，将数据放到适配器中
            FreeBookAdapter adapter = new FreeBookAdapter(FreeBookActivity.this, bookList);
            mGridView.setAdapter(adapter);
            progressdialogcancel();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, FreePageActivity.class);
        intent.putExtra("FreeBook", bookList.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_book_clearcache:
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                ToastUtil.showMessage(FreeBookActivity.this, "清除缓存,更新书籍成功");
                break;
            default:
                finish();
                break;
        }
    }
}
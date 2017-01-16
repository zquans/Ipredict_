package com.woyuce.activity.UI.Activity.Free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Adapter.Free.FreeBookAdapter;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.Bean.Free.FreeBook;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/21.
 */
public class FreeBookActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TextView mTitle;
    private Button mBtnClearcache;
    private ImageView mBack;
    private GridView mGridView;

    private String URL = "http://api.iyuce.com/v1/exam/freeexamunits";
    private String localtoken, localtitle, localMid, localPid, localtypeid;
    private List<FreeBook> bookList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("book");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freebook);

        initView();
        getJson();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        localtitle = bundle.getString("localtitle");
        localMid = bundle.getString("localMid");
        localPid = bundle.getString("localPid");
        localtypeid = bundle.getString("localtypeid");

        mTitle = (TextView) findViewById(R.id.txt_book_typeName);
        mBack = (ImageView) findViewById(R.id.arrow_back);
        mBtnClearcache = (Button) findViewById(R.id.btn_book_clearcache);
        mGridView = (GridView) findViewById(R.id.gridview_book);

        mBack.setOnClickListener(this);
        mBtnClearcache.setOnClickListener(this);
        mGridView.setOnItemClickListener(this);

        mTitle.setText(localtitle);
    }

    private void getJson() {
        progressdialogshow(this);
        StringRequest strinRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                FreeBook book;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
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
                    } else {
                        LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + jsonObject.getString("message"));
                    }
                    // 第二步，将数据放到适配器中
                    FreeBookAdapter adapter = new FreeBookAdapter(FreeBookActivity.this, bookList);
                    mGridView.setAdapter(adapter);
                    progressdialogcancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.e("DATA-BACK", "JSON接口返回的信息： " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong_BACK", "联接错误原因： " + error.getMessage());
                progressdialogcancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                localtoken = PreferenceUtil.getSharePre(FreeBookActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("month_id", localMid);
                map.put("user_power_type_id", localPid);
                map.put("type_id", localtypeid);
                return map;
            }
        };
        strinRequest.setTag("book");
        AppContext.getHttpQueue().add(strinRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FreeBook book = bookList.get(position);
        String localunit_name = book.unit_name;
        String localunit_id = book.unit_id;
        String localshow_type_id = book.show_type_id;
        Intent intent = new Intent(this, FreePageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("localunit_name", localunit_name);
        bundle.putString("localunit_id", localunit_id);
        bundle.putString("localshow_type_id", localshow_type_id);
        intent.putExtras(bundle);
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
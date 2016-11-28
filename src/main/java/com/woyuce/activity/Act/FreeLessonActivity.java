package com.woyuce.activity.Act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.woyuce.activity.Adapter.FreeLessonAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.FreeLesson;
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
public class FreeLessonActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TextView mTitle;
    private Button mBtnClearcache;
    private ImageView mBack;
    private GridView mGridView;

    private String URL = "http://api.iyuce.com/v1/exam/freeexamtype";
    private String localtoken, localMonthid, localTitle;
    private List<FreeLesson> lessonList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("lesson");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_freelesson);

        initView();
        getJson();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        localMonthid = bundle.getString("localMonthid");
        localTitle = bundle.getString("localTitle");

        mTitle = (TextView) findViewById(R.id.txt_lesson_title);
        mBack = (ImageView) findViewById(R.id.arrow_back);
        mBtnClearcache = (Button) findViewById(R.id.btn_lesson_clearcache);
        mGridView = (GridView) findViewById(R.id.gridview_lesson);

        mBack.setOnClickListener(this);
        mBtnClearcache.setOnClickListener(this);
        mGridView.setOnItemClickListener(this);
        mTitle.setText(localTitle);
    }

    // 请求接口
    private void getJson() {
        progressdialogshow(this);
        StringRequest lessonRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                doSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-BACK", "连接错误原因： " + error.getMessage() + error);
                progressdialogcancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(FreeLessonActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("", localMonthid);
                return map;
            }
        };
        lessonRequest.setTag("lesson");
        AppContext.getHttpQueue().add(lessonRequest);
    }

    /**
     * 请求成功后执行
     *
     * @param response
     */
    private void doSuccess(String response) {
        JSONObject jsonObject;
        FreeLesson lesson;
        try {
            LogUtil.i("response = " + response.toString());
            jsonObject = new JSONObject(response);
            int result = jsonObject.getInt("code");
            if (result == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    lesson = new FreeLesson();
                    lesson.title = jsonObject.getString("title");
                    lesson.user_power_type_id = jsonObject.getString("user_power_type_id");
                    lesson.image = jsonObject.getString("image");
                    lesson.type_id = jsonObject.getString("type_id");
                    lessonList.add(lesson);
                }
            } else {
                LogUtil.e("code!=0 DATA_BACK", "读取页面失败： " + jsonObject.getString("message"));
            }
            // 第二步，将数据放到适配器中
            FreeLessonAdapter adapter = new FreeLessonAdapter(FreeLessonActivity.this, lessonList);
            mGridView.setAdapter(adapter);
            progressdialogcancel();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FreeLesson lesson = lessonList.get(position);
        String localPid = lesson.user_power_type_id;
        String localtitle = lesson.title;
        String localtypeid = lesson.type_id;
        Intent intent = new Intent(this, FreeBookActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("localMid", localMonthid);
        bundle.putString("localPid", localPid);
        bundle.putString("localtitle", localtitle);
        bundle.putString("localtypeid", localtypeid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lesson_clearcache:
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                ToastUtil.showMessage(FreeLessonActivity.this, "清除缓存,更新书籍成功");
                break;
            default:
                finish();
                break;
        }
    }
}
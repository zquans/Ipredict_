package com.woyuce.activity.UI.Activity.Free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Free.FreeLessonAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Free.FreeLesson;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/21.
 */
public class FreeLessonActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TextView mTitle;
    private Button mBtnClearcache;
    private ImageView mBack;
    private GridView mGridView;

    private String localtoken, localMonthid, localTitle;
    private List<FreeLesson> lessonList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_LESSON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        localtoken = PreferenceUtil.getSharePre(this).getString("localtoken", "");
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        params.put("", localMonthid);

        OkGo.post(Constants.URL_POST_FREE_LESSON).tag(Constants.ACTIVITY_LESSON).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
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
package com.woyuce.activity.Act;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/23.
 */
public class SuggestionActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtBack;
    private EditText edt;
    private Button btnSure;

    private String mContent;
    private String URl = "http://api.iyuce.com/v1/service/feedback";

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("suggestion");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        initView();
    }

    private void initView() {
        txtBack = (TextView) findViewById(R.id.txt_suggestion_back);
        edt = (EditText) findViewById(R.id.edt_suggestion);
        btnSure = (Button) findViewById(R.id.btn_suggestion_sure);

        txtBack.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    /**
     * 返回日期字符串
     *
     * @return
     */
    public String getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String nowtime = formatter.format(currentTime);
        return nowtime;
    }

    private void getJson() {
        StringRequest aboutusrequest = new StringRequest(Request.Method.POST, URl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("suggest activty is here" + getNowDate());
                ToastUtil.showMessage(SuggestionActivity.this, "亲，提交成功啦，感谢您的宝贵意见");
            }
        }, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put("user_id", PreferenceUtil.getSharePre(SuggestionActivity.this).getString("userId", ""));
                hashmap.put("createat", getNowDate());
                hashmap.put("content", mContent);
                return hashmap;
            }
        };
        aboutusrequest.setTag("suggestion");
        AppContext.getHttpQueue().add(aboutusrequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_suggestion_back:
                finish();
                break;
            case R.id.btn_suggestion_sure:
                mContent = edt.getText().toString();
                new AlertDialog.Builder(SuggestionActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("填好了")
                        .setNegativeButton("马上反馈", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getJson();
                            }
                        }).setPositiveButton("再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SuggestionActivity.this.finish();
                    }
                }).show();
                break;
        }
    }
}
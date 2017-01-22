package com.woyuce.activity.UI.Activity.Common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/23.
 */
public class SuggestionActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtBack;
    private EditText edt;
    private Button btnSure;

    private String mContent;

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_SUGGESTION);
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
        HttpParams params = new HttpParams();
        params.put("user_id", PreferenceUtil.getSharePre(SuggestionActivity.this).getString("userId", ""));
        params.put("createat", getNowDate());
        params.put("content", mContent);
        OkGo.post(Constants.URL_POST_SUGGESTION).tag(Constants.ACTIVITY_SUGGESTION).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        LogUtil.e("suggest activty is here" + getNowDate());
                        ToastUtil.showMessage(SuggestionActivity.this, "亲，提交成功啦，感谢您的宝贵意见");
                    }
                });
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
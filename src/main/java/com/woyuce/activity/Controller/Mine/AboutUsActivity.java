package com.woyuce.activity.Controller.Mine;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

/**
 * Created by Administrator on 2016/9/23.
 */
public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initEvent();
    }

    public void back(View view) {
        finish();
    }

    /* 两种方式判断最大允许运行内存 */
    private void initEvent() {
        ActivityManager activityManager = (ActivityManager) AboutUsActivity.this
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryClass();
        activityManager.getLargeMemoryClass();
        LogUtil.e("ActivityManager方式获取", "memory = " + activityManager.getMemoryClass() + ",largememory = "
                + activityManager.getLargeMemoryClass());

        Runtime.getRuntime().maxMemory();
        LogUtil.e("Runtime获取", "runtimememory = " + Runtime.getRuntime().maxMemory() + ", 即"
                + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M");
    }

}
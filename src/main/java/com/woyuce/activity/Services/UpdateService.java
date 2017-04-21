package com.woyuce.activity.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.UpdateManager;

/**
 * Created by Administrator on 2016/9/26
 */
public class UpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.e("onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("onStartCommand");

        // 自动检测升级
        new UpdateManager(UpdateService.this).checkUpdate();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.e("onDestroy");
        super.onDestroy();
    }
}

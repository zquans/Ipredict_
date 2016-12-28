package com.woyuce.activity.Act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.umeng.analytics.MobclickAgent;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

/**
 * Created by Administrator on 2016/9/20.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private ProgressDialog mProgressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); // getActiveNetworkInfo获取当前可用网络
        if (networkInfo == null || !networkInfo.isAvailable()) {
            ToastUtil.showMessage(this, "网络链接不可用，请检查网络");
        } else {
            // toast("链接成功");
        }
    }

    /**
     * 判断是否拥有权限
     *
     * @param permissions
     * @return
     */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /**
     * 请求权限
     */
    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
        ToastUtil.showMessage(this, "如果拒绝存储授权,会导致应用无法正常使用", 8000);
    }

    /**
     * 请求权限的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.showMessage(this, "现在您拥有了存储权限");
                    // 自动检测更新
                    doUpdate();
                } else {
                    ToastUtil.showMessage(this, "您拒绝存储授权,会导致应用无法正常使用，可以在系统设置中重新开启权限", 8000);
                }
                break;
            case Constants.CODE_READ_EXTERNAL_STORAGE:
                break;
        }
    }

    //子类重写后实现具体调用相机的业务逻辑
    public void doUpdate() {
    }

    private void show() {
        mProgressdialog.setTitle("加载中，请稍候");
        mProgressdialog.setMessage("Loading...");
        mProgressdialog.setCanceledOnTouchOutside(false);
//        mProgressdialog.setCancelable(false);
        mProgressdialog.show();
    }

    public ProgressDialog progressdialogshow(Context context) {
        if (mProgressdialog == null) {
            mProgressdialog = new ProgressDialog(context);
            show();
            return mProgressdialog;
        }
        show();
        return mProgressdialog;
    }

    public void progressdialogcancel() {
        mProgressdialog.cancel();
    }
}
package com.woyuce.activity.Act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.woyuce.activity.Utils.ActivityManager;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

/**
 * Created by Administrator on 2016/9/20.
 */
public class BaseActivity extends Activity {

    private GestureDetector mGestureDetector;

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
        //一、配置沉浸式状态栏
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        //二、判断是否WIFI环境
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); // getActiveNetworkInfo获取当前可用网络
        if (networkInfo == null || !networkInfo.isAvailable()) {
            ToastUtil.showMessage(this, "网络链接不可用，请检查网络");
        } else {
            // toast("链接成功");
        }
        //三、将Activity装入管理栈中，以便管理
        ActivityManager.getAppManager().addActivity(this);

        //四、手势监听，做左侧右滑退出
        mGestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                LogUtil.i("onDown ");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                LogUtil.i("onShowPress ");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                LogUtil.i("onSingleTapUp ");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                LogUtil.i("onScroll distanceX = " + distanceX);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                LogUtil.i("onLongPress ");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                LogUtil.i("onFling  velocityX  = " + velocityX);
                if (velocityX > 200) {
                    BaseActivity.this.finish();
                }
                return true;
            }
        });
        LogUtil.i(this.getClass().getSimpleName());
    }

    /**
     * 将onTouchEvent改为自定义的手势监听
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
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

    /**
     * 单例模式
     *
     * @param context
     * @return
     */
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
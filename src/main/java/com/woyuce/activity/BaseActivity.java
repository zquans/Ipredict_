package com.woyuce.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Utils.ActivityManager;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.NetUtil;
import com.woyuce.activity.Utils.ToastUtil;

/**
 * Created by Administrator on 2016/9/20
 */
public class BaseActivity extends Activity {

//    private GestureDetector mGestureDetector;

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

        //日志输出Activity名称，方便查找
        LogUtil.i(this.getClass().getSimpleName());

        //一、配置沉浸式状态栏
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        //二、判断是否WIFI环境
        //网络已连接
        if (NetUtil.isConnected(this)) {
            //但不是WIFI
//            if (!NetUtil.isWifi(this)) {
//                ToastUtil.showMessage(this, "当前不在WIFI环境，下载将消耗较多流量");
//            }
        } else {
            ToastUtil.showMessage(this, "网络链接不可用，请检查网络");
        }
        //三、将Activity装入管理栈中，以便管理
        ActivityManager.getAppManager().addActivity(this);

        //四、手势监听，做左侧右滑退出
//        mGestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e) {
//                LogUtil.i("onDown ");
//                return false;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent e) {
//                LogUtil.i("onShowPress ");
//            }
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                LogUtil.i("onSingleTapUp ");
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
////                LogUtil.i("onScroll distanceX = " + distanceX);
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                LogUtil.i("onLongPress ");
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
////                LogUtil.i("onFling  velocityX  = " + velocityX);
//                if (velocityX > 200) {
////                    BaseActivity.this.finish();
//                }
//                return true;
//            }
//        });
    }

    /**
     * 将onTouchEvent改为自定义的手势监听
     *
     * @param event
     * @return
     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return mGestureDetector.onTouchEvent(event);
//    }

    /**
     * 判断是否拥有权限
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
        ToastUtil.showMessage(this, "如果拒绝存储授权,会导致应用无法正常使用", Toast.LENGTH_LONG);
    }

    /**
     * 请求权限的回调
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
                    ToastUtil.showMessage(this, "您拒绝存储授权,会导致应用无法正常使用，可以在系统设置中重新开启权限", Toast.LENGTH_LONG);
                }
                break;
            case Constants.CODE_READ_EXTERNAL_STORAGE:
                break;
        }
    }

    /**
     * 子类重写后实现具体的业务逻辑
     */
    public void doUpdate() {
    }

    /**
     * 默认定义的progressDialog的show方法,给子类用
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

    /**
     * show方法,给子类用
     */
    private void show() {
        mProgressdialog.setTitle("加载中，请稍候");
        mProgressdialog.setMessage("Loading...");
        mProgressdialog.setCanceledOnTouchOutside(false);
//        mProgressdialog.setCancelable(false);
        mProgressdialog.show();
    }

    /**
     * 默认定义的progressDialog的cancel方法,给子类用
     */
    public void progressdialogcancel() {
        if (mProgressdialog != null) {
            mProgressdialog.cancel();
        }
    }
}
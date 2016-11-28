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
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.umeng.analytics.MobclickAgent;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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

    public ProgressDialog progressdialog;

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

    public void progressdialogshow(Context context) {
        progressdialog = new ProgressDialog(context);
        progressdialog.setTitle("加载中，请稍候");
        progressdialog.setMessage("Loading...");
        progressdialog.setCanceledOnTouchOutside(false);
//         progressdialog.setCancelable(false);
        progressdialog.show();
    }

    public void progressdialogcancel() {
        progressdialog.cancel();
    }

    //先获取token，token用于此后每一次接口请求的参数
    public void getBaseToken() {
        StringRequest tokenrequest = new StringRequest(Request.Method.POST, Constants.URL_API_REQUESTTOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String localtoken = obj.getString("access_token");
//							String token_expires = obj.getString("expires_in");
                            PreferenceUtil.save(BaseActivity.this, "localtoken", localtoken);
                            LogUtil.e("localtoken1 = " + localtoken);
//							PreferenceUtil.save(BaseActivity.this, "token_expires", token_expires);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getBaseToken();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String base64EncodedString = null;
                try {
                    String encodedConsumerKey = URLEncoder.encode("defA8Dq2ambB", "UTF-8");
                    String encodedConsumerSecret = URLEncoder.encode("WM7Ei5mzrrHl42HHXuGkNR0bVJexq4P", "UTF-8");
                    String authString = encodedConsumerKey + ":" + encodedConsumerSecret;
                    base64EncodedString = Base64.encodeToString(authString.getBytes("UTF-8"), Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + base64EncodedString);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("grant_type", "client_credentials");
                headers.put("scope", "");
                return headers;
            }
        };
        tokenrequest.setTag("base");
        AppContext.getHttpQueue().add(tokenrequest);
    }
}

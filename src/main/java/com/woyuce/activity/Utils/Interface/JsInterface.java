package com.woyuce.activity.Utils.Interface;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.ToastUtil;

/**
 * Created by Administrator on 2016/12/22.
 */
//虽然命名为Interface，其实是伪接口，主要是为了方便理解及以后做抽象处理
public class JsInterface {

    private Context mContext;

    public JsInterface(Context context) {
        this.mContext = context;
    }

    // js调用java
    @JavascriptInterface
    public void openExternalApp(final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
//                String url = "alipays://";  //支付宝
//                String url = "weixin://";   //微信
//                String url = "mqqwpa://";   //腾讯QQ

                //主线程更新UI
                LogUtil.i("JS 调用了 JAVA --->" + url);
                //判断微信
                ToDoOrNotToDoJs(url, "weixin", "com.tencent.mm", "微信");
                //判断QQ
                ToDoOrNotToDoJs(url, "mqqwpa", "com.tencent.mobileqq", "QQ");
//                判断支付宝
//                ToDoOrNotToDoJs(url, "alipays", "com.eg.android.AlipayGphone", "支付宝");
            }
        });
    }

    /**
     * 判断是否去做JS需要调用的操作
     *
     * @param url
     * @param url_head
     * @param package_name
     * @param toast_content
     */
    private void ToDoOrNotToDoJs(String url, String url_head, String package_name, String toast_content) {
        if (url.contains(url_head)) {
            boolean isExist = isApkInstalled(mContext, package_name);
            if (!isExist) {
                ToastUtil.showMessage(mContext, "亲,请先安装" + toast_content);
            } else {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        }
    }

    /**
     * packageName="com.tencent.mobileqq" QQ包名
     * "com.tencent.mm" 微信包名
     *
     * @param context
     * @param packageName
     * @return
     */
    public static final boolean isApkInstalled(Context context, String packageName) { // 该方法判断手机中是否有对应应用包
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
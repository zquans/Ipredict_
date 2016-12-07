package com.woyuce.activity.Application;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.woyuce.activity.Act.LoginActivity;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import java.io.File;
import java.util.Map;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @author linjizong
 * @created 2015-3-22
 */
public class AppContext extends Application {
    private static final String TAG = AppContext.class.getSimpleName();
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private static String DEVICE_TOKEN;

    //singleton
    private static AppContext appContext = null;
    private Display display;

    private static RequestQueue mQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        mQueue = Volley.newRequestQueue(getApplicationContext());
        init();

        PushAgent mPushAgent = PushAgent.getInstance(this);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法(透传)，用户无感知
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                LogUtil.i("linx", "dealWithCustomMessage----msg:" + msg.custom);
                if (msg.custom.equals(PreferenceUtil.getSharePre(context).getString("userId", ""))) {
                    Intent startLogin = new Intent(context, LoginActivity.class);
                    startLogin.putExtra("local_push_code", msg.custom);
                    startLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(startLogin);
                }
                //自定义参数
                LogUtil.i("linx", "dealWithCustomMessage----extra:" + msg.extra);
                for (Map.Entry<String, String> entry : msg.extra.entrySet()) {
                    //键值自己拼装，2016/12/07全蛋说只会传一组数据key不变，所以我只拿了value
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value.equals(PreferenceUtil.getSharePre(context).getString("userId", ""))) {
                        Intent startLogin = new Intent(context, LoginActivity.class);
                        startLogin.putExtra("local_push_code", value);
                        startLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startLogin);
                    }
                }
//                boolean isClickOrDismissed = true;
//                if (isClickOrDismissed) {
//                    //自定义消息的点击统计
//                    UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
//                } else {
//                    //自定义消息的忽略统计
//                    UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
//                }
            }

            /**
             * 当消息推送到用户通知栏时回调
             */
            @Override
            public Notification getNotification(Context context, UMessage uMessage) {
                LogUtil.i("linx", "getNotification----extra:" + uMessage.extra);
                for (Map.Entry<String, String> entry : uMessage.extra.entrySet()) {
                    //键值自己拼装，2016/12/07全蛋说只会传一组数据key不变，所以我只拿了value
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value.equals(PreferenceUtil.getSharePre(context).getString("userId", ""))) {
                        Intent startLogin = new Intent(context, LoginActivity.class);
                        startLogin.putExtra("local_push_code", value);
                        startLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startLogin);
                    }
                }
                return super.getNotification(context, uMessage);
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 当用户点击收到的通知时回调
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(final Context context, UMessage msg) {
                //TODO 接收到推送后自定义打开的界面或者其他
//                ToastUtil.showMessage(context, "收到通知：" + msg.custom + "||" + msg.extra + ",并执行相应操作");
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                LogUtil.i("Appcontext deviceToken = " + deviceToken);
                DEVICE_TOKEN = deviceToken;
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    public static AppContext getInstance() {
        return appContext;
    }

    public static RequestQueue getHttpQueue() {
        return mQueue;
    }

    /**
     * 初始化
     */
    private void init() {
        //本地图片辅助类初始化(因为6.0后动态权限的原因，将初始化延迟放在MainActivity中做，否则应用无权限时，一打开就崩溃)
//        LocalImageHelper.init(this);

        initImageLoader(getApplicationContext());
        if (display == null) {
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
        }
    }


    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCacheSize((int) Runtime.getRuntime().maxMemory() / 4);
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        //修改连接超时时间5秒，下载超时时间5秒
        config.imageDownloader(new BaseImageDownloader(appContext, 5 * 1000, 5 * 1000));
        //		config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public String getCachePath() {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = getExternalCacheDir();
        else
            cacheDir = getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }

    /**
     * 返回DEVICE_TOKEN
     */
    public static String getDeviceToken() {
        return DEVICE_TOKEN;
    }

    /**
     * @return
     * @Description： 获取当前屏幕的宽度
     */
    public int getWindowWidth() {
        return display.getWidth();
    }

    /**
     * @return
     * @Description： 获取当前屏幕的高度
     */
    public int getWindowHeight() {
        return display.getHeight();
    }

    /**
     * @return
     * @Description： 获取当前屏幕一半宽度
     */
    public int getHalfWidth() {
        return display.getWidth() / 2;
    }

    /**
     * @return
     * @Description： 获取当前屏幕1/4宽度
     */
    public int getQuarterWidth() {
        return display.getWidth() / 4;
    }
}
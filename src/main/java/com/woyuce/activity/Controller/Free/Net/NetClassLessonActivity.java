package com.woyuce.activity.Controller.Free.Net;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Free.Net.NetClassLessonAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Free.FreePageActivity;
import com.woyuce.activity.Controller.Login.LoginActivity;
import com.woyuce.activity.Controller.Store.StoreGoodsActivity;
import com.woyuce.activity.Controller.WebNoCookieActivity;
import com.woyuce.activity.Model.Free.Net.NetLessonBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21
 */
public class NetClassLessonActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TextView mTitle;
    private Button mBtnClearcache;
    private ImageView mBack, mGuide;
    private GridView mGridview;
    private NetClassLessonAdapter wanglessonAdapter;
    private List<NetLessonBean> wanglessonList = new ArrayList<>();

    //    private static final String URL = "http://api.iyuce.com/v1/exam/examunit";
    //    private static final String URL_Check = "http://api.iyuce.com/v1/exam/checkuserforwlb";
    //    private static final String URL_CheckCode = "http://api.iyuce.com/v1/exam/activecodeforwlb";

    private int localtry = 0;
    private String localtoken;
    private String localgid, localpid, localmid, localwcg_name;
    private String localCheckCode, localunitid, localunitName;

    @Override
    protected void onStop() {
        super.onStop();
//        AppContext.getHttpQueue().cancelAll("wangluobanlesson");
        HttpUtil.removeTag(Constants.ACTIVITY_NET_LESSON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_class_lesson);

        initView();
        getExamUnit();
    }

    private void initView() {
        localtoken = PreferenceUtil.getSharePre(NetClassLessonActivity.this).getString("localtoken", "");
        Intent intent = getIntent();
        localgid = intent.getStringExtra("localwcg_id");
        localpid = intent.getStringExtra("localwcg_pid");
        localmid = intent.getStringExtra("localwcg_mid");
        localwcg_name = intent.getStringExtra("localwcg_name");

        mTitle = (TextView) findViewById(R.id.title_activity_wangluobanlesson);
        mGuide = (ImageView) findViewById(R.id.img_wangluobanlesson_guide);
        mBack = (ImageView) findViewById(R.id.arrow_back);
        mBtnClearcache = (Button) findViewById(R.id.btn_wangluobanlesson_clearcache);
        mGridview = (GridView) findViewById(R.id.gridview_activity_wangluobanlesson);

        mBack.setOnClickListener(this);
        mGuide.setOnClickListener(this);
        mBtnClearcache.setOnClickListener(this);
        mGridview.setOnItemClickListener(this);
        mTitle.setText(localwcg_name);

        // 第一次引导的动画
        if (PreferenceUtil.getSharePre(NetClassLessonActivity.this).contains("imgclearguide_wangluobanlesson")) {
            mGuide.setVisibility(View.GONE);
        }
    }

    /**
     * 获取spn的数据
     */
    private void getExamUnit() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("mid", localmid);
        params.put("pid", localpid);
        HttpUtil.post(Constants.URL_POST_NET_LESSON, headers, params, Constants.ACTIVITY_NET_LESSON, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONArray arr;
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        NetLessonBean wanglesson;
                        for (int i = 0; i < arr.length(); i++) {
                            wanglesson = new NetLessonBean();
                            obj = arr.getJSONObject(i);
                            wanglesson.setUnitId(obj.getString("unit_id"));
                            wanglesson.setUnitName(obj.getString("unit_name"));
                            wanglesson.setImgPath(obj.getString("img_path"));
                            wanglesson.setShowTypeId(obj.getString("show_type_id"));
                            wanglessonList.add(wanglesson);
                        }
                    }
                    //gridview设置adapter
                    wanglessonAdapter = new NetClassLessonAdapter(NetClassLessonActivity.this, wanglessonList);
                    mGridview.setAdapter(wanglessonAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 检查权限
     */
    private void checkRight() {
        // 检测用户ID，若无则未登录，无法验证权限
        String localuserId = PreferenceUtil.getSharePre(NetClassLessonActivity.this).getString("userId", "");
        if (localuserId.equals("")) {
            // 建造者模式Builder
            new AlertDialog.Builder(NetClassLessonActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setIcon(android.R.drawable.ic_dialog_info).setTitle("您还没有登陆哦，亲").setMessage("现在去登陆吗？")
                    .setPositiveButton("登陆", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(NetClassLessonActivity.this, LoginActivity.class));
                        }
                    }).setNegativeButton("取消", null).create().show();
            return;
        }


        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", PreferenceUtil.getSharePre(NetClassLessonActivity.this).getString("userId", ""));
        params.put("gid", localgid);
        HttpUtil.post(Constants.URL_POST_NET_LESSON_Check, headers, params, Constants.ACTIVITY_NET_LESSON, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        String flag = obj.getString("data");
                        // 判断是否有权限,有则进入下一级
                        if (flag.equals("1")) {
                            Intent intent = new Intent(NetClassLessonActivity.this, FreePageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("localunit_name", localunitName);
                            bundle.putString("localunit_id", localunitid);
                            bundle.putString("localshow_type_id", "wangluoban");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            // 没有则输入激活码
                            final EditText checktxt = new EditText(NetClassLessonActivity.this);
                            new AlertDialog.Builder(NetClassLessonActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                    .setTitle("亲，您没有权限，请输入激活码\n若已报名490课程，请联系客服\n").setView(checktxt)
                                    .setPositiveButton("验证激活码", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            localCheckCode = checktxt.getText().toString().trim();
                                            CheckCode();
                                        }
                                    }).setNeutralButton("前往购买", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //对话中再做对话
                                    new AlertDialog.Builder(NetClassLessonActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                            .setNeutralButton("购买直播课程", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(NetClassLessonActivity.this, WebNoCookieActivity.class);
                                                    intent.putExtra("URL", Constants.URL_WEB_ZHIBO);
                                                    intent.putExtra("TITLE", "网络班直播报名");
                                                    intent.putExtra("COLOR", "#1e87e2");
                                                    startActivity(intent);
                                                }
                                            })
                                            .setPositiveButton("购买录播课程", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //跳转商城商品
                                                    getactivegoods();
                                                }
                                            })
                                            .show();
                                }
                            }).setNegativeButton("联系客服", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean isqq = isApkInstalled(NetClassLessonActivity.this, "com.tencent.mobileqq");
                                    if (!isqq) {
                                        ToastUtil.showMessage(NetClassLessonActivity.this,
                                                "手机必须安装腾讯QQ才能联系客服哦");
                                    } else {
                                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=3174839753";
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                    }
                                }
                            }).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 获取商城商品信息
     */
    private void getactivegoods() {
        HttpUtil.get(Constants.URL_GetGoods, Constants.ACTIVITY_NET_LESSON, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("data");
                        Intent intent = new Intent(NetClassLessonActivity.this, StoreGoodsActivity.class);
                        intent.putExtra("goods_id", obj.getString("goods_id"));
                        intent.putExtra("goods_sku_id", obj.getString("goods_sku_id"));
                        intent.putExtra("goods_title", obj.getString("goods_title"));
                        intent.putExtra("sales_price", obj.getString("sales_price"));
                        intent.putExtra("can_go_store_back", "yes");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 输入激活码
     */
    private void CheckCode() {
        // 简单防暴力破解，做次数限制。 //复杂的可以考虑放到share preference中去做
        if (localtry >= 3 && localtry < 5) {
            ToastUtil.showMessage(NetClassLessonActivity.this, "您已经输错三次了亲，输错五次将被冻结");
            localtry++;
            return;
        } else if (localtry >= 5) {
            ToastUtil.showMessage(NetClassLessonActivity.this, "您已经输错五次了亲，激活功能被冻结五分钟，请五分钟后重试");
            localtry++;
            return;
        }
        localtry++;

        // 激活码正确则进入
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", PreferenceUtil.getSharePre(NetClassLessonActivity.this).getString("userId", ""));
        params.put("gid", localgid);
        params.put("code", localCheckCode);
        HttpUtil.post(Constants.URL_POST_NET_LESSON_CheckCode, headers, params, Constants.ACTIVITY_NET_LESSON, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    int code = obj.getInt("code");
                    if (code == 1) {
                        ToastUtil.showMessage(NetClassLessonActivity.this, obj.getString("message"));
                    } else if (code == 0) {
                        ToastUtil.showMessage(NetClassLessonActivity.this, "恭喜您，验证成功啦!");
                        Intent intent = new Intent(NetClassLessonActivity.this, FreePageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("localunit_name", localunitName);
                        bundle.putString("localunit_id", localunitid);
                        bundle.putString("localshow_type_id", "wangluoban");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isApkInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // 该方法以后才需要判断权限
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_wangluobanlesson_guide:
                mGuide.setVisibility(View.GONE);
                PreferenceUtil.save(NetClassLessonActivity.this, "imgclearguide_wangluobanlesson", "guided");
                break;
            case R.id.btn_wangluobanlesson_clearcache:
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                ToastUtil.showMessage(NetClassLessonActivity.this, "清除缓存,更新书籍成功");
                break;
            default:
                finish();
                break;
        }
    }

    // 该方法以后才需要判断权限
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        localunitid = wanglessonList.get(position).getUnitId();
        localunitName = wanglessonList.get(position).getUnitName();
        checkRight();
    }
}
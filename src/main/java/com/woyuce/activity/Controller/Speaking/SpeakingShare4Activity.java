package com.woyuce.activity.Controller.Speaking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Main.MainActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2016/9/22
 */
public class SpeakingShare4Activity extends BaseActivity implements View.OnClickListener {

    private Button btnBack;
    private LinearLayout llBack;

    private String localRoomID, localTime, localMessage, localsubname, localRoom;
    private List<String> subidList = new ArrayList<>();
    private List<String> subnameList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_SPEAKING_SHARE_FOUR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_share4);

        initView();
        dataRequest();
    }

    private void initView() {
        Intent intent = getIntent();
        localRoomID = intent.getStringExtra("localRoomID");
        localTime = intent.getStringExtra("localTime");
        localMessage = intent.getStringExtra("localMessage");
        subidList = intent.getStringArrayListExtra("subidList");
        localsubname = intent.getStringExtra("localsubname");
        localRoom = intent.getStringExtra("localRoom");
        subnameList = intent.getStringArrayListExtra("subnameList");
        LogUtil.e("subnameList === " + subnameList);

        llBack = (LinearLayout) findViewById(R.id.ll_speaking_stastis);
        btnBack = (Button) findViewById(R.id.button_share4_back);

        llBack.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void dataRequest() {
        progressdialogshow(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("uname", PreferenceUtil.getSharePre(getApplicationContext()).getString("mUserName", ""));
        params.put("umessage", localMessage);
        params.put("roomid", localRoomID);
                /* 按数组长度做判断，传递参数 */
        switch (subidList.size()) {
            case 1:
                params.put("subid", subidList.get(0));
                break;
            case 2:
                params.put("subid", subidList.get(0) + "," + subidList.get(1));
                break;
            case 3:
                params.put("subid", subidList.get(0) + "," + subidList.get(1) + "," + subidList.get(2));
                break;
            case 4:
                params.put("subid", subidList.get(0) + "," + subidList.get(1) + "," + subidList.get(2) + ","
                        + subidList.get(3));
                break;
        }
        params.put("examtime", localTime);
        HttpUtil.post(Constants.URL_POST_SPEAKING_SHARE_FOUR, params, Constants.ACTIVITY_SPEAKING_SHARE_FOUR, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                new AlertDialog.Builder(SpeakingShare4Activity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle("分享结果")
                        .setCancelable(false)
                        .setPositiveButton("分享成功 !", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it_speaking = new Intent(SpeakingShare4Activity.this, SpeakingActivity.class);
                                startActivity(it_speaking);
                                SpeakingShare4Activity.this.finish();
                                if (subnameList.size() == 0) {
                                    showShare("我们不卖答案，我们是试卷的搬运工", "我在" + localRoom + "考到了:" + localsubname);
                                } else {
                                    showShare("我们不卖答案，我们是试卷的搬运工", "我在" + localRoom + "考到了:" + subnameList.get(0));
                                }
                            }
                        }).show();
            }
        });
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_POST_SPEAKING_SHARE_FOUR, new Response.Listener<String>() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onResponse(String response) {
//                new AlertDialog.Builder(SpeakingShare4Activity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
//                        .setTitle("分享结果")
//                        .setCancelable(false)
//                        .setPositiveButton("分享成功 !", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent it_speaking = new Intent(SpeakingShare4Activity.this, SpeakingActivity.class);
//                                startActivity(it_speaking);
//                                SpeakingShare4Activity.this.finish();
//                                if (subnameList.size() == 0) {
//                                    showShare("我们不卖答案，我们是试卷的搬运工", "我在" + localRoom + "考到了:" + localsubname);
//                                } else {
//                                    showShare("我们不卖答案，我们是试卷的搬运工", "我在" + localRoom + "考到了:" + subnameList.get(0));
//                                }
//                            }
//                        }).show();
//            }
//        }, new Response.ErrorListener() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                LogUtil.e("Wrong-BACK", "联接错误原因： " + error.getMessage());
//                new AlertDialog.Builder(SpeakingShare4Activity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("分享结果")
//                        .setCancelable(false).setPositiveButton("分享失败，请重试 !", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SpeakingShare4Activity.this.finish();
//                    }
//                }).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> hashMap = new HashMap<>();
//                hashMap.put("uname", PreferenceUtil.getSharePre(getApplicationContext()).getString("mUserName", ""));
//                hashMap.put("umessage", localMessage);
//                hashMap.put("roomid", localRoomID);
//                /* 按数组长度做判断，传递参数 */
//                switch (subidList.size()) {
//                    case 1:
//                        hashMap.put("subid", subidList.get(0));
//                        break;
//                    case 2:
//                        hashMap.put("subid", subidList.get(0) + "," + subidList.get(1));
//                        break;
//                    case 3:
//                        hashMap.put("subid", subidList.get(0) + "," + subidList.get(1) + "," + subidList.get(2));
//                        break;
//                    case 4:
//                        hashMap.put("subid", subidList.get(0) + "," + subidList.get(1) + "," + subidList.get(2) + ","
//                                + subidList.get(3));
//                        break;
//                }
//                hashMap.put("examtime", localTime);
//                return hashMap;
//            }
//        };
//        stringRequest.setTag("share4");
//        AppContext.getHttpQueue().add(stringRequest);
    }

    //多社交平台分享
    private void showShare(String title, String message) {
        try {
            String temp = null;
            switch (subnameList.size()) {
                case 0:
                    temp = "";
                    break;
                case 1:
                    temp = subnameList.get(0);
                    break;
                case 2:
                    temp = subnameList.get(0) + "," + subnameList.get(1);
                    break;
                case 3:
                    temp = subnameList.get(0) + "," + subnameList.get(1) + "," + subnameList.get(2);
                    break;
            }
            String encode_collage = URLEncoder.encode(localRoom, "utf-8");
            String URLcode;
            if (subnameList.size() > 0) {
                String encode_title = URLEncoder.encode(subnameList.get(0), "utf-8");
                URLcode = encode_title.replace("+", "%20");
            } else {
                URLcode = "";
            }
            String url = "http://xm.iyuce.com/app/fenxiang.html?viewid=1&collage=" + encode_collage
                    + "&title=" + URLcode + "&datetime=" + localTime + "&img=&part1=" + temp
                    + "&part2= " + localsubname + "&message=" + localMessage;
            ShareSDK.initSDK(SpeakingShare4Activity.this);
            OnekeyShare oks = new OnekeyShare();
            oks.setTitle(message);
            oks.setTitleUrl(url);
            oks.setText(title);
            oks.setUrl(url);
            oks.setImageUrl("http://www.iyuce.com/uploadfiles/app/logo.png");
            oks.setComment("答题超赞");
            oks.setSite(getString(R.string.app_name));
            oks.setSiteUrl(url);
            oks.show(this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
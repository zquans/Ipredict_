package com.woyuce.activity.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Activity.AboutUsActivity;
import com.woyuce.activity.Activity.LoginActivity;
import com.woyuce.activity.Activity.StoreHomeActivity;
import com.woyuce.activity.Activity.SuggestionActivity;
import com.woyuce.activity.Activity.WebActivity;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.SpeakingRoom;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.Utils.UpdateManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragmentfive extends Fragment implements View.OnClickListener {

    private TextView txtName, txtMoney, txtAboutUs, txtUpdate, txtSuggestion, txtRoom, txtSubject, txtClassTable, txtStore, txtSignOut;
    private ImageView imgIcon;
    // 暂做课表的入口
    private TextView mCourseTable;

    private String localroomname;
    private String URL_ROOM = "http://iphone.ipredicting.com/kymyroom.aspx";
    private String URL_SUBJECT = "http://iphone.ipredicting.com/kymyshanesub.aspx";
    private List<SpeakingRoom> roomList = new ArrayList<>();
    private List<String> subcontentList = new ArrayList<>();
    private List<String> myexamList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab5, container, false);

        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        txtRoom.setText("");
        txtSubject.setText("");
        initEvent();
    }

    private void initView(View view) {
        imgIcon = (ImageView) view.findViewById(R.id.img_tab5_icon);
        txtName = (TextView) view.findViewById(R.id.txt_tab5_username);
        txtMoney = (TextView) view.findViewById(R.id.txt_tab5_localmoney);
        txtAboutUs = (TextView) view.findViewById(R.id.txt_to_aboutus);
        txtUpdate = (TextView) view.findViewById(R.id.txt_to_update);
        txtSuggestion = (TextView) view.findViewById(R.id.txt_to_suggestion);
        txtRoom = (TextView) view.findViewById(R.id.txt_tab5_localroom);
        txtSubject = (TextView) view.findViewById(R.id.txt_tab5_localsubject);
        txtStore = (TextView) view.findViewById(R.id.txt_to_store);
        txtSignOut = (TextView) view.findViewById(R.id.txt_to_signout);
        mCourseTable = (TextView) view.findViewById(R.id.txt_tab5_localmessage);

        mCourseTable.setOnClickListener(this);
        imgIcon.setOnClickListener(this);
        txtAboutUs.setOnClickListener(this);
        txtUpdate.setOnClickListener(this);
        txtSuggestion.setOnClickListener(this);
        txtRoom.setOnClickListener(this);
        txtSubject.setOnClickListener(this);
        txtStore.setOnClickListener(this);
        txtSignOut.setOnClickListener(this);
    }

    // fragment 生命周期，打开时
    private void initEvent() {
        if (share().getString("username", "").length() == 0) {
            txtRoom.setText("登录后可见");
            txtSubject.setText("登录后可见");
            myexamList.clear();
        } else {
            roomList.clear();
            subcontentList.clear();
            myexamList.clear();
            getRoomJson();
            getSubjectJson();
            mCourseTable.setText("查看");
        }
        txtName.setText(share().getString("mUserName", "点击头像切换账号"));
        txtMoney.setText(share().getString("money", "登录后可见"));
    }

    // 从initEvent 抽出，方便调用，代码简洁
    private SharedPreferences share() {
        return PreferenceUtil.getSharePre(getActivity());
    }

    //获取考场
    private void getRoomJson() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ROOM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                SpeakingRoom room;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            room = new SpeakingRoom();
                            room.roomid = jsonObject.getString("id");
                            room.roomname = jsonObject.getString("examroom");
                            roomList.add(room);
                            localroomname = room.roomname;
                            txtRoom.setText(localroomname);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("uname", PreferenceUtil.getSharePre(getActivity()).getString("username", ""));
                return hashMap;
            }
        };
        stringRequest.setTag("fragmentfive");
        AppContext.getHttpQueue().add(stringRequest);
    }

    /**
     * 获取考题列表
     */
    private void getSubjectJson() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBJECT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        String local_subname;
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            local_subname = jsonObject.getString("subname");
                            subcontentList.add(local_subname);
                            myexamList.add(local_subname);
                        }
                        List<String> sublist = new ArrayList<>();
                        for (int i = 0; i < subcontentList.size(); i++) {
                            sublist.add("《" + subcontentList.get(i) + "》");
                        }
                        txtSubject.setText(sublist.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("uname", PreferenceUtil.getSharePre(getActivity()).getString("username", ""));
                return hashMap;
            }
        };
        stringRequest.setTag("fragmentfive");
        AppContext.getHttpQueue().add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_tab5_localmessage:
                if (TextUtils.isEmpty(PreferenceUtil.getSharePre(getActivity()).getString("userId", ""))) {
                    return;
                }
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("URL", "http://plan.iyuce.com");
                intent.putExtra("TITLE", "课表系统");
                intent.putExtra("COLOR", "#f7941d");
                startActivity(intent);
                break;
            case R.id.img_tab5_icon:
                toSignOut();
                break;
//			case R.id.txt_tab5_localsubject:
//				Intent intent = new Intent(getActivity(),MyExamContent.class);
//				intent.putStringArrayListExtra("myexamList", (ArrayList<String>) myexamList);
//				startActivity(intent);
//				break;
            case R.id.txt_to_aboutus:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            case R.id.txt_to_update:
                String serverVersion = PreferenceUtil.getSharePre(getActivity()).getString("serverVersion", "");
                String localVersion = PreferenceUtil.getSharePre(getActivity()).getString("localVersion", "");
                LogUtil.e("serverVersion", "serverVersion = " + serverVersion + " localVersion" + localVersion);
                if (Float.parseFloat(serverVersion) > Float.parseFloat(localVersion)) {
                    // 自动更新
                    new UpdateManager(getActivity()).checkUpdate();
                    break;
                } else {
                    ToastUtil.showMessage(getActivity(), "当前已经是最新版本:v" + localVersion);
                    break;
                }
            case R.id.txt_to_suggestion:
                startActivity(new Intent(getActivity(), SuggestionActivity.class));
                break;
            case R.id.txt_to_store:
                startActivity(new Intent(getActivity(), StoreHomeActivity.class));
                break;
            case R.id.txt_to_signout:
                toSignOut();
                break;
        }
    }

    /**
     * 登出操作
     */
    private void toSignOut() {
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("确定要前往登录界面吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CookieManager.getInstance().removeAllCookie();
                        LogUtil.e("CookieManager = " + CookieManager.getInstance().getCookie("iyuce.com") + "");
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                }).setNegativeButton("取消", null)
                .show();
    }
}
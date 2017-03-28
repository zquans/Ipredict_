package com.woyuce.activity.UI.Fragment.Main;

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
import com.woyuce.activity.AppContext;
import com.woyuce.activity.Bean.Speaking.SpeakingRoom;
import com.woyuce.activity.R;
import com.woyuce.activity.UI.Activity.Common.AboutUsActivity;
import com.woyuce.activity.UI.Activity.Common.CustomServiceActivity;
import com.woyuce.activity.UI.Activity.Common.SuggestionActivity;
import com.woyuce.activity.UI.Activity.Common.WebActivity;
import com.woyuce.activity.UI.Activity.Login.LoginActivity;
import com.woyuce.activity.UI.Activity.Store.StoreCarActivity;
import com.woyuce.activity.UI.Activity.Store.StoreOrderListActivity;
import com.woyuce.activity.Utils.ActivityManager;
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

    private TextView txtName, txtMoney, txtAboutUs, txtUpdate, txtSuggestion, txtRoom, txtSubject,
            txtClassTable, txtStore, txtClear, txtService, txtSignOut, txtOrderList;
    private ImageView imgIcon;
    // 暂做课表的入口
    private TextView mCourseTable;

    private String localroomname;
    private String URL_ROOM = "http://iphone.ipredicting.com/kymyroom.aspx";
    private String URL_SUBJECT = "http://iphone.ipredicting.com/kymyshanesub.aspx";
    private String URL_MONEY_INFO = "http://api.iyuce.com/v1/store/getusermoney?userid=";
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
        txtClear = (TextView) view.findViewById(R.id.txt_to_clearcache);
        txtService = (TextView) view.findViewById(R.id.txt_to_service);
        txtSignOut = (TextView) view.findViewById(R.id.txt_to_signout);
        txtOrderList = (TextView) view.findViewById(R.id.txt_to_orderlist);
        mCourseTable = (TextView) view.findViewById(R.id.txt_tab5_localmessage);

        mCourseTable.setOnClickListener(this);
        imgIcon.setOnClickListener(this);
        txtAboutUs.setOnClickListener(this);
        txtUpdate.setOnClickListener(this);
        txtSuggestion.setOnClickListener(this);
        txtRoom.setOnClickListener(this);
        txtSubject.setOnClickListener(this);
        txtStore.setOnClickListener(this);
        txtClear.setOnClickListener(this);
        txtService.setOnClickListener(this);
        txtSignOut.setOnClickListener(this);
        txtOrderList.setOnClickListener(this);
    }

    // fragment 生命周期，打开时
    private void initEvent() {
        if (share().getString("username", "").length() == 0) {
            txtRoom.setText("登录后可见");
            txtSubject.setText("登录后可见");
            txtMoney.setText(share().getString("money", "登录后可见"));
            myexamList.clear();
        } else {
            roomList.clear();
            subcontentList.clear();
            myexamList.clear();
            getRoomJson();
            getSubjectJson();
            getMoney();
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
        StringRequest rommRequest = new StringRequest(Request.Method.POST, URL_ROOM, new Response.Listener<String>() {
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
        rommRequest.setTag("fragmentfive");
        AppContext.getHttpQueue().add(rommRequest);
    }

    /**
     * 获取考题列表
     */
    private void getSubjectJson() {
        StringRequest subjectRequest = new StringRequest(Request.Method.POST, URL_SUBJECT, new Response.Listener<String>() {
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
                        String subStr = sublist.toString();
                        txtSubject.setText(subStr.substring(1, subStr.length() - 1));
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
        subjectRequest.setTag("fragmentfive");
        AppContext.getHttpQueue().add(subjectRequest);
    }


    /**
     * 获取金币
     */
    private void getMoney() {
        StringRequest moneyRequest = new StringRequest(Request.Method.GET,
                URL_MONEY_INFO + PreferenceUtil.getSharePre(getActivity()).getString("userId", ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(response);
                    if (obj.getString("code").equals("0")) {
                        txtMoney.setText(obj.getString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        moneyRequest.setTag("fragmentfive");
        AppContext.getHttpQueue().add(moneyRequest);
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
            case R.id.txt_to_clearcache:
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearDiskCache();
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearMemoryCache();
                ToastUtil.showMessage(getActivity(), "清除缓存成功");
                break;
            case R.id.txt_to_store:
                if (!PreferenceUtil.getSharePre(getActivity()).getString("storetb_is_exist", "no").equals("yes")) {
                    ToastUtil.showMessage(getActivity(), "您的购物车空空哒，快去添加商品吧！");
                    return;
                }
                startActivity(new Intent(getActivity(), StoreCarActivity.class));
                break;
            case R.id.txt_to_service:
                startActivity(new Intent(getActivity(), CustomServiceActivity.class));
                break;
            case R.id.txt_to_orderlist:
                startActivity(new Intent(getActivity(), StoreOrderListActivity.class));
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
                        //启动Login页
                        ActivityManager.getAppManager().finishAllActivity();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        PreferenceUtil.removeall(getActivity()); // 只留下了版本号和localtoken
                        getActivity().finish();
                    }
                }).setNegativeButton("取消", null)
                .show();
    }
}
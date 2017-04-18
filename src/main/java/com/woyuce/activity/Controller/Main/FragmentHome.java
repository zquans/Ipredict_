package com.woyuce.activity.Controller.Main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Free.FreeRangeActivity;
import com.woyuce.activity.Controller.Free.Net.NetClassActivity;
import com.woyuce.activity.Controller.Login.LoginActivity;
import com.woyuce.activity.Controller.OpenClass.OpenTypeActivity;
import com.woyuce.activity.Controller.Speaking.SpeakingActivity;
import com.woyuce.activity.Controller.WebActivity;
import com.woyuce.activity.Controller.Writting.WitActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.StringUtils;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private ImageView img_speaking, img_range, img_gongyi, img_wangluo, img_writting, img_waitting;

    // 计时器所需
    private FrameLayout mFrame;
    private TextView mTxtTimer, mTxtTimerTitle;
    private String localTimer, localtoken, localuserid;

//    private String URL = "http://api.iyuce.com/v1/exam/setexamtime";

    @Override
    public void onStart() {
        super.onStart();
        // 每次start重设倒计时
        setTimer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_main_home, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        img_speaking = (ImageView) view.findViewById(R.id.img_speaking);
        img_range = (ImageView) view.findViewById(R.id.img_range);
        img_gongyi = (ImageView) view.findViewById(R.id.img_gongyi);
        img_wangluo = (ImageView) view.findViewById(R.id.img_wangluo);
        img_writting = (ImageView) view.findViewById(R.id.img_writting);
        img_waitting = (ImageView) view.findViewById(R.id.img_waitting);
        mTxtTimerTitle = (TextView) view.findViewById(R.id.txt_tab1_timer1);
        mTxtTimer = (TextView) view.findViewById(R.id.txt_tab1_timer2);
        mFrame = (FrameLayout) view.findViewById(R.id.framelayout_tab1_timer);

        img_speaking.setOnClickListener(this);
        img_range.setOnClickListener(this);
        img_gongyi.setOnClickListener(this);
        img_wangluo.setOnClickListener(this);
        img_writting.setOnClickListener(this);
        img_waitting.setOnClickListener(this);

        mFrame.setOnClickListener(this);
//		mTxtTimer.setOnClickListener(this);
    }

    /**
     * 设置考试倒计时
     */
    private void setTimer() {
        localtoken = PreferenceUtil.getSharePre(getActivity()).getString("localtoken", "");
        localuserid = PreferenceUtil.getSharePre(getActivity()).getString("userId", "");
        localTimer = PreferenceUtil.getSharePre(getActivity()).getString("mtimer", "");
        if (localTimer.equals("") || localTimer == null) {
            mTxtTimer.setText("设置考试时间");
        } else {
            // mTxtTimer.setText("距离考试时间" + localTimer + "天");
            SimpleDateFormat mSimpleDate = new SimpleDateFormat("yyyy-MM-dd");
            // 将String转为Date格式,获得当前时间
            Date mDateBySet;
            try {
                mDateBySet = mSimpleDate.parse(localTimer);
                Date mDateNow = mSimpleDate.parse(mSimpleDate.format(new Date()));
                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c1.setTime(mDateBySet);
                c2.setTime(mDateNow);
                long mFloat1 = c1.getTimeInMillis();
                long mFloat2 = c2.getTimeInMillis();
                // 根据时间差进行判断
                int mDistantofTime = (int) ((mFloat1 - mFloat2) / 86400000);
                if (mDistantofTime == 0) {
                    mTxtTimerTitle.setText("祝您今天考试顺利");
                    mTxtTimer.setText("加油!");
                } else if (mDistantofTime > 0) {
                    mTxtTimerTitle.setText("距离考试时间还有");
                    mTxtTimer.setText(mDistantofTime + "天");
                } else if (mDistantofTime < 0) {
                    mTxtTimerTitle.setText("距离考试时间还有");
                    mTxtTimer.setText("已结束");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将设定的时间上传
     */
    private void getJson(final String mTime) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", localuserid);
        params.put("exam_time", mTime);
        HttpUtil.post(Constants.URL_POST_SET_EXAM_TIME, headers, params, null, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    String code = obj.getString("code");
                    if (code.equals("0")) {
                        LogUtil.e("settime,success");
                    } else {
                        LogUtil.e("settime,false");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.framelayout_tab1_timer:
                Calendar mCalendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog, timePick(),
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            case R.id.img_speaking:
                startActivity(new Intent(getActivity(), SpeakingActivity.class));
                break;
            case R.id.img_range:
                startActivity(new Intent(getActivity(), FreeRangeActivity.class));
                break;
            case R.id.img_gongyi:
                startActivity(new Intent(getActivity(), OpenTypeActivity.class));
                break;
            case R.id.img_wangluo:
                String userId = PreferenceUtil.getSharePre(getActivity()).getString("userId", "");
                if (StringUtils.isEmpty(userId)) {
                    new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("访问网络班专属需要先登录哦，亲")
                            .setMessage("现在去登陆吗？")
                            .setPositiveButton("登陆", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }
                            }).setNegativeButton("取消", null).create().show();
                    break;
                }
                startActivity(new Intent(getActivity(), NetClassActivity.class));
                break;
            case R.id.img_writting:
                startActivity(new Intent(getActivity(), WitActivity.class));
                break;
            case R.id.img_waitting:
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("URL", Constants.URL_EXAM_ANSWER);
                intent.putExtra("TITLE", "考后笔试答案");
                intent.putExtra("COLOR", "#408f40");
                startActivity(intent);
                break;
        }
    }

    /**
     * 时间选择的方法
     */
    private DatePickerDialog.OnDateSetListener timePick() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String mTime = year + "-" + (month + 1) + "-" + day;
                SimpleDateFormat mSimpleDate = new SimpleDateFormat("yyyy-MM-dd");
                // 如果已设置过，则保存
                PreferenceUtil.save(getActivity(), "mtimer", mTime);
                //将mTime上传后台
                getJson(mTime);
                dealTimePicker(mTime, mSimpleDate);
            }
        };
    }

    /**
     * 根据sharepreference判断如何处理选中的
     */
    private void dealTimePicker(String mTime, SimpleDateFormat mSimpleDate) {
        try {
            // 将String转为Date格式,获得当前时间
            Date mDateBySet = mSimpleDate.parse(mTime);
            Date mDateNow = mSimpleDate.parse(mSimpleDate.format(new Date()));
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(mDateBySet);
            c2.setTime(mDateNow);
            long mFloat1 = c1.getTimeInMillis();
            long mFloat2 = c2.getTimeInMillis();
            // 根据时间差进行判断
            int mDistantofTime = (int) ((mFloat1 - mFloat2) / 86400000);
            if (mDistantofTime > 0) {
                mTxtTimerTitle.setText("距离考试时间还有");
                mTxtTimer.setText(mDistantofTime + "天");
                String examtimer = String.valueOf(mDistantofTime);
                // PreferenceUtil.save(getActivity(), "examtimer", examtimer);
            } else if (mDistantofTime == 0) {
                mTxtTimerTitle.setText("祝您今天考试顺利");
                mTxtTimer.setText("加油!");
            } else if (mDistantofTime < 0) {
                ToastUtil.showMessage(getActivity(), "不能设置已经过去的时间哦，亲");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
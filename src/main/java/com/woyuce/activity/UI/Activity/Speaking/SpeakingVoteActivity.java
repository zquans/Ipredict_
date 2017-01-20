package com.woyuce.activity.UI.Activity.Speaking;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.woyuce.activity.Adapter.Speaking.SpeakingAreaAdapter;
import com.woyuce.activity.Adapter.Speaking.SpeakingCityAdapter;
import com.woyuce.activity.Adapter.Speaking.SpeakingRoomAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Speaking.SpeakingArea;
import com.woyuce.activity.Bean.Speaking.SpeakingCity;
import com.woyuce.activity.Bean.Speaking.SpeakingRoom;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingVoteActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView txtTitle;
    private Button btnCancel, btnVote;
    private Spinner spnArea, spnCity, spnRoom;

    private String localsubID, localsubName, localUrl;
    private String localAreaId, localCityId, localRoomId, localRoomName;

    private List<SpeakingArea> areaList = new ArrayList<>();
    private List<SpeakingCity> cityList = new ArrayList<>();
    private List<SpeakingRoom> roomList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_SPEAKING_VOTE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingvote);

        initView();
        getAreaJson();
    }

    private void initView() {
        localsubID = getIntent().getStringExtra("localsubID");
        localsubName = getIntent().getStringExtra("localsubName");
        localUrl = getIntent().getStringExtra("local_url");

        txtTitle = (TextView) findViewById(R.id.txt_vote_Title);
        btnCancel = (Button) findViewById(R.id.btn_vote_cancel);
        btnVote = (Button) findViewById(R.id.btn_vote_vote);
        spnArea = (Spinner) findViewById(R.id.spn_vote_area);
        spnCity = (Spinner) findViewById(R.id.spn_vote_city);
        spnRoom = (Spinner) findViewById(R.id.spn_vote_room);

        btnCancel.setOnClickListener(this);
        btnVote.setOnClickListener(this);
        txtTitle.setText(localsubName);
    }

    private void setAreaData() {
        SpeakingAreaAdapter areaAdapter = new SpeakingAreaAdapter(this, areaList);
        spnArea.setAdapter(areaAdapter);
        spnArea.setOnItemSelectedListener(this);
    }

    private void setCityData() {
        SpeakingCityAdapter cityAdapter = new SpeakingCityAdapter(this, cityList);
        spnCity.setAdapter(cityAdapter);
        spnCity.setOnItemSelectedListener(this);
    }

    private void setRoomData() {
        SpeakingRoomAdapter roomAdapter = new SpeakingRoomAdapter(this, roomList);
        spnRoom.setAdapter(roomAdapter);
        spnRoom.setOnItemSelectedListener(this);
    }

    private void getAreaJson() {
        OkGo.post(Constants.URL_POST_SPEAKING_CHOOSE_AREA).tag(Constants.ACTIVITY_SPEAKING_VOTE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doAreaSuccess(s);
                    }
                });
    }

    private void doAreaSuccess(String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            SpeakingArea area;
            int result = jsonObject.getInt("code");
            if (result == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    area = new SpeakingArea();
                    area.subAreaName = jsonObject.getString("subAreaName");
                    area.subAreaid = jsonObject.getString("subAreaid");
                    areaList.add(area);
                }
            }
            setAreaData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCityJson() {
        HttpParams params = new HttpParams();
        if (localAreaId == null) {
            params.put("areaid", "45");
        }
        params.put("areaid", localAreaId);
        OkGo.post(Constants.URL_POST_SPEAKING_CHOOSE_CITY).tag(Constants.ACTIVITY_SPEAKING_VOTE).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doCitySuccess(s);
                    }
                });
    }

    private void doCitySuccess(String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            SpeakingCity city;
            int result = jsonObject.getInt("code");
            if (result == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    city = new SpeakingCity();
                    city.cityid = jsonObject.getString("cityid");
                    city.cityname = jsonObject.getString("cityname");
                    cityList.add(city);
                }
            }
            setCityData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRoomJson() {
        HttpParams params = new HttpParams();
        params.put("cityid", localCityId);
        OkGo.post(Constants.URL_POST_SPEAKING_CHOOSE_ROOM).tag(Constants.ACTIVITY_SPEAKING_VOTE).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doRoomSuccess(s);
                    }
                });
    }

    private void doRoomSuccess(String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            SpeakingRoom room;
            int result = jsonObject.getInt("code");
            if (result == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    room = new SpeakingRoom();
                    room.roomid = jsonObject.getString("roomid");
                    room.roomname = jsonObject.getString("roomname");
                    roomList.add(room);
                }
            }
            setRoomData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toVote() {
        HttpParams params = new HttpParams();
        params.put("areaid", localAreaId);
        params.put("cityid", localCityId);
        params.put("roomid", localRoomId);
        params.put("subid", localsubID);
        OkGo.post(Constants.URL_POST_SPEAKING_VOTE_VOTE).tag(Constants.ACTIVITY_SPEAKING_VOTE).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doVoteSuccess(s);
                    }
                });
    }

    private void doVoteSuccess(String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            int result = jsonObject.getInt("code");
            if (result == 0) {
                ToastUtil.showMessage(SpeakingVoteActivity.this, "投票成功,分享给好友");
                showShare("我们不卖答案，我们是试卷的搬运工", "我把票投给了" + localsubName);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_vote_area:
                SpeakingArea area = areaList.get(position);
                localAreaId = area.subAreaid;
                cityList.removeAll(cityList);
                getCityJson();
                break;
            case R.id.spn_vote_city:
                SpeakingCity city = cityList.get(position);
                localCityId = city.cityid;
                roomList.removeAll(roomList);
                getRoomJson();
                break;
            case R.id.spn_vote_room:
                SpeakingRoom room = roomList.get(position);
                localRoomId = room.roomid;
                localRoomName = room.roomname;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void showShare(String title, String message) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mDatetime = sdf.format(new Date());
            String encode_collage = java.net.URLEncoder.encode(localRoomName, "utf-8");
//			String  encode_title  =   java.net.URLEncoder.encode("我在" + localRoomName + "把票投给了话题" + localsubName + "。你呢？","utf-8");
            String encode_title = java.net.URLEncoder.encode("我在" + localRoomName + "把票投给了话题" + localsubName, "utf-8");
            String URLcode = encode_title.replace("+", "%20");
            String url = "http://xm.iyuce.com/app/fenxiang.html?viewid=2&img=" + localUrl + "&title=" + URLcode + "&collage=" + encode_collage + "&datetime=" + mDatetime;

            ShareSDK.initSDK(SpeakingVoteActivity.this);
            OnekeyShare oks = new OnekeyShare();
            oks.setTitle(message);
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(url);
            // text是分享文本，所有平台都需要这个字段
            oks.setText(title);
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(url);
            oks.setImageUrl("http://www.iyuce.com/uploadfiles/app/logo.png");
            oks.setComment("答题超赞");
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(url);
            // 启动分享GUI
            oks.show(SpeakingVoteActivity.this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_vote_cancel:
                finish();
                break;
            case R.id.btn_vote_vote:
                toVote();
                break;
        }
    }
}
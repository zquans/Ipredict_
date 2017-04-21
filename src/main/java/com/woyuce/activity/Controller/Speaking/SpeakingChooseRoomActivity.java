package com.woyuce.activity.Controller.Speaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.woyuce.activity.Adapter.Speaking.SpeakingAreaAdapter;
import com.woyuce.activity.Adapter.Speaking.SpeakingCityAdapter;
import com.woyuce.activity.Adapter.Speaking.SpeakingRoomAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Speaking.SpeakingArea;
import com.woyuce.activity.Model.Speaking.SpeakingCity;
import com.woyuce.activity.Model.Speaking.SpeakingRoom;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22
 */
public class SpeakingChooseRoomActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner spnArea, spnCity, spnRoom;
    private Button btnSure, btnCancel;

    private List<SpeakingArea> areaList = new ArrayList<>();
    private List<SpeakingCity> cityList = new ArrayList<>();
    private List<SpeakingRoom> roomList = new ArrayList<>();

    private String localAreaID, localCityID;
    private String localRoomName, localRoomID; // 传递给下一级的数据

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_SPEAKING_SHARE_CHOOSE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_choose_room);

        initView();
        getAreaList();
    }

    private void initView() {
        spnArea = (Spinner) findViewById(R.id.spn_choose_area);
        spnCity = (Spinner) findViewById(R.id.spn_choose_city);
        spnRoom = (Spinner) findViewById(R.id.spn_choose_room);

        btnSure = (Button) findViewById(R.id.btn_choose_sure);
        btnCancel = (Button) findViewById(R.id.btn_choose_cancel);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void getAreaList() {
        HttpUtil.get(Constants.URL_POST_SPEAKING_CHOOSE_AREA, Constants.ACTIVITY_SPEAKING_SHARE_CHOOSE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonObject;
                    SpeakingArea area;
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            area = new SpeakingArea();
                            jsonObject = data.getJSONObject(i);
                            area.subAreaName = jsonObject.getString("subAreaName");
                            area.subAreaid = jsonObject.getString("subAreaid");
                            areaList.add(area);
                        }
                    }
                    setAreaData(); // 数据加载完成后再放入
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getCityList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("areaid", localAreaID);
        HttpUtil.post(Constants.URL_POST_SPEAKING_CHOOSE_CITY, params, Constants.ACTIVITY_SPEAKING_SHARE_CHOOSE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonObject;
                    SpeakingCity city;
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            city = new SpeakingCity();
                            city.cityname = jsonObject.getString("cityname");
                            city.cityid = jsonObject.getString("cityid");
                            cityList.add(city);
                        }
                    }
                    setCityData(); // 数据加载完成后再放入
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getRoomList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("cityid", localCityID);
        HttpUtil.post(Constants.URL_POST_SPEAKING_CHOOSE_ROOM, params, Constants.ACTIVITY_SPEAKING_SHARE_CHOOSE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonObject;
                    SpeakingRoom room;
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            room = new SpeakingRoom();
                            jsonObject = data.getJSONObject(i);
                            room.roomname = jsonObject.getString("roomname");
                            room.roomid = jsonObject.getString("roomid");
                            roomList.add(room);
                        }
                    }
                    setRoomData(); // 数据加载完成后再放入
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_choose_area:
                SpeakingArea area = areaList.get(position);
                localAreaID = area.subAreaid;
                cityList.clear();
                getCityList();
                break;
            case R.id.spn_choose_city:
                SpeakingCity city = cityList.get(position);
                localCityID = city.cityid;
                roomList.clear();
                getRoomList();
                break;
            case R.id.spn_choose_room:
                SpeakingRoom room = roomList.get(position);
                localRoomID = room.roomid;
                localRoomName = room.roomname;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_sure:
                Intent intent = new Intent(this, SpeakingShare1Activity.class);
                intent.putExtra("localRoomID", localRoomID); // 设置返回数据
                intent.putExtra("localRoom", localRoomName); // 设置返回数据
                setResult(1, intent);
                finish();
                break;
            case R.id.btn_choose_cancel:
                finish();
                break;
        }
    }
}
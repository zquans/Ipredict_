package com.woyuce.activity.UI.Fragment.Speaking;

/**
 * Created by Administrator on 2016/9/22.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.woyuce.activity.Adapter.Speaking.SpeakingPart2Adapter;
import com.woyuce.activity.Bean.Speaking.SpeakingPart;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class FragmentPartTwo extends Fragment implements OnItemClickListener {

    private ListView listviewPart2;
    private ArrayList<SpeakingPart> partList = new ArrayList<>();
    private String localsubid, localsubname;

    private int checkNum = 0;

    public String returnSubid2() {                    //创建该方法给Activity调用， 返回Subid
        return localsubid;
    }

    public String returnSubname2() {
        return localsubname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speakingpart, container, false);
        initView(view);
        getJson();
        return view;
    }

    private void initView(View view) {
        listviewPart2 = (ListView) view.findViewById(R.id.listview_part);
        listviewPart2.setOnItemClickListener(this);
    }

    private void getJson() {

        OkGo.post(Constants.URL_POST_SPEAKING_SHARE_THREE_FRAGMENT).tag(Constants.FRAGMENT_SHARE_THREE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
        JSONObject jsonObject;
        SpeakingPart part;
        try {
            jsonObject = new JSONObject(response);
            int result = jsonObject.getInt("code");
            if (result == 0) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    jsonObject = data.getJSONObject(i);
                    part = new SpeakingPart();
                    part.pid = jsonObject.getString("pid");
                    if (Integer.parseInt(part.pid) == 1) {          //比较int值, 而不是比较栈空间        调用continue跳出本轮循环
                        continue;
                    }
                    part.subid = jsonObject.getString("subid");
                    part.subname = jsonObject.getString("subname");
                    partList.add(part);
                }
            }
            SpeakingPart2Adapter adapter = new SpeakingPart2Adapter(getActivity(), partList);
            listviewPart2.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpeakingPart localpart = partList.get(position);

        // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的ckBox实例的步骤
        SpeakingPart2Adapter.ViewHolder holder = (SpeakingPart2Adapter.ViewHolder) view.getTag();
        // 改变CheckBox的状态
        holder.ckBox.toggle();
        // 将CheckBox的选中状况记录下来
        SpeakingPart2Adapter.getIsSelected().put(position, holder.ckBox.isChecked());
        //  调整选定条目
        if (holder.ckBox.isChecked() == true) {
            if (checkNum == 1) {
                ToastUtil.showMessage(getActivity(), "part2只能选择一项哦，亲");
                holder.ckBox.setChecked(false);
            } else {
                checkNum++;
                localsubid = localpart.subid;
                localsubname = localpart.subname;
            }
        } else {
            checkNum--;
            localsubid = null;
            localsubname = null;
        }
    }
}
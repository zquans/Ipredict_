package com.woyuce.activity.UI.Fragment.Speaking;

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
import com.woyuce.activity.Adapter.Speaking.SpeakingPart1Adapter;
import com.woyuce.activity.Bean.Speaking.SpeakingPart;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class FragmentPartOne extends Fragment implements OnItemClickListener {

    private ListView listviewPart1;
    private List<SpeakingPart> partList = new ArrayList<>();
    private List<String> subidList = new ArrayList<>();
    private List<String> subnameList = new ArrayList<>();

    private String localsubid, localsubname;
    private int checkNum = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speakingpart, container, false);
        initView(view);
        getJson();
        return view;
    }

    private void initView(View view) {
        listviewPart1 = (ListView) view.findViewById(R.id.listview_part);
        listviewPart1.setOnItemClickListener(this);
    }

    public List<String> returnSubid1() {
        return subidList;
    }

    public String returnSubname1() {
        return localsubname;
    }

    public List<String> returnSubnameList() {
        return subnameList;
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
                    if (Integer.parseInt(part.pid) == 2) {
                        continue;
                    }
                    part.subid = jsonObject.getString("subid");
                    part.subname = jsonObject.getString("subname");
                    partList.add(part);
                }
            }
            SpeakingPart1Adapter adapter = new SpeakingPart1Adapter(getActivity(), partList);
            listviewPart1.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpeakingPart localpart = partList.get(position);
        SpeakingPart1Adapter.ViewHolder holder = (SpeakingPart1Adapter.ViewHolder) view.getTag();

		/*
         * 这里存在一个问题,设置Checked为false后，又无法再进行复选 **** 可以尝试的解决方法，深入理解toggle()方法,
		 * 以及记录Item选中状态的hashmap方法
		 */
        if (checkNum > 2) {
            ToastUtil.showMessage(getActivity(), "part1 最多只能选择3项哦，亲");
            holder.ckBox.setChecked(false);
            checkNum = 2;
        } else {
            if (holder.ckBox.isChecked()) {
                holder.ckBox.setChecked(false);
            } else {
                holder.ckBox.setChecked(true);
            }
            SpeakingPart1Adapter.getIsSelected().put(position, holder.ckBox.isChecked()); // 将CheckBox的Item,和,isChecked记录下来
            if (holder.ckBox.isChecked() == true) { // ***** 调整选定条目
                checkNum++;
                localsubid = localpart.subid;
                subidList.add(localsubid);

                subnameList.add(localpart.subname);
                localsubname = localpart.subname;
            } else {
                checkNum--;
                subidList.remove(subidList.size() - 1);
                subnameList.remove(subnameList.size() - 1);
                localsubname = null;
            }
        }
    }
}
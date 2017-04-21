package com.woyuce.activity.Controller.Speaking;

/**
 * Created by Administrator on 2016/9/22
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.woyuce.activity.Adapter.Speaking.SpeakingPart2Adapter;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Speaking.SpeakingPart;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentPartTwo extends Fragment implements OnItemClickListener {

    private ListView listviewPart2;
    private List<SpeakingPart> partList = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_speaking_part, container, false);
        initView(view);
        getJson();
        return view;
    }

    private void initView(View view) {
        listviewPart2 = (ListView) view.findViewById(R.id.listview_part);
        listviewPart2.setOnItemClickListener(this);
    }

    private void getJson() {
        HttpUtil.get(Constants.URL_POST_SPEAKING_SHARE_THREE_FRAGMENT, Constants.FRAGMENT_SHARE_THREE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject jsonObject;
                    SpeakingPart part;
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            jsonObject = data.getJSONObject(i);
                            part = new SpeakingPart();
                            part.pid = jsonObject.getString("pid");
                            if (Integer.parseInt(part.pid) == 1) {
                                continue;
                            }
                            part.subid = jsonObject.getString("subid");
                            part.subname = jsonObject.getString("subname");
                            partList.add(part);
                        }
                    }
//                    第二步，将数据放到适配器中
                    SpeakingPart2Adapter adapter = new SpeakingPart2Adapter(getActivity(), partList);
                    listviewPart2.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        // 调整选定条目
        if (holder.ckBox.isChecked()) {
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
package com.woyuce.activity.UI.Fragment.Speaking;

/**
 * Created by Administrator on 2016/9/22.
 */
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.Speaking.SpeakingPart2Adapter;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.Bean.Speaking.SpeakingPart;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.ToastUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class FragmentPartTwo extends Fragment implements OnItemClickListener{

    private ListView listviewPart2;
    private String URL_PART2 = "http://iphone.ipredicting.com/kysubNshare.aspx";
    private List<SpeakingPart> partList = new ArrayList<>();
    private String localsubid , localsubname;

    private int checkNum = 0;

    public String returnSubid2(){                    //创建该方法给Activity调用， 返回Subid
        return localsubid;
    }

    public String returnSubname2(){
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
        AppContext.getHttpQueue().cancelAll("post");
        StringRequest stringRequest = new StringRequest(Method.POST,URL_PART2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                SpeakingPart part;
                try {
                    jsonObject = new JSONObject(response);
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for(int i=0; i < data.length(); i++){
                            jsonObject = data.getJSONObject(i);
                            part = new SpeakingPart();
                            part.pid = jsonObject.getString("pid");
                            if(Integer.parseInt(part.pid) == 1){          //比较int值, 而不是比较栈空间        调用continue跳出本轮循环
                                continue;
                            }
                            part.subid = jsonObject.getString("subid");
                            part.subname = jsonObject.getString("subname");
                            partList.add(part);
                        }
                    } else {
                        LogUtil.e("code!=0 --DATA-BACK", "part2读取页面失败： " + jsonObject.getString("message"));
                    }
//                    第二步，将数据放到适配器中
                    SpeakingPart2Adapter adapter = new SpeakingPart2Adapter(getActivity(), partList);
                    listviewPart2.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong-BACK", "联接错误原因： " + error.getMessage() );
            }
        });
        stringRequest.setTag("fragmentparttwo");
        AppContext.getHttpQueue().add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SpeakingPart localpart = partList.get(position);

        SpeakingPart2Adapter.ViewHolder holder = (SpeakingPart2Adapter.ViewHolder) view.getTag();                         // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的ckBox实例的步骤
        holder.ckBox.toggle();   						                          // 改变CheckBox的状态
        SpeakingPart2Adapter.getIsSelected().put(position, holder.ckBox.isChecked());   // 将CheckBox的选中状况记录下来

        if (holder.ckBox.isChecked() == true) {     							  //   ***** 调整选定条目
            if(checkNum == 1){
                ToastUtil.showMessage(getActivity(), "part2只能选择一项哦，亲");
                holder.ckBox.setChecked(false);
            }else{
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
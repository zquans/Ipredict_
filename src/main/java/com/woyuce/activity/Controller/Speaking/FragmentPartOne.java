package com.woyuce.activity.Controller.Speaking;

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
import com.woyuce.activity.Adapter.Speaking.SpeakingPart1Adapter;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.Model.Speaking.SpeakingPart;
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

public class FragmentPartOne extends Fragment implements OnItemClickListener {

	private ListView listviewPart1;
	private String URL_PART1 = "http://iphone.ipredicting.com/kysubNshare.aspx";
	private List<SpeakingPart> partList = new ArrayList<>();
	private List<String> subidList = new ArrayList<>();
	private List<String> subnameList = new ArrayList<>();
	
	private String localsubid,localsubname;
	private int checkNum = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_speaking_part, container, false);
		initView(view);
		getJson();
		return view;
	}

	private void initView(View view) {
		listviewPart1 = (ListView) view.findViewById(R.id.listview_part);
		listviewPart1.setOnItemClickListener(this);
	}

	// 创建该方法给Activity调用， 返回SubidList
	public List<String> returnSubid1() {
		return subidList;
	}

	public String returnSubname1(){
		return localsubname;
	}

	// 创建该方法给Activity调用， 返回SubnameList
	public List<String> returnSubnameList() {
		return subnameList;
	}

	private void getJson() {
		AppContext.getHttpQueue().cancelAll("post");
		StringRequest stringRequest = new StringRequest(Method.POST, URL_PART1, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
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
							if (Integer.parseInt(part.pid) == 2) { // 比较int值,而不是比较栈空间,调用continue跳出本轮循环
								continue;
							}
							part.subid = jsonObject.getString("subid");
							part.subname = jsonObject.getString("subname");
							partList.add(part);
						}
					} else {
						LogUtil.e("code!=0 --DATA-BACK", "读取页面失败： " + jsonObject.getString("message"));
					}
					// 第二步，将数据放到适配器中
					SpeakingPart1Adapter adapter = new SpeakingPart1Adapter(getActivity(), partList);
					listviewPart1.setAdapter(adapter);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtil.e("Wrong-BACK", "联接错误原因： " + error.getMessage());
			}
		});
		stringRequest.setTag("fragmentpartone");
		AppContext.getHttpQueue().add(stringRequest);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SpeakingPart localpart =  partList.get(position);
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
			if(holder.ckBox.isChecked()){
				holder.ckBox.setChecked(false);
			}else{
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
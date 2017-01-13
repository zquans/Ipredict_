package com.woyuce.activity.Adapter.Speaking;

import java.util.List;

import com.woyuce.activity.Bean.Speaking.SpeakingCity;
import com.woyuce.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpeakingCityAdapter extends BaseAdapter{

	private List<SpeakingCity> mList;
	private LayoutInflater mInflater;
	
	public SpeakingCityAdapter(Context context, List<SpeakingCity> data) {
		mList = data;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_speaking_vote, null);
			viewHolder.txtCityName = (TextView) convertView.findViewById(R.id.txt_item_vote_area);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtCityName.setText(mList.get(position).cityname);		
		return convertView;
	}
	
	class ViewHolder{
		public TextView txtCityName;
	}
}

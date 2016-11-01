package com.woyuce.activity.Adapter;

import java.util.List;

import com.woyuce.activity.Bean.SpeakingMore;
import com.woyuce.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpeakingMoreAdapter extends BaseAdapter{

	private List<SpeakingMore> mList;
	private LayoutInflater mInflater;
	
	public SpeakingMoreAdapter(Context context, List<SpeakingMore> data) {
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
			convertView = mInflater.inflate(R.layout.gvitem_speakingmore, null);
			viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.txt_category);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtCategory.setText(mList.get(position).subCategoryname);
		viewHolder.txtCategory.setTextColor(Color.parseColor("#"+mList.get(position).fontColor));
		return convertView;
	}

	class ViewHolder{
		public TextView txtCategory;
	}
}
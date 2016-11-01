package com.woyuce.activity.Adapter;

import java.util.List;

import com.woyuce.activity.Bean.FreeSection;
import com.woyuce.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FreeSectionAdapter extends BaseAdapter{

	private List<FreeSection> mList;
	private LayoutInflater mInflater;
	
	public FreeSectionAdapter(Context context, List<FreeSection> data) {
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
			convertView = mInflater.inflate(R.layout.gvitem_freesection, null);
			viewHolder.txtsection = (TextView) convertView.findViewById(R.id.txt_section);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtsection.setBackgroundColor(Color.parseColor(mList.get(position).sectioncolor));   //   Color.parseColor(localPage.tag)��ɫ�����Ĺؼ�����
		viewHolder.txtsection.setText(mList.get(position).sectionname);
		return convertView;
	}
	
	class ViewHolder{
		public TextView txtsection;
	}
}

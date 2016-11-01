package com.woyuce.activity.Adapter;

import java.util.List;

import com.woyuce.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FreeListPageAdapter extends BaseAdapter {

	private List<String> mNoList;
	private List<String> mColorList;
	private LayoutInflater mInflater;

	public FreeListPageAdapter(Context context, List<String> mNoList, List<String> mColorList) {
		this.mNoList = mNoList;
		this.mColorList = mColorList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mNoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mNoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_freelistpage, null);
			viewHolder.txtpage = (TextView) convertView.findViewById(R.id.txt_listpage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtpage.setBackgroundColor(Color.parseColor(mColorList.get(position).trim()));
		viewHolder.txtpage.setText(mNoList.get(position));
		return convertView;
	}

	class ViewHolder {
		public TextView txtpage;
	}
}

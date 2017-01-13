package com.woyuce.activity.Adapter.Free;

import java.util.List;

import com.woyuce.activity.Bean.Free.FreeRange;
import com.woyuce.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FreeRangeAdapter extends BaseAdapter {

	private List<FreeRange> mList;
	private LayoutInflater mInflater;

	public FreeRangeAdapter(Context context, List<FreeRange> data) {
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
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_freerange, null);
			viewHolder.txt_name = (TextView) convertView.findViewById(R.id.text_item_rang);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.txt_name.setText(mList.get(position).getTitle());
		return convertView;
	}

	class ViewHolder {
		public TextView txt_name;
	}
}
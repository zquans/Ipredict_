package com.woyuce.activity.Adapter.Writting;

import java.util.List;

import com.woyuce.activity.Bean.Writting.WitSearch;
import com.woyuce.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WitSearchAdapter extends BaseAdapter{

	private List<WitSearch> mList;
	private LayoutInflater mInflater;
	
	public WitSearchAdapter(Context context, List<WitSearch> data) {
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
			convertView = mInflater.inflate(R.layout.listitem_witsearch, null);
			viewHolder.txtWitSearch = (TextView) convertView.findViewById(R.id.text_item_witsearch);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtWitSearch.setText(mList.get(position).subname);
		return convertView;
	}

	class ViewHolder{
		public TextView txtWitSearch;
	}
}
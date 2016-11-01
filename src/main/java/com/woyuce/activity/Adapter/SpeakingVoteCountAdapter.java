package com.woyuce.activity.Adapter;

import java.util.List;

import com.woyuce.activity.Bean.SpeakingVoteCount;
import com.woyuce.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpeakingVoteCountAdapter extends BaseAdapter{

	private List<SpeakingVoteCount> mList;
	private LayoutInflater mInflater;
	
	public SpeakingVoteCountAdapter(Context context, List<SpeakingVoteCount> data , boolean isAllCity) {
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_speakingstatis, null);
			viewHolder.voteView = (View) convertView.findViewById(R.id.view_vote);
			viewHolder.txtExamTitle = (TextView) convertView.findViewById(R.id.txt_item_examTitle);
			viewHolder.txtVoteNumber = (TextView) convertView.findViewById(R.id.txt_item_voteNumber);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

//		if(mIsAllCity == true){
//			Log.e("getX1", viewHolder.voteView.getX() + "");
//			viewHolder.voteView.setX((float) -(200 - Long.parseLong(mList.get(position).votetotal)*0.01));   // 用 "-" 号反向偏移X的值
//			Log.e("getX2", viewHolder.voteView.getX() + "");
//		}else{
//			viewHolder.voteView.setX((float) -(200 - Long.parseLong(mList.get(position).votetotal)));   // 用 "-" 号反向偏移X的值
//			Log.e("getX3", viewHolder.voteView.getX() + "");
//		}

		float f = Float.parseFloat(mList.get(position).votetotal);
		if(500< f && f< 1500){
			viewHolder.voteView.setX((float) -(10 -Long.parseLong(mList.get(position).votetotal)*0.001));   // 用 "-" 号反向偏移X的值
		}else if(250< f && f< 500){
			viewHolder.voteView.setX((float) -(300 - Long.parseLong(mList.get(position).votetotal)*0.3));   // 用 "-" 号反向偏移X的值
		}else{
			viewHolder.voteView.setX((float) -(400 - Long.parseLong(mList.get(position).votetotal)));   // 用 "-" 号反向偏移X的值
		}
		/*此处可以通过判断mList.get(position).votetotal的值范围，再乘以不同的系数，例如800~1100,200~400*/
//		viewHolder.voteView.setX((float) -(250 - Long.parseLong(mList.get(position).votetotal)*0.1));   // 用 "-" 号反向偏移X的值
		viewHolder.txtExamTitle.setText(mList.get(position).categoryName);
		viewHolder.txtVoteNumber.setText(mList.get(position).votetotal);
		return convertView;
	}

	class ViewHolder{
		public TextView txtExamTitle,txtVoteNumber;
		public View voteView;
	}
}
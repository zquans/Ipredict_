package com.woyuce.activity.Adapter.Free;

import java.util.List;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.Bean.Free.FreeSection;
import com.woyuce.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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
		//增加动画效果
		ViewHelper.setScaleX(viewHolder.txtsection, 0.8f);
		ViewHelper.setScaleY(viewHolder.txtsection, 0.8f);
		ViewPropertyAnimator.animate(viewHolder.txtsection).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
		ViewPropertyAnimator.animate(viewHolder.txtsection).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

		viewHolder.txtsection.setBackgroundColor(Color.parseColor(mList.get(position).sectioncolor));   //   Color.parseColor(localPage.tag)��ɫ�����Ĺؼ�����
		viewHolder.txtsection.setText(mList.get(position).sectionname);
		return convertView;
	}
	
	class ViewHolder{
		public TextView txtsection;
	}
}

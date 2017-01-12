package com.woyuce.activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.Bean.SpeakingVoteCount;
import com.woyuce.activity.R;

import java.util.List;

public class SpeakingVoteCountAdapter extends BaseAdapter {

    private List<SpeakingVoteCount> mList;
    private LayoutInflater mInflater;

    public SpeakingVoteCountAdapter(Context context, List<SpeakingVoteCount> data, boolean isAllCity) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_speakingstatis, null);
            viewHolder.itemView = (LinearLayout) convertView.findViewById(R.id.ll_speaking_statis_itemview);
            viewHolder.voteView = convertView.findViewById(R.id.view_vote);
            viewHolder.txtExamTitle = (TextView) convertView.findViewById(R.id.txt_item_examTitle);
            viewHolder.txtVoteNumber = (TextView) convertView.findViewById(R.id.txt_item_voteNumber);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //增加动画效果
        ViewHelper.setScaleX(viewHolder.itemView, 0.8f);
        ViewHelper.setScaleY(viewHolder.itemView, 0.8f);
        ViewPropertyAnimator.animate(viewHolder.itemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(viewHolder.itemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

        float f = Float.parseFloat(mList.get(position).votetotal);
        if (500 < f && f < 1500) {
            viewHolder.voteView.setX((float) -(10 - Long.parseLong(mList.get(position).votetotal) * 0.001));   // 用 "-" 号反向偏移X的值
        } else if (250 < f && f < 500) {
            viewHolder.voteView.setX((float) -(300 - Long.parseLong(mList.get(position).votetotal) * 0.3));   // 用 "-" 号反向偏移X的值
        } else {
            viewHolder.voteView.setX((float) -(400 - Long.parseLong(mList.get(position).votetotal)));   // 用 "-" 号反向偏移X的值
        }
        /*此处可以通过判断mList.get(position).votetotal的值范围，再乘以不同的系数，例如800~1100,200~400*/
//		viewHolder.voteView.setX((float) -(250 - Long.parseLong(mList.get(position).votetotal)*0.1));   // 用 "-" 号反向偏移X的值
        viewHolder.txtExamTitle.setText(mList.get(position).categoryName);
        viewHolder.txtVoteNumber.setText(mList.get(position).votetotal);
        return convertView;
    }

    class ViewHolder {
        private LinearLayout itemView;
        private TextView txtExamTitle, txtVoteNumber;
        private View voteView;
    }
}
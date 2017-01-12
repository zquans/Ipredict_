package com.woyuce.activity.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.Bean.SpeakingMore;
import com.woyuce.activity.R;

import java.util.List;

public class SpeakingMoreAdapter extends BaseAdapter {

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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gvitem_speakingmore, null);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.txt_category);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //增加动画效果
        ViewHelper.setScaleX(viewHolder.txtCategory, 0.8f);
        ViewHelper.setScaleY(viewHolder.txtCategory, 0.8f);
        ViewPropertyAnimator.animate(viewHolder.txtCategory).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(viewHolder.txtCategory).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

        viewHolder.txtCategory.setText(mList.get(position).subCategoryname);
        viewHolder.txtCategory.setTextColor(Color.parseColor("#" + mList.get(position).fontColor));
        return convertView;
    }

    class ViewHolder {
        private TextView txtCategory;
    }
}
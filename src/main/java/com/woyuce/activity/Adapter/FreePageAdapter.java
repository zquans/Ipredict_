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
import com.woyuce.activity.Bean.FreePage;
import com.woyuce.activity.R;

import java.util.List;

public class FreePageAdapter extends BaseAdapter {

    private List<FreePage> mList;
    private LayoutInflater mInflater;


    public FreePageAdapter(Context context, List<FreePage> data) {
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
            convertView = mInflater.inflate(R.layout.gvitem_freepage, null);
            viewHolder.txtpage = (TextView) convertView.findViewById(R.id.txt_pageno);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //增加动画效果
        ViewHelper.setScaleX(viewHolder.txtpage, 0.8f);
        ViewHelper.setScaleY(viewHolder.txtpage, 0.8f);
        ViewPropertyAnimator.animate(viewHolder.txtpage).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(viewHolder.txtpage).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

        viewHolder.txtpage.setBackgroundColor(Color.parseColor(mList.get(position).sub_color));
        viewHolder.txtpage.setText(mList.get(position).sub_name);
        return convertView;
    }

    class ViewHolder {
        public TextView txtpage;
    }
}

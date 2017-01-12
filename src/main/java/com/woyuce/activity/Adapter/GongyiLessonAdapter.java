package com.woyuce.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.Bean.GongyiAudio;
import com.woyuce.activity.R;

import java.util.List;

public class GongyiLessonAdapter extends BaseAdapter {

    private List<GongyiAudio> mList;
    private LayoutInflater mInflater;

    public GongyiLessonAdapter(Context context, List<GongyiAudio> data) {
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
            convertView = mInflater.inflate(R.layout.listitem_gongyilesson, null);
            viewHolder.txt_name = (TextView) convertView.findViewById(R.id.txt_audiolesson_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //增加动画效果
        ViewHelper.setScaleX(viewHolder.txt_name, 0.8f);
        ViewHelper.setScaleY(viewHolder.txt_name, 0.8f);
        ViewPropertyAnimator.animate(viewHolder.txt_name).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(viewHolder.txt_name).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

        viewHolder.txt_name.setText(mList.get(position).getTitle());
        return convertView;
    }

    class ViewHolder {
        public TextView txt_name;
    }
}
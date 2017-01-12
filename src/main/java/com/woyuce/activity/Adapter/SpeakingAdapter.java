package com.woyuce.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.Bean.SpeakingBean;
import com.woyuce.activity.R;

import java.util.List;

public class SpeakingAdapter extends BaseAdapter {

    private List<SpeakingBean> mList;
    private LayoutInflater mInflater;

    public SpeakingAdapter(Context context, List<SpeakingBean> data) {
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
            convertView = mInflater.inflate(R.layout.listitem_speaking, null);
            viewHolder.mItemView = (LinearLayout) convertView.findViewById(R.id.ll_speaking_itemview);
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.img_speaking_icon);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_speaking_username);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txt_speaking_content);
            viewHolder.txtRoom = (TextView) convertView.findViewById(R.id.txt_speaking_examroom);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.txt_speaking_examtime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //增加动画效果
        ViewHelper.setScaleX(viewHolder.mItemView, 0.8f);
        ViewHelper.setScaleY(viewHolder.mItemView, 0.8f);
        ViewPropertyAnimator.animate(viewHolder.mItemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(viewHolder.mItemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

        viewHolder.imgIcon.setImageResource(R.mipmap.img_duck);
        viewHolder.txtName.setText(mList.get(position).uname);
        viewHolder.txtContent.setText(mList.get(position).message);
        viewHolder.txtRoom.setText(mList.get(position).examroom);
        viewHolder.txtTime.setText(mList.get(position).vtime);
        return convertView;
    }

    class ViewHolder {
        private LinearLayout mItemView;
        private ImageView imgIcon;
        private TextView txtName, txtContent, txtRoom, txtTime;
    }
}

package com.woyuce.activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.Act.GongyiContentActivity;
import com.woyuce.activity.Bean.GongyiAudio;
import com.woyuce.activity.R;

import java.util.List;

public class GongyiLessonAdapter extends RecyclerView.Adapter<GongyiLessonAdapter.MyViewHolder> {

    private List<GongyiAudio> mList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public GongyiLessonAdapter(Context context, List<GongyiAudio> data) {
        mList = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public GongyiLessonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewholder = new MyViewHolder(
                mLayoutInflater.inflate(R.layout.listitem_gongyilesson, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(GongyiLessonAdapter.MyViewHolder holder, final int position) {
        //增加动画效果
        ViewHelper.setScaleX(holder.itemView, 0.8f);
        ViewHelper.setScaleY(holder.itemView, 0.8f);
        ViewPropertyAnimator.animate(holder.itemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(holder.itemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GongyiAudio audio = mList.get(position);
                Intent intent = new Intent(mContext, GongyiContentActivity.class);
                intent.putExtra("url", audio.getUrl());
                intent.putExtra("title", audio.getTitle());
                mContext.startActivity(intent);
            }
        });
        holder.txt_name.setText(mList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mItemView;
        TextView txt_name;

        public MyViewHolder(View view) {
            super(view);
            mItemView = (RelativeLayout) view.findViewById(R.id.relativelayout_activity_itemview);
            txt_name = (TextView) view.findViewById(R.id.txt_audiolesson_content);
        }
    }
}
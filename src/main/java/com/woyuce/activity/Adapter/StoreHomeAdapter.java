package com.woyuce.activity.Adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.Bean.StoreBean;
import com.woyuce.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreHomeAdapter extends RecyclerView.Adapter<StoreHomeAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<StoreBean> mDatas;
    private Context context;
    private Integer screen_width;

    public StoreHomeAdapter(Context context, List<StoreBean> mList, Integer screen_width) {
        this.mDatas = mList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.screen_width = screen_width;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewholder = new MyViewHolder(
                mLayoutInflater.inflate(R.layout.recycleritem_store_tab1, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        //增加动画效果
        ViewHelper.setScaleX(holder.itemView, 0.8f);
        ViewHelper.setScaleY(holder.itemView, 0.8f);
        ViewPropertyAnimator.animate(holder.itemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(holder.itemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

        holder.mTxt.setText(mDatas.get(pos).getTitle());
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 2);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mDatas.get(pos).getGoods_result().size() == 1) {
                    return 2;
                }
                return 1;
            }
        });
        holder.mRecycler.setLayoutManager(mGridLayoutManager);
        RecyclerView.Adapter mmAdapter = new StoreGoodsAdapter(context, mDatas.get(pos).getGoods_result(), screen_width);
        holder.mRecycler.setAdapter(mmAdapter);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTxt;
        RecyclerView mRecycler;

        public MyViewHolder(View view) {
            super(view);
            mTxt = (TextView) view.findViewById(R.id.txt_item_recycler);
            mRecycler = (RecyclerView) view.findViewById(R.id.recycler_item_recycler);
        }
    }
}
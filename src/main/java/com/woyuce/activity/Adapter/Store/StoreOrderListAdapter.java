package com.woyuce.activity.Adapter.Store;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.woyuce.activity.UI.Act.Store.StoreOrderActivity;
import com.woyuce.activity.Bean.Store.StoreOrder;
import com.woyuce.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreOrderListAdapter extends RecyclerView.Adapter<StoreOrderListAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<StoreOrder> mList;
    private Context context;

    public StoreOrderListAdapter(Context context, List<StoreOrder> mList) {
        this.mList = mList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewholder = new MyViewHolder(
                mLayoutInflater.inflate(R.layout.recycleritem_storeorderlist, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int pos) {
        //增加动画效果
        ViewHelper.setScaleX(holder.itemView, 0.8f);
        ViewHelper.setScaleY(holder.itemView, 0.8f);
        ViewPropertyAnimator.animate(holder.itemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        ViewPropertyAnimator.animate(holder.itemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
        holder.mTxtOrder.setText("订单号:" + mList.get(pos).getOrder_no());
        holder.mTxtTime.setText("订单时间:" + mList.get(pos).getCreate_at());
        holder.mTxtPrice.setText("实付款:" + mList.get(pos).getPrice());
        holder.mTxtStatue.setText(mList.get(pos).getOrder_status());
        boolean isPay;
        if (mList.get(pos).getOrder_status().equals("Pay")) {
            isPay = true;
            //如果已经支付，则隐藏,留给子RecyclerView去做评论
            holder.mTxtToPay.setVisibility(View.GONE);
        } else {
            isPay = false;
            //如果未支付，则去支付
            holder.mTxtToPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StoreOrderActivity.class);
                    intent.putExtra("local_order_id", mList.get(pos).getId());
                    intent.putExtra("local_order_no", mList.get(pos).getOrder_no());
                    intent.putExtra("total_price", mList.get(pos).getPrice());
                    intent.putExtra("goods_name", mList.get(pos).getUser_order_details().get(0).getGoods_title() + "\r...");
                    context.startActivity(intent);
                }
            });
        }
        holder.mRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.mRecycler.setHasFixedSize(true);
        RecyclerView.Adapter mAdapter = new StoreOrderGoodsAdapter(context, mList.get(pos).getUser_order_details(), isPay);
        holder.mRecycler.setAdapter(mAdapter);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtOrder, mTxtTime, mTxtPrice, mTxtStatue, mTxtToPay;
        RecyclerView mRecycler;

        public MyViewHolder(View view) {
            super(view);
            mTxtOrder = (TextView) view.findViewById(R.id.txt_listitem_orderlist_num);
            mTxtTime = (TextView) view.findViewById(R.id.txt_listitem_orderlist_time);
            mTxtPrice = (TextView) view.findViewById(R.id.txt_listitem_orderlist_price);
            mRecycler = (RecyclerView) view.findViewById(R.id.recyclerview_listitem_orderlist);
            mTxtToPay = (TextView) view.findViewById(R.id.btn_listitem_orderlist_pay);
            mTxtStatue = (TextView) view.findViewById(R.id.txt_listitem_orderlist_statue);
        }
    }
}
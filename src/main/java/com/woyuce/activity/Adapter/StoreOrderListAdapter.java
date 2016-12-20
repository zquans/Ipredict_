package com.woyuce.activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woyuce.activity.Act.StoreCommentActivity;
import com.woyuce.activity.Act.StoreOrderActivity;
import com.woyuce.activity.Bean.StoreOrder;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

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
        holder.mTxtOrder.setText("订单号:" + mList.get(pos).getOrder_no());
        holder.mTxtTime.setText("订单时间:" + mList.get(pos).getCreate_at());
        holder.mTxtPrice.setText("实付款:" + mList.get(pos).getPrice());
        holder.mTxtStatue.setText(mList.get(pos).getOrder_status());
        holder.mRecycler.setLayoutManager(new LinearLayoutManager(context));
        RecyclerView.Adapter mAdapter = new StoreOrderGoodsAdapter(context, mList.get(pos).getUser_order_details());
        holder.mRecycler.setAdapter(mAdapter);
        if (mList.get(pos).getOrder_status().equals("Pay")) {
            //如果已经支付，则去评价
//            holder.mTxtToPay.setVisibility(View.GONE);
            holder.mTxtToPay.setText("去评论");
            holder.mTxtToPay.setBackgroundColor(Color.parseColor("#f7941d"));
            holder.mTxtToPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StoreCommentActivity.class);
                    intent.putExtra("local_order_id", mList.get(pos).getUser_order_details().get(0).getId());
                    intent.putExtra("local_order_no", mList.get(pos).getOrder_no());
                    intent.putExtra("total_price", mList.get(pos).getPrice());
                    intent.putExtra("goods_name", mList.get(pos).getUser_order_details().get(0).getGoods_title() + "\r...");
                    LogUtil.i(mList.get(pos).getUser_order_details().get(0).getId() + "--------");
                    context.startActivity(intent);
                }
            });
        } else {
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
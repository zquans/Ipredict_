package com.woyuce.activity.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woyuce.activity.Bean.StoreOrder;
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
        holder.mTxtOrder.setText(mList.get(pos).getOrder_no());
        holder.mTxtTime.setText(mList.get(pos).getCreate_at());
        holder.mTxtPrice.setText(mList.get(pos).getPrice());

        holder.mRecycler.setLayoutManager(new LinearLayoutManager(context));
        RecyclerView.Adapter mAdapter = new StoreOrderGoodsAdapter(context, mList.get(pos).getUser_order_details());
        holder.mRecycler.setAdapter(mAdapter);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtOrder, mTxtTime, mTxtPrice;
        RecyclerView mRecycler;

        public MyViewHolder(View view) {
            super(view);
            mTxtOrder = (TextView) view.findViewById(R.id.txt_listitem_orderlist_num);
            mTxtTime = (TextView) view.findViewById(R.id.txt_listitem_orderlist_time);
            mTxtPrice = (TextView) view.findViewById(R.id.txt_listitem_orderlist_price);
            mRecycler = (RecyclerView) view.findViewById(R.id.recyclerview_listitem_orderlist);
        }
    }
}
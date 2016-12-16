package com.woyuce.activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woyuce.activity.Act.StoreGoodsActivity;
import com.woyuce.activity.Act.WebNoCookieActivity;
import com.woyuce.activity.Bean.StoreBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.RecyclerItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreHomeAdapter extends RecyclerView.Adapter<StoreHomeAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<StoreBean> mDatas;
    private Context context;

    public StoreHomeAdapter(Context context, List<StoreBean> mList) {
        this.mDatas = mList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewholder = new MyViewHolder(
                mLayoutInflater.inflate(R.layout.recycleritem_store_tab1, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int pos) {
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
        RecyclerView.Adapter mmAdapter = new StoreGoodsAdapter(context, mDatas.get(pos).getGoods_result());
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
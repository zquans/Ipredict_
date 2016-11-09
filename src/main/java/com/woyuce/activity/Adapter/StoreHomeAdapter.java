package com.woyuce.activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woyuce.activity.Activity.StoreGoodsActivity;
import com.woyuce.activity.Bean.StoreBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.RecyclerItemClickListener;
import com.woyuce.activity.Utils.ToastUtil;

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
        holder.mTxt.setText("---------" + mDatas.get(pos).getTitle() + "---------");
        holder.mRecycler.setLayoutManager(new GridLayoutManager(context, 2));
        RecyclerView.Adapter mmAdapter = new StoreGoodsAdapter(context, mDatas.get(pos).getGoods_result());
        holder.mRecycler.setAdapter(mmAdapter);
        //加入Item点击事件
        holder.mRecycler.addOnItemTouchListener(new RecyclerItemClickListener(context, holder.mRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtil.showMessage(context, "what ? = " + position + pos);
                //通过反向获取嵌套数组,拿到所需的参数
                Intent intent = new Intent(context, StoreGoodsActivity.class);
                intent.putExtra("goods_id", mDatas.get(pos).getGoods_result().get(position).getGoods_id());
                intent.putExtra("goods_sku_id", mDatas.get(pos).getGoods_result().get(position).getGoods_sku_id());
                intent.putExtra("goods_title", mDatas.get(pos).getGoods_result().get(position).getGoods_title());
                intent.putExtra("sales_price", mDatas.get(pos).getGoods_result().get(position).getSales_price());
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                ToastUtil.showMessage(context, "what ?? = " + position);
            }
        }));
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
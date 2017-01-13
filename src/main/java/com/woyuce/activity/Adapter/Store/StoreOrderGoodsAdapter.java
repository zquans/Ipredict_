package com.woyuce.activity.Adapter.Store;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.UI.Activity.Store.StoreCommentActivity;
import com.woyuce.activity.UI.Activity.Store.StoreGoodsActivity;
import com.woyuce.activity.Bean.Store.StoreGoods;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 * 商品订单列表中二级列表的适配器RecyclerView
 */
public class StoreOrderGoodsAdapter extends RecyclerView.Adapter<StoreOrderGoodsAdapter.MViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<StoreGoods> mList;
    private Context mContext;
    private boolean isPay = false;

    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error_horizon)
            .showImageOnFail(R.mipmap.img_error_horizon).cacheInMemory(true).cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public StoreOrderGoodsAdapter(Context context, List<StoreGoods> list, boolean is_pay) {
        this.isPay = is_pay;
        this.mList = list;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MViewHolder viewholder = new MViewHolder(
                mLayoutInflater.inflate(R.layout.recycleritem_ordergoods, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(StoreOrderGoodsAdapter.MViewHolder holder, final int position) {
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StoreGoodsActivity.class);
                intent.putExtra("goods_id", mList.get(position).getGoods_id());
                intent.putExtra("goods_sku_id", mList.get(position).getGoods_sku_id());
                intent.putExtra("goods_title", mList.get(position).getGoods_title());
                intent.putExtra("sales_price", mList.get(position).getSales_price());
                intent.putExtra("can_go_store_back", "yes");
                mContext.startActivity(intent);
            }
        });
        holder.mTxtName.setText(mList.get(position).getGoods_title());
        holder.mTxtSpecName.setText(mList.get(position).getGoods_property());
        holder.mTxtGoodsNum.setText("x\r" + mList.get(position).getQuantity());
        if (!isPay) {
            holder.mTxtToComment.setVisibility(View.GONE);
        }
        if (mList.get(position).getIs_comment().equals("true")) {
            holder.mTxtToComment.setText("已评论");
        } else {
            holder.mTxtToComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, StoreCommentActivity.class);
                    intent.putExtra("local_order_id", mList.get(position).getId());
                    intent.putExtra("goods_name", mList.get(position).getGoods_title());
                    LogUtil.i(mList.get(position).getId() + "--------");
                    mContext.startActivity(intent);
                }
            });
        }
        ImageLoader.getInstance().displayImage(mList.get(position).getThumb_img(), holder.mImg, options);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mItemLayout;
        ImageView mImg;
        TextView mTxtName, mTxtSpecName, mTxtGoodsNum, mTxtToComment;

        public MViewHolder(View itemView) {
            super(itemView);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.rl_listitem_orderlist_goods);
            mImg = (ImageView) itemView.findViewById(R.id.img_listitem_ordergoods_imgurl);
            mTxtName = (TextView) itemView.findViewById(R.id.txt_listitem_ordergoods_name);
            mTxtSpecName = (TextView) itemView.findViewById(R.id.txt_listitem_ordergoods_specname);
            mTxtGoodsNum = (TextView) itemView.findViewById(R.id.txt_listitem_ordergoods_goodsnum);
            mTxtToComment = (TextView) itemView.findViewById(R.id.txt_listitem_ordergoods_tocomment);
        }
    }
}
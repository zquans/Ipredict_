package com.woyuce.activity.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreOrderGoodsAdapter extends RecyclerView.Adapter<StoreOrderGoodsAdapter.MViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<StoreGoods> mList;

    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
            .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public StoreOrderGoodsAdapter(Context context, List<StoreGoods> list) {
        this.mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MViewHolder viewholder = new MViewHolder(
                mLayoutInflater.inflate(R.layout.recycleritem_ordergoods, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(StoreOrderGoodsAdapter.MViewHolder holder, int position) {
        holder.mTxtName.setText(mList.get(position).getGoods_title());
        holder.mTxtSpecName.setText(mList.get(position).getGoods_property());
        holder.mTxtGoodsNum.setText(mList.get(position).getQuantity());
        ImageLoader.getInstance().displayImage(mList.get(position).getThumb_img(), holder.mImg, options);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTxtName, mTxtSpecName, mTxtGoodsNum;

        public MViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img_listitem_ordergoods_imgurl);
            mTxtName = (TextView) itemView.findViewById(R.id.txt_listitem_ordergoods_name);
            mTxtSpecName = (TextView) itemView.findViewById(R.id.txt_listitem_ordergoods_specname);
            mTxtGoodsNum = (TextView) itemView.findViewById(R.id.txt_listitem_ordergoods_goodsnum);
        }
    }
}
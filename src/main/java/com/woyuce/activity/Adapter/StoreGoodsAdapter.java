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
public class StoreGoodsAdapter extends RecyclerView.Adapter<StoreGoodsAdapter.MViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<StoreGoods> mDatas;

    public StoreGoodsAdapter(Context context, List<StoreGoods> mList) {
        this.mDatas = mList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MViewHolder viewholder = new MViewHolder(
                mLayoutInflater.inflate(R.layout.recycleritem_store_goods, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(StoreGoodsAdapter.MViewHolder holder, int position) {
        //淘宝商品价格做区间
        String local_price = mDatas.get(position).getSales_price();
        if (mDatas.get(position).getGoods_title().contains("http") && local_price.contains(".") && !local_price.contains(".0")) {
            holder.mTxt.setText("￥" + local_price.replace(".", "～"));
        } else {
            holder.mTxt.setText("￥" + local_price);
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(mDatas.get(position).getThumb_img(), holder.mImg, options);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTxt;

        public MViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img_item_recycler_goods);
            mTxt = (TextView) itemView.findViewById(R.id.txt_item_recycler_goods);
        }
    }

}
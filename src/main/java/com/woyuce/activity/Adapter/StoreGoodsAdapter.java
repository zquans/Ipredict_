package com.woyuce.activity.Adapter;

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
import com.woyuce.activity.Act.StoreGoodsActivity;
import com.woyuce.activity.Act.WebNoCookieActivity;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class StoreGoodsAdapter extends RecyclerView.Adapter<StoreGoodsAdapter.MViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<StoreGoods> mDatas;
    private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error_horizon)
            .showImageOnFail(R.mipmap.img_error_horizon).cacheInMemory(true).cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public StoreGoodsAdapter(Context context, List<StoreGoods> mList) {
        this.mDatas = mList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MViewHolder viewholder = new MViewHolder(
                mLayoutInflater.inflate(R.layout.recycleritem_store_goods, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(StoreGoodsAdapter.MViewHolder holder, final int position) {
        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDatas.get(position).getGoods_title().contains("http")) {
                    Intent intent = new Intent(mContext, WebNoCookieActivity.class);
                    intent.putExtra("URL", mDatas.get(position).getGoods_title());
                    intent.putExtra("TITLE", "淘宝商品");
                    intent.putExtra("COLOR", "#f7941d");
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, StoreGoodsActivity.class);
                    intent.putExtra("goods_id", mDatas.get(position).getGoods_id());
                    intent.putExtra("goods_sku_id", mDatas.get(position).getGoods_sku_id());
                    intent.putExtra("goods_title", mDatas.get(position).getGoods_title());
                    intent.putExtra("sales_price", mDatas.get(position).getSales_price());
                    mContext.startActivity(intent);
                }
            }
        });
        //淘宝商品价格要做区间
        String local_price = mDatas.get(position).getSales_price();
        if (mDatas.get(position).getGoods_title().contains("http") && local_price.contains(".") && !local_price.contains(".0")) {
            holder.mTxt.setText("￥" + local_price.replace(".", "～"));
        } else {
            holder.mTxt.setText("￥" + local_price);
        }

        ImageLoader.getInstance().displayImage(mDatas.get(position).getThumb_img(), holder.mImg, options);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRelativeLayout;
        ImageView mImg;
        TextView mTxt;

        public MViewHolder(View itemView) {
            super(itemView);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_item_recycler);
            mImg = (ImageView) itemView.findViewById(R.id.img_item_recycler_goods);
            mTxt = (TextView) itemView.findViewById(R.id.txt_item_recycler_goods);
        }
    }
}
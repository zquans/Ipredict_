package com.woyuce.activity.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/28.
 */
public class OrderGoodsAdapter extends BaseAdapter {

    private ArrayList<StoreGoods> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
            .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public OrderGoodsAdapter(Context context, ArrayList<StoreGoods> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_ordergoods, null);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txt_listitem_ordergoods_name);
            viewHolder.mTxtSpecName = (TextView) convertView.findViewById(R.id.txt_listitem_ordergoods_specname);
            viewHolder.mTxtGoodsNum = (TextView) convertView.findViewById(R.id.txt_listitem_ordergoods_goodsnum);
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.img_listitem_ordergoods_imgurl);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(mList.get(position).getGoods_title());
        viewHolder.mTxtSpecName.setText(mList.get(position).getGoods_property());
        viewHolder.mTxtGoodsNum.setText(mList.get(position).getQuantity());

        ImageLoader.getInstance().displayImage(mList.get(position).getGoods_thumb_img_url(), viewHolder.mImg, options);
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtName, mTxtSpecName, mTxtGoodsNum;
        public ImageView mImg;
    }
}
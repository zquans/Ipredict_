package com.woyuce.activity.Adapter;

import android.annotation.SuppressLint;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StoreShowOrderAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<StoreGoods> mList = new ArrayList<>();

    public StoreShowOrderAdapter(Context context, ArrayList<StoreGoods> mShowOrderList) {
        mInflater = LayoutInflater.from(context);
        this.mList = mShowOrderList;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_storeshoworder, null);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txt_goodscomment_username);
            viewHolder.mTxtComment = (TextView) convertView.findViewById(R.id.txt_goodscomment_comment);
            viewHolder.mTxtTime = (TextView) convertView.findViewById(R.id.txt_goodscomment_time);
            viewHolder.mTxtContent = (TextView) convertView.findViewById(R.id.txt_goodscomment_content);
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.img_goodscomment_showgoods);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(mList.get(position).getCreate_by_name());
        viewHolder.mTxtTime.setText(mList.get(position).getShow_at());
        viewHolder.mTxtContent.setText(mList.get(position).getComment_text());

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_error)
                .showImageOnFail(R.mipmap.img_error)
                .displayer(new RoundedBitmapDisplayer(10))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(mList.get(position).getImg_url(), viewHolder.mImg, options);
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtName, mTxtTime, mTxtContent, mTxtComment;
        public ImageView mImg;
    }
}

package com.woyuce.activity.Adapter.Store;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.woyuce.activity.Act.Store.ShowImgActivity;
import com.woyuce.activity.Bean.Store.StoreGoods;
import com.woyuce.activity.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StoreShowOrderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StoreGoods> mList = new ArrayList<>();

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.img_error_horizon)
            .showImageOnFail(R.mipmap.img_error_horizon)
            .displayer(new RoundedBitmapDisplayer(10))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public StoreShowOrderAdapter(Context context, ArrayList<StoreGoods> mShowOrderList) {
        this.mContext = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
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

        ImageLoader.getInstance().displayImage(mList.get(position).getImg_url(), viewHolder.mImg, options);

        viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowImgActivity.class);
                intent.putExtra("img_url", mList.get(position).getImg_url());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtName, mTxtTime, mTxtContent, mTxtComment;
        public ImageView mImg;
    }
}

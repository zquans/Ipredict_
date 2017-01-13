package com.woyuce.activity.Adapter.Weibo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.R;

import java.util.List;

public class WeiboInfoImgAdapter extends BaseAdapter {

    private List<String> mList;
    private LayoutInflater mInflater;

    public WeiboInfoImgAdapter(Context context, List<String> data) {
        mList = data;
        mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.gvitem_weiboinfoimg, null);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img_item_weiboinfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String url = mList.get(position);
        viewHolder.img.setTag(url);

        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(url, viewHolder.img, options);

        return convertView;
    }

    class ViewHolder {
        public ImageView img;
    }
}
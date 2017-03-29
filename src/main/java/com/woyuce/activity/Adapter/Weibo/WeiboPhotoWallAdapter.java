//package com.woyuce.activity.Adapter;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.woyuce.activity.R;
//
//import java.util.List;
//
///**
// * Created by Administrator on 2016/9/27.
// */
//public class WeiboPhotoWallAdapter extends BaseAdapter {
//
//    private List<String> mList;
//    private LayoutInflater mInflater;
//
//    private DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .cacheInMemory(true)
//            .cacheOnDisk(true)
//            .bitmapConfig(Bitmap.Config.ARGB_8888)
//            .displayer(new RoundedBitmapDisplayer(5))
//            .build();
//
//    public WeiboPhotoWallAdapter(Context context, List<String> mList) {
//        this.mList = mList;
//        mInflater = LayoutInflater.from(context);
//    }
//
//    @Override
//    public int getCount() {
//        return mList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = mInflater.inflate(R.layout.gvitem_weibo_photo_wall, null);
//            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.img_item_weibophotowall);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        String url = mList.get(position);
//        viewHolder.mImg.setTag(url);
//        if (position == 0) {
//            viewHolder.mImg.setBackgroundResource(R.mipmap.block_answer);
//        } else {
//            ImageLoader.getInstance().displayImage("file://" + url, viewHolder.mImg, options);
//        }
//        return convertView;
//    }
//
//    class ViewHolder {
//        public ImageView mImg;
//    }
//}

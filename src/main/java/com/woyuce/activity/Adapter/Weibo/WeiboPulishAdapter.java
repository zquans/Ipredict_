//package com.woyuce.activity.Adapter;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.net.Uri;
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
//import com.woyuce.activity.Utils.LogUtil;
//
//import java.util.List;
//
///**
// * Created by Administrator on 2016/9/27.
// */
//public class WeiboPulishAdapter extends BaseAdapter {
//
//    private List<Uri> mList;
//    private LayoutInflater mInflater;
//    private DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .cacheInMemory(true).cacheOnDisk(true)
//            .bitmapConfig(Bitmap.Config.ARGB_8888)
//            .displayer(new RoundedBitmapDisplayer(5))
//            .build();
//
//    public WeiboPulishAdapter(Context context, List<Uri> mList) {
//        this.mList = mList;
//        mInflater = LayoutInflater.from(context);
//    }
//
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
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder ;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = mInflater.inflate(R.layout.gvitem_weibophotowawll, null);
//            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.img_item_weibophotowall);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        ImageLoader.getInstance().displayImage(String.valueOf(mList.get(position)), viewHolder.mImg, options);
//        return convertView;
//    }
//
//    class ViewHolder {
//        public ImageView mImg;
//    }
//}

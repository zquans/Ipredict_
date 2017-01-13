package com.woyuce.activity.Adapter.Weibo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woyuce.activity.Bean.Weibo.WeiboBean;
import com.woyuce.activity.R;

import java.util.List;


/**
 * 微博精选的Adapter
 */
public class WeiboInfoAdapter extends BaseAdapter {

    private List<WeiboBean> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public WeiboInfoAdapter(Context context, List<WeiboBean> data) {
        mList = data;
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
            convertView = mInflater.inflate(R.layout.listitem_weiboinfo, null);
            viewHolder.imgPath = (ImageView) convertView.findViewById(R.id.img_item_weibo);
            viewHolder.txtBody = (TextView) convertView.findViewById(R.id.body_item_weibo);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.time_item_weibo);
            viewHolder.txtAuthor = (TextView) convertView.findViewById(R.id.author_item_weibo);
//            viewHolder.mListView = (InnerListView) convertView.findViewById(R.id.listview_item_weiboinfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        String url = mList.get(position).pulish_image;
//        viewHolder.imgPath.setTag(url);

//        DisplayImageOptions options = new DisplayImageOptions.Builder().
//                showImageOnLoading(R.mipmap.img_error)
//                .showImageOnFail(R.mipmap.img_error)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .displayer(new RoundedBitmapDisplayer(10))
//                .bitmapConfig(Bitmap.Config.RGB_565).build();
//        ImageLoader.getInstance().displayImage(url, viewHolder.imgPath, options);

        viewHolder.txtBody.setText(mList.get(position).body);
        viewHolder.txtTime.setText("时间: " + mList.get(position).date_created);
        viewHolder.txtAuthor.setText(mList.get(position).author);

//        List<String> mStringList = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            mStringList.add("string + " + i);
//        }
//        ArrayAdapter mAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, mStringList);
//        viewHolder.mListView.setAdapter(mAdapter);
        return convertView;
    }

    class ViewHolder {
        //        public InnerListView mListView;
        public ImageView imgPath;
        public TextView txtBody, txtTime, txtAuthor;
    }
}
package com.woyuce.activity.Adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Bean.NetBean;
import com.woyuce.activity.Bean.NetLessonBean;
import com.woyuce.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class NetClassLessonAdapter extends BaseAdapter {

	private List<NetLessonBean> mList;
	private LayoutInflater mInflater;

	public NetClassLessonAdapter(Context context, List<NetLessonBean> data) {
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
			convertView = mInflater.inflate(R.layout.gvitem_netclasslesson, null);
			viewHolder.imgPath = (ImageView) convertView.findViewById(R.id.img_item_wanglesson);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String url = mList.get(position).getImgPath().trim();
		viewHolder.imgPath.setTag(url);

		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
				.showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(false)
				.bitmapConfig(Config.RGB_565).build();
		ImageLoader.getInstance().displayImage(url, viewHolder.imgPath, options);
		return convertView;
	}

	class ViewHolder {
		public ImageView imgPath;
	}
}
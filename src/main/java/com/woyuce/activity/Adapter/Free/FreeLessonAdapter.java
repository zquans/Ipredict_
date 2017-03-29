package com.woyuce.activity.Adapter.Free;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.woyuce.activity.Model.Free.FreeLesson;
import com.woyuce.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class FreeLessonAdapter extends BaseAdapter {

	private List<FreeLesson> mList;
	private LayoutInflater mInflater;

	public FreeLessonAdapter(Context context, List<FreeLesson> data) {
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
			convertView = mInflater.inflate(R.layout.gvitem_free_lesson, null);
			viewHolder.imgPath = (ImageView) convertView.findViewById(R.id.img_item_lesson);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String url = mList.get(position).image;
		viewHolder.imgPath.setTag(url);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.mipmap.img_error)
				.showImageOnFail(R.mipmap.img_error)
				.displayer(new RoundedBitmapDisplayer(10))
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoader.getInstance().displayImage(url, viewHolder.imgPath, options);

		return convertView;
	}

	class ViewHolder {
		public ImageView imgPath;
	}
}
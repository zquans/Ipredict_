package com.woyuce.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woyuce.activity.Bean.GongyiAudio;
import com.woyuce.activity.R;

import java.util.List;

public class GongyiLessonAdapter extends BaseAdapter {

    private List<GongyiAudio> mList;
    private LayoutInflater mInflater;

    public GongyiLessonAdapter(Context context, List<GongyiAudio> data) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_gongyilesson, null);
            viewHolder.txt_name = (TextView) convertView.findViewById(R.id.txt_audiolesson_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txt_name.setText(mList.get(position).title);
        return convertView;
    }

    class ViewHolder {
        public TextView txt_name;
    }
}
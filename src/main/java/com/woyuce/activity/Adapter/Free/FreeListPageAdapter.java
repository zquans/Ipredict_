package com.woyuce.activity.Adapter.Free;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woyuce.activity.Model.Free.FreePage;
import com.woyuce.activity.R;

import java.util.List;

public class FreeListPageAdapter extends BaseAdapter {

    private List<FreePage> mDataList;
    private LayoutInflater mInflater;

    public FreeListPageAdapter(Context context, List<FreePage> list) {
        this.mDataList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
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
            convertView = mInflater.inflate(R.layout.listitem_free_list_page, parent, false);
            viewHolder.txtpage = (TextView) convertView.findViewById(R.id.txt_listpage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtpage.setBackgroundColor(Color.parseColor(mDataList.get(position).sub_color.trim()));
        viewHolder.txtpage.setText(mDataList.get(position).sub_name);
        return convertView;
    }

    class ViewHolder {
        public TextView txtpage;
    }
}
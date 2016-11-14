package com.woyuce.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;

import java.util.List;

public class StoreSpcAdapter extends BaseAdapter {

    private List<StoreGoods> mList;
    private LayoutInflater mInflater;

    public StoreSpcAdapter(Context context, List<StoreGoods> data) {
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
            convertView = mInflater.inflate(R.layout.listitem_storegoodsspc, null);
            viewHolder.txtGoal = (TextView) convertView.findViewById(R.id.txt_storegoods_spc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtGoal.setText(mList.get(position).getAttr_text());
        return convertView;
    }

    class ViewHolder {
        public TextView txtGoal;
    }
}

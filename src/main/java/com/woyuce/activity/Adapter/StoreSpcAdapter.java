package com.woyuce.activity.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;

import java.util.ArrayList;
import java.util.List;

public class StoreSpcAdapter extends BaseAdapter {

    private List<StoreGoods> mList;
    private LayoutInflater mInflater;
    private ArrayList<String> mSelectSpcList = new ArrayList<>();

    public StoreSpcAdapter(Context context, List<StoreGoods> data, ArrayList<String> list) {
        mList = data;
        mInflater = LayoutInflater.from(context);
        mSelectSpcList = list;
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
            convertView = mInflater.inflate(R.layout.listitem_storegoodsspc, null);
            viewHolder.txtGoal = (TextView) convertView.findViewById(R.id.txt_storegoods_spc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtGoal.setText(mList.get(position).getAttr_text());
        if (mList.get(position).getAttr_clickable().equals("false")) {
            viewHolder.txtGoal.setTextColor(Color.parseColor("#ff0000"));//设置不可选的颜色表现
        }
        if (mSelectSpcList.contains(mList.get(position).getAttr_id())) {
            viewHolder.txtGoal.setBackgroundColor(Color.parseColor("#ff0000"));//设置已选中的的颜色表现
        }
        return convertView;
    }

    class ViewHolder {
        public TextView txtGoal;
    }
}
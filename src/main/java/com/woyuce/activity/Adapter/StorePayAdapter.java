package com.woyuce.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woyuce.activity.Bean.StoreMenu;
import com.woyuce.activity.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/17.
 */
public class StorePayAdapter extends BaseAdapter {

    private LayoutInflater mInflate;
    private ArrayList<StoreMenu> mList;

    public StorePayAdapter(Context context, ArrayList<StoreMenu> list) {
        mInflate = LayoutInflater.from(context);
        mList = list;
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
            convertView = mInflate.inflate(R.layout.listitem_storepay, null);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txt_listitem_storepay_name);
            viewHolder.mTxtSpec = (TextView) convertView.findViewById(R.id.txt_listitem_storepay_spec);
            viewHolder.mTxtNum = (TextView) convertView.findViewById(R.id.txt_listitem_storepay_num);
            viewHolder.mTxtPrice = (TextView) convertView.findViewById(R.id.txt_listitem_storepay_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(mList.get(position).getName());
//        viewHolder.mTxtSpec.setText(mList.get(position).getName());
        viewHolder.mTxtNum.setText(mList.get(position).getNum());

        Integer num = Integer.parseInt(mList.get(position).getNum());
        Double price = Double.parseDouble(mList.get(position).getPrice());

        viewHolder.mTxtPrice.setText((num * price) + "元");
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtName, mTxtSpec, mTxtNum, mTxtPrice;
    }
}
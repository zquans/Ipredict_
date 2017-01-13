package com.woyuce.activity.Adapter.Store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woyuce.activity.Bean.Store.StoreAddress;
import com.woyuce.activity.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/17.
 */
public class StoreAddressAdapter extends BaseAdapter {


    private LayoutInflater mInflate;
    private ArrayList<StoreAddress> mList;

    public StoreAddressAdapter(Context context, ArrayList<StoreAddress> list) {
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
            convertView = mInflate.inflate(R.layout.listitem_storeaddress, null);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txt_listitem_storeaddress_name);
            viewHolder.mTxtMobile = (TextView) convertView.findViewById(R.id.txt_listitem_storeaddress_mobile);
            viewHolder.mTxtQQ = (TextView) convertView.findViewById(R.id.txt_listitem_storeaddress_qq);
            viewHolder.mTxtEmail = (TextView) convertView.findViewById(R.id.txt_listitem_storeaddress_email);
            viewHolder.mTxtIsDeault = (TextView) convertView.findViewById(R.id.txt_listitem_storeaddress_is_default);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(mList.get(position).getName());
        viewHolder.mTxtMobile.setText(mList.get(position).getMobile());
        viewHolder.mTxtQQ.setText("QQ:" + (mList.get(position).getQ_q()));
        viewHolder.mTxtEmail.setText("邮箱:" + mList.get(position).getEmail());
        if (mList.get(position).getIs_default().equals("true")) {
            viewHolder.mTxtIsDeault.setText("默认");
        } else {
            viewHolder.mTxtIsDeault.setText("");
        }
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtName, mTxtMobile, mTxtQQ, mTxtEmail, mTxtIsDeault;
    }
}
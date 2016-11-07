package com.woyuce.activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woyuce.activity.Bean.StoreGoods;
import com.woyuce.activity.R;

import java.util.List;

public class StoreGoodsCommentAdapter extends BaseAdapter {

    private List<StoreGoods> mList;
    private LayoutInflater mInflater;

    public StoreGoodsCommentAdapter(Context context, List<StoreGoods> data) {
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
            convertView = mInflater.inflate(R.layout.listitem_storegoodscomment, null);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txt_goodscomment_username);
            viewHolder.mTxtComment = (TextView) convertView.findViewById(R.id.txt_goodscomment_comment);
            viewHolder.mTxtTime = (TextView) convertView.findViewById(R.id.txt_goodscomment_time);
            viewHolder.mTxtContent = (TextView) convertView.findViewById(R.id.txt_goodscomment_content);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(mList.get(position).getCreate_by_name());
        viewHolder.mTxtTime.setText(mList.get(position).getCreate_at());
        viewHolder.mTxtComment.setText(mList.get(position).getSatisfaction());
        viewHolder.mTxtContent.setText(mList.get(position).getComment_text());
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtName, mTxtTime, mTxtContent, mTxtComment;
    }
}
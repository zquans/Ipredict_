package com.woyuce.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.woyuce.activity.Bean.StoreOrder;
import com.woyuce.activity.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/28.
 */
public class StoreOrderListAdapter extends BaseAdapter {

    private ArrayList<StoreOrder> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    private OrderGoodsAdapter mAdapter;

    public StoreOrderListAdapter(ArrayList<StoreOrder> list, Context context) {
        this.mList = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.listitem_storeorderlist, null);
            viewHolder.mTxtOrder = (TextView) convertView.findViewById(R.id.txt_listitem_orderlist_num);
            viewHolder.mTxtTime = (TextView) convertView.findViewById(R.id.txt_listitem_orderlist_time);
            viewHolder.mTxtPrice = (TextView) convertView.findViewById(R.id.txt_listitem_orderlist_price);
            viewHolder.mListView = (ListView) convertView.findViewById(R.id.listview_listitem_orderlist);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtOrder.setText(mList.get(position).getOrder_no());
        viewHolder.mTxtTime.setText(mList.get(position).getCreate_at());
        viewHolder.mTxtPrice.setText(mList.get(position).getPrice());

        mAdapter = new OrderGoodsAdapter(mContext, mList.get(position).getUser_order_details());
        viewHolder.mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(viewHolder.mListView);
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtOrder, mTxtTime, mTxtPrice;
        public ListView mListView;
    }

    /**
     * 重新计算ListView高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
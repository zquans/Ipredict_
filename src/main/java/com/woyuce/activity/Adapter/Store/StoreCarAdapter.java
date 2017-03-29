package com.woyuce.activity.Adapter.Store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.woyuce.activity.Model.Store.StoreMenu;
import com.woyuce.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/8.
 */
public class StoreCarAdapter extends BaseAdapter {

    private List<StoreMenu> mList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public StoreCarAdapter(Context context, List<StoreMenu> list) {
        this.mList = list;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    //回调三部曲
    public interface OnMyClickListener {
        void OnMyAddClick(View view, int pos);

        void OnMyMinusClick(View view, int pos);
    }

    private OnMyClickListener mListener;

    public void setOnMyClickListener(OnMyClickListener listener) {
        this.mListener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.listitem_store_cart, null);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txt_listitem_storecar_name);
            viewHolder.mTxtSpecName = (TextView) convertView.findViewById(R.id.txt_listitem_storecar_specname);
            viewHolder.mTxtPrice = (TextView) convertView.findViewById(R.id.txt_listitem_storecar_price);
            viewHolder.mTxtCount = (TextView) convertView.findViewById(R.id.txt_listitem_storecar_count);

            viewHolder.btnAdd = (Button) convertView.findViewById(R.id.btn_listitem_storecar_add);
            viewHolder.btnMinus = (Button) convertView.findViewById(R.id.btn_listitem_storecar_minus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(mList.get(position).getName());
        viewHolder.mTxtSpecName.setText(mList.get(position).getSpecname());
        viewHolder.mTxtPrice.setText(mList.get(position).getPrice());
        viewHolder.mTxtCount.setText(mList.get(position).getNum());

        //TODO 接口的方法
        //mListener.OnMyAddClick();
        //mListener.OnMyMinusClick();
        // 如果设置了回调，则设置点击事件
        if (mListener != null) {
            viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnMyAddClick(viewHolder.btnAdd, position);
                }
            });
            viewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnMyMinusClick(viewHolder.btnMinus, position);
                }
            });
        }
        return convertView;
    }

    public class ViewHolder {
        public TextView mTxtName, mTxtSpecName, mTxtPrice, mTxtCount;
        public Button btnAdd, btnMinus;
    }
}

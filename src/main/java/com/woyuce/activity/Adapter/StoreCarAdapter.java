package com.woyuce.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.woyuce.activity.Bean.StoreMenu;
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

    public interface OnMyClickListener {
        void OnMyAddClick( );

        void OnMyMinusClick( );
    }

    private OnMyClickListener mListener;

    private void setOnMyClickListener(OnMyClickListener listener) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.listitem_storecar, null);
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.txt_listitem_storecar_name);
            viewHolder.mTxtPrice = (TextView) convertView.findViewById(R.id.txt_listitem_storecar_price);

            viewHolder.btnAdd = (Button) convertView.findViewById(R.id.btn_listitem_storecar_add);
            viewHolder.btnMinus = (Button) convertView.findViewById(R.id.btn_listitem_storecar_minus);
            //TODO 给Button绑定监听事件
            viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOnMyClickListener(mListener);
                }
            });
            viewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOnMyClickListener(mListener);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(mList.get(position).getName());
        viewHolder.mTxtPrice.setText(mList.get(position).getPrice());
        return convertView;
    }

    class ViewHolder {
        public TextView mTxtName, mTxtPrice;
        public Button btnAdd, btnMinus;
    }
}

package com.woyuce.activity.Adapter.Writting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.woyuce.activity.Model.Writting.WitCategory;
import com.woyuce.activity.R;

import java.util.List;

public class WittingAdapter extends BaseAdapter {

    private List<WitCategory> mList;
    private LayoutInflater mInflater;

    public WittingAdapter(Context context, List<WitCategory> data) {
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
        ViewHolder viewholder;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gvitem_writting, null);
            viewholder.categoryname = (TextView) convertView.findViewById(R.id.txt_item_witcategoryname);
            viewholder.img = (ImageView) convertView.findViewById(R.id.img_writting);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        switch (position) {
            case 0:
                viewholder.img.setImageResource(R.mipmap.writting_a);
                break;
            case 1:
                viewholder.img.setImageResource(R.mipmap.writting_b);
                break;
            case 2:
                viewholder.img.setImageResource(R.mipmap.writting_c);
                break;
            case 3:
                viewholder.img.setImageResource(R.mipmap.writting_d);
                break;
            case 4:
                viewholder.img.setImageResource(R.mipmap.writting_e);
                break;
            case 5:
                viewholder.img.setImageResource(R.mipmap.writting_f);
                break;
            case 6:
                viewholder.img.setImageResource(R.mipmap.writting_g);
                break;
            case 7:
                viewholder.img.setImageResource(R.mipmap.writting_h);
                break;
            case 8:
                viewholder.img.setImageResource(R.mipmap.writting_i);
                break;
        }
        viewholder.categoryname.setText(mList.get(position).name);
        return convertView;
    }

    class ViewHolder {
        public TextView categoryname;
        public ImageView img;
    }
}
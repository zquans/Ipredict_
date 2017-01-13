package com.woyuce.activity.Adapter.Speaking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.woyuce.activity.Bean.Speaking.SpeakingPart;
import com.woyuce.activity.R;

import java.util.HashMap;
import java.util.List;

public class SpeakingPart2Adapter extends BaseAdapter {

    private List<SpeakingPart> mList;
    private LayoutInflater mInflater;
    private static HashMap<Integer, Boolean> isSelected;

    public SpeakingPart2Adapter(Context context, List<SpeakingPart> data) {
        mList = data;
        mInflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        initData();
    }

    private void initData() {
        for (int i = 0; i < mList.size(); i++) {
            getIsSelected().put(i, false);
        }
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_speakingpart, null);
            viewHolder.txtsubname = (TextView) convertView.findViewById(R.id.text_item_part);
            viewHolder.ckBox = (CheckBox) convertView.findViewById(R.id.ckbox_item_part);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtsubname.setText(mList.get(position).subname);
        viewHolder.ckBox.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public class ViewHolder {
        public TextView txtsubname;
        public CheckBox ckBox;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        SpeakingPart2Adapter.isSelected = isSelected;
    }
}
package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.woyuce.activity.R;

public class Fragment_Store_Two extends Fragment {

    private LinearLayout mLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_two, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_fragment_goods_two);
        for (int i = 0; i < 3; i++) {
            ImageView mImg = new ImageView(getActivity());
            mImg.setBackgroundResource(R.mipmap.background_music);
            mLinearLayout.addView(mImg);
        }
    }
}
package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.R;

import java.util.ArrayList;

/**
 * 两部分，一：将传递到的数据直接放上视图，二：请求规格获取数据
 */
public class Fragment_StoreGoods_One extends Fragment {

    private ViewFlipper mFlipper;

    private TextView mTxtGoodsTitle, mTxtGoodsPrice, mTxtTotalSale, mTxtGoodComment, mTxtShowOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_one, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mFlipper = (ViewFlipper) view.findViewById(R.id.viewflip_activity_storegoods);

        mTxtGoodsTitle = (TextView) view.findViewById(R.id.txt_activity_storegoods_goodstitle);
        mTxtGoodsPrice = (TextView) view.findViewById(R.id.txt_activity_storegoods_goodsprice);
        mTxtTotalSale = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_salenum);
        mTxtGoodComment = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_good);
        mTxtShowOrder = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_showorder);

        mGridGoal = (GridView) view.findViewById(R.id.gridview_fragment_store_goal);
        mGridArea = (GridView) view.findViewById(R.id.gridview_fragment_store_area);
        mGridTicket = (GridView) view.findViewById(R.id.gridview_fragment_store_ticket);

        //做第一部分，设置View上的数据
        setView();
        //做第二部分，请求规格参数
        requestGoodsSpe();
    }

    /**
     * 第一部分:将上一级获取到的数据设置在View上
     */
    private void setView() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        //将获取到的数据设置到View上
        ArrayList<String> mList = getArguments().getStringArrayList("mList");
        mTxtGoodsTitle.setText(getArguments().getString("goods_title"));
        mTxtGoodsPrice.setText(getArguments().getString("sales_price"));
        mTxtTotalSale.setText("销量" + getArguments().getString("total_sales_volume"));
        mTxtGoodComment.setText("好评" + getArguments().getString("total_good_volume"));
        mTxtShowOrder.setText("晒单" + getArguments().getString("total_show_order_volume"));

        //查看是否有轮播图
        if (mList.size() == 0) {
            mFlipper.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < mList.size(); i++) {
                ImageView img = new ImageView(getActivity());
                ImageLoader.getInstance().displayImage(mList.get(i), img, options);
                mFlipper.addView(img);
            }
            mFlipper.setInAnimation(getActivity(), R.anim.left_in);
            mFlipper.setOutAnimation(getActivity(), R.anim.left_out);
            mFlipper.startFlipping();
        }
    }


    private GridView mGridGoal, mGridArea, mGridTicket;
    private ArrayList<String> mListGoal = new ArrayList<>();
    private ArrayList<String> mListArea = new ArrayList<>();
    private ArrayList<String> mListTickt = new ArrayList<>();
    private ArrayAdapter mGoalAdapter, mAreaAdapter, mTicketAdapter;

    /**
     * 第二部分:请求规格参数
     */
    private void requestGoodsSpe() {
        for (int i = 0; i < 4; i++) {
            mListGoal.add(String.valueOf(i));
            mListArea.add(String.valueOf(i));
            mListTickt.add(String.valueOf(i));
        }

        mGoalAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mListGoal);
        mAreaAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mListArea);
        mTicketAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mListTickt);
        mGridGoal.setAdapter(mGoalAdapter);
        mGridArea.setAdapter(mAreaAdapter);
        mGridTicket.setAdapter(mTicketAdapter);
    }
}
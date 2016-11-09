package com.woyuce.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.woyuce.activity.Adapter.StoreCarAdapter;
import com.woyuce.activity.Bean.StoreMenu;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/7.
 */
//TODO 此Activity主要作一些移除/修改ListView中Item的工作
public class StoreCarActivity extends BaseActivity implements StoreCarAdapter.OnMyClickListener {

    private TextView mTxtTotalNum, mTxtTotalPrice, mTxtFinalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storecar);

        initView();
    }

    //TODO 先做出列表，Item中的加减事件再另做，(参数数组)String to Array,
    //TODO 思考需不需要用数据库来做

    private ListView mListView;
    private ArrayList<StoreMenu> mList = new ArrayList<>();
    private StoreCarAdapter mAdapter;

    private void initView() {
        //保存订单商品信息,可以考虑封装
        PreferenceUtil.save(this, getIntent().getStringExtra("name"),
                getIntent().getStringExtra("id") + ","
                        + getIntent().getStringExtra("name") + ","
                        + getIntent().getDoubleExtra("num", -1) + ","
                        + getIntent().getStringExtra("price") + ","
                        + getIntent().getStringExtra("goodsid"));

        LogUtil.i("pre_util = " + PreferenceUtil.getSharePre(this).getString(getIntent().getStringExtra("name"), null));

        mListView = (ListView) findViewById(R.id.listview_activity_store_car);
        mTxtTotalNum = (TextView) findViewById(R.id.txt_storecar_total_num);
        mTxtTotalPrice = (TextView) findViewById(R.id.txt_storecar_total_price);
        mTxtFinalPrice = (TextView) findViewById(R.id.txt_storecar_final_price);

        //TODO 这个商品详情数组要怎么做
        StoreMenu storemenu;
        for (int i = 0; i < 5; i++) {
            storemenu = new StoreMenu();
            storemenu.setGoodsid(getIntent().getStringExtra("id"));
            storemenu.setName(getIntent().getStringExtra("name"));
            storemenu.setNum(getIntent().getDoubleExtra("num", -1) + "");
            storemenu.setPrice(getIntent().getStringExtra("price"));
            storemenu.setGoodsid(getIntent().getStringExtra("goodsid"));
            mList.add(storemenu);
        }

        mAdapter = new StoreCarAdapter(this, mList);
        mAdapter.setOnMyClickListener(this);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);

        //商品列表 总值计算
        mTxtTotalNum.setText(mList.size() + "件");
        mTxtTotalPrice.setText(Double.parseDouble(mList.get(0).getPrice()) * mList.size() + "元");
        mTxtFinalPrice.setText(Double.parseDouble(mList.get(0).getPrice()) * mList.size() + "元");
    }

    public void toPay(View view) {
        startActivity(new Intent(this, StorePayActivity.class));
    }

    /**
     * 帮助类:动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //TODO 从这里开始做,加减的按钮操作后，中间的TextView如何联动？

    //回调的方法，两个Button的处理
    @Override
    public void OnMyAddClick(View view, final int pos) {
        ToastUtil.showMessage(StoreCarActivity.this, "pos add = " + pos);

        TextView txtCount = (TextView) getViewByPosition(pos, mListView);

//        TextView txtCount = (TextView) mListView.getChildAt(pos).findViewById(R.id.txt_listitem_storecar_count);
        txtCount.setText("2");

//        StoreCarAdapter.ViewHolder holder = (StoreCarAdapter.ViewHolder) view.getTag();
//        int i = Integer.parseInt(holder.mTxtCount.getText().toString());
//        LogUtil.i("i = " + i + "mListView.getChildAt(pos) = " + mListView.getChildAt(pos));
//        holder.mTxtCount.setText(i + 1);


//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                StoreCarAdapter.ViewHolder holder = (StoreCarAdapter.ViewHolder) view.getTag();
//                int i = Integer.parseInt(holder.mTxtCount.getText().toString());
//                LogUtil.i("i = " + i + "mListView.getChildAt(pos) = " + mListView.getChildAt(pos));
//                holder.mTxtCount.setText(i + 1);
//            }
//        });
    }

    @Override
    public void OnMyMinusClick(View view, int pos) {
        ToastUtil.showMessage(StoreCarActivity.this, "pos minus = " + pos);
    }

    /**
     * 获取listview中Item中的View
     *
     * @param pos
     * @param listView
     * @return
     */
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}
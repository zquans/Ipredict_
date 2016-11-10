package com.woyuce.activity.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
public class StoreCarActivity extends BaseActivity implements StoreCarAdapter.OnMyClickListener {

    private TextView mTxtTotalNum, mTxtTotalPrice, mTxtFinalPrice;

    private ListView mListView;
    private ArrayList<StoreMenu> mList = new ArrayList<>();
    private StoreCarAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storecar);

        initView();
    }

    /**
     * 初始化商品信息数据
     */
    private void initData() {
        SQLiteDatabase mDatabase;
        if (!PreferenceUtil.getSharePre(this).getString("storetb_is_exist", "").equals("yes")) {
            return;
        }
        mDatabase = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
        //数据库查询
        Cursor mCursor = mDatabase.query("storetb", null, "_id>?", new String[]{"0"}, null, null, "_id desc");
        if (mCursor != null) {
            StoreMenu storemenu;
            while (mCursor.moveToNext()) {
                storemenu = new StoreMenu();
                storemenu.setId(mCursor.getString(mCursor.getColumnIndex("id")));
                storemenu.setGoodsid(mCursor.getString(mCursor.getColumnIndex("goodsid")));
                storemenu.setName(mCursor.getString(mCursor.getColumnIndex("name")));
                storemenu.setNum(mCursor.getString(mCursor.getColumnIndex("num")));
                storemenu.setPrice(mCursor.getString(mCursor.getColumnIndex("price")));
                mList.add(storemenu);
            }
            mCursor.close();
        }
        mDatabase.close();
        LogUtil.i("mList = " + mList.toString());
    }

    private void initView() {
        //获取数据
        initData();
        //获取视图
        mListView = (ListView) findViewById(R.id.listview_activity_store_car);
        mTxtTotalNum = (TextView) findViewById(R.id.txt_storecar_total_num);
        mTxtTotalPrice = (TextView) findViewById(R.id.txt_storecar_total_price);
        mTxtFinalPrice = (TextView) findViewById(R.id.txt_storecar_final_price);

        mAdapter = new StoreCarAdapter(this, mList);
        mAdapter.setOnMyClickListener(this);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);

        //商品列表 总值计算
        mTxtTotalNum.setText(mList.size() + "件");

        //计算总价
        Double total = 0.00;
        for (int i = 0; i < mList.size(); i++) {
            Double mtotal = (Double.parseDouble(mList.get(i).getNum())) * (Double.parseDouble(mList.get(i).getPrice()));
            total = total + mtotal;
        }
        mTxtTotalPrice.setText(total + "元");
        mTxtFinalPrice.setText(total + "元");
    }

    public void toPay(View view) {
        startActivity(new Intent(this, StorePayActivity.class));
    }

    //回调的方法，两个Button的处理
    @Override
    public void OnMyAddClick(View view, final int pos) {
        ToastUtil.showMessage(StoreCarActivity.this, "pos add = " + pos);

        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        int local_count = Integer.parseInt(txtCount.getText().toString());
        local_count = local_count + 1;
        txtCount.setText(local_count + "");
    }

    @Override
    public void OnMyMinusClick(View view, int pos) {
        ToastUtil.showMessage(StoreCarActivity.this, "pos minus = " + pos);
        //减少商品的时候需要考虑商品减少到0的情况
        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        int local_count = Integer.parseInt(txtCount.getText().toString());
        local_count = local_count - 1;
        if (local_count == 0) {
            mList.remove(pos);
            mAdapter.notifyDataSetChanged();
            return;
        }
        txtCount.setText(local_count + "");
    }

    /**
     * 获取listview中Item中的View(LinearLayout)下的TextView
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
            return listView.getChildAt(childIndex).findViewById(R.id.txt_listitem_storecar_count);
        }
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
}
package com.woyuce.activity.UI.Activity.Store;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.Store.StoreCarAdapter;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Bean.Store.StoreMenu;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.DbUtil;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.MathUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/10/3
 */

public class StoreCarActivity extends BaseActivity implements StoreCarAdapter.OnMyClickListener {

    private TextView mTxtTotalNum, mTxtTotalPrice, mTxtFinalPrice;

    private ListView mListView;
    private ArrayList<StoreMenu> mList = new ArrayList<>();
    private StoreCarAdapter mAdapter;
    private Integer total_count = 0;
    private Double total_price = 0.00;

    private String URL = "http://api.iyuce.com/v1/store/getgoodskusinfo?ids=";

    public void back(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storecar);

        initView();
    }

    private void initView() {
        //初始化数据库,获取数据
        initData();
        //网络请求，相同的商品，获取最新价格,名称等
        requestRecentInfo();
        //计算总价
        initTotalPrice();

        mListView = (ListView) findViewById(R.id.listview_activity_store_car);
        mTxtTotalNum = (TextView) findViewById(R.id.txt_storecar_total_num);
        mTxtTotalPrice = (TextView) findViewById(R.id.txt_storecar_total_price);
        mTxtFinalPrice = (TextView) findViewById(R.id.txt_storecar_final_price);

        mAdapter = new StoreCarAdapter(this, mList);
        mAdapter.setOnMyClickListener(this);
        mListView.setAdapter(mAdapter);

        //商品列表 总值计算
        mTxtTotalNum.setText(total_count + "件");
        mTxtTotalPrice.setText(total_price + "元");
        mTxtFinalPrice.setText(total_price + "元");
    }

    /**
     * 初始化商品信息数据
     */
    private void initData() {
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, Constants.DATABASE_IYUCE).getWritableDatabase();
        String isNone = DbUtil.queryToString(mDatabase, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_CART);
        if (isNone.equals(Constants.NONE)) {
            mDatabase.close();
            ToastUtil.showMessage(this, "购物车空空如也");
            return;
        }
        //查出所有属性
        Cursor mCursor = mDatabase.query(Constants.TABLE_CART, null, null, null, null, null, null);
        if (mCursor != null) {
            StoreMenu storemenu;
            while (mCursor.moveToNext()) {
                storemenu = new StoreMenu();
                //TODO 优化 目前如果商品数量是0，则不加载进数组
                if (Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(Constants.COLUMN_COUNT))) <= 0) {
                    continue;
                }
                storemenu.setGoodsskuid(mCursor.getString(mCursor.getColumnIndex(Constants.COLUMN_GOODS_SPEC_ID)));
                storemenu.setId(mCursor.getString(mCursor.getColumnIndex(Constants.COLUMN_GOODS_ID)));
                storemenu.setName(mCursor.getString(mCursor.getColumnIndex(Constants.COLUMN_NAME)));
                storemenu.setSpecname(mCursor.getString(mCursor.getColumnIndex(Constants.COLUMN_SPEC_NAME)));
                storemenu.setPrice(mCursor.getString(mCursor.getColumnIndex(Constants.COLUMN_PRICE)));
                storemenu.setNum(mCursor.getString(mCursor.getColumnIndex(Constants.COLUMN_COUNT)));
                mList.add(storemenu);
            }
            mCursor.close();
        }
        mDatabase.close();
    }

    private void requestRecentInfo() {
        for (int i = 0; i < mList.size(); i++) {
            URL = URL + mList.get(i).getGoodsskuid() + ",";
        }
        progressdialogshow(this);
        StringRequest requestRecentGoods = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj;
                    JSONArray arr;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        arr = new JSONArray(obj.getString("data"));
                        LogUtil.i("arr = " + arr);
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            for (int j = 0; j < mList.size(); j++) {
                                if (obj.getString("Id").equals(mList.get(j).getGoodsskuid())) {
                                    mList.get(j).setPrice(obj.getString("SalesPrice"));
                                    break;
                                }
                            }
                        }
                    } else {
                        ToastUtil.showMessage(StoreCarActivity.this, "获取最新商品信息失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressdialogcancel();
            }
        }, null);
        requestRecentGoods.setTag("requestRecentGoods");
        AppContext.getHttpQueue().add(requestRecentGoods);
    }

    /**
     * 初始化总价
     */
    private void initTotalPrice() {
        for (int i = 0; i < mList.size(); i++) {
            Double mtotal_price = MathUtil.mul(
                    Double.parseDouble(mList.get(i).getNum()), Double.parseDouble(mList.get(i).getPrice()));
            total_price = MathUtil.add(total_price, mtotal_price);
            int mtota_count = Integer.parseInt(mList.get(i).getNum());
            total_count = total_count + mtota_count;
        }
    }

    @Override
    public void OnMyAddClick(View view, final int pos) {
        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        //增加数据库中的数据
        int count = Integer.parseInt(txtCount.getText().toString());
        count = count + 1;
        String where_goodsskuid = mList.get(pos).getGoodsskuid();
        changeData(count, where_goodsskuid);
        txtCount.setText(count + "");
        countPrice("add", mList.get(pos).getPrice());
    }

    @Override
    public void OnMyMinusClick(View view, int pos) {
        //减少商品的时候需要考虑商品减少到0的情况
        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        String where_goodsskuid = mList.get(pos).getGoodsskuid();
        int count = Integer.parseInt(txtCount.getText().toString());
        count = count - 1;
        if (count <= 0) {
            count = 0;
        }
        changeData(count, where_goodsskuid);
        txtCount.setText(count + "");
        countPrice("minus", mList.get(pos).getPrice());
    }

    /**
     * 数据库修改商品数量
     */
    private void changeData(int count, String goodsskuid) {
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, Constants.DATABASE_IYUCE).getWritableDatabase();
        String sql_update = "UPDATE " + Constants.TABLE_CART + " SET " + Constants.COLUMN_COUNT + " = " + count
                + " WHERE " + Constants.COLUMN_GOODS_SPEC_ID + " = " + goodsskuid;
        mDatabase.execSQL(sql_update);
        int change_after = DbUtil.queryToInt(mDatabase, Constants.TABLE_CART, Constants.COLUMN_COUNT, Constants.COLUMN_GOODS_SPEC_ID + "=? ", goodsskuid);
        LogUtil.i("change_after = " + change_after);
        mDatabase.close();
    }

    /**
     * Button增减时，价格和数量随之变化
     */
    private void countPrice(String add_or_minus, String price) {
        if (add_or_minus.equals("add")) {
            total_count = total_count + 1;
            total_price = MathUtil.add(total_price, Double.parseDouble(price));
        } else if (add_or_minus.equals("minus")) {
            total_count = total_count - 1;
            total_price = MathUtil.sub(total_price, Double.parseDouble(price));
        }
        mTxtTotalNum.setText(total_count + "件");
        mTxtTotalPrice.setText(total_price + "元");
        mTxtFinalPrice.setText(total_price + "元");
    }

    /**
     * 获取listview中Item中的View(LinearLayout)下的TextView
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
     * Button事件，去结账付款
     */
    //TODO 改为传递一个对象数组
    public void toPay(View view) {
        Intent intent = new Intent(this, StorePayActivity.class);
        ArrayList<String> mGoodsSkuIdList = new ArrayList<>();
        ArrayList<String> mSpecNameList = new ArrayList<>();
        ArrayList<String> mNameList = new ArrayList<>();
        ArrayList<String> mPriceList = new ArrayList<>();
        ArrayList<String> mNumList = new ArrayList<>();
        intent.putStringArrayListExtra("mGoodsSkuIdList", mGoodsSkuIdList);
        intent.putStringArrayListExtra("mNameList", mNameList);
        intent.putStringArrayListExtra("mSpecNameList", mSpecNameList);
        intent.putStringArrayListExtra("mPriceList", mPriceList);
        intent.putStringArrayListExtra("mNumList", mNumList);
        intent.putExtra("total_price", total_price);
        intent.putExtra("total_count", total_count);
        startActivity(intent);
    }
}
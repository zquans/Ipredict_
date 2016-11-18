package com.woyuce.activity.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.StoreCarAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreMenu;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StoreCarActivity extends BaseActivity implements StoreCarAdapter.OnMyClickListener {

    private TextView mTxtTotalNum, mTxtTotalPrice, mTxtFinalPrice;

    private ListView mListView;
    private ArrayList<StoreMenu> mList = new ArrayList<>();
    private ArrayList<StoreMenu> mFinalList = new ArrayList<>();
    private StoreCarAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storecar);

        initView();
    }

    private void initView() {
        //获取数据
        initData();
        //网络请求，相同的商品，获取最新价格,名称等
        requestRecentInfo();

        //计算总价
        for (int i = 0; i < mFinalList.size(); i++) {
            Double mtotal_price = (Double.parseDouble(mFinalList.get(i).getNum())) * (Double.parseDouble(mFinalList.get(i).getPrice()));
            total_price = total_price + mtotal_price;
            int mtota_count = Integer.parseInt(mFinalList.get(i).getNum());
            total_count = total_count + mtota_count;
        }

        //获取视图
        mListView = (ListView) findViewById(R.id.listview_activity_store_car);
        mTxtTotalNum = (TextView) findViewById(R.id.txt_storecar_total_num);
        mTxtTotalPrice = (TextView) findViewById(R.id.txt_storecar_total_price);
        mTxtFinalPrice = (TextView) findViewById(R.id.txt_storecar_final_price);

        mAdapter = new StoreCarAdapter(this, mFinalList);
        mAdapter.setOnMyClickListener(this);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);//动态计算ListView宽高

        //商品列表 总值计算
        mTxtTotalNum.setText(total_count + "件");
        mTxtTotalPrice.setText(total_price + "元");
        mTxtFinalPrice.setText(total_price + "元");
    }

    /**
     * 获取最新的商品信息
     * (主要是价格),其实名称之类也应该相应修改
     */
    private String URL = "http://api.iyuce.com/v1/store/getgoodskusinfo?ids=";

    private void requestRecentInfo() {
        for (int i = 0; i < mFinalList.size(); i++) {
            URL = URL + mFinalList.get(i).getGoodsskuid() + ",";
        }
        LogUtil.i("URL = " + URL);
        StringRequest requestRecentGoods = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj;
                    JSONArray arr;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        arr = new JSONArray(obj.getString("data"));
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            for (int j = 0; j < mFinalList.size(); j++) {
                                if (obj.getString("Id").equals(mFinalList.get(j).getGoodsskuid())) {
                                    mFinalList.get(j).setPrice(obj.getString("SalesPrice"));
                                    break;
                                }
                            }
                        }
                    } else {
                        LogUtil.i("oode != 0 " + obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        requestRecentGoods.setTag("requestRecentGoods");
        AppContext.getHttpQueue().add(requestRecentGoods);
    }

    /**
     * 删除数据库中某商品的某一项
     */
    private SQLiteDatabase mDatabase;

    private void deleteData(String arg, String _id_or_goodsskuid) {
        mDatabase = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
        String local_delete = null;
        if (_id_or_goodsskuid.equals("goodsskuid")) {
            local_delete = "DELETE FROM storetb WHERE goodsskuid = '" + arg + "'";
        } else if (_id_or_goodsskuid.equals("_id")) {
            local_delete = "DELETE FROM storetb WHERE _id = '" + arg + "'";
        }
        if (!TextUtils.isEmpty(local_delete)) {
            mDatabase.execSQL(local_delete);
        }
        mDatabase.close();
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
                storemenu.set_id(mCursor.getString(mCursor.getColumnIndex("_id")));
                storemenu.setId(mCursor.getString(mCursor.getColumnIndex("id")));
                storemenu.setGoodsskuid(mCursor.getString(mCursor.getColumnIndex("goodsskuid")));
                storemenu.setName(mCursor.getString(mCursor.getColumnIndex("name")));
                storemenu.setNum(mCursor.getString(mCursor.getColumnIndex("num")));
                storemenu.setPrice(mCursor.getString(mCursor.getColumnIndex("price")));
                mList.add(storemenu);
            }
            mCursor.close();
        }
        mDatabase.close();

        //统计数据,过滤重复项
        doFilter();
    }

    // 回调的方法，两个Button的处理
    @Override
    public void OnMyAddClick(View view, final int pos) {
        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        int local_count = Integer.parseInt(txtCount.getText().toString());
        local_count = local_count + 1;
        //计算商品总价和总数
        countPrice("add", mFinalList.get(pos).getPrice());
        txtCount.setText(local_count + "");
    }

    @Override
    public void OnMyMinusClick(View view, int pos) {
        //减少商品的时候需要考虑商品减少到0的情况
        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        int local_count = Integer.parseInt(txtCount.getText().toString());
        local_count = local_count - 1;
        //循环后删除随意一项mList中的商品
        String local_del_goodsskuid = mFinalList.get(pos).getGoodsskuid();
        for (int i = 0; i < mList.size(); i++) {
            LogUtil.i("i = " + i);
            if (mList.get(i).getGoodsskuid().equals(local_del_goodsskuid)) {
                //删除对应主键ID的那条数据
                deleteData(mList.get(i).get_id(), "_id");
                mList.remove(i);
                LogUtil.i("mList.get(i).get_id() = " + mList.get(i).get_id());
                break;
            }
        }
        //计算商品总价和总数
        countPrice("minus", mFinalList.get(pos).getPrice());
        if (local_count == 0) {
            //清除数据库该商品(可以留给删除用)
            deleteData(mFinalList.get(pos).getGoodsskuid(), "goodsskuid");
            //同时移除视图
            mFinalList.remove(pos);
            mAdapter.notifyDataSetChanged();
            return;
        }
        txtCount.setText(local_count + "");
    }

    /**
     * Button增减时，价格和数量随之变化
     *
     * @return
     */
    private void countPrice(String add_or_minus, String price) {
        if (add_or_minus.equals("add")) {
            total_count = total_count + 1;
            total_price = total_price + Double.parseDouble(price);
        } else if (add_or_minus.equals("minus")) {
            total_count = total_count - 1;
            total_price = total_price - Double.parseDouble(price);
        }
        mTxtTotalNum.setText(total_count + "件");
        mTxtTotalPrice.setText(total_price + "元");
        mTxtFinalPrice.setText(total_price + "元");
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

    /**
     * 统计商品数据,过滤后总计,重复的商品叠加View
     */
    private void doFilter() {
        //第一步，先找出重复的商品,做成唯一数组
        Set<String> mSkuIdSet = new HashSet<>();
        for (int i = 0; i < mList.size(); i++) {
            mSkuIdSet.add(mList.get(i).getGoodsskuid());
        }
        LogUtil.e("mSkuIdSet =" + mSkuIdSet);
        //转换Set为ArrayList
        ArrayList mSkuIdList = new ArrayList();
        Iterator it = mSkuIdSet.iterator();
        while (it.hasNext()) {
            mSkuIdList.add(it.next());
        }

        //第二步，循环商品唯一的数组,去匹配商品的其余属性，并给数量Num赋值
        StoreMenu menu;
        for (int i = 0; i < mSkuIdList.size(); i++) {
            int icount = 0;
            for (int j = 0; j < mList.size(); j++) {
                if (mList.get(j).getGoodsskuid().equals(mSkuIdList.get(i))) {
                    icount = icount + 1;
                }
            }
            //去找出匹配的那个商品ID
            menu = new StoreMenu();
            for (int k = 0; k < mList.size(); k++) {
                if (mList.get(k).getGoodsskuid().equals(mSkuIdList.get(i))) {
                    menu = mList.get(k);
                    break;
                }
            }
            //给数量Num赋值
            menu.setNum(icount + "");
            mFinalList.add(menu);
        }
    }

    /**
     * Button事件，去结账付款
     */
    private Integer total_count = 0;
    private Double total_price = 0.00;

    public void toPay(View view) {
        Intent intent = new Intent(this, StorePayActivity.class);
        ArrayList<String> mGoodsSkuIdList = new ArrayList<>();
        ArrayList<String> mNameList = new ArrayList<>();
        ArrayList<String> mPriceList = new ArrayList<>();
        ArrayList<String> mNumList = new ArrayList<>();
        for (int i = 0; i < mFinalList.size(); i++) {
            mGoodsSkuIdList.add(mFinalList.get(i).getGoodsskuid());
            mNameList.add(mFinalList.get(i).getName());
            mPriceList.add(mFinalList.get(i).getPrice());
            mNumList.add(mFinalList.get(i).getNum());
        }
        intent.putStringArrayListExtra("mGoodsSkuIdList", mGoodsSkuIdList);
        intent.putStringArrayListExtra("mNameList", mNameList);
        intent.putStringArrayListExtra("mPriceList", mPriceList);
        intent.putStringArrayListExtra("mNumList", mNumList);
        intent.putExtra("total_price", total_price);
        intent.putExtra("total_count", total_count);
        startActivity(intent);
    }
}
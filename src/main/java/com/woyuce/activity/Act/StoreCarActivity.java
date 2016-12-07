package com.woyuce.activity.Act;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Adapter.StoreCarAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.StoreMenu;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
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
        //获取数据
        initData();
        //网络请求，相同的商品，获取最新价格,名称等
        requestRecentInfo();

        //计算总价
        for (int i = 0; i < mFinalList.size(); i++) {
            Double mtotal_price = ArithUtil
                    .mul(Double.parseDouble(mFinalList.get(i).getNum()), Double.parseDouble(mFinalList.get(i).getPrice()));
//            total_price = total_price + mtotal_price;
            total_price = ArithUtil.add(total_price, mtotal_price);
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
        progressdialogshow(this);
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
                        LogUtil.i("arr = " + arr);
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            for (int j = 0; j < mFinalList.size(); j++) {
                                if (obj.getString("Id").equals(mFinalList.get(j).getGoodsskuid())) {
                                    mFinalList.get(j).setPrice(obj.getString("SalesPrice"));
//                                    LogUtil.i("obj.getString(\"Id\") = " + obj.getString("Id") +
//                                            "-----obj.getString(\"SalesPrice\") = " + obj.getString("SalesPrice")
//                                    );
                                    break;
                                }
                            }
                        }
                    } else {
                        ToastUtil.showMessage(StoreCarActivity.this, "获取最新商品信息失败");
                        LogUtil.i("oode != 0 " + obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressdialogcancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("volleyError = " + volleyError.getMessage());
                progressdialogcancel();
            }
        });
        requestRecentGoods.setTag("requestRecentGoods");
        AppContext.getHttpQueue().add(requestRecentGoods);
    }

    //数据库
    private SQLiteDatabase mDatabase;

    /**
     * 插入新商品数据
     */
    private void insertData(String id, String goodsid, String goodsskuid, String name, String specname, String num, String price) {
        mDatabase = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
        ContentValues mValues = new ContentValues();
        mValues.put("id", id);
        mValues.put("goodsskuid", goodsskuid);
        mValues.put("name", name);
        mValues.put("specname", specname);
        mValues.put("num", num);
        mValues.put("price", price);
        mDatabase.insert("storetb", null, mValues);
        mValues.clear();
        mDatabase.close();
    }

    /**
     * 删除数据库中某商品的某一项
     */
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
                storemenu.setSpecname(mCursor.getString(mCursor.getColumnIndex("specname")));
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
        progressdialogshow(this);
        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        int local_count = Integer.parseInt(txtCount.getText().toString());
        local_count = local_count + 1;
        //循环后增加一项mList中的商品
        String local_insert_goodsskuid = mFinalList.get(pos).getGoodsskuid();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getGoodsskuid().equals(local_insert_goodsskuid)) {
                //增加对应主键ID的那条数据
                insertData(mList.get(i).getId(), mList.get(i).getId(), mList.get(i).getGoodsskuid(),
                        mList.get(i).getName(), mList.get(i).getSpecname(), "1", mList.get(i).getPrice());
                break;
            }
        }
        //计算商品总价和总数
        countPrice("add", mFinalList.get(pos).getPrice());
        txtCount.setText(local_count + "");
        progressdialogcancel();
    }

    @Override
    public void OnMyMinusClick(View view, int pos) {
        progressdialogshow(this);
        //减少商品的时候需要考虑商品减少到0的情况
        TextView txtCount = (TextView) getViewByPosition(pos, mListView);
        int local_count = Integer.parseInt(txtCount.getText().toString());
        local_count = local_count - 1;
        //循环后删除随意一项mList中的商品
        String local_del_goodsskuid = mFinalList.get(pos).getGoodsskuid();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getGoodsskuid().equals(local_del_goodsskuid)) {
                //删除对应主键ID的那条数据
                deleteData(mList.get(i).get_id(), "_id");
                mList.remove(i);
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
            progressdialogcancel();
            return;
        }
        txtCount.setText(local_count + "");
        progressdialogcancel();
    }

    /**
     * Button增减时，价格和数量随之变化
     *
     * @return
     */

    //TODO 这里会发生double计算错误，导致崩溃
    private void countPrice(String add_or_minus, String price) {
        if (add_or_minus.equals("add")) {
            total_count = total_count + 1;
//            total_price = total_price + Double.parseDouble(price);
            total_price = ArithUtil.add(total_price, Double.parseDouble(price));
        } else if (add_or_minus.equals("minus")) {
            total_count = total_count - 1;
//            total_price = total_price - Double.parseDouble(price);
            total_price = ArithUtil.sub(total_price, Double.parseDouble(price));
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
     * 统计商品数据,过滤后总计,重复的商品叠加View
     */
    private void doFilter() {
        //第一步，先找出重复的商品,做成唯一数组
        Set<String> mSkuIdSet = new HashSet<>();
        for (int i = 0; i < mList.size(); i++) {
            mSkuIdSet.add(mList.get(i).getGoodsskuid());
        }
//        LogUtil.e("mSkuIdSet =" + mSkuIdSet);
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
        ArrayList<String> mSpecNameList = new ArrayList<>();
        ArrayList<String> mNameList = new ArrayList<>();
        ArrayList<String> mPriceList = new ArrayList<>();
        ArrayList<String> mNumList = new ArrayList<>();
        for (int i = 0; i < mFinalList.size(); i++) {
            mGoodsSkuIdList.add(mFinalList.get(i).getGoodsskuid());
            mNameList.add(mFinalList.get(i).getName());
            mSpecNameList.add(mFinalList.get(i).getSpecname());
            mPriceList.add(mFinalList.get(i).getPrice());
            mNumList.add(mFinalList.get(i).getNum());
        }
        intent.putStringArrayListExtra("mGoodsSkuIdList", mGoodsSkuIdList);
        intent.putStringArrayListExtra("mNameList", mNameList);
        intent.putStringArrayListExtra("mSpecNameList", mSpecNameList);
        intent.putStringArrayListExtra("mPriceList", mPriceList);
        intent.putStringArrayListExtra("mNumList", mNumList);
        intent.putExtra("total_price", total_price);
        intent.putExtra("total_count", total_count);
        startActivity(intent);
    }

    /**
     * 这个类应该放在外面
     */
    static class ArithUtil {
        private static final int DEF_DIV_SCALE = 10;

        private ArithUtil() {
        }

        public static double add(double d1, double d2) {
            BigDecimal b1 = new BigDecimal(Double.toString(d1));
            BigDecimal b2 = new BigDecimal(Double.toString(d2));
            return b1.add(b2).doubleValue();

        }

        public static double sub(double d1, double d2) {
            BigDecimal b1 = new BigDecimal(Double.toString(d1));
            BigDecimal b2 = new BigDecimal(Double.toString(d2));
            return b1.subtract(b2).doubleValue();

        }

        public static double mul(double d1, double d2) {
            BigDecimal b1 = new BigDecimal(Double.toString(d1));
            BigDecimal b2 = new BigDecimal(Double.toString(d2));
            return b1.multiply(b2).doubleValue();

        }

        public static double div(double d1, double d2) {

            return div(d1, d2, DEF_DIV_SCALE);

        }

        public static double div(double d1, double d2, int scale) {
            if (scale < 0) {
                throw new IllegalArgumentException("The scale must be a positive integer or zero");
            }
            BigDecimal b1 = new BigDecimal(Double.toString(d1));
            BigDecimal b2 = new BigDecimal(Double.toString(d2));
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

        }
    }
}
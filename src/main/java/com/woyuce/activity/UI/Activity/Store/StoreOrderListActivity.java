package com.woyuce.activity.UI.Activity.Store;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Adapter.Store.StoreOrderListAdapter;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.Bean.Store.StoreGoods;
import com.woyuce.activity.Bean.Store.StoreOrder;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.RecyclerItemClickListener;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/25.
 */
public class StoreOrderListActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private XRecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<StoreOrder> mList = new ArrayList<>();

    private String local_user_id;
    private String URL = "http://api.iyuce.com/v1/store/orderlist?pageSize=10&userid=";
    private String URL_Del = "http://api.iyuce.com/v1/store/orderdelete?userid=";

    private static final int GET_DATA_OK = 0;      //获取数据
    private static final int LOAD_MORE_DATA_OK = 1;   //加载更多数据
    private int local_page_number = 1;

    private boolean isRefresh = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_DATA_OK) {
                mAdapter.notifyDataSetChanged();
                mRecyclerView.refreshComplete();
            }
            if (msg.what == LOAD_MORE_DATA_OK) {
                mAdapter.notifyDataSetChanged();
                mRecyclerView.loadMoreComplete();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("StoreOrderList");
        local_page_number = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_orderlist);

        initView();
    }

    private void initView() {
        local_user_id = PreferenceUtil.getSharePre(this).getString("userId", "");
        mRecyclerView = (XRecyclerView) findViewById(R.id.recycler_activity_store_orderlist);
        mAdapter = new StoreOrderListAdapter(StoreOrderListActivity.this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(StoreOrderListActivity.this));
        mRecyclerView.setAdapter(mAdapter);
        doRecyclerItemClick();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallGridBeat);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLoadingListener(this);
        requestData(GET_DATA_OK, URL + local_user_id);
    }

    public void back(View view) {
        finish();
    }

    /**
     * 请求数据列表
     */
    private void requestData(final int code, String url) {
        StringRequest orderListRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("s = " + s);
                try {
                    JSONObject obj;
                    JSONArray arr;
                    JSONObject obj_;
                    JSONArray arr_;
                    StoreOrder order;
                    StoreGoods goods;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("orderlist");
                        arr = obj.getJSONArray("data");
                        if (isRefresh) {
                            mList.clear();
                        }
                        for (int i = 0; i < arr.length(); i++) {
                            order = new StoreOrder();
                            obj = arr.getJSONObject(i);
                            order.setId(obj.getString("id"));
                            order.setOrder_no(obj.getString("order_no"));
                            order.setPrice(obj.getString("actual_price"));
                            order.setCreate_at(obj.getString("create_at"));
                            order.setOrder_status(obj.getString("order_status"));
                            ArrayList<StoreGoods> mArrayList = new ArrayList<>();
                            //StoreOrder对象内的StoreGoods数组
                            arr_ = obj.getJSONArray("user_order_details");
                            for (int j = 0; j < arr_.length(); j++) {
                                goods = new StoreGoods();
                                obj_ = arr_.getJSONObject(j);
                                goods.setId(obj_.getString("id"));
                                goods.setThumb_img(obj_.getString("goods_thumb_img_url"));
                                goods.setGoods_title(obj_.getString("goods_title"));
                                goods.setGoods_property(obj_.getString("goods_property"));
                                goods.setQuantity(obj_.getString("quantity"));
                                goods.setIs_comment(obj_.getString("is_comment"));
                                goods.setGoods_thumb_img_url(obj_.getString("goods_thumb_img_url"));
                                goods.setGoods_id(obj_.getString("goods_id"));
                                goods.setGoods_sku_id(obj_.getString("goods_sku_id"));
                                goods.setSales_price(obj_.getString("unit_price"));
                                mArrayList.add(goods);
                            }
                            order.setUser_order_details(mArrayList);
                            mList.add(order);
                        }

                        LogUtil.i("mList = " + mList);
                        Message msg = new Message();
                        //第一次获取数据，或者下拉刷新数据
                        if (code == GET_DATA_OK) {
                            msg.what = GET_DATA_OK;
                        }
                        //分页加载更多数据
                        if (code == LOAD_MORE_DATA_OK) {
                            msg.what = LOAD_MORE_DATA_OK;
                        }
                        msg.obj = mList;
                        mHandler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("volleyError = " + volleyError.getMessage());
            }
        });
        orderListRequest.setTag("StoreOrderList");
        AppContext.getHttpQueue().add(orderListRequest);
    }

    /**
     * 删除订单
     *
     * @param position
     * @param url
     */
    private void delRequest(final int position, String url) {
        StringRequest delRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        mList.remove(position);
                        mAdapter.notifyItemRemoved(position);
//                        mAdapter.notifyDataSetChanged();
                        ToastUtil.showMessage(StoreOrderListActivity.this, "订单删除成功");
                    } else {
                        ToastUtil.showMessage(StoreOrderListActivity.this, "订单删除失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        delRequest.setTag("StoreOrderList");
        AppContext.getHttpQueue().add(delRequest);
    }

    /**
     * 设置Recycler的Item事件
     */
    private void doRecyclerItemClick() {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(StoreOrderListActivity.this, mRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
//                        Intent intent = new Intent(StoreOrderListActivity.this, StoreOrderActivity.class);
//                        intent.putExtra("local_order_id", mList.get(position).getId());
//                        intent.putExtra("local_order_no", mList.get(position).getOrder_no());
//                        intent.putExtra("total_price", mList.get(position).getPrice());
//                        intent.putExtra("goods_name", mList.get(position).getUser_order_details().get(0).getGoods_title() + "\r...");
//                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, final int position) {

                        new AlertDialog.Builder(StoreOrderListActivity.this)
                                .setTitle("删除订单")
                                .setMessage("确认删除")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //删除某项订单
                                        delRequest(position, URL_Del + local_user_id + "&id=" + mList.get(position - 1).getId());
                                    }
                                }).setNegativeButton("取消", null).show();
                    }
                }));
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        LogUtil.i("onRefresh = ");
        local_page_number = 1;
        String url = URL + local_user_id + "&pageNum=" + local_page_number;
        requestData(GET_DATA_OK, url);
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        LogUtil.i("onLoadMore = ");
        local_page_number++;
        String url = URL + local_user_id + "&pageNum=" + local_page_number;
        requestData(LOAD_MORE_DATA_OK, url);
    }
}
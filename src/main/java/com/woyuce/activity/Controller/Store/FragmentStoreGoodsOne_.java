package com.woyuce.activity.Controller.Store;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.woyuce.activity.Adapter.Store.StoreSpcAdapter_;
import com.woyuce.activity.BaseFragment;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Store.StoreGoods;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.GlideImageLoader;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 两部分，一：将传递到的数据直接放上视图，二：请求规格获取数据
 */
public class FragmentStoreGoodsOne_ extends BaseFragment implements AdapterView.OnItemClickListener {

    private Banner mBanner;
    private TextView mTxtGoodsTitle, mTxtGoodsPrice, mTxtTotalSale, mTxtGoodComment, mTxtShowOrder, mTxtPresentPoint;
    private TextView mTxtSpcOne, mTxtSpcTwo, mTxtSpcThree;

    private String local_skuid, local_goodsid, URL;
    private String return_local_goodsid, return_local_goods_sku_id, return_local_specname, return_local_price;

    public String returenGoodsId() {
        return return_local_goodsid;
    }

    public String returenGoodsSkuId() {
        return return_local_goods_sku_id;
    }

    public String returenGoodsSpecName() {
        return return_local_specname;
    }

    public String returenGoodsPrice() {
        return return_local_price;
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_STORE_GOODS);
        mBanner.stopAutoPlay();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_goods_one, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        local_goodsid = getArguments().getString("goods_id");
        local_skuid = getArguments().getString("goods_sku_id");
        URL = "http://api.iyuce.com/v1/store/goodsdetail?goodsid=" + local_goodsid;

        mBanner = (Banner) view.findViewById(R.id.banner_fragment_store_goods);
        mTxtGoodsTitle = (TextView) view.findViewById(R.id.txt_activity_storegoods_goodstitle);
        mTxtGoodsPrice = (TextView) view.findViewById(R.id.txt_activity_storegoods_goodsprice);
        mTxtTotalSale = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_salenum);
        mTxtGoodComment = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_good);
        mTxtShowOrder = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_showorder);
        mTxtPresentPoint = (TextView) view.findViewById(R.id.txt_storegoods_fragmentone_gift);
        mTxtSpcOne = (TextView) view.findViewById(R.id.txt_fragment_store_one);
        mTxtSpcTwo = (TextView) view.findViewById(R.id.txt_fragment_store_two);
        mTxtSpcThree = (TextView) view.findViewById(R.id.txt_fragment_store_three);

        mGridOne = (GridView) view.findViewById(R.id.gridview_fragment_store_one);
        mGridTwo = (GridView) view.findViewById(R.id.gridview_fragment_store_two);
        mGridThree = (GridView) view.findViewById(R.id.gridview_fragment_store_three);
        mGridOne.setOnItemClickListener(this);
        mGridTwo.setOnItemClickListener(this);
        mGridThree.setOnItemClickListener(this);

        //做第一部分，设置View上的数据
        setView();
        //做第二部分，请求规格参数
        requestGoodsSpe(URL + "&skuid=" + local_skuid, false);
    }

    /**
     * 第一部分:将上一级获取到的数据设置在View上
     */
    private void setView() {
        //将获取到的数据设置到View上
        ArrayList<String> mImgList = getArguments().getStringArrayList("mList");
        mTxtGoodsTitle.setText(getArguments().getString("goods_title"));
        mTxtGoodsPrice.setText(getArguments().getString("sales_price"));
        mTxtTotalSale.setText("销量" + getArguments().getString("total_sales_volume"));
        mTxtGoodComment.setText("好评" + getArguments().getString("total_good_volume"));
        mTxtShowOrder.setText("晒单" + getArguments().getString("total_show_order_volume"));

        //查看是否有轮播图
        if (mImgList.size() == 0) {
            mBanner.setVisibility(View.GONE);
        } else {
            mBanner.setImageLoader(new GlideImageLoader());
            mBanner.setImages(mImgList);
            mBanner.setIndicatorGravity(BannerConfig.RIGHT);
            mBanner.setBannerStyle(BannerConfig.NUM_INDICATOR);
            mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
            mBanner.start();
        }
    }

    /**
     * 第二部分:请求规格参数
     */

    //规格相关View
    private GridView mGridOne, mGridTwo, mGridThree;
    private ArrayList<StoreGoods> mListOne = new ArrayList<>();
    private ArrayList<StoreGoods> mListTwo = new ArrayList<>();
    private ArrayList<StoreGoods> mListThree = new ArrayList<>();
    private StoreSpcAdapter_ mAdapterOne, mAdapterTwo, mAdapterThree;

    private ArrayList<String> mAllSpcId = new ArrayList<>();
    private ArrayList<String> mSelectSpcList = new ArrayList<>();

    private void requestGoodsSpe(String url, final boolean need_notify) {
        HttpUtil.get(url, Constants.ACTIVITY_STORE_GOODS, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj, obj_one, obj_two, obj_three;
                    JSONArray arr_seleted_specs, arr_all_spec_id, arr_all_spec, arr_one, arr_two, arr_three;
                    obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        //拆解JSON对象之一，对象
                        obj = obj.getJSONObject("goods_sku");
                        return_local_goodsid = obj.getString("goods_id");
                        return_local_goods_sku_id = obj.getString("id");
                        return_local_specname = obj.getString("spec_texts");
                        return_local_price = obj.getString("sales_price");
                        mTxtPresentPoint.setText("赠送金币" + obj.getString("present_trade_point"));
                        mTxtGoodsTitle.setText(return_local_specname);
                        mTxtGoodsPrice.setText(return_local_price);

                        //拆解JSON对象之二，数组，选中的规格
                        if (need_notify) {
                            arr_seleted_specs = obj.getJSONArray("seleted_specs");
                            for (int i = 0; i < arr_seleted_specs.length(); i++) {
                                mSelectSpcList.add(arr_seleted_specs.getJSONObject(i).getString("attr_value_id"));
                            }
                            LogUtil.i("mSelectSpcList =" + mSelectSpcList);
                        }

                        //拆解JSON对象之三，数组，所有的规格ID
                        arr_all_spec_id = obj.getJSONArray("all_spec_ids");
                        for (int i = 0; i < arr_all_spec_id.length(); i++) {
                            mAllSpcId.add(arr_all_spec_id.get(i).toString());
                        }

                        //拆解JSON对象之四，数组，所有的规格。这里需要优化或者封装
                        arr_all_spec = obj.getJSONArray("all_specs");
                        if (need_notify) {
                            //如果不是第一次请求，则做数据刷新，先清除数据
                            mListOne.clear();
                            mListTwo.clear();
                            mListThree.clear();
                        }
                        if (arr_all_spec.length() == 1) {
                            obj_one = arr_all_spec.getJSONObject(0);
                            arr_one = obj_one.getJSONArray("spec_values");
                            //获取item数据的数组
                            getDataList(arr_one, mListOne, need_notify);
                            //设置数组的标题
                            mTxtSpcOne.setText(obj_one.getString("attr_text"));
                            mTxtSpcOne.setBackgroundColor(Color.parseColor("#ffffff"));
                        } else if (arr_all_spec.length() == 2) {
                            obj_one = arr_all_spec.getJSONObject(0);
                            obj_two = arr_all_spec.getJSONObject(1);
                            arr_one = obj_one.getJSONArray("spec_values");
                            arr_two = obj_two.getJSONArray("spec_values");
                            //获取item数据的数组
                            getDataList(arr_one, mListOne, need_notify);
                            getDataList(arr_two, mListTwo, need_notify);
                            //设置数组的标题
                            mTxtSpcOne.setText(obj_one.getString("attr_text"));
                            mTxtSpcTwo.setText(obj_two.getString("attr_text"));
                            mTxtSpcOne.setBackgroundColor(Color.parseColor("#ffffff"));
                            mTxtSpcTwo.setBackgroundColor(Color.parseColor("#ffffff"));
                        } else if (arr_all_spec.length() == 3) {
                            obj_one = arr_all_spec.getJSONObject(0);
                            obj_two = arr_all_spec.getJSONObject(1);
                            obj_three = arr_all_spec.getJSONObject(2);
                            arr_one = obj_one.getJSONArray("spec_values");
                            arr_two = obj_two.getJSONArray("spec_values");
                            arr_three = obj_three.getJSONArray("spec_values");
                            //获取item数据的数组
                            getDataList(arr_one, mListOne, need_notify);
                            getDataList(arr_two, mListTwo, need_notify);
                            getDataList(arr_three, mListThree, need_notify);
                            //设置数组的标题
                            mTxtSpcOne.setText(obj_one.getString("attr_text"));
                            mTxtSpcTwo.setText(obj_two.getString("attr_text"));
                            mTxtSpcThree.setText(obj_three.getString("attr_text"));
                            mTxtSpcOne.setBackgroundColor(Color.parseColor("#ffffff"));
                            mTxtSpcTwo.setBackgroundColor(Color.parseColor("#ffffff"));
                            mTxtSpcThree.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        //如果不是第一次加载，刷新数据就好
                        if (need_notify) {
                            mAdapterOne.notifyDataSetChanged();
                            mAdapterTwo.notifyDataSetChanged();
                            mAdapterThree.notifyDataSetChanged();
                        } else {
                            mAdapterOne = new StoreSpcAdapter_(getActivity(), mListOne, mSelectSpcList);
                            mGridOne.setAdapter(mAdapterOne);
                            if (mListOne.size() > 2) {
                                mGridOne.setNumColumns(2);//设置每行显示的Item数
                            } else {
                                mGridOne.setNumColumns(mListOne.size());//设置每行显示的Item数
                            }

                            mAdapterTwo = new StoreSpcAdapter_(getActivity(), mListTwo, mSelectSpcList);
                            mGridTwo.setAdapter(mAdapterTwo);
                            if (mListTwo.size() > 2) {
                                mGridTwo.setNumColumns(2);//设置每行显示的Item数
                            } else {
                                mGridTwo.setNumColumns(mListTwo.size());//设置每行显示的Item数
                            }

                            mAdapterThree = new StoreSpcAdapter_(getActivity(), mListThree, mSelectSpcList);
                            mGridThree.setAdapter(mAdapterThree);
                            if (mListThree.size() > 2) {
                                mGridThree.setNumColumns(2);//设置每行显示的Item数
                            } else {
                                mGridThree.setNumColumns(mListThree.size());//设置每行显示的Item数
                            }
                        }
                    } else {
                        ToastUtil.showMessage(getActivity(), obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 抽象出方法获取数组
     */
    private void getDataList(JSONArray arr, ArrayList<StoreGoods> list, boolean need_notify) throws JSONException {
        StoreGoods storeGoods;
        for (int i = 0; i < arr.length(); i++) {
            storeGoods = new StoreGoods();
            storeGoods.setAttr_id(arr.getJSONObject(i).getString("attr_id"));
            storeGoods.setAttr_text(arr.getJSONObject(i).getString("attr_text"));

            if (need_notify) {
                //两个选中项
                if ((!mAllSpcId.contains("," + storeGoods.getAttr_id() + "," + mSelectSpcList.get(0) + ","))
                        && (!mAllSpcId.contains("," + mSelectSpcList.get(0) + "," + storeGoods.getAttr_id() + ","))) {
                    storeGoods.setAttr_clickable("false");
                } else {
                    storeGoods.setAttr_clickable("true");
                }
//                LogUtil.e(mAllSpcId + "," + arr.getJSONObject(i).getString("attr_id") + mSelectSpcList.get(0));
            } else {
                //一个选中项
                if (!mAllSpcId.contains("," + storeGoods.getAttr_id() + ",")) {
                    storeGoods.setAttr_clickable("false");
                } else {
                    storeGoods.setAttr_clickable("true");
                }
                LogUtil.e(mAllSpcId + "," + arr.getJSONObject(i).getString("attr_id"));
            }

//            if (!mAllSpcId.contains("," + storeGoods.getAttr_id() + ",")) {
//                storeGoods.setAttr_clickable("false");
//            } else {
//                storeGoods.setAttr_clickable("true");
//            }
            list.add(storeGoods);
        }
    }

    /**
     * 重设选中的Item及全部的Item
     */
    private void resetItemView(AdapterView<?> parent, View view, ArrayList<StoreGoods> list) {
        for (int i = 0; i < list.size(); i++) {
            parent.getChildAt(i).findViewById(R.id.txt_storegoods_spc).setBackgroundColor(Color.parseColor("#f0f2f5"));
        }
        view.findViewById(R.id.txt_storegoods_spc).setBackgroundResource(R.drawable.buttonstyle_orangestroke_address);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridview_fragment_store_one:
                resetItemView(parent, view, mListOne);
                requestGoodsSpe(URL + "&skuid=&selected_specs=" + mListOne.get(position).getAttr_id(), true);
                mSelectSpcList.clear();
                mAllSpcId.clear();
                break;
            case R.id.gridview_fragment_store_two:
                resetItemView(parent, view, mListTwo);
                requestGoodsSpe(URL + "&skuid=&selected_specs=" + mListTwo.get(position).getAttr_id() + "," + mSelectSpcList.get(0), true);
                mSelectSpcList.clear();
                mAllSpcId.clear();
                break;
            case R.id.gridview_fragment_store_three:
                resetItemView(parent, view, mListThree);
                requestGoodsSpe(URL + "&skuid=&selected_specs=" + mListThree.get(position).getAttr_id(), true);
                mSelectSpcList.clear();
                mAllSpcId.clear();
                break;
        }
    }
}
package com.woyuce.activity.Controller.Free;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Free.FreePageAdapter;
import com.woyuce.activity.Adapter.Free.FreeSectionAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Controller.Store.StoreGoodsActivity;
import com.woyuce.activity.Controller.WebNoCookieActivity;
import com.woyuce.activity.Model.Free.FreeBook;
import com.woyuce.activity.Model.Free.FreePage;
import com.woyuce.activity.Model.Free.FreeSection;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21
 */
public class FreePageActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // 各类控件
    private TextView mTitle, mImportant, mAll, mImptnothold, mCancelImptHold, mCancelTag;
    private ImageView mBack, mGuidemap;
    private GridView mGridViewPage, mGridViewSection;
    private FrameLayout flSection;

    private int mSectionlength;
    // 用于判断状态的boolean值(以下常量应该考虑枚举，然而枚举性能不佳)
    private int isPageImportants, isSectionImportants, isCanDo;
    private static final int IMPORTANT_NULL = 0, IMPORTANT_TRUE = 1, IMPORTANT_FALSE = 2;
    private static final int CANDO_NULL = 0, CANDO_TRUE = 1, CANDO_FALSE = 2, CANCEL_TAG = -1;

    private String localunit_name, localunit_id, localshow_type_id;
    private String localtoken, localsection_id, localsection_color, localsection_state;
    private FreeBook mFreeBook;

    private List<FreePage> pageList = new ArrayList<>();
    private List<FreeSection> sectionList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtil.removeTag(Constants.ACTIVITY_PAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_page);

        initView();
        requestSectionJson();
    }

    private void initView() {
        localtoken = PreferenceUtil.getSharePre(FreePageActivity.this).getString("localtoken", "");
        mFreeBook = (FreeBook) getIntent().getSerializableExtra("FreeBook");
        localunit_name = getIntent().getStringExtra("localunit_name");
        localunit_id = getIntent().getStringExtra("localunit_id");
        localshow_type_id = getIntent().getStringExtra("localshow_type_id");

        mTitle = (TextView) findViewById(R.id.txt_page_title);
        mAll = (TextView) findViewById(R.id.txt_page_all);
        mImportant = (TextView) findViewById(R.id.txt_page_important);
        mImptnothold = (TextView) findViewById(R.id.txt_page_imptnothold);
        mCancelImptHold = (TextView) findViewById(R.id.txt_page_importantnothold);
        mCancelTag = (TextView) findViewById(R.id.txt_page_canceltag);
        mBack = (ImageView) findViewById(R.id.arrow_back);
        mGuidemap = (ImageView) findViewById(R.id.img_page_guidemap);
        mGridViewPage = (GridView) findViewById(R.id.gridview_page);
        mGridViewSection = (GridView) findViewById(R.id.gridview_section);
        flSection = (FrameLayout) findViewById(R.id.fl_section);

        mBack.setOnClickListener(this);
        mAll.setOnClickListener(this);
        mImportant.setOnClickListener(this);
        mImptnothold.setOnClickListener(this);
        mCancelImptHold.setOnClickListener(this);
        mCancelTag.setOnClickListener(this);
        mGridViewPage.setOnItemClickListener(this);
        mGridViewSection.setOnItemClickListener(this);

        if (TextUtils.isEmpty(localunit_name)) {
            mTitle.setText(mFreeBook.unit_name);
            initGuidemap(mFreeBook.show_type_id);
        } else {
            mTitle.setText(localunit_name);
            initGuidemap(localshow_type_id);
        }
    }

    /**
     * 初始化重点标识图
     */
    private void initGuidemap(String imgurl) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        if (imgurl.equals("wangluoban")) {
            ImageLoader.getInstance().displayImage(Constants.URL_GUIDE_IMG_NET, mGuidemap, options);
        } else {
            ImageLoader.getInstance().displayImage(Constants.URL_GUIDE_IMG_FREE, mGuidemap, options);
        }
    }

    // 加载章节
    private void requestSectionJson() {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("unit_id", TextUtils.isEmpty(localunit_id) ? mFreeBook.unit_id : localunit_id);
        if (isPageImportants == IMPORTANT_NULL) {
            params.put("important", "");
        } else if (isPageImportants == IMPORTANT_TRUE) {
            params.put("important", "true");
        }
        HttpUtil.post(Constants.URL_POST_FREE_SECTION, headers, params, Constants.ACTIVITY_PAGE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    JSONArray arr;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        FreeSection section;
                        mSectionlength = arr.length();
                        if (mSectionlength != 0) {
                            flSection.setVisibility(View.VISIBLE);
                            for (int i = 0; i < mSectionlength; i++) {
                                section = new FreeSection();
                                obj = arr.getJSONObject(i);
                                section.sectionid = obj.getString("section_id");
                                section.sectionname = obj.getString("name");
                                section.sectioncolor = obj.getString("color");
                                section.sectionstate = obj.getString("state");
                                section.range_type = obj.getString("range_type");
                                // 章节状态如果是不可读，则设为灰色
                                if (section.sectionstate.equals("0")) {
                                    section.sectioncolor = "#cccccc";
                                    // 在不可读前提下，如果是重点，则跳过
                                    if (isSectionImportants == IMPORTANT_TRUE) {
                                        continue;
                                    }
                                }
                                sectionList.add(section);
                            }
                        }
                        // 如果章节不为空，则章节ID设为第一个章节的ID,颜色也设为第一个章节的颜色
                        if (mSectionlength != 0) {
                            // TODO 应该做一个循环，把第一项章节的状态赋给它
                            localsection_id = sectionList.get(0).sectionid;
                            localsection_color = sectionList.get(0).sectioncolor;
                            if (isSectionImportants == IMPORTANT_TRUE) {
                                isPageImportants = IMPORTANT_NULL;
                            }
                        }
                        requestPageJson();
                    }
                    FreeSectionAdapter adapter = new FreeSectionAdapter(FreePageActivity.this, sectionList);
                    mGridViewSection.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 加载书页
    private void requestPageJson() {
        progressdialogshow(this);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("unit_id", TextUtils.isEmpty(localunit_id) ? mFreeBook.unit_id : localunit_id);
        params.put("user_id", PreferenceUtil.getSharePre(FreePageActivity.this).getString("userId", ""));
        // 判断获取到的章节数组是否为空
        if (localsection_id == null) {
            params.put("section_id", "");
        } else {
            params.put("section_id", localsection_id);
        }

        // 判断是否查看会做,三种状态的boolean
        if (isCanDo == CANDO_FALSE) {
            params.put("is_can_do", "false");
        } else if (isCanDo == CANDO_NULL) {
            params.put("is_can_do", "");
        } else if (isCanDo == CANDO_TRUE) {
            params.put("is_can_do", "true");
        }

        // 判断是否查看重点,三种状态的boolean
        if (isPageImportants == IMPORTANT_NULL) {
            params.put("important", "");
        } else if (isPageImportants == IMPORTANT_TRUE) {
            params.put("important", "true");
        }
        HttpUtil.post(Constants.URL_POST_FREE_PAGE, headers, params, Constants.ACTIVITY_PAGE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    JSONArray arr;
                    FreePage page;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            page = new FreePage();
                            page.sub_id = obj.getString("sub_id");
                            page.sub_name = obj.getString("sub_name");
                            page.sub_color = obj.getString("sub_color");
                            page.sub_img = obj.getString("sub_img");
                            page.sub_state = obj.getString("sub_state");
                            page.sub_range_type = obj.getString("sub_range_type");
                            page.addtime = obj.getString("addtime");
                            page.sub_img_empty = obj.getString("sub_img_empty");
                            // 如果有章节存在，页码的颜色和状态等于章节的颜色和状态
                            if (mSectionlength != 0) {
                                // 如果页码本身有状态为不可读，则设为灰色
                                if (page.sub_state.equals("0")) {
                                    page.sub_color = "#cccccc";
                                    // 如果是灰色的，在只看重点时，移除
                                    if (isSectionImportants == IMPORTANT_TRUE) {
                                        continue;
                                    }
                                } else {
                                    page.sub_color = localsection_color;
                                }
                            }
                            // 如果无章节且页码为不可读，则设为灰色
                            if (mSectionlength == 0 && page.sub_state.equals("0")) {
                                page.sub_color = "#cccccc";
                                // 在此基础上，如果是重点，则跳过显示
                                if (isPageImportants == IMPORTANT_TRUE) {
                                    continue;
                                }
                            }
                            pageList.add(page);
                        }
                    }
                    // 第二步，将数据放到适配器中
                    FreePageAdapter adapter = new FreePageAdapter(FreePageActivity.this, pageList);
                    mGridViewPage.setAdapter(adapter);
                    progressdialogcancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.txt_page_canceltag:
                // 取消已会的标识
                ObjectAnimator mAnimator5 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator5.setDuration(500).start();

                int CANCEL_TAG = -1;
                checkRight(CANCEL_TAG);
                break;
            case R.id.txt_page_importantnothold:
                // 只看不会重点
                ObjectAnimator mAnimator4 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator4.setDuration(500).start();

                checkRight(CANDO_FALSE);
                break;
            case R.id.txt_page_imptnothold:
                // 只看已会重点
                ObjectAnimator mAnimator0 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator0.setDuration(500).start();

                checkRight(CANDO_TRUE);
                break;
            case R.id.txt_page_important:
                // 只看重点
                ObjectAnimator mAnimator1 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator1.setDuration(500).start();

                isSectionImportants = IMPORTANT_TRUE;
                isPageImportants = IMPORTANT_TRUE;
                isCanDo = CANDO_NULL;
                sectionList.clear();
                pageList.clear();
                requestSectionJson();
                break;
            case R.id.txt_page_all:
                // 查看全部
                ObjectAnimator mAnimator2 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator2.setDuration(500).start();

                isSectionImportants = IMPORTANT_NULL;
                isPageImportants = IMPORTANT_NULL;
                isCanDo = CANDO_NULL;
                sectionList.clear();
                pageList.clear();
                requestSectionJson();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridview_section:
                ObjectAnimator mAnimatorSection = ObjectAnimator.ofFloat(view, "rotationY", 0, 360);
                mAnimatorSection.setDuration(500).start();

                FreeSection section = sectionList.get(position);
                localsection_color = section.sectioncolor;
                localsection_id = section.sectionid;
                localsection_state = section.sectionstate;
                // 如果章节是重点，无论是否重点，页码都必须是重点,这里让isImportant=false,就是传"",后面会显示所有，从而不过滤
                if (isSectionImportants == IMPORTANT_TRUE) {
                    isPageImportants = IMPORTANT_NULL;
                }
                if (localsection_state.equals("0")) {
                    ToastUtil.showMessage(FreePageActivity.this, "该篇章已命中删除");
                    return;
                }
                pageList.clear();
                requestPageJson();
                break;
            case R.id.gridview_page:
                FreePage page = pageList.get(position);
                // 测试中会有Bug,所以加个||条件，双条件，满足其一则可
                if (page.sub_state.equals("0") || page.sub_color.equals("#cccccc")) {
                    ToastUtil.showMessage(FreePageActivity.this, "该页已命中删除");
                    return;
                }
                Intent intent = new Intent(this, FreeContentActivity.class);
                intent.putExtra("localposition", position + "");
                intent.putExtra("localsection_id", localsection_id);
                intent.putExtra("pageList", (Serializable) pageList);
                intent.putExtra("sectionList", (Serializable) sectionList);
                intent.putExtra("localunit_id",TextUtils.isEmpty(localunit_id) ? mFreeBook.unit_id : localunit_id);
                intent.putExtra("localunit_name",TextUtils.isEmpty(localunit_name) ? mFreeBook.unit_name : localunit_name);
                startActivity(intent);
                break;
        }
    }

    /**
     * 提示购买课程
     */
    private void showBuyAd() {
        new AlertDialog.Builder(FreePageActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("报名网络班才能使用这个功能哦，亲")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("报名录播", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getactivegoods();
                    }
                }).setNegativeButton("报名直播", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FreePageActivity.this, WebNoCookieActivity.class);
                intent.putExtra("URL", Constants.URL_WEB_ZHIBO);
                intent.putExtra("TITLE", "网络班直播报名");
                intent.putExtra("COLOR", "#1e87e2");
                startActivity(intent);
            }
        }).setNeutralButton("再想想", null).show();
    }

    /**
     * 获取商城商品信息
     */
    private void getactivegoods() {
        HttpUtil.get(Constants.URL_GetGoods, Constants.ACTIVITY_PAGE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("code").equals("0")) {
                        obj = obj.getJSONObject("data");
                        Intent intent = new Intent(FreePageActivity.this, StoreGoodsActivity.class);
                        intent.putExtra("goods_id", obj.getString("goods_id"));
                        intent.putExtra("goods_sku_id", obj.getString("goods_sku_id"));
                        intent.putExtra("goods_title", obj.getString("goods_title"));
                        intent.putExtra("sales_price", obj.getString("sales_price"));
                        intent.putExtra("can_go_store_back", "yes");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 检测有无网络班权限
     */
    private void checkRight(final int flag) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", PreferenceUtil.getSharePre(FreePageActivity.this).getString("userId", ""));
        params.put("gid", "99");
        HttpUtil.post(Constants.URL_POST_NET_CHECKRIGHT, headers, params, Constants.ACTIVITY_PAGE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0 && obj.getString("data").equals("1")) {
                        // 取消的标识，则做取消的请求
                        if (flag == CANCEL_TAG) {
                            new AlertDialog.Builder(FreePageActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                    .setIcon(android.R.drawable.ic_dialog_info).setTitle("确定要取消所有重点标记吗？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestCancealtag();
                                        }
                                    }).setNegativeButton("取消", null).show();
                        }
                        // 否则，则做是否会做的标识
                        else {
                            isCanDo = flag;
                            isSectionImportants = IMPORTANT_TRUE;
                            isPageImportants = IMPORTANT_TRUE;
                            sectionList.clear();
                            pageList.clear();
                            requestSectionJson();
                        }
                    } else {
                        showBuyAd();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 取消所有标识
     */
    private void requestCancealtag() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", PreferenceUtil.getSharePre(FreePageActivity.this).getString("userId", ""));
        params.put("unit_id", "");
        params.put("section_id", "");
        HttpUtil.post(Constants.URL_POST_NET_CANCELTAG, headers, params, Constants.ACTIVITY_PAGE, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        ToastUtil.showMessage(FreePageActivity.this, "取消成功");
                    } else {
                        ToastUtil.showMessage(FreePageActivity.this, "取消失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
package com.woyuce.activity.Act;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.FreePageAdapter;
import com.woyuce.activity.Adapter.FreeSectionAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.FreePage;
import com.woyuce.activity.Bean.FreeSection;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/21.
 */
public class FreePageActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // 各类控件
    private TextView mTitle, mImportant, mAll, mImptnothold, mCancelImptHold, mCancelTag;
    private ImageView mBack, mGuidemap;
    private GridView mGridViewPage, mGridViewSection;
    private FrameLayout flSection;

    // URL地址
    private String URL_SECTION = "http://api.iyuce.com/v1/exam/section";
    private String URL_PAGE = "http://api.iyuce.com/v1/exam/subjects";
    private String URL_CHECKRIGHT = "http://api.iyuce.com/v1/exam/checkuserforwlb";
    private String URL_CANCELTAG = "http://api.iyuce.com/v1/exam/cancelexams";

    private int mSectionlength;
    // 用于判断状态的boolean值
    private int isPageImportants, isSectionImportants, isCanDo;
    private static final int IMPORTANT_NULL = 0, IMPORTANT_TRUE = 1, IMPORTANT_FALSE = 2;
    private static final int CANDO_NULL = 0, CANDO_TRUE = 1, CANDO_FALSE = 2, CANCEL_TAG = -1;

    private String localtoken, localunit_name, localunit_id, localshow_type_id, localsection_id, localsection_color,
            localsection_state;

    private List<FreePage> pageList = new ArrayList<>();
    private List<FreeSection> sectionList = new ArrayList<>();
    private List<String> pagenNolist = new ArrayList<>();
    private List<String> pageStatelist = new ArrayList<>();
    private List<String> pageImglist = new ArrayList<>();
    private List<String> pageIdlist = new ArrayList<>();
    private List<String> pageColorlist = new ArrayList<>();
    private List<String> pageEmptyImglist = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("page");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freepage);

        initView();
        getSectionJson();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        localunit_name = bundle.getString("localunit_name");
        localunit_id = bundle.getString("localunit_id");
        localshow_type_id = bundle.getString("localshow_type_id");

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

        mTitle.setText(localunit_name);
        initGuidemap(localshow_type_id);
    }

    /**
     * 初始化重点标识图
     */
    private void initGuidemap(String imgurl) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        if (imgurl.equals("wangluoban")) {
            ImageLoader.getInstance().displayImage("http://www.iyuce.com/res/images/assault.jpg", mGuidemap, options);
        } else {
            ImageLoader.getInstance().displayImage("http://www.iyuce.com/res/images/tl.jpg", mGuidemap, options);
        }
    }

    // 加载章节
    private void getSectionJson() {
        StringRequest strinRequest = new StringRequest(Request.Method.POST, URL_SECTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(response);
                    int result = obj.getInt("code");
                    if (result == 0) {
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
                        getPageJson();
                    } else {
                        // LogUtil.e("读取章节失败");
                    }
                    FreeSectionAdapter adapter = new FreeSectionAdapter(FreePageActivity.this, sectionList);
                    mGridViewSection.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(FreePageActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("unit_id", localunit_id);
                if (isPageImportants == IMPORTANT_NULL) {
                    map.put("important", "");
                } else if (isPageImportants == IMPORTANT_TRUE) {
                    map.put("important", "true");
                }
                return map;
            }

        };
        strinRequest.setTag("page");
        AppContext.getHttpQueue().add(strinRequest);
    }

    // 加载书页
    private void getPageJson() {
        progressdialogshow(this);
        StringRequest strinRequest = new StringRequest(Request.Method.POST, URL_PAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                JSONArray arr;
                FreePage page;
                try {
                    obj = new JSONObject(response);
                    int result = obj.getInt("code");
                    if (result == 0) {
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
                            String pageno = page.sub_name;
                            String pageid = page.sub_id;
                            String pagecolor = page.sub_color;
                            String pageimg = page.sub_img;
                            String pagestate = page.sub_state;
                            pagenNolist.add(pageno);
                            pageStatelist.add(pagestate);
                            pageImglist.add(pageimg);
                            pageIdlist.add(pageid);
                            pageColorlist.add(pagecolor);
                            pageEmptyImglist.add(page.sub_img_empty);
                            pageList.add(page);
                        }
                    } else {
                    }
                    // 第二步，将数据放到适配器中
                    FreePageAdapter adapter = new FreePageAdapter(FreePageActivity.this, pageList);
                    mGridViewPage.setAdapter(adapter);
                    progressdialogcancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialogcancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                localtoken = PreferenceUtil.getSharePre(FreePageActivity.this).getString("localtoken", "");
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("unit_id", localunit_id);
                map.put("user_id", PreferenceUtil.getSharePre(FreePageActivity.this).getString("userId", ""));

                // 判断获取到的章节数组是否为空
                if (localsection_id == null) {
                    map.put("section_id", "");
                } else {
                    map.put("section_id", localsection_id);
                }

                // 判断是否查看会做,三种状态的boolean
                if (isCanDo == CANDO_FALSE) {
                    map.put("is_can_do", "false");
                } else if (isCanDo == CANDO_NULL) {
                    map.put("is_can_do", "");
                } else if (isCanDo == CANDO_TRUE) {
                    map.put("is_can_do", "true");
                }

                // 判断是否查看重点,三种状态的boolean
                if (isPageImportants == IMPORTANT_NULL) {
                    map.put("important", "");
                } else if (isPageImportants == IMPORTANT_TRUE) {
                    map.put("important", "true");
                }

                LogUtil.e("localunit_id" + localunit_id + ",,"
                        + PreferenceUtil.getSharePre(FreePageActivity.this).getString("userId", "") + ",,");
                return map;
            }

        };
        strinRequest.setTag("page");
        AppContext.getHttpQueue().add(strinRequest);
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
                // if (!localshow_type_id.equals("wangluoban")) {
                // showBuyAd();
                // break;
                // }
                break;
            case R.id.txt_page_imptnothold:
                // 只看已会重点
                ObjectAnimator mAnimator0 = ObjectAnimator.ofFloat(v, "rotationY", 0, 360);
                mAnimator0.setDuration(500).start();

                checkRight(CANDO_TRUE);
                // if (!localshow_type_id.equals("wangluoban")) {
                // showBuyAd();
                // break;
                // }
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
                pagenNolist.clear();
                pageStatelist.clear();
                pageImglist.clear();
                pageIdlist.clear();
                pageColorlist.clear();
                pageEmptyImglist.clear();

                getSectionJson();
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
                pagenNolist.clear();
                pageStatelist.clear();
                pageImglist.clear();
                pageIdlist.clear();
                pageColorlist.clear();
                pageEmptyImglist.clear();

                getSectionJson();
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
                pagenNolist.clear();
                pageStatelist.clear();
                pageImglist.clear();
                pageIdlist.clear();
                pageColorlist.clear();
                pageEmptyImglist.clear();

                getPageJson();
                break;
            case R.id.gridview_page:
                FreePage page = pageList.get(position);
                // 测试中会有Bug,所以加个||条件，双条件，满足其一则可
                if (page.sub_state.equals("0") || page.sub_color.equals("#cccccc")) {
                    ToastUtil.showMessage(FreePageActivity.this, "该页已命中删除");
                    return;
                }
                Intent intent = new Intent(this, FreeContentActivity.class);
                intent.putStringArrayListExtra("pagenNolist", (ArrayList<String>) pagenNolist);
                intent.putStringArrayListExtra("pageStatelist", (ArrayList<String>) pageStatelist);
                intent.putStringArrayListExtra("pageImglist", (ArrayList<String>) pageImglist);
                intent.putStringArrayListExtra("pageIdlist", (ArrayList<String>) pageIdlist);
                intent.putStringArrayListExtra("pageColorlist", (ArrayList<String>) pageColorlist);
                intent.putStringArrayListExtra("pageEmptyImglist", (ArrayList<String>) pageEmptyImglist);
                intent.putExtra("localposition", position + "");
                intent.putExtra("localunit_id", localunit_id);
                intent.putExtra("localsection_id", localsection_id);
                intent.putExtra("localunit_name", localunit_name);
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
        StringRequest getGoodsRequest = new StringRequest(Request.Method.GET, Constants.URL_GetGoods, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
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
        }, null);
        getGoodsRequest.setTag("page");
        AppContext.getHttpQueue().add(getGoodsRequest);
    }

    /**
     * 检测有无网络班权限
     */
    private void checkRight(final int flag) {
        StringRequest strinrequest = new StringRequest(Request.Method.POST, URL_CHECKRIGHT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray arr;
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    int result = obj.getInt("code");
                    if (result == 0 && obj.getString("data").equals("1")) {
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
                            pagenNolist.clear();
                            pageStatelist.clear();
                            pageImglist.clear();
                            pageIdlist.clear();
                            pageColorlist.clear();
                            pageEmptyImglist.clear();

                            getSectionJson();
                        }

                    } else {
                        showBuyAd();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", PreferenceUtil.getSharePre(FreePageActivity.this).getString("userId", ""));
                map.put("gid", "99");
                return map;
            }
        };
        strinrequest.setTag("page");
        AppContext.getHttpQueue().add(strinrequest);
    }

    /**
     * 取消所有标识
     */
    private void requestCancealtag() {
        StringRequest requestCanceltag = new StringRequest(Request.Method.POST, URL_CANCELTAG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray arr;
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    int result = obj.getInt("code");
                    if (result == 0) {
                        ToastUtil.showMessage(FreePageActivity.this, "取消成功");
                    } else {
                        ToastUtil.showMessage(FreePageActivity.this, "取消失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localtoken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", PreferenceUtil.getSharePre(FreePageActivity.this).getString("userId", ""));
                map.put("unit_id", "");
                map.put("section_id", "");
                return map;
            }
        };
        requestCanceltag.setTag("page");
        AppContext.getHttpQueue().add(requestCanceltag);
    }
}

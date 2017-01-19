package com.woyuce.activity.UI.Activity.Free;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Adapter.Free.FreeListPageAdapter;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;
import com.woyuce.activity.UI.Fragment.Free.FragmentCheckSpell;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;
import com.woyuce.activity.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/9/21.
 */
public class FreeContentActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, FragmentCheckSpell.IShowButton {

    private ImageView mBack, mPre, mNext;
    private PhotoView mContent;
    private TextView mTitle, mBookmenu;

    // 答题卡相关布局
    private Button mBtnToShowFrame;
    private FragmentCheckSpell mFragment;

    // 右侧书页目录
    private ListView mListview;
    private FreeListPageAdapter madapter;
    private boolean isShowList = false;

    private int localposition;
    private String localunit_id, localsection_id, localunit_name;
    private List<String> pagenNolist = new ArrayList<>();
    private List<String> pageStatelist = new ArrayList<>();
    private List<String> pageImglist = new ArrayList<>();
    private List<String> pageIdlist = new ArrayList<>();
    private List<String> pageColorlist = new ArrayList<>();
    private List<String> pageEmptyImglist = new ArrayList<>();

    //第一次引导用的动画
    private ImageView mImgGuide;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(Constants.ACTIVITY_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freecontent);

        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        localunit_name = intent.getStringExtra("localunit_name");
        localposition = Integer.parseInt(intent.getStringExtra("localposition"));
        localunit_id = intent.getStringExtra("localunit_id");
        localsection_id = intent.getStringExtra("localsection_id");
        pagenNolist = intent.getStringArrayListExtra("pagenNolist");
        pageStatelist = intent.getStringArrayListExtra("pageStatelist");
        pageImglist = intent.getStringArrayListExtra("pageImglist");
        pageIdlist = intent.getStringArrayListExtra("pageIdlist");
        pageColorlist = intent.getStringArrayListExtra("pageColorlist");
        pageEmptyImglist = intent.getStringArrayListExtra("pageEmptyImglist");

        mBack = (ImageView) findViewById(R.id.back);
        mPre = (ImageView) findViewById(R.id.img_pre);
        mNext = (ImageView) findViewById(R.id.img_next);
        mImgGuide = (ImageView) findViewById(R.id.img_content_guide);
        mTitle = (TextView) findViewById(R.id.txt_content_title);
        mBookmenu = (TextView) findViewById(R.id.txt_content_bookmenu);
        mContent = (PhotoView) findViewById(R.id.img_content);
        mListview = (ListView) findViewById(R.id.listview_activity_content);

        // 答题卡相关布局
        mBtnToShowFrame = (Button) findViewById(R.id.btn_content_showframe);
        mBtnToShowFrame.setOnClickListener(this);

        mBookmenu.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mPre.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mListview.setOnItemClickListener(this);
        mImgGuide.setOnClickListener(this);

        //第一次引导的动画
        boolean b = PreferenceUtil.getSharePre(FreeContentActivity.this).contains("imgspellguide");
        if (!b) {
            mImgGuide.setVisibility(View.VISIBLE);
        }

        setPageno(localposition);
        getImage(pageImglist.get(localposition));
    }

    // 设置页码
    private void setPageno(int localposition) {
        mTitle.setText("第" + (pagenNolist.get(localposition)) + "页");
    }

    /**
     * 并加载图片内容
     *
     * @param imgurl
     */
    private void getImage(String imgurl) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_loading)
                .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(imgurl, mContent, options);
    }

    /**
     * 拼写检查,如果答案的长度为0，则不显示Fragment
     */
    private void requestJson() {
        String localtoken = PreferenceUtil.getSharePre(this).getString("localtoken", "");
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", "Bearer " + localtoken);
        HttpParams params = new HttpParams();
        params.put("", pageIdlist.get(localposition));

        OkGo.post(Constants.URL_POST_FREE_TOANSWER).tag(Constants.ACTIVITY_CONTENT).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, okhttp3.Response response) {
                        doSuccess(s);
                    }
                });
    }

    private void doSuccess(String response) {
        JSONObject obj;
        JSONArray arr;
        try {
            obj = new JSONObject(response);
            int result = obj.getInt("code");
            if (result == 0) {
                arr = obj.getJSONArray("data");
                if (arr.length() == 0) {
                    ToastUtil.showMessage(FreeContentActivity.this, "亲，真遗憾，本页答案还未制作好哦");
                } else {
                    // 构造Fragement向下传递
                    getImage(pageEmptyImglist.get(localposition));
                    FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
                    String localtoken = PreferenceUtil.getSharePre(FreeContentActivity.this).getString("localtoken", "");
                    mFragment = new FragmentCheckSpell();
                    Bundle bundle = new Bundle();
                    bundle.putString("localsubid", pageIdlist.get(localposition));
                    bundle.putString("localtoken", localtoken);
                    bundle.putString("localimgurl", pageEmptyImglist.get(localposition));
                    bundle.putString("localsectionId", localunit_id);
                    bundle.putString("localunitId", localsection_id);
                    bundle.putString("localunit_name", localunit_name);
                    bundle.putString("localpageno", pagenNolist.get(localposition));
                    mFragment.setArguments(bundle);
                    mTransaction.replace(R.id.frame_activity_content, mFragment).commit();
                    mBtnToShowFrame.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.img_content_guide:
                //只显示一次的引导动画
                mImgGuide.setVisibility(View.GONE);
                PreferenceUtil.save(FreeContentActivity.this, "imgspellguide", "guided");
                break;
            case R.id.btn_content_showframe:
                // 判断是否有挖空的图片，如果没有，就不加载，有则加载
                LogUtil.e("挖空" + pageEmptyImglist.get(localposition));
                if (pageEmptyImglist.get(localposition).equals("")) {
                    ToastUtil.showMessage(FreeContentActivity.this, "本页没有拼写检查");
                    break;
                }
                requestJson();
                // getImage(pageEmptyImglist.get(localposition));
                break;
            case R.id.txt_content_bookmenu:
                madapter = new FreeListPageAdapter(FreeContentActivity.this, pagenNolist, pageColorlist);
                mListview.setAdapter(madapter);

                if (isShowList == false) {
                    mListview.setVisibility(View.VISIBLE);
                    isShowList = true;
                    break;
                } else {
                    mListview.setVisibility(View.GONE);
                    isShowList = false;
                    break;
                }
            case R.id.img_pre:
                if (localposition == 0) {
                    ToastUtil.showMessage(FreeContentActivity.this, "已经是第一页啦，亲");
                    break;
                }
                //mFragment不为空，则移除
                if (mFragment != null) {
                    FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
                    mTransaction.remove(mFragment).commit();
                }
                localposition = localposition - 1;
                if (pageStatelist.get(localposition).equals("0")) {
                    ToastUtil.showMessage(FreeContentActivity.this, "该页已命中删除");
                    break;
                }
                getImage(pageImglist.get(localposition));
                setPageno(localposition);
                break;
            case R.id.img_next:
                if (localposition == pagenNolist.size() - 1) {
                    ToastUtil.showMessage(FreeContentActivity.this, "已经是最后一页啦，亲");
                    break;
                }
                //mFragment不为空，则移除
                if (mFragment != null) {
                    FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
                    mTransaction.remove(mFragment).commit();
                }

                localposition = localposition + 1;
                if (pageStatelist.get(localposition).equals("0")) {
                    ToastUtil.showMessage(FreeContentActivity.this, "该页已命中删除");
                    break;
                }
                getImage(pageImglist.get(localposition));
                setPageno(localposition);
                break;
        }
    }

    // 右侧页码栏
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (pageStatelist.get(position).equals("0") || pageColorlist.get(position).equals("#cccccc")) {
            ToastUtil.showMessage(FreeContentActivity.this, "第" + pagenNolist.get(position) + "页已命中删除");
            return;
        }
        localposition = position;
        getImage(pageImglist.get(localposition));
        setPageno(localposition);

        LogUtil.e("mFragment = " + mFragment);
        if (mFragment != null) {
            FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
            mTransaction.remove(mFragment).commit();
        }
    }

    @Override
    public void showButton() {
        LogUtil.e("showButton() = ");
        mBtnToShowFrame.setVisibility(View.VISIBLE);
        // 取消答题卡时，显示回原图
        getImage(pageImglist.get(localposition));
    }

    @Override
    public void cancelFragment() {
        LogUtil.e("cancelFragment() = ");
        mBtnToShowFrame.setVisibility(View.VISIBLE);
        // 这里会导致返回栈错误,解决方法是判断此时返回栈中是否有Activity
        FragmentTransaction mTransaction = getFragmentManager().beginTransaction();
        mTransaction.remove(mFragment).commit();
    }
}

package com.woyuce.activity.Act.Weibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.woyuce.activity.Act.BaseActivity;
import com.woyuce.activity.Adapter.Weibo.WeiboInfoAdapter;
import com.woyuce.activity.Adapter.Weibo.WeiboInfoImgAdapter;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.Weibo.WeiboBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11.
 */
public class WeiboInfoActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    //接收上一级传递过来的信息
    private String local_headurl, local_body, local_time, local_author, local_reply_count, local_token;
    private int local_microblog_id;
    private List<String> mImgList = new ArrayList<>();

    //发表的内容图片列表
    private GridView mGridView;
    private WeiboInfoImgAdapter mImgAdapter;

    //把URL抽出去
    private String URL = "http://api.iyuce.com/v1/bbs/getrootcomments?MicroblogId=";
    private String URL_SUBCOMMIT = "http://api.iyuce.com/v1/bbs/subcomment";

    //评论回复列表
    private ListView mListView;
    private WeiboInfoAdapter mAdapter;
    private List<WeiboBean> mDataList = new ArrayList<>();

    private TextView mTxtAuthor, mTxtBody, mTxtTime, mTxtReplyCount;
    private ImageView mIconHead;

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("weiboinfo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weiboinfo);

        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        local_body = intent.getStringExtra("local_body");
        local_time = intent.getStringExtra("local_time");
        local_author = intent.getStringExtra("local_author");
        local_headurl = intent.getStringExtra("local_headurl");
        local_reply_count = intent.getStringExtra("local_reply_count");
        local_token = intent.getStringExtra("local_token");
        local_microblog_id = intent.getIntExtra("local_microblog_id", -1);
        mImgList = intent.getStringArrayListExtra("mImgList");

        mTxtAuthor = (TextView) findViewById(R.id.txt_weiboinfo_username);
        mTxtBody = (TextView) findViewById(R.id.txt_weiboinfo_body);
        mTxtTime = (TextView) findViewById(R.id.txt_weiboinfo_time);
        mTxtReplyCount = (TextView) findViewById(R.id.txt_weiboinfo_replycount);
        mIconHead = (ImageView) findViewById(R.id.img_weiboinfo_headphoto);

        //评论列表
        mListView = (ListView) findViewById(R.id.listview_activity_weiboinfo);
        mListView.setOnItemClickListener(this);
        //图片列表
        mGridView = (GridView) findViewById(R.id.gridview_activity_weiboinfo);
        mImgAdapter = new WeiboInfoImgAdapter(this, mImgList);
        mGridView.setAdapter(mImgAdapter);

        mTxtAuthor.setText(local_author);
        mTxtBody.setText(local_body);
        mTxtTime.setText(local_time);
        mTxtReplyCount.setText("全部评论  ( " + local_reply_count + " )");
        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(R.mipmap.img_duck)
                .showImageOnFail(R.mipmap.img_duck)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(local_headurl, mIconHead, options);

        requestJson();
    }

    /**
     * 评论列表ListView填充数据
     */
    private void requestJson() {
        LogUtil.i("local_microblog_id = " + local_microblog_id);
        StringRequest weiboDataRequest = new StringRequest(Request.Method.GET, URL + local_microblog_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                JSONArray arr;
                try {
                    obj = new JSONObject(response);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        WeiboBean weibo;
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            weibo = new WeiboBean();
                            weibo.author = obj.getString("author");
                            weibo.body = obj.getString("body");
                            weibo.date_created = obj.getString("date_created");
                            weibo.subject = obj.getString("subject");
                            weibo.commented_object_id = obj.getString("commented_object_id");
                            weibo.parent_id = obj.getInt("parent_id");
                            mDataList.add(weibo);
                        }
                    } else {
                        LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + obj.getString("message"));
                    }
                    // 将数据放到适配器中
                    mAdapter = new WeiboInfoAdapter(WeiboInfoActivity.this, mDataList);
                    mListView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + local_token);
                return headers;
            }
        };
        weiboDataRequest.setTag("weiboinfo");
        weiboDataRequest.setRetryPolicy(new DefaultRetryPolicy(180 * 1000, 1, 1.0f));
        AppContext.getHttpQueue().add(weiboDataRequest);
    }

    /**
     * 评论微博
     */
    private void requestSubcommit(final int parent_id, final String subject, final String commented_object_id, final String body) {
        StringRequest weiboSubcommitRequest = new StringRequest(Request.Method.POST, URL_SUBCOMMIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("评论提交成功 = " + response);
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (obj.getInt("code") == 0) {
                        ToastUtil.showMessage(WeiboInfoActivity.this, "评论成功啦");
                        //刷新评论列表
                        mDataList.clear();
                        mAdapter.notifyDataSetChanged();
                        requestJson();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("wrong message = " + volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + local_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                LogUtil.e("param = " + ";body =" + body + ";local_author =" + local_author + ";subject =" + subject);
                map.put("Author", local_author);
                map.put("UserId", PreferenceUtil.getSharePre(WeiboInfoActivity.this).getString("userId", ""));
                map.put("Body", body);
                map.put("Subject", subject);
                map.put("CommentedObjectId", commented_object_id);
                return map;
            }
        };
        weiboSubcommitRequest.setTag("weiboinfo");
        weiboSubcommitRequest.setRetryPolicy(new DefaultRetryPolicy(180 * 1000, 1, 1.0f));
        AppContext.getHttpQueue().add(weiboSubcommitRequest);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int parent_id = mDataList.get(position).parent_id;
        final String subject = mDataList.get(position).subject;
        final String commented_object_id = mDataList.get(position).commented_object_id;

        //评论原微博下的评论
        final EditText mEdit = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("请输入评论内容")
                .setView(mEdit)
                .setPositiveButton("确定评论", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String body = mEdit.getText().toString();
                        requestSubcommit(parent_id, subject, commented_object_id, body);
                        mEdit.setText("");
                    }
                }).show();
    }

    //评论原微博
    public void toReply(View view) {
        final EditText mEdit = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("请输入评论内容")
                .setView(mEdit)
                .setPositiveButton("确定评论", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String body = mEdit.getText().toString();
                        ToastUtil.showMessage(WeiboInfoActivity.this, "评论原微博，内容为=" + body);
                        requestSubcommit(0, "伪subject，去截取", local_microblog_id + "", body);
                        mEdit.setText("");
                    }
                }).show();
    }
}

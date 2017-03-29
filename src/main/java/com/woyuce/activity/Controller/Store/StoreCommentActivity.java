package com.woyuce.activity.Controller.Store;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.AppContext;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19.
 */
public class StoreCommentActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTxtStoreName;
    private EditText mEdtComment;
    private ImageView mImgGood, mImgMedium, mImgBad;

    private String URL_COMMENT = "http://api.iyuce.com/v1/store/submitcomment";

    private String local_order_id;

    //默认好评
    private String local_comment_star = "Good";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_comment);

        initView();
    }

    private void initView() {
        local_order_id = getIntent().getStringExtra("local_order_id");

        mImgGood = (ImageView) findViewById(R.id.img_activity_storecomment_good);
        mImgMedium = (ImageView) findViewById(R.id.img_activity_storecomment_medium);
        mImgBad = (ImageView) findViewById(R.id.img_activity_storecomment_bad);
        mImgGood.setOnClickListener(this);
        mImgMedium.setOnClickListener(this);
        mImgBad.setOnClickListener(this);

        mEdtComment = (EditText) findViewById(R.id.edt_activity_storecomment_content);
        mTxtStoreName = (TextView) findViewById(R.id.txt_activity_storecomment_storename);
        mTxtStoreName.setText("商品名称: " + getIntent().getStringExtra("goods_name"));
    }

    /**
     * 请求提交评论
     *
     * @param url
     * @param userid
     * @param user_comment
     */
    private void storeCommentRequest(String url, final String userid, final String user_comment, final String local_comment_star) {
        StringRequest storeCommentRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("comment s = " + s);
                JSONObject obj;
                try {
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(StoreCommentActivity.this, obj.getString("message"));
                        new AlertDialog.Builder(StoreCommentActivity.this)
                                .setMessage("感谢您的评论")
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StoreCommentActivity.this.finish();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("volleyError = " + volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", userid);
                map.put("item_id", local_order_id);
                map.put("cmt_text", user_comment);
                map.put("satisfaction", local_comment_star);
                return map;
            }
        };
        storeCommentRequest.setTag("storeCommentRequest");
        AppContext.getHttpQueue().add(storeCommentRequest);
    }

    public void toSubmit(View view) {
        if (TextUtils.isEmpty(mEdtComment.getText().toString())) {
            ToastUtil.showMessage(this, "您还没有填写评论内容哦");
            return;
        }
        //这里拼接URL
        String user_id = PreferenceUtil.getSharePre(this).getString("userId", null);
        String user_comment = mEdtComment.getText().toString();
        storeCommentRequest(URL_COMMENT, user_id, user_comment, local_comment_star);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_activity_storecomment_good:
                resetImg();
                mImgGood.setBackgroundResource(R.mipmap.icon_star_yellow);
                mImgMedium.setBackgroundResource(R.mipmap.icon_star_yellow);
                mImgBad.setBackgroundResource(R.mipmap.icon_star_yellow);
                local_comment_star = "Good";
                break;
            case R.id.img_activity_storecomment_medium:
                resetImg();
                mImgMedium.setBackgroundResource(R.mipmap.icon_star_yellow);
                mImgBad.setBackgroundResource(R.mipmap.icon_star_yellow);
                local_comment_star = "Medium";
                break;
            case R.id.img_activity_storecomment_bad:
                resetImg();
                mImgBad.setBackgroundResource(R.mipmap.icon_star_yellow);
                local_comment_star = "Bad";
                break;

        }
    }

    private void resetImg() {
        mImgGood.setBackgroundResource(R.mipmap.icon_star_gray);
        mImgMedium.setBackgroundResource(R.mipmap.icon_star_gray);
        mImgBad.setBackgroundResource(R.mipmap.icon_star_gray);
    }
}
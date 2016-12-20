package com.woyuce.activity.Act;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.Application.AppContext;
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
public class StoreCommentActivity extends BaseActivity {

    private TextView mTxtStoreName, mTxtStoreTime;
    private EditText mEdtComment;

    private String URL_COMMENT = "http://api.iyuce.com/v1/store/submitcomment?";

    private String local_order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storecoment);

        initView();
    }

    private void initView() {
        local_order_id = getIntent().getStringExtra("local_order_id");

        mEdtComment = (EditText) findViewById(R.id.edt_activity_storecomment_content);
        mTxtStoreName = (TextView) findViewById(R.id.txt_activity_storecomment_storename);
        mTxtStoreTime = (TextView) findViewById(R.id.txt_activity_storecomment_ordertime);
        mTxtStoreName.setText("商品名称: " + getIntent().getStringExtra("goods_name"));
        mTxtStoreTime.setText("金额：其实应该是时间" + getIntent().getStringExtra("total_price"));
    }

    private void storeCommentRequest(String url, final String userid, final String user_comment) {

        StringRequest storeCommentRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogUtil.i("comment s = " + s);
                JSONObject obj;
                try {
                    obj = new JSONObject(s);
                    if (obj.getString("code").equals("0")) {
                        ToastUtil.showMessage(StoreCommentActivity.this, obj.getString("message"));
                        StoreCommentActivity.this.finish();
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
                map.put("userId", userid);
                map.put("itemId", local_order_id);
                map.put("cmtText", user_comment);
                map.put("satisfaction", "Good");
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
        String final_url = URL_COMMENT + "userId=" + user_id + "&itemId=" + local_order_id + "&imgUrl=&cmtText=" +
                user_comment + "&satisfaction=" + "Good";
        LogUtil.i("all = " + local_order_id + user_id);
        storeCommentRequest(final_url, user_id, user_comment);
    }

    public void back(View view) {
        finish();
    }
}
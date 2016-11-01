package com.woyuce.activity.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.Application.AppContext;
import com.woyuce.activity.Bean.SpeakingContent;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SpeakingContentActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtTitle;
    private Button btnBack, btnVote, btnShare;
    private PhotoView imgSubContent;

    private String local_url = "http://iphone.ipredicting.com/kysubContent.aspx";
    private String localsubCategoryid, localsubid; // 从上一级不同Activity中柱状图Item被选中后传递过来的不同本地参数
    private String localImg, localsubID, localsubName;

    private List<SpeakingContent> subContentList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        AppContext.getHttpQueue().cancelAll("subcontent");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakingcontent);

        initView();
        getJson();
    }

    private void initView() {
        Intent it_subContent = getIntent();
        localsubCategoryid = it_subContent.getStringExtra("localsubCategoryid");
        localsubid = it_subContent.getStringExtra("localsubid");
        LogUtil.e("ID", "ID = " + localsubCategoryid + "，id = " + localsubid);

        imgSubContent = (PhotoView) findViewById(R.id.img_subcontent);
        txtTitle = (TextView) findViewById(R.id.txt_subcontent_title);
        btnVote = (Button) findViewById(R.id.btn_subcontent_vote);
        btnBack = (Button) findViewById(R.id.button_subcontent_back);
        btnShare = (Button) findViewById(R.id.btn_subcontent_forshare);

        btnVote.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    private void getJson() {
        StringRequest strinRequest = new StringRequest(Request.Method.POST, local_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    SpeakingContent subContent;
                    int result = jsonObject.getInt("code");
                    if (result == 0) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        jsonObject = data.getJSONObject(0);
                        subContent = new SpeakingContent();
                        subContent.subanswer = jsonObject.getString("subanswer");
                        subContent.subid = jsonObject.getString("subid");
                        subContent.subimg = jsonObject.getString("subimg");
                        subContent.subname = jsonObject.getString("subname");
                        subContent.timestamp = jsonObject.getString("timestamp");
                        subContentList.add(subContent);
                        // localAnswer = subContent.subanswer;
                        localImg = subContent.subimg;
                        localsubID = subContent.subid; // 取出后给下一级 "投票" 使用
                        localsubName = subContent.subname; // 取出后给下一级 "投票" 使用
                        getImageView(); // 设置图片
                        txtTitle.setText(subContent.subname); // 显示Title
//						imgPreloading.setVisibility(View.GONE);
                        imgSubContent.setVisibility(View.VISIBLE);
                    } else {
                        LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Log.e("DATA-BACK", "JSON接口返回的信息： " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("Wrong_BACK", "联接错误原因： " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<String, String>();
                if (localsubCategoryid != null) {
                    hashMap.put("subCategoryid", localsubCategoryid);
                } else {
                    hashMap.put("subid", localsubid);
                }
                return hashMap;
            }
        };
        strinRequest.setTag("subcontent");
        AppContext.getHttpQueue().add(strinRequest);
    }

    private void getImageView() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.img_error)
                .showImageOnFail(R.mipmap.img_error).cacheInMemory(true).cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(localImg, imgSubContent, options);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subcontent_vote:
                Intent intent = new Intent(this, SpeakingVoteActivity.class);
                intent.putExtra("localsubID", localsubID);
                intent.putExtra("localsubName", localsubName);
                intent.putExtra("local_url", localImg);
                LogUtil.e("what local_url = " +localImg);
                startActivity(intent);
                break;
            case R.id.btn_subcontent_forshare:
                showShare();
                break;
            case R.id.button_subcontent_back:
                finish();
                break;
        }
    }


    /**
     * MOB分享
     */
    private void showShare() {
        try {
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mDatetime = sdf.format(new Date());
//			String encode_title = URLEncoder.encode(localunit_name, "utf-8");
            String encode_collage = URLEncoder.encode("我在我预测APP学习了口语话题" +  localsubName,"utf-8");
            String URLcode = encode_collage.replace("+","%20");
            LogUtil.e("imgurl = " + localImg);
            String url = "http://xm.iyuce.com/app/fenxiang.html?viewid=4&img="+localImg+"&title=&collage="+ URLcode +"&datetime=" + mDatetime;

            ShareSDK.initSDK(this);
            OnekeyShare oks = new OnekeyShare();

            //微信不同平台
//			oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//				@Override
//				public void onShare(Platform platform, ShareParams paramsToShare) {
//					if (Wechat.NAME.equals(platform.getName())){
//						paramsToShare.setTitle("分享到微信好友title");
//						paramsToShare.setText("分享到微信好友content");
//						paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//						paramsToShare.setUrl("http://mob.com");
//						paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//					}else if(WechatMoments.NAME.equals(platform.getName())){
//						paramsToShare.setTitle("分享到微信朋友圈title");
//						paramsToShare.setText("分享到微信朋友圈content");
//						paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//						paramsToShare.setUrl("http://mob.com");
//						paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//					}
//				}
//			});
            oks.setTitle("我分享了口语话题:" + localsubName);
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(url);
            // text是分享文本，所有平台都需要这个字段
            oks.setText("我们不卖答案，我们是试卷的搬运工");
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(url);
            oks.setImageUrl("http://www.iyuce.com/uploadfiles/app/logo.png");
            oks.setComment("答题超赞");
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(url);
            // 启动分享GUI
            oks.show(this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
package com.woyuce.activity.Controller.Free;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.woyuce.activity.Adapter.Free.FreeSpellAdapter;
import com.woyuce.activity.Common.Constants;
import com.woyuce.activity.Model.Free.FreeSpellBean;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.Http.Volley.HttpUtil;
import com.woyuce.activity.Utils.Http.Volley.RequestInterface;
import com.woyuce.activity.Utils.LogUtil;
import com.woyuce.activity.Utils.PreferenceUtil;
import com.woyuce.activity.Utils.ToastUtil;

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

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class FragmentCheckSpell extends Fragment implements OnClickListener {

    private Button mFinish, mCancel;

    private ListView mListview;
    private List<FreeSpellBean> spellbeanList = new ArrayList<>();
    private FreeSpellAdapter mAdapters;

    private List<String> answerList = new ArrayList<>();

    // 是否答题
    private boolean FLAG = false;
    // 实例化接口
    private IShowButton mShowbutton;

    // 获取需要的subid
    private String localsubid, localtoken, localimgurl, localsectionId, localunitId, localunit_name, localpage_no;

    //记分用
    private String mShowCount;

    /**
     * 销毁Fragment的View时，让Activity实现方法
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mShowbutton.showButton();
        spellbeanList.clear();
    }

    /**
     * 将Activity与Fragment中的接口绑定,即将Activity强转为接口
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mShowbutton = (IShowButton) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_free_check_spell, null);
        initView(view);
        requestJson();
        return view;
    }

    private void initView(View view) {
        //获取Activity传来的参数
        Bundle bundle = getArguments();
        localsubid = bundle.getString("localsubid");
        localtoken = bundle.getString("localtoken");
        localimgurl = bundle.getString("localimgurl");
        localsectionId = bundle.getString("localsectionId");
        localunitId = bundle.getString("localunitId");
        localunit_name = bundle.getString("localunit_name");
        localpage_no = bundle.getString("localpageno");

        mFinish = (Button) view.findViewById(R.id.btn_checkspell_finish);
        mCancel = (Button) view.findViewById(R.id.btn_checkspell_cancel);
        mListview = (ListView) view.findViewById(R.id.listview_fragment_checkspell);
        mFinish.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private void requestJson() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        params.put("", localsubid);
        HttpUtil.post(Constants.URL_POST_FREE_TOANSWER, headers, params, Constants.FRAGMENT_CONTENT, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    JSONArray arr;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        arr = obj.getJSONArray("data");
                        FreeSpellBean spellbean;
                        for (int i = 0; i < arr.length(); i++) {
                            obj = arr.getJSONObject(i);
                            spellbean = new FreeSpellBean();
                            spellbean.answer = obj.getString("answer");
                            spellbean.num = obj.getString("num");
                            spellbean.spell = " ";
                            answerList.add(spellbean.answer);
                            spellbeanList.add(spellbean);
                        }
                    } else {
                        LogUtil.e("code!=0 Data-BACK", "读取页面失败： " + obj.getString("message"));
                    }
                    // 将数据放到适配器中
                    mAdapters = new FreeSpellAdapter(getActivity(), spellbeanList, FLAG, answerList);
                    mListview.setAdapter(mAdapters);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 修改标记为会做或不会做
     */
    private void tagTo(final String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + localtoken);
        HashMap<String, String> params = new HashMap<>();
        if (url.equals(Constants.URL_POST_FREE_ANSWER_TAGCAN)) {
            params.put("user_id", PreferenceUtil.getSharePre(getActivity()).getString("userId", ""));
            params.put("sub_id", localsubid);
        } else if (url.equals(Constants.URL_POST_FREE_ANSWER_TAGCANT)) {
            params.put("user_id", PreferenceUtil.getSharePre(getActivity()).getString("userId", ""));
            params.put("unit_id", localunitId);
            params.put("section_id", localsectionId);
        }
        HttpUtil.post(url, headers, params, Constants.FRAGMENT_CONTENT, new RequestInterface() {
            @Override
            public void doSuccess(String result) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        ToastUtil.showMessage(getActivity(), "标记成功");
                    } else {
                        ToastUtil.showMessage(getActivity(), "标记失败，请稍候再试");
                    }
                    // 将数据放到适配器中
                    mAdapters = new FreeSpellAdapter(getActivity(), spellbeanList, FLAG, answerList);
                    mListview.setAdapter(mAdapters);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // 点击“答案”按钮后，标记改变
        switch (v.getId()) {
            case R.id.btn_checkspell_cancel:
                mShowbutton.cancelFragment();
                spellbeanList.clear();
                break;
            case R.id.btn_checkspell_finish:
                if (!FLAG) {
                    FLAG = true;
                    // 判断得分
                    ArrayList<FreeSpellBean> spellList = mAdapters.returnSpellList();
                    int mCount = 0;
                    for (int i = 0; i < answerList.size(); i++) {
                        if (spellList.get(i).spell.toLowerCase().trim()
                                .equals(answerList.get(i).toLowerCase().trim())) {
                            mCount = mCount + 10;
                        }
                    }
                    LogUtil.e("mCount = " + mCount);
                    mShowCount = mCount + "";
                    new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("您的得分是：" + mCount + "分")
                            .setPositiveButton("标记本页为会做", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 标记本页为会做
                                    tagTo(Constants.URL_POST_FREE_ANSWER_TAGCAN);
                                }
                            }).setNegativeButton("分享成绩", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //MOB分享
                            showShare(mShowCount);
                        }
                    }).setNeutralButton("不标记", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 标记本页为不会做
                            // tagTo(URL_TAGCANT);
                        }
                    }).show();
                    mFinish.setText("重做");
                } else {
                    FLAG = false;
                    mFinish.setText("交卷");
                }
                mAdapters.notifyDataSetChanged();
                mAdapters = new FreeSpellAdapter(getActivity(), spellbeanList, FLAG, answerList);
                mListview.setAdapter(mAdapters);
                break;
        }
    }

    /**
     * 定义一个接口供Activity调用,实现showButton方法
     */
    public interface IShowButton {
        void showButton();

        void cancelFragment();
    }

    /**
     * MOB分享
     */
    private void showShare(String mShowCount) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mDatetime = sdf.format(new Date());
            String encode_title = URLEncoder.encode(localunit_name, "utf-8");
            String count_message = localunit_name + "第" + localpage_no + "页," + mShowCount + "分";
            String encode_collage = URLEncoder.encode(count_message, "utf-8");
            String url = "http://xm.iyuce.com/app/fenxiang.html?viewid=3&img=" + localimgurl + "&title=&collage=" + encode_collage + "&datetime=" + mDatetime;

            ShareSDK.initSDK(getActivity());
            OnekeyShare oks = new OnekeyShare();

            oks.setTitle("我在背" + localunit_name + "第" + localpage_no + "页," + "得了" + mShowCount + "分");
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
            oks.show(getActivity());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
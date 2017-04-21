package com.woyuce.activity.Controller.Main;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ToastUtil;

public class FragmentStore extends Fragment implements View.OnClickListener {

    private ImageView qqOne, qqTwo, weixinOne, weixinTwo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_service, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        qqOne = (ImageView) view.findViewById(R.id.img_tab4_qqone);
        qqTwo = (ImageView) view.findViewById(R.id.img_tab4_qqtwo);
        weixinOne = (ImageView) view.findViewById(R.id.img_tab4_weixinone);
        weixinTwo = (ImageView) view.findViewById(R.id.img_tab4_weixintwo);

        qqOne.setOnClickListener(this);
        qqTwo.setOnClickListener(this);
        weixinOne.setOnClickListener(this);
        weixinTwo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_tab4_qqone:
                if (!isApkInstalled(getActivity(), "com.tencent.mobileqq")) {
                    ToastUtil.showMessage(getActivity(), "您没有安装腾讯QQ哦，亲");
                    return;
                } else {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=3242880686";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                break;
            case R.id.img_tab4_qqtwo:
                if (!isApkInstalled(getActivity(), "com.tencent.mobileqq")) {
                    ToastUtil.showMessage(getActivity(), "您没有安装腾讯QQ哦，亲");
                    return;
                } else {
                    String urltwo = "mqqwpa://im/chat?chat_type=wpa&uin=3217852966";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urltwo)));
                    break;
                }
            case R.id.img_tab4_weixinone:
                if (!isApkInstalled(getActivity(), "com.tencent.mm")) {
                    ToastUtil.showMessage(getActivity(), "您没有安装微信哦，亲");
                } else {
                    Intent intent = new Intent();
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                }
                break;
            case R.id.img_tab4_weixintwo:
                if (!isApkInstalled(getActivity(), "com.tencent.mm")) {
                    ToastUtil.showMessage(getActivity(), "您没有安装微信哦，亲");
                } else {
                    Intent intent = new Intent();
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * "com.tencent.mobileqq" QQ包名
     * "com.tencent.mm" 微信包名
     * 该方法判断手机中是否有对应应用包
     */
    public static boolean isApkInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
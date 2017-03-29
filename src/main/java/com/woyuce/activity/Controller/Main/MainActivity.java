package com.woyuce.activity.Controller.Main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.woyuce.activity.Controller.Store.FragmentStoreHome;
import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ActivityManager;
import com.woyuce.activity.View.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnClickListener, OnPageChangeListener {

    private NoScrollViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;

    private LinearLayout mLinearlayout1, mLinearlayout2, mLinearlayout3, mLinearlayout4, mLinearlayout5;
    private ImageView mImgtab1, mImgtab2, mImgtab4, mImgtab5;
    private TextView mTxt1, mTxt2, mTxt4, mTxt5;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("确认退出吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CookieManager.getInstance().removeAllCookie();
                        ActivityManager.getAppManager().finishAllActivity();
                        MainActivity.this.finish();
                    }
                }).setNegativeButton("返回", null).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //因为已经集成了FragmentActivity,加入Activity管理栈
        ActivityManager.getAppManager().addActivity(this);

        initView();
        initEvent();
        setSelect(0);

        //微博相册图片辅助类初始化(这一步必须，开启异步，否则后面相册无法打开，会崩溃)
        //LocalImageHelper.init(AppContext.getInstance());
    }

    private void initView() {
        mLinearlayout1 = (LinearLayout) findViewById(R.id.linearlayout_tab1);
        mLinearlayout2 = (LinearLayout) findViewById(R.id.linearlayout_tab2);
        mLinearlayout3 = (LinearLayout) findViewById(R.id.linearlayout_tab3);
        mLinearlayout4 = (LinearLayout) findViewById(R.id.linearlayout_tab4);
        mLinearlayout5 = (LinearLayout) findViewById(R.id.linearlayout_tab5);

        mImgtab1 = (ImageView) findViewById(R.id.icon_home);
        mImgtab2 = (ImageView) findViewById(R.id.icon_social);
        mImgtab4 = (ImageView) findViewById(R.id.icon_client);
        mImgtab5 = (ImageView) findViewById(R.id.icon_mine);

        mTxt1 = (TextView) findViewById(R.id.txt_home);
        mTxt2 = (TextView) findViewById(R.id.txt_social);
        mTxt4 = (TextView) findViewById(R.id.txt_client);
        mTxt5 = (TextView) findViewById(R.id.txt_mine);

        mViewPager = (NoScrollViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setOnPageChangeListener(this);
        mFragments = new ArrayList<>();
        Fragment mTab01 = new FragmentHome();
        Fragment mTab02 = new FragmentSocial();
        Fragment mTab03 = new FragmentWeb();
        Fragment mTab04 = new FragmentStoreHome();
        Fragment mTab05 = new FragmentMine();

        mFragments.add(mTab01);
        mFragments.add(mTab02);
        mFragments.add(mTab03);
        mFragments.add(mTab04);
        mFragments.add(mTab05);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
    }

    private void initEvent() {
        mLinearlayout1.setOnClickListener(this);
        mLinearlayout2.setOnClickListener(this);
        mLinearlayout3.setOnClickListener(this);
        mLinearlayout4.setOnClickListener(this);
        mLinearlayout5.setOnClickListener(this);
    }

    private void setSelect(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i);
    }

    private void setTab(int i) {
        resetImg();
        switch (i) {
            case 0:
                mImgtab1.setImageResource(R.mipmap.icon_home_pressed);
                mTxt1.setTextColor(Color.parseColor("#f25f11"));
                break;
            case 1:
                mImgtab2.setImageResource(R.mipmap.icon_social_pressed);
                mTxt2.setTextColor(Color.parseColor("#f25f11"));
                break;
            case 3:
                mImgtab4.setImageResource(R.mipmap.icon_client_pressed);
                mTxt4.setTextColor(Color.parseColor("#f25f11"));
                break;
            case 4:
                mImgtab5.setImageResource(R.mipmap.icon_mine_pressed);
                mTxt5.setTextColor(Color.parseColor("#f25f11"));
                break;
        }
    }

    // 每次点击后,图片及文字回复原状
    private void resetImg() {
        mImgtab1.setImageResource(R.mipmap.icon_home);
        mTxt1.setTextColor(Color.parseColor("#707070"));
        mImgtab2.setImageResource(R.mipmap.icon_social);
        mTxt2.setTextColor(Color.parseColor("#707070"));
        mImgtab4.setImageResource(R.mipmap.icon_client);
        mTxt4.setTextColor(Color.parseColor("#707070"));
        mImgtab5.setImageResource(R.mipmap.icon_mine);
        mTxt5.setTextColor(Color.parseColor("#707070"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearlayout_tab1:
                setSelect(0);
                break;
            case R.id.linearlayout_tab2:
                setSelect(1);
                break;
            case R.id.linearlayout_tab3:
                setSelect(2);
                break;
            case R.id.linearlayout_tab4:
                setSelect(3);
                break;
            case R.id.linearlayout_tab5:
                setSelect(4);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        setSelect(arg0);
    }
}
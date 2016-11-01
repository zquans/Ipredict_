package com.woyuce.activity.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woyuce.activity.R;
import com.woyuce.activity.Utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 */
public class WelcomeActivity extends Activity {

    private ViewPager mViewpager;
    private List<View> mList = new ArrayList<>();
    private View view1, view2, view3;
    private PagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (PreferenceUtil.getSharePre(WelcomeActivity.this).contains("welcome")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            initView();
        }
    }

    private void initView() {
        mViewpager = (ViewPager) findViewById(R.id.viewpager_activity_animation);

        initPager();
    }

    private void initPager() {
        LayoutInflater mInflate = getLayoutInflater().from(this);

        view1 = mInflate.inflate(R.layout.pageitem_welcome_a, null);
        view2 = mInflate.inflate(R.layout.pageitem_welcome_b, null);
        view3 = mInflate.inflate(R.layout.pageitem_welcome_c, null);
        mList.add(view1);
        mList.add(view2);
        mList.add(view3);
        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mList.get(position));
                return mList.get(position);
            }
        };
        mViewpager.setAdapter(mAdapter);
    }

    public void animationWelcome(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        PreferenceUtil.save(WelcomeActivity.this, "welcome", "1");
        finish();
    }
}
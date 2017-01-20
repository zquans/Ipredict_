package com.woyuce.activity.UI.Activity.Store;

import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.R;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/12/29.
 */
public class ShowImgActivity extends BaseActivity {

    private PhotoView mPhotoView;
    private String img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimg);

        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        img_url = getIntent().getStringExtra("img_url");
        mPhotoView = (PhotoView) findViewById(R.id.photoview_activity_showimg);
        ImageLoader.getInstance().displayImage(img_url, mPhotoView);
    }
}
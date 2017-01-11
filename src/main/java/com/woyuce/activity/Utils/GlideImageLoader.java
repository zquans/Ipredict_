package com.woyuce.activity.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.woyuce.activity.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by jingbin on 2016/11/30.
 * 首页轮播图
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object url, ImageView imageView) {
        Glide.with(context).load(url)
                .placeholder(R.mipmap.img_error_horizon)
                .error(R.mipmap.img_error_horizon)
                .crossFade(1500)
                .into(imageView);
    }
}

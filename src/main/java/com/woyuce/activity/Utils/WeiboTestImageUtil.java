package com.woyuce.activity.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * @author LeBang
 * @Description:该方法仿极客学院中封装,从相册/从相机获取照片
 * @date 2016-9-30
 */
public class WeiboTestImageUtil {

    private Context context;
    public static final int REQUEST_CODE_FROM_CAMERA = 5501;
    public static final int REQUEST_CODE_FROM_ALBUM = 5502;

    public static Uri ImageUriFromCamera;

    public WeiboTestImageUtil(Context context) {
        this.context = context;
    }

    /**
     * 创建一条图片Uri，用于保存拍照后的照片
     *
     * @param context
     * @return
     */

    public static Uri createImageUri(Context context) {
        String name = "weiboimg" + System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }

    /**
     * 打开相机，选取相片
     *
     * @param activity
     */
    public static void pickImagefromCamera(final Activity activity) {
        ImageUriFromCamera = createImageUri(activity);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUriFromCamera);
        activity.startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
    }

    /**
     * 打开本地相册，选取照片
     *
     * @param activity
     */
    public static void pickImagefromAlbum(final Activity activity) {
        ImageUriFromCamera = createImageUri(activity);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
    }
}

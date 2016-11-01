//package com.woyuce.activity.Activity;
//
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.GridView;
//
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.woyuce.activity.Adapter.WeiboPhotoWallAdapter;
//import com.woyuce.activity.R;
//import com.woyuce.activity.Utils.ToastUtil;
//import com.woyuce.activity.Utils.WeiboTestImageUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import uk.co.senab.photoview.PhotoView;
//
///**
// * Created by Administrator on 2016/9/27.
// */
//public class WeiboPhotoWallActivity extends Activity implements AdapterView.OnItemClickListener {
//
//    private PhotoView mImg;
//
//    private GridView mGridView;
//    private WeiboPhotoWallAdapter mAdapter;
//    private List<String> mList = new ArrayList<>();
//
//    private Uri local_image_Uri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_weibophotowall);
//
//        initView();
//    }
//
//    /**
//     * 初始化布局
//     */
//    private void initView() {
//        mImg = (PhotoView) findViewById(R.id.img_weibophotowall);
//        mGridView = (GridView) findViewById(R.id.gridview_activity_weibophotowall);
//        mGridView.setOnItemClickListener(this);
//
//        getImages(this);
//        mAdapter = new WeiboPhotoWallAdapter(this, mList);
//        mGridView.setAdapter(mAdapter);
//    }
//
//    /**
//     * 获取本地相册List
//     *
//     * @param context
//     * @return
//     */
//    public List<String> getImages(Context context) {
//        ContentResolver contentResolver = context.getContentResolver();
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,};
//        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " desc";
//        Cursor cursor = contentResolver.query(uri, projection, null, null, sortOrder);
//        int iId = cursor.getColumnIndex(MediaStore.Images.Media._ID);
//        int iPath = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            String id = cursor.getString(iId);
//            String path = cursor.getString(iPath);
////            ImageModel imageModel = new ImageModel(id,path);
//            mList.add(path);
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return mList;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        if (resultCode != RESULT_OK)
////            return;
//        switch (requestCode) {
//            case WeiboTestImageUtil.REQUEST_CODE_FROM_CAMERA:
//                local_image_Uri = WeiboTestImageUtil.ImageUriFromCamera;
//                //将PhotoView设为可见
//                mImg.setVisibility(View.VISIBLE);
//                // 将图片显示在PhotoView里
////                mImg.setImageURI(local_image_Uri);
//                DisplayImageOptions options = new DisplayImageOptions.Builder()
//                        .cacheInMemory(true).cacheOnDisk(true)
//                        .showImageOnLoading(R.mipmap.img_error)
//                        .showImageOnFail(R.mipmap.img_error)
//                        .bitmapConfig(Bitmap.Config.ARGB_8888)
//                        .displayer(new RoundedBitmapDisplayer(5))
//                        .build();
//                ImageLoader.getInstance().displayImage(String.valueOf(local_image_Uri), mImg, options);
//                break;
//            case WeiboTestImageUtil.REQUEST_CODE_FROM_ALBUM:
//                local_image_Uri = data.getData();
//                mImg.setVisibility(View.VISIBLE);
//                // 将图片显示在PhotoView里
//                mImg.setImageURI(local_image_Uri);
//                break;
//        }
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        switch (position) {
//            case 0:
//                //ToastUtil.showMessage(this, "去拍照获取图片");
//                WeiboTestImageUtil.pickImagefromCamera(this);
//                break;
//            default:
//                //ToastUtil.showMessage(this, "去相册获取图片");
//                ToastUtil.showMessage(this, "点右上角选中该图片，点其他部位，跳转看详细图");
//                WeiboTestImageUtil.pickImagefromAlbum(this);
//                break;
//        }
//    }
//
//    public void toNext(View view) {
//        Intent intent = new Intent(this, WeiboPulishActivity2.class);
//        intent.setData(local_image_Uri);
//        startActivity(intent);
//        mImg.setVisibility(View.INVISIBLE);
//    }
//}

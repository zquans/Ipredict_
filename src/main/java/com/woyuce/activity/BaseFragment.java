package com.woyuce.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2016/12/16.
 */
public class BaseFragment extends Fragment {

    private ProgressDialog progressdialog;

    public void progressdialogshow(Context context) {
        if (progressdialog == null) {
            progressdialog = new ProgressDialog(context);
        }
        progressdialog.setTitle("加载中，请稍候");
        progressdialog.setMessage("Loading...");
        progressdialog.setCanceledOnTouchOutside(false);
//         progressdialog.setCancelable(false);
        progressdialog.show();
    }

    public void progressdialogcancel() {
        progressdialog.cancel();
    }
}
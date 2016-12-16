package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;

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
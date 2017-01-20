package com.woyuce.activity.UI.Activity.Common;

import android.os.Bundle;
import android.view.View;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.UI.Fragment.Main.Fragmentfour;
import com.woyuce.activity.R;

/**
 * Created by Administrator on 2016/11/25.
 */
public class CustomServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_emptyfragment);


        getFragmentManager().beginTransaction().replace(R.id.ll_activity_empty, new Fragmentfour()).commit();
    }

    public void back(View view) {
        finish();
    }
}
package com.woyuce.activity.Controller.Mine;

import android.os.Bundle;
import android.view.View;

import com.woyuce.activity.BaseActivity;
import com.woyuce.activity.Controller.Main.FragmentStore;
import com.woyuce.activity.R;

/**
 * Created by Administrator on 2016/11/25
 */
public class CustomServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_empty_fragment);


        getFragmentManager().beginTransaction().replace(R.id.ll_activity_empty, new FragmentStore()).commit();
    }

    public void back(View view) {
        finish();
    }
}
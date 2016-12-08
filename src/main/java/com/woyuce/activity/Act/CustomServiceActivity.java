package com.woyuce.activity.Act;

import android.app.Activity;
import android.os.Bundle;

import com.woyuce.activity.Fragment.Fragmentfour;
import com.woyuce.activity.R;

/**
 * Created by Administrator on 2016/11/25.
 */
public class CustomServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_emptyfragment);


//        getFragmentManager().beginTransaction().replace(R.id.ll_activity_empty,new Fragmentfour()).commit();
    }
}
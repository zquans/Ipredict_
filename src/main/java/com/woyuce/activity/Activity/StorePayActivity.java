package com.woyuce.activity.Activity;

import android.os.Bundle;
import android.view.View;

import com.woyuce.activity.R;
import com.woyuce.activity.Utils.ToastUtil;

/**
 * Created by Administrator on 2016/11/7.
 */
public class StorePayActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storepay);
    }

    public void nowPay(View view) {
        ToastUtil.showMessage(this, "去调支付吧");
    }

}

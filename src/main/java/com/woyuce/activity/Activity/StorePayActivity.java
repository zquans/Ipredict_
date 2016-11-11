package com.woyuce.activity.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

        initView();
    }

    private void initView() {
        TextView mTxtPrice = (TextView) this.findViewById(R.id.txt_storecar_final_price);
        mTxtPrice.setText(getIntent().getDoubleExtra("goods_price", -1.00) + "元");
    }

    public void nowPay(View view) {
        ToastUtil.showMessage(this, "去调支付吧,你要付这么多钱" + getIntent().getDoubleExtra("goods_price", -1.00) + "元");
    }
}
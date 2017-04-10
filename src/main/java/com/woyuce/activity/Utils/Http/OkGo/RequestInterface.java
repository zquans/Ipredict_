package com.woyuce.activity.Utils.Http.OkGo;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2017/2/16
 */
public interface RequestInterface {

    void doSuccess(String result, Call call, Response response);

}
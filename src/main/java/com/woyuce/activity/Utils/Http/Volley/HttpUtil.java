package com.woyuce.activity.Utils.Http.Volley;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.woyuce.activity.AppContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LeBang on 2017/2/16
 */
public class HttpUtil {

    public static void removeTag(String tag) {
        AppContext.getHttpQueue().cancelAll(tag);
    }

    /**
     * Get请求
     */
    public static void get(String url, String tag, final RequestInterface requestInterface) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestInterface.doSuccess(response);
            }
        }, null);
        stringRequest.setTag(tag);
        AppContext.getHttpQueue().add(stringRequest);
    }

    /**
     * Get请求
     */
    public static void get(String url, String tag, final HashMap<String, String> headers, final RequestInterface requestInterface) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestInterface.doSuccess(response);
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        stringRequest.setTag(tag);
        AppContext.getHttpQueue().add(stringRequest);
    }


    /**
     * Post请求
     */
    public static void post(String url, final HashMap<String, String> headers, final HashMap<String, String> params, String tag, final RequestInterface requestInterface) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestInterface.doSuccess(response);
            }
        }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setTag(tag);
        AppContext.getHttpQueue().add(stringRequest);
    }

    /**
     * Post请求
     */
    public static void post(String url, final HashMap<String, String> param, String tag, final RequestInterface requestInterface) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestInterface.doSuccess(response);
            }
        }, null) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };
        stringRequest.setTag(tag);
        AppContext.getHttpQueue().add(stringRequest);
    }
}
package com.woyuce.activity.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class PreferenceUtil {
    public static SharedPreferences getSharePre(Context paramContext) {
        return PreferenceManager.getDefaultSharedPreferences(paramContext);
    }

    public static void save(Context paramContext, String paramString, int paramInt) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.putInt(paramString, paramInt);
        localEditor.commit();
    }

    public static void save(Context paramContext, String paramString, long paramLong) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.putLong(paramString, paramLong);
        localEditor.commit();
    }

    public static void save(Context paramContext, String paramString, Float paramFloat) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.putFloat(paramString, paramFloat.floatValue());
        localEditor.commit();
    }

    public static void save(Context paramContext, String paramString1, String paramString2) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.putString(paramString1, paramString2);
        localEditor.commit();
    }

    public static void save(Context paramContext, String paramString, boolean paramBoolean) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.putBoolean(paramString, paramBoolean);
        localEditor.commit();
    }

    public static void save(Context paramContext, String paramString, float paramFloat) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.putFloat(paramString, paramFloat);
        localEditor.commit();
    }

    public static void save(Context paramContext, String paramString, Set<String> paramSet) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.putStringSet(paramString, paramSet);
        localEditor.commit();
    }

    public static void clear(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.clear().commit();
    }

    public static void removeall(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("username");
        localEditor.remove("password");
        localEditor.remove("mUserName");
        localEditor.remove("money");
        localEditor.remove("token");
        localEditor.remove("update");
        localEditor.remove("userId");
        localEditor.remove("mtimer");
//		localEditor.remove("localtoken");  //author2
        localEditor.commit();
    }

    public static void removeusername(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("username");
        localEditor.commit();
    }

    public static void removepassword(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("password");
        localEditor.commit();
    }

    public static void removemoney(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("money");
        localEditor.commit();
    }

    public static void removetoken(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("token");
        localEditor.commit();
    }

    public static void removeupdate(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("update");
        localEditor.commit();
    }

    public static void removeuserId(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("userId");
        localEditor.commit();
    }

    public static void removelocaltoken(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("localtoken");
        localEditor.commit();
    }

    public static void removefirstguide(Context paramContext) {
        SharedPreferences.Editor localEditor = getSharePre(paramContext).edit();
        localEditor.remove("welcome");
        localEditor.remove("imgspellguide");
        localEditor.remove("imgclearguide");
        localEditor.remove("imgclearguide_wangluobanlesson");
        localEditor.commit();
    }
}
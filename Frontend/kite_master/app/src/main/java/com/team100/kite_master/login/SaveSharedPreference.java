package com.team100.kite_master.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference
{
    private static final String PREF_USER_NAME= "username";
    private static final String HOST_IP= "hostip";
    private static final String PWD= "pass";
    private static final String FAVORITES_LIST = "favoriteslist";

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.apply();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }


    public static void setPass(Context ctx, String p)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PWD, p);
        editor.apply();
    }

    public static String getPass(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PWD, "");
    }

    public static void setHostIp(Context ctx, String ip)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(HOST_IP, ip);
        editor.apply();
    }

    public static String getHostIp(Context ctx)
    {
        return getSharedPreferences(ctx).getString(HOST_IP, "");
    }


    public static void setFavoritesList(Context ctx, String favlist)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(FAVORITES_LIST, favlist);
        editor.apply();
    }

    public static String getFavoritesList(Context ctx)
    {
        return getSharedPreferences(ctx).getString(FAVORITES_LIST, "");
    }
}
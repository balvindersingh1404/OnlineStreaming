package com.headsupseven.corp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PersistentUser {

    private static final String PREFS_FILE_NAME = "Headsup7AppPreferences";
    private static final String PUSHKEY = "push_key";
    private static final String USERNAME = "username";
    private static final String USERDETAILS = "userdetails";
    private static final String PASSWORD = "password";
    private static final String SOCIALLOGINEMAIL = "social_logn_email";


    public static String getSocialLoginEmal(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.SOCIALLOGINEMAIL, "");
    }

    public static void setSocialLoginEmal(final Context ctx, final String logindata) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.SOCIALLOGINEMAIL, logindata);
        editor.commit();
    }
    public static String GetPushkey(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.PUSHKEY, "");
    }

    public static void SetPushkey(final Context ctx, final String logindata) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.PUSHKEY, logindata);
        editor.commit();
    }

    public static String getUserDetails(final Context ctx) {
        return ctx.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .getString(USERDETAILS, "");
    }

    public static void setUserDetails(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(USERDETAILS, data);
        editor.commit();
    }

    public static void logOut(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putBoolean("LOGIN", false).commit();
    }

    public static void setLogin(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putBoolean("LOGIN", true).commit();
    }

    public static boolean isLogged(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getBoolean("LOGIN", false);
    }

    public static void resetAllData(Context c) {
        setUserDetails(c, "");
    }

    public static String getUserName(final Context ctx) {
        return ctx.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .getString(USERNAME, "");
    }

    public static void setUserName(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(USERNAME, data);
        editor.commit();
    }

    public static void setPassword(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PASSWORD, data);
        editor.commit();
    }

    public static String getPassword(final Context ctx) {
        return ctx.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .getString(PASSWORD, "");
    }
    public static void setSlider(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putBoolean("SLIDER", true).commit();
    }

    public static boolean isSlider(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getBoolean("SLIDER", false);
    }
}

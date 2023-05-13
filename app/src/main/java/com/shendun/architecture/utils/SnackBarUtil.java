package com.shendun.architecture.utils;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shendun.renter.R;
import com.shendun.snackbar.Snackbar;

public class SnackBarUtil {
    public static void showSnack(Activity activity, String content, int duration) {
        if (TextUtils.isEmpty(content)) {
            content = activity.getString(R.string.exception_default_tip);
        }
        View rootView = ((ViewGroup) activity.getWindow()
            .getDecorView()
            .findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(rootView, content, duration);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(R.color.theme_blue);
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showLongSnack(Activity activity, String content) {
        showSnack(activity, content, Snackbar.LENGTH_LONG);
    }

    public static void showShortSnack(Activity activity, String content) {
        showSnack(activity, content, Snackbar.LENGTH_SHORT);
    }

    public static void showSnackDelay(Activity activity, String content, int duration,
        Snackbar.Callback callback) {
        View rootView = ((ViewGroup) activity.getWindow()
            .getDecorView()
            .findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(rootView, content, duration);
        snackbar.setCallback(callback);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(R.color.theme_blue);
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showLongSnackDelay(Activity activity, String content,
        Snackbar.Callback callback) {
        showSnackDelay(activity, content, Snackbar.LENGTH_LONG, callback);
    }

    public static void showShortSnackDelay(Activity activity, String content,
        Snackbar.Callback callback) {
        showSnackDelay(activity, content, Snackbar.LENGTH_SHORT, callback);
    }
}

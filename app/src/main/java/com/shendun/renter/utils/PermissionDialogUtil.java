package com.shendun.renter.utils;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import androidx.appcompat.app.AlertDialog;

import com.shendun.renter.R;

public class PermissionDialogUtil {

    public static void showDialog(Context context, int title, int msg, int positiveBtn,
                                  OnClickListener posListener, OnClickListener negListener) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
            .setMessage(msg)
            .setPositiveButton(positiveBtn, posListener)
            .setNegativeButton(R.string.cancel, negListener)
            .create();
        dialog.show();
    }
}

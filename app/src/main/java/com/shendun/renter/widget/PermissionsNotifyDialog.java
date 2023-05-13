package com.shendun.renter.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.permissionx.guolindev.dialog.RationaleDialog;
import com.shendun.renter.R;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PermissionsNotifyDialog extends RationaleDialog {

    private View.OnClickListener onConfirmListener;
    private final Context context;
    private final String permission;
    @DrawableRes
    private final  int iconResId;
    private final String permissionTitle;
    private final List<String> permissionContentList;

    public PermissionsNotifyDialog(Context context, String permission,@DrawableRes int iconResId, String permissionTitle,
        List<String> permissionContentList) {
        super(context, R.style.PermissionDialogStyle);
        this.context = context;
        this.permission = permission;
        this.iconResId=iconResId;
        this.permissionTitle = permissionTitle;
        this.permissionContentList = permissionContentList;
    }

    @NonNull
    @NotNull
    @Override
    public View getPositiveButton() {
        return findViewById(R.id.btn_confirm);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View getNegativeButton() {
        return null;
    }

    @NonNull
    @NotNull
    @Override
    public List<String> getPermissionsToRequest() {
        List<String> list=new ArrayList<>();
        list.add(permission);
        return list;
    }

    public void setOnClickListener(View.OnClickListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_permissions_notify);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ImageView iconIv= findViewById(R.id.icon_permission);
        if(iconResId!=-1){
            iconIv.setImageResource(iconResId);
        }
        TextView titleTv= findViewById(R.id.tv_permission);
        if(!TextUtils.isEmpty(permissionTitle)){
           titleTv.setText(permissionTitle);
        }
        RecyclerView recyclerView = findViewById(R.id.rv_permission_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new PermissionContentListAdapter(context, permissionContentList));
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != onConfirmListener) {
                    onConfirmListener.onClick(v);
                }
            }
        });
    }
}


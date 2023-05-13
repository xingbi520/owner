package com.shendun.renter.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shendun.renter.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PermissionContentListAdapter
    extends RecyclerView.Adapter<PermissionContentListAdapter.VH> {

    private final List<String> contentList;
    private final Context context;

    public PermissionContentListAdapter(@NonNull Context context,
        @NonNull List<String> contentList) {
        this.context = context;
        this.contentList = contentList;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(context).inflate(R.layout.item_permission_content, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PermissionContentListAdapter.VH holder,
        int position) {
        String content = contentList.get(position);
        holder.contentTv.setText(TextUtils.isEmpty(content) ? "" : content);
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final TextView contentTv;

        public VH(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.content);
        }
    }
}
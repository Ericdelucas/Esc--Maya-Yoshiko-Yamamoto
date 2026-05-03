package com.example.testbackend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.R;
import com.example.testbackend.models.ReportAttachment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAttachmentAdapter extends RecyclerView.Adapter<ReportAttachmentAdapter.ViewHolder> {
    
    private List<ReportAttachment> attachments;
    private Context context;
    private OnAttachmentClickListener listener;
    
    public interface OnAttachmentClickListener {
        void onViewClick(ReportAttachment attachment);
        void onDeleteClick(ReportAttachment attachment);
    }
    
    public ReportAttachmentAdapter(Context context, List<ReportAttachment> attachments, OnAttachmentClickListener listener) {
        this.context = context;
        this.attachments = attachments;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report_attachment, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportAttachment attachment = attachments.get(position);
        
        holder.tvFileName.setText(attachment.getFileName());
        
        if (attachment.getFileSize() != null) {
            holder.tvFileSize.setText(formatFileSize(attachment.getFileSize()));
        }
        
        holder.tvUploadDate.setText(attachment.getUploadedAt());
        
        if (attachment.getDescription() != null && !attachment.getDescription().isEmpty()) {
            holder.tvDescription.setText(attachment.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        
        if ("image".equals(attachment.getAttachmentType())) {
            String imageUrl = "https://esc-maya-yoshiko-yamamoto.onrender.com/reports/" + attachment.getReportId() + 
                             "/attachments/" + attachment.getId() + "/download";
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.imgPreview);
        }
        
        holder.itemView.setOnClickListener(v -> listener.onViewClick(attachment));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(attachment));
    }
    
    @Override
    public int getItemCount() {
        return attachments.size();
    }
    
    public void updateAttachments(List<ReportAttachment> newAttachments) {
        this.attachments = newAttachments;
        notifyDataSetChanged();
    }
    
    private String formatFileSize(int bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPreview;
        TextView tvFileName, tvFileSize, tvUploadDate, tvDescription;
        ImageButton btnDelete;
        
        ViewHolder(View view) {
            super(view);
            imgPreview = view.findViewById(R.id.imgPreview);
            tvFileName = view.findViewById(R.id.tvFileName);
            tvFileSize = view.findViewById(R.id.tvFileSize);
            tvUploadDate = view.findViewById(R.id.tvUploadDate);
            tvDescription = view.findViewById(R.id.tvDescription);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}

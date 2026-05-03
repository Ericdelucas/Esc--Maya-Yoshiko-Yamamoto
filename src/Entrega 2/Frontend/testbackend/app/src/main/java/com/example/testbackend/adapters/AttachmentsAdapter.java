package com.example.testbackend.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.ViewHolder> {
    
    private List<Uri> images;
    private OnImageRemoveListener removeListener;
    
    public interface OnImageRemoveListener {
        void onRemove(int position);
    }
    
    public AttachmentsAdapter(List<Uri> images, OnImageRemoveListener removeListener) {
        this.images = images;
        this.removeListener = removeListener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_attachment_image, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = images.get(position);
        
        // Carregar imagem com Picasso (já que Glide não foi encontrado nas dependências diretas)
        Picasso.get()
            .load(imageUri)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.imageView);
        
        // Remover imagem
        holder.btnRemove.setOnClickListener(v -> {
            removeListener.onRemove(holder.getAdapterPosition());
        });
    }
    
    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnRemove;
        
        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_attachment);
            btnRemove = view.findViewById(R.id.btn_remove);
        }
    }
}

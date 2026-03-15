package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.R;
import com.example.testbackend.models.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (message.getType() == ChatMessage.TYPE_USER) {
            holder.cardUser.setVisibility(View.VISIBLE);
            holder.cardAssistant.setVisibility(View.GONE);
            holder.tvUserMessage.setText(message.getText());
        } else {
            holder.cardUser.setVisibility(View.GONE);
            holder.cardAssistant.setVisibility(View.VISIBLE);
            holder.tvAssistantMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        View cardUser, cardAssistant;
        TextView tvUserMessage, tvAssistantMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            cardUser = itemView.findViewById(R.id.cardUser);
            cardAssistant = itemView.findViewById(R.id.cardAssistant);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvAssistantMessage = itemView.findViewById(R.id.tvAssistantMessage);
        }
    }
}
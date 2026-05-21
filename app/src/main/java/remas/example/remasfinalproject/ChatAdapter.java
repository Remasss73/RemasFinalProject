package remas.example.remasfinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying chat messages in a RecyclerView.
 * Handles both sent and received messages with proper styling and timestamps.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    
    private List<ChatMessage> messages;
    private String lastDisplayedDate = "";
    private String currentUserId;
    
    /**
     * Constructor for ChatAdapter.
     */
    public ChatAdapter() {
        this.messages = new ArrayList<>();
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null 
            ? FirebaseAuth.getInstance().getCurrentUser().getUid() 
            : "";
    }
    
    /**
     * Add a new message to the chat.
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
    
    /**
     * Update the last message (useful for typing indicators).
     */
    public void updateLastMessage(ChatMessage message) {
        if (!messages.isEmpty()) {
            int position = messages.size() - 1;
            messages.set(position, message);
            notifyItemChanged(position);
        }
    }
    
    /**
     * Clear all messages.
     */
    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
        lastDisplayedDate = "";
    }
    
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        return new ChatViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        
        // Set message text
        holder.tvMessageText.setText(message.getMessageText());
        
        // Set timestamp
        holder.tvMessageTime.setText(message.getFormattedTime());
        
        // Handle date separator
        String currentDate = message.getFormattedDate();
        if (!currentDate.equals(lastDisplayedDate)) {
            holder.tvDateSeparator.setText(message.isToday() ? "Today" : currentDate);
            holder.tvDateSeparator.setVisibility(View.VISIBLE);
            lastDisplayedDate = currentDate;
        } else {
            holder.tvDateSeparator.setVisibility(View.GONE);
        }
        
        // Style based on message type (sent vs received)
        if (message.getMessageType() == ChatMessage.MESSAGE_TYPE_SENT) {
            // Sent message styling
            holder.messageContainer.setGravity(View.TEXT_ALIGNMENT_VIEW_END);
            holder.messageBubble.setBackgroundResource(R.drawable.message_bubble_sent);
            holder.ivProfilePic.setVisibility(View.GONE);
            holder.tvSenderName.setVisibility(View.GONE);
            holder.ivMessageStatus.setVisibility(View.VISIBLE);
        } else {
            // Received message styling
            holder.messageContainer.setGravity(View.TEXT_ALIGNMENT_VIEW_START);
            holder.messageBubble.setBackgroundResource(R.drawable.message_bubble_received);
            holder.ivProfilePic.setVisibility(View.VISIBLE);
            holder.tvSenderName.setVisibility(View.VISIBLE);
            holder.tvSenderName.setText(message.getSenderName());
            holder.ivMessageStatus.setVisibility(View.GONE);
            
            // Load profile picture if available
            if (message.getProfilePictureUrl() != null && !message.getProfilePictureUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                    .load(message.getProfilePictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(holder.ivProfilePic);
            } else {
                // Load default profile picture
                holder.ivProfilePic.setImageResource(android.R.drawable.ic_menu_myplaces);
            }
        }
        
        // Handle typing indicator
        if (message.isTyping()) {
            holder.tvMessageText.setText("Typing...");
        }
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    /**
     * ViewHolder class for chat message items.
     */
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout messageContainer;
        LinearLayout messageBubble;
        ImageView ivProfilePic;
        TextView tvSenderName;
        TextView tvMessageText;
        TextView tvMessageTime;
        TextView tvDateSeparator;
        ImageView ivMessageStatus;
        LinearLayout fileAttachmentContainer;
        ImageView ivFileIcon;
        TextView tvFileName;
        TextView tvFileSize;
        ImageView ivDownloadFile;
        ImageView ivImageAttachment;
        
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            messageBubble = itemView.findViewById(R.id.messageBubble);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            tvDateSeparator = itemView.findViewById(R.id.tvDateSeparator);
            ivMessageStatus = itemView.findViewById(R.id.ivMessageStatus);
            fileAttachmentContainer = itemView.findViewById(R.id.fileAttachmentContainer);
            ivFileIcon = itemView.findViewById(R.id.ivFileIcon);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            ivDownloadFile = itemView.findViewById(R.id.ivDownloadFile);
            ivImageAttachment = itemView.findViewById(R.id.ivImageAttachment);
        }
    }
}

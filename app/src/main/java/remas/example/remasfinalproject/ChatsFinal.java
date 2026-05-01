package remas.example.remasfinalproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatsFinal extends AppCompatActivity {
    
    // UI Components
    private ImageView ivBack, ibSearch, ibFilter, ibCloseAI;
    private TextView tvTitle, tvArchiveCount;
    private LinearLayout archiveSection, emptyState;
    private RecyclerView rvChats;
    private Button btnStartChat;
    
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    // Data
    private ChatAdapter chatAdapter;
    private List<ChatItem> chatList;
    private List<ChatItem> archivedChatList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_final);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        // Initialize UI
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadChats();
        simulateRealTimeUpdates();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        ibSearch = findViewById(R.id.ibSearch);
        ibFilter = findViewById(R.id.ibFilter);
        ibCloseAI = findViewById(R.id.ibCloseAI);
        tvTitle = findViewById(R.id.tvTitle);
        tvArchiveCount = findViewById(R.id.tvArchiveCount);
        archiveSection = findViewById(R.id.archiveSection);
        emptyState = findViewById(R.id.emptyState);
        rvChats = findViewById(R.id.rvChats);
        btnStartChat = findViewById(R.id.btnStartChat);
    }
    
    private void setupRecyclerView() {
        chatList = new ArrayList<>();
        archivedChatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.setAdapter(chatAdapter);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
        
        ibFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        
        ibCloseAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAIBanner();
            }
        });
        
        archiveSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleArchiveView();
            }
        });
        
        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatsFinal.this, AddDormActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
    
    private void loadChats() {
        if (mAuth.getCurrentUser() == null) {
            showEmptyState();
            return;
        }
        
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("chats").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                archivedChatList.clear();
                
                // Always add AI Assistant as pinned chat
                ChatItem aiAssistant = new ChatItem();
                aiAssistant.setChatId("ai_assistant");
                aiAssistant.setUserId("ai_bot");
                aiAssistant.setUserName("LUXE STAY AI");
                aiAssistant.setLastMessage("Hello! I am here to help you find the perfect property. Ask me anything!");
                aiAssistant.setTimestamp(System.currentTimeMillis());
                aiAssistant.setUnreadCount(0);
                aiAssistant.setArchived(false);
                aiAssistant.setVerified(true);
                aiAssistant.setOnline(true);
                chatList.add(0, aiAssistant);
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatItem chat = snapshot.getValue(ChatItem.class);
                    if (chat != null && !chat.getChatId().equals("ai_assistant")) {
                        chat.setChatId(snapshot.getKey());
                        if (chat.isArchived()) {
                            archivedChatList.add(chat);
                        } else {
                            chatList.add(chat);
                        }
                    }
                }
                
                updateUI();
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatsFinal.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateUI() {
        updateArchiveCount();
        chatAdapter.notifyDataSetChanged();
        
        // Check if there are any real user chats (excluding AI assistant)
        boolean hasUserChats = chatList.size() > 1 || !archivedChatList.isEmpty();
        
        if (!hasUserChats) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }
    
    private void updateArchiveCount() {
        int archiveCount = archivedChatList.size();
        tvArchiveCount.setText("Archived Chats (" + archiveCount + ")");
    }
    
    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        rvChats.setVisibility(View.GONE);
    }
    
    private void hideEmptyState() {
        emptyState.setVisibility(View.GONE);
        rvChats.setVisibility(View.VISIBLE);
    }
    
    private boolean isShowingArchived = false;
    
    private void toggleArchiveView() {
        isShowingArchived = !isShowingArchived;
        
        if (isShowingArchived) {
            // Show archived chats
            chatAdapter.updateList(archivedChatList);
            tvTitle.setText("Archived Chats");
        } else {
            // Show active chats
            chatAdapter.updateList(chatList);
            tvTitle.setText("Messages");
        }
    }
    
    private void simulateRealTimeUpdates() {
        // Simulate typing indicators and online status changes
        Handler handler = new Handler();
        Random random = new Random();
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Simulate someone coming online
                if (chatAdapter != null && chatAdapter.getItemCount() > 0) {
                    int position = random.nextInt(chatAdapter.getItemCount());
                    ChatItem chat = chatAdapter.currentList.get(position);
                    chat.setOnline(true);
                    chat.setTyping(random.nextBoolean());
                    chatAdapter.notifyItemChanged(position);
                }
                
                // Repeat every 5 seconds
                handler.postDelayed(this, 5000);
            }
        }, 2000);
    }
    
    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Messages");
        
        final TextView searchInput = new TextView(this);
        searchInput.setText("Type to search...");
        searchInput.setPadding(50, 30, 50, 30);
        
        builder.setView(searchInput);
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String query = searchInput.getText().toString();
                if (!query.isEmpty()) {
                    filterChats(query);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showFilterDialog() {
        String[] filterOptions = {"All Messages", "Unread Only", "Property Owners Only", "AI Assistants Only"};
        
        new AlertDialog.Builder(this)
                .setTitle("Filter Chats")
                .setItems(filterOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                filterChats("all");
                                break;
                            case 1:
                                filterChats("unread");
                                break;
                            case 2:
                                filterChats("owners");
                                break;
                            case 3:
                                filterChats("ai");
                                break;
                        }
                    }
                })
                .show();
    }
    
    private void filterChats(String filter) {
        Toast.makeText(this, "Filter: " + filter, Toast.LENGTH_SHORT).show();
        // TODO: Implement actual filtering logic
    }
    
    private void hideAIBanner() {
        // TODO: Hide AI customer service banner
        Toast.makeText(this, "AI Assistant hidden", Toast.LENGTH_SHORT).show();
    }
    
    private void openChat(ChatItem chat) {
        // Create a more realistic chat opening experience
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("chatId", chat.getChatId());
        intent.putExtra("userName", chat.getUserName());
        intent.putExtra("userId", chat.getUserId());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    private void showChatOptions(ChatItem chat) {
        String[] options = {"Open Chat", "View Profile", "Mark as Read", "Mute Notifications", "Archive", "Delete Chat", "Block User", "Report", "Cancel"};
        
        new AlertDialog.Builder(this)
                .setTitle("Chat Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openChat(chat);
                                break;
                            case 1:
                                viewUserProfile(chat);
                                break;
                            case 2:
                                markAsRead(chat);
                                break;
                            case 3:
                                muteNotifications(chat);
                                break;
                            case 4:
                                archiveChat(chat);
                                break;
                            case 5:
                                deleteChat(chat);
                                break;
                            case 6:
                                blockUser(chat);
                                break;
                            case 7:
                                reportUser(chat);
                                break;
                            case 8:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }
    
    private void viewUserProfile(ChatItem chat) {
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("userId", chat.getUserId());
        startActivity(intent);
    }
    
    private void markAsRead(ChatItem chat) {
        chat.setUnreadCount(0);
        saveChatToFirebase(chat);
        Toast.makeText(this, "Marked as read", Toast.LENGTH_SHORT).show();
    }
    
    private void muteNotifications(ChatItem chat) {
        chat.setMuted(true);
        saveChatToFirebase(chat);
        Toast.makeText(this, "Notifications muted", Toast.LENGTH_SHORT).show();
    }
    
    private void reportUser(ChatItem chat) {
        new AlertDialog.Builder(this)
                .setTitle("Report User")
                .setMessage("Report " + chat.getUserName() + " for inappropriate behavior")
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Implement reporting system
                        Toast.makeText(ChatsFinal.this, "User reported", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void deleteChat(ChatItem chat) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Chat")
                .setMessage("Are you sure you want to delete this chat? This action cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mAuth.getCurrentUser() != null) {
                            mDatabase.child("chats")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(chat.getChatId())
                                    .removeValue();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void blockUser(ChatItem chat) {
        new AlertDialog.Builder(this)
                .setTitle("Block User")
                .setMessage("Are you sure you want to block " + chat.getUserName() + "? You won't receive messages from this user.")
                .setPositiveButton("Block", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mAuth.getCurrentUser() != null) {
                            // Add to blocked users list
                            mDatabase.child("blockedUsers")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(chat.getUserId())
                                    .setValue(true);
                            
                            // Remove chat from active list
                            deleteChat(chat);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void archiveChat(ChatItem chat) {
        chat.setArchived(true);
        saveChatToFirebase(chat);
        Toast.makeText(this, "Chat archived", Toast.LENGTH_SHORT).show();
    }
    
    private void saveChatToFirebase(ChatItem chat) {
        if (mAuth.getCurrentUser() != null) {
            mDatabase.child("chats")
                    .child(mAuth.getCurrentUser().getUid())
                    .child(chat.getChatId())
                    .setValue(chat);
        }
    }
    
    // Enhanced Chat Adapter
    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatItem> currentList;
        
        public ChatAdapter(List<ChatItem> list) {
            this.currentList = list;
        }
        
        public void updateList(List<ChatItem> newList) {
            this.currentList = newList;
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ChatsFinal.this).inflate(R.layout.chat_item_final, parent, false);
            return new ChatViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatItem chat = currentList.get(position);
            
            // Special handling for AI Assistant
            boolean isAI = chat.getChatId().equals("ai_assistant");
            
            // Profile and status
            if (isAI) {
                holder.ivProfilePic.setImageResource(android.R.drawable.ic_lock_idle_lock);
                holder.ivProfilePic.setBackgroundColor(0xFF38BDF8);
            } else {
                holder.ivProfilePic.setImageResource(android.R.drawable.ic_menu_myplaces);
                holder.ivProfilePic.setBackgroundColor(0xFF38BDF8);
            }
            holder.onlineStatus.setVisibility(chat.isOnline() ? View.VISIBLE : View.GONE);
            
            // User info
            holder.tvUserName.setText(chat.getUserName());
            holder.ivVerified.setVisibility(chat.isVerified() ? View.VISIBLE : View.GONE);
            
            // Message and status
            holder.tvLastMessage.setText(chat.getLastMessage());
            holder.ivTyping.setVisibility(chat.isTyping() ? View.VISIBLE : View.GONE);
            
            // Time and unread
            holder.tvTime.setText(chat.getFormattedTime());
            holder.tvUnreadCount.setText(String.valueOf(chat.getUnreadCount()));
            holder.tvUnreadCount.setVisibility(chat.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
            holder.ivMessageStatus.setVisibility(chat.hasDelivered() ? View.VISIBLE : View.GONE);
            
            // Setup click listeners
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChat(chat);
                }
            });
            
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showChatOptions(chat);
                    return true;
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return currentList.size();
        }
        
        class ChatViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProfilePic, onlineStatus, ivVerified, ivTyping, ivMessageStatus;
            TextView tvUserName, tvLastMessage, tvTime, tvUnreadCount;
            
            @SuppressLint("WrongViewCast")
            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
                onlineStatus = itemView.findViewById(R.id.onlineStatus);
                ivVerified = itemView.findViewById(R.id.ivVerified);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
                ivTyping = itemView.findViewById(R.id.ivTyping);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);
                ivMessageStatus = itemView.findViewById(R.id.ivMessageStatus);
            }
        }
    }
    
    // Enhanced Chat Item Model
    public static class ChatItem {
        private String chatId;
        private String userId;
        private String userName;
        private String lastMessage;
        private long timestamp;
        private int unreadCount;
        private boolean archived;
        private boolean online;
        private boolean verified;
        private boolean typing;
        private boolean delivered;
        private boolean muted;
        
        public ChatItem() {}
        
        // Enhanced getters and setters
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public int getUnreadCount() { return unreadCount; }
        public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
        
        public boolean isArchived() { return archived; }
        public void setArchived(boolean archived) { this.archived = archived; }
        
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
        
        public boolean isVerified() { return verified; }
        public void setVerified(boolean verified) { this.verified = verified; }
        
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
        
        public boolean hasDelivered() { return delivered; }
        public void setDelivered(boolean delivered) { this.delivered = delivered; }
        
        public boolean isMuted() { return muted; }
        public void setMuted(boolean muted) { this.muted = muted; }
        
        public String getFormattedTime() {
            if (timestamp == 0) return "";
            long now = System.currentTimeMillis();
            long diff = now - timestamp;
            
            if (diff < 60000) return "Just now";
            if (diff < 3600000) return (diff / 60000) + "m ago";
            if (diff < 86400000) return (diff / 3600000) + "h ago";
            return (diff / 86400000) + "d ago";
        }
    }
}

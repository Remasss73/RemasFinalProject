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
import android.widget.EditText;
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
        
        // AI Chat Container
        LinearLayout aiChatContainer = findViewById(R.id.aiChatContainer);
        aiChatContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AI Chat Activity
                Intent intent = new Intent(ChatsFinal.this, AIChatActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
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
                
                // Sort by timestamp (most recent first)
                chatList.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                
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
        // Check if it's the AI assistant
        if (chat.getChatId().equals("ai_assistant")) {
            // Open AI Chat Activity
            Intent intent = new Intent(this, AIChatActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            // Open regular chat within ChatsFinal
            openWhatsAppChat(chat);
        }
    }
    
    private void openWhatsAppChat(ChatItem chat) {
        // Create a WhatsApp-style chat dialog or fragment
        showWhatsAppChatScreen(chat);
    }
    
    private void showWhatsAppChatScreen(ChatItem chat) {
        // Create WhatsApp-style chat interface
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        
        // Create custom view for WhatsApp chat
        View chatView = getLayoutInflater().inflate(R.layout.whatsapp_chat_dialog, null);
        
        // Initialize chat UI components
        initializeWhatsAppChatUI(chatView, chat);
        
        // Create dialog and store reference
        androidx.appcompat.app.AlertDialog dialog = builder.setView(chatView)
               .setCancelable(true)
               .create();
        
        // Store dialog reference in chat view for later access
        chatView.setTag(dialog);
        dialog.show();
               
        // Load messages for this chat
        loadChatMessages(chat.getChatId(), chatView);
    }
    
    private void initializeWhatsAppChatUI(View chatView, ChatItem chat) {
        // Header components
        ImageView ivBack = chatView.findViewById(R.id.ivBack);
        ImageView ivProfilePic = chatView.findViewById(R.id.ivProfilePic);
        TextView tvUserName = chatView.findViewById(R.id.tvUserName);
        TextView tvUserStatus = chatView.findViewById(R.id.tvUserStatus);
        ImageView ivVoiceCall = chatView.findViewById(R.id.ivVoiceCall);
        ImageView ivVideoCall = chatView.findViewById(R.id.ivVideoCall);
        ImageView ivMore = chatView.findViewById(R.id.ivMore);
        
        // Message components
        RecyclerView rvMessages = chatView.findViewById(R.id.rvMessages);
        EditText etMessage = chatView.findViewById(R.id.etMessage);
        ImageView ivSend = chatView.findViewById(R.id.ivSend);
        ImageView ivAttachment = chatView.findViewById(R.id.ivAttachment);
        ImageView ivEmoji = chatView.findViewById(R.id.ivEmoji);
        ImageView ivVoiceNote = chatView.findViewById(R.id.ivVoiceNote);
        
        // Set user info
        tvUserName.setText(chat.getUserName());
        tvUserStatus.setText("Online");
        ivProfilePic.setImageResource(android.R.drawable.ic_menu_myplaces);
        ivProfilePic.setBackgroundColor(0xFF38BDF8);
        
        // Setup RecyclerView for messages
        setupChatRecyclerView(rvMessages, chat.getChatId());
        
        // Setup click listeners
        ivBack.setOnClickListener(v -> {
            // Close the chat dialog
            androidx.appcompat.app.AlertDialog dialog = (androidx.appcompat.app.AlertDialog) 
                ((View) ivBack.getParent().getParent()).getTag();
            if (dialog != null) dialog.dismiss();
        });
        
        ivSend.setOnClickListener(v -> sendMessage(chat.getChatId(), chat.getUserId(), etMessage));
        ivVoiceCall.setOnClickListener(v -> startVoiceCall());
        ivVideoCall.setOnClickListener(v -> startVideoCall());
        ivMore.setOnClickListener(v -> showChatOptions(chat));
        ivAttachment.setOnClickListener(v -> showAttachmentOptions());
        ivVoiceNote.setOnClickListener(v -> startVoiceRecording());
        ivEmoji.setOnClickListener(v -> showEmojiPicker());
    }
    
    private void setupChatRecyclerView(RecyclerView rvMessages, String chatId) {
        List<ChatMessage> messageList = new ArrayList<>();
        ChatMessageAdapter adapter = new ChatMessageAdapter(messageList, mAuth.getCurrentUser().getUid());
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);
        
        // Store adapter reference for message loading
        rvMessages.setTag(adapter);
    }
    
    private void loadChatMessages(String chatId, View chatView) {
        // Load messages from Firebase for this specific chat
        if (mAuth.getCurrentUser() != null) {
            mDatabase.child("messages").child(chatId).orderByChild("timestamp")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<ChatMessage> messageList = new ArrayList<>();
                            
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ChatMessage message = snapshot.getValue(ChatMessage.class);
                                if (message != null) {
                                    messageList.add(message);
                                }
                            }
                            
                            // Find the RecyclerView and update adapter
                            RecyclerView rvMessages = chatView.findViewById(R.id.rvMessages);
                            if (rvMessages != null && rvMessages.getAdapter() instanceof ChatMessageAdapter) {
                                ChatMessageAdapter adapter = (ChatMessageAdapter) rvMessages.getAdapter();
                                adapter.updateMessages(messageList);
                                adapter.notifyDataSetChanged();
                                rvMessages.scrollToPosition(messageList.size() - 1);
                            }
                        }
                        
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ChatsFinal.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    
    private void sendMessage(String chatId, String userId, EditText etMessage) {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) return;
        
        // Create message
        ChatMessage message = new ChatMessage();
        message.setMessageText(messageText);
        message.setSenderId(mAuth.getCurrentUser().getUid());
        message.setReceiverId(userId);
        message.setTimestamp(System.currentTimeMillis());
        message.setChatId(chatId);
        message.setRead(false);
        
        // Save to Firebase
        String messageId = mDatabase.child("messages").push().getKey();
        mDatabase.child("messages").child(chatId).child(messageId).setValue(message);
        
        // Update chat list
        updateChatList(chatId, messageText);
        
        // Clear input
        etMessage.setText("");
        
        // Show simple notification
        Toast.makeText(this, "Message sent: " + messageText, Toast.LENGTH_SHORT).show();
    }
    
    private void updateChatList(String chatId, String lastMessage) {
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference chatRef = mDatabase.child("chats")
                    .child(mAuth.getCurrentUser().getUid())
                    .child(chatId);
            
            chatRef.child("lastMessage").setValue(lastMessage);
            chatRef.child("timestamp").setValue(System.currentTimeMillis());
            chatRef.child("unreadCount").setValue(0); // Reset for sender
        }
    }
    
    // Helper methods for WhatsApp features
    private void startVoiceCall() {
        Toast.makeText(this, "Voice call feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void startVideoCall() {
        Toast.makeText(this, "Video call feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void showAttachmentOptions() {
        String[] options = {"Document", "Camera", "Gallery", "Audio", "Location", "Contact"};
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Attach")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: attachDocument(); break;
                        case 1: openCamera(); break;
                        case 2: openGallery(); break;
                        case 3: attachAudio(); break;
                        case 4: shareLocation(); break;
                        case 5: shareContact(); break;
                    }
                })
                .show();
    }
    
    private void startVoiceRecording() {
        Toast.makeText(this, "Voice recording feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void showEmojiPicker() {
        Toast.makeText(this, "Emoji picker coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void attachDocument() { Toast.makeText(this, "Attach document", Toast.LENGTH_SHORT).show(); }
    private void openCamera() { Toast.makeText(this, "Open camera", Toast.LENGTH_SHORT).show(); }
    private void openGallery() { Toast.makeText(this, "Open gallery", Toast.LENGTH_SHORT).show(); }
    private void attachAudio() { Toast.makeText(this, "Attach audio", Toast.LENGTH_SHORT).show(); }
    private void shareLocation() { Toast.makeText(this, "Share location", Toast.LENGTH_SHORT).show(); }
    private void shareContact() { Toast.makeText(this, "Share contact", Toast.LENGTH_SHORT).show(); }
    
    // Chat message model
    public static class ChatMessage {
        private String messageText;
        private String senderId;
        private String receiverId;
        private long timestamp;
        private String chatId;
        private boolean read;
        
        public ChatMessage() {}
        
        // Getters and setters
        public String getMessageText() { return messageText; }
        public void setMessageText(String messageText) { this.messageText = messageText; }
        
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        
        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
        
        public String getFormattedTime() {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
            return sdf.format(new java.util.Date(timestamp));
        }
        
        public boolean isSentByMe(String currentUserId) {
            return senderId.equals(currentUserId);
        }
    }
    
    // Chat Message Adapter for WhatsApp-style display
    private static class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {
        private List<ChatMessage> messages;
        private String currentUserId;
        
        public ChatMessageAdapter(List<ChatMessage> messages, String currentUserId) {
            this.messages = messages;
            this.currentUserId = currentUserId;
        }
        
        public void updateMessages(List<ChatMessage> newMessages) {
            this.messages = newMessages;
        }
        
        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
            return new MessageViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            ChatMessage message = messages.get(position);
            
            boolean isSentByMe = message.isSentByMe(currentUserId);
            
            // Show/hide appropriate containers based on sender
            if (isSentByMe) {
                holder.userMessageContainer.setVisibility(View.VISIBLE);
                holder.aiMessageContainer.setVisibility(View.GONE);
                
                // Bind user message data
                holder.tvUserMessage.setText(message.getMessageText());
                holder.tvUserTime.setText(message.getFormattedTime());
                holder.ivUserMessageStatus.setVisibility(View.VISIBLE);
            } else {
                holder.userMessageContainer.setVisibility(View.GONE);
                holder.aiMessageContainer.setVisibility(View.VISIBLE);
                
                // Bind received message data
                holder.tvAIMessage.setText(message.getMessageText());
                holder.tvAITime.setText(message.getFormattedTime());
                holder.ivAIMessageStatus.setVisibility(View.GONE);
            }
        }
        
        @Override
        public int getItemCount() {
            return messages.size();
        }
        
        static class MessageViewHolder extends RecyclerView.ViewHolder {
            // User message views
            LinearLayout userMessageContainer;
            TextView tvUserMessage, tvUserTime;
            ImageView ivUserMessageStatus;
            
            // AI message views
            LinearLayout aiMessageContainer;
            TextView tvAIMessage, tvAITime;
            ImageView ivAIMessageStatus;
            
            public MessageViewHolder(View itemView) {
                super(itemView);
                // User message views
                userMessageContainer = itemView.findViewById(R.id.userMessageContainer);
                tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
                tvUserTime = itemView.findViewById(R.id.tvUserTime);
                ivUserMessageStatus = itemView.findViewById(R.id.ivUserMessageStatus);
                
                // AI message views
                aiMessageContainer = itemView.findViewById(R.id.aiMessageContainer);
                tvAIMessage = itemView.findViewById(R.id.tvAIMessage);
                tvAITime = itemView.findViewById(R.id.tvAITime);
                ivAIMessageStatus = itemView.findViewById(R.id.ivAIMessageStatus);
            }
        }
    }
    
    private void showChatOptions(ChatItem chat) {
        boolean isAI = chat.getChatId().equals("ai_assistant");
        String[] options;
        
        if (isAI) {
            options = new String[]{"Open Chat", "Clear History", "Mute Notifications", "Help", "Cancel"};
        } else {
            options = new String[]{"Open Chat", "View Profile", "Mark as Read", "Mute Notifications", "Archive", "Delete Chat", "Forward", "Block User", "Report", "Cancel"};
        }
        
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
                                if (isAI) {
                                    clearAIHistory();
                                } else {
                                    viewUserProfile(chat);
                                }
                                break;
                            case 2:
                                if (isAI) {
                                    muteNotifications(chat);
                                } else {
                                    markAsRead(chat);
                                }
                                break;
                            case 3:
                                if (isAI) {
                                    showAIHelp();
                                } else {
                                    muteNotifications(chat);
                                }
                                break;
                            case 4:
                                if (!isAI) archiveChat(chat);
                                break;
                            case 5:
                                if (!isAI) deleteChat(chat);
                                break;
                            case 6:
                                if (!isAI) forwardChat(chat);
                                break;
                            case 7:
                                if (!isAI) blockUser(chat);
                                break;
                            case 8:
                                if (!isAI) reportUser(chat);
                                break;
                            case 9:
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
    
    private void clearAIHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Clear AI Chat History")
                .setMessage("Clear all conversation history with LUXE STAY AI?")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Clear AI chat history
                        Toast.makeText(ChatsFinal.this, "AI chat history cleared", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showAIHelp() {
        new AlertDialog.Builder(this)
                .setTitle("LUXE STAY AI Help")
                .setMessage("LUXE STAY AI can help you:\n\n" +
                        "• Find perfect properties\n" +
                        "• Compare prices and features\n" +
                        "• Answer location questions\n" +
                        "• Help with applications\n" +
                        "• Search for amenities\n\n" +
                        "Just ask anything about properties!")
                .setPositiveButton("Got it", null)
                .show();
    }
    
    private void forwardChat(ChatItem chat) {
        Toast.makeText(this, "Forward feature coming soon!", Toast.LENGTH_SHORT).show();
        // TODO: Implement forward functionality
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
            
            // Profile and status - Use email icon for AI
            if (isAI) {
                holder.ivProfilePic.setImageResource(android.R.drawable.ic_dialog_email);
                holder.ivProfilePic.setBackgroundColor(0xFF38BDF8);
                holder.onlineStatus.setVisibility(View.VISIBLE); // Always show AI as online
            } else {
                holder.ivProfilePic.setImageResource(android.R.drawable.ic_menu_myplaces);
                holder.ivProfilePic.setBackgroundColor(0xFF38BDF8);
                holder.onlineStatus.setVisibility(chat.isOnline() ? View.VISIBLE : View.GONE);
            }
            
            // User info
            holder.tvUserName.setText(chat.getUserName());
            holder.ivVerified.setVisibility(chat.isVerified() ? View.VISIBLE : View.GONE);
            
            // Make AI assistant more prominent
            if (isAI) {
                holder.tvUserName.setTextColor(0xFF38BDF8); // Blue color for AI
                holder.ivVerified.setVisibility(View.VISIBLE); // Always show verified for AI
            } else {
                holder.tvUserName.setTextColor(0xFFFFFFFF); // White for regular users
            }
            
            // Message and status
            holder.tvLastMessage.setText(chat.getLastMessage());
            holder.ivTyping.setVisibility(chat.isTyping() ? View.VISIBLE : View.GONE);
            
            // Time and unread
            holder.tvTime.setText(chat.getFormattedTime());
            holder.tvUnreadCount.setText(String.valueOf(chat.getUnreadCount()));
            holder.tvUnreadCount.setVisibility(chat.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
            holder.ivMessageStatus.setVisibility(chat.hasDelivered() ? View.VISIBLE : View.GONE);
            
            // Voice note indicator (show for voice notes, but not for AI)
            holder.ivVoiceNote.setVisibility(chat.isVoiceNote() && !isAI ? View.VISIBLE : View.GONE);
            
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
            ImageView ivProfilePic, onlineStatus, ivVerified, ivTyping, ivMessageStatus, ivVoiceNote;
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
                ivVoiceNote = itemView.findViewById(R.id.ivVoiceNote);
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
        private boolean isVoiceNote;
        
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
        
        public boolean isVoiceNote() { return isVoiceNote; }
        public void setVoiceNote(boolean voiceNote) { isVoiceNote = voiceNote; }
        
        public String getFormattedTime() {
            if (timestamp == 0) return "";
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
            java.util.Calendar now = java.util.Calendar.getInstance();
            java.util.Calendar messageTime = java.util.Calendar.getInstance();
            messageTime.setTimeInMillis(timestamp);
            
            // Check if message is from today
            if (now.get(java.util.Calendar.DAY_OF_YEAR) == messageTime.get(java.util.Calendar.DAY_OF_YEAR) &&
                now.get(java.util.Calendar.YEAR) == messageTime.get(java.util.Calendar.YEAR)) {
                return sdf.format(messageTime.getTime());
            }
            
            // Check if message is from yesterday
            java.util.Calendar yesterday = (java.util.Calendar) now.clone();
            yesterday.add(java.util.Calendar.DAY_OF_YEAR, -1);
            if (yesterday.get(java.util.Calendar.DAY_OF_YEAR) == messageTime.get(java.util.Calendar.DAY_OF_YEAR) &&
                yesterday.get(java.util.Calendar.YEAR) == messageTime.get(java.util.Calendar.YEAR)) {
                return "Yesterday";
            }
            
            // Check if message is from this week
            if (now.get(java.util.Calendar.WEEK_OF_YEAR) == messageTime.get(java.util.Calendar.WEEK_OF_YEAR) &&
                now.get(java.util.Calendar.YEAR) == messageTime.get(java.util.Calendar.YEAR)) {
                java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault());
                return dayFormat.format(messageTime.getTime());
            }
            
            // Otherwise show date
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault());
            return dateFormat.format(messageTime.getTime());
        }
    }
}

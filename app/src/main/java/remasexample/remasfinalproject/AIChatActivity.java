package remas.example.remasfinalproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIChatActivity extends AppCompatActivity {
    
    // UI Components
    private ImageView ivBack, ivMenu, ivAttachment, ivEmoji, ivSend;
    private TextView tvStatus;
    private EditText etMessage;
    private MaterialCardView cvSend;
    private RecyclerView rvMessages;
    
    // Data
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private boolean isTyping = false;
    
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    // AI Assistant
    private static final String TAG = "AIChatActivity";
    
    // Async execution
    private ExecutorService executorService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();
        
        // Initialize UI
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupTextWatcher();
        
        // Load existing messages
        loadExistingMessages();
        
        // Add welcome message only if no existing messages
        if (messageList.isEmpty()) {
            addWelcomeMessage();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        ivMenu = findViewById(R.id.ivMenu);
        ivAttachment = findViewById(R.id.ivAttachment);
        ivEmoji = findViewById(R.id.ivEmoji);
        ivSend = findViewById(R.id.ivSend);
        tvStatus = findViewById(R.id.tvStatus);
        etMessage = findViewById(R.id.etMessage);
        cvSend = findViewById(R.id.cvSend);
        rvMessages = findViewById(R.id.rvMessages);
    }
    
    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messageAdapter);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        cvSend.setOnClickListener(v -> sendMessage());
        
        ivMenu.setOnClickListener(v -> showMenuDialog());
        
        ivAttachment.setOnClickListener(v -> showAttachmentOptions());
        
        ivEmoji.setOnClickListener(v -> Toast.makeText(this, "Emoji picker coming soon!", Toast.LENGTH_SHORT).show());
    }
    
    private void setupTextWatcher() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update send button state
                updateSendButtonState();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void updateSendButtonState() {
        boolean hasText = etMessage.getText().toString().trim().length() > 0;
        ivSend.setAlpha(hasText ? 1.0f : 0.5f);
        ivSend.setEnabled(hasText);
    }
    
    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) return;
        
        // Add user message
        Message userMessage = new Message();
        userMessage.setText(messageText);
        userMessage.setIsUser(true);
        userMessage.setTimestamp(System.currentTimeMillis());
        
        messageList.add(userMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
        
        // Save user message to Firebase
        saveMessageToFirebase(messageText, true);
        
        // Clear input
        etMessage.setText("");
        
        // Show typing indicator
        showTypingIndicator();
        
        // Generate AI response
        simulateAIResponse(messageText);
    }
    
    private void simulateAIResponse(String userMessage) {
        showTypingIndicator();
        
        // Use CompletableFuture for async AI call
        CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate network delay
                Thread.sleep(1000 + new Random().nextInt(1000));
                
                // Generate smart response based on user input
                return generateSmartResponse(userMessage);
            } catch (Exception e) {
                Log.e(TAG, "Error generating AI response", e);
                return generateSmartResponse(userMessage);
            }
        }, executorService).thenAcceptAsync(aiResponse -> {
            // Update UI on main thread
            runOnUiThread(() -> {
                hideTypingIndicator();
                
                if (aiResponse != null && !aiResponse.isEmpty()) {
                    Message aiMessage = new Message();
                    aiMessage.setText(aiResponse);
                    aiMessage.setIsUser(false);
                    aiMessage.setTimestamp(System.currentTimeMillis());
                    
                    messageList.add(aiMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    rvMessages.scrollToPosition(messageList.size() - 1);
                    
                    // Save AI response to Firebase
                    saveMessageToFirebase(aiResponse, false);
                } else {
                    // Fallback response if AI fails
                    showFallbackResponse();
                }
            });
        }, executorService).exceptionally(throwable -> {
            // Handle errors on main thread
            runOnUiThread(() -> {
                hideTypingIndicator();
                showFallbackResponse();
            });
            return null;
        });
    }
    
    private String generateSmartResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase().trim();
        
        // Property-related responses
        if (lowerMessage.contains("price") || lowerMessage.contains("cost") || lowerMessage.contains("budget")) {
            return "I can help you with pricing! To give you the best recommendations, could you tell me:\n\n" +
                    "📍 What area are you interested in?\n" +
                    "🏠 What type of property (apartment, house, dorm)?\n" +
                    "👥 How many bedrooms do you need?\n" +
                    "💰 What's your preferred price range?\n\n" +
                    "This will help me find properties within your budget!";
        }
        
        if (lowerMessage.contains("profile") || lowerMessage.contains("picture") || lowerMessage.contains("account")) {
            return "I can help you with your profile! Here's how to change your profile picture:\n\n" +
                    "1️⃣ Go to the Profile section\n" +
                    "2️⃣ Tap your current profile picture\n" +
                    "3️⃣ Choose 'Change Photo' from gallery or camera\n" +
                    "4️⃣ Crop and save your new photo\n" +
                    "5️⃣ Your profile will be updated across the app\n\n" +
                    "For other account settings, go to Profile → Settings. What else would you like help with?";
        }
        
        if (lowerMessage.contains("location") || lowerMessage.contains("area") || lowerMessage.contains("where")) {
            return "Great question about locations! Here are some popular areas I can help you with:\n\n" +
                    "🏙️ **Downtown** - Close to campus, lots of amenities\n" +
                    "🌳 **Suburbs** - Quieter, more space, better prices\n" +
                    "🏫 **University District** - Walking distance to classes\n" +
                    "🏢 **City Center** - Urban living, great transportation\n\n" +
                    "What's most important to you in a location?";
        }
        
        if (lowerMessage.contains("bedroom") || lowerMessage.contains("room") || lowerMessage.contains("sleep")) {
            return "For bedrooms, I can help you find:\n\n" +
                    "🛏️ **Studio** - Perfect for students, budget-friendly\n" +
                    "🛏️ **1 Bedroom** - Great for couples or singles\n" +
                    "🛏️ **2 Bedrooms** - Ideal for roommates\n" +
                    "🛏️ **3+ Bedrooms** - Perfect for families\n\n" +
                    "How many people will be living there?";
        }
        
        if (lowerMessage.contains("help") || lowerMessage.contains("assist")) {
            return "I'm here to help! Here's what I can do for you:\n\n" +
                    "🔍 **Property Search** - Find exactly what you need\n" +
                    "💰 **Price Analysis** - Compare costs and find deals\n" +
                    "📍 **Location Advice** - Best neighborhoods for you\n" +
                    "📋 **Application Help** - Guide through rental process\n" +
                    "🏠 **Property Questions** - Answer anything about rentals\n" +
                    "📊 **Market Trends** - Current rental market insights\n" +
                    "👤 **Profile Settings** - Help with account changes\n\n" +
                    "What would you like help with today?";
        }
        
        if (lowerMessage.contains("hello") || lowerMessage.contains("hi") || lowerMessage.contains("hey")) {
            return "Hello! 👋 I'm your LUXE STAY AI assistant, here to help you find the perfect property!\n\n" +
                    "I can help you with:\n" +
                    "🏠 Finding properties that match your needs\n" +
                    "💰 Comparing prices and finding great deals\n" +
                    "📍 Recommending the best locations\n" +
                    "📋 Guiding you through applications\n" +
                    "👤 Managing your profile and account\n\n" +
                    "What are you looking for in a property today?";
        }
        
        // Default intelligent response
        return "That's an interesting question! Based on what you've asked, I'd recommend:\n\n" +
                "🔍 **Property Search** - Let me help you find properties that match your criteria\n" +
                "💰 **Budget Analysis** - I can analyze prices in your preferred areas\n" +
                "📍 **Location Comparison** - Compare neighborhoods based on your needs\n" +
                "📊 **Market Insights** - Current rental trends and availability\n" +
                "👤 **Account Help** - Profile settings and account management\n\n" +
                "Could you tell me more about what specific features you're looking for?";
    }
    
    private void showFallbackResponse() {
        String fallbackResponse = "I'm having trouble connecting right now. " +
                "I'm here to help you find properties, compare prices, and answer questions about rentals. " +
                "Could you try again or ask me something specific about properties?";
        
        Message aiMessage = new Message();
        aiMessage.setText(fallbackResponse);
        aiMessage.setIsUser(false);
        aiMessage.setTimestamp(System.currentTimeMillis());
        
        messageList.add(aiMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
        
        saveMessageToFirebase(fallbackResponse, false);
    }
    
    private void showTypingIndicator() {
        if (!isTyping) {
            isTyping = true;
            tvStatus.setText("Typing...");
            
            Message typingMessage = new Message();
            typingMessage.setTyping(true);
            messageList.add(typingMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            rvMessages.scrollToPosition(messageList.size() - 1);
        }
    }
    
    private void hideTypingIndicator() {
        isTyping = false;
        tvStatus.setText("Online • Ready to help");
        
        // Remove typing indicator
        if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).isTyping()) {
            messageList.remove(messageList.size() - 1);
            messageAdapter.notifyItemRemoved(messageList.size());
        }
    }
    
    private void loadExistingMessages() {
        if (mAuth.getCurrentUser() == null) return;
        
        mDatabase.child("messages").child("ai_assistant").orderByChild("timestamp")
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        messageList.clear();
                        
                        for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FirebaseMessage firebaseMessage = snapshot.getValue(FirebaseMessage.class);
                            if (firebaseMessage != null) {
                                Message message = new Message();
                                message.setText(firebaseMessage.getMessageText());
                                message.setIsUser(firebaseMessage.getSenderId().equals(mAuth.getCurrentUser().getUid()));
                                message.setTimestamp(firebaseMessage.getTimestamp());
                                messageList.add(message);
                            }
                        }
                        
                        messageAdapter.notifyDataSetChanged();
                        if (messageList.size() > 0) {
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                        // Handle error silently
                    }
                });
    }
    
    private void saveMessageToFirebase(String messageText, boolean isUser) {
        if (mAuth.getCurrentUser() == null) return;
        
        // Create Firebase message
        FirebaseMessage message = new FirebaseMessage();
        message.setMessageText(messageText);
        message.setSenderId(isUser ? mAuth.getCurrentUser().getUid() : "ai_bot");
        message.setReceiverId(isUser ? "ai_bot" : mAuth.getCurrentUser().getUid());
        message.setTimestamp(System.currentTimeMillis());
        message.setChatId("ai_assistant");
        message.setRead(true);
        
        // Save to Firebase
        String messageId = mDatabase.child("messages").child("ai_assistant").push().getKey();
        mDatabase.child("messages").child("ai_assistant").child(messageId).setValue(message);
    }
    
    private void addWelcomeMessage() {
        String welcomeMessage = "Hello! 👋 I'm your LUXE STAY AI assistant, here to help you find the perfect property!\n\n" +
                "I can help you with:\n" +
                "🏠 Finding properties that match your needs\n" +
                "💰 Comparing prices and finding great deals\n" +
                "📍 Recommending the best locations\n" +
                "📋 Guiding you through applications\n" +
                "👤 Managing your profile and account\n\n" +
                "What would you like help with today?";
        
        Message welcomeMsg = new Message();
        welcomeMsg.setText(welcomeMessage);
        welcomeMsg.setIsUser(false);
        welcomeMsg.setTimestamp(System.currentTimeMillis());
        
        messageList.add(welcomeMsg);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
    }
    
    private void showMenuDialog() {
        String[] options = {"Clear Chat History", "Export Chat", "Settings", "Help & Support", "About"};
        
        new AlertDialog.Builder(this)
                .setTitle("Chat Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            clearChatHistory();
                            break;
                        case 1:
                            exportChat();
                            break;
                        case 2:
                            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(this, "Help & Support coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            showAboutDialog();
                            break;
                    }
                })
                .show();
    }
    
    private void showAttachmentOptions() {
        String[] options = {"Document", "Camera", "Gallery", "Audio", "Location", "Contact"};
        
        new AlertDialog.Builder(this)
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
    
    private void clearChatHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Chat History")
                .setMessage("Are you sure you want to clear all chat history?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    messageList.clear();
                    messageAdapter.notifyDataSetChanged();
                    addWelcomeMessage();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void exportChat() {
        Toast.makeText(this, "Export chat feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void openSettings() {
        Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void showHelp() {
        Toast.makeText(this, "Help & Support coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About LUXE STAY AI")
                .setMessage("Version: 1.0.0\n\n" +
                        "LUXE STAY AI is your intelligent property assistant, powered by advanced AI technology.")
                .setPositiveButton("OK", null)
                .show();
    }
    
    private void attachDocument() { Toast.makeText(this, "Attach document", Toast.LENGTH_SHORT).show(); }
    private void openCamera() { Toast.makeText(this, "Open camera", Toast.LENGTH_SHORT).show(); }
    private void openGallery() { Toast.makeText(this, "Open gallery", Toast.LENGTH_SHORT).show(); }
    private void attachAudio() { Toast.makeText(this, "Attach audio", Toast.LENGTH_SHORT).show(); }
    private void shareLocation() { Toast.makeText(this, "Share location", Toast.LENGTH_SHORT).show(); }
    private void shareContact() { Toast.makeText(this, "Share contact", Toast.LENGTH_SHORT).show(); }
    
    // Firebase Message class for database storage
    public static class FirebaseMessage {
        private String messageText;
        private String senderId;
        private String receiverId;
        private long timestamp;
        private String chatId;
        private boolean read;
        
        public FirebaseMessage() {}
        
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
    }
    
    // Message class
    public static class Message {
        private String text;
        private boolean isUser;
        private long timestamp;
        private boolean isTyping;
        
        public Message() {}
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public boolean isUser() { return isUser; }
        public void setIsUser(boolean isUser) { this.isUser = isUser; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public boolean isTyping() { return isTyping; }
        public void setTyping(boolean typing) { isTyping = typing; }
    }
    
    // Message Adapter
    private static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private List<Message> messages;
        
        public MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }
        
        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
            return new MessageViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            
            // Show/hide appropriate containers based on sender
            if (message.isUser()) {
                holder.userMessageContainer.setVisibility(View.VISIBLE);
                holder.aiMessageContainer.setVisibility(View.GONE);
                
                // Bind user message data
                holder.tvUserMessage.setText(message.getText());
                holder.tvUserTime.setText(getFormattedTime(message.getTimestamp()));
                holder.ivUserMessageStatus.setVisibility(View.VISIBLE);
            } else {
                holder.userMessageContainer.setVisibility(View.GONE);
                holder.aiMessageContainer.setVisibility(View.VISIBLE);
                
                // Bind received message data
                holder.tvAIMessage.setText(message.getText());
                holder.tvAITime.setText(getFormattedTime(message.getTimestamp()));
                holder.ivAIMessageStatus.setVisibility(View.GONE);
            }
        }
        
        @Override
        public int getItemCount() {
            return messages.size();
        }
        
        private String getFormattedTime(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return sdf.format(new Date(timestamp));
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
}

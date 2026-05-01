package remas.example.remasfinalproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        // Initialize UI
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupTextWatcher();
        
        // Add welcome message
        addWelcomeMessage();
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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog();
            }
        });
        
        ivAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttachmentOptions();
            }
        });
        
        ivEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement emoji picker
                Toast.makeText(AIChatActivity.this, "Emoji picker coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
        
        cvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    
    private void setupTextWatcher() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    ivSend.setImageResource(android.R.drawable.ic_menu_send);
                } else {
                    ivSend.setImageResource(android.R.drawable.ic_menu_send);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void addWelcomeMessage() {
        Message welcomeMessage = new Message();
        welcomeMessage.setText("Hello! I'm LUXE STAY AI, your personal property assistant. I can help you:\n\n" +
                "🏠 Find the perfect property\n" +
                "💰 Compare prices and features\n" +
                "📍 Answer questions about locations\n" +
                "📋 Help with rental applications\n" +
                "🔍 Search for specific amenities\n\n" +
                "What can I help you with today?");
        welcomeMessage.setIsUser(false);
        welcomeMessage.setTimestamp(System.currentTimeMillis());
        
        messageList.add(welcomeMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
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
        
        // Clear input
        etMessage.setText("");
        
        // Show typing indicator
        showTypingIndicator();
        
        // Simulate AI response (replace with actual Gemini API)
        simulateAIResponse(messageText);
    }
    
    private void showTypingIndicator() {
        isTyping = true;
        tvStatus.setText("Typing...");
        
        Message typingMessage = new Message();
        typingMessage.setTyping(true);
        messageList.add(typingMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
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
    
    private void simulateAIResponse(String userMessage) {
        // Simulate network delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideTypingIndicator();
                
                String aiResponse = generateAIResponse(userMessage);
                
                Message aiMessage = new Message();
                aiMessage.setText(aiResponse);
                aiMessage.setIsUser(false);
                aiMessage.setTimestamp(System.currentTimeMillis());
                
                messageList.add(aiMessage);
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                rvMessages.scrollToPosition(messageList.size() - 1);
                
                // Save to Firebase
                saveMessageToFirebase(userMessage, aiResponse);
            }
        }, 1500 + new Random().nextInt(1000)); // 1.5-2.5 second delay
    }
    
    private String generateAIResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("hello") || lowerMessage.contains("hi")) {
            return "Hello! How can I help you find your perfect property today? 🏠";
        } else if (lowerMessage.contains("price") || lowerMessage.contains("cost")) {
            return "I can help you with pricing information! Could you tell me:\n\n" +
                    "📍 What area are you interested in?\n" +
                    "🏠 What type of property (apartment, house, dorm)?\n" +
                    "👥 How many bedrooms do you need?\n\n" +
                    "This will help me find the best options within your budget!";
        } else if (lowerMessage.contains("location") || lowerMessage.contains("area")) {
            return "Great question about locations! Here are some popular areas:\n\n" +
                    "🏙️ **Downtown** - Close to campus, lots of amenities\n" +
                    "🌳 **Suburbs** - Quieter, more space, better prices\n" +
                    "🏫 **University District** - Walking distance to classes\n\n" +
                    "What's most important to you in a location?";
        } else if (lowerMessage.contains("bedroom") || lowerMessage.contains("room")) {
            return "For bedrooms, I can help you find:\n\n" +
                    "🛏️ **Studio** - Perfect for students, budget-friendly\n" +
                    "🛏️ **1 Bedroom** - Great for couples or singles\n" +
                    "🛏️ **2+ Bedrooms** - Ideal for roommates or families\n\n" +
                    "How many people will be living there?";
        } else if (lowerMessage.contains("help")) {
            return "I'm here to help! Here's what I can do:\n\n" +
                    "🔍 **Search Properties** - Find exactly what you need\n" +
                    "💰 **Price Comparison** - Get the best deals\n" +
                    "📍 **Location Advice** - Find the perfect neighborhood\n" +
                    "📋 **Application Help** - Guide you through the process\n" +
                    "🏠 **Property Questions** - Answer anything about rentals\n\n" +
                    "What would you like help with?";
        } else {
            return "That's interesting! Based on what you've told me, I'd recommend:\n\n" +
                    "🏠 Looking at properties in your preferred area\n" +
                    "💰 Comparing prices within your budget\n" +
                    "📍 Checking proximity to important locations\n\n" +
                    "Could you tell me more about what specific features you're looking for in a property?";
        }
    }
    
    private void saveMessageToFirebase(String userMessage, String aiResponse) {
        if (mAuth.getCurrentUser() == null) return;
        
        String chatId = "ai_assistant";
        String userId = mAuth.getCurrentUser().getUid();
        
        // Save user message
        DatabaseReference userMsgRef = mDatabase.child("ai_chats")
                .child(userId)
                .child(chatId)
                .push();
        userMsgRef.child("text").setValue(userMessage);
        userMsgRef.child("isUser").setValue(true);
        userMsgRef.child("timestamp").setValue(System.currentTimeMillis());
        
        // Save AI response
        DatabaseReference aiMsgRef = mDatabase.child("ai_chats")
                .child(userId)
                .child(chatId)
                .push();
        aiMsgRef.child("text").setValue(aiResponse);
        aiMsgRef.child("isUser").setValue(false);
        aiMsgRef.child("timestamp").setValue(System.currentTimeMillis());
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
        String[] options = {"Photo", "Document", "Location", "Contact"};
        
        new AlertDialog.Builder(this)
                .setTitle("Share")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(this, "Photo upload coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(this, "Document upload coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(this, "Location sharing coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(this, "Contact sharing coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }
    
    private void clearChatHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Chat History")
                .setMessage("Are you sure you want to clear all chat history? This cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    messageList.clear();
                    messageAdapter.notifyDataSetChanged();
                    addWelcomeMessage();
                    Toast.makeText(this, "Chat history cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void exportChat() {
        StringBuilder chatText = new StringBuilder();
        chatText.append("LUXE STAY AI Chat Export\n");
        chatText.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n\n");
        
        for (Message message : messageList) {
            if (!message.isTyping()) {
                String sender = message.isUser() ? "You" : "LUXE STAY AI";
                chatText.append(sender).append(": ").append(message.getText()).append("\n\n");
            }
        }
        
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Chat Export", chatText.toString());
        clipboard.setPrimaryClip(clip);
        
        Toast.makeText(this, "Chat copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
    
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About LUXE STAY AI")
                .setMessage("Version: 1.0.0\n\n" +
                        "LUXE STAY AI is your intelligent property assistant, powered by advanced AI technology to help you find the perfect rental property.\n\n" +
                        "Features:\n" +
                        "• Smart property recommendations\n" +
                        "• Real-time price comparisons\n" +
                        "• Location insights\n" +
                        "• 24/7 availability\n\n" +
                        "Made with ❤️ for students and renters.")
                .setPositiveButton("OK", null)
                .show();
    }
    
    // Message Adapter
    private class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Message> messages;
        
        public MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }
        
        @Override
        public int getItemViewType(int position) {
            Message message = messages.get(position);
            if (message.isTyping()) {
                return 2; // Typing indicator
            } else if (message.isUser()) {
                return 0; // User message
            } else {
                return 1; // AI message
            }
        }
        
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AIChatActivity.this).inflate(R.layout.chat_message_item, parent, false);
            return new MessageViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Message message = messages.get(position);
            MessageViewHolder messageHolder = (MessageViewHolder) holder;
            
            if (message.isTyping()) {
                messageHolder.bindTypingIndicator();
            } else if (message.isUser()) {
                messageHolder.bindUserMessage(message);
            } else {
                messageHolder.bindAIMessage(message);
            }
        }
        
        @Override
        public int getItemCount() {
            return messages.size();
        }
        
        class MessageViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout userMessageContainer, aiMessageContainer, typingIndicator;
            private TextView tvUserMessage, tvUserTime, tvAIMessage, tvAITime;
            private ImageView ivCopy, ivLike, ivDislike;
            
            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                userMessageContainer = itemView.findViewById(R.id.userMessageContainer);
                aiMessageContainer = itemView.findViewById(R.id.aiMessageContainer);
                typingIndicator = itemView.findViewById(R.id.typingIndicator);
                tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
                tvUserTime = itemView.findViewById(R.id.tvUserTime);
                tvAIMessage = itemView.findViewById(R.id.tvAIMessage);
                tvAITime = itemView.findViewById(R.id.tvAITime);
                ivCopy = itemView.findViewById(R.id.ivCopy);
                ivLike = itemView.findViewById(R.id.ivLike);
                ivDislike = itemView.findViewById(R.id.ivDislike);
            }
            
            public void bindUserMessage(Message message) {
                userMessageContainer.setVisibility(View.VISIBLE);
                aiMessageContainer.setVisibility(View.GONE);
                typingIndicator.setVisibility(View.GONE);
                
                tvUserMessage.setText(message.getText());
                tvUserTime.setText(formatTime(message.getTimestamp()));
            }
            
            public void bindAIMessage(Message message) {
                userMessageContainer.setVisibility(View.GONE);
                aiMessageContainer.setVisibility(View.VISIBLE);
                typingIndicator.setVisibility(View.GONE);
                
                tvAIMessage.setText(message.getText());
                tvAITime.setText(formatTime(message.getTimestamp()));
                
                ivCopy.setOnClickListener(v -> copyToClipboard(message.getText()));
                ivLike.setOnClickListener(v -> {
                    Toast.makeText(AIChatActivity.this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                });
                ivDislike.setOnClickListener(v -> {
                    Toast.makeText(AIChatActivity.this, "Thanks for helping me improve!", Toast.LENGTH_SHORT).show();
                });
            }
            
            public void bindTypingIndicator() {
                userMessageContainer.setVisibility(View.GONE);
                aiMessageContainer.setVisibility(View.GONE);
                typingIndicator.setVisibility(View.VISIBLE);
            }
            
            private void copyToClipboard(String text) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("AI Response", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(AIChatActivity.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
            
            private String formatTime(long timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                return sdf.format(new Date(timestamp));
            }
        }
    }
    
    // Message Model
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
}

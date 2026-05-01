package remas.example.remasfinalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * WhatsApp-Style Notification Manager
 * Handles beautiful notifications with sender info, preview, and actions
 */
public class ChatNotificationManager {
    
    private static final String CHANNEL_ID = "chat_messages";
    private static final String CHANNEL_NAME = "Chat Messages";
    private static final int NOTIFICATION_ID = 1001;
    
    private Context context;
    private android.app.NotificationManager notificationManager;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    
    public ChatNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
        
        createNotificationChannel();
        startListeningForMessages();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Chat messages and notifications");
            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private void startListeningForMessages() {
        if (mAuth.getCurrentUser() == null) return;
        
        String userId = mAuth.getCurrentUser().getUid();
        
        // Listen for new messages
        mDatabase.child("messages").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageData message = snapshot.getValue(MessageData.class);
                    if (message != null && !message.isRead()) {
                        showWhatsAppStyleNotification(message);
                        // Mark as read to avoid duplicate notifications
                        mDatabase.child("messages").child(userId).child(snapshot.getKey()).child("read").setValue(true);
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    
    public void showWhatsAppStyleNotification(MessageData message) {
        // Create standard notification (since custom layout was deleted)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(message.getSenderName())
                .setContentText(message.getMessageText())
                .setSubText(message.getFormattedTime())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        
        // Create intent to open chat
        Intent chatIntent;
        if (message.getChatId().equals("ai_assistant")) {
            chatIntent = new Intent(context, AIChatActivity.class);
        } else {
            chatIntent = new Intent(context, ChatsFinal.class);
            chatIntent.putExtra("chatId", message.getChatId());
            chatIntent.putExtra("userName", message.getSenderName());
        }
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent chatPendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                chatIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        builder.setContentIntent(chatPendingIntent);
        
        // Show notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    
    public void showNewMessageNotification(String senderName, String messageText, String chatId) {
        MessageData message = new MessageData();
        message.setSenderName(senderName);
        message.setMessageText(messageText);
        message.setChatId(chatId);
        message.setTimestamp(System.currentTimeMillis());
        message.setRead(false);
        
        showWhatsAppStyleNotification(message);
    }
    
    public void showAIChatNotification(String aiMessage) {
        MessageData message = new MessageData();
        message.setSenderName("LUXE STAY AI");
        message.setMessageText(aiMessage);
        message.setChatId("ai_assistant");
        message.setTimestamp(System.currentTimeMillis());
        message.setRead(false);
        
        // Use AI icon for AI notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("LUXE STAY AI")
                .setContentText(aiMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(aiMessage))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        
        Intent aiIntent = new Intent(context, AIChatActivity.class);
        aiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent aiPendingIntent = PendingIntent.getActivity(
                context, 
                2, 
                aiIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        builder.setContentIntent(aiPendingIntent);
        
        notificationManager.notify(NOTIFICATION_ID + 1, builder.build());
    }
    
    public void clearAllNotifications() {
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancel(NOTIFICATION_ID + 1);
    }
    
    /**
     * Message data model for notifications
     */
    public static class MessageData {
        private String senderName;
        private String messageText;
        private String chatId;
        private long timestamp;
        private boolean isRead;
        
        public MessageData() {}
        
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        
        public String getMessageText() { return messageText; }
        public void setMessageText(String messageText) { this.messageText = messageText; }
        
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
        
        public String getFormattedTime() {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;
            
            if (diff < 60000) return "Just now";
            if (diff < 3600000) return (diff / 60000) + "m ago";
            if (diff < 86400000) return (diff / 3600000) + "h ago";
            return (diff / 86400000) + "d ago";
        }
    }
}

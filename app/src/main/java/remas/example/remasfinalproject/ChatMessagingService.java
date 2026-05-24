package remas.example.remasfinalproject;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Service for sending chat messages and triggering notifications.
 * When a user sends a message, it's stored in the recipient's messages node
 * which triggers the ChatNotificationManager to show a notification.
 */
public class ChatMessagingService {
    
    private static ChatMessagingService instance;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context context;
    
    private ChatMessagingService(Context context) {
        this.context = context.getApplicationContext();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
    }
    
    public static synchronized ChatMessagingService getInstance(Context context) {
        if (instance == null) {
            instance = new ChatMessagingService(context);
        }
        return instance;
    }
    
    /**
     * Send a message to another user.
     * This stores the message in the recipient's messages node,
     * which will trigger a notification via ChatNotificationManager.
     * 
     * @param recipientId The ID of the user receiving the message
     * @param messageText The text of the message
     * @param senderName The name of the sender (for notification)
     */
    public void sendMessage(String recipientId, String messageText, String senderName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        
        String senderId = currentUser.getUid();
        
        // Store message in recipient's messages node for notification
        DatabaseReference recipientMessagesRef = mDatabase.child("messages").child(recipientId).push();
        String messageId = recipientMessagesRef.getKey();
        
        ChatNotificationManager.MessageData messageData = new ChatNotificationManager.MessageData();
        messageData.setSenderName(senderName);
        messageData.setSenderId(senderId);
        messageData.setMessageText(messageText);
        messageData.setChatId(senderId + "_" + recipientId);
        messageData.setTimestamp(System.currentTimeMillis());
        messageData.setRead(false);
        
        recipientMessagesRef.setValue(messageData);
        
        // Also store in sender's messages for chat history
        DatabaseReference senderMessagesRef = mDatabase.child("messages").child(senderId).push();
        ChatNotificationManager.MessageData senderMessageData = new ChatNotificationManager.MessageData();
        senderMessageData.setSenderName(senderName);
        senderMessageData.setSenderId(recipientId);
        senderMessageData.setMessageText(messageText);
        senderMessageData.setChatId(senderId + "_" + recipientId);
        senderMessageData.setTimestamp(System.currentTimeMillis());
        senderMessageData.setRead(true); // Mark as read for sender
        
        senderMessagesRef.setValue(senderMessageData);
        
        // Store in chat room for both users
        String chatRoomId = senderId.compareTo(recipientId) < 0 
            ? senderId + "_" + recipientId 
            : recipientId + "_" + senderId;
        
        DatabaseReference chatRoomRef = mDatabase.child("chats").child(chatRoomId).push();
        ChatMessage chatMessage = new ChatMessage(messageText, ChatMessage.MESSAGE_TYPE_SENT);
        chatMessage.setSenderName(senderName);
        chatMessage.setTimestamp(System.currentTimeMillis());
        
        chatRoomRef.setValue(chatMessage);
    }
    
    /**
     * Send a message with profile picture URL.
     * 
     * @param recipientId The ID of the user receiving the message
     * @param messageText The text of the message
     * @param senderName The name of the sender
     * @param senderProfilePicture The profile picture URL of the sender
     */
    public void sendMessageWithProfile(String recipientId, String messageText, String senderName, String senderProfilePicture) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        
        String senderId = currentUser.getUid();
        
        // Store message in recipient's messages node for notification
        DatabaseReference recipientMessagesRef = mDatabase.child("messages").child(recipientId).push();
        String messageId = recipientMessagesRef.getKey();
        
        ChatNotificationManager.MessageData messageData = new ChatNotificationManager.MessageData();
        messageData.setSenderName(senderName);
        messageData.setSenderId(senderId);
        messageData.setMessageText(messageText);
        messageData.setChatId(senderId + "_" + recipientId);
        messageData.setTimestamp(System.currentTimeMillis());
        messageData.setRead(false);
        
        recipientMessagesRef.setValue(messageData);
        
        // Also store in sender's messages for chat history
        DatabaseReference senderMessagesRef = mDatabase.child("messages").child(senderId).push();
        ChatNotificationManager.MessageData senderMessageData = new ChatNotificationManager.MessageData();
        senderMessageData.setSenderName(senderName);
        senderMessageData.setSenderId(recipientId);
        senderMessageData.setMessageText(messageText);
        senderMessageData.setChatId(senderId + "_" + recipientId);
        senderMessageData.setTimestamp(System.currentTimeMillis());
        senderMessageData.setRead(true); // Mark as read for sender
        
        senderMessagesRef.setValue(senderMessageData);
        
        // Store in chat room with profile picture
        String chatRoomId = senderId.compareTo(recipientId) < 0 
            ? senderId + "_" + recipientId 
            : recipientId + "_" + senderId;
        
        DatabaseReference chatRoomRef = mDatabase.child("chats").child(chatRoomId).push();
        ChatMessage chatMessage = new ChatMessage(messageText, ChatMessage.MESSAGE_TYPE_SENT);
        chatMessage.setSenderName(senderName);
        chatMessage.setProfilePictureUrl(senderProfilePicture);
        chatMessage.setTimestamp(System.currentTimeMillis());
        
        chatRoomRef.setValue(chatMessage);
    }
}

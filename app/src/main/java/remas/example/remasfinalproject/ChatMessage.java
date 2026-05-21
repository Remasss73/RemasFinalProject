package remas.example.remasfinalproject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model class representing a chat message in the AI conversation.
 * Includes message content, sender type, timestamp, and message status.
 */
public class ChatMessage implements Serializable {
    
    public static final int MESSAGE_TYPE_RECEIVED = 0;
    public static final int MESSAGE_TYPE_SENT = 1;
    
    private String messageText;
    private int messageType; // MESSAGE_TYPE_RECEIVED or MESSAGE_TYPE_SENT
    private long timestamp;
    private boolean isTyping;
    private String senderName;
    private String profilePictureUrl;
    
    /**
     * Constructor for creating a new chat message.
     * @param messageText The text content of the message
     * @param messageType The type of message (sent or received)
     */
    public ChatMessage(String messageText, int messageType) {
        this.messageText = messageText;
        this.messageType = messageType;
        this.timestamp = System.currentTimeMillis();
        this.isTyping = false;
        this.senderName = messageType == MESSAGE_TYPE_RECEIVED ? "LUXE STAY AI" : "You";
    }
    
    /**
     * Constructor with custom timestamp.
     */
    public ChatMessage(String messageText, int messageType, long timestamp) {
        this.messageText = messageText;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.isTyping = false;
        this.senderName = messageType == MESSAGE_TYPE_RECEIVED ? "LUXE STAY AI" : "You";
    }
    
    /**
     * Get the message text.
     */
    public String getMessageText() {
        return messageText;
    }
    
    /**
     * Set the message text.
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    /**
     * Get the message type (sent or received).
     */
    public int getMessageType() {
        return messageType;
    }
    
    /**
     * Set the message type.
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    
    /**
     * Get the timestamp of the message.
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Set the timestamp of the message.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Check if the message is currently being typed (typing indicator).
     */
    public boolean isTyping() {
        return isTyping;
    }
    
    /**
     * Set the typing indicator status.
     */
    public void setTyping(boolean typing) {
        isTyping = typing;
    }
    
    /**
     * Get the sender name.
     */
    public String getSenderName() {
        return senderName;
    }
    
    /**
     * Set the sender name.
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    /**
     * Get the profile picture URL.
     */
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    
    /**
     * Set the profile picture URL.
     */
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    
    /**
     * Format the timestamp as a readable time string (e.g., "10:30 AM").
     */
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Format the timestamp as a readable date string (e.g., "Today", "Yesterday", or "MM/dd/yyyy").
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Check if this message was sent today.
     */
    public boolean isToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String today = sdf.format(new Date());
        String messageDate = sdf.format(new Date(timestamp));
        return today.equals(messageDate);
    }
}

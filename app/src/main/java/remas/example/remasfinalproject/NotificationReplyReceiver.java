package remas.example.remasfinalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Handles notification reply actions
 */
public class NotificationReplyReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String chatId = intent.getStringExtra("chatId");
        String senderName = intent.getStringExtra("senderName");
        
        // Open the chat directly
        if (chatId != null) {
            Intent chatIntent;
            
            if (chatId.equals("ai_assistant")) {
                // Open AI Chat
                chatIntent = new Intent(context, AIChatActivity.class);
            } else {
                // Open regular chat
                chatIntent = new Intent(context, Chat.class);
                chatIntent.putExtra("chatId", chatId);
                chatIntent.putExtra("userName", senderName);
            }
            
            chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(chatIntent);
        }
        
        // Dismiss the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1001);
    }
}

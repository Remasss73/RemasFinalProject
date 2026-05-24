package remas.example.remasfinalproject;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

/**
 * BaseActivity: Acts as the parent for all activities to ensure language settings are applied.
 */
public class BaseActivity extends AppCompatActivity {
    
    protected ChatNotificationManager chatNotificationManager;
    
    @Override
    protected void attachBaseContext(Context newBase) {
        // Safe check to prevent startup crashes if context is null
        if (newBase != null) {
            super.attachBaseContext(LocaleHelper.onAttach(newBase));
        } else {
            super.attachBaseContext(newBase);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Initialize chat notification manager when activity resumes
        if (chatNotificationManager == null) {
            chatNotificationManager = new ChatNotificationManager(this);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Clear notifications when activity is paused (optional)
        if (chatNotificationManager != null) {
            chatNotificationManager.clearAllNotifications();
        }
    }
}

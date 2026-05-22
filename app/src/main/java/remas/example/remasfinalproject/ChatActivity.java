package remas.example.remasfinalproject;

/**
 * ChatActivity - Activity for displaying chat/messaging options
 * نشاط لعرض خيارات الدردشة/المراسلة
 * 
 * This activity serves as a hub for messaging features:
 * يخدم هذا النشاط كمركز لميزات المراسلة:
 * - Provides access to AI Assistant chat
 *   يوفر الوصول إلى دردشة المساعد الذكي
 * - Shows toolbar with navigation
 *   يظهر شريط الأدوات مع التنقل
 * - Handles back navigation with smooth transitions
 *   يتعامل مع التنقل الخلفي مع انتقالات سلسة
 * 
 * The activity is a simple launcher for the AI chat feature
 * النشاط هو مشغل بسيط لميزة الدردشة الذكية
 * 
 * @author Remas Project Team
 * فريق مشروع REMAS
 * @version 1.0
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;

public class ChatActivity extends BaseActivity {
    
    /**
     * Called when activity is created
     * يتم الاستدعاء عند إنشاء النشاط
     * 
     * Sets up the toolbar with title and back button
     * يقوم بإعداد شريط الأدوات مع العنوان وزر الرجوع
     * and initializes click listeners for chat options
     * ويهيئ مستمعي النقرات لخيارات الدردشة
     * 
     * @param savedInstanceState Bundle containing previously saved state
     * حزمة تحتوي على الحالة المحفوظة مسبقاً
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_final);

        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Sets up click listeners for interactive UI elements
     * يقوم بإعداد مستمعي النقرات لعناصر واجهة المستخدم التفاعلية
     * 
     * Configures the AI chat container to open the AI chat activity
     * يكوين حاوية الدردشة الذكية لفتح نشاط الدردشة الذكية
     * when tapped
     * عند النقر
     */
    private void setupClickListeners() {
        // AI Assistant chat
        // دردشة المساعد الذكي
        @SuppressLint("WrongViewCast") MaterialCardView aiChatContainer = findViewById(R.id.aiChatContainer);
        if (aiChatContainer != null) {
            aiChatContainer.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, ai.class);
                startActivity(intent);
            });
        }
        
        // Back button in the custom layout
        // زر الرجوع في التخطيط المخصص
        android.view.View ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    /**
     * Handles the physical back button press
     * يتعامل مع ضغط زر الرجوع الفعلي
     * 
     * Adds smooth fade transition when navigating back
     * يضيف انتقال تلاشي سلس عند التنقل للخلف
     */
    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

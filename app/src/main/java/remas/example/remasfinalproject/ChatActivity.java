package remas.example.remasfinalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.card.MaterialCardView;

/**
 * نشاط المحادثات: يعرض قائمة خيارات الدردشة، بما في ذلك الدردشة مع المساعد الذكي.
 */
public class ChatActivity extends BaseActivity {
    
    /**
     * يتم استدعاؤها عند إنشاء النشاط؛ تقوم بتهيئة واجهة الدردشة.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_final);

        setupClickListeners();
    }

    /**
     * إعداد مستمعي النقرات لفتح محادثة الذكاء الاصطناعي أو الرجوع للخلف.
     */
    private void setupClickListeners() {
        @SuppressLint("WrongViewCast") 
        MaterialCardView aiChatContainer = findViewById(R.id.aiChatContainer);
        if (aiChatContainer != null) {
            aiChatContainer.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, ai.class);
                startActivity(intent);
            });
        }
        
        android.view.View ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    /**
     * معالجة ضغط زر الرجوع لإضافة تأثير حركي عند الخروج.
     */
    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

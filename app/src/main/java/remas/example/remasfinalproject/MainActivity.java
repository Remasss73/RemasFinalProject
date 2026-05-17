package remas.example.remasfinalproject;

/**
 * MainActivity - Main entry point of the application
 * MainActivity - نقطة الدخول الرئيسية للتطبيق
 * 
 * This is the first activity that launches when the app starts:
 * هذا هو النشاط الأول الذي يتم إطلاقه عند بدء التطبيق:
 * - Sets up the main layout with bottom navigation
 *   يضبط التخطيط الرئيسي مع التنقل السفلي
 * - Enables edge-to-edge display for modern Android look
 *   يتيح العرض من حافة إلى حافة لمظهر Android الحديث
 * - Handles system window insets for proper padding
 *   يتعامل مع إدراجات النظام للحشو المناسب
 * 
 * The activity extends BaseActivity for language support
 * يمد النشاط BaseActivity لدعم اللغة
 * 
 * @author Remas Project Team
 * فريق مشروع REMAS
 * @version 1.0
 */

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends BaseActivity {

    /**
     * Called when activity is created
     * يتم الاستدعاء عند إنشاء النشاط
     * 
     * Sets up the main screen with edge-to-edge display
     * يضبط الشاشة الرئيسية مع العرض من حافة إلى حافة
     * and handles system bar insets for proper layout
     * ويتعامل مع إدراجات الشريط للتخطيط المناسب
     * 
     * @param savedInstanceState Bundle containing previously saved state
     * حزمة تحتوي على الحالة المحفوظة مسبقاً
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

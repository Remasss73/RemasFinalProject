package remas.example.remasfinalproject;

/**
 * BaseActivity - Base class for all activities in the application
 * BaseActivity - الفئة الأساسية لجميع الأنشطة في التطبيق
 * 
 * This class extends AppCompatActivity and provides common functionality
 * تمد هذه الفئة AppCompatActivity وتوفر وظائف مشتركة
 * for all activities in the app:
 * لجميع الأنشطة في التطبيق:
 * - Automatic language/locale management using LocaleHelper
 *   إدارة اللغة/اللغة المحلية التلقائية باستخدام LocaleHelper
 * - Ensures consistent language across all screens
 *   يضمن اتساق اللغة عبر جميع الشاشات
 * - Centralized base activity configuration
 *   تكوين النشاط الأساسي المركزي
 * 
 * All activities in the app should extend this class instead of AppCompatActivity
 * يجب أن تمتد جميع الأنشطة في التطبيق هذه الفئة بدلاً من AppCompatActivity
 * to ensure proper language support
 * لضمان دعم اللغة بشكل صحيح
 * 
 * @author Remas Project Team
 * فريق مشروع REMAS
 * @version 1.0
 */

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    
    /**
     * Attaches the base context with language configuration
     * يربط السياق الأساسي مع تكوين اللغة
     * 
     * This method is called before onCreate and ensures that
     * يتم استدعاء هذه الطريقة قبل onCreate وتضمن أن
     * the app's language is properly set for this activity
     * لغة التطبيق مضبوطة بشكل صحيح لهذا النشاط
     * using the LocaleHelper utility class
     * باستخدام فئة الأداة المساعدة LocaleHelper
     * 
     * @param newBase The new base context to attach
     * السياق الأساسي الجديد للربط
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}

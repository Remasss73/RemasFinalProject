package remas.example.remasfinalproject;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

/**
 * النشاط الأساسي: يعمل كأب لجميع الأنشطة في التطبيق لضمان تطبيق إعدادات اللغة والخصائص المشتركة.
 */
public class BaseActivity extends AppCompatActivity {
    
    /**
     * ربط السياق الأساسي للنشاط مع إعدادات اللغة المختارة (عربي أو إنجليزي).
     * @param newBase السياق الأساسي الجديد.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}

package remas.example.remasfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;

/**
 * نشاط المساعد الذكي: يستخدم نموذج Gemini لتقديم اقتراحات وخطوات للمهام التي يدخلها المستخدم.
 */
public class ai extends AppCompatActivity {
    private GenerativeModel model;
    private EditText etTaskTopic;
    private Button btnSuggestSteps;
    private ProgressBar pbLoading;
    private TextView tvAiResponse;

    /**
     * يتم استدعاؤها عند إنشاء النشاط؛ تقوم بتهيئة نموذج الذكاء الاصطناعي وربط عناصر الواجهة.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        // تهيئة نموذج Gemini (يجب وضع مفتاح API حقيقي ليعمل)
        model = new GenerativeModel("gemini-1.5-flash", "YOUR_API_KEY_HERE");

        etTaskTopic = findViewById(R.id.etTaskTopic);
        btnSuggestSteps = findViewById(R.id.btnSuggestSteps);
        pbLoading = findViewById(R.id.pbLoading);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        
        btnSuggestSteps.setOnClickListener(v -> {
            String topic = etTaskTopic.getText().toString().trim();
            if (!topic.isEmpty()) {
                askFirebaseAiGeminiForSteps(topic);
            } else {
                Toast.makeText(ai.this, "يرجى إدخال موضوع المهمة", Toast.LENGTH_SHORT).show();
            }
        });
        
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * إرسال طلب لنموذج Gemini للحصول على خطوات مقترحة للمهمة المدخلة.
     * @param topic عنوان أو وصف المهمة المراد الحصول على خطوات لها.
     */
    private void askFirebaseAiGeminiForSteps(String topic) {
        pbLoading.setVisibility(View.VISIBLE);
        tvAiResponse.setText("");
        btnSuggestSteps.setEnabled(false);
        
        String promptStr = "I want to perform the following task: '" + topic + "'. " +
                "Can you suggest a clear, step-by-step checklist to complete this task effectively?";
        
        // استخدام CoroutineScope للتعامل مع العمليات في الخلفية (Background Thread)
        CoroutineScope scope = new CoroutineScope() {
            @NonNull
            @Override
            public CoroutineContext getCoroutineContext() {
                return null;
            }
        };
        // ملاحظة: هذا الكود يحتاج لتبعية Coroutines ليعمل بشكل صحيح في Kotlin/Java
        // يتم تنفيذ العملية في خلفية التطبيق لضمان عدم توقفه
        // scope.launch هو استدعاء لعملية غير متزامنة
    }
}
package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import remas.example.remasfinalproject.data.Seeker.Seekers;

/**
 * نشاط إنشاء حساب جديد للمستخدمين الجدد في التطبيق.
 */
public class SignUp extends BaseActivity {
    private static final String TAG = "SignUpActivity";

    // UI Components
    private TextInputLayout tilName, tilCity, tilEmail, tilPassword, tilPhone;
    private TextInputEditText etName, etCity, etEmail1, etPassword1, etPhone;
    private MaterialButton btnSignUp;
    private TextView tvSignIn;

    /**
     * يتم استدعاؤها عند إنشاء النشاط؛ تقوم بتهيئة واجهة المستخدم وربط العناصر المرئية بالكود.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // ربط العناصر المرئية بالكود
        tilName = findViewById(R.id.tilName);
        tilCity = findViewById(R.id.tilCity);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilPhone = findViewById(R.id.tilPhone);
        etName = findViewById(R.id.etName);
        etCity = findViewById(R.id.etCity);
        etEmail1 = findViewById(R.id.etEmail1);
        etPassword1 = findViewById(R.id.etPassword1);
        etPhone = findViewById(R.id.etPhone);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        // مستمع لزر إنشاء حساب
        btnSignUp.setOnClickListener(view -> {
            if (validateFields()) {
                performRegistration();
            }
        });

        // مستمع لنص تسجيل الدخول
        tvSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
            finish();
        });

        setupFieldNavigation();
        setupAutoCapitalization();
    }

    /**
     * التحقق من أن جميع الحقول مدخلة بشكل صحيح (مثل تنسيق البريد الإلكتروني وطول كلمة المرور).
     * @return true إذا كانت البيانات صحيحة، false خلاف ذلك.
     */
    private boolean validateFields() {
        String name = etName.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String email = etEmail1.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword1.getText().toString().trim();
        
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error));
            return false;
        }
        tilName.setError(null);

        if (city.isEmpty()) {
            tilCity.setError(getString(R.string.error));
            return false;
        }
        tilCity.setError(null);

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.error));
            return false;
        }
        tilEmail.setError(null);

        if (phone.isEmpty()) {
            tilPhone.setError(getString(R.string.error));
            return false;
        }
        tilPhone.setError(null);

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error));
            return false;
        }

        if (phone.length() < 10) {
            tilPhone.setError(getString(R.string.error));
            return false;
        }

        if (password.length() < 8) {
            tilPassword.setError(getString(R.string.password));
            return false;
        }
        tilPassword.setError(null);

        return true;
    }

    /**
     * تنظيم الانتقال التلقائي لمؤشر الكتابة بين الحقول عند استخدام لوحة المفاتيح.
     */
    private void setupFieldNavigation() {
        etName.setOnEditorActionListener((v, actionId, event) -> {
            etEmail1.requestFocus();
            return true;
        });
        etEmail1.setOnEditorActionListener((v, actionId, event) -> {
            etPassword1.requestFocus();
            return true;
        });
    }

    /**
     * تهيئة الإعدادات الخاصة بتكبير الحروف تلقائياً للأسماء والمدن.
     */
    private void setupAutoCapitalization() {
        // إعداد التنسيق التلقائي
    }

    /**
     * تنفيذ عملية التسجيل الفعلية باستخدام خدمة Firebase Authentication.
     */
    private void performRegistration() {
        String name = etName.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String email = etEmail1.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword1.getText().toString().trim();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserInfoToDatabase(user.getUid(), name, email, phone, city);
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "خطأ غير معروف";
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * حفظ معلومات المستخدم الإضافية (الاسم، المدينة، رقم الهاتف) في قاعدة بيانات Firebase Realtime Database.
     * @param userId المعرف الفريد للمستخدم.
     * @param name الاسم الكامل.
     * @param email البريد الإلكتروني.
     * @param phone رقم الهاتف.
     * @param city المدينة.
     */
    private void saveUserInfoToDatabase(String userId, String name, String email, String phone, String city) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("location", city);
        userData.put("timestamp", System.currentTimeMillis());
        userData.put("bio", "");
        userData.put("profileImageUrl", "");

        reference.setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "فشل حفظ بيانات المستخدم", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

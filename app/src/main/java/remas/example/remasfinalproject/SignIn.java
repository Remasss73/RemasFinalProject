package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Seeker.Seekers;

/**
 * نشاط تسجيل الدخول للمستخدمين المسجلين مسبقاً في التطبيق.
 */
public class SignIn extends BaseActivity {
    private TextView tv_ForgotPassword;
    private TextView tv_CreateAccount;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnSignIn;

    /**
     * يتم استدعاؤها عند إنشاء النشاط؛ تقوم بتهيئة الواجهة، والتحقق مما إذا كان المستخدم مسجلاً بالفعل للدخول التلقائي.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // التحقق من وجود مستخدم مسجل دخول مسبقاً للانتقال للشاشة الرئيسية مباشرة
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
           Intent i = new Intent(SignIn.this, HomeScreen.class);
            startActivity(i);
            finish();
        }

        // تهيئة مكونات واجهة المستخدم (Material Design)
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tv_ForgotPassword = findViewById(R.id.tvForgotPassword);
        tv_CreateAccount = findViewById(R.id.tvCreateAccount);
        
        // إعداد مستمعي النقرات للأزرار والنصوص
        tv_ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, ForgottenPassword.class);
                startActivity(i);
            }
        });
        
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
        
        tv_CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
            }
        });
        
        // إعداد التنسيق التلقائي لنصوص الإدخال
        setupAutoCapitalization();
    }

    /**
     * التحقق من صحة المدخلات (البريد وكلمة المرور) وتنفيذ عملية تسجيل الدخول عبر Firebase.
     * @return true إذا كانت الحقول صحيحة، false خلاف ذلك.
     */
    private boolean validateFields() {
        boolean isValid = true;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // التحقق من البريد الإلكتروني
        if (email.isEmpty()) {
            tilEmail.setError("البريد الإلكتروني مطلوب");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("يرجى إدخال بريد إلكتروني صحيح");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // التحقق من كلمة المرور
        if (password.isEmpty()) {
            tilPassword.setError("كلمة المرور مطلوبة");
            isValid = false;
        } else if (password.length() < 8) {
            tilPassword.setError("يجب أن تكون كلمة المرور 8 أحرف على الأقل");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        // تنفيذ تسجيل الدخول إذا كانت البيانات صحيحة
        if (isValid) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(SignIn.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignIn.this, HomeScreen.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(SignIn.this, "فشل تسجيل الدخول", Toast.LENGTH_SHORT).show();
                        tilEmail.setError(task.getException().getMessage());
                    }
                }
            });
        }
        return isValid;
    }

    /**
     * إعداد مستمع للنص لضمان تحويل البريد الإلكتروني إلى حروف صغيرة (lowercase) تلقائياً أثناء الكتابة.
     */
    private void setupAutoCapitalization() {
        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lowerCase = s.toString().toLowerCase();
                if (!s.toString().equals(lowerCase)) {
                    etEmail.removeTextChangedListener(this);
                    etEmail.setText(lowerCase);
                    etEmail.setSelection(lowerCase.length());
                    etEmail.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
}

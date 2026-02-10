package remas.example.remasfinalproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Seeker.Seekers;

public class SignUp extends AppCompatActivity {
    private TextView tv_R;
    private TextView tv_Create;
    private TextView tv_ChitChat;
    private TextView tv_Name;
    private EditText et_Name;
    private TextView tv_Age;
    private EditText et_Age;
    private TextView tv_City;
    private EditText et_City;
    private TextView tv_Email1;
    private EditText et_Email1;
    private TextView tv_Password1;
    private EditText et_Password1;
    private Button btn_SignUp;
    private TextView tv_AlreadyHaveAnAccount;
    private TextView tv_SignIn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tv_R = findViewById(R.id.tvR);
        tv_Create = findViewById(R.id.tvCreate);
        tv_ChitChat = findViewById(R.id.tvChitChat);
        tv_Name = findViewById(R.id.tvName);
        et_Name = findViewById(R.id.etName);
        tv_Age = findViewById(R.id.tvAge);
        et_Age = findViewById(R.id.etAge);
        tv_City = findViewById(R.id.tvCity);
        et_City = findViewById(R.id.etCity);
        tv_Email1 = findViewById(R.id.tvEmail1);
        et_Email1 = findViewById(R.id.etEmail1);
        tv_Password1 = findViewById(R.id.tvPassword1);
        et_Password1 = findViewById(R.id.etPassword1);
        btn_SignUp = findViewById(R.id.btnSignUp);

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
        tv_AlreadyHaveAnAccount = findViewById(R.id.tvAlreadyHaveAnAccount);
        tv_SignIn = findViewById(R.id.tvSignIn);

        tv_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SignUp.this, HomeScreen.class);
                startActivity(i);
            }
        });
        {

        }
        ;
    }


    /**
     * Validates the user input fields
     *
     * @return true if the fields are valid, false otherwise
     */
    private boolean validateFields() {
        boolean isValid = true;

        String name = et_Name.getText().toString().trim();
        String ageText = et_Age.getText().toString().trim();
        String city = et_City.getText().toString().trim();
        String email = et_Email1.getText().toString().trim();
        String password = et_Password1.getText().toString().trim();

        if (name.isEmpty()) {
            et_Name.setError("Name is required");
            isValid = false;
        } else {
            et_Name.setError(null);
        }

        if (ageText.isEmpty()) {
            et_Age.setError("Age is required");
            isValid = false;
        } else if (!TextUtils.isDigitsOnly(ageText)) {
            et_Age.setError("Age must be a number");
            isValid = false;
        } else {
            et_Age.setError(null);
        }

        if (city.isEmpty()) {
            et_City.setError("City is required");
            isValid = false;
        } else {
            et_City.setError(null);
        }

        if (email.isEmpty()) {
            et_Email1.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_Email1.setError("Please enter a valid email address");
            isValid = false;
        } else {
            et_Email1.setError(null);
        }

        if (password.isEmpty()) {
            et_Password1.setError("Password is required");
            isValid = false;
        } else if (password.length() < 8) {
            et_Password1.setError("Password must be at least 8 characters long");
            isValid = false;
        } else {
            et_Password1.setError(null);
        }
        if (isValid) {
            Seekers seeker = new Seekers();
            seeker.setFullName(name);
            seeker.setAge(Integer.parseInt(ageText));
            seeker.setCity(city);
            seeker.setEmail(email);
            seeker.setPassword(password);


            // Save to local database on a background thread (Room requirement)
            saveUser(seeker);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        AppDatabase db = AppDatabase.getDB(SignUp.this);
                        db.getSeekersQuery().insert(seeker);
                        saveUser(seeker);
                        Toast.makeText(SignUp.this, "Signing Up Succeeded", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignUp.this, HomeScreen.class);
                        startActivity(i);

                    } else {
                        Toast.makeText(SignUp.this, "Signing Up Failed", Toast.LENGTH_SHORT).show();
                        et_Email1.setError(task.getException().getMessage());
                    }




                }
            });
        }
        return isValid;
    }
    public void saveUser(Seekers user) {// الحصول على مرجع إلى عقدة "users" في قاعدة البيانات

        // تهيئة Firebase Realtime Database    //مؤشر لقاعدة البيانات
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
// ‏مؤشر لجدول المستعملين
        DatabaseReference usersRef = database.child("seekes");
        // إنشاء مفتاح فريد للمستخدم الجديد
        DatabaseReference newUserRef = usersRef.push();
        // تعيين معرف المستخدم في كائن MyUser
        user.setUserId(newUserRef.getKey());
        // حفظ بيانات المستخدم في قاعدة البيانات
        //اضافة كائن "لمجموعة" المستعملين ومعالج حدث لفحص نجاح المطلوب
       // معالج حدث لفحص هل تم المطلوب من قاعدة البيانات //
        newUserRef.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUp.this, "Succeeded to add User",  Toast.LENGTH_SHORT).show();
                        finish();




                        // تم حفظ البيانات بنجاح
                        Log.d(TAG, "تم حفظ المستخدم بنجاح: " + user.getUserId());
                        // تحديث واجهة المستخدم أو تنفيذ إجراءات أخرى
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // معالجة الأخطاء
                        Log.e(TAG, "خطأ في حفظ المستخدم: " + e.getMessage(), e);
                        Toast.makeText(SignUp.this, "Failed to add User", Toast.LENGTH_SHORT).show();
                        // عرض رسالة خطأ للمستخدم
                    }
                });
    }

}

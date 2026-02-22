package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Seeker.Seekers;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private EditText et_Name, et_Age, et_City, et_Email1, et_Password1;
    private Button btn_SignUp;
    private TextView tv_SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 1. Initialize Views
        et_Name = findViewById(R.id.etName);
        et_Age = findViewById(R.id.etAge);
        et_City = findViewById(R.id.etCity);
        et_Email1 = findViewById(R.id.etEmail1);
        et_Password1 = findViewById(R.id.etPassword1);
        btn_SignUp = findViewById(R.id.btnSignUp);
        tv_SignIn = findViewById(R.id.tvSignIn);

        // 2. Sign Up Action
        btn_SignUp.setOnClickListener(view -> validateFields());

        // 3. Navigate to Sign In screen manually
        tv_SignIn.setOnClickListener(view -> {
            startActivity(new Intent(SignUp.this, HomeScreen.class));
            finish();
        });
    }

    private void validateFields() {
        String name = et_Name.getText().toString().trim();
        String ageStr = et_Age.getText().toString().trim();
        String city = et_City.getText().toString().trim();
        String email = et_Email1.getText().toString().trim();
        String password = et_Password1.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || city.isEmpty() || email.isEmpty() || password.length() < 8) {
            Toast.makeText(this, "Please fill all fields correctly (Password min 8 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        Seekers seeker = new Seekers();
        seeker.setFullName(name);
        seeker.setAge(Integer.parseInt(ageStr));
        seeker.setCity(city);
        seeker.setEmail(email);
        seeker.setPassword(password);

        performRegistration(seeker);
    }

    private void performRegistration(Seekers seeker) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(seeker.getEmail(), seeker.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save to Local Room DB (Must be background thread)
                        new Thread(() -> {
                            AppDatabase.getDB(SignUp.this).getSeekersQuery().insert(seeker);
                        }).start();

                        // Save to Firebase Cloud
                        saveUserToFirebase(seeker);
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "Auth Failed";
                        Toast.makeText(SignUp.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirebase(Seekers user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("seekers");
        String key = usersRef.push().getKey();
        user.setUserId(key);

        if (key != null) {
            usersRef.child(key).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUp.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to Home Screen and Clear History
                        Intent intent = new Intent(SignUp.this, HomeScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Firebase DB Error: " + e.getMessage());
                        Toast.makeText(SignUp.this, "Cloud Sync Failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}

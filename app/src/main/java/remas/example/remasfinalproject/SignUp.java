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

public class SignUp extends BaseActivity {
    private static final String TAG = "SignUpActivity";

    // UI Components
    private TextInputLayout tilName, tilCity, tilEmail, tilPassword, tilPhone;
    private TextInputEditText etName, etCity, etEmail1, etPassword1, etPhone;
    private MaterialButton btnSignUp;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize all UI views
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

        btnSignUp.setOnClickListener(view -> {
            if (validateFields()) {
                performRegistration();
            }
        });

        tvSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
            finish();
        });

        setupFieldNavigation();
        setupAutoCapitalization();
    }

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

    private void setupAutoCapitalization() {
        // Simple auto-cap setup
    }

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
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

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
                Toast.makeText(this, "Failed to save user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

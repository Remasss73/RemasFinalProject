package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPassword extends AppCompatActivity {

    // UI Components
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnResetPassword;
    private TextView tvBackToLogin, tvSuccessMessage;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        initializeViews();
        setupClickListeners();
        setupAutoCapitalization();
    }

    private void initializeViews() {
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);
    }

    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(v -> {
            if (validateEmail()) {
                sendPasswordResetEmail();
            }
        });

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgottenPassword.this, SignIn.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            return false;
        }

        tilEmail.setError(null);
        return true;
    }

    private void sendPasswordResetEmail() {
        String email = etEmail.getText().toString().trim();

        // Show loading state
        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Sending...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    // Restore button state
                    btnResetPassword.setEnabled(true);
                    btnResetPassword.setText("Send Reset Link");

                    if (task.isSuccessful()) {
                        // Show success message
                        tilEmail.setVisibility(android.view.View.GONE);
                        btnResetPassword.setVisibility(android.view.View.GONE);
                        tvSuccessMessage.setVisibility(android.view.View.VISIBLE);
                        
                        Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        
                        // Auto-navigate back to sign in after 3 seconds
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(ForgottenPassword.this, SignIn.class);
                            startActivity(intent);
                            finish();
                        }, 3000);
                    } else {
                        // Show error message
                        String errorMessage = task.getException() != null ? 
                                task.getException().getMessage() : "Failed to send reset email";
                        tilEmail.setError(errorMessage);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupAutoCapitalization() {
        // Email field - lowercase all letters (standard email format)
        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Convert to lowercase as user types
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
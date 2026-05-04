package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Seeker.Seekers;

public class SignIn extends AppCompatActivity {
    private TextView tv_ForgotPassword;
    private TextView tv_CreateAccount;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
           Intent i = new Intent(SignIn.this, HomeScreen.class);
            startActivity(i);
            finish();
        }
        // Initialize Material Design components
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tv_ForgotPassword = findViewById(R.id.tvForgotPassword);
        tv_CreateAccount = findViewById(R.id.tvCreateAccount);
        
        // Set click listeners
        tv_ForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, ForgottenPassword.class);
                startActivity(i);
            }
        });
        
        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
        
        tv_CreateAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
            }
        });
        
        // Set up auto-capitalization for input fields
        setupAutoCapitalization();
    }
    /**
     * Validates the user input fields
     * @return true if the fields are valid, false otherwise
     */
    private boolean validateFields()
    {
        boolean isValid = true;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty())
        {
            tilEmail.setError("Email is required");
            isValid = false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        else
        {
            tilEmail.setError(null);
        }

        if (password.isEmpty())
        {
            tilPassword.setError("Password is required");
            isValid = false;
        }
        else if (password.length() < 8)
        {
            tilPassword.setError("Password must be at least 8 characters long");
            isValid = false;
        }
        else
        {
            tilPassword.setError(null);
        }
        if (isValid)
        {
            Seekers seeker=new Seekers();
            seeker.setEmail(email);
            seeker.setPassword(password);







            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(SignIn.this, "Signing In Succeeded", Toast.LENGTH_SHORT).show();
                        AppDatabase db = AppDatabase.getDB(SignIn.this);
                        Seekers sk = db.getSeekersQuery().checkEmailPassword(email, password);
                        if(sk!=null)
                        {
                            Toast.makeText(SignIn.this, "Signing In Succeeded", Toast.LENGTH_SHORT).show();

                        }
                        Intent i = new Intent(SignIn.this, HomeScreen.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Toast.makeText(SignIn.this, "Signing In Failed", Toast.LENGTH_SHORT).show();
                        tilEmail.setError(task.getException().getMessage());
                    }
                }

            });
        }
        return isValid;
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
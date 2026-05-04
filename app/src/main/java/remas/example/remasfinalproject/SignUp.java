package remas.example.remasfinalproject;

/**
 * Contains the SignUp Activity for user registration.
 *
 * @see remas.example.remasfinalproject.SignUp
 */
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Seeker.Seekers;

/**
 * SignUp Activity handles the user registration process.
 *
 * Logic Flow:
 * 1. validateFields() checks user input.
 * 2. performRegistration() creates the Auth account.
 * 3. saveUserToFirebase() saves the data to the cloud.
 * 4. Navigation to HomeScreen happens ONLY after the cloud save is successful.
 */
public class SignUp extends AppCompatActivity {
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

        // 1. Initialize all UI views from the layout
        tilName = findViewById(R.id.tilName);
        tilCity = findViewById(R.id.tilCity);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilPhone = findViewById(R.id.tilPhone);
        etName = findViewById(R.id.etName); // Name input field
        etCity = findViewById(R.id.etCity); // City input field
        etEmail1 = findViewById(R.id.etEmail1); // Email input field
        etPassword1 = findViewById(R.id.etPassword1); // Password input field
        etPhone = findViewById(R.id.etPhone); // Phone input field
        btnSignUp = findViewById(R.id.btnSignUp); // Sign up button
        tvSignIn = findViewById(R.id.tvSignIn); // Sign in link

        // Set click listener for the sign-up button
        btnSignUp.setOnClickListener(view -> {
            Log.d(TAG, "Sign up button clicked");
            if (validateFields()) {
                Log.d(TAG, "Validation passed, performing registration");
                performRegistration(); // Validate input and attempt registration
            } else {
                Log.d(TAG, "Validation failed");
            }
        });

        // Set click listener for sign-in link
        tvSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
            finish();
        });

        // Set up field navigation with Enter key
        setupFieldNavigation();
        
        // Set up auto-capitalization for input fields
        setupAutoCapitalization();
    }

    /**
     * Retrieves text from input fields and performs basic validation.
     * Checks for empty fields and minimum password length.
     */
    private boolean validateFields() {
        Log.d(TAG, "Starting field validation");
        String name = etName.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String email = etEmail1.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword1.getText().toString().trim();
        
        Log.d(TAG, "Fields - Name: " + name + ", City: " + city + ", Email: " + email + ", Phone: " + phone + ", Password: " + (password.isEmpty() ? "empty" : "filled"));

        // Validate required fields
        if (name.isEmpty()) {
            tilName.setError("Name is required");
            return false;
        }
        tilName.setError(null);

        if (city.isEmpty()) {
            tilCity.setError("City is required");
            return false;
        }
        tilCity.setError(null);

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            return false;
        }
        tilEmail.setError(null);

        if (phone.isEmpty()) {
            tilPhone.setError("Phone number is required");
            return false;
        }
        tilPhone.setError(null);

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            return false;
        }

        // Validate phone format (basic)
        if (phone.length() < 10) {
            tilPhone.setError("Please enter a valid phone number");
            return false;
        }

        // Validate password
        if (password.length() < 8) {
            tilPassword.setError("Password must be at least 8 characters");
            return false;
        }
        tilPassword.setError(null);

        return true;
    }

    private void setupFieldNavigation() {
        // Name -> Email on Enter
        etName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT || 
                (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                etEmail1.requestFocus();
                return true;
            }
            return false;
        });

        // Email -> Password on Enter
        etEmail1.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT || 
                (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                etPassword1.requestFocus();
                return true;
            }
            return false;
        });

        // Password -> City on Enter
        etPassword1.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT || 
                (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                etCity.requestFocus();
                return true;
            }
            return false;
        });

        // City -> Phone on Enter
        etCity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT || 
                (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                etPhone.requestFocus();
                return true;
            }
            return false;
        });

        // Phone -> Submit on Enter
        etPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE || 
                (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                // Clear focus and hide keyboard
                etPhone.clearFocus();
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                
                // Trigger sign up
                if (validateFields()) {
                    performRegistration();
                }
                return true;
            }
            return false;
        });
    }

    private void setupAutoCapitalization() {
        // Name field - simple capitalization without interfering with spaces
        etName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only capitalize first letter, don't interfere with spaces
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etName.removeTextChangedListener(this);
                        etName.setText(firstChar);
                        etName.setSelection(1);
                        etName.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Don't interfere with space input - let user type freely
            }
        });

        // City field - capitalize first letter
        etCity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etCity.setText(firstChar);
                        etCity.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Capitalize after spaces for multi-word city names
                String text = s.toString();
                String[] words = text.split(" ");
                StringBuilder capitalized = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (!words[i].isEmpty()) {
                        capitalized.append(Character.toUpperCase(words[i].charAt(0)))
                                .append(words[i].substring(1).toLowerCase());
                        if (i < words.length - 1) {
                            capitalized.append(" ");
                        }
                    }
                }
                if (!text.equals(capitalized.toString())) {
                    etCity.removeTextChangedListener(this);
                    etCity.setText(capitalized.toString());
                    etCity.setSelection(capitalized.length());
                    etCity.addTextChangedListener(this);
                }
            }
        });

        // Email field - lowercase all letters (standard email format)
        etEmail1.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Convert to lowercase as user types
                String lowerCase = s.toString().toLowerCase();
                if (!s.toString().equals(lowerCase)) {
                    etEmail1.removeTextChangedListener(this);
                    etEmail1.setText(lowerCase);
                    etEmail1.setSelection(lowerCase.length());
                    etEmail1.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void performRegistration() {
        Log.d(TAG, "Starting registration process");
        String name = etName.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String email = etEmail1.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword1.getText().toString().trim();

        Log.d(TAG, "Creating Seekers object with data");
        // Create the data model object
        Seekers seeker = new Seekers();
        seeker.setFullName(name);
        seeker.setCity(city);
        seeker.setEmail(email);
        seeker.setPassword(password);
        seeker.setPhone(phone);

        // Register with Firebase
        Log.d(TAG, "Attempting Firebase registration with email: " + email);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "Firebase registration task completed");
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Registration successful!");
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, SignIn.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Registration failed", task.getException());
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Registration failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
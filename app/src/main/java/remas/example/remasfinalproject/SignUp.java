package remas.example.remasfinalproject;

/**
 * Contains the SignUp Activity for user registration.
 *
 * @see remas.example.remasfinalproject.SignUp
 */
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private EditText et_Name, et_Age, et_City, et_Email1, et_Password1;
    private Button btn_SignUp;
    private TextView tv_SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 1. Initialize all UI views from the layout
        et_Name = findViewById(R.id.etName); // Name input field
        et_Age = findViewById(R.id.etAge); // Age input field
        et_City = findViewById(R.id.etCity); // City input field
        et_Email1 = findViewById(R.id.etEmail1); // Email input field
        et_Password1 = findViewById(R.id.etPassword1); // Password input field
        btn_SignUp = findViewById(R.id.btnSignUp); // Sign up button
        tv_SignIn = findViewById(R.id.tvSignIn); // Sign in link

        // Set click listener for the sign-up button
        btn_SignUp.setOnClickListener(view -> {
            validateFields(); // Validate input and attempt registration
        });
    }

    /**
     * Retrieves text from input fields and performs basic validation.
     * Checks for empty fields and minimum password length.
     */
    private boolean validateFields() {
        String name = et_Name.getText().toString().trim();
        String ageStr = et_Age.getText().toString().trim();
        String city = et_City.getText().toString().trim();
        String email = et_Email1.getText().toString().trim();
        String password = et_Password1.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || city.isEmpty() || email.isEmpty() || password.length() < 8) {
            Toast.makeText(this, "Please fill all fields (Password min 8 chars)", Toast.LENGTH_SHORT).show();
            return false;

        }
    /**
     * Validates the user input fields and performs the registration process.
     * Attempts to register the user with Firebase Authentication.
     *
     * @return true if the registration process was successful, false otherwise
     */
        // Create the data model object
        Seekers seeker = new Seekers();
        seeker.setFullName(name);
        seeker.setAge(Integer.parseInt(ageStr));
        seeker.setCity(city);
        seeker.setEmail(email);
        seeker.setPassword(password);

        performRegistration(seeker);

        return true;
    }

    /**
     * Registers the user with Firebase Authentication.
     * On success, saves to local Room DB and then triggers Cloud Save.
     *
     * @param seeker The user data object.
     */
    private void performRegistration(Seekers seeker) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(seeker.getEmail(), seeker.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Success: Save locally first on background thread
                        new Thread(() -> {
                            AppDatabase.getDB(SignUp.this).getSeekersQuery().insert(seeker);
                        }).start();

                        // Proceed to Cloud Sync
                        saveUserToFirebase(seeker);
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "Auth Failed";
                        Toast.makeText(SignUp.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Uploads the user profile to Firebase Realtime Database.
     *
     * IMPORTANT: This method handles the navigation to HomeScreen.
     * It only navigates if the database write is successful.
     *
     * @param user The Seeker object to be saved.
     */
    private void saveUserToFirebase(Seekers user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("seekers");
        String key = usersRef.push().getKey();
        user.setUserId(key);

        if (key != null) {
            usersRef.child(key).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        // DATA IS SAVED - NOW NAVIGATE
                        Toast.makeText(SignUp.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SignUp.this, HomeScreen.class);
                        // Clear the activity stack so the user cannot press "back" to the sign up page
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
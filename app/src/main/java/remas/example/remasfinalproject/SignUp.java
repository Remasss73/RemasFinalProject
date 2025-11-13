package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity
{
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

        btn_SignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this, HomeScreen.class);
                startActivity(i);
            }
        });
        tv_AlreadyHaveAnAccount = findViewById(R.id.tvAlreadyHaveAnAccount);
        tv_SignIn = findViewById(R.id.tvSignIn);

        tv_SignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this, SignIn.class);
                startActivity(i);
            }
        });
         {

        };
    }

        private void registerUser() {
            // --- 1. READ DATA FROM FIELDS ---
            String FullName = et_Name.getText().toString().trim();
            String email = et_Email1.getText().toString().trim();
            String password = et_Password1.getText().toString().trim();



            // --- 2. VALIDATE THE DATA USING A FLAG ---
            boolean isDataValid = true; // This is our validation flag

            // Validate Username
            if (com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils.isEmpty(username)) {
                et_Name.setError("Username is required.");
                isDataValid = false; // Set flag to false on failure
            } else {
                // Clear any previous error
                et_Name.setError(null);
            }

            // Validate Email
            if (com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils.isEmpty(email)) {
                et_Email1.setError("Email is required.");
                isDataValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Use Android's built-in email pattern validator
                et_Email1.setError("Please enter a valid email address.");
                isDataValid = false;
            } else {
                et_Email1.setError(null);
            }

            // Validate Password
            if (com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils.isEmpty(password)) {
                et_Password1.setError("Password is required.");
                isDataValid = false;
            } else if (password.length() < 6) {
                et_Password1.setError("Password must be at least 6 characters long.");
                isDataValid = false;
            } else {
                et_Password1.setError(null);
            }


            // --- 3. CHECK THE FLAG AND PROCEED ---
            if (isDataValid) {
                // All data is valid, proceed with registration logic
                // For example, save to a database, call an API, etc.
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                // You would add your database insert logic here, e.g.:
                // myDbHelper.addUser(username, email, password);

            } else {
                // Data is invalid, do nothing further
                Toast.makeText(this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
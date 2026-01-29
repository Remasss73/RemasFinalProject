package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Seeker.Seekers;

public class SignIn extends AppCompatActivity {
    private TextView tv_welcome;
    private TextView tv_Intro;
    private TextView tv_Email;
    private EditText et_Email;
    private TextView tv_Password;
    private  EditText et_Password;
    private  TextView tv_ForgotPassword;
    private Button btn_SignIn;
    private TextView tv_NoAccount;
    private TextView tv_CreateAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        tv_welcome = findViewById(R.id.tvWelcome);
        tv_Intro = findViewById(R.id.tvIntro);
        tv_Email = findViewById(R.id.tvEmail1);
        et_Email = findViewById(R.id.etEmail);
        tv_Password = findViewById(R.id.tvPassword1);
        et_Password = findViewById(R.id.etPassword);
        tv_ForgotPassword = findViewById(R.id.tvForgotPassword);
        tv_ForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, ForgottenPassword.class);
                startActivity(i);
            }
        });
        btn_SignIn = findViewById(R.id.btnSignIn);
        btn_SignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(validateFields()) {
                    Intent i = new Intent(SignIn.this, HomeScreen.class);
                    startActivity(i);
                }
            }
        });
        tv_NoAccount = findViewById(R.id.tvNoAccount);
        tv_CreateAccount = findViewById(R.id.tvCreateAccount);
        //
        tv_CreateAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
            }
        });
    }
    /**
     * Validates the user input fields
     * @return true if the fields are valid, false otherwise
     */
    private boolean validateFields()
    {
        boolean isValid = true;
        String email = et_Email.getText().toString().trim();
        String password = et_Password.getText().toString().trim();

        if (email.isEmpty())
        {
            et_Email.setError("Email is required");
            isValid = false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            et_Email.setError("Please enter a valid email address");
            isValid = false;
        }
        else
        {
            et_Email.setError(null);
        }

        if (password.isEmpty())
        {
            et_Password.setError("Password is required");
            isValid = false;
        }
        else if (password.length() < 8)
        {
            et_Password.setError("Password must be at least 8 characters long");
            isValid = false;
        }
        else
        {
            et_Password.setError(null);
        }
        if (isValid)
        {
            Seekers seeker=new Seekers();
            seeker.setEmail(email);
            seeker.setPassword(password);


            AppDatabase db = AppDatabase.getDB(SignIn.this);
            db.getSeekersQuery().insert(seeker);
            Toast.makeText(SignIn.this, "You have registered successfully", Toast.LENGTH_SHORT).show();
            finish();//close current activity (return immediately to the previous activity)


        }
        else
        {
            Toast.makeText(SignIn.this, "User registration failed", Toast.LENGTH_SHORT).show();
        }


        if (isValid)
        {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(SignIn.this, "Signing In Succeded", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignIn.this, HomeScreen.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(SignIn.this, "Signing In Failed", Toast.LENGTH_SHORT).show();
                        et_Email.setError(task.getException().getMessage());
                    }
                }
            });
        }
        return isValid;
    }
}
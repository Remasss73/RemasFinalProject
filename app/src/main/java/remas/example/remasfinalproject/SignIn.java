package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
                Intent i = new Intent(SignIn.this, HomeScreen.class);
                startActivity(i);
            }
        });
        tv_NoAccount = findViewById(R.id.tvNoAccount);
        tv_CreateAccount = findViewById(R.id.tvCreateAccount);
        tv_CreateAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
            }
        });




    }
}
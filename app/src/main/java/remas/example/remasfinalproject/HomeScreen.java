package remas.example.remasfinalproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeScreen extends AppCompatActivity
{
    private TextView tv_Rumi;
    private TextView tv_Filter;
    private TextView tv_Rent;
    private TextView tv_City1;
    private TextView tv_Rent1;
    private TextView tv_City2;
    private TextView tv_Home;
    private TextView tv_Matches;
    private TextView tv_Add;
    private TextView tv_Profile;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        tv_Rumi= findViewById(R.id.tvRumi);
        tv_Filter = findViewById(R.id.tvFilter);
        tv_Rent = findViewById(R.id.tvRent);
        tv_City1 = findViewById(R.id.tvCity1);
        tv_Rent1 = findViewById(R.id.tvRent1);
        tv_City2 = findViewById(R.id.tvCity2);
        tv_Home = findViewById(R.id.tvHome);
        tv_Matches = findViewById(R.id.tvMatches);
        tv_Add = findViewById(R.id.tvAdd);
        tv_Profile = findViewById(R.id.tvProfile);


        

        };
    }

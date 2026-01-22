package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class DormActivity extends AppCompatActivity {

    private EditText etCity;
    private EditText etAddress;
    private EditText etZipcode;
    private EditText etRent;
    private EditText etAmenities;
    private EditText etDescription;
    private EditText etStatus;
    private MaterialButton btnSaveDorm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dorm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etZipcode = findViewById(R.id.etZipcode);
        etRent = findViewById(R.id.etRent);
        etAmenities = findViewById(R.id.etAmenities);
        etDescription = findViewById(R.id.etDescription);
        etStatus = findViewById(R.id.etStatus);
        btnSaveDorm = findViewById(R.id.btnSaveDorm);
    }

    private void setupClickListeners() {
        btnSaveDorm.setOnClickListener(v -> {
            validateInputs();
            AddListing();
        });
    }

    public void onClick(View view) {

        Intent i = new Intent(DormActivity.this, DormsListActivity.class);
        startActivity(i);
    }


    private void AddListing() {
        // Get values from EditText fields
        String city = etCity.getText().toString();
        String address = etAddress.getText().toString();
        String zipcode = etZipcode.getText().toString();
        String rent = etRent.getText().toString();
        String amenities = etAmenities.getText().toString();
        String description = etDescription.getText().toString();
        String status = etStatus.getText().toString();


        // TODO: Implement save logic
    }
    private void validateInputs() {
        if (etCity.getText().toString().isEmpty()) {
            etCity.setError("City is required");
            return;
        }
        if (etAddress.getText().toString().isEmpty()) {
            etAddress.setError("Address is required");
            return;
        }
        if (etZipcode.getText().toString().isEmpty()) {
            etZipcode.setError("Zipcode is required");
            return;
        }
        if (etRent.getText().toString().isEmpty()) {
            etRent.setError("Rent is required");
            return;
        }
        if (etAmenities.getText().toString().isEmpty()) {
            etAmenities.setError("Amenities is required");
            return;
        }
        if (etDescription.getText().toString().isEmpty()) {
            etDescription.setError("Description is required");
            return;
        }
        if (etStatus.getText().toString().isEmpty()) {
            etStatus.setError("Status is required");
            return;
        }
        // TODO: Implement validation logic
    }

}
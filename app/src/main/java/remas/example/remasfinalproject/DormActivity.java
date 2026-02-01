package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Dorm.Dorms;

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
        {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(SignIn.this, .class);
            startActivity(i);
        }
        });
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

    private boolean validateInputs() {
        if (etCity.getText().toString().isEmpty()) {
            etCity.setError("City is required");
            return false;
        }
        if (etAddress.getText().toString().isEmpty()) {
            etAddress.setError("Address is required");
            return false;
        }
        if (etZipcode.getText().toString().isEmpty()) {
            etZipcode.setError("Zipcode is required");
            return false;
        }
        if (etRent.getText().toString().isEmpty()) {
            etRent.setError("Rent is required");
            return false;
        }
        if (etAmenities.getText().toString().isEmpty()) {
            etAmenities.setError("Amenities is required");
            return false;
        }
        if (etDescription.getText().toString().isEmpty()) {
            etDescription.setError("Description is required");
            return false;
        }
        if (etStatus.getText().toString().isEmpty()) {
            etStatus.setError("Status is required");
            return false;
        }
        return false;
    }

    private void PublicListing() {
        if (validateInputs()) {
            String City = etCity.getText().toString();
            String Address = etAddress.getText().toString();
            String Zipcode = etZipcode.getText().toString();
            String Rent = etRent.getText().toString();
            String Amenities = etAmenities.getText().toString();
            String Description = etDescription.getText().toString();
            String Status = etStatus.getText().toString();

            Dorms dorm = new Dorms();
            dorm.setCity(City);
            dorm.setAddress(Address);
            dorm.setZipcode(Zipcode);
            dorm.setRent(Rent);
            dorm.setAmenities(Amenities);
            dorm.setDescription(Description);
            dorm.setStatus(Status);

            AppDatabase db = AppDatabase.getDB(getApplication());
            db.getDormQuery().insert(dorm);

            String msg = "Listing Published" + City + Address + Zipcode + Rent + Amenities + Description + Status;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();


        }
        // TODO: Implement validation logic
    }
}


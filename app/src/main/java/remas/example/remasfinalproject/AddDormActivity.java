package remas.example.remasfinalproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Dorm.Dorms;
import remas.example.remasfinalproject.data.Seeker.Seekers;

/**
 * Activity for adding new dormitory listings to the Remas application.
 * This activity provides a form for users to input dormitory details
 * and saves them to both the local Room database and Firebase Realtime Database.
 */
public class AddDormActivity extends AppCompatActivity {

    // UI components for dormitory input fields
    private EditText etCity; // City input field
    private EditText etAddress; // Address input field
    private EditText etZipcode; // Zipcode input field
    private EditText etRent; // Rent amount input field
    private EditText etAmenities; // Amenities input field
    private EditText etDescription; // Description input field
    private EditText etStatus; // Status input field
    private MaterialButton btnSaveDorm; // Save button


    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up the activity layout.
     *
     * @param savedInstanceState If the activity is being re-initialized after 
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_dorm); // Set the activity layout
        
        // Apply window insets for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews(); // Initialize all UI components
    }

    /**
     * Initializes all UI components by finding them from the layout.
     * Also sets up the click listener for the save button.
     */
    private void initializeViews() {
        // Find all EditText components from the layout
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etZipcode = findViewById(R.id.etZipcode);
        etRent = findViewById(R.id.etRent);
        etAmenities = findViewById(R.id.etAmenities);
        etDescription = findViewById(R.id.etDescription);
        etStatus = findViewById(R.id.etStatus);
        btnSaveDorm = findViewById(R.id.btnSaveDorm);

        // Set click listener for the save button
        btnSaveDorm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicListing(); // Call method to save the dormitory listing
            }
        });

    }


    /**
     * Handles click events for navigation to the dormitory list activity.
     * This method appears to be unused in the current implementation.
     *
     * @param view The view that was clicked
     */
    public void onClick(View view) {
        // Create intent to navigate to dormitory list activity
        Intent i = new Intent(AddDormActivity.this, DormsListActivity.class);
        startActivity(i);
    }


    /**
     * Placeholder method for adding a dormitory listing.
     * Currently contains only TODO comment and is not implemented.
     * This method appears to be unused in favor of PublicListing().
     */
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

    /**
     * Validates all input fields to ensure they are not empty.
     * Sets error messages on empty fields and returns validation result.
     *
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateInputs() {
        // Validate each required field
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
            etAmenities.setError("Amenities are required");
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
        return true; // All fields are valid
    }

    /**
     * Validates input and saves the dormitory listing to the local Room database.
     * Creates a new Dorms object with the input data and inserts it into the database.
     * Shows a success message and closes the activity upon successful save.
     */
    private void PublicListing() {
        if (validateInputs()) { // First validate all input fields
            // Get text from all input fields
            String City = etCity.getText().toString();
            String Address = etAddress.getText().toString();
            String Zipcode = etZipcode.getText().toString();
            String Rent = etRent.getText().toString();
            String Amenities = etAmenities.getText().toString();
            String Description = etDescription.getText().toString();
            String Status = etStatus.getText().toString();

            // Create new Dorms object and set its properties
            Dorms dorm = new Dorms();
            dorm.setCity(City);
            dorm.setAddress(Address);
            dorm.setZipcode(Zipcode);
            dorm.setRent(Rent);
            dorm.setAmenities(Amenities);
            dorm.setDescription(Description);
            dorm.setStatus(Status);

            // Get database instance and insert the new dormitory
            AppDatabase db = AppDatabase.getDB(getApplication());
            db.getDormQuery().insert(dorm);

            // Create success message with dormitory details
            String msg = "Listing Published: " + City + ", " + Address + ", " + Zipcode + ", " + Rent + ", " + Amenities + ", " + Description + ", " + Status;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }
        // TODO: Implement validation logic (Note: validation is already implemented above)
    }

    /**
     * Saves the dormitory listing to Firebase Realtime Database.
     * This method handles the Firebase synchronization for the dormitory data.
     *
     * @param dorm The Dorms object to be saved to Firebase
     */
    public void saveListing(Dorms dorm) {
        // Get reference to the "seekers" node in Firebase Realtime Database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("seekers"); // Reference to users table
        
        // Create a unique key for the new dormitory listing
        DatabaseReference newUserRef = usersRef.push();
        
        // Set the generated unique ID in the Dorms object
        dorm.setDormId(newUserRef.getKey());
        
        // Save the dormitory data to Firebase with success/failure listeners
        newUserRef.setValue(dorm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data saved successfully
                        Toast.makeText(AddDormActivity.this, "Successfully added dormitory", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                        Log.d(TAG, "Dormitory saved successfully: " + dorm.getDormId());
                        // Update UI or perform other actions as needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error
                        Log.e(TAG, "Error saving dormitory: " + e.getMessage(), e);
                        Toast.makeText(AddDormActivity.this, "Failed to add dormitory", Toast.LENGTH_SHORT).show();
                        // Display error message to user
                    }
                });
    }
}


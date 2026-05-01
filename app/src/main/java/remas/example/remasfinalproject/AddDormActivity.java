package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDormActivity extends AppCompatActivity {
    
    // UI Components
    private ImageView ivBack, ivSave, ivAddPhoto;
    private TextInputLayout tilTitle, tilCity, tilArea, tilAddress, tilSize, tilPrice, tilDescription;
    private TextInputEditText etTitle, etCity, etArea, etAddress, etSize, etPrice, etDescription;
    private TextView tvBedrooms, tvBathrooms;
    private ImageButton ibMinusBedrooms, ibPlusBedrooms, ibMinusBathrooms, ibPlusBathrooms;
    private MaterialButton btnSaveDraft, btnPublish;
    
    // Data
    private int bedrooms = 1;
    private int bathrooms = 1;
    private boolean isEditMode = false;
    private String listingId = null;
    
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dorm);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        // Check if editing
        checkEditMode();
        
        // Initialize UI
        initializeViews();
        setupClickListeners();
        
        // Load data if editing
        if (isEditMode) {
            loadListingData();
        }
    }
    
    private void checkEditMode() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEdit", false);
        if (isEditMode) {
            listingId = intent.getStringExtra("listingId");
        }
    }
    
    private void initializeViews() {
        // Header
        ivBack = findViewById(R.id.ivBack);
        ivSave = findViewById(R.id.ivSave);
        
        // Input fields
        tilTitle = findViewById(R.id.tilTitle);
        tilCity = findViewById(R.id.tilCity);
        tilArea = findViewById(R.id.tilArea);
        tilAddress = findViewById(R.id.tilAddress);
        tilSize = findViewById(R.id.tilSize);
        tilPrice = findViewById(R.id.tilPrice);
        tilDescription = findViewById(R.id.tilDescription);
        
        etTitle = findViewById(R.id.etTitle);
        etCity = findViewById(R.id.etCity);
        etArea = findViewById(R.id.etArea);
        etAddress = findViewById(R.id.etAddress);
        etSize = findViewById(R.id.etSize);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        
        // Bedroom/Bathroom controls
        tvBedrooms = findViewById(R.id.tvBedrooms);
        tvBathrooms = findViewById(R.id.tvBathrooms);
        ibMinusBedrooms = findViewById(R.id.ibMinusBedrooms);
        ibPlusBedrooms = findViewById(R.id.ibPlusBedrooms);
        ibMinusBathrooms = findViewById(R.id.ibMinusBathrooms);
        ibPlusBathrooms = findViewById(R.id.ibPlusBathrooms);
        
        // Photo
        ivAddPhoto = findViewById(R.id.ivAddPhoto);
        
        // Buttons
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnPublish = findViewById(R.id.btnPublish);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        ivSave.setOnClickListener(v -> saveDraft());
        
        ivAddPhoto.setOnClickListener(v -> {
            // TODO: Implement photo upload
            Toast.makeText(this, "Photo upload coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        // Bedroom controls
        ibMinusBedrooms.setOnClickListener(v -> {
            if (bedrooms > 0) {
                bedrooms--;
                tvBedrooms.setText(String.valueOf(bedrooms));
            }
        });
        
        ibPlusBedrooms.setOnClickListener(v -> {
            if (bedrooms < 10) {
                bedrooms++;
                tvBedrooms.setText(String.valueOf(bedrooms));
            }
        });
        
        // Bathroom controls
        ibMinusBathrooms.setOnClickListener(v -> {
            if (bathrooms > 0) {
                bathrooms--;
                tvBathrooms.setText(String.valueOf(bathrooms));
            }
        });
        
        ibPlusBathrooms.setOnClickListener(v -> {
            if (bathrooms < 10) {
                bathrooms++;
                tvBathrooms.setText(String.valueOf(bathrooms));
            }
        });
        
        // Action buttons
        btnSaveDraft.setOnClickListener(v -> saveDraft());
        btnPublish.setOnClickListener(v -> publishListing());
    }
    
    private void loadListingData() {
        // TODO: Load existing listing data for editing
        // For now, just update the title
        if (isEditMode) {
            // You can implement this later
        }
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Title validation
        if (etTitle.getText() == null || etTitle.getText().toString().trim().isEmpty()) {
            tilTitle.setError("Please enter a title");
            isValid = false;
        } else {
            tilTitle.setError(null);
        }
        
        // City validation
        if (etCity.getText() == null || etCity.getText().toString().trim().isEmpty()) {
            tilCity.setError("Please enter a city");
            isValid = false;
        } else {
            tilCity.setError(null);
        }
        
        // Address validation
        if (etAddress.getText() == null || etAddress.getText().toString().trim().isEmpty()) {
            tilAddress.setError("Please enter an address");
            isValid = false;
        } else {
            tilAddress.setError(null);
        }
        
        // Size validation
        if (etSize.getText() == null || etSize.getText().toString().trim().isEmpty()) {
            tilSize.setError("Please enter property size");
            isValid = false;
        } else {
            tilSize.setError(null);
        }
        
        // Price validation
        if (etPrice.getText() == null || etPrice.getText().toString().trim().isEmpty()) {
            tilPrice.setError("Please enter monthly rent");
            isValid = false;
        } else {
            tilPrice.setError(null);
        }
        
        // Description validation
        if (etDescription.getText() == null || etDescription.getText().toString().trim().isEmpty()) {
            tilDescription.setError("Please enter a description");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }
        
        return isValid;
    }
    
    private void saveDraft() {
        if (!validateInputs()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Save as draft
        saveListingToDatabase("Draft");
    }
    
    private void publishListing() {
        if (!validateInputs()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Publish as active
        saveListingToDatabase("Active");
    }
    
    private void saveListingToDatabase(String status) {
        try {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create listing object
            MyListings.ListingItem listing = new MyListings.ListingItem();
            listing.setTitle(etTitle.getText().toString().trim());
            listing.setCity(etCity.getText().toString().trim());
            listing.setArea(etArea.getText().toString().trim());
            listing.setAddress(etAddress.getText().toString().trim());
            listing.setPrice("₪" + etPrice.getText().toString().trim() + "/month");
            listing.setDescription(etDescription.getText().toString().trim());
            listing.setBedrooms(bedrooms);
            listing.setBathrooms(bathrooms);
            listing.setArea(Integer.parseInt(etSize.getText().toString().trim()));
            listing.setStatus(status);
            listing.setUserId(mAuth.getCurrentUser().getUid());
            listing.setTimestamp(System.currentTimeMillis());
            
            // Save to database
            DatabaseReference listingRef;
            if (isEditMode && listingId != null) {
                listingRef = mDatabase.child("listings").child(listingId);
            } else {
                listingRef = mDatabase.child("listings").push();
            }
            
            listingRef.setValue(listing).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, 
                        isEditMode ? "Listing updated successfully!" : "Listing saved successfully!", 
                        Toast.LENGTH_SHORT).show();
                    
                    // Go back to My Listings screen
                    Intent intent = new Intent(AddDormActivity.this, MyListings.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error saving listing: " + task.getException().getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

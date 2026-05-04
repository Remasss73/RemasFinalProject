package remas.example.remasfinalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
    
    // Location data
    private double currentLatitude, currentLongitude;
    private String currentAddress;
    private TextView tvBedrooms, tvBathrooms;
    private ImageButton ibMinusBedrooms, ibPlusBedrooms, ibMinusBathrooms, ibPlusBathrooms;
    private CheckBox cbWifi, cbParking, cbLaundry, cbGym, cbKitchen, cbAirConditioning, cbBalcony, cbElevator, cbSecurity, cbStorage;
    private RadioGroup rgFurnished;
    private RadioButton rbUnfurnished, rbPartiallyFurnished, rbFullyFurnished;
    private MaterialButton btnSaveDraft, btnPublish, btnGetLocation;
    
    // Data
    private int bedrooms = 1;
    private int bathrooms = 1;
    private boolean isEditMode = false;
    private String listingId = null;
    
    // Photo upload
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private java.util.List<Uri> selectedImageUris = new java.util.ArrayList<>();
    private LinearLayout llPhotosContainer;
    private Uri currentCameraPhotoUri;
    
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
        tilAddress = findViewById(R.id.tilAddress);
        tilSize = findViewById(R.id.tilSize);
        tilPrice = findViewById(R.id.tilPrice);
        tilDescription = findViewById(R.id.tilDescription);
        
        etTitle = findViewById(R.id.etTitle);
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etSize = findViewById(R.id.etSize);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        
        // Amenities
        cbWifi = findViewById(R.id.cbWifi);
        cbParking = findViewById(R.id.cbParking);
        cbLaundry = findViewById(R.id.cbLaundry);
        cbGym = findViewById(R.id.cbGym);
        cbKitchen = findViewById(R.id.cbKitchen);
        cbAirConditioning = findViewById(R.id.cbAirConditioning);
        cbBalcony = findViewById(R.id.cbBalcony);
        cbElevator = findViewById(R.id.cbElevator);
        cbSecurity = findViewById(R.id.cbSecurity);
        cbStorage = findViewById(R.id.cbStorage);
        
        // Furnished status
        rgFurnished = findViewById(R.id.rgFurnished);
        rbUnfurnished = findViewById(R.id.rbUnfurnished);
        rbPartiallyFurnished = findViewById(R.id.rbPartiallyFurnished);
        rbFullyFurnished = findViewById(R.id.rbFullyFurnished);
        
        // Set default furnished status
        rgFurnished.check(R.id.rbUnfurnished);
        
        // Set up auto-capitalization for input fields
        setupAutoCapitalization();
        
        // Bedroom/Bathroom controls
        tvBedrooms = findViewById(R.id.tvBedrooms);
        tvBathrooms = findViewById(R.id.tvBathrooms);
        ibMinusBedrooms = findViewById(R.id.ibMinusBedrooms);
        ibPlusBedrooms = findViewById(R.id.ibPlusBedrooms);
        ibMinusBathrooms = findViewById(R.id.ibMinusBathrooms);
        ibPlusBathrooms = findViewById(R.id.ibPlusBathrooms);
        
        // Photo
        ivAddPhoto = findViewById(R.id.ivAddPhoto);
        llPhotosContainer = findViewById(R.id.llPhotosContainer);
        
        // Buttons
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnPublish = findViewById(R.id.btnPublish);
        btnGetLocation = findViewById(R.id.btnGetLocation);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        ivSave.setOnClickListener(v -> saveDraft());
        
        ivAddPhoto.setOnClickListener(v -> showPhotoPickerDialog());
        
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
        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
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
            listing.setAddress(etAddress.getText().toString().trim());
            listing.setPrice("₪" + etPrice.getText().toString().trim() + "/month");
            listing.setDescription(etDescription.getText().toString().trim());
            listing.setBedrooms(bedrooms);
            listing.setBathrooms(bathrooms);
            listing.setArea2(Integer.parseInt(etSize.getText().toString().trim()));
            listing.setStatus(status);
            listing.setUserId(mAuth.getCurrentUser().getUid());
            listing.setTimestamp(System.currentTimeMillis());
            
            // Set location coordinates
            if (currentLatitude != 0 && currentLongitude != 0) {
                listing.setLatitude(currentLatitude);
                listing.setLongitude(currentLongitude);
            }
            
            // Set furnished status
            String furnishedStatus = "Unfurnished";
            int selectedFurnishedId = rgFurnished.getCheckedRadioButtonId();
            if (selectedFurnishedId == R.id.rbPartiallyFurnished) {
                furnishedStatus = "Partially Furnished";
            } else if (selectedFurnishedId == R.id.rbFullyFurnished) {
                furnishedStatus = "Fully Furnished";
            }
            
            // Set amenities
            java.util.List<String> amenitiesList = new java.util.ArrayList<>();
            if (cbWifi.isChecked()) amenitiesList.add("WiFi");
            if (cbParking.isChecked()) amenitiesList.add("Parking");
            if (cbLaundry.isChecked()) amenitiesList.add("Laundry");
            if (cbGym.isChecked()) amenitiesList.add("Gym");
            if (cbKitchen.isChecked()) amenitiesList.add("Kitchen");
            if (cbAirConditioning.isChecked()) amenitiesList.add("Air Conditioning");
            if (cbBalcony.isChecked()) amenitiesList.add("Balcony");
            if (cbElevator.isChecked()) amenitiesList.add("Elevator");
            if (cbSecurity.isChecked()) amenitiesList.add("Security");
            if (cbStorage.isChecked()) amenitiesList.add("Storage");
            
            // Add furnished status to amenities for display
            amenitiesList.add(furnishedStatus);
            listing.setAmenities(amenitiesList);
            
            // Set photo URLs (convert URIs to strings)
            java.util.List<String> photoUrlList = new java.util.ArrayList<>();
            for (Uri uri : selectedImageUris) {
                photoUrlList.add(uri.toString());
            }
            listing.setPhotoUrls(photoUrlList);
            
            // Set first photo as main image URL for backward compatibility
            if (!selectedImageUris.isEmpty()) {
                listing.setImageUrl(selectedImageUris.get(0).toString());
            }
            
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
    
    private void showPhotoPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    checkCameraPermission();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });
        builder.show();
    }
    
    private void checkCameraPermission() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }
    
    private void openCamera() {
        try {
            // Create a temporary file to store the camera photo
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String imageFileName = "IMG_" + timeStamp + ".jpg";
            java.io.File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            java.io.File imageFile = new java.io.File(storageDir, imageFileName);
            
            // Get URI using FileProvider
            currentCameraPhotoUri = FileProvider.getUriForFile(this, 
                    getPackageName() + ".fileprovider", imageFile);
            
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentCameraPhotoUri);
            
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error preparing camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                showLocationDialog();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                // Handle multiple photos from gallery
                if (data.getClipData() != null) {
                    // Multiple photos selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        if (!selectedImageUris.contains(imageUri)) {
                            selectedImageUris.add(imageUri);
                            addPhotoView(imageUri);
                        }
                    }
                    Toast.makeText(this, count + " photos added", Toast.LENGTH_SHORT).show();
                } else if (data.getData() != null) {
                    // Single photo selected
                    Uri imageUri = data.getData();
                    if (!selectedImageUris.contains(imageUri)) {
                        selectedImageUris.add(imageUri);
                        addPhotoView(imageUri);
                        Toast.makeText(this, "Photo added", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == CAMERA_REQUEST) {
                // Handle camera photo
                if (currentCameraPhotoUri != null) {
                    selectedImageUris.add(currentCameraPhotoUri);
                    addPhotoView(currentCameraPhotoUri);
                    Toast.makeText(this, "Camera photo added", Toast.LENGTH_SHORT).show();
                    currentCameraPhotoUri = null; // Reset for next photo
                } else {
                    Toast.makeText(this, "Failed to capture photo", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private void addPhotoView(Uri imageUri) {
        // Create photo view layout
        View photoView = LayoutInflater.from(this).inflate(R.layout.item_photo, null);
        
        ImageView ivPhoto = photoView.findViewById(R.id.ivPhoto);
        ImageView ivDelete = photoView.findViewById(R.id.ivDelete);
        
        // Set photo
        ivPhoto.setImageURI(imageUri);
        
        // Set delete click listener
        ivDelete.setOnClickListener(v -> {
            removePhoto(imageUri, photoView);
        });
        
        // Add to container before the add button
        int addButtonIndex = llPhotosContainer.getChildCount() - 1;
        llPhotosContainer.addView(photoView, addButtonIndex);
    }
    
    private void removePhoto(Uri imageUri, View photoView) {
        // Remove from list
        selectedImageUris.remove(imageUri);
        
        // Remove from layout
        llPhotosContainer.removeView(photoView);
        
        Toast.makeText(this, "Photo removed", Toast.LENGTH_SHORT).show();
    }
    
    private void getCurrentLocation() {
        // Check location permissions
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 200);
        } else {
            // Get location automatically and show map
            getLocationAndShowMap();
        }
    }
    
    private void getLocationAndShowMap() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager == null) {
                Toast.makeText(this, "Location services not available", Toast.LENGTH_SHORT).show();
                showLocationDialog();
                return;
            }
            
            // Check if any location provider is enabled
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            
            if (!gpsEnabled && !networkEnabled) {
                Toast.makeText(this, "Please enable location services for accurate location", Toast.LENGTH_LONG).show();
                showLocationDialog();
                return;
            }
            
            // Prefer GPS but allow network as fallback
            String provider = gpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
            
            Toast.makeText(this, "Getting your location using " + (gpsEnabled ? "GPS" : "Network") + "...", Toast.LENGTH_SHORT).show();
            
            // Try to get last known location first
            Location lastLocation = null;
            try {
                lastLocation = locationManager.getLastKnownLocation(provider);
            } catch (Exception e) {
                // Continue with location updates
            }
            
            if (lastLocation != null) {
                // Use last known location and show map
                currentLatitude = lastLocation.getLatitude();
                currentLongitude = lastLocation.getLongitude();
                currentAddress = etAddress.getText().toString().trim();
                if (currentAddress.isEmpty()) {
                    currentAddress = "Current Location";
                }
                showMapDialog();
            } else {
                // Request location updates with timeout
                final LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            currentAddress = etAddress.getText().toString().trim();
                            if (currentAddress.isEmpty()) {
                                currentAddress = "Current Location";
                            }
                            showMapDialog();
                            // Stop location updates after getting first location
                            try {
                                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                                if (lm != null) {
                                    lm.removeUpdates(this);
                                }
                            } catch (SecurityException e) {
                                // Ignore
                            }
                        }
                    }
                    
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    
                    @Override
                    public void onProviderEnabled(String provider) {}
                    
                    @Override
                    public void onProviderDisabled(String provider) {}
                };
                
                locationManager.requestLocationUpdates(provider, 5000, 10, locationListener);
                
                // Add timeout handler
                new android.os.Handler().postDelayed(() -> {
                    try {
                        locationManager.removeUpdates(locationListener);
                        // If no location received, try fallback or show error
                        if (currentLatitude == 0 && currentLongitude == 0) {
                            // Try the other provider as fallback
                            String fallbackProvider = provider.equals(LocationManager.GPS_PROVIDER) ? 
                                    LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;
                            
                            if (locationManager.isProviderEnabled(fallbackProvider)) {
                                Toast.makeText(this, "Trying " + fallbackProvider + " provider...", Toast.LENGTH_SHORT).show();
                                locationManager.requestLocationUpdates(fallbackProvider, 5000, 10, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        if (location != null) {
                                            currentLatitude = location.getLatitude();
                                            currentLongitude = location.getLongitude();
                                            currentAddress = etAddress.getText().toString().trim();
                                            if (currentAddress.isEmpty()) {
                                                currentAddress = "Current Location";
                                            }
                                            showMapDialog();
                                            try {
                                                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                                                if (lm != null) {
                                                    lm.removeUpdates(this);
                                                }
                                            } catch (SecurityException e) {
                                                // Ignore
                                            }
                                        }
                                    }
                                    
                                    @Override
                                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                                    
                                    @Override
                                    public void onProviderEnabled(String provider) {}
                                    
                                    @Override
                                    public void onProviderDisabled(String provider) {}
                                });
                            } else {
                                Toast.makeText(this, "Unable to get location. Please try again.", Toast.LENGTH_LONG).show();
                                showLocationDialog();
                            }
                        }
                    } catch (Exception e) {
                        // Ignore timeout cleanup errors
                    }
                }, 15000); // 15 second timeout
            }
            
        } catch (SecurityException e) {
            Toast.makeText(this, "Security error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showLocationDialog();
        } catch (Exception e) {
            Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showLocationDialog();
        }
    }
    
    private void showMapDialog() {
        // Auto-fill address field if it's empty
        String currentAddressText = etAddress.getText().toString().trim();
        if (currentAddressText.isEmpty()) {
            // Create a simple address from coordinates or use default
            String autoAddress = "Location detected (" + String.format("%.4f", currentLatitude) + ", " + String.format("%.4f", currentLongitude) + ")";
            etAddress.setText(autoAddress);
            Toast.makeText(this, "Address auto-filled from GPS location", Toast.LENGTH_SHORT).show();
        }
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("📍 Location Set!")
                .setMessage("Your location has been set to:\n\n" + currentAddress + 
                           "\n\nLat: " + String.format("%.6f", currentLatitude) + 
                           "\nLon: " + String.format("%.6f", currentLongitude))
                .setPositiveButton("✅ Confirm", (dialog, which) -> {
                    Toast.makeText(this, "Location saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("🔄 Change", (dialog, which) -> {
                    getCurrentLocation();
                })
                .show();
    }
    
    private void showLocationDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Get Location")
                .setMessage("Choose how to get location:")
                .setPositiveButton("📍 Try Again", (dialog, which) -> {
                    getCurrentLocation();
                })
                .setNegativeButton("⚙️ Enable GPS", (dialog, which) -> {
                    // Open location settings
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .show();
    }
    
    private void openMapApp() {
        try {
            String address = etAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter an address first", Toast.LENGTH_SHORT).show();
                etAddress.requestFocus();
                return;
            }
            
            // Try to open Google Maps first
            Intent intent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("geo:0,0?q=" + address));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback to any map app
                intent = new Intent(Intent.ACTION_VIEW, 
                        Uri.parse("geo:0,0?q=" + address));
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening maps: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAutoCapitalization() {
        // Title field - capitalize first letter of each word
        etTitle.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etTitle.removeTextChangedListener(this);
                        etTitle.setText(firstChar);
                        etTitle.setSelection(1);
                        etTitle.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Capitalize after spaces for multi-word titles
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
                    etTitle.removeTextChangedListener(this);
                    etTitle.setText(capitalized.toString());
                    etTitle.setSelection(capitalized.length());
                    etTitle.addTextChangedListener(this);
                }
            }
        });

        // City field - capitalize first letter of each word
        etCity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etCity.removeTextChangedListener(this);
                        etCity.setText(firstChar);
                        etCity.setSelection(1);
                        etCity.addTextChangedListener(this);
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

        // Address field - capitalize first letter of each word
        etAddress.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etAddress.removeTextChangedListener(this);
                        etAddress.setText(firstChar);
                        etAddress.setSelection(1);
                        etAddress.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Capitalize after spaces for multi-word addresses
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
                    etAddress.removeTextChangedListener(this);
                    etAddress.setText(capitalized.toString());
                    etAddress.setSelection(capitalized.length());
                    etAddress.addTextChangedListener(this);
                }
            }
        });

        // Description field - capitalize first letter of each sentence
        etDescription.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etDescription.removeTextChangedListener(this);
                        etDescription.setText(firstChar);
                        etDescription.setSelection(1);
                        etDescription.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Capitalize after periods for sentences
                String text = s.toString();
                String[] sentences = text.split("\\. ");
                StringBuilder capitalized = new StringBuilder();
                for (int i = 0; i < sentences.length; i++) {
                    if (!sentences[i].isEmpty()) {
                        capitalized.append(Character.toUpperCase(sentences[i].charAt(0)))
                                .append(sentences[i].substring(1).toLowerCase());
                        if (i < sentences.length - 1) {
                            capitalized.append(". ");
                        }
                    }
                }
                if (!text.equals(capitalized.toString())) {
                    etDescription.removeTextChangedListener(this);
                    etDescription.setText(capitalized.toString());
                    etDescription.setSelection(capitalized.length());
                    etDescription.addTextChangedListener(this);
                }
            }
        });

        // Price field - no capitalization needed (numbers only)
        // Size field - no capitalization needed (numbers only)
    }
}

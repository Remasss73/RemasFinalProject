package remas.example.remasfinalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDormActivity extends BaseActivity {
    
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
        if (isEditMode) {
            // implementation details
        }
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        if (etTitle.getText() == null || etTitle.getText().toString().trim().isEmpty()) {
            tilTitle.setError("Please enter a title");
            isValid = false;
        } else tilTitle.setError(null);
        if (etCity.getText() == null || etCity.getText().toString().trim().isEmpty()) {
            tilCity.setError("Please enter a city");
            isValid = false;
        } else tilCity.setError(null);
        if (etAddress.getText() == null || etAddress.getText().toString().trim().isEmpty()) {
            tilAddress.setError("Please enter an address");
            isValid = false;
        } else tilAddress.setError(null);
        if (etSize.getText() == null || etSize.getText().toString().trim().isEmpty()) {
            tilSize.setError("Please enter property size");
            isValid = false;
        } else tilSize.setError(null);
        if (etPrice.getText() == null || etPrice.getText().toString().trim().isEmpty()) {
            tilPrice.setError("Please enter monthly rent");
            isValid = false;
        } else tilPrice.setError(null);
        if (etDescription.getText() == null || etDescription.getText().toString().trim().isEmpty()) {
            tilDescription.setError("Please enter a description");
            isValid = false;
        } else tilDescription.setError(null);
        return isValid;
    }
    
    private void saveDraft() {
        if (!validateInputs()) return;
        saveListingToDatabase("Draft");
    }
    
    private void publishListing() {
        if (!validateInputs()) return;
        saveListingToDatabase("Active");
    }
    
    private void saveListingToDatabase(String status) {
        try {
            if (mAuth.getCurrentUser() == null) return;
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
            if (currentLatitude != 0 && currentLongitude != 0) {
                listing.setLatitude(currentLatitude);
                listing.setLongitude(currentLongitude);
            }
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
            listing.setAmenities(amenitiesList);
            java.util.List<String> photoUrlList = new java.util.ArrayList<>();
            for (Uri uri : selectedImageUris) photoUrlList.add(uri.toString());
            listing.setPhotoUrls(photoUrlList);
            if (!selectedImageUris.isEmpty()) listing.setImageUrl(selectedImageUris.get(0).toString());
            DatabaseReference listingRef = isEditMode && listingId != null ? mDatabase.child("listings").child(listingId) : mDatabase.child("listings").push();
            listingRef.setValue(listing).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(AddDormActivity.this, MyListings.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (Exception e) {}
    }
    
    private void showPhotoPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
            if (which == 0) checkCameraPermission();
            else openGallery();
        });
        builder.show();
    }
    
    private void checkCameraPermission() {
        String[] permissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? new String[]{Manifest.permission.CAMERA} : new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        else openCamera();
    }
    
    private void openCamera() {
        try {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String imageFileName = "IMG_" + timeStamp + ".jpg";
            java.io.File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            java.io.File imageFile = new java.io.File(storageDir, imageFileName);
            currentCameraPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentCameraPhotoUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } catch (Exception e) {}
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
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera();
        else if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCurrentLocation();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        if (!selectedImageUris.contains(imageUri)) {
                            selectedImageUris.add(imageUri);
                            addPhotoView(imageUri);
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    if (!selectedImageUris.contains(imageUri)) {
                        selectedImageUris.add(imageUri);
                        addPhotoView(imageUri);
                    }
                }
            } else if (requestCode == CAMERA_REQUEST && currentCameraPhotoUri != null) {
                selectedImageUris.add(currentCameraPhotoUri);
                addPhotoView(currentCameraPhotoUri);
                currentCameraPhotoUri = null;
            }
        }
    }
    
    private void addPhotoView(Uri imageUri) {
        View photoView = LayoutInflater.from(this).inflate(R.layout.item_photo, null);
        ImageView ivPhoto = photoView.findViewById(R.id.ivPhoto);
        ImageView ivDelete = photoView.findViewById(R.id.ivDelete);
        ivPhoto.setImageURI(imageUri);
        ivDelete.setOnClickListener(v -> {
            selectedImageUris.remove(imageUri);
            llPhotosContainer.removeView(photoView);
        });
        llPhotosContainer.addView(photoView, llPhotosContainer.getChildCount() - 1);
    }
    
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        else getLocationAndShowMap();
    }
    
    private void getLocationAndShowMap() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager == null) return;
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gpsEnabled && !networkEnabled) return;
            String provider = gpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
            Location lastLocation = locationManager.getLastKnownLocation(provider);
            if (lastLocation != null) {
                currentLatitude = lastLocation.getLatitude();
                currentLongitude = lastLocation.getLongitude();
                currentAddress = etAddress.getText().toString().trim();
                showMapDialog();
            } else {
                locationManager.requestLocationUpdates(provider, 5000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            currentAddress = etAddress.getText().toString().trim();
                            showMapDialog();
                            locationManager.removeUpdates(this);
                        }
                    }
                    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override public void onProviderEnabled(String provider) {}
                    @Override public void onProviderDisabled(String provider) {}
                });
            }
        } catch (SecurityException e) {}
    }
    
    private void showMapDialog() {
        if (etAddress.getText().toString().trim().isEmpty()) etAddress.setText("Location detected (" + String.format("%.4f", currentLatitude) + ", " + String.format("%.4f", currentLongitude) + ")");
        new AlertDialog.Builder(this).setTitle("📍 Location Set!").setMessage("Your location has been set.").setPositiveButton("✅ Confirm", null).show();
    }
    
    private void setupAutoCapitalization() {
        etTitle.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
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
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        etCity.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
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
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }
}

package remas.example.remasfinalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Profile extends AppCompatActivity {
    
    private static final String TAG = "ProfileActivity";
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;
    private static final int PERMISSION_REQUEST_CODE = 103;
    
    // UI Components
    private Toolbar toolbar;
    private ImageView ivProfileImage;
    private FloatingActionButton fabCamera, fabGallery;
    private TextInputLayout tilFullName, tilEmail, tilPhone, tilLocation, tilBio;
    private TextInputEditText etFullName, etEmail, etPhone, etLocation, etBio;
    private MaterialButton btnSaveProfile, btnCancel, btnEditProfile, btnChangePassword, btnViewListings;
    private TextView tvMemberSince, tvListingsCount, tvProfileStatus, tvUserName;
    private CircularProgressIndicator progressIndicator;
    
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    
    // Image handling
    private Uri selectedImageUri;
    private String profileImageUrl;
    private boolean isEditMode = false;
    private boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        // Initialize UI components
        initializeViews();
        setupToolbar();
        setupClickListeners();
        setupAutoCapitalization();
        loadUserProfile();
        
        // Setup animations
        setupAnimations();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        fabCamera = findViewById(R.id.fabCamera);
        fabGallery = findViewById(R.id.fabGallery);
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        tilPhone = findViewById(R.id.tilPhone);
        etPhone = findViewById(R.id.etPhone);
        tilLocation = findViewById(R.id.tilLocation);
        etLocation = findViewById(R.id.etLocation);
        tilBio = findViewById(R.id.tilBio);
        etBio = findViewById(R.id.etBio);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCancel = findViewById(R.id.btnCancel);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        tvListingsCount = findViewById(R.id.tvListingsCount);
        tvProfileStatus = findViewById(R.id.tvProfileStatus);
        tvUserName = findViewById(R.id.tvUserName);
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        fabCamera.setOnClickListener(v -> {
            if (!isUploading) {
                showImageSourceDialog();
            }
        });
        
        fabGallery.setOnClickListener(v -> {
            if (!isUploading) {
                showImageSourceDialog();
            }
        });
        
        btnEditProfile.setOnClickListener(v -> toggleEditMode());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> finish());
        
        // Additional action buttons
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnViewListings = findViewById(R.id.btnViewListings);
        
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnViewListings.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MyListings.class);
            startActivity(intent);
        });
    }

    private void setupAnimations() {
        // Fade in animation for profile image
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        ivProfileImage.startAnimation(fadeIn);
        
        // Scale animation for profile image
        ScaleAnimation scaleIn = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, 
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleIn.setDuration(800);
        ivProfileImage.startAnimation(scaleIn);
        
        // Slide up animation for profile header
        ViewCompat.animate(ivProfileImage)
                .translationY(0)
                .alpha(1.0f)
                .setDuration(800)
                .start();
    }

    private void showImageSourceDialog() {
        String[] options = {"📷 Take Photo", "🖼️ Choose from Gallery", "❌ Cancel"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Profile Picture")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Take Photo
                            checkCameraPermission();
                            break;
                        case 1: // Choose from Gallery
                            checkStoragePermission();
                            break;
                        case 2: // Cancel
                            dialog.dismiss();
                            break;
                    }
                });
        
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupAutoCapitalization() {
        // Full Name field - capitalize first letter of each word
        etFullName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etFullName.removeTextChangedListener(this);
                        etFullName.setText(firstChar);
                        etFullName.setSelection(1);
                        etFullName.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
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
                    etFullName.removeTextChangedListener(this);
                    etFullName.setText(capitalized.toString());
                    etFullName.setSelection(capitalized.length());
                    etFullName.addTextChangedListener(this);
                }
            }
        });

        // Email field - lowercase all letters
        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lowerCase = s.toString().toLowerCase();
                if (!s.toString().equals(lowerCase)) {
                    etEmail.removeTextChangedListener(this);
                    etEmail.setText(lowerCase);
                    etEmail.setSelection(lowerCase.length());
                    etEmail.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Location field - capitalize first letter of each word
        etLocation.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etLocation.removeTextChangedListener(this);
                        etLocation.setText(firstChar);
                        etLocation.setSelection(1);
                        etLocation.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
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
                    etLocation.removeTextChangedListener(this);
                    etLocation.setText(capitalized.toString());
                    etLocation.setSelection(capitalized.length());
                    etLocation.addTextChangedListener(this);
                }
            }
        });

        // Bio field - capitalize first letter of each sentence
        etBio.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && start == 0) {
                    String firstChar = s.toString().toUpperCase();
                    if (!s.toString().equals(firstChar)) {
                        etBio.removeTextChangedListener(this);
                        etBio.setText(firstChar);
                        etBio.setSelection(1);
                        etBio.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
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
                    etBio.removeTextChangedListener(this);
                    etBio.setText(capitalized.toString());
                    etBio.setSelection(capitalized.length());
                    etBio.addTextChangedListener(this);
                }
            }
        });
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        
        if (isEditMode) {
            enableEditing();
            btnEditProfile.setText("❌ Cancel Edit");
            btnSaveProfile.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            showStatusMessage("Edit mode enabled - Make your changes", "#38BDF8");
        } else {
            disableEditing();
            btnEditProfile.setText("✏️ Edit Profile");
            btnSaveProfile.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            showStatusMessage("View mode - Tap Edit to modify", "#94A3B8");
            loadUserProfile(); // Reload original data
        }
    }

    private void enableEditing() {
        etFullName.setEnabled(true);
        etEmail.setEnabled(true);
        etPhone.setEnabled(true);
        etLocation.setEnabled(true);
        etBio.setEnabled(true);
        fabCamera.setVisibility(View.VISIBLE);
        fabGallery.setVisibility(View.VISIBLE);
        
        // Animate transition
        animateEditMode(true);
    }

    private void disableEditing() {
        etFullName.setEnabled(false);
        etEmail.setEnabled(false);
        etPhone.setEnabled(false);
        etLocation.setEnabled(false);
        etBio.setEnabled(false);
        fabCamera.setVisibility(View.GONE);
        fabGallery.setVisibility(View.GONE);
        
        // Animate transition
        animateEditMode(false);
    }

    private void animateEditMode(boolean editing) {
        float alpha = editing ? 1.0f : 0.5f;
        float scale = editing ? 1.05f : 1.0f;
        
        if (fabCamera != null) {
            fabCamera.animate().alpha(alpha).scaleX(scale).scaleY(scale).setDuration(300).start();
        }
        if (fabGallery != null) {
            fabGallery.animate().alpha(alpha).scaleX(scale).scaleY(scale).setDuration(300).start();
        }
    }

    private void showStatusMessage(String message, String color) {
        if (tvProfileStatus != null) {
            tvProfileStatus.setText(message);
            tvProfileStatus.setTextColor(Color.parseColor(color));
            tvProfileStatus.setVisibility(View.VISIBLE);
            
            // Auto-hide after 3 seconds
            tvProfileStatus.postDelayed(() -> {
                tvProfileStatus.setVisibility(View.GONE);
            }, 3000);
        }
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoadingState(true);
        
        // Load user profile data
        mDatabase.child("users").child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        showLoadingState(false);
                        
                        if (dataSnapshot.exists()) {
                            // Load existing profile data
                            String fullName = dataSnapshot.child("fullName").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);
                            String phone = dataSnapshot.child("phone").getValue(String.class);
                            String location = dataSnapshot.child("location").getValue(String.class);
                            String bio = dataSnapshot.child("bio").getValue(String.class);
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                            Long memberSince = dataSnapshot.child("timestamp").getValue(Long.class);

                            // Set UI fields
                            etFullName.setText(fullName != null ? fullName : "");
                            etEmail.setText(email != null ? email : "");
                            etPhone.setText(phone != null ? phone : "");
                            etLocation.setText(location != null ? location : "");
                            etBio.setText(bio != null ? bio : "");

                            // Set user name display
                            if (tvUserName != null) {
                                tvUserName.setText(fullName != null ? fullName : "User Name");
                            }

                            // Set member since date
                            if (tvMemberSince != null && memberSince != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                                tvMemberSince.setText(sdf.format(new Date(memberSince)));
                            }

                            // Load profile image
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                // For now, using placeholder - in real app, use image loading library
                                ivProfileImage.setImageResource(android.R.drawable.ic_menu_mylocation);
                            } else {
                                ivProfileImage.setImageResource(android.R.drawable.ic_menu_mylocation);
                            }

                            // Load listings count
                            loadUserListingsCount(currentUser.getUid());

                            // Start in view mode
                            disableEditing();
                            btnSaveProfile.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                            
                            showStatusMessage("Profile loaded successfully", "#10B981");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showLoadingState(false);
                        Toast.makeText(Profile.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Database error", databaseError.toException());
                    }
                });
    }

    private void loadUserListingsCount(String userId) {
        if (tvListingsCount != null) {
            mDatabase.child("listings")
                    .orderByChild("userId")
                    .equalTo(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                count++;
                            }
                            tvListingsCount.setText(String.valueOf(count));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Error loading listings count", databaseError.toException());
                        }
                    });
        }
    }

    private void showLoadingState(boolean show) {
        isUploading = show;
        if (progressIndicator != null) {
            progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (permissions[i].equals(Manifest.permission.CAMERA)) {
                        openCamera();
                    } else if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        openGallery();
                    }
                } else {
                    Toast.makeText(this, "Permission denied: " + permissions[i], Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    selectedImageUri = getImageUri(imageBitmap);
                    ivProfileImage.setImageBitmap(imageBitmap);
                    showStatusMessage("Camera image captured", "#10B981");
                }
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    selectedImageUri = imageUri;
                    ivProfileImage.setImageURI(imageUri);
                    showStatusMessage("Gallery image selected", "#10B981");
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ProfileImage", null);
        return Uri.parse(path);
    }

    private void saveProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to save profile", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate fields
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        if (fullName.isEmpty()) {
            tilFullName.setError("Full Name is required");
            return;
        }
        tilFullName.setError(null);

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            return;
        }
        tilEmail.setError(null);

        // Show loading state
        showLoadingState(true);
        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("Saving...");

        // Save profile image if selected
        if (selectedImageUri != null) {
            uploadProfileImage(fullName, email, phone, location, bio);
        } else {
            saveProfileDataToFirebase(fullName, email, phone, location, bio, profileImageUrl);
        }
    }

    private void uploadProfileImage(String fullName, String email, String phone, String location, String bio) {
        StorageReference profileImagesRef = mStorage.child("profile_images/" + mAuth.getCurrentUser().getUid() + ".jpg");
        
        profileImagesRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImagesRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        profileImageUrl = downloadUrl.toString();
                        saveProfileDataToFirebase(fullName, email, phone, location, bio, profileImageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    showLoadingState(false);
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Save Changes");
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showStatusMessage("Image upload failed", "#EF4444");
                });
    }

    private void saveProfileDataToFirebase(String fullName, String email, String phone, String location, String bio, String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());
        
        // Save all profile data
        userRef.child("fullName").setValue(fullName);
        userRef.child("email").setValue(email);
        userRef.child("phone").setValue(phone);
        userRef.child("location").setValue(location);
        userRef.child("bio").setValue(bio);
        if (imageUrl != null) {
            userRef.child("profileImageUrl").setValue(imageUrl);
        }
        
        // Add timestamp if not exists
        userRef.child("timestamp").setValue(System.currentTimeMillis());

        showLoadingState(false);
        btnSaveProfile.setEnabled(true);
        btnSaveProfile.setText("Save Changes");
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
        showStatusMessage("Profile updated successfully!", "#10B981");
        
        // Reset UI state
        selectedImageUri = null;
        
        // Switch back to view mode
        toggleEditMode();
    }

    private void showChangePasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Change Password")
                .setMessage("A password reset link will be sent to your email.")
                .setPositiveButton("Send Reset Link", (dialog, which) -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        mAuth.sendPasswordResetEmail(user.getEmail())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                        showStatusMessage("Check your email for reset link", "#10B981");
                                    } else {
                                        Toast.makeText(Profile.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
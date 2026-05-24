package remas.example.remasfinalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * نشاط الملف الشخصي: يسمح للمستخدم بعرض وتعديل بياناته الشخصية وصورته.
 */
public class Profile extends BaseActivity {
    
    private static final String TAG = "ProfileActivity";
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;
    private static final int PERMISSION_REQUEST_CODE = 103;
    
    // UI Components
    private Toolbar toolbar;
    private ImageView ivProfileImage;
    private FloatingActionButton fabEditImage;
    private TextInputLayout tilFullName, tilEmail, tilPhone, tilLocation, tilBio;
    private TextInputEditText etFullName, etEmail, etPhone, etLocation, etBio;
    private MaterialButton btnSaveProfile, btnCancel, btnEditProfile, btnLogout;
    private TextView tvMemberSince, tvListingsCount, tvProfileStatus, tvUserName, tvFavoritesCount, tvBlockedCount;
    private LinearLayout layoutEditActions, layoutMyListings, layoutFavorites, layoutBlockedUsers, layoutChangePassword, layoutNotifications, layoutPrivacy;
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

    /**
     * تهيئة النشاط، إعداد Firebase، وربط الواجهة بالبيانات.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        initializeViews();
        setupToolbar();
        setupClickListeners();
        setupAutoCapitalization();
        loadUserProfile();
        setupAnimations();
    }

    /**
     * تعريف العناصر المرئية وربطها بمتغيرات الكود.
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        fabEditImage = findViewById(R.id.fabEditImage);
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
        btnLogout = findViewById(R.id.btnLogout);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        tvListingsCount = findViewById(R.id.tvListingsCount);
        tvProfileStatus = findViewById(R.id.tvProfileStatus);
        tvUserName = findViewById(R.id.tvUserName);
        tvFavoritesCount = findViewById(R.id.tvFavoritesCount);
        tvBlockedCount = findViewById(R.id.tvBlockedCount);
        layoutEditActions = findViewById(R.id.layoutEditActions);
        layoutMyListings = findViewById(R.id.layoutMyListings);
        layoutFavorites = findViewById(R.id.layoutFavorites);
        layoutBlockedUsers = findViewById(R.id.layoutBlockedUsers);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);
        layoutNotifications = findViewById(R.id.layoutNotifications);
        layoutPrivacy = findViewById(R.id.layoutPrivacy);
        progressIndicator = findViewById(R.id.progressIndicator);

        // Make profile image circular
        ivProfileImage.setClipToOutline(true);
    }

    /**
     * إعداد شريط العنوان العلوي (Toolbar) وتفعيل زر الرجوع.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * إعداد وظائف الأزرار مثل تعديل الملف، حفظ التغييرات، وتغيير الصورة.
     */
    private void setupClickListeners() {
        ivProfileImage.setOnClickListener(v -> {
            if (isEditMode && !isUploading) showImageSourceDialog();
        });
        
        fabEditImage.setOnClickListener(v -> {
            if (!isUploading) showImageSourceDialog();
        });
        
        btnEditProfile.setOnClickListener(v -> toggleEditMode());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> {
            toggleEditMode();
            loadUserProfile();
        });
        
        // Quick actions
        layoutMyListings.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MyListings.class);
            startActivity(intent);
        });
        
        layoutFavorites.setOnClickListener(v -> {
            // TODO: Navigate to favorites screen
            Toast.makeText(this, "Favorites feature coming soon", Toast.LENGTH_SHORT).show();
        });
        
        layoutBlockedUsers.setOnClickListener(v -> {
            // TODO: Navigate to blocked users screen
            Toast.makeText(this, "Blocked users feature coming soon", Toast.LENGTH_SHORT).show();
        });
        
        // Settings
        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        layoutNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications settings coming soon", Toast.LENGTH_SHORT).show();
        });
        layoutPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy settings coming soon", Toast.LENGTH_SHORT).show();
        });
        
        // Logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    /**
     * إضافة تأثيرات حركية عند فتح شاشة الملف الشخصي.
     */
    private void setupAnimations() {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        ivProfileImage.startAnimation(fadeIn);
        
        ScaleAnimation scaleIn = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, 
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleIn.setDuration(800);
        ivProfileImage.startAnimation(scaleIn);
    }

    /**
     * عرض خيار للمستخدم لاختيار مصدر الصورة (الكاميرا أو المعرض).
     */
    private void showImageSourceDialog() {
        String[] options = {getString(R.string.photo), "🖼️ المعرض", getString(R.string.cancel)};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.profile))
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: checkCameraPermission(); break;
                        case 1: checkStoragePermission(); break;
                        case 2: dialog.dismiss(); break;
                    }
                });
        builder.create().show();
    }

    /**
     * تحويل الحرف الأول من كل كلمة إلى حرف كبير تلقائياً عند كتابة الاسم.
     */
    private void setupAutoCapitalization() {
        etFullName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String text = s.toString();
                String[] words = text.split(" ");
                StringBuilder capitalized = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (!words[i].isEmpty()) {
                        capitalized.append(Character.toUpperCase(words[i].charAt(0)))
                                .append(words[i].substring(1).toLowerCase());
                        if (i < words.length - 1) capitalized.append(" ");
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
    }

    /**
     * التبديل بين وضع "العرض" ووضع "التعديل" لبيانات المستخدم.
     */
    private void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            enableEditing();
            btnEditProfile.setText(getString(R.string.cancel));
            layoutEditActions.setVisibility(View.VISIBLE);
        } else {
            disableEditing();
            btnEditProfile.setText(getString(R.string.edit_profile));
            layoutEditActions.setVisibility(View.GONE);
            loadUserProfile();
        }
    }

    /**
     * تفعيل حقول الإدخال للسماح للمستخدم بالكتابة وتغيير الصورة.
     */
    private void enableEditing() {
        etFullName.setEnabled(true);
        etEmail.setEnabled(true);
        etPhone.setEnabled(true);
        etLocation.setEnabled(true);
        etBio.setEnabled(true);
        fabEditImage.setVisibility(View.VISIBLE);
    }

    /**
     * إغلاق حقول الإدخال لمنع التعديل غير المقصود.
     */
    private void disableEditing() {
        etFullName.setEnabled(false);
        etEmail.setEnabled(false);
        etPhone.setEnabled(false);
        etLocation.setEnabled(false);
        etBio.setEnabled(false);
        fabEditImage.setVisibility(View.GONE);
    }


    /**
     * عرض رسالة حالة ملونة أسفل الشاشة لفترة قصيرة.
     */
    private void showStatusMessage(String message, String color) {
        if (tvProfileStatus != null) {
            tvProfileStatus.setText(message);
            tvProfileStatus.setTextColor(Color.parseColor(color));
            tvProfileStatus.setVisibility(View.VISIBLE);
            tvProfileStatus.postDelayed(() -> tvProfileStatus.setVisibility(View.GONE), 3000);
        }
    }

    /**
     * تحميل بيانات المستخدم من قاعدة بيانات Firebase وعرضها في الحقول المخصصة.
     */
    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { finish(); return; }

        showLoadingState(true);
        mDatabase.child("users").child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        showLoadingState(false);
                        if (dataSnapshot.exists()) {
                            String fullName = dataSnapshot.child("fullName").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);
                            String phone = dataSnapshot.child("phone").getValue(String.class);
                            String location = dataSnapshot.child("location").getValue(String.class);
                            String bio = dataSnapshot.child("bio").getValue(String.class);
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                            Long memberSince = dataSnapshot.child("timestamp").getValue(Long.class);

                            etFullName.setText(fullName != null ? fullName : "");
                            etEmail.setText(email != null ? email : "");
                            etPhone.setText(phone != null ? phone : "");
                            etLocation.setText(location != null ? location : "");
                            etBio.setText(bio != null ? bio : "");

                            if (tvUserName != null) tvUserName.setText(fullName != null && !fullName.isEmpty() ? fullName : "مستخدم");
                            if (tvMemberSince != null && memberSince != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                                tvMemberSince.setText(sdf.format(new Date(memberSince)));
                            }
                            
                            // Load profile image with circular crop
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(Profile.this)
                                    .load(profileImageUrl)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(ivProfileImage);
                            } else {
                                ivProfileImage.setImageResource(android.R.drawable.ic_menu_myplaces);
                            }
                            
                            loadUserListingsCount(currentUser.getUid());
                            loadFavoritesCount(currentUser.getUid());
                            loadBlockedUsersCount(currentUser.getUid());
                            disableEditing();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { showLoadingState(false); }
                });
    }

    /**
     * حساب عدد العقارات التي قام هذا المستخدم بنشرها وعرضها.
     */
    private void loadUserListingsCount(String userId) {
        if (tvListingsCount != null) {
            mDatabase.child("listings").orderByChild("userId").equalTo(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tvListingsCount.setText(dataSnapshot.getChildrenCount() + " listings");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
        }
    }
    
    /**
     * حساب عدد المفضلات للمستخدم.
     */
    private void loadFavoritesCount(String userId) {
        if (tvFavoritesCount != null) {
            mDatabase.child("favorites").child(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tvFavoritesCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
        }
    }
    
    /**
     * حساب عدد المستخدمين المحظورين.
     */
    private void loadBlockedUsersCount(String userId) {
        if (tvBlockedCount != null) {
            mDatabase.child("blockedUsers").child(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tvBlockedCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
        }
    }

    /**
     * التحكم في إظهار أو إخفاء مؤشر التحميل أثناء العمليات الطويلة.
     */
    private void showLoadingState(boolean show) {
        isUploading = show;
        if (progressIndicator != null) progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * التحقق من منح التطبيق صلاحية استخدام الكاميرا.
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else openCamera();
    }

    /**
     * التحقق من منح التطبيق صلاحية الوصول إلى ملفات الصور.
     */
    private void checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            } else openGallery();
        } else {
            // Android 12 and below uses READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else openGallery();
        }
    }

    /**
     * فتح تطبيق الكاميرا لالتقاط صورة شخصية جديدة.
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    /**
     * فتح معرض الصور لاختيار صورة موجودة مسبقاً.
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    /**
     * التعامل مع رد المستخدم على طلبات الصلاحيات (قبول أو رفض).
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (permissions[i].equals(Manifest.permission.CAMERA)) openCamera();
                    else if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE) || 
                             permissions[i].equals(Manifest.permission.READ_MEDIA_IMAGES)) openGallery();
                }
            }
        }
    }

    /**
     * يتم استدعاؤها بعد التقاط صورة أو اختيارها لمعالجتها وعرضها.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                selectedImageUri = getImageUri(imageBitmap);
                Glide.with(this)
                    .load(imageBitmap)
                    .apply(new RequestOptions().circleCrop())
                    .into(ivProfileImage);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                selectedImageUri = data.getData();
                Glide.with(this)
                    .load(selectedImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(ivProfileImage);
            }
        }
    }

    /**
     * تحويل صورة من نوع Bitmap إلى مسار (Uri) ليتم التعامل معها لاحقاً.
     */
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ProfileImage", null);
        return Uri.parse(path);
    }

    /**
     * التحقق من صحة البيانات الجديدة ثم البدء في عملية حفظها.
     */
    private void saveProfile() {
        if (etFullName.getText().toString().trim().isEmpty()) {
            tilFullName.setError(getString(R.string.error));
            return;
        }
        showLoadingState(true);
        if (selectedImageUri != null) uploadProfileImage();
        else saveProfileDataToFirebase(profileImageUrl);
    }

    /**
     * رفع الصورة الشخصية الجديدة إلى Firebase Storage.
     */
    private void uploadProfileImage() {
        StorageReference ref = mStorage.child("profile_images/" + mAuth.getCurrentUser().getUid() + ".jpg");
        ref.putFile(selectedImageUri).addOnSuccessListener(t -> ref.getDownloadUrl().addOnSuccessListener(url -> saveProfileDataToFirebase(url.toString())))
                .addOnFailureListener(e -> showLoadingState(false));
    }

    /**
     * حفظ جميع بيانات الملف الشخصي النصية والروابط في قاعدة البيانات.
     */
    private void saveProfileDataToFirebase(String imageUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        DatabaseReference ref = mDatabase.child("users").child(user.getUid());
        ref.child("fullName").setValue(etFullName.getText().toString().trim());
        ref.child("email").setValue(etEmail.getText().toString().trim());
        ref.child("phone").setValue(etPhone.getText().toString().trim());
        ref.child("location").setValue(etLocation.getText().toString().trim());
        ref.child("bio").setValue(etBio.getText().toString().trim());
        if (imageUrl != null) ref.child("profileImageUrl").setValue(imageUrl);
        
        showLoadingState(false);
        toggleEditMode();
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
    }

    /**
     * عرض مربع حوار لإرسال رابط إعادة تعيين كلمة المرور إلى بريد المستخدم.
     */
    private void showChangePasswordDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.change_password))
                .setMessage(getString(R.string.reset_password_description))
                .setPositiveButton(getString(R.string.send_reset_link), (dialog, which) -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(t -> Toast.makeText(this, "تم إرسال الرابط", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.cancel), null).show();
    }
    
    /**
     * عرض مربع حوار لتأكيد تسجيل الخروج.
     */
    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(Profile.this, SignIn.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null).show();
    }
}

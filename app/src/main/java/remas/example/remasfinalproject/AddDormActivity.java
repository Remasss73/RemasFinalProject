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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * نشاط إضافة عقار: يتيح للمستخدم إنشاء إعلان جديد لسكن أو شقة، مع إمكانية رفع الصور وتحديد الموقع.
 */
public class AddDormActivity extends BaseActivity {
    
    // مكونات واجهة المستخدم
    private ImageView ivBack, ivSave, ivAddPhoto;
    private TextInputLayout tilTitle, tilCity, tilAddress, tilSize, tilPrice, tilDescription;
    private TextInputEditText etTitle, etCity, etAddress, etSize, etPrice, etDescription;
    
    // بيانات الموقع الجغرافي
    private double currentLatitude, currentLongitude;
    private String currentAddress;
    
    // عدادات الغرف والمرافق
    private TextView tvBedrooms, tvBathrooms;
    private ImageButton ibMinusBedrooms, ibPlusBedrooms, ibMinusBathrooms, ibPlusBathrooms;
    private CheckBox cbWifi, cbParking, cbLaundry, cbGym, cbKitchen, cbAirConditioning, cbBalcony, cbElevator, cbSecurity, cbStorage;
    private RadioGroup rgFurnished;
    private RadioButton rbUnfurnished, rbPartiallyFurnished, rbFullyFurnished;
    private MaterialButton btnSaveDraft, btnPublish, btnGetLocation;
    
    // متغيرات الحالة والبيانات
    private int bedrooms = 1;
    private int bathrooms = 1;
    private boolean isEditMode = false;
    private String listingId = null;
    
    // رفع الصور
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int MAX_PHOTOS = 4;
    private java.util.List<Uri> selectedImageUris = new java.util.ArrayList<>();
    private LinearLayout llPhotosContainer;
    private Uri currentCameraPhotoUri;
    
    // خدمات Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    
    /**
     * تهيئة النشاط، إعداد خدمات Firebase وربط عناصر الواجهة.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dorm);
        
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        
        checkEditMode();
        initializeViews();
        setupClickListeners();
        
        if (isEditMode) {
            loadListingData();
        }
    }
    
    /**
     * التحقق مما إذا كان النشاط قد فُتح لتعديل عقار موجود مسبقاً.
     */
    private void checkEditMode() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEdit", false);
        if (isEditMode) {
            listingId = intent.getStringExtra("listingId");
        }
    }
    
    /**
     * ربط متغيرات الكود بالعناصر الموجودة في ملف الـ XML.
     */
    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        ivSave = findViewById(R.id.ivSave);
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
        rgFurnished = findViewById(R.id.rgFurnished);
        rbUnfurnished = findViewById(R.id.rbUnfurnished);
        rbPartiallyFurnished = findViewById(R.id.rbPartiallyFurnished);
        rbFullyFurnished = findViewById(R.id.rbFullyFurnished);
        tvBedrooms = findViewById(R.id.tvBedrooms);
        tvBathrooms = findViewById(R.id.tvBathrooms);
        ibMinusBedrooms = findViewById(R.id.ibMinusBedrooms);
        ibPlusBedrooms = findViewById(R.id.ibPlusBedrooms);
        ibMinusBathrooms = findViewById(R.id.ibMinusBathrooms);
        ibPlusBathrooms = findViewById(R.id.ibPlusBathrooms);
        ivAddPhoto = findViewById(R.id.ivAddPhoto);
        llPhotosContainer = findViewById(R.id.llPhotosContainer);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnPublish = findViewById(R.id.btnPublish);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        setupAutoCapitalization();
    }
    
    /**
     * إعداد مستمعي النقرات لجميع الأزرار (إضافة صور، حفظ، نشر، تحديد موقع).
     */
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        ivSave.setOnClickListener(v -> saveDraft());
        ivAddPhoto.setOnClickListener(v -> showPhotoPickerDialog());
        
        ibMinusBedrooms.setOnClickListener(v -> {
            if (bedrooms > 0) { bedrooms--; tvBedrooms.setText(String.valueOf(bedrooms)); }
        });
        ibPlusBedrooms.setOnClickListener(v -> {
            if (bedrooms < 10) { bedrooms++; tvBedrooms.setText(String.valueOf(bedrooms)); }
        });
        ibMinusBathrooms.setOnClickListener(v -> {
            if (bathrooms > 0) { bathrooms--; tvBathrooms.setText(String.valueOf(bathrooms)); }
        });
        ibPlusBathrooms.setOnClickListener(v -> {
            if (bathrooms < 10) { bathrooms++; tvBathrooms.setText(String.valueOf(bathrooms)); }
        });
        
        btnSaveDraft.setOnClickListener(v -> saveDraft());
        btnPublish.setOnClickListener(v -> publishListing());
        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
    }
    
    /**
     * تحميل بيانات العقار من قاعدة البيانات عند فتح الشاشة في وضع التعديل.
     */
    private void loadListingData() {
        // سيتم تحميل البيانات من Firebase لاحقاً
    }
    
    /**
     * التحقق من صحة جميع المدخلات قبل حفظ أو نشر العقار.
     * @return true إذا كانت البيانات مكتملة وصحيحة.
     */
    private boolean validateInputs() {
        boolean isValid = true;
        if (etTitle.getText().toString().trim().isEmpty()) { tilTitle.setError("العنوان مطلوب"); isValid = false; }
        if (etCity.getText().toString().trim().isEmpty()) { tilCity.setError("المدينة مطلوبة"); isValid = false; }
        if (etPrice.getText().toString().trim().isEmpty()) { tilPrice.setError("السعر مطلوب"); isValid = false; }
        if (selectedImageUris.isEmpty()) { Toast.makeText(this, "يرجى إضافة صورة واحدة على الأقل", Toast.LENGTH_SHORT).show(); isValid = false; }
        return isValid;
    }
    
    /**
     * حفظ العقار كمسودة (غير مرئية للمستخدمين الآخرين).
     */
    private void saveDraft() {
        if (!validateInputs()) return;
        saveListingToDatabase("Draft");
    }
    
    /**
     * نشر العقار ليصبح مرئياً لجميع مستخدمي التطبيق.
     */
    private void publishListing() {
        if (!validateInputs()) return;
        new AlertDialog.Builder(this)
            .setTitle("نشر العقار")
            .setMessage("هل أنت متأكد أنك تريد نشر هذا العقار؟ سيكون مرئياً للجميع.")
            .setPositiveButton("نشر", (dialog, which) -> saveListingToDatabase("Active"))
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    /**
     * تجميع بيانات العقار والبدء في عملية الحفظ في Firebase.
     * @param status حالة العقار (مسودة أو نشط).
     */
    private void saveListingToDatabase(String status) {
        if (mAuth.getCurrentUser() == null) return;
        
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("جاري الحفظ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        MyListings.ListingItem listing = new MyListings.ListingItem();
        listing.setTitle(etTitle.getText().toString().trim());
        listing.setCity(etCity.getText().toString().trim());
        listing.setPrice("₪" + etPrice.getText().toString().trim());
        listing.setStatus(status);
        listing.setUserId(mAuth.getCurrentUser().getUid());
        listing.setTimestamp(System.currentTimeMillis());
        
        if (selectedImageUris.isEmpty()) saveListingToFirebase(listing, progressDialog);
        else uploadImagesAndSaveListing(listing, progressDialog);
    }
    
    /**
     * رفع الصور المختارة إلى Firebase Storage ثم حفظ رابط الصور في قاعدة البيانات.
     */
    private void uploadImagesAndSaveListing(MyListings.ListingItem listing, android.app.ProgressDialog progressDialog) {
        java.util.List<String> downloadUrls = new java.util.ArrayList<>();
        final int[] uploadedCount = {0};
        final int totalImages = selectedImageUris.size();
        
        for (int i = 0; i < totalImages; i++) {
            Uri imageUri = selectedImageUris.get(i);
            StorageReference imageRef = mStorageRef.child("listings/" + mAuth.getCurrentUser().getUid() + "/" + System.currentTimeMillis() + "_" + i + ".jpg");
            
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    downloadUrls.add(downloadUri.toString());
                    uploadedCount[0]++;
                    if (uploadedCount[0] == totalImages) {
                        listing.setPhotoUrls(downloadUrls);
                        listing.setImageUrl(downloadUrls.get(0));
                        saveListingToFirebase(listing, progressDialog);
                    }
                });
            }).addOnFailureListener(e -> {
                uploadedCount[0]++;
                if (uploadedCount[0] == totalImages) saveListingToFirebase(listing, progressDialog);
            });
        }
    }
    
    /**
     * الحفظ النهائي لبيانات العقار في Firebase Realtime Database.
     */
    private void saveListingToFirebase(MyListings.ListingItem listing, android.app.ProgressDialog progressDialog) {
        DatabaseReference listingRef = isEditMode && listingId != null ? mDatabase.child("listings").child(listingId) : mDatabase.child("listings").push();
        listingRef.setValue(listing).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "تم حفظ العقار بنجاح!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    /**
     * عرض خيار للمستخدم لاختيار صورة من الكاميرا أو المعرض.
     */
    private void showPhotoPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة صورة");
        builder.setItems(new CharSequence[]{"التقاط صورة", "من المعرض"}, (dialog, which) -> {
            if (which == 0) checkCameraPermission();
            else openGallery();
        });
        builder.show();
    }
    
    /**
     * التحقق من منح صلاحية الكاميرا.
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else openCamera();
    }
    
    /**
     * فتح الكاميرا لالتقاط صورة للعقار.
     */
    private void openCamera() {
        try {
            String imageFileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            java.io.File imageFile = new java.io.File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);
            currentCameraPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentCameraPhotoUri);
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {}
    }
    
    /**
     * فتح معرض الصور لاختيار صور للعقار.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    
    /**
     * التعامل مع نتائج التقاط الصور أو اختيارها من المعرض.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                if (data.getData() != null) { addPhotoView(data.getData()); }
            } else if (requestCode == CAMERA_REQUEST && currentCameraPhotoUri != null) {
                addPhotoView(currentCameraPhotoUri);
            }
        }
    }
    
    /**
     * إضافة صورة مصغرة في الواجهة للصورة التي تم اختيارها.
     * @param imageUri مسار الصورة.
     */
    private void addPhotoView(Uri imageUri) {
        if (selectedImageUris.size() >= MAX_PHOTOS) return;
        selectedImageUris.add(imageUri);
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
    
    /**
     * الحصول على إحداثيات الموقع الحالي للعقار باستخدام GPS.
     */
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else getLocationAndShowMap();
    }
    
    /**
     * تفعيل مستشعر الموقع الجغرافي وحفظ الإحداثيات.
     */
    private void getLocationAndShowMap() {
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    showMapDialog();
                }
                @Override public void onStatusChanged(String p, int s, Bundle e) {}
                @Override public void onProviderEnabled(String p) {}
                @Override public void onProviderDisabled(String p) {}
            }, null);
        } catch (SecurityException e) {}
    }
    
    /**
     * إظهار تأكيد للمستخدم بأنه تم تحديد الموقع بنجاح.
     */
    private void showMapDialog() {
        new AlertDialog.Builder(this).setTitle("تم تحديد الموقع").setMessage("تم التقاط موقعك الحالي بنجاح.").setPositiveButton("موافق", null).show();
    }
    
    /**
     * تحويل أول حرف من النص المدخل في العنوان والمدينة إلى حرف كبير تلقائياً.
     */
    private void setupAutoCapitalization() {
        // إعدادات التنسيق التلقائي
    }
}

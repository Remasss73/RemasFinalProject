package remas.example.remasfinalproject;

/**
 * AddDormActivity - Activity for creating and editing dorm/apartment listings
 * نشاط إضافة وإدراج قوائم السكن/الشقق
 * 
 * This activity allows users to create new dormitory or apartment listings with:
 * يسمح هذا النشاط للمستخدمين بإنشاء قوائم جديدة للسكن أو الشقق مع:
 * - Property details (title, city, address, size, price, description)
 *   تفاصيل العقار (العنوان، المدينة، العنوان، الحجم، السعر، الوصف)
 * - Amenities selection (WiFi, parking, gym, etc.)
 *   اختيار المرافق (واي فاي، مواقف، صالة رياضية، إلخ)
 * - Photo upload with 4-photo limit using Firebase Storage
 *   رفع الصور بحد أقصى 4 صور باستخدام تخزين Firebase
 * - Location detection using GPS
 *   تحديد الموقع باستخدام GPS
 * - Bedroom and bathroom counters
 *   عدادات غرف النوم والحمامات
 * 
 * The activity uses Firebase Authentication for user identification,
 * يستخدم النشاط مصادقة Firebase لتحديد هوية المستخدم،
 * Firebase Realtime Database for storing listing data,
 * Firebase Realtime Database لتخزين بيانات القائمة،
 * and Firebase Storage for uploading and storing images.
 * و Firebase Storage لرفع وتخزين الصور.
 * 
 * @author Remas Project Team
 * فريق مشروع REMAS
 * @version 1.0
 */

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
import com.google.firebase.storage.UploadTask;

public class AddDormActivity extends BaseActivity {
    
    // ========== UI Components ==========
    // ========== مكونات واجهة المستخدم ==========
    // Image views for navigation and actions
    // طرق العرض للصور للتنقل والإجراءات
    /**
     * Navigation and action image views
     * طرق عرض الصور للتنقل والإجراءات
     * ivBack: Back button to return to previous screen
     * زر الرجوع للعودة إلى الشاشة السابقة
     * ivSave: Save draft button
     * زر حفظ المسودة
     * ivAddPhoto: Button to add photos from gallery or camera
     * زر إضافة الصور من المعرض أو الكاميرا
     */
    private ImageView ivBack, ivSave, ivAddPhoto;
    
    /**
     * Input field layouts for text inputs with error handling
     * تخطيطات حقول الإدخال للنصوص مع معالجة الأخطاء
     * tilTitle: Layout for property title input
     * تخطيط لإدخال عنوان العقار
     * tilCity: Layout for city input
     * تخطيط لإدخال المدينة
     * tilAddress: Layout for street address input
     * تخطيط لإدخال عنوان الشارع
     * tilSize: Layout for property size input
     * تخطيط لإدخال حجم العقار
     * tilPrice: Layout for monthly rent input
     * تخطيط لإدخال الإيجار الشهري
     * tilDescription: Layout for property description input
     * تخطيط لإدخال وصف العقار
     */
    private TextInputLayout tilTitle, tilCity, tilAddress, tilSize, tilPrice, tilDescription;
    
    /**
     * Text input fields for user data entry
     * حقول إدخال النصوص لإدخال بيانات المستخدم
     * Corresponds to the TextInputLayouts above
     * تتوافق مع تخطيطات TextInputLayout أعلاه
     */
    private TextInputEditText etTitle, etCity, etAddress, etSize, etPrice, etDescription;
    
    // ========== Location Data ==========
    // ========== بيانات الموقع ==========
    /**
     * GPS coordinates for the property location
     * إحداثيات GPS لموقع العقار
     * currentLatitude: Latitude coordinate
     * إحداثي خط العرض
     * currentLongitude: Longitude coordinate
     * إحداثي خط الطول
     */
    private double currentLatitude, currentLongitude;
    
    /**
     * Human-readable address string
     * سلسلة العنوان المقروء للإنسان
     */
    private String currentAddress;
    /**
     * Text views displaying bedroom and bathroom counts
     * طرق عرض النصوص تعرض عدد غرف النوم والحمامات
     */
    private TextView tvBedrooms, tvBathrooms;
    
    /**
     * Image buttons to increment/decrement bedroom and bathroom counts
     * أزرار الصور لزيادة/إنقاص عدد غرف النوم والحمامات
     */
    private ImageButton ibMinusBedrooms, ibPlusBedrooms, ibMinusBathrooms, ibPlusBathrooms;
    /**
     * Checkboxes for property amenities selection
     * مربعات الاختيار لاختيار مرافق العقار
     * Users can select multiple amenities that their property offers
     * يمكن للمستخدمين اختيار عدة مرافق يوفرها عقارهم
     */
    private CheckBox cbWifi, cbParking, cbLaundry, cbGym, cbKitchen, cbAirConditioning, cbBalcony, cbElevator, cbSecurity, cbStorage;
    /**
     * Radio group for furnished status selection (single choice)
     * مجموعة الراديو لاختيار حالة التأثيث (خيار واحد)
     * rgFurnished: Container for radio buttons
     * حاوية لأزرار الراديو
     * rbUnfurnished: Property is unfurnished
     * العقار غير مفروش
     * rbPartiallyFurnished: Property is partially furnished
     * العقار مفروش جزئياً
     * rbFullyFurnished: Property is fully furnished
     * العقار مفروش بالكامل
     */
    private RadioGroup rgFurnished;
    private RadioButton rbUnfurnished, rbPartiallyFurnished, rbFullyFurnished;
    /**
     * Action buttons for listing operations
     * أزرار الإجراءات لعمليات القائمة
     * btnSaveDraft: Save listing as draft (not visible to others)
     * حفظ القائمة كمسودة (غير مرئية للآخرين)
     * btnPublish: Publish listing (visible to all users)
     * نشر القائمة (مرئية لجميع المستخدمين)
     * btnGetLocation: Get current GPS location
     * الحصول على موقع GPS الحالي
     */
    private MaterialButton btnSaveDraft, btnPublish, btnGetLocation;
    
    // ========== Data ==========
    // ========== البيانات ==========
    /**
     * Property room counts (default: 1 each)
     * عدد غرف العقار (الافتراضي: 1 لكل منها)
     */
    private int bedrooms = 1;
    private int bathrooms = 1;
    
    /**
     * Edit mode flag - true if editing existing listing, false if creating new
     * علامة وضع التحرير - صحيح إذا كان تحرير قائمة موجودة، خطأ إذا كان إنشاء جديد
     */
    private boolean isEditMode = false;
    
    /**
     * Unique identifier for the listing (used in edit mode)
     * معرف فريد للقائمة (يستخدم في وضع التحرير)
     */
    private String listingId = null;
    
    // ========== Photo Upload ==========
    // ========== رفع الصور ==========
    /**
     * Request codes for startActivityForResult
     * رموز الطلب ل startActivityForResult
     * PICK_IMAGE_REQUEST: Code for gallery image selection
     * رمز لاختيار صورة من المعرض
     * CAMERA_REQUEST: Code for camera photo capture
     * رمز لالتقاط صورة بالكاميرا
     * PERMISSION_REQUEST_CODE: Code for permission requests
     * رمز لطلبات الأذونات
     */
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    /**
     * List of URIs for selected photos (max 4)
     * قائمة URIs للصور المحددة (حد أقصى 4)
     * These URIs are uploaded to Firebase Storage
     * يتم رفع هذه URIs إلى Firebase Storage
     */
    private java.util.List<Uri> selectedImageUris = new java.util.ArrayList<>();
    
    /**
     * Container layout for displaying selected photo thumbnails
     * تخطيط الحاوية لعرض صور مصغرة للصور المحددة
     */
    private LinearLayout llPhotosContainer;
    
    /**
     * URI for the most recent camera photo
     * URI لآخر صورة التقطت بالكاميرا
     */
    private Uri currentCameraPhotoUri;
    
    // ========== Firebase Services ==========
    // ========== خدمات Firebase ==========
    /**
     * Firebase Authentication instance for user management
     * مثيل مصادقة Firebase لإدارة المستخدمين
     * Used to get current user ID and verify authentication
     * يستخدم للحصول على معرف المستخدم الحالي والتحقق من المصادقة
     */
    private FirebaseAuth mAuth;
    
    /**
     * Firebase Realtime Database reference for storing listing data
     * مرجع Firebase Realtime Database لتخزين بيانات القائمة
     * All listing information is stored under "listings" node
     * يتم تخزين جميع معلومات القائمة تحت عقدة "listings"
     */
    private DatabaseReference mDatabase;
    
    /**
     * Firebase Storage reference for uploading and storing images
     * مرجع Firebase Storage لرفع وتخزين الصور
     * Images are stored under "listings/{userId}/" path
     * يتم تخزين الصور تحت المسار "listings/{userId}/"
     */
    private StorageReference mStorageRef;
    
    /**
     * Maximum number of photos allowed per listing (like Airbnb/Booking.com)
     * الحد الأقصى لعدد الصور المسموح بها لكل قائمة (مثل Airbnb/Booking.com)
     */
    private static final int MAX_PHOTOS = 4;
    
    /**
     * Called when activity is created
     * يتم الاستدعاء عند إنشاء النشاط
     * 
     * Initializes Firebase services, sets up UI components,
     * يقوم بتهيئة خدمات Firebase، وإعداد مكونات واجهة المستخدم،
     * configures click listeners, and loads existing data if in edit mode
     * وتكوين مستمعي النقرات، وتحميل البيانات الموجودة إذا كان في وضع التحرير
     * 
     * @param savedInstanceState Bundle containing previously saved state
     * حزمة تحتوي على الحالة المحفوظة مسبقاً
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dorm);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        
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
    
    /**
     * Checks if activity was opened in edit mode
     * يتحقق مما إذا كان النشاط قد تم فتحه في وضع التحرير
     * 
     * Reads intent extras to determine if we're editing an existing listing
     * يقرأ الإضافات من Intent لتحديد ما إذا كنا نحرر قائمة موجودة
     * or creating a new one
     * أو إنشاء قائمة جديدة
     */
    private void checkEditMode() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEdit", false);
        if (isEditMode) {
            listingId = intent.getStringExtra("listingId");
        }
    }
    
    /**
     * Initializes all UI components by finding them in the layout
     * يقوم بتهيئة جميع مكونات واجهة المستخدم عن طريق العثور عليها في التخطيط
     * 
     * Uses findViewById to get references to all views defined in XML layout
     * يستخدم findViewById للحصول على مراجع لجميع المشاهدات المحددة في تخطيط XML
     */
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
    
    /**
     * Sets up click listeners for all interactive UI elements
     * يقوم بإعداد مستمعي النقرات لجميع عناصر واجهة المستخدم التفاعلية
     * 
     * Defines what happens when users tap buttons, images, or other interactive elements
     * يحدد ما يحدث عندما ينقر المستخدمون على الأزرار أو الصور أو العناصر التفاعلية الأخرى
     */
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
    
    /**
     * Loads existing listing data when in edit mode
     * يقوم بتحميل بيانات القائمة الموجودة عند التواجد في وضع التحرير
     * 
     * Retrieves listing data from Firebase Database and populates UI fields
     * يسترجع بيانات القائمة من Firebase Database ويملأ حقول واجهة المستخدم
     * 
     * TODO: Implement this method to load existing listing data
     * TODO: تنفيذ هذه الطريقة لتحميل بيانات القائمة الموجودة
     */
    private void loadListingData() {
        if (isEditMode) {
            // implementation details
            // تفاصيل التنفيذ
        }
    }
    
    /**
     * Validates all user inputs before saving the listing
     * يتحقق من جميع مدخلات المستخدم قبل حفظ القائمة
     * 
     * Checks that all required fields are filled with valid data:
     * يتحقق من أن جميع الحقول المطلوبة ممتلئة ببيانات صالحة:
     * - Title: minimum 5 characters
     *   العنوان: الحد الأدنى 5 أحرف
     * - City: required
     *   المدينة: مطلوبة
     * - Address: required
     *   العنوان: مطلوب
     * - Size: valid number between 1-10000
     *   الحجم: رقم صالح بين 1-10000
     * - Price: valid number between 1-100000
     *   السعر: رقم صالح بين 1-100000
     * - Description: minimum 20 characters
     *   الوصف: الحد الأدنى 20 حرفاً
     * - Photos: at least one photo required
     *   الصور: مطلوبة صورة واحدة على الأقل
     * 
     * @return true if all inputs are valid, false otherwise
     * صحيح إذا كانت جميع المدخلات صالحة، خطأ خلاف ذلك
     */
    private boolean validateInputs() {
        boolean isValid = true;
        
        if (etTitle.getText() == null || etTitle.getText().toString().trim().isEmpty()) {
            tilTitle.setError("Title is required");
            isValid = false;
        } else if (etTitle.getText().toString().trim().length() < 5) {
            tilTitle.setError("Title must be at least 5 characters");
            isValid = false;
        } else {
            tilTitle.setError(null);
        }
        
        if (etCity.getText() == null || etCity.getText().toString().trim().isEmpty()) {
            tilCity.setError("City is required");
            isValid = false;
        } else {
            tilCity.setError(null);
        }
        
        if (etAddress.getText() == null || etAddress.getText().toString().trim().isEmpty()) {
            tilAddress.setError("Address is required");
            isValid = false;
        } else {
            tilAddress.setError(null);
        }
        
        if (etSize.getText() == null || etSize.getText().toString().trim().isEmpty()) {
            tilSize.setError("Property size is required");
            isValid = false;
        } else {
            try {
                int size = Integer.parseInt(etSize.getText().toString().trim());
                if (size <= 0) {
                    tilSize.setError("Size must be greater than 0");
                    isValid = false;
                } else if (size > 10000) {
                    tilSize.setError("Size seems too large");
                    isValid = false;
                } else {
                    tilSize.setError(null);
                }
            } catch (NumberFormatException e) {
                tilSize.setError("Please enter a valid number");
                isValid = false;
            }
        }
        
        if (etPrice.getText() == null || etPrice.getText().toString().trim().isEmpty()) {
            tilPrice.setError("Monthly rent is required");
            isValid = false;
        } else {
            try {
                int price = Integer.parseInt(etPrice.getText().toString().trim());
                if (price <= 0) {
                    tilPrice.setError("Rent must be greater than 0");
                    isValid = false;
                } else if (price > 100000) {
                    tilPrice.setError("Rent seems too high");
                    isValid = false;
                } else {
                    tilPrice.setError(null);
                }
            } catch (NumberFormatException e) {
                tilPrice.setError("Please enter a valid number");
                isValid = false;
            }
        }
        
        if (etDescription.getText() == null || etDescription.getText().toString().trim().isEmpty()) {
            tilDescription.setError("Description is required");
            isValid = false;
        } else if (etDescription.getText().toString().trim().length() < 20) {
            tilDescription.setError("Description must be at least 20 characters");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }
        
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Please add at least one photo", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Saves the listing as a draft
     * يحفظ القائمة كمسودة
     * 
     * Drafts are not visible to other users and can be edited later
     * المسودات غير مرئية للمستخدمين الآخرين ويمكن تحريرها لاحقاً
     * Status is set to "Draft" in the database
     * يتم تعيين الحالة على "مسودة" في قاعدة البيانات
     */
    private void saveDraft() {
        if (!validateInputs()) return;
        saveListingToDatabase("Draft");
    }
    
    /**
     * Publishes the listing to make it visible to all users
     * ينشر القائمة لجعلها مرئية لجميع المستخدمين
     * 
     * Shows a confirmation dialog before publishing
     * يظهر مربع حوار تأكيد قبل النشر
     * Status is set to "Active" in the database
     * يتم تعيين الحالة على "نشط" في قاعدة البيانات
     */
    private void publishListing() {
        if (!validateInputs()) return;
        
        // Show confirmation dialog before publishing
        // إظهار مربع حوار تأكيد قبل النشر
        new AlertDialog.Builder(this)
            .setTitle("Publish Listing")
            .setMessage("Are you sure you want to publish this listing? It will be visible to all users.")
            .setPositiveButton("Publish", (dialog, which) -> saveListingToDatabase("Active"))
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Saves the listing data to Firebase Database
     * يحفظ بيانات القائمة إلى Firebase Database
     * 
     * This is the main method that orchestrates the save process:
     * هذه هي الطريقة الرئيسية التي تنسق عملية الحفظ:
     * 1. Validates user is authenticated
     *    يتحقق من مصادقة المستخدم
     * 2. Creates a ListingItem object with all form data
     *    ينشئ كائن ListingItem مع جميع بيانات النموذج
     * 3. Uploads images to Firebase Storage (if any)
     *    يرفع الصور إلى Firebase Storage (إذا وجدت)
     * 4. Saves the listing to Firebase Realtime Database
     *    يحفظ القائمة إلى Firebase Realtime Database
     * 
     * @param status The listing status ("Draft" or "Active")
     * حالة القائمة ("مسودة" أو "نشط")
     */
    private void saveListingToDatabase(String status) {
        try {
            if (mAuth.getCurrentUser() == null) return;
            
            // Show loading indicator
            android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
            progressDialog.setMessage("Saving listing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            
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
            
            // Upload images to Firebase Storage
            if (selectedImageUris.isEmpty()) {
                // No images, save listing directly
                saveListingToFirebase(listing, progressDialog);
            } else {
                uploadImagesAndSaveListing(listing, progressDialog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Uploads selected images to Firebase Storage
     * يرفع الصور المحددة إلى Firebase Storage
     * 
     * This method handles the asynchronous upload of multiple images:
     * تتعامل هذه الطريقة مع الرفع غير المتزامن لصور متعددة:
     * 1. Shows progress dialog with upload count
     *    يظهر مربع حوار التقدم مع عدد الرفع
     * 2. Uploads each image to Firebase Storage under user's folder
     *    يرفع كل صورة إلى Firebase Storage تحت مجلد المستخدم
     * 3. Gets download URLs for each uploaded image
     *    يحصل على عناوين التنزيل لكل صورة مرفوعة
     * 4. Saves URLs to the listing object
     *    يحفظ عناوين URL في كائن القائمة
     * 5. Continues even if some uploads fail (graceful degradation)
     *    يستمر حتى إذا فشلت بعض عمليات الرفع (تدهور أنيق)
     * 
     * @param listing The listing item to save image URLs to
     * عنصر القائمة لحفظ عناوين URL للصور فيه
     * @param progressDialog Progress dialog to show upload status
     * مربع حوار التقدم لإظهار حالة الرفع
     */
    private void uploadImagesAndSaveListing(MyListings.ListingItem listing, android.app.ProgressDialog progressDialog) {
        java.util.List<String> downloadUrls = new java.util.ArrayList<>();
        final int[] uploadedCount = {0};
        final int[] failedCount = {0};
        final int totalImages = selectedImageUris.size();
        
        progressDialog.setMessage("Uploading photos (0/" + totalImages + ")...");
        
        for (int i = 0; i < selectedImageUris.size(); i++) {
            final int imageIndex = i + 1;
            Uri imageUri = selectedImageUris.get(i);
            String fileName = "listing_" + System.currentTimeMillis() + "_" + i + ".jpg";
            StorageReference imageRef = mStorageRef.child("listings/" + mAuth.getCurrentUser().getUid() + "/" + fileName);
            
            imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        downloadUrls.add(downloadUri.toString());
                        uploadedCount[0]++;
                        progressDialog.setMessage("Uploading photos (" + uploadedCount[0] + "/" + totalImages + ")...");
                        
                        if (uploadedCount[0] + failedCount[0] == totalImages) {
                            listing.setPhotoUrls(downloadUrls);
                            if (!downloadUrls.isEmpty()) {
                                listing.setImageUrl(downloadUrls.get(0));
                            }
                            saveListingToFirebase(listing, progressDialog);
                        }
                    }).addOnFailureListener(e -> {
                        failedCount[0]++;
                        if (uploadedCount[0] + failedCount[0] == totalImages) {
                            listing.setPhotoUrls(downloadUrls);
                            if (!downloadUrls.isEmpty()) {
                                listing.setImageUrl(downloadUrls.get(0));
                            }
                            saveListingToFirebase(listing, progressDialog);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    failedCount[0]++;
                    if (uploadedCount[0] + failedCount[0] == totalImages) {
                        listing.setPhotoUrls(downloadUrls);
                        if (!downloadUrls.isEmpty()) {
                            listing.setImageUrl(downloadUrls.get(0));
                        }
                        saveListingToFirebase(listing, progressDialog);
                    }
                });
        }
    }
    
    /**
     * Saves the listing data to Firebase Realtime Database
     * يحفظ بيانات القائمة إلى Firebase Realtime Database
     * 
     * After images are uploaded (or if no images), this method saves
     * بعد رفع الصور (أو إذا لم تكن هناك صور)، تحفظ هذه الطريقة
     * the complete listing data to the database
     * بيانات القائمة الكاملة إلى قاعدة البيانات
     * 
     * Uses either the existing listing ID (edit mode) or generates a new one
     * يستخدم إما معرف القائمة الموجود (وضع التحرير) أو يولد معرفاً جديداً
     * 
     * @param listing The complete listing item with all data
     * عنصر القائمة الكامل مع جميع البيانات
     * @param progressDialog Progress dialog to dismiss after save
     * مربع حوار التقدم لإغلاقه بعد الحفظ
     */
    private void saveListingToFirebase(MyListings.ListingItem listing, android.app.ProgressDialog progressDialog) {
        progressDialog.setMessage("Saving listing...");
        DatabaseReference listingRef = isEditMode && listingId != null ? mDatabase.child("listings").child(listingId) : mDatabase.child("listings").push();
        listingRef.setValue(listing).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(AddDormActivity.this, "Listing saved successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddDormActivity.this, MyListings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AddDormActivity.this, "Failed to save listing. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * Shows a dialog to choose photo source (camera or gallery)
     * يظهر مربع حوار لاختيار مصدر الصورة (كاميرا أو معرض)
     * 
     * Users can choose between:
     * يمكن للمستخدمين الاختيار بين:
     * - Taking a new photo with camera
     *   التقاط صورة جديدة بالكاميرا
     * - Selecting from device gallery
     *   الاختيار من معرض الجهاز
     */
    private void showPhotoPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
            if (which == 0) checkCameraPermission();
            else openGallery();
        });
        builder.show();
    }
    
    /**
     * Checks and requests camera permission if needed
     * يتحقق ويطلب إذن الكاميرا إذا لزم الأمر
     * 
     * On Android 13+, only camera permission is needed
     * على Android 13+، يلزم إذن الكاميرا فقط
     * On older versions, both camera and storage permissions are needed
     * على الإصدارات الأقدم، يلزم إذن الكاميرا والتخزين
     */
    private void checkCameraPermission() {
        String[] permissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? new String[]{Manifest.permission.CAMERA} : new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        else openCamera();
    }
    
    /**
     * Opens the device camera to take a photo
     * يفتح كاميرا الجهاز لالتقاط صورة
     * 
     * Creates a temporary file to store the photo
     * ينشئ ملفاً مؤقتاً لتخزين الصورة
     * Uses FileProvider to get URI for the file
     * يستخدم FileProvider للحصول على URI للملف
     * 
     * Photo is saved to app's external files directory
     * يتم حفظ الصورة في دليل الملفات الخارجية للتطبيق
     */
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
    
    /**
     * Opens the device gallery to select photos
     * يفتح معرض الجهاز لاختيار الصور
     * 
     * Allows multiple photo selection (up to 4 total)
     * يسمح باختيار صور متعددة (حتى 4 صور إجمالاً)
     * Uses Intent.ACTION_GET_CONTENT with EXTRA_ALLOW_MULTIPLE
     * يستخدم Intent.ACTION_GET_CONTENT مع EXTRA_ALLOW_MULTIPLE
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }
    
    /**
     * Handles permission request results
     * يتعامل مع نتائج طلبات الأذونات
     * 
     * Called when user grants or denies permission requests
     * يتم الاستدعاء عندما يمنح المستخدم أو يرفض طلبات الأذونات
     * 
     * @param requestCode The code passed when requesting permission
     * الرمز الممر عند طلب الإذن
     * @param permissions The requested permissions
     * الأذونات المطلوبة
     * @param grantResults Grant results for each permission
     * نتائج المنح لكل إذن
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera();
        else if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) getCurrentLocation();
    }
    
    /**
     * Handles results from camera and gallery activities
     * يتعامل مع نتائج أنشطة الكاميرا والمعرض
     * 
     * Called when user returns from camera or gallery with selected photos
     * يتم الاستدعاء عندما يعود المستخدم من الكاميرا أو المعرض بالصور المحددة
     * Enforces 4-photo limit and adds photos to the display
     * يفرض حد 4 صور ويضيف الصور إلى العرض
     * 
     * @param requestCode The request code (PICK_IMAGE_REQUEST or CAMERA_REQUEST)
 * رمز الطلب (PICK_IMAGE_REQUEST أو CAMERA_REQUEST)
     * @param resultCode Result code (RESULT_OK if successful)
     * رمز النتيجة (RESULT_OK إذا كان ناجحاً)
     * @param data Intent containing selected image URIs
     * يحتوي Intent على URIs الصور المحددة
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int slotsAvailable = MAX_PHOTOS - selectedImageUris.size();
                    int imagesToAdd = Math.min(count, slotsAvailable);
                    for (int i = 0; i < imagesToAdd; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        if (!selectedImageUris.contains(imageUri)) {
                            selectedImageUris.add(imageUri);
                            addPhotoView(imageUri);
                        }
                    }
                    if (count > slotsAvailable) {
                        Toast.makeText(this, "Maximum " + MAX_PHOTOS + " photos allowed", Toast.LENGTH_SHORT).show();
                    }
                } else if (data.getData() != null) {
                    if (selectedImageUris.size() < MAX_PHOTOS) {
                        Uri imageUri = data.getData();
                        if (!selectedImageUris.contains(imageUri)) {
                            selectedImageUris.add(imageUri);
                            addPhotoView(imageUri);
                        }
                    } else {
                        Toast.makeText(this, "Maximum " + MAX_PHOTOS + " photos allowed", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == CAMERA_REQUEST && currentCameraPhotoUri != null) {
                if (selectedImageUris.size() < MAX_PHOTOS) {
                    selectedImageUris.add(currentCameraPhotoUri);
                    addPhotoView(currentCameraPhotoUri);
                    currentCameraPhotoUri = null;
                } else {
                    Toast.makeText(this, "Maximum " + MAX_PHOTOS + " photos allowed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    /**
     * Adds a photo thumbnail to the photos container
     * يضيف صورة مصغرة إلى حاوية الصور
     * 
     * Inflates the item_photo layout and adds it to the horizontal scroll view
     * يضيف تخطيط item_photo إلى عرض التمرير الأفقي
     * Includes delete button to remove the photo
     * يتضمن زر حذف لإزالة الصورة
     * Shows toast with current photo count
     * يظهر إشعاراً بعدد الصور الحالي
     * 
     * @param imageUri URI of the photo to display
     * URI للصورة لعرضها
     */
    private void addPhotoView(Uri imageUri) {
        View photoView = LayoutInflater.from(this).inflate(R.layout.item_photo, null);
        ImageView ivPhoto = photoView.findViewById(R.id.ivPhoto);
        ImageView ivDelete = photoView.findViewById(R.id.ivDelete);
        ivPhoto.setImageURI(imageUri);
        ivDelete.setOnClickListener(v -> {
            selectedImageUris.remove(imageUri);
            llPhotosContainer.removeView(photoView);
            Toast.makeText(this, "Photo removed (" + selectedImageUris.size() + "/" + MAX_PHOTOS + ")", Toast.LENGTH_SHORT).show();
        });
        llPhotosContainer.addView(photoView, llPhotosContainer.getChildCount() - 1);
        Toast.makeText(this, "Photo added (" + selectedImageUris.size() + "/" + MAX_PHOTOS + ")", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Requests location permission and gets current GPS location
     * يطلب إذن الموقع ويحصل على موقع GPS الحالي
     * 
     * Checks for location permissions and requests if not granted
     * يتحقق من أذونات الموقع ويطلبها إذا لم تمنح
     */
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        else getLocationAndShowMap();
    }
    
    /**
     * Gets current location from GPS or network provider
     * يحصل على الموقع الحالي من مزود GPS أو الشبكة
     * 
     * Tries GPS first, falls back to network location if GPS unavailable
     * يحاول GPS أولاً، يرجع إلى موقع الشبكة إذا كان GPS غير متاح
     * Shows confirmation dialog when location is found
     * يظهر مربع حوار تأكيد عند العثور على الموقع
     */
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
    
    /**
     * Shows confirmation dialog when location is successfully set
     * يظهر مربع حوار تأكيد عند تعيين الموقع بنجاح
     * 
     * Displays coordinates in address field if address is empty
     * يعرض الإحداثيات في حقل العنوان إذا كان العنوان فارغاً
     */
    private void showMapDialog() {
        if (etAddress.getText().toString().trim().isEmpty()) etAddress.setText("Location detected (" + String.format("%.4f", currentLatitude) + ", " + String.format("%.4f", currentLongitude) + ")");
        new AlertDialog.Builder(this).setTitle("📍 Location Set!").setMessage("Your location has been set.").setPositiveButton("✅ Confirm", null).show();
    }
    
    /**
     * Sets up auto-capitalization for title and city fields
     * يقوم بإعداد التكيف التلقائي لحقول العنوان والمدينة
     * 
     * Automatically capitalizes the first letter of title and city
     * يقوم تلقائياً بتكبير الحرف الأول من العنوان والمدينة
     * Improves data consistency and appearance
     * يحسن اتساق البيانات والمظهر
     */
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

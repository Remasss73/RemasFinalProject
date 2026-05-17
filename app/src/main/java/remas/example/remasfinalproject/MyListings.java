package remas.example.remasfinalproject;

/**
 * MyListings - Activity for displaying and managing user's property listings
 * نشاط لعرض وإدارة قوائم عقارات المستخدم
 * 
 * This activity shows all listings created by the current user:
 * يعرض هذا النشاط جميع القوائم التي أنشأها المستخدم الحالي:
 * - Displays listings in a RecyclerView with cards
 *   يعرض القوائم في RecyclerView مع بطاقات
 * - Shows listing images, title, price, location, and status
 *   يعرض صور القائمة والعنوان والسعر والموقع والحالة
 * - Allows viewing listing details
 *   يسمح بعرض تفاصيل القائمة
 * - Allows editing existing listings
 *   يسمح بتحرير القوائم الموجودة
 * - Shows empty state when no listings exist
 *   يظهر حالة فارغة عندما لا توجد قوائم
 * 
 * Uses Firebase Authentication to get current user,
 * يستخدم مصادقة Firebase للحصول على المستخدم الحالي،
 * Firebase Realtime Database to load listings,
 * Firebase Realtime Database لتحميل القوائم،
 * and Glide to load listing images
 * و Glide لتحميل صور القائمة
 * 
 * @author Remas Project Team
 * فريق مشروع REMAS
 * @version 1.0
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.button.MaterialButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyListings extends BaseActivity {
    
    // ========== UI Components ==========
    // ========== مكونات واجهة المستخدم ==========
    /**
     * Navigation and filter image views
     * طرق عرض الصور للتنقل والتصفية
     */
    private ImageView ivBack, ivFilter;
    /**
     * RecyclerView for displaying listing cards
     * RecyclerView لعرض بطاقات القائمة
     */
    private RecyclerView rvMyListings;
    
    /**
     * Empty state layout shown when user has no listings
     * تخطيط الحالة الفارغة يظهر عندما لا يكون لدى المستخدم قوائم
     */
    private LinearLayout emptyStateLayout;
    
    /**
     * Button to add first listing (shown in empty state)
     * زر لإضافة القائمة الأولى (يظهر في الحالة الفارغة)
     */
    private MaterialButton btnAddFirstListing;
    
    /**
     * Floating action button to add new listing
     * زر الإجراء العائم لإضافة قائمة جديدة
     */
    private FloatingActionButton fabAddListing;
    // ========== Data & Adapters ==========
    // ========== البيانات والمحولات ==========
    /**
     * Adapter for displaying listings in RecyclerView
     * محول لعرض القوائم في RecyclerView
     */
    private MyListingAdapter listingAdapter;
    
    /**
     * List of listing items to display
     * قائمة عناصر القائمة للعرض
     */
    private List<ListingItem> listingList;
    
    // ========== Firebase Services ==========
    // ========== خدمات Firebase ==========
    /**
     * Firebase Authentication instance for user management
     * مثيل مصادقة Firebase لإدارة المستخدمين
     */
    private FirebaseAuth mAuth;
    
    /**
     * Firebase Realtime Database reference for loading listings
     * مرجع Firebase Realtime Database لتحميل القوائم
     */
    private DatabaseReference mDatabase;
    
    /**
     * Called when activity is created
     * يتم الاستدعاء عند إنشاء النشاط
     * 
     * Initializes Firebase services, sets up RecyclerView,
     * يقوم بتهيئة خدمات Firebase، وإعداد RecyclerView،
     * and loads user's listings from database
     * وتحميل قوائم المستخدم من قاعدة البيانات
     * 
     * @param savedInstanceState Bundle containing previously saved state
     * حزمة تحتوي على الحالة المحفوظة مسبقاً
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);
        
        try {
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            
            initializeViews();
            setupRecyclerView();
            setupClickListeners();
            loadMyListings();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes all UI components by finding them in the layout
     * يقوم بتهيئة جميع مكونات واجهة المستخدم عن طريق العثور عليها في التخطيط
     */
    private void initializeViews() {
        try {
            ivBack = findViewById(R.id.ivBack);
            ivFilter = findViewById(R.id.ivFilter);
            rvMyListings = findViewById(R.id.rvMyListings);
            emptyStateLayout = findViewById(R.id.emptyStateLayout);
            btnAddFirstListing = findViewById(R.id.btnAddFirstListing);
            fabAddListing = findViewById(R.id.fabAddListing);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Sets up RecyclerView with adapter and layout manager
     * يقوم بإعداد RecyclerView مع المحول ومدير التخطيط
     * 
     * Uses LinearLayoutManager for vertical scrolling list
     * يستخدم LinearLayoutManager لقائمة التمرير العمودي
     */
    private void setupRecyclerView() {
        try {
            listingList = new ArrayList<>();
            listingAdapter = new MyListingAdapter(listingList, this::onListingClick, this::onEditClick);
            rvMyListings.setLayoutManager(new LinearLayoutManager(this));
            rvMyListings.setAdapter(listingAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up RecyclerView: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Sets up click listeners for all interactive UI elements
     * يقوم بإعداد مستمعي النقرات لجميع عناصر واجهة المستخدم التفاعلية
     */
    private void setupClickListeners() {
        try {
            if (ivBack != null) {
                ivBack.setOnClickListener(v -> finish());
            }
            if (ivFilter != null) {
                ivFilter.setOnClickListener(v -> showFilterDialog());
            }
            if (btnAddFirstListing != null) {
                btnAddFirstListing.setOnClickListener(v -> {
                    Intent intent = new Intent(MyListings.this, AddDormActivity.class);
                    startActivity(intent);
                });
            }
            if (fabAddListing != null) {
                fabAddListing.setOnClickListener(v -> {
                    Intent intent = new Intent(MyListings.this, AddDormActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up click listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Loads all listings for the current user from Firebase Database
     * يقوم بتحميل جميع القوائم للمستخدم الحالي من Firebase Database
     * 
     * Queries the "listings" node filtered by current user's ID
     * يستفسر عن عقدة "listings" المفلترة حسب معرف المستخدم الحالي
     * Updates RecyclerView when data changes (real-time updates)
     * يحدث RecyclerView عند تغير البيانات (تحديثات في الوقت الفعلي)
     */
    private void loadMyListings() {
        try {
            if (mAuth.getCurrentUser() == null) {
                updateEmptyState();
                return;
            }
            
            mDatabase.child("listings").orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                listingList.clear();
                                
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ListingItem listing = snapshot.getValue(ListingItem.class);
                                    if (listing != null) {
                                        listing.setListingId(snapshot.getKey());
                                        listingList.add(listing);
                                    }
                                }
                                
                                listingAdapter.notifyDataSetChanged();
                                updateEmptyState();
                            } catch (Exception e) {
                                Toast.makeText(MyListings.this, "Error processing listings", Toast.LENGTH_SHORT).show();
                            }
                        }
                        
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MyListings.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error loading listings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Updates the visibility of empty state vs listings view
     * يحدث ظهور الحالة الفارغة مقابل عرض القوائم
     * 
     * Shows empty state when listingList is empty
     * يظهر الحالة الفارغة عندما تكون listingList فارغة
     * Shows RecyclerView when listings exist
     * يظهر RecyclerView عندما توجد قوائم
     */
    private void updateEmptyState() {
        try {
            if (emptyStateLayout != null && rvMyListings != null) {
                if (listingList.isEmpty()) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    rvMyListings.setVisibility(View.GONE);
                } else {
                    emptyStateLayout.setVisibility(View.GONE);
                    rvMyListings.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating empty state: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Handles click on a listing item
     * يتعامل مع النقر على عنصر قائمة
     * 
     * Opens ListingDetailsActivity to show full listing details
     * يفتح ListingDetailsActivity لعرض تفاصيل القائمة الكاملة
     * 
     * @param listing The listing item that was clicked
     * عنصر القائمة الذي تم النقر عليه
     */
    private void onListingClick(ListingItem listing) {
        Intent intent = new Intent(MyListings.this, ListingDetailsActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        startActivity(intent);
    }
    
    /**
     * Handles click on edit button for a listing
     * يتعامل مع النقر على زر التحرير لقائمة
     * 
     * Opens AddDormActivity in edit mode with the listing data
     * يفتح AddDormActivity في وضع التحرير مع بيانات القائمة
     * 
     * @param listing The listing item to edit
     * عنصر القائمة للتحرير
     */
    private void onEditClick(ListingItem listing) {
        Intent intent = new Intent(MyListings.this, AddDormActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }
    
    /**
     * Shows filter dialog for sorting and filtering listings
     * يظهر مربع حوار التصفية لفرز وتصفية القوائم
     * 
     * Currently shows placeholder options
     * يعرض حالياً خيارات نائبة
     * TODO: Implement actual filtering logic
     * TODO: تنفيذ منطق التصفية الفعلي
     */
    private void showFilterDialog() {
        String[] options = {"All", "Active", "Inactive", "Price: Low to High", "Price: High to Low"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Filter Listings")
                .setItems(options, (dialog, which) -> {
                    Toast.makeText(this, "Filter: " + options[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }
    
    /**
     * ListingItem - Data model for a property listing
     * ListingItem - نموذج البيانات لقائمة عقار
     * 
     * This class represents a single dorm/apartment listing with all its properties:
     * يمثل هذا الفئة قائمة سكن/شقة واحدة مع جميع خصائصها:
     * - Basic info: title, price, location, description
     *   معلومات أساسية: العنوان، السعر، الموقع، الوصف
     * - Property details: bedrooms, bathrooms, size
     *   تفاصيل العقار: غرف النوم، الحمامات، الحجم
     * - Images: imageUrl (primary), photoUrls (all images)
     *   الصور: imageUrl (الأساسية)، photoUrls (جميع الصور)
     * - Status: Draft, Active, or Inactive
     *   الحالة: مسودة، نشط، أو غير نشط
     * - Metadata: timestamp, userId, location coordinates
     *   البيانات الوصفية: الطابع الزمني، معرف المستخدم، إحداثيات الموقع
     * 
     * Used with Firebase Realtime Database for serialization/deserialization
     * يستخدم مع Firebase Realtime Database للتسلسل/إلغاء التسلسل
     */
    public static class ListingItem {
        private String listingId, title, price, location, city, area, address, description, imageUrl, userId, status;
        private int bedrooms, bathrooms, size;
        private long timestamp;
        private List<String> amenities;
        private double latitude, longitude;
        private List<String> photoUrls;
        
        /**
         * Default constructor required by Firebase for deserialization
         * المُنشئ الافتراضي المطلوب من Firebase لإلغاء التسلسل
         */
        public ListingItem() {}
        
        // ========== Getters and Setters ==========
        // ========== الجوابت والمُحددات ==========
        public String getListingId() { return listingId; }
        public void setListingId(String listingId) { this.listingId = listingId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getBedrooms() { return bedrooms; }
        public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }
        public int getBathrooms() { return bathrooms; }
        public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public int getArea2() { return size; }
        public void setArea2(int area) { this.size = area; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public List<String> getAmenities() { return amenities; }
        public void setAmenities(List<String> amenities) { this.amenities = amenities; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public List<String> getPhotoUrls() { return photoUrls; }
        public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
    }
    
    /**
     * MyListingAdapter - RecyclerView adapter for displaying listing cards
     * MyListingAdapter - محول RecyclerView لعرض بطاقات القائمة
     * 
     * This adapter binds ListingItem data to the my_listing_item layout:
     * يربط هذا المحول بيانات ListingItem بتخطيط my_listing_item:
     * - Loads listing images using Glide
     *   يحمل صور القائمة باستخدام Glide
     * - Displays listing information (title, price, location, etc.)
     *   يعرض معلومات القائمة (العنوان، السعر، الموقع، إلخ)
     * - Handles click events for viewing details and editing
     *   يتعامل مع أحداث النقر لعرض التفاصيل والتحرير
     * - Shows status badge (Active/Draft)
     *   يظهر شارة الحالة (نشط/مسودة)
     */
    private static class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.ListingViewHolder> {
        
        private List<ListingItem> listings;
        private OnListingClickListener onListingClickListener;
        private OnEditClickListener onEditClickListener;
        
        /**
         * Interface for handling listing item clicks
         * واجهة للتعامل مع نقرات عنصر القائمة
         */
        public interface OnListingClickListener {
            void onListingClick(ListingItem listing);
        }
        
        /**
         * Interface for handling edit button clicks
         * واجهة للتعامل مع نقرات زر التحرير
         */
        public interface OnEditClickListener {
            void onEditClick(ListingItem listing);
        }
        
        /**
         * Constructor for the adapter
         * مُنشئ المحول
         * 
         * @param listings List of listing items to display
         * قائمة عناصر القائمة للعرض
         * @param onListingClickListener Listener for item clicks
         * مستمع لنقرات العناصر
         * @param onEditClickListener Listener for edit button clicks
         * مستمع لنقرات زر التحرير
         */
        public MyListingAdapter(List<ListingItem> listings, OnListingClickListener onListingClickListener, OnEditClickListener onEditClickListener) {
            this.listings = listings;
            this.onListingClickListener = onListingClickListener;
            this.onEditClickListener = onEditClickListener;
        }
        
        /**
         * Called when RecyclerView needs a new ViewHolder
         * يتم الاستدعاء عندما يحتاج RecyclerView إلى ViewHolder جديد
         * 
         * Inflates the my_listing_item layout
         * يضيف تخطيط my_listing_item
         */
        @NonNull
        @Override
        public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_listing_item, parent, false);
            return new ListingViewHolder(view);
        }
        
        /**
         * Binds data to the ViewHolder at the specified position
         * يربط البيانات بـ ViewHolder في الموضع المحدد
         * 
         * Loads image with Glide, sets text fields, configures click listeners
         * يحمل الصورة مع Glide، يضبط حقول النص، يكوين مستمعي النقرات
         * 
         * @param holder The ViewHolder to bind data to
         * ViewHolder لربط البيانات به
         * @param position The position in the list
         * الموضع في القائمة
         */
        @Override
        public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
            ListingItem listing = listings.get(position);
            
            holder.tvTitle.setText(listing.getTitle());
            holder.tvPrice.setText(listing.getPrice());
            holder.tvLocation.setText(listing.getLocation());
            holder.tvBedrooms.setText(listing.getBedrooms() + " Beds");
            holder.tvBathrooms.setText(listing.getBathrooms() + " Bath");
            holder.tvArea.setText(listing.getArea() + " m²");
            holder.tvListedDate.setText(getFormattedDate(listing.getTimestamp()));
            holder.tvStatus.setText(listing.getStatus());
            
            holder.tvStatus.setBackgroundColor("Active".equals(listing.getStatus()) ? 0xFF10B981 : 0xFFF59E0B);
            
            // Load image using Glide
            if (listing.getImageUrl() != null && !listing.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                    .load(listing.getImageUrl())
                    .apply(new RequestOptions()
                        .placeholder(android.R.drawable.ic_menu_camera)
                        .error(android.R.drawable.ic_menu_report_image)
                        .centerCrop())
                    .into(holder.ivListingImage);
            } else {
                holder.ivListingImage.setImageResource(android.R.drawable.ic_menu_camera);
            }
            
            holder.itemView.setOnClickListener(v -> {
                if (onListingClickListener != null) {
                    onListingClickListener.onListingClick(listing);
                }
            });
            
            holder.btnEdit.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(listing);
                }
            });
            
            holder.btnViewDetails.setOnClickListener(v -> {
                if (onListingClickListener != null) {
                    onListingClickListener.onListingClick(listing);
                }
            });
        }
        
        /**
         * Returns the total number of listings
         * يرجع العدد الإجمالي للقوائم
         */
        @Override
        public int getItemCount() {
            return listings.size();
        }
        
        /**
         * Formats timestamp to readable date string
         * ينسق الطابع الزمني إلى سلسلة تاريخ مقروءة
         * 
         * @param timestamp Timestamp in milliseconds
         * الطابع الزمني بالمللي ثانية
         * @return Formatted date string (e.g., "Jan 15, 2024")
         * سلسلة التاريخ المنسقة (مثلاً "يناير 15، 2024")
         */
        private String getFormattedDate(long timestamp) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new java.util.Date(timestamp));
        }
        
        /**
         * ListingViewHolder - Holds references to views in a listing card
         * ListingViewHolder - يحمل مراجع للمشاهدات في بطاقة قائمة
         * 
         * Contains all views from my_listing_item layout for efficient recycling
         * يحتوي على جميع المشاهدات من تخطيط my_listing_item لإعادة التدوير الفعال
         */
        static class ListingViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvLocation, tvBedrooms, tvBathrooms, tvArea, tvListedDate, tvStatus;
            MaterialButton btnViewDetails, btnEdit;
            ImageView ivListingImage;
            
            /**
             * Constructor for ViewHolder
             * مُنشئ ViewHolder
             * 
             * Finds all views in the item layout using findViewById
             * يجد جميع المشاهدات في تخطيط العنصر باستخدام findViewById
             * 
             * @param itemView The item view
             * عرض العنصر
             */
            public ListingViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvBedrooms = itemView.findViewById(R.id.tvBedrooms);
                tvBathrooms = itemView.findViewById(R.id.tvBathrooms);
                tvArea = itemView.findViewById(R.id.tvArea);
                tvListedDate = itemView.findViewById(R.id.tvListedDate);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                ivListingImage = itemView.findViewById(R.id.ivListingImage);
            }
        }
    }
}

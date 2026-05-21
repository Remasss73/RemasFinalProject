package remas.example.remasfinalproject;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * نشاط "عقاراتي": يعرض قائمة بجميع العقارات التي قام المستخدم الحالي بنشرها.
 */
public class MyListings extends AppCompatActivity {
    
    private ImageView ivBack, ivFilter;
    private RecyclerView rvMyListings;
    private LinearLayout emptyStateLayout;
    private MaterialButton btnAddFirstListing;
    private FloatingActionButton fabAddListing;
    private MyListingAdapter listingAdapter;
    private List<ListingItem> listingList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    /**
     * يتم استدعاؤها عند إنشاء النشاط؛ تقوم بتهيئة Firebase والواجهة وتحميل العقارات الخاصة بالمستخدم.
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
     * ربط عناصر واجهة المستخدم بمتغيرات الكود.
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
     * إعداد قائمة العرض (RecyclerView) والمحول (Adapter) الخاص بها.
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
     * إعداد مستمعي النقرات للأزرار (الرجوع، التصفية، إضافة عقار).
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
     * تحميل العقارات الخاصة بالمستخدم الحالي فقط من قاعدة بيانات Firebase.
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
     * تحديث حالة الواجهة لإظهار رسالة "لا توجد عقارات" إذا كانت القائمة فارغة.
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
     * معالجة النقر على عقار لفتح شاشة التفاصيل الخاصة به.
     * @param listing العقار الذي تم اختياره.
     */
    private void onListingClick(ListingItem listing) {
        Intent intent = new Intent(MyListings.this, ListingDetailsActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        startActivity(intent);
    }
    
    /**
     * معالجة النقر على زر التعديل لفتح شاشة إضافة/تعديل العقار.
     * @param listing العقار المراد تعديله.
     */
    private void onEditClick(ListingItem listing) {
        Intent intent = new Intent(MyListings.this, AddDormActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }
    
    /**
     * عرض مربع حوار لتصفية أو ترتيب العقارات المعروضة.
     */
    private void showFilterDialog() {
        String[] options = {"الكل", "نشط", "غير نشط", "السعر: من الأقل للأعلى", "السعر: من الأعلى للأقل"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("تصفية العقارات")
                .setItems(options, (dialog, which) -> {
                    Toast.makeText(this, "تصفية: " + options[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }
    
    /**
     * فئة تمثل عنصر العقار الواحد وتحتوي على جميع بياناته.
     */
    public static class ListingItem {
        private String listingId, title, price, location, city, area, address, description, imageUrl, userId, status;
        private String userName, userProfilePicture;
        private int bedrooms, bathrooms, size;
        private long timestamp;
        private List<String> amenities;
        private double latitude, longitude;
        private List<String> photoUrls;
        
        public ListingItem() {}
        
        // Getters and setters
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
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getUserProfilePicture() { return userProfilePicture; }
        public void setUserProfilePicture(String userProfilePicture) { this.userProfilePicture = userProfilePicture; }
    }
    
    /**
     * محول خاص بـ RecyclerView لعرض بطاقات العقارات الخاصة بالمستخدم.
     */
    private static class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.ListingViewHolder> {
        
        private List<ListingItem> listings;
        private OnListingClickListener onListingClickListener;
        private OnEditClickListener onEditClickListener;
        
        public interface OnListingClickListener {
            void onListingClick(ListingItem listing);
        }
        
        public interface OnEditClickListener {
            void onEditClick(ListingItem listing);
        }
        
        public MyListingAdapter(List<ListingItem> listings, OnListingClickListener onListingClickListener, OnEditClickListener onEditClickListener) {
            this.listings = listings;
            this.onListingClickListener = onListingClickListener;
            this.onEditClickListener = onEditClickListener;
        }
        
        @NonNull
        @Override
        public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_listing_item, parent, false);
            return new ListingViewHolder(view);
        }
        
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
            
            if (listing.getImageUrl() != null && !listing.getImageUrl().isEmpty()) {
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(listing.getImageUrl())
                    .apply(new com.bumptech.glide.request.RequestOptions().centerCrop())
                    .into(holder.ivListingImage);
            }
            
            // Load current user's name and profile picture
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userName = dataSnapshot.child("fullName").getValue(String.class);
                            String userProfilePicture = dataSnapshot.child("profileImageUrl").getValue(String.class);
                            
                            if (userName != null && holder.tvUserName != null) {
                                holder.tvUserName.setText(userName);
                            }
                            
                            if (userProfilePicture != null && !userProfilePicture.isEmpty() && holder.ivUserProfile != null) {
                                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                                    .load(userProfilePicture)
                                    .apply(new com.bumptech.glide.request.RequestOptions().circleCrop())
                                    .into(holder.ivUserProfile);
                            }
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
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
        
        @Override
        public int getItemCount() {
            return listings.size();
        }
        
        /**
         * تنسيق التاريخ من طابع زمني (Timestamp) إلى نص مقروء.
         */
        private String getFormattedDate(long timestamp) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new java.util.Date(timestamp));
        }
        
        /**
         * فئة لربط عناصر واجهة المستخدم لكل بطاقة عقار.
         */
        static class ListingViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvLocation, tvBedrooms, tvBathrooms, tvArea, tvListedDate, tvStatus, tvUserName;
            MaterialButton btnViewDetails, btnEdit;
            ImageView ivListingImage, ivUserProfile;
            
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
                tvUserName = itemView.findViewById(R.id.tvUserName);
                btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                ivListingImage = itemView.findViewById(R.id.ivListingImage);
                ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
            }
        }
    }
}

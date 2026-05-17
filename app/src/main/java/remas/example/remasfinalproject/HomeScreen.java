package remas.example.remasfinalproject;

/**
 * HomeScreen - Main home screen displaying recommended listings
 * الشاشة الرئيسية - الشاشة الرئيسية تعرض القوائم الموصى بها
 * 
 * This activity shows listings from other users (not the current user's own listings):
 * يعرض هذا النشاط قوائم من مستخدمين آخرين (ليس قوائم المستخدم الحالي):
 * - Displays "Recommended for you" section
 *   يعرض قسم "موصى به لك"
 * - Shows only active listings from other users
 *   يعرض فقط القوائم النشطة من مستخدمين آخرين
 * - Filters out current user's own listings
 *   يصفي قوائم المستخدم الحالي الخاصة به
 * - Uses RecyclerView with adapter for efficient display
 *   يستخدم RecyclerView مع محول للعرض الفعال
 * - Loads images using Glide
 *   يحمل الصور باستخدام Glide
 * 
 * @author Remas Project Team
 * فريق مشروع REMAS
 * @version 1.0
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;

public class HomeScreen extends BaseActivity {
    
    // UI Components
    private ImageButton ibMenu;
    private LinearLayout llHome, llChats, llMyListings, llProfile;
    private LinearLayout emptyStateLayout;
    private RecyclerView rvListings;
    private ImageView ivUserProfile;
    
    // Firebase Services
    // خدمات Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    
    // Data & Adapter
    // البيانات والمحول
    private List<MyListings.ListingItem> listingList;
    private HomeListingAdapter listingAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        
        // Initialize UI components
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        setupOnBackPressed();
        loadUserProfile();
    }
    
    private void initializeViews() {
        // Header components
        ibMenu = findViewById(R.id.ibMenu);
        ivUserProfile = findViewById(R.id.ivUserProfile);
        
        // Bottom navigation
        llHome = findViewById(R.id.llHome);
        llChats = findViewById(R.id.llChats);
        llMyListings = findViewById(R.id.llMyListings);
        llProfile = findViewById(R.id.llProfile);
        
        // Content components
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        rvListings = findViewById(R.id.rvListings);
    }
    
    private void setupClickListeners() {
        // Menu button click listener
        ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show popup menu
                showPopupMenu();
            }
        });
        
        // Profile picture click listener
        ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        
        // Bottom navigation click listeners
        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on home screen - maybe refresh content
                refreshListings();
                Toast.makeText(HomeScreen.this, "Home", Toast.LENGTH_SHORT).show();
            }
        });
        
        llChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        
        llMyListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, MyListings.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
    
    private void setupRecyclerView() {
        // Setup RecyclerView for listings
        // إعداد RecyclerView للقوائم
        listingList = new ArrayList<>();
        listingAdapter = new HomeListingAdapter(listingList, this::onListingClick);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        rvListings.setAdapter(listingAdapter);
        
        // Load listings from Firebase
        // تحميل القوائم من Firebase
        loadListingsFromFirebase();
    }
    
    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        rvListings.setVisibility(View.GONE);
    }
    
    private void showListings() {
        emptyStateLayout.setVisibility(View.GONE);
        rvListings.setVisibility(View.VISIBLE);
    }
    
    /**
     * Loads listings from Firebase Database
     * يقوم بتحميل القوائم من Firebase Database
     * 
     * Loads only active listings from other users (not current user's own listings)
     * يحمل فقط القوائم النشطة من مستخدمين آخرين (ليس قوائم المستخدم الحالي)
     */
    private void loadListingsFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showEmptyState();
            return;
        }
        
        String currentUserId = currentUser.getUid();
        
        // Query all listings
        // الاستعلام عن جميع القوائم
        mDatabase.child("listings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    listingList.clear();
                    
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MyListings.ListingItem listing = snapshot.getValue(MyListings.ListingItem.class);
                        if (listing != null) {
                            listing.setListingId(snapshot.getKey());
                            
                            // Only add if:
                            // أضف فقط إذا:
                            // 1. Status is "Active"
                            // الحالة هي "نشط"
                            // 2. Not created by current user
                            // لم يتم إنشاؤها بواسطة المستخدم الحالي
                            if ("Active".equals(listing.getStatus()) && 
                                !currentUserId.equals(listing.getUserId())) {
                                listingList.add(listing);
                            }
                        }
                    }
                    
                    listingAdapter.notifyDataSetChanged();
                    
                    // Show empty state or listings
                    // إظهار الحالة الفارغة أو القوائم
                    if (listingList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showListings();
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeScreen.this, "Error loading listings", Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeScreen.this, "Database error", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
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
    private void onListingClick(MyListings.ListingItem listing) {
        Intent intent = new Intent(HomeScreen.this, ListingDetailsActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    private void refreshListings() {
        // Reload listings from Firebase
        // إعادة تحميل القوائم من Firebase
        loadListingsFromFirebase();
        Toast.makeText(this, "Refreshing listings...", Toast.LENGTH_SHORT).show();
    }
    
    private void showPopupMenu() {
        // Create popup menu
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, ibMenu);
        
        // Inflate menu
        popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
        
        // Handle menu item clicks
        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                
                if (id == R.id.action_settings) {
                    Intent intent = new Intent(HomeScreen.this, SettingsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else if (id == R.id.action_help) {
                    Intent intent = new Intent(HomeScreen.this, HelpActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else if (id == R.id.action_about) {
                    showAboutDialog();
                    return true;
                } else if (id == R.id.action_logout) {
                    logoutUser();
                    return true;
                }
                
                return false;
            }
        });
        
        // Show the popup menu
        popup.show();
    }
    
    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About LUXE STAY")
                .setMessage("LUXE STAY - Premium Accommodations\n\nVersion 1.0\n\nFind your perfect living space with our premium dormitory and apartment listings.")
                .setPositiveButton("OK", null)
                .show();
    }
    
    private void logoutUser() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent intent = new Intent(HomeScreen.this, SignIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when screen comes to foreground
        refreshListings();
    }
    
    private void setupOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press - show exit confirmation
                new androidx.appcompat.app.AlertDialog.Builder(HomeScreen.this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    
    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Load user profile data from Firebase
            mDatabase.child("users").child(currentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                                
                                // Load profile image if available
                                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                    // For now, use placeholder - in real app, use image loading library like Glide
                                    ivUserProfile.setImageResource(android.R.drawable.ic_menu_mylocation);
                                } else {
                                    // Use default profile icon
                                    ivUserProfile.setImageResource(android.R.drawable.ic_menu_mylocation);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Use default profile icon on error
                            ivUserProfile.setImageResource(android.R.drawable.ic_menu_mylocation);
                        }
                    });
        } else {
            // User not logged in, use default profile icon
            // المستخدم غير مسجل للدخول، استخدم أيقونة الملف الشخصي الافتراضية
            ivUserProfile.setImageResource(android.R.drawable.ic_menu_mylocation);
        }
    }
    
    /**
     * HomeListingAdapter - RecyclerView adapter for displaying listing cards on home screen
     * HomeListingAdapter - محول RecyclerView لعرض بطاقات القائمة على الشاشة الرئيسية
     * 
     * Similar to MyListingAdapter but optimized for home screen recommendations
     * مشابه لـ MyListingAdapter ولكن محسّن لتوصيات الشاشة الرئيسية
     */
    private static class HomeListingAdapter extends RecyclerView.Adapter<HomeListingAdapter.ListingViewHolder> {
        
        private List<MyListings.ListingItem> listings;
        private OnListingClickListener onListingClickListener;
        
        public interface OnListingClickListener {
            void onListingClick(MyListings.ListingItem listing);
        }
        
        public HomeListingAdapter(List<MyListings.ListingItem> listings, OnListingClickListener onListingClickListener) {
            this.listings = listings;
            this.onListingClickListener = onListingClickListener;
        }
        
        @NonNull
        @Override
        public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_listing_item, parent, false);
            return new ListingViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
            MyListings.ListingItem listing = listings.get(position);
            
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
            // تحميل الصورة باستخدام Glide
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
            
            // Hide edit button on home screen (users can only edit their own listings)
            // إخفاء زر التحرير على الشاشة الرئيسية (يمكن للمستخدمين تحرير قوائمهم الخاصة فقط)
            holder.btnEdit.setVisibility(View.GONE);
        }
        
        @Override
        public int getItemCount() {
            return listings.size();
        }
        
        private String getFormattedDate(long timestamp) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
            return sdf.format(new java.util.Date(timestamp));
        }
        
        static class ListingViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvLocation, tvBedrooms, tvBathrooms, tvArea, tvListedDate, tvStatus;
            MaterialButton btnViewDetails, btnEdit;
            ImageView ivListingImage;
            
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

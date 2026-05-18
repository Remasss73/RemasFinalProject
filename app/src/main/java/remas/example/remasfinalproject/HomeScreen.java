package remas.example.remasfinalproject;

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

/**
 * الشاشة الرئيسية للتطبيق التي تعرض العقارات المتاحة من قبل المستخدمين الآخرين.
 */
public class HomeScreen extends BaseActivity {
    
    // مكونات واجهة المستخدم
    private ImageButton ibMenu;
    private LinearLayout llHome, llChats, llMyListings, llProfile;
    private LinearLayout emptyStateLayout;
    private RecyclerView rvListings;
    private ImageView ivUserProfile;
    
    // خدمات Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    
    // البيانات والمحول (Adapter)
    private List<MyListings.ListingItem> listingList;
    private HomeListingAdapter listingAdapter;
    
    /**
     * يتم استدعاؤها عند إنشاء الشاشة؛ تقوم بتهيئة Firebase والواجهة وتحميل البيانات.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        setupOnBackPressed();
        loadUserProfile();
    }
    
    /**
     * ربط متغيرات الكود بالعناصر الموجودة في ملف الـ XML.
     */
    private void initializeViews() {
        ibMenu = findViewById(R.id.ibMenu);
        ivUserProfile = findViewById(R.id.ivUserProfile);
        llHome = findViewById(R.id.llHome);
        llChats = findViewById(R.id.llChats);
        llMyListings = findViewById(R.id.llMyListings);
        llProfile = findViewById(R.id.llProfile);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        rvListings = findViewById(R.id.rvListings);
    }
    
    /**
     * إعداد مستمعي النقرات لجميع الأزرار وعناصر التنقل في الشاشة.
     */
    private void setupClickListeners() {
        ibMenu.setOnClickListener(v -> showPopupMenu());
        
        ivUserProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, Profile.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        llHome.setOnClickListener(v -> refreshListings());
        
        llChats.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, ChatActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        llMyListings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, MyListings.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        llProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, Profile.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
    
    /**
     * تهيئة قائمة العرض (RecyclerView) لإظهار بطاقات العقارات.
     */
    private void setupRecyclerView() {
        listingList = new ArrayList<>();
        listingAdapter = new HomeListingAdapter(listingList, this::onListingClick);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        rvListings.setAdapter(listingAdapter);
        loadListingsFromFirebase();
    }
    
    /**
     * إظهار واجهة "لا توجد بيانات" عندما تكون القائمة فارغة.
     */
    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        rvListings.setVisibility(View.GONE);
    }
    
    /**
     * إظهار قائمة العقارات وإخفاء واجهة "لا توجد بيانات".
     */
    private void showListings() {
        emptyStateLayout.setVisibility(View.GONE);
        rvListings.setVisibility(View.VISIBLE);
    }
    
    /**
     * تحميل قائمة العقارات من Firebase مع تصفية العقارات الخاصة بالمستخدم الحالي.
     */
    private void loadListingsFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showEmptyState();
            return;
        }
        
        String currentUserId = currentUser.getUid();
        
        mDatabase.child("listings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    listingList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MyListings.ListingItem listing = snapshot.getValue(MyListings.ListingItem.class);
                        if (listing != null) {
                            listing.setListingId(snapshot.getKey());
                            // إضافة العقارات النشطة والتابعة لمستخدمين آخرين فقط
                            if ("Active".equals(listing.getStatus()) && !currentUserId.equals(listing.getUserId())) {
                                listingList.add(listing);
                            }
                        }
                    }
                    listingAdapter.notifyDataSetChanged();
                    if (listingList.isEmpty()) showEmptyState(); else showListings();
                } catch (Exception e) {
                    showEmptyState();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showEmptyState();
            }
        });
    }
    
    /**
     * معالجة النقر على عقار معين لفتح شاشة التفاصيل.
     * @param listing العنصر الذي تم النقر عليه.
     */
    private void onListingClick(MyListings.ListingItem listing) {
        Intent intent = new Intent(HomeScreen.this, ListingDetailsActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    /**
     * إعادة تحديث القائمة من الخادم.
     */
    private void refreshListings() {
        loadListingsFromFirebase();
        Toast.makeText(this, "تحديث القائمة...", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * عرض القائمة المنبثقة (القائمة الجانبية) التي تحتوي على الإعدادات والمساعدة.
     */
    private void showPopupMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, ibMenu);
        popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                startActivity(new Intent(HomeScreen.this, SettingsActivity.class));
                return true;
            } else if (id == R.id.action_help) {
                startActivity(new Intent(HomeScreen.this, HelpActivity.class));
                return true;
            } else if (id == R.id.action_about) {
                showAboutDialog();
                return true;
            } else if (id == R.id.action_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
        popup.show();
    }
    
    /**
     * عرض مربع حوار يحتوي على معلومات حول التطبيق.
     */
    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("حول LUXE STAY")
                .setMessage("تطبيق LUXE STAY لتوفير أفضل أماكن السكن والخدمات العقارية.")
                .setPositiveButton("موافق", null)
                .show();
    }
    
    /**
     * تسجيل خروج المستخدم والعودة لشاشة تسجيل الدخول.
     */
    private void logoutUser() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("تسجيل الخروج")
                .setMessage("هل أنت متأكد أنك تريد تسجيل الخروج؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(HomeScreen.this, SignIn.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("لا", null)
                .show();
    }
    
    /**
     * يتم استدعاؤها عند العودة للشاشة لتحديث البيانات.
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshListings();
    }
    
    /**
     * تخصيص سلوك زر "الرجوع" لإظهار تأكيد الخروج من التطبيق.
     */
    private void setupOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new androidx.appcompat.app.AlertDialog.Builder(HomeScreen.this)
                        .setTitle("خروج")
                        .setMessage("هل تريد حقاً الخروج من التطبيق؟")
                        .setPositiveButton("نعم", (dialog, which) -> finishAffinity())
                        .setNegativeButton("لا", null)
                        .show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    
    /**
     * تحميل بيانات وصورة الملف الشخصي للمستخدم الحالي.
     */
    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mDatabase.child("users").child(currentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // تحميل الصورة إذا وجدت (تستخدم Glide في العادة)
                                ivUserProfile.setImageResource(android.R.drawable.ic_menu_mylocation);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            ivUserProfile.setImageResource(android.R.drawable.ic_menu_mylocation);
                        }
                    });
        }
    }
    
    /**
     * محول خاص بـ RecyclerView لعرض بطاقات العقارات في الشاشة الرئيسية.
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
            
            if (listing.getImageUrl() != null && !listing.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                    .load(listing.getImageUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.ivListingImage);
            }
            
            holder.itemView.setOnClickListener(v -> {
                if (onListingClickListener != null) onListingClickListener.onListingClick(listing);
            });
            holder.btnEdit.setVisibility(View.GONE);
        }
        
        @Override
        public int getItemCount() { return listings.size(); }
        
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

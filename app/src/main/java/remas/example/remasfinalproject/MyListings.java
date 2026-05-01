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

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
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

public class MyListings extends AppCompatActivity {
    
    private ImageView ivBack, ivFilter;
    private RecyclerView rvMyListings;
    private LinearLayout emptyStateLayout;
    private MaterialButton btnAddFirstListing;
    private MyListingAdapter listingAdapter;
    private List<ListingItem> listingList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
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
    
    private void initializeViews() {
        try {
            ivBack = findViewById(R.id.ivBack);
            ivFilter = findViewById(R.id.ivFilter);
            rvMyListings = findViewById(R.id.rvMyListings);
            emptyStateLayout = findViewById(R.id.emptyStateLayout);
            btnAddFirstListing = findViewById(R.id.btnAddFirstListing);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
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
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up click listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
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
    
    private void onListingClick(ListingItem listing) {
        Intent intent = new Intent(MyListings.this, ListingDetailsActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        startActivity(intent);
    }
    
    private void onEditClick(ListingItem listing) {
        Intent intent = new Intent(MyListings.this, AddDormActivity.class);
        intent.putExtra("listingId", listing.getListingId());
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }
    
    private void showFilterDialog() {
        String[] options = {"All", "Active", "Inactive", "Price: Low to High", "Price: High to Low"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Filter Listings")
                .setItems(options, (dialog, which) -> {
                    Toast.makeText(this, "Filter: " + options[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }
    
    public static class ListingItem {
        private String listingId, title, price, location, description, imageUrl, userId, status;
        private int bedrooms, bathrooms, area;
        private long timestamp;
        private List<String> amenities;
        
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
        public int getArea() { return area; }
        public void setArea(int area) { this.area = area; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public List<String> getAmenities() { return amenities; }
        public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    }
    
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
        
        private String getFormattedDate(long timestamp) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new java.util.Date(timestamp));
        }
        
        static class ListingViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvLocation, tvBedrooms, tvBathrooms, tvArea, tvListedDate, tvStatus;
            MaterialButton btnViewDetails, btnEdit;
            
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
            }
        }
    }
}

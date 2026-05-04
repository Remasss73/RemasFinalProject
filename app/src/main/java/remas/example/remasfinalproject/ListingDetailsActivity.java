package remas.example.remasfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class ListingDetailsActivity extends AppCompatActivity {
    
    // UI Components
    private ImageView ivBack, ivShare, ivMore, ivMainImage, ivMap;
    private TextView tvTitle, tvPrice, tvLocation, tvBedrooms, tvBathrooms, tvArea, tvDescription, tvImageCount;
    private RecyclerView rvAmenities;
    private MaterialButton btnEdit, btnDelete, btnGetDirections;
    
    // Data
    private String listingId;
    private MyListings.ListingItem currentListing;
    private AmenityAdapter amenityAdapter;
    
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        // Get listing ID from intent
        listingId = getIntent().getStringExtra("listingId");
        
        // Initialize UI
        initializeViews();
        setupClickListeners();
        
        // Load listing data
        if (listingId != null) {
            loadListingDetails();
        } else {
            Toast.makeText(this, "Error: Listing not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initializeViews() {
        // Header
        ivBack = findViewById(R.id.ivBack);
        ivShare = findViewById(R.id.ivShare);
        ivMore = findViewById(R.id.ivMore);
        
        // Content
        ivMainImage = findViewById(R.id.ivMainImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvLocation = findViewById(R.id.tvLocation);
        tvBedrooms = findViewById(R.id.tvBedrooms);
        tvBathrooms = findViewById(R.id.tvBathrooms);
        tvArea = findViewById(R.id.tvArea);
        tvDescription = findViewById(R.id.tvDescription);
        tvImageCount = findViewById(R.id.tvImageCount);
        rvAmenities = findViewById(R.id.rvAmenities);
        
        // Action buttons
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        
        // Map buttons
        ivMap = findViewById(R.id.ivMap);
        btnGetDirections = findViewById(R.id.btnGetDirections);
        
        // Setup amenities RecyclerView
        setupAmenitiesRecyclerView();
    }
    
    private void setupAmenitiesRecyclerView() {
        amenityAdapter = new AmenityAdapter(new ArrayList<>());
        rvAmenities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAmenities.setAdapter(amenityAdapter);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        ivShare.setOnClickListener(v -> {
            // TODO: Implement share functionality
            Toast.makeText(this, "Share coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        ivMore.setOnClickListener(v -> showMoreOptions());
        
        btnEdit.setOnClickListener(v -> editListing());
        
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        
        // Map click listeners
        ivMap.setOnClickListener(v -> showNavigationOptions());
        btnGetDirections.setOnClickListener(v -> showNavigationOptions());
    }
    
    private void loadListingDetails() {
        mDatabase.child("listings").child(listingId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentListing = dataSnapshot.getValue(MyListings.ListingItem.class);
                        if (currentListing != null) {
                            currentListing.setListingId(dataSnapshot.getKey());
                            updateUI();
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ListingDetailsActivity.this, "Error loading listing", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void updateUI() {
        if (currentListing == null) return;
        
        // Set basic info
        tvTitle.setText(currentListing.getTitle());
        tvPrice.setText(currentListing.getPrice());
        tvLocation.setText(currentListing.getLocation());
        tvDescription.setText(currentListing.getDescription());
        
        // Set property details
        tvBedrooms.setText(String.valueOf(currentListing.getBedrooms()));
        tvBathrooms.setText(String.valueOf(currentListing.getBathrooms()));
        tvArea.setText(String.valueOf(currentListing.getArea()));
        
        // Load and display images
        loadListingImages();
        
        // Update amenities (for now, add some default ones)
        List<String> amenities = currentListing.getAmenities();
        if (amenities == null || amenities.isEmpty()) {
            amenities = getDefaultAmenities();
        }
        amenityAdapter.updateAmenities(amenities);
        
        // Check if user can edit this listing
        boolean canEdit = mAuth.getCurrentUser() != null && 
                         mAuth.getCurrentUser().getUid().equals(currentListing.getUserId());
        
        btnEdit.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        btnDelete.setVisibility(canEdit ? View.VISIBLE : View.GONE);
    }
    
    private List<String> getDefaultAmenities() {
        List<String> amenities = new ArrayList<>();
        amenities.add("WiFi");
        amenities.add("Parking");
        amenities.add("Air Conditioning");
        amenities.add("Kitchen");
        return amenities;
    }
    
    private void showMoreOptions() {
        String[] options = {"Share", "Report", "Contact Owner"};
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("More Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Share
                            Toast.makeText(this, "Share coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                        case 1: // Report
                            Toast.makeText(this, "Report coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                        case 2: // Contact
                            Toast.makeText(this, "Contact coming soon!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }
    
    private void editListing() {
        Intent intent = new Intent(this, AddDormActivity.class);
        intent.putExtra("listingId", listingId);
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }
    
    private void showDeleteConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Listing")
                .setMessage("Are you sure you want to delete this listing? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteListing())
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void deleteListing() {
        if (listingId == null) return;
        
        mDatabase.child("listings").child(listingId)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Listing deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error deleting listing", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    // Simple amenity adapter
    private static class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.AmenityViewHolder> {
        
        private List<String> amenities;
        
        public AmenityAdapter(List<String> amenities) {
            this.amenities = amenities;
        }
        
        public void updateAmenities(List<String> newAmenities) {
            this.amenities = newAmenities;
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public AmenityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.amenity_item, parent, false);
            return new AmenityViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull AmenityViewHolder holder, int position) {
            String amenity = amenities.get(position);
            holder.tvAmenity.setText(amenity);
        }
        
        @Override
        public int getItemCount() {
            return amenities.size();
        }
        
        static class AmenityViewHolder extends RecyclerView.ViewHolder {
            TextView tvAmenity;
            
            public AmenityViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAmenity = itemView.findViewById(R.id.tvAmenity);
            }
        }
    }
    
    private void showNavigationOptions() {
        if (currentListing == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String location = currentListing.getAddress();
        if (location == null || location.isEmpty()) {
            location = currentListing.getCity() + ", " + currentListing.getArea();
        }
        
        final String finalLocation = location;
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("🗺️ Get Directions")
                .setMessage("Choose navigation app:")
                .setPositiveButton("🗺️ Google Maps", (dialog, which) -> {
                    openGoogleMaps(finalLocation);
                })
                .setNegativeButton("🚗 Waze", (dialog, which) -> {
                    openWaze(finalLocation);
                })
                .setNeutralButton("📍 Show on Map", (dialog, which) -> {
                    showOnMap(finalLocation);
                })
                .show();
    }
    
    private void openGoogleMaps(String location) {
        try {
            Uri uri = Uri.parse("google.navigation:q=" + Uri.encode(location));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback to web Google Maps
                uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(location));
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening Google Maps", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openWaze(String location) {
        try {
            Uri uri = Uri.parse("waze://?q=" + Uri.encode(location));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.waze");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback to web Waze
                uri = Uri.parse("https://waze.com/ul?q=" + Uri.encode(location));
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening Waze", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadListingImages() {
        if (currentListing == null || currentListing.getImageUrl() == null) {
            // Set default placeholder or empty state
            ivMainImage.setImageResource(android.R.drawable.ic_menu_gallery);
            tvImageCount.setText("No Images");
            return;
        }
        
        String imageUrl = currentListing.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load image into main ImageView
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // For now, just show the image
                // In a real implementation, you would use an image loading library like Glide or Picasso
                ivMainImage.setImageResource(android.R.drawable.ic_menu_gallery);
                tvImageCount.setText("Image Available");
            }
        } else {
            // No images available
            ivMainImage.setImageResource(android.R.drawable.ic_menu_gallery);
            tvImageCount.setText("No Images");
        }
    }

    private void showOnMap(String location) {
        try {
            Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening map", Toast.LENGTH_SHORT).show();
        }
    }
}

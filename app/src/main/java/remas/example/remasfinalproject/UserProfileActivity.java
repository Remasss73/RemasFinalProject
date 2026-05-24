package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * UserProfileActivity - Professional user profile viewing activity
 * Displays another user's profile with options to chat, block, and view listings
 */
public class UserProfileActivity extends BaseActivity {
    
    private ImageView ivBack, ivProfilePicture;
    private TextView tvUserName, tvBio, tvLocation, tvListingsCount, tvMemberSince;
    private MaterialButton btnChat, btnBlock, btnViewListings, btnReport;
    private LinearLayout llProfileHeader;
    
    private String targetUserId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        
        // Get target user ID from intent
        targetUserId = getIntent().getStringExtra("userId");
        if (targetUserId == null || targetUserId.isEmpty()) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userRef = mDatabase.child("users").child(targetUserId);
        
        initializeViews();
        setupClickListeners();
        loadUserData();
        checkBlockStatus();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUserName = findViewById(R.id.tvUserName);
        tvBio = findViewById(R.id.tvBio);
        tvLocation = findViewById(R.id.tvLocation);
        tvListingsCount = findViewById(R.id.tvListingsCount);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        btnChat = findViewById(R.id.btnChat);
        btnBlock = findViewById(R.id.btnBlock);
        btnViewListings = findViewById(R.id.btnViewListings);
        btnReport = findViewById(R.id.btnReport);
        llProfileHeader = findViewById(R.id.llProfileHeader);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        btnChat.setOnClickListener(v -> openChat());
        
        btnBlock.setOnClickListener(v -> showBlockDialog());
        
        btnViewListings.setOnClickListener(v -> viewUserListings());
        
        btnReport.setOnClickListener(v -> showReportDialog());
    }
    
    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String location = dataSnapshot.child("location").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    Long memberSince = dataSnapshot.child("createdAt").getValue(Long.class);
                    
                    // Set user name
                    tvUserName.setText(fullName != null ? fullName : "User");
                    
                    // Set bio
                    tvBio.setText(bio != null && !bio.isEmpty() ? bio : "No bio available");
                    
                    // Set location
                    tvLocation.setText(location != null && !location.isEmpty() ? location : "Unknown location");
                    
                    // Set member since date
                    if (memberSince != null) {
                        tvMemberSince.setText("Member since " + formatDate(memberSince));
                    }
                    
                    // Load profile picture
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(UserProfileActivity.this)
                            .load(profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(ivProfilePicture);
                    } else {
                        ivProfilePicture.setImageResource(android.R.drawable.ic_menu_myplaces);
                    }
                    
                    // Load listings count
                    loadListingsCount();
                } else {
                    Toast.makeText(UserProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadListingsCount() {
        mDatabase.child("listings").orderByChild("userId").equalTo(targetUserId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    tvListingsCount.setText(count + " Listings");
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    tvListingsCount.setText("0 Listings");
                }
            });
    }
    
    private void checkBlockStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        
        String currentUserId = currentUser.getUid();
        DatabaseReference blockedRef = mDatabase.child("blocked_users").child(currentUserId).child(targetUserId);
        
        blockedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User is blocked
                    btnBlock.setText("Unblock");
                    btnBlock.setBackgroundColor(0xFF10B981); // Green for unblock
                    btnChat.setEnabled(false);
                    btnChat.setAlpha(0.5f);
                } else {
                    // User is not blocked
                    btnBlock.setText("Block");
                    btnBlock.setBackgroundColor(0xFFEF4444); // Red for block
                    btnChat.setEnabled(true);
                    btnChat.setAlpha(1.0f);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    
    private void openChat() {
        if (targetUserId == null) return;
        
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to chat", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String currentUserId = currentUser.getUid();
        if (currentUserId.equals(targetUserId)) {
            Toast.makeText(this, "You cannot chat with yourself", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create chat room ID
        String chatRoomId = currentUserId.compareTo(targetUserId) < 0 
            ? currentUserId + "_" + targetUserId 
            : targetUserId + "_" + currentUserId;
        
        // Open chat activity
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("targetUserId", targetUserId);
        startActivity(intent);
    }
    
    private void showBlockDialog() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to block users", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String currentUserId = currentUser.getUid();
        DatabaseReference blockedRef = mDatabase.child("blocked_users").child(currentUserId).child(targetUserId);
        
        blockedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isBlocked = dataSnapshot.exists();
                String message = isBlocked 
                    ? "Are you sure you want to unblock this user?" 
                    : "Are you sure you want to block this user? You won't receive messages from them.";
                
                new AlertDialog.Builder(UserProfileActivity.this)
                    .setTitle(isBlocked ? "Unblock User" : "Block User")
                    .setMessage(message)
                    .setPositiveButton(isBlocked ? "Unblock" : "Block", (dialog, which) -> {
                        if (isBlocked) {
                            unblockUser();
                        } else {
                            blockUser();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    
    private void blockUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        
        String currentUserId = currentUser.getUid();
        DatabaseReference blockedRef = mDatabase.child("blocked_users").child(currentUserId).child(targetUserId);
        
        blockedRef.setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserProfileActivity.this, "User blocked successfully", Toast.LENGTH_SHORT).show();
                checkBlockStatus();
            } else {
                Toast.makeText(UserProfileActivity.this, "Error blocking user", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void unblockUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        
        String currentUserId = currentUser.getUid();
        DatabaseReference blockedRef = mDatabase.child("blocked_users").child(currentUserId).child(targetUserId);
        
        blockedRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserProfileActivity.this, "User unblocked successfully", Toast.LENGTH_SHORT).show();
                checkBlockStatus();
            } else {
                Toast.makeText(UserProfileActivity.this, "Error unblocking user", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void viewUserListings() {
        // Navigate to a filtered view of listings for this user
        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("filterByUserId", targetUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    
    private void showReportDialog() {
        String[] reportOptions = {
            "Inappropriate content",
            "Spam",
            "Fake listing",
            "Harassment",
            "Other"
        };
        
        new AlertDialog.Builder(this)
            .setTitle("Report User")
            .setItems(reportOptions, (dialog, which) -> {
                String reason = reportOptions[which];
                reportUser(reason);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void reportUser(String reason) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to report users", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String currentUserId = currentUser.getUid();
        DatabaseReference reportRef = mDatabase.child("reports").push();
        
        reportRef.child("reporterId").setValue(currentUserId);
        reportRef.child("reportedUserId").setValue(targetUserId);
        reportRef.child("reason").setValue(reason);
        reportRef.child("timestamp").setValue(System.currentTimeMillis());
        
        Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
    }
    
    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}

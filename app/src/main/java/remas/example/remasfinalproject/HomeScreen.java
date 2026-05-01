package remas.example.remasfinalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {
    
    // UI Components
    private ImageButton ibMenu;
    private LinearLayout llHome, llChats, llMyListings, llProfile;
    private LinearLayout emptyStateLayout;
    private RecyclerView rvListings;
    
    // Firebase
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        
        // Initialize UI components
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        setupOnBackPressed();
    }
    
    private void initializeViews() {
        // Header components
        ibMenu = findViewById(R.id.ibMenu);
        
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
                Intent intent = new Intent(HomeScreen.this, ChatsFinal.class);
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
                // For now, show user info - you can create a ProfileActivity later
                String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "Unknown";
                Toast.makeText(HomeScreen.this, "Profile: " + userEmail, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupRecyclerView() {
        // Setup RecyclerView for listings
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Initialize adapter and load listings from Firebase/Database
        // For now, show empty state
        showEmptyState();
    }
    
    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        rvListings.setVisibility(View.GONE);
    }
    
    private void showListings() {
        emptyStateLayout.setVisibility(View.GONE);
        rvListings.setVisibility(View.VISIBLE);
    }
    
    private void refreshListings() {
        // TODO: Implement actual data loading from Firebase
        // For now, just show a toast
        Toast.makeText(this, "Refreshing listings...", Toast.LENGTH_SHORT).show();
        
        // Simulate loading delay
        emptyStateLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: Check if listings exist and update UI accordingly
                showEmptyState(); // Keep showing empty state for now
            }
        }, 1000);
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
                    Toast.makeText(HomeScreen.this, "Settings", Toast.LENGTH_SHORT).show();
                    // TODO: Navigate to SettingsActivity
                    return true;
                } else if (id == R.id.action_help) {
                    Toast.makeText(HomeScreen.this, "Help & Support", Toast.LENGTH_SHORT).show();
                    // TODO: Navigate to HelpActivity or show help dialog
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
}



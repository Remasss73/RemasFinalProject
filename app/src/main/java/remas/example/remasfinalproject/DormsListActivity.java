package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Dorm.DormAdapter;
import remas.example.remasfinalproject.data.Dorm.Dorms;


/**
 * Activity that displays a list of all dormitory listings in the Remas application.
 * Users can view all available dorms, tap on any listing to see details,
 * and add new listings using the floating action button.
 */
public class DormsListActivity extends AppCompatActivity {

    // UI components
    private RecyclerView rvListings; // List that shows all dormitory listings
    private DormAdapter dormAdapter; // Adapter that connects the dorm data to the RecyclerView
    private List<Dorms> dormList; // List to hold all dormitory objects
    private TextView tvNoListings; // Message shown when there are no listings

    /**
     * Called when the activity is first created.
     * Sets up the user interface and loads all dormitory listings.
     *
     * @param savedInstanceState If the activity is being re-initialized after 
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_add_listing); // Set the layout for this screen
        
        // Initialize all UI components from the layout
        rvListings = findViewById(R.id.rvListings); // Find the RecyclerView for listings
        tvNoListings = findViewById(R.id.tvNoListings); // Find the "no listings" message
        FloatingActionButton fabAddListing = findViewById(R.id.fabAddListing); // Find the add button

        // Setup RecyclerView to display the list of dorms
        dormList = AppDatabase.getDB(this).getDormQuery().getAll(); // Get all dorms from database
        dormAdapter = new DormAdapter(this, dormList); // Create adapter with the data
        rvListings.setLayoutManager(new LinearLayoutManager(this)); // Set how items are arranged
        rvListings.setAdapter(dormAdapter); // Connect the adapter to the RecyclerView

        // Set click listener for the floating action button (add button)
        fabAddListing.setOnClickListener(v -> {
            // Navigate to AddDormActivity to add a new dormitory listing
            startActivity(new Intent(DormsListActivity.this, AddDormActivity.class));
        });

        // Load and display all dormitory listings
        loadListings();

        // Handle system bars (status bar, navigation bar) for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Loads all dormitory listings from the database and updates the UI.
     * Shows either the list of dorms or a "no listings" message based on availability.
     */
    private void loadListings() {
        // Get the latest list of all dorms from the database
        dormAdapter.setDormList(AppDatabase.getDB(this).getDormQuery().getAll());
        
        // Show the appropriate UI based on whether we have any listings
        if (dormList.isEmpty()) {
            // No dorms available - show the empty message
            rvListings.setVisibility(View.GONE); // Hide the list
            tvNoListings.setVisibility(View.VISIBLE); // Show the "no listings" message
        } else {
            // We have dorms - show the list
            rvListings.setVisibility(View.VISIBLE); // Show the list
            tvNoListings.setVisibility(View.GONE); // Hide the empty message
        }
    }

    /**
     * Called when the activity becomes visible and interactive.
     * Refreshes the dormitory list to show any new or updated listings.
     * This ensures the list is always up-to-date when users return from adding a new dorm.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from AddDormActivity
        loadListings(); // Reload all dorms from database
    }
}
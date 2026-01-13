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

import java.util.ArrayList;
import java.util.List;

import remas.example.remasfinalproject.adapters.DormAdapter;
import remas.example.remasfinalproject.data.Dorm.DormItem;

public class AddListing extends AppCompatActivity {

    private RecyclerView rvListings;
    private DormAdapter dormAdapter;
    private List<DormItem> dormList;
    private TextView tvNoListings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_listing);
        
        // Initialize views
        rvListings = findViewById(R.id.rvListings);
        tvNoListings = findViewById(R.id.tvNoListings);
        FloatingActionButton fabAddListing = findViewById(R.id.fabAddListing);

        // Setup RecyclerView
        dormList = new ArrayList<>();
        dormAdapter = new DormAdapter(dormList, this);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        rvListings.setAdapter(dormAdapter);

        // Set click listener for FAB
        fabAddListing.setOnClickListener(v -> {
            // Navigate to DormActivity to add a new listing
            startActivity(new Intent(AddListing.this, DormActivity.class));
        });

        // Load listings
        loadListings();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadListings() {
        // TODO: Replace with actual data loading from your data source
        // This is just sample data
        List<DormItem> sampleListings = new ArrayList<>();
        sampleListings.add(new DormItem("1", "Modern Apartment", "Riyadh", "Al Olaya", 2, 1, 80, 1200, "A beautiful modern apartment in the heart of the city"));
        sampleListings.add(new DormItem("2", "Cozy Studio", "Jeddah", "Al Hamra", 1, 1, 50, 900, "Cozy studio with great view"));
        
        dormList.clear();
        dormList.addAll(sampleListings);
        dormAdapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (dormList.isEmpty()) {
            rvListings.setVisibility(View.GONE);
            tvNoListings.setVisibility(View.VISIBLE);
        } else {
            rvListings.setVisibility(View.VISIBLE);
            tvNoListings.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from DormActivity
        loadListings();
    }
}
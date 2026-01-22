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

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Dorm.DormAdapter;
import remas.example.remasfinalproject.data.Dorm.Dorms;


public class DormsListActivity extends AppCompatActivity {

    private RecyclerView rvListings;
    private DormAdapter dormAdapter;
    private List<Dorms> dormList;
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
        dormList = AppDatabase.getDB(this).getDormQuery().getAll();
        dormAdapter = new DormAdapter(this,dormList );
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        rvListings.setAdapter(dormAdapter);

        // Set click listener for FAB
        fabAddListing.setOnClickListener(v -> {
            // Navigate to DormActivity to add a new listing
            startActivity(new Intent(DormsListActivity.this, DormActivity.class));
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

        dormList.clear();
        dormList.addAll(AppDatabase.getDB(this).getDormQuery().getAll());
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
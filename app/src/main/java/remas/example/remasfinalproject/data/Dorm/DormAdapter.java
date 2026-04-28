package remas.example.remasfinalproject.data.Dorm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import remas.example.remasfinalproject.AddDormActivity;
import remas.example.remasfinalproject.R;

/**
 * Adapter class for the RecyclerView that displays a list of Dormitory listings.
 * This class handles the inflation of individual item layouts and binds Dorm data
 * to the UI components.
 */
public class DormAdapter extends RecyclerView.Adapter<DormAdapter.DormViewHolder> {

    private List<Dorms> dormList; // List to hold all dormitory objects
    private Context context; // Application context for inflating layouts and starting activities

    /**
     * Constructor for the DormAdapter.
     *
     * @param context  The activity or fragment context.
     * @param dormList The initial list of Dorms to be displayed.
     */
    public DormAdapter(Context context, List<Dorms> dormList) {
        this.context = context;
        this.dormList = dormList;
    }

    /**
     * Updates the adapter's data set and refreshes the RecyclerView.
     *
     * @param dormList The new list of Dorms objects.
     */
    public void setDormList(List<Dorms> dormList) {
        // Clear existing data and add all new dorms to prevent duplicates
        this.dormList.clear();
        this.dormList.addAll(dormList);
        // Notify RecyclerView that the data has changed to refresh the UI
        notifyDataSetChanged();
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new DormViewHolder that holds the View for each list item.
     */
    @NonNull
    @Override
    public DormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for a single dorm item from XML
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dorm_item_layout, parent, false);
        return new DormViewHolder(itemView);
    }

    /**
     * Binds the data from a specific Dorms object to the views held by the ViewHolder.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull DormViewHolder holder, int position) {
        // Get the dorm object at the current position in the list
        Dorms current = dormList.get(position);

        // Bind dorm data to the corresponding TextView elements
        holder.tvPrice.setText(current.getPrice()); // Display rental price
        holder.tvTitle.setText(current.getTitle()); // Display dorm title/name
        holder.tvAddress.setText(current.address); // Display dorm address

        // Display room and bathroom information
        holder.tvBeds.setText(current.getBeds()); // Number of beds available
        holder.tvBaths.setText(current.getBaths()); // Number of bathrooms
        holder.tvOwnerName.setText(current.getOwnerName()); // Property owner name

        // Set click listener for the 'View Details' button
        holder.btnViewDetails.setOnClickListener(v -> {
            // Create intent to navigate to dorm details screen
            Intent intent = new Intent(context, AddDormActivity.class);
            // Pass the selected dorm object to the details activity using Serializable
            intent.putExtra("selected_dorm", current);
            context.startActivity(intent); // Start the details activity
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The size of the dormList, or 0 if the list is null.
     */
    @Override
    public int getItemCount() {
        return (dormList != null) ? dormList.size() : 0;
    }

    /**
     * ViewHolder class that defines and initializes the UI components for a single
     * item in the RecyclerView.
     */
    public static class DormViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice, tvTitle, tvAddress, tvBeds, tvBaths, tvOwnerName;
        MaterialButton btnViewDetails;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The inflated layout view for a single list item.
         */
        public DormViewHolder(@NonNull View itemView) {
            super(itemView);
        // Find and initialize all UI components from the inflated layout
        tvPrice = itemView.findViewById(R.id.tvPrice); // Price TextView
        tvTitle = itemView.findViewById(R.id.tvTitle); // Title TextView
        tvAddress = itemView.findViewById(R.id.tvAddress); // Address TextView
        tvBeds = itemView.findViewById(R.id.tvBeds); // Beds count TextView
        tvBaths = itemView.findViewById(R.id.tvBaths); // Baths count TextView
        tvOwnerName = itemView.findViewById(R.id.tvOwnerName); // Owner name TextView
        btnViewDetails = itemView.findViewById(R.id.btnViewDetails); // Details button
        }
    }
}
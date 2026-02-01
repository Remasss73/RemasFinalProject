package remas.example.remasfinalproject.data.Dorm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import remas.example.remasfinalproject.DormActivity;
import remas.example.remasfinalproject.R;

/**
 * Adapter class for the RecyclerView that displays a list of Dormitory listings.
 * This class handles the inflation of individual item layouts and binds Dorm data
 * to the UI components.
 */
public class DormAdapter extends RecyclerView.Adapter<DormAdapter.DormViewHolder> {

    private List<Dorms> dormList;
    private Context context;

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
        this.dormList = dormList;
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
        // Inflates the custom layout created for a single row/item
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
        Dorms current = dormList.get(position);

        // Bind data using the helper methods or direct public fields from the Dorms entity
        holder.tvPrice.setText(current.getPrice());
        holder.tvTitle.setText(current.getTitle());
        holder.tvAddress.setText(current.address);

        // Displaying amenities in the area designated for 'beds'
        holder.tvBeds.setText(current.getBeds());
        holder.tvBaths.setText(current.getBaths());
        holder.tvOwnerName.setText(current.getOwnerName());

        // Set listener for the 'View Details' button to open the DormActivity
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, DormActivity.class);
            // Passing the current Dorms object (Serializable) to the details screen
            intent.putExtra("selected_dorm", current);
            context.startActivity(intent);
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
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvBeds = itemView.findViewById(R.id.tvBeds);
            tvBaths = itemView.findViewById(R.id.tvBaths);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
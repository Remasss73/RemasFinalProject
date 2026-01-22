package remas.example.remasfinalproject.data.Dorm;

import android.content.Context;
import android.content.Intent;import android.view.LayoutInflater;
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

// THE CLASS NAME MUST BE DormAdapter TO MATCH YOUR FILE NAME
public class DormAdapter extends RecyclerView.Adapter<DormAdapter.DormViewHolder> {

    private List<Dorms> dormList;
    private Context context;

    public DormAdapter(Context context, List<Dorms> dormList) {
        this.context = context;
        this.dormList = dormList;
    }

    public void setDormList(ArrayList<Dorms> dormList) {
        this.dormList = dormList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We use item_apartment_listing or seeker_item_layout here (the single row layout)
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dorm_item_layout, parent, false);
        return new DormViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DormViewHolder holder, int position) {
        Dorms current = dormList.get(position);

        // Map your Room Entity fields (rent, city, address) to the TextViews
        holder.tvPrice.setText("$" + current.rent);
        holder.tvTitle.setText(current.city);
        holder.tvAddress.setText(current.address);

        // Amenities is a string, we show it where "beds" used to be
        holder.tvBeds.setText(current.amenities);

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, DormActivity.class);
            // Pass data to the details screen
            intent.putExtra("city", current);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (dormList != null) ? dormList.size() : 0;
    }

    // This is the "Holder" for the views in one single row
    public static class DormViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice, tvTitle, tvAddress, tvBeds, tvBaths, tvOwnerName;
        MaterialButton btnViewDetails;

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
package remas.example.remasfinalproject.data.Dorm;

import static java.nio.file.Files.size;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import remas.example.remasfinalproject.DormActivity;
import remas.example.remasfinalproject.R;
public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.TaskViewHolder>
{
    private ArrayList<DormItem> dormList;
    private Context context;
    public TasksRecyclerAdapter(Context context, ArrayList<DormItem> dormList) {
        this.context = context;
        this.dormList = dormList;
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice;
        TextView tvTitle;
        TextView tvAddress;
        TextView tvBeds;
        TextView tvBaths;
        TextView tvOwnerName;
        MaterialButton btnViewDetails;



        public TaskViewHolder (@NonNull View itemView){
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

@NonNull
@Override
public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_dorm_adapter, parent, false);
    return new TaskViewHolder(itemView);
}

@Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position){
    Dorms current = dormList.get(position);
    holder.tvPrice.setText(current.getPrice());
    holder.tvTitle.setText(current.getTitle());
    holder.tvAddress.setText(current.getAddress());
    holder.tvBeds.setText(current.getBeds());
    holder.tvBaths.setText(current.getBaths());
    holder.tvOwnerName.setText(current.getOwnerName());
    holder.btnViewDetails.setOnClickListener(v -> {
        Intent intent = new Intent(context, DormActivity.class);
        intent.putExtra("dorm", current);
        context.startActivity(intent);
    });
    }

    @Override
    public int getItemCount() {
        return .size();
    }
}

public void setDormList(ArrayList<DormItem> dormList) {
    this.dormList = dormList;
    notifyDataSetChanged();
}

private void notifyDataSetChanged() {
}


public class DormsAdapter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dorm_adapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    }


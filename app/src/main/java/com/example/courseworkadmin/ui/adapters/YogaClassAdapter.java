package com.example.courseworkadmin.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseworkadmin.R;
import com.example.courseworkadmin.ui.details.YogaClassDetailsDialog;
import com.example.courseworkadmin.ui.dialogs.YogaClassDialog;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.helpers.FirebaseHelper;
import com.example.courseworkadmin.ui.models.YogaClass;

import java.util.List;

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.YogaClassViewHolder> {

    private final Context context;
    private final List<YogaClass> yogaClasses;
    private final DatabaseHelper dbHelper;
    private final FirebaseHelper firebaseHelper;

    public YogaClassAdapter(Context context, List<YogaClass> yogaClasses, DatabaseHelper dbHelper, FirebaseHelper firebaseHelper) {
        this.context = context;
        this.yogaClasses = yogaClasses;
        this.dbHelper = dbHelper;
        this.firebaseHelper = firebaseHelper;
    }

    @NonNull
    @Override
    public YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.yoga_class_item, parent, false);
        return new YogaClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassViewHolder holder, int position) {
        YogaClass yogaClass = yogaClasses.get(position);

        holder.titleTextView.setText(yogaClass.getTitle());

        // Handle "Edit" button click
        holder.editButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();

            YogaClassDialog dialog = new YogaClassDialog(context, new YogaClassDialog.YogaClassDialogListener() {
                @Override
                public void onYogaClassSaved(YogaClass updatedYogaClass) {
                    yogaClasses.set(adapterPosition, updatedYogaClass);
                    notifyItemChanged(adapterPosition);  // Refresh the specific item
                    dbHelper.updateYogaClass(updatedYogaClass, updatedYogaClass.getId());
                    firebaseHelper.updateYogaClass(updatedYogaClass, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Updated on Firestore!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Firestore update failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, dbHelper, firebaseHelper);

            dialog.showAddEditDialog(yogaClass);
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition(); // Get the current adapter position

            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this class?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteYogaClass(adapterPosition))
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.itemView.setOnClickListener(v -> {
            YogaClassDetailsDialog dialog = new YogaClassDetailsDialog(yogaClass);
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "YogaClassDetailsDialog");
        });
    }

    @Override
    public int getItemCount() {
        return yogaClasses.size();
    }

    private void deleteYogaClass(int position) {
        YogaClass yogaClassToDelete = yogaClasses.get(position);

        // Delete from SQLite
        if (dbHelper.deleteYogaClass(yogaClassToDelete.getId()) > 0) {
            // Also delete from Firestore
            firebaseHelper.deleteYogaClass(yogaClassToDelete.getId(), task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Deleted from Firestore!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Firestore deletion failed.", Toast.LENGTH_SHORT).show();
                }
            });

            yogaClasses.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, yogaClasses.size());
        } else {
            Toast.makeText(context, "Error deleting class from database", Toast.LENGTH_SHORT).show();
        }
    }

    public static class YogaClassViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button editButton;
        Button deleteButton;

        public YogaClassViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.yoga_class_title);
            editButton = itemView.findViewById(R.id.yoga_class_edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}

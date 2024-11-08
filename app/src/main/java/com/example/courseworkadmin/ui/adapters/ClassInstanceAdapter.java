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
import com.example.courseworkadmin.ui.details.ClassInstanceDetailsDialog;
import com.example.courseworkadmin.ui.dialogs.ClassInstanceDialog;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.helpers.FirebaseHelper;
import com.example.courseworkadmin.ui.models.ClassInstance;

import java.util.List;

public class ClassInstanceAdapter extends RecyclerView.Adapter<ClassInstanceAdapter.ClassInstanceViewHolder> {

    private final Context context;
    private final List<ClassInstance> classInstances;
    private final DatabaseHelper dbHelper;
    private final FirebaseHelper firebaseHelper;

    public ClassInstanceAdapter(Context context, List<ClassInstance> classInstances, DatabaseHelper dbHelper, FirebaseHelper firebaseHelper) {
        this.context = context;
        this.classInstances = classInstances;
        this.dbHelper = dbHelper;
        this.firebaseHelper = firebaseHelper;
    }

    @NonNull
    @Override
    public ClassInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.instance_class_item, parent, false);
        return new ClassInstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassInstanceViewHolder holder, int position) {
        ClassInstance classInstance = classInstances.get(position);

        holder.teacherTextView.setText(classInstance.getTeacher());

        // Handle "Edit" button click
        holder.editButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();

            ClassInstanceDialog dialog = new ClassInstanceDialog(context, updatedClassInstance -> {
                classInstances.set(adapterPosition, updatedClassInstance);
                notifyItemChanged(adapterPosition); // Refresh the specific item
                dbHelper.updateClassInstance(updatedClassInstance, updatedClassInstance.getInstanceId()); // Update in SQLite

                // Update in Firebase
                firebaseHelper.updateClassInstance(updatedClassInstance, task -> {
                    if (task.isSuccessful()) { // Here task is the Task<Void> passed to the listener
                        Toast.makeText(context, "Updated on Firestore!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Firestore update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }, dbHelper, firebaseHelper); // Pass FirebaseHelper here

            dialog.showAddEditDialog(classInstance); // Pass the current instance for editing
        });


        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();

            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this class instance?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteClassInstance(adapterPosition))
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.itemView.setOnClickListener(v -> {
            ClassInstanceDetailsDialog detailsDialog = new ClassInstanceDetailsDialog(classInstance, dbHelper);
            detailsDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ClassInstanceDetailsDialog");
        });
    }


    @Override
    public int getItemCount() {
        return classInstances.size();
    }

    private void deleteClassInstance(int position) {
        ClassInstance classInstanceToDelete = classInstances.get(position);

        // Delete from SQLite
        if (dbHelper.deleteClassInstance(classInstanceToDelete.getInstanceId()) > 0) {
            // Also delete from Firestore
            firebaseHelper.deleteClassInstance(String.valueOf(classInstanceToDelete.getInstanceId()), task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Deleted from Firestore!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Firestore deletion failed.", Toast.LENGTH_SHORT).show();
                }
            });

            classInstances.remove(position); // Remove from the list
            notifyItemRemoved(position); // Notify RecyclerView to update
            notifyItemRangeChanged(position, classInstances.size());
        } else {
            Toast.makeText(context, "Error deleting class instance from database", Toast.LENGTH_SHORT).show();
        }
    }

    public static class ClassInstanceViewHolder extends RecyclerView.ViewHolder {
        TextView teacherTextView;
        Button editButton;
        Button deleteButton;

        public ClassInstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherTextView = itemView.findViewById(R.id.class_instance_teacher);
            editButton = itemView.findViewById(R.id.class_instance_edit_button);
            deleteButton = itemView.findViewById(R.id.class_instance_delete_button);
        }
    }
}

package com.example.courseworkadmin.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.courseworkadmin.R;
import com.example.courseworkadmin.ui.adapters.ClassInstanceAdapter;
import com.example.courseworkadmin.ui.dialogs.ClassInstanceDialog;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.helpers.FirebaseHelper;
import com.example.courseworkadmin.ui.models.ClassInstance;

import java.util.List;

public class DashboardFragment extends Fragment implements ClassInstanceDialog.InstanceDialogListener {

    private ClassInstanceAdapter classInstanceAdapter;
    private DatabaseHelper dbHelper;
    private FirebaseHelper firebaseHelper; // Added FirebaseHelper
    private List<ClassInstance> classInstances;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity());
        dbHelper.openDatabase();

        firebaseHelper = new FirebaseHelper(); // Initialize FirebaseHelper

        classInstances = dbHelper.getAllClassInstances();

        RecyclerView classInstanceRecyclerView = view.findViewById(R.id.class_instance_recycler_view);
        classInstanceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        classInstanceAdapter = new ClassInstanceAdapter(getActivity(), classInstances, dbHelper, firebaseHelper);
        classInstanceRecyclerView.setAdapter(classInstanceAdapter);

        view.findViewById(R.id.add_class_instance_button).setOnClickListener(v -> {
            ClassInstanceDialog classInstanceDialog = new ClassInstanceDialog(getActivity(), this, dbHelper, firebaseHelper);
            classInstanceDialog.showAddEditDialog(null);
        });

        // Handle delete all class instances event
        view.findViewById(R.id.delete_all_button).setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm Delete All")
                    .setMessage("Are you sure you want to delete all class instances?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.clearAllClassInstances();
                        // Clear from Firebase
                        firebaseHelper.deleteAllClassInstances(
                                aVoid -> { // This corresponds to OnSuccessListener
                                    classInstances.clear();
                                    classInstanceAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "All class instances deleted successfully", Toast.LENGTH_SHORT).show();
                                },
                                e -> { // This corresponds to OnFailureListener
                                    Toast.makeText(getActivity(), "Failed to delete class instances from Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        );
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onInstanceSaved(ClassInstance classInstance) {
        Log.d("app_log: ", classInstance.getTeacher());
        classInstances.add(classInstance);
        classInstanceAdapter.notifyItemInserted(classInstances.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbHelper.close();
    }
}

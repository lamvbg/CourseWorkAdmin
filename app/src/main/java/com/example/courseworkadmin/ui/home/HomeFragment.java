package com.example.courseworkadmin.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
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
import com.example.courseworkadmin.ui.adapters.YogaClassAdapter;
import com.example.courseworkadmin.ui.dialogs.YogaClassDialog;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.helpers.FirebaseHelper;
import com.example.courseworkadmin.ui.models.YogaClass;
import java.util.List;

public class HomeFragment extends Fragment implements YogaClassDialog.YogaClassDialogListener {

    private YogaClassAdapter yogaClassAdapter;
    private DatabaseHelper dbHelper;
    private List<YogaClass> yogaClasses;
    private FirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity());
        dbHelper.openDatabase();

        firebaseHelper = new FirebaseHelper();

        yogaClasses = dbHelper.getAllYogaClasses();

        RecyclerView yogaClassRecyclerView = view.findViewById(R.id.yoga_class_recycler_view);
        yogaClassRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        yogaClassAdapter = new YogaClassAdapter(getActivity(), yogaClasses, dbHelper, firebaseHelper);
        yogaClassRecyclerView.setAdapter(yogaClassAdapter);

        view.findViewById(R.id.add_course_button).setOnClickListener(v -> {
            YogaClassDialog yogaClassDialog = new YogaClassDialog(getActivity(), this, dbHelper, firebaseHelper);
            yogaClassDialog.showAddEditDialog(null);
        });

        view.findViewById(R.id.delete_all_button).setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm Delete All")
                    .setMessage("Are you sure you want to delete all classes?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.clearAllClasses();
                        firebaseHelper.clearAllClasses(task -> {
                            if (task.isSuccessful()) {
                                yogaClasses.clear();
                                yogaClassAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "All classes deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Failed to delete classes from Firestore", Toast.LENGTH_SHORT).show();
                            }
                        });
                        yogaClasses.clear();
                        yogaClassAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onYogaClassSaved(YogaClass yogaClass) {
        yogaClasses.add(yogaClass);
        yogaClassAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbHelper.close();
    }
}

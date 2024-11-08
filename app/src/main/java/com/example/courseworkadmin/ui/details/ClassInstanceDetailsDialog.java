package com.example.courseworkadmin.ui.details;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.courseworkadmin.R;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.models.ClassInstance;
import com.example.courseworkadmin.ui.models.YogaClass;

public class ClassInstanceDetailsDialog extends DialogFragment {

    private ClassInstance classInstance;
    private DatabaseHelper dbHelper;

    public ClassInstanceDetailsDialog(ClassInstance classInstance, DatabaseHelper dbHelper) {
        this.classInstance = classInstance;
        this.dbHelper = dbHelper; // Initialize the DatabaseHelper
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_class_instance_details, null);

        // Set details
        TextView dateTextView = view.findViewById(R.id.date_text_view);
        TextView teacherTextView = view.findViewById(R.id.teacher_text_view);
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        TextView commentsTextView = view.findViewById(R.id.comments_text_view);

        dateTextView.setText("Date: " + classInstance.getDate());
        teacherTextView.setText("Teacher: " + classInstance.getTeacher());

        // Fetch the course title using courseId
        YogaClass yogaClass = dbHelper.getYogaClassById(classInstance.getCourseId());
        if (yogaClass != null) {
            titleTextView.setText("Course: " + yogaClass.getTitle());
        } else {
            titleTextView.setText("Course: Unknown");
        }

        commentsTextView.setText("Comments: " + classInstance.getComments());

        builder.setView(view)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}

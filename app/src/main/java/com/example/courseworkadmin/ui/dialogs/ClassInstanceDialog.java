package com.example.courseworkadmin.ui.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.example.courseworkadmin.R;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.helpers.FirebaseHelper;
import com.example.courseworkadmin.ui.models.ClassInstance;
import com.example.courseworkadmin.ui.models.YogaClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClassInstanceDialog {

    public interface InstanceDialogListener {
        void onInstanceSaved(ClassInstance classInstance);
    }

    private final Context context;
    private final InstanceDialogListener listener;
    private final DatabaseHelper dbHelper;
    private final FirebaseHelper firebaseHelper;

    public ClassInstanceDialog(Context context, InstanceDialogListener listener, DatabaseHelper dbHelper, FirebaseHelper firebaseHelper) {
        this.context = context;
        this.listener = listener;
        this.dbHelper = dbHelper;
        this.firebaseHelper = firebaseHelper;
    }

    public void showAddEditDialog(ClassInstance classInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.fragment_add_edit_instance, null);

        EditText teacherEditText = dialogView.findViewById(R.id.dialog_instance_teacher);
        TextView dateEditText = dialogView.findViewById(R.id.dialog_instance_date);
        EditText commentsEditText = dialogView.findViewById(R.id.dialog_instance_comments);
        Spinner coursesSpinner = dialogView.findViewById(R.id.spinner_courses);

        // Fetching all yoga classes to populate the spinner
        List<YogaClass> yogaClasses = dbHelper.getAllYogaClasses();
        List<String> courseNames = new ArrayList<>();
        for (YogaClass yogaClass : yogaClasses) {
            courseNames.add(yogaClass.getTitle());
        }

        // Create an ArrayAdapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, courseNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesSpinner.setAdapter(adapter);

        // Pre-fill fields for editing
        if (classInstance != null) {
            dateEditText.setText(classInstance.getDate());
            teacherEditText.setText(classInstance.getTeacher());
            commentsEditText.setText(classInstance.getComments());

            // Set the spinner selection based on the course ID
            int position = -1;
            for (int i = 0; i < yogaClasses.size(); i++) {
                if (yogaClasses.get(i).getId() == classInstance.getCourseId()) {
                    position = i;
                    break;
                }
            }

            if (position != -1) {
                coursesSpinner.setSelection(position);
            } else {
                Toast.makeText(context, "Selected course not found", Toast.LENGTH_SHORT).show();
            }
        }

        // Setup DatePickerDialog for date field
        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateEditText.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setView(dialogView)
                .setPositiveButton(classInstance == null ? "Add" : "Edit", (dialog, which) -> {
                    String date = dateEditText.getText().toString().trim();
                    String teacher = teacherEditText.getText().toString().trim();
                    String comments = commentsEditText.getText().toString().trim();
                    int selectedCourseIndex = coursesSpinner.getSelectedItemPosition();
                    if (selectedCourseIndex == -1) {
                        Toast.makeText(context, "Please select a course.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int courseId = yogaClasses.get(selectedCourseIndex).getId();

                    if (date.isEmpty() || teacher.isEmpty()) {
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ClassInstance updatedInstance;

                    if (classInstance == null) {
                        updatedInstance = new ClassInstance(courseId, date, teacher, comments);
                        long id = dbHelper.addClassInstance(updatedInstance);
                        updatedInstance.setInstanceId((int) id);

                        // Add to Firebase
                        firebaseHelper.addClassInstance(updatedInstance, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Class instance added successfully to Firestore", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to add class instance to Firestore", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        classInstance.setDate(date);
                        classInstance.setTeacher(teacher);
                        classInstance.setComments(comments);
                        classInstance.setCourseId(courseId);
                        dbHelper.updateClassInstance(classInstance, classInstance.getInstanceId());
                        updatedInstance = classInstance;

                        // Update in Firebase
                        firebaseHelper.updateClassInstance(updatedInstance, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Class instance updated successfully in Firestore", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to update class instance in Firestore", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    listener.onInstanceSaved(updatedInstance);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize button styles
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.purple_700));
    }
}

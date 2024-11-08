package com.example.courseworkadmin.ui.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.example.courseworkadmin.R;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.helpers.FirebaseHelper;
import com.example.courseworkadmin.ui.models.YogaClass;

import java.util.Calendar;

public class YogaClassDialog {

    public interface YogaClassDialogListener {
        void onYogaClassSaved(YogaClass yogaClass);
    }

    private final Context context;
    private final YogaClassDialogListener listener;
    private final DatabaseHelper dbHelper;
    private final FirebaseHelper firebaseHelper;

    public YogaClassDialog(Context context, YogaClassDialogListener listener, DatabaseHelper dbHelper, FirebaseHelper firebaseHelper) {
        this.context = context;
        this.listener = listener;
        this.dbHelper = dbHelper;
        this.firebaseHelper = firebaseHelper;
    }

    public void showAddEditDialog(YogaClass yogaClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.fragment_add_edit_class, null);

        EditText titleEditText = dialogView.findViewById(R.id.dialog_class_title);
        TextView dayTextView = dialogView.findViewById(R.id.dialog_class_day);
        TextView timeTextView = dialogView.findViewById(R.id.dialog_class_time);
        EditText capacityEditText = dialogView.findViewById(R.id.dialog_class_capacity);
        EditText durationEditText = dialogView.findViewById(R.id.dialog_class_duration);
        EditText priceEditText = dialogView.findViewById(R.id.dialog_class_price);
        RadioGroup typeRadioGroup = dialogView.findViewById(R.id.radioGroupType);
        EditText descriptionEditText = dialogView.findViewById(R.id.dialog_class_description);

        // Pre-fill fields for editing
        if (yogaClass != null) {
            titleEditText.setText(yogaClass.getTitle());
            dayTextView.setText(yogaClass.getDayOfWeek());
            timeTextView.setText(yogaClass.getTimeOfCourse());
            capacityEditText.setText(String.valueOf(yogaClass.getCapacity()));
            durationEditText.setText(String.valueOf(yogaClass.getDuration()));
            priceEditText.setText(String.valueOf(yogaClass.getPrice()));
            descriptionEditText.setText(yogaClass.getDescription());

            // Set the checked RadioButton based on the class type
            switch (yogaClass.getTypeOfClass()) {
                case "Flow Yoga":
                    typeRadioGroup.check(R.id.rb_flow_yoga);
                    break;
                case "Aerial Yoga":
                    typeRadioGroup.check(R.id.rb_aerial_yoga);
                    break;
                case "Family Yoga":
                    typeRadioGroup.check(R.id.rb_family_yoga);
                    break;
            }
        }

        // Date picker for the day
        dayTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                dayTextView.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Time picker for the time
        timeTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute1) -> {
                String selectedTime = hourOfDay + ":" + String.format("%02d", minute1);
                timeTextView.setText(selectedTime);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        builder.setView(dialogView)
                .setPositiveButton(yogaClass == null ? "Add" : "Edit", (dialog, which) -> {
                    String title = titleEditText.getText().toString().trim();
                    String day = dayTextView.getText().toString().trim();
                    String time = timeTextView.getText().toString().trim();
                    String capacityStr = capacityEditText.getText().toString().trim();
                    String durationStr = durationEditText.getText().toString().trim();
                    String priceStr = priceEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();

                    if (day.isEmpty() || time.isEmpty() || capacityStr.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int selectedId = typeRadioGroup.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(context, "Please select a class type.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                    String type = selectedRadioButton.getText().toString();

                    int capacity;
                    int duration;
                    double price;

                    try {
                        capacity = Integer.parseInt(capacityStr);
                        duration = Integer.parseInt(durationStr);
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Please enter valid numbers for capacity, duration, and price.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    YogaClass updatedYogaClass;
                    updatedYogaClass = new YogaClass(day, time, capacity, duration, price, type, description, title);

                    if (yogaClass == null) {
                        // Add Yoga Class to SQLite
                        long id = dbHelper.addYogaClass(updatedYogaClass);
                        updatedYogaClass.setId((int) id);

                        // Add Yoga Class to Firebase
                        firebaseHelper.addYogaClass(updatedYogaClass, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Class added successfully to Firestore", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to add class to Firestore", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        yogaClass.setDayOfWeek(day);
                        yogaClass.setTimeOfCourse(time);
                        yogaClass.setCapacity(capacity);
                        yogaClass.setDuration(duration);
                        yogaClass.setPrice(price);
                        yogaClass.setTypeOfClass(type);
                        yogaClass.setDescription(description);
                        yogaClass.setTitle(title);

                        // Update Yoga Class in SQLite
                        dbHelper.updateYogaClass(yogaClass, yogaClass.getId());

                        // Update Yoga Class in Firebase
                        firebaseHelper.updateYogaClass(yogaClass, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Class updated successfully in Firestore", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to update class in Firestore", Toast.LENGTH_SHORT).show();
                            }
                        });

                        updatedYogaClass = yogaClass;
                    }

                    listener.onYogaClassSaved(updatedYogaClass);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize button styles
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.purple_700));
    }
}

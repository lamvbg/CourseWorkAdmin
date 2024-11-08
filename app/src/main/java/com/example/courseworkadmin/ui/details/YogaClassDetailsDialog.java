package com.example.courseworkadmin.ui.details;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.courseworkadmin.R;
import com.example.courseworkadmin.ui.models.YogaClass;

public class YogaClassDetailsDialog extends DialogFragment {

    private YogaClass yogaClass;

    public YogaClassDetailsDialog(YogaClass yogaClass) {
        this.yogaClass = yogaClass;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_yoga_class_details, null);

        // Set details
        TextView typeTextView = view.findViewById(R.id.type_text_view);
        TextView dayTextView = view.findViewById(R.id.day_text_view);
        TextView timeTextView = view.findViewById(R.id.time_text_view);
        TextView capacityTextView = view.findViewById(R.id.capacity_text_view);
        TextView durationTextView = view.findViewById(R.id.duration_text_view);
        TextView priceTextView = view.findViewById(R.id.price_text_view);
        TextView typeOfClassTextView = view.findViewById(R.id.typeOfClass_text_view);
        TextView descriptionTextView = view.findViewById(R.id.description_text_view);

        typeTextView.setText(yogaClass.getTitle());
        dayTextView.setText("Day: " + yogaClass.getDayOfWeek());
        timeTextView.setText("Time: " + yogaClass.getTimeOfCourse());
        capacityTextView.setText("Capacity: " + yogaClass.getCapacity() + " persons");
        durationTextView.setText("Duration: " + yogaClass.getDuration() + " mins");
        priceTextView.setText("Price: " + yogaClass.getPrice() + " $");
        typeOfClassTextView.setText("Type Of Class: " + yogaClass.getTypeOfClass());
        descriptionTextView.setText("Description: " + yogaClass.getDescription());

        builder.setView(view);
        return builder.create();
    }
}
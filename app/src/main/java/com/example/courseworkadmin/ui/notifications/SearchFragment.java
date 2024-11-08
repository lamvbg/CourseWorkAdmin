package com.example.courseworkadmin.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.courseworkadmin.R;
import com.example.courseworkadmin.ui.adapters.ClassInstanceAdapter;
import com.example.courseworkadmin.ui.helpers.DatabaseHelper;
import com.example.courseworkadmin.ui.helpers.FirebaseHelper;
import com.example.courseworkadmin.ui.models.ClassInstance;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearchTeacher;
    private Button btnSearch;
    private RecyclerView rvSearchResults;
    private TextView tvNoResults;
    private DatabaseHelper dbHelper;
    private FirebaseHelper firebaseHelper;
    private ClassInstanceAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        dbHelper = new DatabaseHelper(getContext());

        etSearchTeacher = root.findViewById(R.id.et_search_teacher);
        btnSearch = root.findViewById(R.id.btn_search);
        rvSearchResults = root.findViewById(R.id.rv_search_results);
        tvNoResults = root.findViewById(R.id.tv_no_results);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        btnSearch.setOnClickListener(v -> {
            String teacherName = etSearchTeacher.getText().toString();
            if (!teacherName.isEmpty()) {
                loadSearchResults(teacherName);
            }
        });

        return root;
    }

    private void loadSearchResults(String teacherName) {
        List<ClassInstance> searchResults = dbHelper.searchClassesByTeacher(teacherName);

        if (searchResults.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            adapter = new ClassInstanceAdapter(getContext(), searchResults, dbHelper, firebaseHelper);
            rvSearchResults.setAdapter(adapter);
        }
    }
}

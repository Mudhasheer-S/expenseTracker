package com.example.expensetracker.ui.fragments;

import android.os.Bundle;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;

import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.ui.viewmodel.CategoryViewModel;

public class AddCategoryFragment extends Fragment {
    private EditText etCategoryName;
    private CategoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        etCategoryName = view.findViewById(R.id.etCategoryName);
        Button btnSave = view.findViewById(R.id.btnSaveCategory);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        btnSave.setOnClickListener(v -> {
            String name = etCategoryName.getText().toString().trim();
            if (!name.isEmpty()) {
                Category category = new Category(name);
                viewModel.insert(category);
                Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
                ((CategoryManagerFragment) requireParentFragment()).onCategoryAdded();
//                requireActivity().onBackPressed(); // or navigate back
            } else {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}


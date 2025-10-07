package com.example.expensetracker.ui.fragments;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.ui.adapter.CategoryGridAdapter;
import com.example.expensetracker.ui.viewmodel.CategoryViewModel;

public class CategoryManagerFragment extends Fragment {
    private CategoryViewModel viewModel;
    private Button btnAddCategory;
    private FrameLayout addCategoryContainer;
    private GridView gridView;

    public CategoryManagerFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_manager, container, false);

        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        addCategoryContainer = view.findViewById(R.id.addCategoryContainer);
        gridView = view.findViewById(R.id.gridCategories);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setupAddCategoryLogic();
        setupCategoryGrid();

        return view;
    }

    private void setupAddCategoryLogic() {
        // Initially hide the AddCategory fragment
        addCategoryContainer.setVisibility(View.GONE);

        btnAddCategory.setOnClickListener(v -> {
            btnAddCategory.setVisibility(View.GONE);
            addCategoryContainer.setVisibility(View.VISIBLE);

            if (getChildFragmentManager().findFragmentById(R.id.addCategoryContainer) == null) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.addCategoryContainer, new AddCategoryFragment())
                        .commit();
            }
        });
    }

    private void setupCategoryGrid() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            CategoryGridAdapter adapter = new CategoryGridAdapter(requireContext(), categories);
            gridView.setAdapter(adapter);
        });

        // Optional: show a toast when a category is clicked
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Category category = (Category) parent.getItemAtPosition(position);
            Toast.makeText(getContext(), "Clicked: " + category.name, Toast.LENGTH_SHORT).show();
        });
    }

    // This method can be called from AddCategoryFragment after saving
    public void onCategoryAdded() {
        if (getView() != null) {
            getView().findViewById(R.id.addCategoryContainer).setVisibility(View.GONE);
            getView().findViewById(R.id.btnAddCategory).setVisibility(View.VISIBLE);
        }
    }
}

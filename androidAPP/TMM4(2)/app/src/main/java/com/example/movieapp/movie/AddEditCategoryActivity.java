package com.example.movieapp.movie;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditCategoryActivity extends AppCompatActivity {
    private EditText editName, editDescription;
    private Button btnSave, btnDelete;

    private ApiService apiService;
    private String categoryId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_add_edit_category);

        // Initialize views
        editName = findViewById(R.id.editCategoryName);
        editDescription = findViewById(R.id.editCategoryDescription);
        btnSave = findViewById(R.id.btnSaveCategory);
        btnDelete = findViewById(R.id.btnDeleteCategory);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Check if we're editing an existing category
        categoryId = getIntent().getStringExtra("CATEGORY_ID");
        isEditMode = categoryId != null;

        if (isEditMode) {
            setTitle("Edit Category");
            btnDelete.setVisibility(View.VISIBLE);
            loadCategory();
        } else {
            setTitle("Add Category");
            btnDelete.setVisibility(View.GONE);
        }

        // Set up button click listeners
        btnSave.setOnClickListener(v -> saveCategory());
        btnDelete.setOnClickListener(v -> confirmDelete());

    }

    private void loadCategory() {
        Call<Category> call = apiService.getCategory(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Category category = response.body();
                    editName.setText(category.getName());
                    editDescription.setText(category.getDescription());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(AddEditCategoryActivity.this, "Failed to load category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCategory() {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Name is required");
            return;
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        if (isEditMode) {
            // Update existing category
            Call<Category> call = apiService.updateCategory(categoryId, category);
            call.enqueue(new Callback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditCategoryActivity.this, "Category updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditCategoryActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Category> call, Throwable t) {
                    Toast.makeText(AddEditCategoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new category
            Call<Category> call = apiService.createCategory(category);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditCategoryActivity.this, "Category added", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditCategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Category> call, Throwable t) {
                    Toast.makeText(AddEditCategoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Delete", (dialog, which) -> deleteCategory())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCategory() {
        Call<Void> call = apiService.deleteCategory(categoryId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddEditCategoryActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEditCategoryActivity.this, "Failed to delete category. Make sure it's not used by any movies.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddEditCategoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
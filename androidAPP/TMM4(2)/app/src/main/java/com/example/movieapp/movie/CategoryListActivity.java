package com.example.movieapp.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_view_category);

        setTitle("Categories");

        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(categoryAdapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        FloatingActionButton fab = findViewById(R.id.fabAddCategory);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryListActivity.this, AddEditCategoryActivity.class);
            startActivity(intent);
        });


        loadCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories(); // Refresh data when returning to activity
    }

    private void loadCategories() {
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(CategoryListActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
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
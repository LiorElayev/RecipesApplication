package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class RecipeViewWithImage extends AppCompatActivity {

    private TextView namePreview, descriptionPreview, categoryPreview;
    private ImageView recipeImagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view_with_image);

        namePreview = findViewById(R.id.namePreview);
        descriptionPreview = findViewById(R.id.descriptionPreview);
        recipeImagePreview = findViewById(R.id.recipeImage);
        categoryPreview = findViewById(R.id.categoryPreview);

        //get intent "putExtra" keys, for displaying each recipe's details
        Intent intent = getIntent();
        namePreview.setText(intent.getStringExtra("nameKey"));
        descriptionPreview.setText(intent.getStringExtra("descriptionKey"));
        categoryPreview.setText(intent.getStringExtra("categoryKey"));
        //Picasso.get().load(intent.getStringExtra("imageUrlKey")).into(recipeImagePreview);
        Picasso.with(this).load(intent.getStringExtra("imageUrlKey")).into(recipeImagePreview);
    }
}
package com.example.maize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Saved extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firestore;
    private String  Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();
        Uid=mAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //Query
        Query query = firestore.collection("Gallery").whereEqualTo("User_id",Uid);
        //        RecyclerOptions
        FirestoreRecyclerOptions<GalleryModel> options = new FirestoreRecyclerOptions.Builder<GalleryModel>()
                .setQuery(query,GalleryModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<GalleryModel, GalleryViewHolder>(options){
            @NonNull
            @Override
            public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false);
                return new GalleryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GalleryViewHolder holder, int position, @NonNull GalleryModel model) {
                Log.d("Saved", "Image Path: " + model.getImagePath());
                Uri mImageUri =Uri.parse(model.getImagePath());
                Glide.with(getApplicationContext()).load(mImageUri).into(holder.img);
            }
        } ;

        // Set the grid layout manager with 3 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        BottomNavigationView naview = findViewById(R.id.nav_view);
        naview.setSelectedItemId(R.id.page_3);
        naview.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() ==R.id.page_1){
                    startActivity(new Intent(Saved.this, MainActivity.class));
                    finish();
                } else if (item.getItemId() == R.id.page_3) {
                    return  true;
                }else if (item.getItemId()== R.id.page_4){
                    startActivity(new Intent(Saved.this, Profile.class));
                    finish();

                }else {
                    return true;
                }
                return false;
            }
        });
    }

    private class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
package com.example.maize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
private TextView editbtn;
private CircleImageView useerimg;
private TextView username, email, location, crops;
private FirebaseAuth mAuth;
private FirebaseUser mCurrentUser;
private FirebaseFirestore firestore;
private Uri imageUri,mImageUri;
private String  Uid;
private StorageReference reference = FirebaseStorage.getInstance().getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();
        Uid=mAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
        BottomNavigationView naview = findViewById(R.id.nav_view);
        naview.setSelectedItemId(R.id.page_4);
        naview.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() ==R.id.page_1){
                    startActivity(new Intent(Profile.this, MainActivity.class));
                    finish();

                } else if (item.getItemId() == R.id.page_3) {
                    startActivity(new Intent(Profile.this, Saved.class));
                    finish();

                }else if (item.getItemId()== R.id.page_4){
                    return  true;
                }else {
                    return true;
                }
                return false;
            }
        });
        useerimg = findViewById(R.id.prof_img);
        username=findViewById(R.id.userid);
        email=findViewById(R.id.emailid);
        location=findViewById(R.id.loc);
        crops=findViewById(R.id.plantid);
        //        get user details
        firestore.collection("Accounts").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document  = task.getResult();
                    if(document.exists()){
                        String user_name=document.getString("username");
                        String em_address =task.getResult().getString("email");
                        String address=document.getString("address");
                        String crops_grown=document.getString("plant");
                        String imageUrl = document.getString("img");
//                        Toast.makeText(Profile.this, ""+task.getResult().get("Username"), Toast.LENGTH_SHORT).show();
                        username.setText(user_name);
                        email.setText(em_address);
                        location.setText(address);
                        crops.setText(crops_grown);
                        if (imageUrl !=null){
                            mImageUri =Uri.parse(imageUrl);
                            Glide.with(Profile.this).load(mImageUri).into(useerimg);
                        }else {
                            useerimg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_manage_accounts_24));
                        }

                    }else {
                        Toast.makeText(Profile.this, "No user data", Toast.LENGTH_SHORT).show();
                    }

                    }
            }
        });

        editbtn = findViewById(R.id.edit_btn);
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, Edit_Profile.class));
                finish();
            }
        });
    }
    }

package com.example.maize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_Profile extends AppCompatActivity {
private CircleImageView profile_img;
private TextInputEditText usernamed, email, location, plant;
private TextView  save_profilebtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firestore;
    private Uri imageUri,mImageUri;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private String  Uid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();
        Uid=mAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
        usernamed = findViewById(R.id.username_id);
        email = findViewById(R.id.email);
        location=findViewById(R.id.location);
        plant = findViewById(R.id.plantid);
        profile_img=findViewById(R.id.profile_img);
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,2);
            }
        });
        save_profilebtn =findViewById(R.id.save_btn);
        save_profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri !=null){
                        uploadTofirestore(imageUri);
                }else {
                    Toast.makeText(Edit_Profile.this, "Please add photo", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profile_img.setImageURI(imageUri);
        }
    }

    private void uploadTofirestore(Uri uri){
        final StorageReference fileref = reference.child(System.currentTimeMillis() + "." +getFileExtension(uri));
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String user_name = usernamed.getText().toString();
                        final String email_ = email.getText().toString();
                        final String loc_add= location.getText().toString();
                        final String planto = plant.getText().toString();
                        final String imagepath=uri.toString();
                        HashMap<String,Object> map =new HashMap<>();
                        map.put("user_id",Uid);
                        map.put("username",user_name);
                        map.put("email",email_);
                        map.put("address",loc_add);
                        map.put("plant",planto);
                        map.put("img",imagepath);
                        firestore.collection("Accounts").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Edit_Profile.this, "profile successfully set", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Edit_Profile.this,Profile.class));
                                    finish();
                                }else{
                                    Toast.makeText(Edit_Profile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Edit_Profile.this, "Profile not saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri mUri){
        ContentResolver cr =getContentResolver();
        MimeTypeMap  mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
package com.example.maize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Classify extends AppCompatActivity {
    private ImageView selectedImage;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firestore;
    private Uri imageUri,mImageUri;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private String  Uid;
     TextView resultTextView , continue_btn, expertdata;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();
        Uid=mAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();
        selectedImage = (ImageView) findViewById(R.id.img);
        resultTextView = findViewById(R.id.resid);
        expertdata = findViewById(R.id.data);
        continue_btn = findViewById(R.id.edit_btn);
        // get image from previous activity to show in the imageView
        String pred_class= getIntent().getStringExtra("result");
        Bitmap image = getIntent().getParcelableExtra("imageKey");
        selectedImage.setImageBitmap(image);
        if(Objects.equals(pred_class, "Healthy")){
            resultTextView.setText("Result: "+getString(R.string.class1));
            expertdata.setText(""+getString(R.string.hlt));
        }else if(Objects.equals(pred_class, "MLB")){
            resultTextView.setText("Result: "+getString(R.string.class2));
            expertdata.setText(""+getString(R.string.mlb));
        }else if(Objects.equals(pred_class, "MSV")){
            resultTextView.setText(String.format("Result: %s", getString(R.string.class3)));
            expertdata.setText(""+getString(R.string.msv));
        }else {
            resultTextView.setText("Result: "+getString(R.string.class4));
            expertdata.setText(""+getString(R.string.other));
        }

        // not sure why this happens, but without this the image appears on its side
        selectedImage.setRotation(selectedImage.getRotation() + 90);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = getImageUriFromImageView(selectedImage);
                if (imageUri != null) {
                    uploadTofirestore(imageUri);
                } else {
                    Toast.makeText(Classify.this, "Results Not Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Classify.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
    private void uploadTofirestore(Uri uri){
        final StorageReference fileref = reference.child(System.currentTimeMillis() + "." +getFileExtension(uri));
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imagepath=uri.toString();
                        HashMap<String,Object> map =new HashMap<>();
                        map.put("User_id",Uid);
                        map.put("result",resultTextView.getText().toString());
                        map.put("ImagePath",imagepath);
                        firestore.collection("Gallery").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Classify.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Classify.this,MainActivity.class));
                                    finish();
                                }else{
                                    Toast.makeText(Classify.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Classify.this, "Results not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri mUri){
        ContentResolver cr =getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
    private Uri getImageUriFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();

            // Save the bitmap to a temporary file with timestamp as name
            File tempFile;
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "image_" + timeStamp + ".jpg";
                tempFile = File.createTempFile(fileName, null, getCacheDir());
                FileOutputStream outStream = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            // Get the URI of the temporary file
            return Uri.fromFile(tempFile);
        }

        return null;
    }


}
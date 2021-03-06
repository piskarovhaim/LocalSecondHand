package com.haim_yarin.finalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddItemFormActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_IMAGE_CAPTURE = 1 ;
    private EditText edTitle;
    private EditText edDiscription;
    private EditText edPrice;
    private ImageView imgAddImage;
    private Button btnSubmit;
    private StorageReference StorageRef;
    private Uri ImageUri;
    private StorageTask UploadTask;
    private ProgressBar ProgressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Object> user;
    private Map<String, Object> location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_form);

        edTitle = (EditText)findViewById(R.id.edTitle);
        edDiscription = (EditText)findViewById(R.id.edDiscription);
        edPrice = (EditText)findViewById(R.id.edPrice);
        imgAddImage = (ImageView)findViewById(R.id.imgAddImage);
        imgAddImage.setOnClickListener(this);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        StorageRef = FirebaseStorage.getInstance().getReference("images");
        ProgressBar = findViewById(R.id.progress_bar);


        // get the user info from the intent
        user = new HashMap<>();
        user.put("Name",getIntent().getExtras().getString("name"));
        user.put("Uid",getIntent().getExtras().getString("uid"));
        user.put("Email",getIntent().getExtras().getString("email"));
        user.put("Phone",getIntent().getExtras().getString("phone"));

        // get the location info from the intent
        location = new HashMap<>();
        location.put("Latitude",getIntent().getExtras().getDouble("latitude"));
        location.put("Longitude",getIntent().getExtras().getDouble("longitude"));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imgAddImage: // add image prosses
                openFileChooser();
                break;
            case R.id.btnSubmit:
                if (UploadTask != null && UploadTask.isInProgress()) {
                    Toast.makeText(AddItemFormActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Submit();
                }
                break;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            ImageUri = data.getData();
            imgAddImage.setImageURI(ImageUri);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void Submit() {

        if (ImageUri != null) {
            final StorageReference fileReference = StorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(ImageUri));

            UploadTask = fileReference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressBar.setProgress(0);
                                }
                            }, 500);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Upload fail",e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            ProgressBar.setProgress((int) progress);
                        }
                    });

            UploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Map<String, Object> item = new HashMap<>();
                        item.put("Title", edTitle.getText().toString());
                        item.put("Disctiption", edDiscription.getText().toString());
                        item.put("Price", edPrice.getText().toString());
                        item.put("ImageUrl", downloadUri.toString());
                        item.put("user",user);
                        item.put("location",location);
                        db.collection("items").document()
                                .set(item)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddItemFormActivity.this, "Item upload successful", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("save item in DB fail",e.getMessage());
                                    }
                                });
                    } else {

                    }
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }


    }


}

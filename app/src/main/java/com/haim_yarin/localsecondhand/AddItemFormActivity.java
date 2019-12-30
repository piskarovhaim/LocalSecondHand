package com.haim_yarin.localsecondhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private LocationManager locationManager;
    private LocationListener locationlistener;
    private Button button;
    private Gps gps;




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

        user = new HashMap<>();
        user.put("Name",getIntent().getExtras().getString("name"));
        user.put("Uid",getIntent().getExtras().getString("uid"));
        user.put("Email",getIntent().getExtras().getString("email"));
        user.put("Phone",getIntent().getExtras().getString("phone"));



        /*
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("GPS",location.getLatitude()+" "+location.getLongitude());
                gps.setLocation(location.getLatitude(),location.getLongitude(),location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);

            }
        };
        gps = new Gps(locationManager,locationlistener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_CHECKIN_PROPERTIES,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},10);
                return;
            }
        }

         */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    gps.configureButton();
                return;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imgAddImage:
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
                        // Handle failures
                        // ...
                    }
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }


    }


}

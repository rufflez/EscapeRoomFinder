package com.escaperoom.rufflez.escaperoomfinder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by rufflez on 11/10/16.
 */

public class NewEscapeRoom extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PHOTO_REQUEST = 9002;
    private static final int REQUEST_READ_PERMISSION = 9003;
    private Button save, select_photo;
    private EditText name, address, url;
    private ImageView photo;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Uri photoUri;
    private ProgressDialog progressBar;
    private String photoUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_escape_room);
        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        url = (EditText) findViewById(R.id.url);
        save = (Button) findViewById(R.id.save);
        select_photo = (Button) findViewById(R.id.select_photo);
        photo = (ImageView) findViewById(R.id.photo);
        progressBar = new ProgressDialog(NewEscapeRoom.this);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText = "";
                String addressText = "";
                String urlText = "";
                if (name.getText().toString() != null) {
                    nameText = name.getText().toString();
                }
                if (address.getText().toString() != null) {
                    addressText = address.getText().toString();
                }
                if (url.getText().toString() != null) {
                    urlText = url.getText().toString();
                }
                final StorageReference onlineStoragePhotoRef = mStorageReference.child("Photos").child(nameText).child(photoUri.getLastPathSegment());
                progressBar.setMessage("Uploading file to Firebase");
                progressBar.show();
                final String finalNameText = nameText;
                final String finalAddressText = addressText;
                final String finalUrlText = urlText;
                onlineStoragePhotoRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setMessage("Success");
                        photoUrl = taskSnapshot.getDownloadUrl().toString();
                        EscapeRoom escapeRoom = new EscapeRoom(finalNameText, finalAddressText, finalUrlText, photoUrl);
                        mDatabaseReference.child("rooms").push().setValue(escapeRoom);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewEscapeRoom.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        photoUrl = "";
                        EscapeRoom escapeRoom = new EscapeRoom(finalNameText, finalAddressText, finalUrlText, photoUrl);
                        mDatabaseReference.child("rooms").push().setValue(escapeRoom);
                    }
                });

                progressBar.dismiss();
                finish();
            }
        });

    }

    private void requestPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(NewEscapeRoom.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NewEscapeRoom.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
            } else {
                openFilePicker();
            }
        } else {
            openFilePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openFilePicker();

                } else {
                    Toast.makeText(NewEscapeRoom.this, "Cannot pick file from storage", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            photoUri = data.getData();
            Glide.with(NewEscapeRoom.this).load(photoUri).into(photo);
        }
    }
}

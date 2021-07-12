package com.example.lumos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    StorageReference storageReference;

    TextView username, firstname, lastname, email, toHomePage;
    EditText modifyUserName, modifyFirstName, modifyLastName;
    ImageView setImageProfile, imageToDisplayIn;
    Button cirUpdateProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toHomePage = (TextView)findViewById(R.id.toHomePage);

        // dla wyświetlanych danych profilu
        username = (TextView)findViewById(R.id.username);
        firstname = (TextView)findViewById(R.id.firstname);
        lastname = (TextView)findViewById(R.id.lastname);
        email = (TextView)findViewById(R.id.email);

        // EditText - update dla profilu
        modifyUserName = (EditText)findViewById(R.id.modifyUserName);
        modifyFirstName = (EditText)findViewById(R.id.modifyFirstName);
        modifyLastName = (EditText)findViewById(R.id.modifyLastName);

        // Button - do zapisania edycji profilu
        cirUpdateProfileButton = (Button)findViewById(R.id.cirUpdateProfileButton);

        // obrazki
        setImageProfile = (ImageView)findViewById(R.id.setImageProfile);
        imageToDisplayIn = (ImageView)findViewById(R.id.imageToDisplayIn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/default.png");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageToDisplayIn);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.get().load(R.mipmap.ic_profile_foreground).into(imageToDisplayIn);
                Log.d("tag", "Ustawiony został awatar domyślny");
            }
        });

        userId = fAuth.getCurrentUser().getUid();
        //FirebaseUser user = fAuth.getCurrentUser();

        // set profile data

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot.exists()) {
                        username.setText(documentSnapshot.getString("UserName"));
                        email.setText(documentSnapshot.getString("Email"));
                        firstname.setText(documentSnapshot.getString("FirstName"));
                        lastname.setText(documentSnapshot.getString("LastName"));

                    } else {
                        Log.d("tag", "Document nie istnieje");
                    }
                }
            }
        });

        toHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        setImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //imageToDisplayIn.setImageURI(imageUri);
                Toast.makeText(ProfileActivity.this, "To może trochę potrwać", Toast.LENGTH_SHORT).show();
                uploadImageDB(imageUri);
            }
        }
    }

    private void uploadImageDB(Uri imageUri) {
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/default.png");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageToDisplayIn);
                    }
                });
                Toast.makeText(ProfileActivity.this, "Zapisano zdjęcie profilowe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Nie udało się zapisać zdjęcia profilowego", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
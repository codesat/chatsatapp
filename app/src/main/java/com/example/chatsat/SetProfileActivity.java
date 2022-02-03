package com.example.chatsat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SetProfileActivity extends AppCompatActivity {
    ImageButton backbtnofviewprof;
    TextView viewprofappname;
    ImageView setprofimgview;
     EditText setprofusername;
     Button setprofbtn;

     String imageUriAccessToken;

     FirebaseAuth firebaseAuth;
     FirebaseFirestore firebaseFirestore;
     FirebaseStorage firebaseStorage;
     FirebaseDatabase firebaseDatabase;
     Toolbar toolbar;
     StorageReference storageReference;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);


        backbtnofviewprof=findViewById(R.id.backbtnofviewprof);
        viewprofappname=findViewById(R.id.myappname);
        setprofimgview=findViewById(R.id.setprofuserimage);
        setprofusername=findViewById(R.id.setprofusername);
        setprofbtn=findViewById(R.id.setprofbtn);

        toolbar=findViewById(R.id.toolbarofviewprof);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();


        /// to set the toolbar in our action bar
        setSupportActionBar(toolbar);


        // on click on button
        backbtnofviewprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SetProfileActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // ye firebase work on create m,ein karrahe taki jab bhi profile activity khole koi toh
        // uski profile aur naam dikhe taki uske baad user usko edit kar paye
        // firebase work
        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).getDownloadUrl() // yah nusse image ka url downlaod karva lenge
        .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageUriAccessToken=uri.toString();
                // now after that we have to load the image in our image view
                Picasso.get().load(uri).into(setprofimgview);

            }
        });



        // now we have to show that alreaqdy ffetch name in our prof activity

     databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid()); // here by this we got the path of the user name in our realtime database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                ProfileModel muserprofile=snapshot.getValue(ProfileModel.class);
                setprofusername.setText(muserprofile.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(SetProfileActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();

            }
        });


        setprofbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SetProfileActivity.this, UpdateProfileActivity.class);
                intent.putExtra("username",setprofusername.getText().toString());
                startActivity(intent);
            }
        });












    }
}
package com.example.chatsat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {



    private ImageView upprofuserimageview;
    private  static int PICKIMAGE=123;
    private Uri profimgpath;
    private EditText upprofusername;
    private Button upsetprofbtn;
    String newname;
    Button savenewprofbtn;

    private FirebaseAuth firebaseAuth;

    private StorageReference storagerefrence;
    private FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    private String imgaccessuritoken;
    private FirebaseFirestore firebasefirestore;
    ImageButton updatebackbtn;
    Toolbar toolbar;

    ProgressBar updateprofprogressabr;

    Uri upimagepath;
    Intent intent;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);



        toolbar=findViewById(R.id.updatetoolbar);
        updatebackbtn=findViewById(R.id.backbtnupdateprofile);
        upprofuserimageview=findViewById(R.id.getnewuserimage);

        updateprofprogressabr=findViewById(R.id.updateprogbar);
        upprofusername=findViewById(R.id.getnewusername);


        savenewprofbtn=findViewById(R.id.savenewprofbtn);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebasefirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();


        intent=getIntent();
        setSupportActionBar(toolbar);


        // cilick on back btn of activty of update profbtn

        updatebackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // matlab pichli activity par chale jaoge after leaving this

            }
        });




        // by this when activity will create then the name will be already set
        upprofusername.setText(intent.getStringExtra("nameofuser"));



        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        // when user click on btn then we have to vaildate the things
        upsetprofbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newname=upprofusername.getText().toString();
                if(newname.isEmpty()){
                    Toast.makeText(UpdateProfileActivity.this, "", Toast.LENGTH_SHORT).show();
                }
                else if(upimagepath!=null){ // that means user select the new image

                    updateprofprogressabr.setVisibility(View.VISIBLE);
                    ProfileModel profileModel=new ProfileModel(newname,firebaseAuth.getUid());
                    databaseReference.setValue(profileModel);
                    
                    updatetheimagetostorage();

                    Toast.makeText(UpdateProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                    updateprofprogressabr.setVisibility(View.INVISIBLE);

                    Intent intent=new Intent(UpdateProfileActivity.this,ChatActivity.class);
                    startActivity(intent);
                    finish();

                    


                }
                else{

                    
                    updateprofprogressabr.setVisibility(View.VISIBLE);
                    ProfileModel profileModel=new ProfileModel(newname,firebaseAuth.getUid());
                    databaseReference.setValue(profileModel);



                    // here after updating image in storage then we will call the
                    // funciton to update in firestore


                    updatenameincloudfirestore();

                    Toast.makeText(UpdateProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();


                    updateprofprogressabr.setVisibility(View.INVISIBLE);

                    Intent intent=new Intent(UpdateProfileActivity.this,ChatActivity.class);
                    startActivity(intent);
                    finish();

                }
                
            }
        });



        upprofuserimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICKIMAGE);

            }
        });


        storagerefrence=firebaseStorage.getReference();
        storagerefrence.child("Images").child(firebaseAuth.getUid()).
                child("Profile pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // first we have to downlaod the url adn store in accesstoken then only we can show the image

                imgaccessuritoken=uri.toString();
                Picasso.get().load(uri).into(upprofuserimageview);

            }
        });





    }

    private void updatenameincloudfirestore() {

        DocumentReference documentReference=firebasefirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String,Object> userdata=new HashMap<>();
        userdata.put("username",newname);
        userdata.put("imageurl",imgaccessuritoken);
        userdata.put("userId",firebaseAuth.getUid());
        userdata.put("status","Online"); // when ever user will create profile for first
        // time status will show online



        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfileActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void updatetheimagetostorage() {



        StorageReference imageref = storagerefrence.child("Images").child(firebaseAuth.getUid()).child("profile Pic");


        // Image Compression code
        Bitmap bitmap = null; // here we initally store null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profimgpath);
        } catch (IOException e) {
            e.printStackTrace();

        }

        // now we have image in bitmap and its time to compress it
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // creating object
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream); // here quality will not decrease instead that size will decrease
        // creating array of imagetype
        byte[] data = byteArrayOutputStream.toByteArray(); // now the image is stored in byte type array

        // now we have to store image in firebase storage
        UploadTask uploadTask = imageref.putBytes(data); // by this our image get uploaded to firebase storage at the given loactaion
        // to check success or failure
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // yaha se hum image ka url le lenge taki store karva sake in firebase firestore;
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // now we can store the uri in our imageuriaccesstoken varaible
                        imgaccessuritoken = uri.toString();
                        // we can show message whether uri is got from storage or not and stored in imageaccesstoken varaible
                        Toast.makeText(UpdateProfileActivity.this, "URI get success", Toast.LENGTH_SHORT).show();

                        /// now we ahve to send data to firestore
                        updatenameincloudfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(UpdateProfileActivity.this, "Image uploaded sucessfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfileActivity.this, "Image upload is failed", Toast.LENGTH_SHORT).show();

            }
        });




    }





    // this below overriding method will help to select the image from the gallery

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==PICKIMAGE && resultCode==RESULT_OK){

            // this means user select some image  resultCode==RESULT_OK
            // now we will get Uri of image so store in uri type of varaible
            profimgpath=data.getData(); // here uri get stored
            // now we have to set the image uri to the image view to show the image
           upprofuserimageview.setImageURI(profimgpath);

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /// here when this activity will stop then we will update the status online ot offline
    // in firestore
    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebasefirestore.collection("Users")
                .document(firebaseAuth.getUid());

        // here we have updated the status to offline
        documentReference.update("status","Offline")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfileActivity.this, "User is offline", Toast.LENGTH_SHORT).show();

                    }
                });


    }


    //by default onstart pahle run karega toh offline status update ho jkayega toh usko online set kardena hai
    @Override
    protected void onStart() {
        super.onStart();

        // here we have updated the status to offline
        DocumentReference documentReference=firebasefirestore.collection("Users")
                .document(firebaseAuth.getUid());

        documentReference.update("status","Online")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfileActivity.this, "User is online", Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
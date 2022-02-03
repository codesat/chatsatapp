package com.example.chatsat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private CardView profuserimage;
    private ImageView profuserimageview;
    private  static int PICKIMAGE=123;
    private Uri profimgpath;
    private EditText profusername;
    private Button setprofbtn;

    private FirebaseAuth firebaseAuth;
    private String name;

    private StorageReference storagerefrence;
    private FirebaseStorage firebaseStorage;
    private String imgaccessuritoken;
    private FirebaseFirestore firebasefirestore;

    ProgressBar profprogressabr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storagerefrence=firebaseStorage.getReference();
        firebasefirestore=FirebaseFirestore.getInstance();

        profuserimage=findViewById(R.id.getuserimage);
        profusername=findViewById(R.id.getusername);
        profuserimageview=findViewById(R.id.profuserimage);
        setprofbtn=findViewById(R.id.saveprofbtn);


        profprogressabr=findViewById(R.id.profprogressbar);

        profuserimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// itne se pick ka dialog open ho jayega   Intent intent=new Intent(Intent.ACTION_PICK
                /// kya pick karna hai    MediaStore.Images.Media.INTERNAL_CONTENT_URI   toh sari jaha images hai vaha le chalo
                // image ka uri jo bhi slect kiya hai
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICKIMAGE);
                // till now we have opened the gallery
                /// now some result will come whether selected image or not



            }
        });

        // now after setting the image selected to image view of profile we have to sav eit to firebase storage

        setprofbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sath emin name aur profile save karvayenge
                name=profusername.getText().toString();


                if (name.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Type your name", Toast.LENGTH_SHORT).show();
                }
                else if(profimgpath==null){
                    Toast.makeText(ProfileActivity.this, "Image is Empty", Toast.LENGTH_SHORT).show();

                }
                else {
                    // now if everything is fine then we cans end dat to firebase
                    profprogressabr.setVisibility(View.VISIBLE);
                    sendDataofirebase();
                    profprogressabr.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(ProfileActivity.this,ChatActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });


    }

    private void sendDataofirebase() {
        
        senddatatoRealTimeDatabase();
        
        
        
        
        
        








    }

    private void senddatatoRealTimeDatabase() {
        // to send data in realtime database we need class
        // to send on firestore , firestorage we dont need any class
        name=profusername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());

        ProfileModel profile=new ProfileModel(name,firebaseAuth.getUid()); // here with object of class we can pass data to realtime database
        databaseReference.setValue(profile); // here the profile has been uploded to firebase database realtime

        Toast.makeText(this, "User profile Updated Succeffully", Toast.LENGTH_SHORT).show();


        // to send the image to storage
        sendimagetofirebasestorage();
    }

    private void sendimagetofirebasestorage() {
        // first we will storage the image in firebase storage and get the link after that we will store the the link
        // after that we will store the name and profile image ka link in firebase firestore

        /// cool if image is large size then we will us eimage compression technique

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
                        Toast.makeText(ProfileActivity.this, "URI get success", Toast.LENGTH_SHORT).show();

                        /// now we ahve to send data to firestore
                        sendtofirebasefirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(ProfileActivity.this, "Image uploaded sucessfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Image upload is failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendtofirebasefirestore() {
        DocumentReference documentReference=firebasefirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String,Object> userdata=new HashMap<>();
        userdata.put("username",name);
        userdata.put("imageurl",imgaccessuritoken);
        userdata.put("userId",firebaseAuth.getUid());
        userdata.put("status","Online"); // when ever user will create profile for first
        // time status will show online

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Data sent on cloud firestore successfully", Toast.LENGTH_SHORT).show();

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
            profuserimageview.setImageURI(profimgpath);

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
                        Toast.makeText(ProfileActivity.this, "User is offline", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(ProfileActivity.this, "User is offline", Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
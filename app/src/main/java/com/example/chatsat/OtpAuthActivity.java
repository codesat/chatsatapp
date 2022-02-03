package com.example.chatsat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpAuthActivity extends AppCompatActivity {

    Button  verifynumbtn;
    TextView changepnum;
    EditText getotpbox;
    String enteredotpbyuser;

    FirebaseAuth firebaseAuth;
    ProgressBar otpauthprogressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);



        verifynumbtn=findViewById(R.id.verifyotpbtn);
        changepnum=findViewById(R.id.changenumtxt);
        getotpbox=findViewById(R.id.getotpbox);
        otpauthprogressbar=findViewById(R.id.otpauthprogressbar);

        firebaseAuth=FirebaseAuth.getInstance();

        // agar if user wants to chnage the number then we have to set on click listener to number change text
        changepnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OtpAuthActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



        // agar verify btn par lcik karta hai toh chekc karenge equality and logged in karva denge
        verifynumbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredotpbyuser=getotpbox.getText().toString();
                if(enteredotpbyuser.isEmpty()){
                    Toast.makeText(OtpAuthActivity.this, "Please enter the otp", Toast.LENGTH_SHORT).show();
                }
                else { // ab progress abr ko visble karenge kyoki ham match karva rahe hai aur iske baad login karva denge
                    String otpoffirebasesent=getIntent().getStringExtra("fireotp"); // yha vahi key denge jo diya tha code sent ke time

                    // now we have to compare these codes but these cant be compared simply
                    // as the otp comes as key value format so firebase ka hi function use karenge
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(otpoffirebasesent,enteredotpbyuser);


                    // toh siginin ke liye hamne manually function define kiya hai niche with phonenumber
                   signInUserWithPhoneCredential(credential);



                }

            }
        });
    }





    // function made for signin
    private void signInUserWithPhoneCredential(PhoneAuthCredential credential) {
        // either success or failure while sigininwithcredential
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // yah task complete hone par success ya failure mili hogi
                if(task.isSuccessful()){
                    // toh progress bar ko invisble karna hia
                    otpauthprogressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(OtpAuthActivity.this, "SignedIn Successfully", Toast.LENGTH_SHORT).show();
                    // then we have to move the user from here to chat activity
                    Intent intent=new Intent(OtpAuthActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                        otpauthprogressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(OtpAuthActivity.this, "Log in Failed", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }


}
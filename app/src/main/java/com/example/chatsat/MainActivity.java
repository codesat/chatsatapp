package com.example.chatsat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText getphonenumber;
    Button sendotpbtn;
    CountryCodePicker mcountrycodepicker; // object banaya jisse lenge country code
    String countrycode; // ye country code lene ke liye hai
    String phonenumber; /// isme phone number with country code finally ayega

    FirebaseAuth firebaseAuth; // for creating user in  database
    ProgressBar mainprogressBar;


    // making the variable of PhoneAuthProvider
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codesent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcountrycodepicker=findViewById(R.id.countrycodepicker);
        getphonenumber=findViewById(R.id.phonenumbox);
        sendotpbtn=findViewById(R.id.sendotpbtn);
        mainprogressBar=findViewById(R.id.mainprogressbar);


        // it gives the instance of the current user whether user is
        // logged in app or not
        firebaseAuth=FirebaseAuth.getInstance();

        //yaha countrycode mein + ke sath selected countrycode store hojayega
        // joki mycountrycodepicker object se select hokar aya hai or automatically detect kiy ahua
        countrycode=mcountrycodepicker.getSelectedCountryCodeWithPlus();

        // ifsomeon changes the country then
        mcountrycodepicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                // ab naya jo bhi slect kiya gay hai usko store karenge in countrycode variable
                countrycode=mcountrycodepicker.getSelectedCountryCodeWithPlus();


            }
        });

        // now to sen dthe otp we have to combine the number and the country code as a single string
        sendotpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ab ham edittext se nmumber lenge
                String number; // as string kyoki ten diigt ka hai big number
                number=getphonenumber.getText().toString();
                if (number.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                }
                else  if(number.length()<10){
                    Toast.makeText(MainActivity.this, "Please enter correct number", Toast.LENGTH_SHORT).show();

                }
                else {
                    // ab agar sab kuch thik hai toh progress bar ko visible karna hai
                    // aur number ko combine karna hai countrycode ke sath aur finaaly otp sen dkarna hai
                    mainprogressBar.setVisibility(View.VISIBLE);
                    phonenumber=countrycode+number;


                    ///// itne se otp send hojayega

                    PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phonenumber) // isme phone number apna dena hai
                            .setTimeout(60L, TimeUnit.SECONDS)  // otp ka time hai 60sec mein sen dho jayega
                            .setActivity(MainActivity.this)  // isme activitgy pass karenge jaha se send kar rahe hai
                            .setCallbacks(mCallbacks)
                            .build();


                    /// otp sent part ends here

                    /// phonenumber ko verify karnahai correct hai ki nahi
                    PhoneAuthProvider.verifyPhoneNumber(options);

                }
            }
        });


        ///////////AB OTP SEND HONE KE BAAD KYA KARNA HAI////////////

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // isme autoreading of otp hoga user manually type nahi karega
                // toh otpcode automatically fetch karne ka code yaha hoga

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }


            // agar user se manually opt  type karvan hai toh ham override karenge oncodesent method ko
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken); // yaha tak code successfully sent kardiya gya hai
                Toast.makeText(MainActivity.this, "Code Sent Successfully", Toast.LENGTH_SHORT).show();
                // agar successfully snet nho gaya hai toh progressbar hatana hai
                mainprogressBar.setVisibility(View.INVISIBLE);
                codesent=s; // yah jo firebase ne otp send kiya hai vo s varaible
                // mein hai usko hamne codesent variable mein le liya hai
                // taki ham log jo user type kare usse match karva sake
                // verifictaion oth otpauth activity mien hoga toh yaha se codesent ko intent ke through
                // us actvity mein le jana hai aur verify karvan hai

                Intent otpintent=new Intent(MainActivity.this,OtpAuthActivity.class);
                otpintent.putExtra("fireotp",codesent); // yaha fireotp mein firebase ke through jo bhi otp aya hai
                // vo ab jayega otpauth activity mein
                startActivity(otpintent);


            }
        };


    }

    // user jab bhi is activity mien ayega baar otp se verify nahi karna just check karna hai ki
    // vo firebase record mein exist karta hai ki nahi


    @Override
    protected void onStart() {
        super.onStart();

        // super ke niche
        if(firebaseAuth.getCurrentUser()!=null){
            // agar user app open kiya agar user exist karta hai toh usko direct chat activity
            // mkine sen dkarenge agar vo already registered hai toh
            Intent intent=new Intent(MainActivity.this,ChatActivity.class); // chat activity mien lke jana hai naki otpauth verify karvane ke liye
            // by this below line ye upar ke code ko skip karva dega aur direct kar dega user ko directly on chat activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

    }
}
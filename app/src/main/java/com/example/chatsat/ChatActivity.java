package com.example.chatsat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatActivity extends AppCompatActivity {

    TabLayout tabLayout;
    TabItem mchat , mstatus , mcall;
    ViewPager viewPager;

    PagerAdapter pagerAdapter;
    Toolbar toolbar;

    FirebaseFirestore firebasefirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        firebasefirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();




        /// here in our chat activity we want our fragmentpageradapter to work
        // so made the object of our pageradapter


        tabLayout=findViewById(R.id.include);
        mchat=findViewById(R.id.chats);
        mstatus=findViewById(R.id.status);
        mcall=findViewById(R.id.calls);
        toolbar=findViewById(R.id.toolbar);
        viewPager=findViewById(R.id.fragmentcontainer); // here at this viewpager

        setSupportActionBar(toolbar); // by this our toolbar will gets et to its position
        // aur hame tripledot menu ke liye bhi set karna hai yaha
        Drawable drawable= ContextCompat.getDrawable(getApplicationContext(),R.drawable.chattripledot);
        // pahle context pass kiya then drawable ka naam
        toolbar.setOverflowIcon(drawable);
        // yaha se toolbar mein set kar diya drawable ko


        //now setting pageadapter here
        pagerAdapter=new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        // and now setting this pageradapter to our viewpager
        viewPager.setAdapter(pagerAdapter);

        // when someone clcik on tabs then also we have to show the selected fragment
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());  // by default jaha bhi tab hoga usi ko dikhayenge agar

                // user change karta hai toh humko change karna padega
                if (tab.getPosition()==0 || tab.getPosition()==1 || tab.getPosition()==2){
                    pagerAdapter.notifyDataSetChanged(); // yah se pageadapter ko kaon si position hai inme
                    // se upadte miljayega phir we apne swtich case se correct fragmemnt ko replace karva dega
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        /// agar user swipe karke tab change karta hai tab fragment replace karvana hai
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));




    }



    // for working on the selection of the items from the list of menu
    // first we ahve to inlfate the menu then work on selction of items


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.chatdotmenu,menu);
        return true;
    }

    // here above menu inflation is done
    // now seelction of menu items

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // here we use swtich case as on what item is selected by id we get that
        switch (item.getItemId()){
            case R.id.setting:
                Toast.makeText(this, "Setting  clicked", Toast.LENGTH_SHORT).show();
                // we have to send the user to setting activity
               Intent setintent=new Intent(ChatActivity.this,SettingActivity.class);
               startActivity(setintent);



            case R.id.profile:
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
                Intent setiintent=new Intent(ChatActivity.this,SetProfileActivity.class);
                startActivity(setiintent);



        }




        return true;
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
                        Toast.makeText(ChatActivity.this, "User is offline", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(ChatActivity.this, "User is offline", Toast.LENGTH_SHORT).show();

                    }
                });

    }
}


// here on same chat activity we ahve to craete menu so that for setting and profile setting so we
// create menu resource file of resource type menu
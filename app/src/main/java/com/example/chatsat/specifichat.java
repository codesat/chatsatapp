package com.example.chatsat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class specifichat extends AppCompatActivity {

    EditText mgetmessag;
    ImageButton sendmsgbtn;
    CardView sendmessagecardview;
    Toolbar toolbarofspecifichat;
    ImageView imgviewofspecificuser;
    TextView nameofspecificuser;

    String enterdmsgstring;
    Intent intent;
    String recievername,receiveruid,senderuid,sendername;

    FirebaseDatabase firebaseDatabase;
    String senderroom , receiverroom;
    FirebaseAuth firebaseAuth;
    ImageButton backbtnofspecifichat;

    RecyclerView specfichatrecview;

    String currentime;  // to hold the currentime
    Calendar calendar;  // to get the currentime
    SimpleDateFormat simpleDateFormat;


    MessageAdapter messageAdapter;
    ArrayList<MessageModel> messageModelArrayList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.specifichatactivity);



        mgetmessag=findViewById(R.id.getmessage);
        sendmessagecardview=findViewById(R.id.cardviewsendmsg);
        sendmsgbtn=findViewById(R.id.imageviewofsendmsg);

        toolbarofspecifichat=findViewById(R.id.toolbarofspecifichat);
        nameofspecificuser=findViewById(R.id.specificusername);
        imgviewofspecificuser=findViewById(R.id.specifichatuserimageview);
        backbtnofspecifichat=findViewById(R.id.backbtnofspecifichat);

        intent=getIntent();

        setSupportActionBar(toolbarofspecifichat);

        // now if user clicks on the toolbar then we can show the profile
        toolbarofspecifichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(specifichat.this, "Toolbar is clciked", Toast.LENGTH_SHORT).show();

            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a"); // for 24 hr format



        ///now creating a room for sender and receiver
        senderroom=senderuid+receiveruid;
        receiverroom=receiveruid+senderuid;


        messageModelArrayList=new ArrayList<>();
        specfichatrecview=findViewById(R.id.recviewofspecifichat);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setStackFromEnd(true);

        specfichatrecview.setLayoutManager(llm);
        messageAdapter=new MessageAdapter(specifichat.this,messageModelArrayList);
        specfichatrecview.setAdapter(messageAdapter);



        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats")
                .child(senderroom).child("messages");
        messageAdapter=new MessageAdapter(specifichat.this,messageModelArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                messageModelArrayList.clear();

                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    MessageModel messageModel=snapshot1.getValue(MessageModel.class);
                    messageModelArrayList.add(messageModel); // here we added the message in the arralist of chatting

                }

                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        // me user of the app is ssender so getting id
        senderuid=firebaseAuth.getUid();
        receiveruid=getIntent().getStringExtra("receiveruid"); // yaha se jispe click kiya tha chat ke liye
        /// toh receiver ki id le rahe upar wali line se jo ki chat frament se milegi
        recievername=getIntent().getStringExtra("name");







        backbtnofspecifichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        nameofspecificuser.setText(recievername);

        String uri=intent.getStringExtra("iimageurl");

        if (uri.isEmpty()){
            Toast.makeText(this, "Null is received", Toast.LENGTH_SHORT).show();
        }
        else {
            Picasso.get().load(uri).into(imgviewofspecificuser);

        }




        sendmsgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // now we will check whether user entered the message or not
                enterdmsgstring=mgetmessag.getText().toString();

                if (enterdmsgstring.isEmpty()){
                    Toast.makeText(specifichat.this, "Nothing to send", Toast.LENGTH_SHORT).show();
                }


                else {

                    // now we have to get time
                    Date date=new Date();
                    currentime=simpleDateFormat.format(calendar.getTime());


                    MessageModel messages=new MessageModel(enterdmsgstring,firebaseAuth.getUid()
                    ,date.getTime(),currentime);




                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("Chats")
                            .child(senderroom)
                            .child("messages")
                            .push()
                    .setValue(messages) // here mesage have all the things
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {




                            //here if the message from the user is sent successfully then
                            // we ahve to show in receiver room also

                            firebaseDatabase.getReference()
                                    .child("chats")
                                    .child(receiverroom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                        }
                    });


                    mgetmessag.setText(null);





                }



            }
        });




    }

    @Override
    public void onStart() {
        super.onStart();
       messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (messageAdapter!=null){
            messageAdapter.notifyDataSetChanged();

        }
    }
}

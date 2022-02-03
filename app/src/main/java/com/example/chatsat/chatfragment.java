package com.example.chatsat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class chatfragment extends Fragment {



    // global variable declaraation
    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager llm;
    private FirebaseAuth firebaseAuth;
    RecyclerView chatsrecview;
    ImageView chatuserimg;

    FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder> chatadapter; // here we have to pass
    // two parameter
    //1. FirebaseModel class name
    //2. ViewHolder


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chatfragment,container,false);



        //here inthis fragment we will fetch dat through firebase recycleradapter for this use dependecny
        // to show the list of user and their staus ,images ,name
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        chatsrecview=view.findViewById(R.id.chatsrecview);


        // here now we have to fecth all info of user
    //    Query query=firebaseFirestore.collection("Users");  // yaha sare users aa jaa rahe
      Query query=firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid()); // yaha se jo login kiya hai vo query mein
        // nahi aayega

        // jisne app mein login kiya hai usko nahi show karna hai
        FirestoreRecyclerOptions<FirebaseModel> allusername=new
                FirestoreRecyclerOptions.Builder<FirebaseModel>()
                .setQuery(query,FirebaseModel.class).build();


        chatadapter=new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteviewHolder, int i, @NonNull FirebaseModel firebaseModel) {


                noteviewHolder.particularusername.setText(firebaseModel.getUsername());
                String uri=firebaseModel.getImageurl();

                Picasso.get().load(uri).into(chatuserimg);
                //now we have to show the satus

                if (firebaseModel.getStatus().equals("Online")){
                    // setting of status
                    noteviewHolder.statususer.setText(firebaseModel.getStatus());
                    //color of status
                    noteviewHolder.statususer.setTextColor(Color.GREEN);

                }
                else {
                    noteviewHolder.statususer.setText(firebaseModel.getStatus());
                }

                // now if anyone clcik on this item of recyclerview then we have to open
                // the activity ot chat
                noteviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "itemis clicked", Toast.LENGTH_SHORT).show();
                        // yaha se user ko actual message activity mien pass akrna hai


                        Intent intent=new Intent(getActivity(),specifichat.class);
                        intent.putExtra("name",firebaseModel.getUsername());
                        intent.putExtra("receiveruid",firebaseModel.getUserId());
                        // jisko message pass kar rahe uski userid bhej rahe vaha upar
                        intent.putExtra("iimageurl",firebaseModel.getImageurl());
                        startActivity(intent);





                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view1=LayoutInflater.from(parent.getContext()).inflate(R.layout.userschatlistitem
               ,parent,false);

               return new NoteViewHolder(view1);
            }
        };

        chatsrecview.setHasFixedSize(true);
        //setting recview in linear layout manager
        llm=new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        chatsrecview.setLayoutManager(llm);
        chatsrecview.setAdapter(chatadapter);


        return view;


    }

    private class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView particularusername;
        private TextView statususer;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            particularusername=itemView.findViewById(R.id.txtusername);
            statususer=itemView.findViewById(R.id.userstatus);
            chatuserimg=itemView.findViewById(R.id.userchatlistimg);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatadapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    if (chatadapter!=null){
        chatadapter.stopListening();

    }
    }
}

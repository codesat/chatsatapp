package com.example.chatsat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<MessageModel> messageModelArrayList;


    int ITEMSEND = 1;
    int ITEMRECEIVE = 2;


    public MessageAdapter(Context context, ArrayList<MessageModel> messageModelArrayList) {
        this.context = context;
        this.messageModelArrayList = messageModelArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType==ITEMSEND){
            // that imples i am the sender
            View view= LayoutInflater.from(context).inflate(R.layout.senderchatlayout,
                    parent,false);
            return new SenderViewHolder(view);
        }
        else {

            View view= LayoutInflater.from(context).inflate(R.layout.receiverchatlayout,
                    parent,false);
            return new ReceiverViewHolder(view);
        }




    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        MessageModel messageModel=messageModelArrayList.get(position);
        if (holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder=(SenderViewHolder) holder;
            viewHolder.textViewmsg.setText(messageModel.getMessage());
            viewHolder.timeofmsg.setText(messageModel.getCurrentime());


        }
        else {

            ReceiverViewHolder viewHolder=(ReceiverViewHolder) holder;
            viewHolder.textViewmsg.setText(messageModel.getMessage());
            viewHolder.timeofmsg.setText(messageModel.getCurrentime());

        }














    }


    /// how to know who is sending message and receivbing


    @Override
    public int getItemViewType(int position) {




        MessageModel messageModel=messageModelArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageModel.getSenderId())){
            return ITEMSEND;
        }
        else {


            return ITEMRECEIVE;
        }


    }

    @Override
    public int getItemCount() {
        return messageModelArrayList.size();
    }


    class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewmsg;
        TextView timeofmsg;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewmsg = itemView.findViewById(R.id.sendermsg);
            timeofmsg = itemView.findViewById(R.id.timeofmessage);
        }
    }


    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView textViewmsg;
        TextView timeofmsg;


        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewmsg = itemView.findViewById(R.id.sendermsg);
            timeofmsg = itemView.findViewById(R.id.timeofmessage);
        }
    }
}

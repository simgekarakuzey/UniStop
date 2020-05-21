package com.atakan.unistop_tt.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atakan.unistop_tt.activities.ChatActivity;
import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder> {

    Context context;
    List<ModelUser> userList; //get user info
    private HashMap<String, String> lastMessageMap;

    public AdapterChatlist(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_chatlist.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String reveiverUid= userList.get(position).getUid();
        String userImage= userList.get(position).getImage();
        String userName= userList.get(position).getName();
        String lastMessage= lastMessageMap.get(reveiverUid);

        //set data
        holder.nameTv.setText(userName);
        if(lastMessage==null || lastMessage.equals("default")){
            holder.lastMessageTv.setVisibility(View.GONE);
        }
        else{
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_default_img).into(holder.profileIv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_default_img).into(holder.profileIv);
        }

        //handle click of user in chatlist
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start chat activity with that user
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("receiverUid", reveiverUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size(); //size of the list
    }

    class MyHolder extends RecyclerView.ViewHolder{
        //views of row_chatlist.xml
        ImageView profileIv;
        TextView nameTv, lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);

        }
    }
}

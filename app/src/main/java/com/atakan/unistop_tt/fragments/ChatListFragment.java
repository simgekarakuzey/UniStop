package com.atakan.unistop_tt.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atakan.unistop_tt.R;
import com.atakan.unistop_tt.adapters.AdapterChatlist;
import com.atakan.unistop_tt.models.ModelChat;
import com.atakan.unistop_tt.models.ModelChatlist;
import com.atakan.unistop_tt.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<ModelUser> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //init
        firebaseAuth = firebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerView);

        chatlistList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                     chatlistList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for(ModelChatlist chatlist: chatlistList){
                        if(user.getUid() != null && user.getUid().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    //adapter
                    adapterChatlist = new AdapterChatlist(getContext(), userList);
                    //setAdapter
                    recyclerView.setAdapter(adapterChatlist);
                    //set last message
                    for (int i=0; i<userList.size(); i++){
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender == null || receiver == null){
                        continue;
                    }
                    if(chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(userUid) ||
                            chat.getReceiver().equals(userUid) &&
                            chat.getSender().equals(currentUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }
                adapterChatlist.setLastMessageMap(userUid, theLastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

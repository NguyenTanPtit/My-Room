package com.example.myroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.RoomManageAdapter;
import model.Room;

public class RoomManagementActivity extends AppCompatActivity {

    FloatingActionButton back, add;
    RecyclerView recRoomManage;
    FirebaseFirestore firebaseFirestore;
    List<Room> roomList;
    RoomManageAdapter roomManageAdapter;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);

        init();
        initRec();
    }

    private void init(){
        back = findViewById(R.id.room_management_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RoomManagementActivity.this, HomeActivity.class));
            }
        });
        add = findViewById(R.id.add_room_management);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RoomManagementActivity.this, AddRoomActivity.class));
            }
        });

    }

    private void initRec(){
        recRoomManage = findViewById(R.id.recyclerView_room_management);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recRoomManage.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        roomList = new ArrayList<>();
        roomManageAdapter = new RoomManageAdapter(this,roomList);
        recRoomManage.setAdapter(roomManageAdapter);
        String userID = firebaseUser.getUid();
        firebaseFirestore.collection("All room").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                        Room room = documentSnapshot.toObject(Room.class);
                        if(room != null){
                            if(room.getOwner().equals(userID)) {
                                roomList.add(room);
                                roomManageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }


}
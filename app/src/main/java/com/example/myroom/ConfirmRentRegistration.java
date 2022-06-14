package com.example.myroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import model.Room;
import model.User;

public class ConfirmRentRegistration extends AppCompatActivity {
    private TextView roomName, roomOwner, ownerPhone, ownerEmail;
    private Room room;
    private DatabaseReference db;
    private RoundedImageView roomImg;
    private FloatingActionButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rent_registration);
        getDataIntent();
        init();
    }

    private void init(){
        db = FirebaseDatabase.getInstance().getReference().child("Users");

        roomName = findViewById(R.id.confirm_rent_RoomName);
        roomOwner = findViewById(R.id.confirm_rent_RoomOwner);
        ownerPhone = findViewById(R.id.confirm_rent_OwnerPhone);
        ownerEmail = findViewById(R.id.confirm_rent_OwnerEmail);
        roomImg = findViewById(R.id.confirm_rent_RoomImg);
        back = findViewById(R.id.confirm_rent_back);

        Glide.with(getBaseContext()).load(room.getImg_url1()).into(roomImg);
        roomName.setText(room.getName());
        db.child(room.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if(u!=null) {
                    roomOwner.setText(u.getName());
                    ownerPhone.setText(u.getPhone());
                    ownerEmail.setText(u.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void getDataIntent(){
        final Object o = getIntent().getSerializableExtra("room");
        if(o instanceof Room){
            room = (Room) o;
        }
    }
}
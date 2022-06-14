package com.example.myroom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.User;

public class ProfileActivity extends AppCompatActivity {

    TextView userName, userPhoneNumber, userEmail, userAddress, userGender, userYearOfBirth;
    ImageView userProfileImg, editUserProfile, back;

    FirebaseUser user;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        setView();
    }

    public void init(){
        userName = findViewById(R.id.profile_user_name);
        userPhoneNumber = findViewById(R.id.profile_user_phone);
        userEmail = findViewById(R.id.profile_user_email);
        userAddress = findViewById(R.id.profile_user_address);
        userGender = findViewById(R.id.profile_user_gender);
        userYearOfBirth = findViewById(R.id.profile_user_yearOfBirth);
        userProfileImg = findViewById(R.id.profile_user_img);
        editUserProfile = findViewById(R.id.profile_edit);
        back =  findViewById(R.id.profile_back);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void setView(){
        db.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if(u != null){
                    userName.setText(u.getName());
                    userPhoneNumber.setText(u.getPhone());
                    userEmail.setText(u.getEmail());
                    userAddress.setText(u.getAddress());
                    userGender.setText(u.getSex());
                    userYearOfBirth.setText(u.getYearOfBirth());
                    Glide.with(getBaseContext()).load(u.getProfileImg()).error(R.drawable.default_user_avatar).into(userProfileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi "+ error, Toast.LENGTH_SHORT).show();
            }
        });

        editUserProfile.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, UpdateUserActivity.class)));
        back.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, HomeActivity.class)));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
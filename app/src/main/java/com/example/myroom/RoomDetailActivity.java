package com.example.myroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import adapter.ImageSliderAdapter;
import me.relex.circleindicator.CircleIndicator3;
import model.Image;
import model.Room;
import model.User;

public class RoomDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private CircleIndicator3 circleIndicator;
    private Room room;
    private List<Image> list;
    private TextView roomName, roomPrice, roomAddress, numBedroom, numBathroom, numKitchen, numParking, roomDescription, roomAcreage;
    private Button rent;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        init();
    }

    public void init() {

        db = FirebaseFirestore.getInstance();

        // sliderImg
        viewPager = findViewById(R.id.viewPager_roomDetail);
        circleIndicator = findViewById(R.id.circle_indicator);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager.setPageTransformer(compositePageTransformer);
        list = getImg();
        ImageSliderAdapter adapter = new ImageSliderAdapter(list, this);
        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);

        //Room detail
        roomName = findViewById(R.id.room_detail_name);
        roomPrice = findViewById(R.id.room_detail_price);
        roomAddress = findViewById(R.id.room_detail_address);
        numBedroom = findViewById(R.id.room_detail_numBedroom);
        numBathroom = findViewById(R.id.room_detail_numBathroom);
        numKitchen = findViewById(R.id.room_detail_numKitchen);
        numParking = findViewById(R.id.room_detail_numParking);
        roomDescription = findViewById(R.id.room_detail_description);
        roomAcreage = findViewById(R.id.room_detail_acreage);
        rent = findViewById(R.id.room_detail_rent);

        roomName.setText(room.getName());
        roomPrice.setText(room.getPrice());
        roomAddress.setText(room.getAddress());
        numBedroom.setText(room.getNum_bedroom());
        numBathroom.setText(room.getNum_bathroom());
        numKitchen.setText(room.getNum_kitchen());
        numParking.setText(room.getNum_parking());
        roomDescription.setText(room.getDescription());
        roomAcreage.setText(room.getAcreage());
        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentRegistration();
            }
        });
    }

    private List<Image> getImg() {
        final Object o = getIntent().getSerializableExtra("detail");

        if (o instanceof Room) {
            room = (Room) o;
        }
        List<Image> listImg = new ArrayList<>();
        listImg.add(new Image(room.getImg_url1()));
        listImg.add(new Image(room.getImg_url2()));
        listImg.add(new Image(room.getImg_url3()));
        return listImg;
    }

    private void rentRegistration() {
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference().child("Users");
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceUser.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if (u != null) {
                    if (u.getPhone().equals("")) {
                        Toast.makeText(RoomDetailActivity.this, "Bạn cần cập nhật số điện thoại để đăng ký!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RoomDetailActivity.this, UpdateUserActivity.class));
                    } else {
                        confirmRent();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void confirmRent() {
        String uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String CurrentDate,CurrentTime;
        Calendar calForDate= Calendar.getInstance();

        SimpleDateFormat currentdate= new SimpleDateFormat("dd/MM/yyyy");
        CurrentDate=currentdate.format(calForDate.getTime());

        SimpleDateFormat currnettime= new SimpleDateFormat("HH:mm");
        CurrentTime=currnettime.format(calForDate.getTime());
        String roomId = room.getId();
        String ownerId = room.getOwner();
        if (uId.equals(ownerId)) {
            Toast.makeText(this, "Bạn không thể đăng kí thuê phòng này! ", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> rentConfirm = new HashMap<>();
            rentConfirm.put("renterId", uId);
            rentConfirm.put("roomId", roomId);
            rentConfirm.put("ownerId", ownerId);
            rentConfirm.put("currentDate",CurrentDate);
            rentConfirm.put("currentTime",CurrentTime);
            db.collection("Rent Registration").add(rentConfirm).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Toast.makeText(RoomDetailActivity.this, "Đã xác nhận thông tin", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RoomDetailActivity.this, ConfirmRentRegistration.class);
                    i.putExtra("room", room);
                    startActivity(i);
                }
            });
        }
    }
}
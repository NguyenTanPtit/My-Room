package com.example.myroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import adapter.ImageSliderAdapter;
import me.relex.circleindicator.CircleIndicator3;
import model.Image;
import model.Room;

public class RoomDetailManage extends AppCompatActivity {

    private ViewPager2 viewPager;
    private CircleIndicator3 circleIndicator;
    private Room room;
    private List<Image> list;
    private TextView roomName, roomPrice, roomAddress, numBedroom, numBathroom, numKitchen, numParking, roomDescription, roomAcreage;
    private Button delete, edit;
    private FirebaseFirestore fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail_manage);
        init();
    }
    public void init(){

        fs = FirebaseFirestore.getInstance();
        // sliderImg
        viewPager = findViewById(R.id.viewPager_manage_roomDetail);
        circleIndicator = findViewById(R.id.circle_manage_indicator);
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
        ImageSliderAdapter adapter = new ImageSliderAdapter(list,this);
        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);

        //Room detail
        roomName = findViewById(R.id.room_detail_manage_name);
        roomPrice = findViewById(R.id.room_detail_manage_price);
        roomAddress = findViewById(R.id.room_detail_manage_address);
        numBedroom = findViewById(R.id.room_detail_manage_numBedroom);
        numBathroom = findViewById(R.id.room_detail_manage_numBathroom);
        numKitchen = findViewById(R.id.room_detail_manage_numKitchen);
        numParking = findViewById(R.id.room_detail_manage_numParking);
        roomDescription = findViewById(R.id.room_detail_manage_description);
        delete = findViewById(R.id.room_detail_manage_delete);
        edit = findViewById(R.id.room_detail_manage_edit);
        roomAcreage = findViewById(R.id.room_detail_acreage);

        roomName.setText(room.getName());
        roomPrice.setText(room.getPrice());
        roomAddress.setText(room.getAddress());
        numBedroom.setText(room.getNum_bedroom());
        numBathroom.setText(room.getNum_bathroom());
        numKitchen.setText(room.getNum_kitchen());
        numParking.setText(room.getNum_parking());
        roomDescription.setText(room.getDescription());
        roomAcreage.setText(room.getAcreage());
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRoom();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRoom();
            }
        });
    }

    @NonNull
    private List<Image> getImg(){
        final Object o =getIntent().getSerializableExtra("detailManage");

        if(o instanceof Room){
            room = (Room) o;
        }
        List <Image> listImg = new ArrayList<>();
        listImg.add(new Image(room.getImg_url1()));
        listImg.add(new Image(room.getImg_url2()));
        listImg.add(new Image(room.getImg_url3()));
        return listImg;
    }

    private void deleteRoom(){
        fs.collection("All room").document(room.getId())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RoomDetailManage.this, "Đã xóa phòng khỏi danh sách", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RoomDetailManage.this, RoomManagementActivity.class));
                    finish();
                }else {
                    Toast.makeText(RoomDetailManage.this, task.getException().getMessage()+"!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void editRoom(){
        Intent i = new Intent(RoomDetailManage.this,EditRoomInfo.class);
        i.putExtra("Room edit",room);
        startActivity(i);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

}
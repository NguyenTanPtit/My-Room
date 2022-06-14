package com.example.myroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import model.Registration;
import model.Room;
import model.User;

public class RenterManageDetailActivity extends AppCompatActivity {

    private Registration reg;
    private RoundedImageView imgRoom;
    private TextView roomName, roomPrice, renterName, renterPhone, renterEmail, regDate, regTime;
    private FloatingActionButton back;
    private Button delete;

    private FirebaseFirestore fs;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_manage_detail);
        fs = FirebaseFirestore.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        getDataIntent();
        initView();
    }

    private void initView() {
        imgRoom = findViewById(R.id.reg_detail_roomImg);
        roomName = findViewById(R.id.reg_detail_roomName);
        roomPrice = findViewById(R.id.reg_detail_roomPrice);
        renterName = findViewById(R.id.reg_detail_renterName);
        renterEmail = findViewById(R.id.reg_detail_renterEmail);
        renterPhone = findViewById(R.id.reg_detail_renterPhone);
        regDate = findViewById(R.id.reg_detail_regDate);
        regTime = findViewById(R.id.reg_detail_regTime);
        back = findViewById(R.id.reg_detail_back);
        delete = findViewById(R.id.reg_detail_delete);

        String roomID = reg.getRoomId();
        String renterId = reg.getRenterId();
        regDate.setText(reg.getCurrentDate());
        regTime.setText(reg.getCurrentTime());

        fs.collection("All room").document(roomID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Room room = task.getResult().toObject(Room.class);
                if(room!=null){
                    Glide.with(getBaseContext()).load(room.getImg_url1()).into(imgRoom);
                    roomName.setText(room.getName());
                    roomPrice.setText(room.getPrice());
                }
            }
        });
        db.child(renterId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if(u!=null){
                    renterName.setText(u.getName());
                    renterEmail.setText(u.getEmail());
                    renterPhone.setText(u.getPhone());
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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReg();
            }
        });
    }

    private void deleteReg() {
        fs.collection("Rent Registration").document(reg.getRegId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RenterManageDetailActivity.this, "Đã xóa khỏi danh sách đăng ký!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataIntent() {
        final Object o = getIntent().getSerializableExtra("registration Detail");
        if(o instanceof Registration){
            reg = (Registration) o;
        }
    }


}
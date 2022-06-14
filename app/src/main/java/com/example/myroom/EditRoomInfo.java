package com.example.myroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import model.Room;

public class EditRoomInfo extends AppCompatActivity {
    private EditText name, address, acreage, price, numBed, numBath, numKitchen, numParking, description;
    private FloatingActionButton back;
    private Button confirm;
    private ImageView img1, img2, img3;
    private HashMap<String, Object> roomInfo;
    private FirebaseFirestore fs ;
    private Room room;

    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room_info);
        init();

    }

    private void init(){
        getDataIntent();
        name = findViewById(R.id.edit_room_name);
        name.setText(room.getName());
        address = findViewById(R.id.edit_room_address);
        address.setText(room.getAddress());
        acreage = findViewById(R.id.edit_room_acreage);
        acreage.setText(room.getAcreage());
        price = findViewById(R.id.edit_room_price);
        price.setText(room.getPrice());
        numBed = findViewById(R.id.edit_room_numbed);
        numBed.setText(room.getNum_bedroom());
        numBath = findViewById(R.id.edit_room_numbath);
        numBath.setText(room.getNum_bathroom());
        numKitchen = findViewById(R.id.edit_room_numKitchen);
        numKitchen.setText(room.getNum_kitchen());
        numParking = findViewById(R.id.edit_room_numParking);
        numParking.setText(room.getNum_parking());
        description = findViewById(R.id.edit_room_description);
        description.setText(room.getDescription());
        back = findViewById(R.id.edit_room_back);
        confirm = findViewById(R.id.edit_room_confirm);
        img1 = findViewById(R.id.edit_room_img1);
        Glide.with(getBaseContext()).load(room.getImg_url1()).into(img1);
        img2 = findViewById(R.id.edit_room_img2);
        Glide.with(getBaseContext()).load(room.getImg_url2()).into(img2);
        img3 = findViewById(R.id.edit_room_img3);
        Glide.with(getBaseContext()).load(room.getImg_url3()).into(img3);
        roomInfo = new HashMap<>();
        storage = FirebaseStorage.getInstance();
        fs = FirebaseFirestore.getInstance();

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, 11);
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, 22);
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, 33);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditRoomInfo.this, RoomDetailManage.class));
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoom();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Đang tải ảnh...");
        String userId =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(requestCode == 11) {
            if (data.getData() != null) {
                Uri img = data.getData();
                img1.setImageURI(img);
                final String randomKey = UUID.randomUUID().toString();
                final StorageReference reference = storage.getReference("Room_picture").child(userId).child(randomKey);

                reference.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                roomInfo.put("img_url1", uri.toString());
                                pd.dismiss();
                                Toast.makeText(EditRoomInfo.this, "Tải thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }else if(requestCode == 22) {
            if (data.getData() != null) {
                Uri img = data.getData();
                img2.setImageURI(img);
                final String randomKey = UUID.randomUUID().toString();
                final StorageReference reference = storage.getReference("Room_picture").child(userId).child(randomKey);
                reference.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                roomInfo.put("img_url2", uri.toString());
                                pd.dismiss();
                                Toast.makeText(EditRoomInfo.this, "Tải thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }else {
            if (data.getData() != null) {
                Uri img = data.getData();
                img3.setImageURI(img);
                final String randomKey = UUID.randomUUID().toString();
                final StorageReference reference = storage.getReference("Room_picture").child(userId).child(randomKey);
                reference.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.show();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                roomInfo.put("img_url3", uri.toString());
                                pd.dismiss();
                                Toast.makeText(EditRoomInfo.this, "Tải thành công", Toast.LENGTH_SHORT).show();
                                Log.d("info", roomInfo.toString());
                            }
                        });
                    }
                });
            }
        }
    }

    private void addRoom(){
        String rName = name.getText().toString().trim();
        if(rName.isEmpty()){
            name.setError("Tên không được để trống!");
            return;
        }
        String rAddress = address.getText().toString().trim();
        if(rAddress.isEmpty()){
            address.setError("Địa chỉ không được để trống!");
            return;
        }
        String rAcreage = acreage.getText().toString().trim();
        if(rAcreage.isEmpty()){
            acreage.setError("Diện tích phòng không được trống!");
            return;
        }
        String rPrice = price.getText().toString().trim();
        if(rPrice.isEmpty()){
            price.setError("Giá phòng không được trống");
            return;
        }
        String rNumbed = numBed.getText().toString().trim();
        if(rNumbed.isEmpty()){
            numBed.setError("Số phòng ngủ không được trống!");
            return;
        }
        String rNumBath = numBath.getText().toString().trim();
        if(rNumBath.isEmpty()){
            numBath.setError("Số phòng ngủ không được trống!");
            return;
        }
        String rNumKitchen = numKitchen.getText().toString().trim();
        if(rNumKitchen.isEmpty()){
            numKitchen.setError("Số phòng ngủ không được trống!");
            return;
        }
        String rNumParking = numParking.getText().toString().trim();
        if(rNumParking.isEmpty()){
            numBed.setError("Số phòng ngủ không được trống!");
            return;
        }
        String rDes = description.getText().toString().trim();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        roomInfo.put("owner", uid);
        roomInfo.put("name",rName);
        roomInfo.put("address",rAddress);
        roomInfo.put("price",rPrice);
        roomInfo.put("acreage",rAcreage);
        roomInfo.put("num_bedroom",rNumbed);
        roomInfo.put("num_bathroom",rNumBath);
        roomInfo.put("num_kitchen",rNumKitchen);
        roomInfo.put("num_parking",rNumParking);
        roomInfo.put("description",rDes);

        fs.collection("All room").document(room.getId()).update(roomInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditRoomInfo.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditRoomInfo.this, RoomManagementActivity.class));
                    finish();
                }
            }
        });
    }

    private void getDataIntent(){
        final Object o = getIntent().getSerializableExtra("Room edit");
        if(o instanceof Room){
            room = (Room) o;
        }
    }
}
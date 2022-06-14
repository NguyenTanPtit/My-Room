package com.example.myroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import model.User;

public class UpdateUserActivity extends AppCompatActivity {

    EditText name, phone, address;
    NumberPicker yearOfBirth;
    RadioButton male, female, other;
    RadioGroup radioGroup;
    Button update;
    CircleImageView profileImg;

    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase db;
    FirebaseFirestore firebaseFirestore;
    HashMap<String, Object> infor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        init();
    }

    private void init() {
        yearOfBirth = findViewById(R.id.update_profile_yearOfBirth);
        yearOfBirth.setMinValue(1922);
        yearOfBirth.setMaxValue(2021);
        yearOfBirth.setWrapSelectorWheel(false);
        yearOfBirth.setValue(2001);
        profileImg = findViewById(R.id.profile_img);
        name = findViewById(R.id.update_profile_name);
        phone = findViewById(R.id.update_profile_phone);
        address = findViewById(R.id.update_profile_add);
        male = findViewById(R.id.radioButtonMale);
        female = findViewById(R.id.radioButtonFemale);
        other = findViewById(R.id.radioButtonOther);
        update = findViewById(R.id.update_profile);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(R.id.radioButtonMale);

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        profileImg.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(i, 33);
        });

        update.setOnClickListener(view -> updateProfile());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if (u != null) {
                    name.setText(u.getName());
                    if(!u.getPhone().equals("")) {
                        phone.setText(u.getPhone());
                        phone.setFocusable(false);
                    }
                    address.setText(u.getAddress());
                    if (u.getSex().equals("Nam")) {
                        radioGroup.check(R.id.radioButtonMale);
                    } else if (u.getSex().equals("Nữ")) {
                        radioGroup.check(R.id.radioButtonFemale);
                    } else radioGroup.check(R.id.radioButtonOther);
                    if (u.getYearOfBirth() != null) {
                        yearOfBirth.setValue(Integer.parseInt(u.getYearOfBirth()));
                    }
                    Glide.with(getBaseContext()).load(u.getProfileImg()).error(R.drawable.default_profile_user).into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile() {
        infor = new HashMap<>();
        String year = String.valueOf(yearOfBirth.getValue());
        String uname = name.getText().toString().trim();

        if (uname.isEmpty()) {
            name.setError("Họ tên không được trống");
            return;
        }
        String uphone = phone.getText().toString().trim();
        if (uphone.isEmpty()) {
            phone.setError("Số điện thoại không được trống");
            return;
        }
        if (uphone.length() < 10 || uphone.length() > 19) {
            phone.setError("Số điện thoại chưa đúng định dạng");
            return;
        }
        String uaddress = address.getText().toString().trim();
        if (uaddress.isEmpty()) {
            address.setError("Địa chỉ không được bỏ trống");
            return;
        }
        String usex = "";
        if (male.isChecked()) {
            usex = "Nam";
        } else if (female.isChecked()) {
            usex = "Nữ";
        } else if (other.isChecked())
            usex = "Khác";

        infor.put("name", uname);
        infor.put("phone", uphone);
        infor.put("address", uaddress);
        infor.put("sex", usex);
        infor.put("yearOfBirth", year);
        infor.put("role","lease");
        checkPhoneNumber(uphone, new OnCheckPhoneNumberListener() {
            @Override
            public void onSuccess() {
                updateInfor();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(UpdateUserActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerifyPhoneNumber() {
                Toast.makeText(UpdateUserActivity.this, "Số điện thoại cần xác thực", Toast.LENGTH_SHORT).show();
                Log.d("phone Num", uphone);
                verifyPhoneNumber("+84"+uphone);
            }
        });



    }

    // up profile image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Đang tải ảnh...");
        if (data.getData() != null) {
            Uri profileUri = data.getData();
            profileImg.setImageURI(profileUri);
            final StorageReference reference = storage.getReference().child("profile_picture")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            db.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profileImg").setValue(uri.toString());
                            pd.dismiss();
                            Toast.makeText(UpdateUserActivity.this, "Tải thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    // check phone if exist
    private void checkPhoneNumber(String phone, @NonNull OnCheckPhoneNumberListener listener){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if(u!=null) {
                    if (u.getPhone().equals(phone)) {
                        listener.onSuccess();
                    } else {
                        reference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    listener.onFailure("Số điện thoại đã tồn tại! Vui lòng cung cấp số điện thoại khác");
                                } else
                                    listener.onVerifyPhoneNumber();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Lỗi update", String.valueOf(error));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //verifyPhoneNumber
    private void verifyPhoneNumber(String strPhoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(strPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        updateInfor();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(UpdateUserActivity.this, "LỖi xác thực", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationID, forceResendingToken);
                        gotoEnterOTPActivity(strPhoneNumber, verificationID);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void updateInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("Users").child(userId);
        ref.updateChildren(infor, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(UpdateUserActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void gotoEnterOTPActivity(String strPhoneNum, String verificationId) {
        Intent i = new Intent(this,EnterOTPPhoneNumberActivity.class);
        i.putExtra("Phone_num", strPhoneNum);
        i.putExtra("verification_Id", verificationId);
        i.putExtra("infor",infor);
        startActivity(i);
    }

}
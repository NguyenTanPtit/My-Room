package com.example.myroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EnterOTPPhoneNumberActivity extends AppCompatActivity {

    EditText otp;
    Button btn;
    FloatingActionButton btnBack;
    private TextView sendOTPAgain;
    private String strPhoneNumber;
    private String verificationId;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private HashMap<String,Object> inFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otpphone_number);
        getDataIntent();
        init();
    }

    private void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        btnBack = findViewById(R.id.enterOTP_back);
        otp = findViewById(R.id.otp);
        btn = findViewById(R.id.verify);
        sendOTPAgain = findViewById(R.id.send_OTP_again);
        btn.setOnClickListener(view -> {
            String otpCode = otp.getText().toString().trim();
            onClickSendOTP(otpCode);
        });

        sendOTPAgain.setOnClickListener(view -> onclickSendOTPAgain());
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(EnterOTPPhoneNumberActivity.this, UpdateUserActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void onclickSendOTPAgain() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(strPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setForceResendingToken(resendingToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePhoneNumber(phoneAuthCredential);
                        updateInfor();

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.d("Lỗi", String.valueOf(e));
                        Toast.makeText(EnterOTPPhoneNumberActivity.this, "LỖi xác thực", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationID, forceResendingToken);
                        verificationId = verificationID;
                        resendingToken = forceResendingToken;
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void onClickSendOTP(String otpCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePhoneNumber(credential);
        updateInfor();
    }

    private void getDataIntent(){
        strPhoneNumber =  getIntent().getStringExtra("Phone_num");
        Log.d("Phone sentcode", strPhoneNumber);
        verificationId = getIntent().getStringExtra("verification_Id");
        inFor = (HashMap<String, Object>) getIntent().getSerializableExtra("infor");
    }



    private void gotoProfileActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
    }
    private void updateInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("Users").child(userId);
        ref.updateChildren(inFor, (error, ref1) -> {
            Toast.makeText(EnterOTPPhoneNumberActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            gotoProfileActivity();
        });
    }
}
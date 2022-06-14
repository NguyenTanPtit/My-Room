package com.example.myroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPassActivity extends AppCompatActivity {

    private FloatingActionButton back;
    private EditText email;
    private Button sendmail;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        init();
    }

    private void init(){
        back = findViewById(R.id.resetPass_back);
        email = findViewById(R.id.resetPass_email);
        sendmail = findViewById(R.id.resetPass_sendMail);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uEmail = email.getText().toString().trim();
                checkEmail(uEmail);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResetPassActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void checkEmail(String email){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Log.d("email user",email);
        reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        sendEmailResetPass(email);
                    } else {
                        Toast.makeText(ResetPassActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendEmailResetPass(String email){
        progressDialog.setMessage("Đang gửi yêu cầu...");
        progressDialog.show();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.cancel();
                    Toast.makeText(ResetPassActivity.this, "Kiểm tra email để khôi phục", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.cancel();
                    Toast.makeText(ResetPassActivity.this, "Thất bại vui lòng kiểm tra lại thông tin", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
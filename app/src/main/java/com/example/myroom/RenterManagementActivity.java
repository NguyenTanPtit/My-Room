package com.example.myroom;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapter.RenterManageAdapter;
import model.Registration;

public class RenterManagementActivity extends AppCompatActivity {

    private RecyclerView rec;
    private FloatingActionButton back;
    private FirebaseFirestore fs;
    private List<Registration> registrationList;
    private RenterManageAdapter renterManageAdapter;
    private ProgressDialog pg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_management);
        pg = new ProgressDialog(this);
        initRec();
        back = findViewById(R.id.renter_management_back);
        back.setOnClickListener(view -> onBackPressed());

    }

    private void initRec() {
        rec = findViewById(R.id.recyclerView_renter_management);
        rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fs = FirebaseFirestore.getInstance();
        registrationList = new ArrayList<>();
        renterManageAdapter = new RenterManageAdapter(this, registrationList);
        rec.setAdapter(renterManageAdapter);
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        pg.setMessage("Đang load...");
        pg.show();
        fs.collection("Rent Registration").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                    Registration reg = snapshot.toObject(Registration.class);
                    if (reg != null) {
                        if (reg.getOwnerId().equals(uid)) {
                            reg.setRegId(snapshot.getId());
                            registrationList.add(reg);
                            renterManageAdapter.notifyDataSetChanged();
                        }
                    }
                }
                pg.dismiss();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
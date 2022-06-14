package com.example.myroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapter.RoomHomeAdapter;
import adapter.SearchAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Room;
import model.User;

public class HomeActivity extends AppCompatActivity {

    private static final float END_SCALE = 0.7f;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView navUserName, navUserEmail;
    private CircleImageView navUserProfileImg;
    private Toolbar toolbar;
    private ConstraintLayout content;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<Room> roomList;
    private List<Room> searchList;
    private RoomHomeAdapter roomHomeAdapter;
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSearch;
    private EditText searchEdt;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initNav();
        initRec();
        searchView();
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()){
                    searchList.clear();
                    searchAdapter.notifyDataSetChanged();
                    reloadAllRoom();
                }else {
                    searchRoom(editable.toString().trim());
                }
            }
        });
    }

    public void initNav() {
        navigationView = findViewById(R.id.nav_view);
        navUserName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        navUserEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_emailUser);
        navUserProfileImg = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_profileImg);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        content = findViewById(R.id.content_home);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = user.getUid();
        Menu menu = navigationView.getMenu();
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if (u != null) {
                    navUserName.setText(u.getName());
                    navUserEmail.setText(u.getEmail());
                    Glide.with(getBaseContext()).load(u.getProfileImg()).error(R.drawable.default_user_avatar).into(navUserProfileImg);
                    if(u.getRole().equals("normal")){
                        menu.findItem(R.id.nav_management_room).setVisible(false);
                        menu.findItem(R.id.nav_manage_renter).setVisible(false);
                    }else {
                        menu.findItem(R.id.nav_sub).setVisible(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), "Lỗi" + error.getMessage() + "!", Toast.LENGTH_SHORT).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) { //set view when click item menu
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        finish();
                        startActivity(getIntent());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                        break;
                    case R.id.nav_sub:
                        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User u = snapshot.getValue(User.class);
                                if (u != null ) {
                                    if( u.getRole().equals("normal")) {
                                        Toast.makeText(HomeActivity.this, "Bạn cần cập nhật đầy đủ thông tin trước khi đăng kí!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(HomeActivity.this, UpdateUserActivity.class));
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getBaseContext(), "Lỗi" + error.getMessage() + "!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case R.id.nav_management_room:
                        startActivity(new Intent(HomeActivity.this, RoomManagementActivity.class));
                        break;
                    case R.id.nav_manage_renter:
                        startActivity(new Intent(HomeActivity.this, RenterManagementActivity.class));
                        break;
                }
                return true;
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final float diffScaleOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaleOffset;
                content.setScaleX(offsetScale);
                content.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float yOffsetDiff = content.getWidth() * diffScaleOffset / 2;
                final float xTranslation = xOffset - yOffsetDiff;
                content.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Ấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }



    public void initRec() {
        recyclerView = findViewById(R.id.rec_home);
        db = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        roomList = new ArrayList<>();
        roomHomeAdapter = new RoomHomeAdapter(this, roomList);
        recyclerView.setAdapter(roomHomeAdapter);

        db.collection("All room").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Room room = documentSnapshot.toObject(Room.class);
                    roomList.add(room);
                    roomHomeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void searchView(){
        recyclerViewSearch = findViewById(R.id.recyclerView_search);
        searchEdt = findViewById(R.id.editText_search);
        searchList = new ArrayList<>();
        searchAdapter = new SearchAdapter(this,searchList);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false));
        recyclerViewSearch.setAdapter(searchAdapter);
        recyclerViewSearch.setHasFixedSize(true);
    }

    private void searchRoom(String address){
        if(!address.isEmpty()){
            db.collection("All room").whereGreaterThanOrEqualTo("address",chuanHoa(address)).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()&&task.getResult()!=null){
                            roomList.clear();
                            searchList.clear();
                            searchAdapter.notifyDataSetChanged();
                            roomHomeAdapter.notifyDataSetChanged();
                            for(DocumentSnapshot doc: task.getResult().getDocuments()){
                                Room room =doc.toObject(Room.class);
                                if(room!=null) {
                                    searchList.add(room);
                                    roomHomeAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }
    private String chuanHoa(String s){
        String name=s.trim().toLowerCase();
        String [] tu = name.split("\\s+");
        StringBuilder res= new StringBuilder();
        for(String str: tu){
            res.append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).append(" ");
        }
        return res.toString().trim();
    }

    private void reloadAllRoom(){
        db.collection("All room").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Room room = documentSnapshot.toObject(Room.class);
                    roomList.add(room);
                    roomHomeAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myroom.R;
import com.example.myroom.RenterManageDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Registration;
import model.User;

public class RenterManageAdapter extends RecyclerView.Adapter<RenterManageAdapter.ViewHolder>{

    Context context;
    List<Registration> renterManageList;
    FirebaseDatabase db;

    public RenterManageAdapter(Context context, List<Registration> renterManageList) {
        this.context = context;
        this.renterManageList = renterManageList;
        db = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.renter_manage_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RenterManageAdapter.ViewHolder holder, int position) {
        db.getReference("Users").child(renterManageList.get(position).getRenterId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if(u!=null){
                    Glide.with(context).load(u.getProfileImg()).error(R.drawable.default_user_avatar).into(holder.avatar);
                    holder.username.setText(u.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RenterManageDetailActivity.class);
                i.putExtra("registration Detail",renterManageList.get(holder.getAdapterPosition()));
                context.startActivity(i);
            }
        });

    }



    @Override
    public int getItemCount() {
        return renterManageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.renter_manage_user_avatar);
            username = itemView.findViewById(R.id.renter_manage_user_name);
        }
    }
}

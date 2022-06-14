package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myroom.R;
import com.example.myroom.RoomDetailManage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import model.Room;

public class RoomManageAdapter extends RecyclerView.Adapter<RoomManageAdapter.ViewHolder> {

    Context context;
    List<Room> roomManageList;
    FirebaseFirestore fs;

    public RoomManageAdapter(Context context, List<Room> roomManageList) {
        this.context = context;
        this.roomManageList = roomManageList;
        fs = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_manage_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(roomManageList.get(position).getImg_url1()).into(holder.roomManageImg);
        holder.name.setText(roomManageList.get(position).getName());
        holder.price.setText(roomManageList.get(position).getPrice());
        holder.address.setText(roomManageList.get(position).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RoomDetailManage.class);
                i.putExtra("detailManage",roomManageList.get(holder.getAdapterPosition()));
                context.startActivity(i);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fs.collection("All room").document(roomManageList.get(holder.getAdapterPosition()).getId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            roomManageList.remove(roomManageList.get(holder.getAdapterPosition()));
                            notifyDataSetChanged();
                            Toast.makeText(context, "Đã xóa phòng khỏi danh sách", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, task.getException().getMessage()+"!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomManageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView roomManageImg, delete;
        TextView name, price, address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomManageImg = itemView.findViewById(R.id.view_room_manage_img);
            delete = itemView.findViewById(R.id.view_room_manage_delete);
            name = itemView.findViewById(R.id.view_room_manage_name);
            price = itemView.findViewById(R.id.view_room_manage_price);
            address = itemView.findViewById(R.id.view_room_manage_address);

        }
    }
}

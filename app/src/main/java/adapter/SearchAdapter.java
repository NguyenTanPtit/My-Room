package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myroom.R;
import model.Room;
import com.example.myroom.RoomDetailActivity;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    Context context;
    List<Room> allRoomList;

    public SearchAdapter(Context context, List<Room> allRoom) {
        this.context = context;
        this.allRoomList = allRoom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_home_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(allRoomList.get(position).getImg_url1()).into(holder.allRoomImg);
        holder.name.setText(allRoomList.get(position).getName());
        holder.address.setText(allRoomList.get(position).getAddress());
        holder.price.setText(allRoomList.get(position).getPrice());
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, RoomDetailActivity.class);
            i.putExtra("detail", allRoomList.get(holder.getAdapterPosition()));
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return allRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView allRoomImg;
        TextView name, price, address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            allRoomImg = itemView.findViewById(R.id.view_room_home_img);
            name = itemView.findViewById(R.id.view_home_room_name);
            price = itemView.findViewById(R.id.view_home_room_price);
            address = itemView.findViewById(R.id.view_home_room_address);
        }
    }
}

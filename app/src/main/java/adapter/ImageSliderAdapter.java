package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import model.Image;
import com.example.myroom.R;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImgViewHolder> {

    Context context;
    List<Image> list;

    public ImageSliderAdapter(List<Image> list, Context context) {
        this.context= context;
        this.list = list;
    }



    @NonNull
    @Override
    public ImageSliderAdapter.ImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_img_item,parent,false);

        return new ImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderAdapter.ImgViewHolder holder, int position) {
        Image img = list.get(position);

        Glide.with(context).load(img.getUrl()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        if(list!= null){
            return list.size();
        }
        return 0;
    }

    public class ImgViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public ImgViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_slider);
        }
    }
}

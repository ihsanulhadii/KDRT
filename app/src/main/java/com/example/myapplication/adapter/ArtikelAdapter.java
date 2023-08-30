package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.ArtikelModel;
import com.example.myapplication.model.ReportModel;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.ItemViewHolder> {

    private Context context;
    private List<ArtikelModel> artikelList;
    public interface OnItemClickListener {
        void onItemClick(ArtikelModel artikeltModel);
    }

    private ArtikelAdapter.OnItemClickListener clickListener;

    public void setOnItemClickListener(ArtikelAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public ArtikelAdapter(Context context, List<ArtikelModel> itemList) {
        this.context = context;
        this.artikelList = artikelList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ArtikelModel item = artikelList.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());

        // Load image using Picasso or any other image loading library
        Picasso.get().load(item.getImg()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return artikelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}

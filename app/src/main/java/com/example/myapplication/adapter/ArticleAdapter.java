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
import com.example.myapplication.model.ArticleModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ItemViewHolder> {

    private Context context;
    private List<ArticleModel> artikelList;
    public interface OnItemClickListener {
        void onItemClick(ArticleModel artikeltModel);
    }

    private ArticleAdapter.OnItemClickListener clickListener;

    public void setOnItemClickListener(ArticleAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public ArticleAdapter(Context context, List<ArticleModel> itemList) {
        this.context = context;
        this.artikelList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ArticleModel item = artikelList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());

        if(position==1){

        }


        Date datePublish = item.getDateValue("createdDate");

        String inputDateString = datePublish.toString();
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")); // Indonesian locale

        try {
            Date date = inputFormat.parse(inputDateString);
            String outputDate = outputFormat.format(date);
            holder.tvDate.setText(outputDate);

            System.out.println(outputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Load image using Picasso or any other image loading library
        Picasso.get().load(item.getImg()).into(holder.ivArticle);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemClick(item);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return artikelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivArticle;
        TextView tvTitle,tvDate,tvContent;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArticle = itemView.findViewById(R.id.ivArticle);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContent=  itemView.findViewById(R.id.tvContent);
        }
    }
}

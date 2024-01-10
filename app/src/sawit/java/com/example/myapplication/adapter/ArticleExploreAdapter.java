package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.model.ArticleModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleExploreAdapter extends RecyclerView.Adapter<ArticleExploreAdapter.ItemViewHolder> {

    private Context context;
    private List<ArticleModel> artikelList;
    public interface OnItemClickListener {
        void onItemClick(ArticleModel artikeltModel);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public ArticleExploreAdapter(Context context, List<ArticleModel> itemList) {
        this.context = context;
        this.artikelList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_article, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ArticleModel item = artikelList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());

        if(item.getCommentCount()!=null){
            holder.tvCommentCount.setText(item.getCommentCount().toString()+" Komentar");
        }



        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);


        if (position == 0) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setMarginStart(holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.padding));
            holder.itemView.setLayoutParams(layoutParams);
        }

       float scaleFactor = 1.5f;

        int width = 0;
        if (artikelList != null && (artikelList.size() == 1 || artikelList.size() == 2)) {
            width = (int) ((display.getWidth() / 2) * scaleFactor) - 10;
        } else {
            width = (int) ((display.getWidth() / 2) * scaleFactor) - 10;
        }

      // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.rlView.getLayoutParams();
        ViewGroup.LayoutParams params = holder.rlView.getLayoutParams();
        if (params != null) {
            params.width = width;
            // params.height = width; // Uncomment this line if you also want to set the height
            holder.rlView.setLayoutParams(params);
        }


        holder.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                // Menentukan tipe konten yang akan dibagikan (URL dalam contoh ini)
                shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.urlArticle+item.getId());
                shareIntent.setType("text/plain");

                // Menampilkan aplikasi yang mendukung fungsi share
                context.startActivity(Intent.createChooser(shareIntent, "Bagikan URL melalui"));
            }
        });




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
        TextView tvTitle,tvDate,tvContent,tvCommentCount;
        RelativeLayout rlView;


        LinearLayout llShare;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArticle = itemView.findViewById(R.id.ivArticle);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContent=  itemView.findViewById(R.id.tvContent);
            rlView = itemView.findViewById(R.id.itemView);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            llShare = itemView.findViewById(R.id.llShare);
        }
    }
}

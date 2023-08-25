package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ThreadModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

   private List<ThreadModel> threadList;

   public ThreadAdapter(List<ThreadModel> threadList) {
      this.threadList = threadList;
   }

   @NonNull
   @Override
   public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_threads, parent, false);
      return new ThreadViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
      ThreadModel thread = threadList.get(position);
      holder.titleTextView.setText(thread.getTitle());
      Picasso.get()
              .load(thread.getImg())  // Assuming getImg() returns the image URL
              /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
              .error(R.drawable.image_blank) // Error image if loading fails
              .fit() // Resize the image to fit the ImageView dimensions
              .centerCrop() // Crop the image to fill the ImageView
              .into(holder.ivThreads); // ImageView to load the image into
      // Set data lainnya sesuai kebutuhan

      holder.tvShortDescription.setText(thread.getKronologisingkat());

     /* String inputDateString = thread.getDate().getCreatedDate();
      SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a 'UTC'Z", Locale.US);

      SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")); // Indonesian locale for month

      try {
         Date date = inputFormat.parse(inputDateString);
         String outputDateString = outputFormat.format(date);
         holder.tvDate.setText(outputDateString);
      } catch (ParseException e) {
         e.printStackTrace();
      }*/
   }

   @Override
   public int getItemCount() {
      return threadList.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView titleTextView,tvShortDescription,tvDate;
      ImageView ivThreads;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         titleTextView = itemView.findViewById(R.id.tvTitle);
         ivThreads = itemView.findViewById(R.id.ivThreads);
         tvShortDescription = itemView.findViewById(R.id.tvShortDescription);
         tvDate = itemView.findViewById(R.id.tvDate);

         // Inisialisasi elemen UI lainnya
      }
   }
}

package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ThreadModel;
import com.example.myapplication.model.User;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

   private List<ThreadModel> threadList;
   private List<User> userList;

   public interface OnItemClickListener {
      void onItemClick(ThreadModel thread, User user);
   }

   private OnItemClickListener clickListener;

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.clickListener = listener;
   }

   public ThreadAdapter(List<ThreadModel> threadList,List<User> userList) {
      this.threadList = threadList;
      this.userList = userList;
   }



   @NonNull
   @Override
   public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_threads, parent, false);
      return new ThreadViewHolder(view);
   }

   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
      ThreadModel thread = threadList.get(position);
      User user = userList.get(position);
      holder.titleTextView.setText(thread.getTitle());
      Picasso.get()
              .load(thread.getImg())  // Assuming getImg() returns the image URL
              /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
              .error(R.drawable.image_blank) // Error image if loading fails
              .fit() // Resize the image to fit the ImageView dimensions
              .centerCrop() // Crop the image to fill the ImageView
              .into(holder.ivThread); // ImageView to load the image into
      // Set data lainnya sesuai kebutuhan

      holder.tvChronology.setText(thread.getChronology());

      Date datePublish = thread.getDateValue("createdDate");

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

      holder.tvAuthor.setText("Oleh "+user.getName());

      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (clickListener != null) {
               clickListener.onItemClick(thread, user);
            }
         }
      });

   }

   @Override
   public int getItemCount() {
      return threadList.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView titleTextView,tvChronology,tvDate,tvAuthor;
      ImageView ivThread;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         titleTextView = itemView.findViewById(R.id.tvTitle);
         ivThread = itemView.findViewById(R.id.ivThread);
         tvChronology = itemView.findViewById(R.id.tvChronology);
         tvDate = itemView.findViewById(R.id.tvDate);
         tvAuthor = itemView.findViewById(R.id.tvAuthor);

         // Inisialisasi elemen UI lainnya
      }
   }
}

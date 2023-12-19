package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.model.ThreadModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotlin.collections.ArrayDeque;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

   private List<ThreadModel> threadList = new ArrayDeque<>();

   private String userId ;

   public interface OnItemClickListener {
      void onItemClick(ThreadModel thread);
   }

   private OnItemClickListener clickListener;

   public interface OnActionClickListener {
      void onActionClick(ThreadModel thread, int position);
   }

   private OnActionClickListener actionClickListener;

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.clickListener = listener;
   }

   public void setOnActionClickListener(OnActionClickListener listener) {
      this.actionClickListener = listener;
   }

   public ThreadAdapter(String userId ,List<ThreadModel> threadList) {
      this.threadList = threadList;
      this.userId = userId;
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
      holder.titleTextView.setText(thread.getTitle());


      if(!thread.getImg().isEmpty()){
         Picasso.get()
                 .load(thread.getImg())  // Assuming getImg() returns the image URL
                 /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
                 .error(R.drawable.image_blank) // Error image if loading fails
                 .fit() // Resize the image to fit the ImageView dimensions
                 .centerCrop() // Crop the image to fill the ImageView
                 .into(holder.ivThread); // ImageView to load the image into
      }

      // Set data lainnya sesuai kebutuhan

      holder.tvDescription.setText(thread.getDescription());


      Date datePublish = thread.getCreatedDate().toDate();

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

      holder.tvAuthor.setText("Oleh "+thread.getName());

      if(thread.getUserId().equals(userId)){
         holder.ivAction.setVisibility(View.VISIBLE);
      }else {
         holder.ivAction.setVisibility(View.GONE);
      }

      holder.ivAction.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (actionClickListener != null) {
               actionClickListener.onActionClick(thread,holder.getAdapterPosition());
            }
         }
      });

      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (clickListener != null) {
               clickListener.onItemClick(thread);
            }
         }
      });

   }

   public Date getDateValue(Map<String, Object> date,String key) {
      if (date != null && date.containsKey(key)) {
         Object value = date.get(key);
         if (value instanceof Date) {
            return (Date) value;
         } else if (value instanceof com.google.firebase.Timestamp) {
            return ((com.google.firebase.Timestamp) value).toDate();
         }
      }
      return null;
   }

   @Override
   public int getItemCount() {
      return threadList.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView titleTextView,tvDescription,tvDate,tvAuthor;
      ImageView ivThread,ivAction;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         titleTextView = itemView.findViewById(R.id.tvTitle);
         ivThread = itemView.findViewById(R.id.ivThread);
         tvDescription = itemView.findViewById(R.id.tvDescription);
         tvDate = itemView.findViewById(R.id.tvDate);
         tvAuthor = itemView.findViewById(R.id.tvAuthor);
         ivAction = itemView.findViewById(R.id.ivAction);

         // Inisialisasi elemen UI lainnya
      }
   }
}

package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ThreadViewHolder> {

   private List<ChatModel> chatModelList;
   private SharedPreferences sharedPreferences;
   private Context context;

   public ChatAdapter(Context context,List<ChatModel> reportModelList) {
      this.chatModelList = reportModelList;
      this.context = context;
   }

   @NonNull
   @Override
   public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
      return new ThreadViewHolder(view);
   }

   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
      ChatModel chatModel = chatModelList.get(position);
      sharedPreferences = context.getSharedPreferences("kdrt",Context.MODE_PRIVATE);
      String userId = sharedPreferences.getString("userId","");
      Timestamp time = chatModel.getTime();

      long seconds = time.getSeconds();
      long nanoseconds = time.getNanoseconds();

      // Calculate the milliseconds from seconds and nanoseconds
      long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);

      // Convert milliseconds to a Date object
      Date date = new Date(milliseconds);

      // Create a SimpleDateFormat for formatting with month names
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault());

      // Format the Date into a String
      String formattedDate = dateFormat.format(date);
      Log.d("asww",String.valueOf(time));

      if(chatModel.getSender().equals(userId)){
         holder.llSender.setVisibility(View.VISIBLE);
         holder.llReceiver.setVisibility(View.GONE);
         holder.tvChatSender.setText(chatModel.getContent());
         holder.tvTimeSender.setText(formattedDate);
      }else {
         holder.llSender.setVisibility(View.GONE);
         holder.llReceiver.setVisibility(View.VISIBLE);
         holder.tvChatReceiver.setText(chatModel.getContent());
         holder.tvTimeReceiver.setText(formattedDate);
      }

   }

   @Override
   public int getItemCount() {
      return chatModelList.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView tvChatReceiver,tvChatSender,tvTimeSender,tvTimeReceiver;
      LinearLayout llReceiver,llSender;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         tvChatReceiver = itemView.findViewById(R.id.tvChatReceiver);
         tvChatSender = itemView.findViewById(R.id.tvChatSender);
         tvTimeSender = itemView.findViewById(R.id.tvTimeSender);
         tvTimeReceiver = itemView.findViewById(R.id.tvTimeReciver);
         llReceiver = itemView.findViewById(R.id.llReceiver);
         llSender = itemView.findViewById(R.id.llSender);

         // Inisialisasi elemen UI lainnya
      }
   }
}

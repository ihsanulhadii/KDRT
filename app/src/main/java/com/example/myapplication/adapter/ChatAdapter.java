package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatModel;

import java.util.List;

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

      if(chatModel.getSender().equals(userId)){
         holder.llSender.setVisibility(View.VISIBLE);
         holder.llReceiver.setVisibility(View.GONE);
         holder.tvChatSender.setText(chatModel.getContent());
      }else {
         holder.llSender.setVisibility(View.GONE);
         holder.llReceiver.setVisibility(View.VISIBLE);
         holder.tvChatReceiver.setText(chatModel.getContent());
      }

   }

   @Override
   public int getItemCount() {
      return chatModelList.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView tvChatReceiver,tvChatSender;
      LinearLayout llReceiver,llSender;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         tvChatReceiver = itemView.findViewById(R.id.tvChatReceiver);
         tvChatSender = itemView.findViewById(R.id.tvChatSender);
         llReceiver = itemView.findViewById(R.id.llReceiver);
         llSender = itemView.findViewById(R.id.llSender);

         // Inisialisasi elemen UI lainnya
      }
   }
}

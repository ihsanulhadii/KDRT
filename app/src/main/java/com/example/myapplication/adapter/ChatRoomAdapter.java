package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Admin;
import com.example.myapplication.model.ChatRoomModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ThreadViewHolder> {

   private List<ChatRoomModel> chatRoomModels;

   private List<Admin> adminList = new ArrayList<>();

   public interface OnItemClickListener {
      void onItemClick(ChatRoomModel reportModel, Admin admin);
   }

   private OnItemClickListener clickListener;

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.clickListener = listener;
   }

   public ChatRoomAdapter(List<ChatRoomModel>chatRoomModelList,List<Admin> adminList) {
      this.chatRoomModels = chatRoomModelList;
      this.adminList = adminList;
   }

   @NonNull
   @Override
   public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
      return new ThreadViewHolder(view);
   }

   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
      if(chatRoomModels.size()==adminList.size()){
         ChatRoomModel chatRoomModel = chatRoomModels.get(position);
         Admin admin = adminList.get(position);

         holder.tvKonselor.setText(admin.getName());

         Picasso.get()
                 .load(admin.getImg())
                 .placeholder(R.drawable.avatar)
                 .error(R.drawable.avatar)
                 .fit()
                 .centerCrop()
                 .into(holder.ivAvatar);


         holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (clickListener != null) {
                  clickListener.onItemClick(chatRoomModel,admin);
               }
            }
         });
      }
   }

   @Override
   public int getItemCount() {
      return chatRoomModels.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView tvKonselor;
      CircleImageView ivAvatar;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         tvKonselor = itemView.findViewById(R.id.tvKonselor);
         ivAvatar = itemView.findViewById(R.id.ivAvatar);

         // Inisialisasi elemen UI lainnya
      }
   }
}

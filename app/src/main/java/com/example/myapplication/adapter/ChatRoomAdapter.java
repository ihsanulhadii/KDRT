package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Admin;
import com.example.myapplication.model.ChatRoomModel;
import com.example.myapplication.model.ReportModel;
import com.example.myapplication.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ThreadViewHolder> {

   private List<ChatRoomModel> chatRoomModels;
   private List<Admin> userList = new ArrayList<>();

   public interface OnItemClickListener {
      void onItemClick(ChatRoomModel reportModel,Admin admin);
   }

   private OnItemClickListener clickListener;

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.clickListener = listener;
   }

   public ChatRoomAdapter(List<ChatRoomModel>chatRoomModelList, List<Admin> userList) {
      this.userList = userList;
      this.chatRoomModels = chatRoomModelList;
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
      ChatRoomModel chatRoomModel = chatRoomModels.get(position);
      if(userList.size()==0){
         return;
      }
      Admin admin = userList.get(position);
      holder.tvKonselor.setText(admin.getName());

      Picasso.get()
              .load(admin.getImg())  // Assuming getImg() returns the image URL
              /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
              .error(R.drawable.image_blank) // Error image if loading fails
              .fit() // Resize the image to fit the ImageView dimensions
              .centerCrop() // Crop the image to fill the ImageView
              .into(holder.ivAvatar); // ImageView to load the image into


      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (clickListener != null) {
               clickListener.onItemClick(chatRoomModel,admin);
            }
         }
      });

   }

   @Override
   public int getItemCount() {
      return chatRoomModels.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView tvKonselor;
      ImageView ivAvatar;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         tvKonselor = itemView.findViewById(R.id.tvKonselor);
         ivAvatar = itemView.findViewById(R.id.ivAvatar);

         // Inisialisasi elemen UI lainnya
      }
   }
}

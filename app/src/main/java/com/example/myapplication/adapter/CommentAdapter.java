package com.example.myapplication.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.CommentModel;
import com.google.firebase.firestore.CollectionReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

   private Context context;
   private List<CommentModel> commentList;

   private RecyclerView recyclerView;

   public CommentAdapter(Context context, List<CommentModel> commentList) {
      this.context = context;
      this.commentList = commentList;
   }

   @NonNull
   @Override
   public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
      return new CommentViewHolder(view);

   }


   @Override
   public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
      CommentModel comment = commentList.get(position);
      holder.tvComment.setText(comment.getContent());
      holder.tvName.setText(comment.getName());
      Log.d("xxx22",comment.getName()+" --");

      Picasso.get()
              .load(comment.getAvatar())
              .placeholder(R.drawable.avatar)
              .error(R.drawable.avatar)
              .fit()
              .centerCrop()
              .into(holder.ivImage);
/*
      holder.txtContent.setText(comment.getContent());*/

      // Format timestamp to display in a readable way
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", new Locale("id", "ID")); // Indonesian locale
      String formattedTime = dateFormat.format(comment.getTime().toDate());
      holder.tvDate.setText(formattedTime);


   }

   @Override
   public int getItemCount() {
      return commentList.size();
   }

   public class CommentViewHolder extends RecyclerView.ViewHolder {

      ImageView ivImage;
      TextView tvComment, tvDate,tvName;

      public CommentViewHolder(@NonNull View itemView) {
         super(itemView);
         ivImage =  itemView.findViewById(R.id.ivImage);
         tvComment = itemView.findViewById(R.id.tvComment);
         tvDate = itemView.findViewById(R.id.tvDate);
         tvName = itemView.findViewById(R.id.tvName);


      }
   }

}

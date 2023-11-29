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
import com.example.myapplication.model.CommentModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

   private Context context;
   private List<CommentModel> commentList;

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
/*
      holder.txtContent.setText(comment.getContent());*/

      // Format timestamp to display in a readable way
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
      String formattedTime = dateFormat.format(comment.getTime().toDate());
      holder.tvDate.setText(formattedTime);
   }

   @Override
   public int getItemCount() {
      return commentList.size();
   }

   public class CommentViewHolder extends RecyclerView.ViewHolder {

   ImageView ivimage;
   TextView tvComment, tvDate;

      public CommentViewHolder(@NonNull View itemView) {
         super(itemView);
         ivimage.findViewById(R.id.ivImage);
         tvComment.findViewById(R.id.tvComment);
         tvDate.findViewById(R.id.tvDate);


      }
   }
}

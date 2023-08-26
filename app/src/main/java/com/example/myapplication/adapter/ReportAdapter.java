package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ReportModel;
import com.example.myapplication.model.ThreadModel;
import com.example.myapplication.model.User;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

   private List<ReportModel> reportList;
   private List<User> userList;

   public ReportAdapter(List<ReportModel> reportList,List<User> userList) {
      this.reportList = reportList;
      this.userList = userList;
   }
   @NonNull
   @Override
   public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
      return new ReportViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
      ReportModel report = reportList.get(position);
      User user = userList.get(position);
      holder.titleTextView.setText(report.getTitle());
      Picasso.get()
              .load(report.getImg())  // Assuming getImg() returns the image URL
              /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
              .error(R.drawable.image_blank) // Error image if loading fails
              .fit() // Resize the image to fit the ImageView dimensions
              .centerCrop() // Crop the image to fill the ImageView
              .into(holder.ivReport); // ImageView to load the image into
      // Set data lainnya sesuai kebutuhan

      holder.tvDescription.setText(report.getKeseluruhan());

      Date datePublish = report.getDateValue("createdDate");

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
   }

   @Override
   public int getItemCount() {
      return reportList.size();
   }

   public class ReportViewHolder extends RecyclerView.ViewHolder {
      TextView titleTextView,tvDescription,tvDate,tvAuthor;
      ImageView ivReport;

      public ReportViewHolder(@NonNull View itemView) {
         super(itemView);
         titleTextView = itemView.findViewById(R.id.tvTitle);
         ivReport = itemView.findViewById(R.id.ivReport);
         tvDescription = itemView.findViewById(R.id.tvDescription);
         tvDate = itemView.findViewById(R.id.tvDate);
         tvAuthor = itemView.findViewById(R.id.tvAuthor);

         // Inisialisasi elemen UI lainnya
      }
   }
}

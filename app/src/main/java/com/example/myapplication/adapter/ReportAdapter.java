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
import com.example.myapplication.model.ReportModel;
import com.example.myapplication.model.ThreadModel;
import com.example.myapplication.model.User;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ThreadViewHolder> {

   private List<ReportModel> reportModelList;

   public interface OnItemClickListener {
      void onItemClick(ReportModel reportModel);
   }

   private OnItemClickListener clickListener;

   public void setOnItemClickListener(OnItemClickListener listener) {
      this.clickListener = listener;
   }

   public ReportAdapter(List<ReportModel> reportModelList) {
      this.reportModelList = reportModelList;
   }

   @NonNull
   @Override
   public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
      return new ThreadViewHolder(view);
   }

   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
      ReportModel reportModel = reportModelList.get(position);
      holder.titleTextView.setText(reportModel.getTitle());
      Picasso.get()
              .load(reportModel.getImg())  // Assuming getImg() returns the image URL
              /*.placeholder(R.drawable.placeholder_image) // Placeholder image while loading*/
              .error(R.drawable.image_blank) // Error image if loading fails
              .fit() // Resize the image to fit the ImageView dimensions
              .centerCrop() // Crop the image to fill the ImageView
              .into(holder.ivReport); // ImageView to load the image into
      // Set data lainnya sesuai kebutuhan

      holder.tvDescription.setText(reportModel.getDescription());
      holder.tvAddressChronology.setText(reportModel.getAddressChronology());

      Date datePublish = reportModel.getDateValue("createdDate");

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

      holder.tvStatus.setText("Status "+reportModel.getStatus());

      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (clickListener != null) {
               clickListener.onItemClick(reportModel);
            }
         }
      });

   }

   @Override
   public int getItemCount() {
      return reportModelList.size();
   }

   public class ThreadViewHolder extends RecyclerView.ViewHolder {
      TextView titleTextView,tvDescription,tvDate,tvStatus, tvAddressChronology;
      ImageView ivReport;

      public ThreadViewHolder(@NonNull View itemView) {
         super(itemView);
         titleTextView = itemView.findViewById(R.id.tvTitle);
         ivReport = itemView.findViewById(R.id.ivReport);
         tvAddressChronology = itemView.findViewById(R.id.tvAddressChronology);
         tvDescription = itemView.findViewById(R.id.tvDescription);
         tvDate = itemView.findViewById(R.id.tvDate);
         tvStatus = itemView.findViewById(R.id.tvStatus);

         // Inisialisasi elemen UI lainnya
      }
   }
}

package com.example.myapplication.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.VideoModel;
import com.example.myapplication.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context context;
    private List<VideoModel> videoList;

    public VideoAdapter(Context context, List<VideoModel> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoModel video = videoList.get(position);

        // Set data to views
        holder.tvTitle.setText(video.getTitle());

        // Load and display the video thumbnail (assuming embed is a URL)
        Uri videoUri = Uri.parse(video.getEmbed());
        holder.vvVideo.setVideoURI(videoUri);
        holder.vvVideo.seekTo(100); // To show the video thumbnail
        holder.vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true); // Optional: set video to loop
            }
        });

        // Set other data or handle interactions if needed
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        VideoView vvVideo;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            tvTitle = itemView.findViewById(R.id.tvTitle);
            vvVideo = itemView.findViewById(R.id.vvVideo);
            cardView = itemView.findViewById(R.id.cardView);

            // Set any click listeners or other view interactions if needed
        }
    }
}
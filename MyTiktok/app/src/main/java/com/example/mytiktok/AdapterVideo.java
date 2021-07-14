package com.example.mytiktok;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderVideo> {

    private Context context;
    private ArrayList<videomodel> arrayList;

    public AdapterVideo(Context context, ArrayList<videomodel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custome_displayvid,parent,false);
        return new HolderVideo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderVideo holder, int position) {
        videomodel vid = arrayList.get(position);

        String id = vid.getId();
        String timeslap = vid.getTimeslap();
        String title = vid.getTitle();
        String uri = vid.getVideoUri();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeslap));
        String formatdate = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();

        holder.title.setText(title);
        holder.timeslap.setText(formatdate);


        setVideoUrl(vid,holder);

    }
    public void setVideoUrl(videomodel vid, final HolderVideo holderVideo){
        holderVideo.progressBar.setVisibility(View.VISIBLE);

        String url = vid.getVideoUri();

        final MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holderVideo.videoView);

        Uri videoUrl = Uri.parse(url);
        holderVideo.videoView.setMediaController(mediaController);
        holderVideo.videoView.setVideoURI(videoUrl);

        holderVideo.videoView.requestFocus();
        holderVideo.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        holderVideo.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                        holderVideo.progressBar.setVisibility(View.GONE);
                        return true;}
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        holderVideo.progressBar.setVisibility(View.VISIBLE);
                        return true;}
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        holderVideo.progressBar.setVisibility(View.GONE);
                        return true;}

                }
                return false;
            }

        });
        holderVideo.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class HolderVideo extends RecyclerView.ViewHolder{
        TextView title,timeslap;
        VideoView videoView;
        ProgressBar progressBar;
        public HolderVideo(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progrssbar);
            title = itemView.findViewById(R.id.titlevid);
            timeslap = itemView.findViewById(R.id.timeslap);
            videoView = itemView.findViewById(R.id.videoiteam);
        }
    }
}

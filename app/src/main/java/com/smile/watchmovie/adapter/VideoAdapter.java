package com.smile.watchmovie.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.ChannelActivity;
import com.smile.watchmovie.activity.VideoActivity;
import com.smile.watchmovie.model.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Activity mActivity;
    private List<Video> mListVideo;
    private String mActive,mType,mKeySearch;
    private int idUser;
    public VideoAdapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void  setData(List<Video> list){
        this.mListVideo=list;
        notifyDataSetChanged();
    }

    public void setIdUser(int idUser){
        this.idUser=idUser;
    }

    public List<Video> getmListVideo() {
        return mListVideo;
    }

    public String getmKeySearch() {
        return mKeySearch;
    }

    public void setmKeySearch(String mKeySearch) {
        this.mKeySearch = mKeySearch;
    }

    public void setAddListVideo(List<Video>list){
        this.mListVideo.addAll(list);
        notifyDataSetChanged();
    }
    public void setType(String type){
        this.mType=type;
    }
    public void setActivity(String Active){
        this.mActive=Active;
    }
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video=mListVideo.get(position);
        if(video==null){
            return;
        }

        String second=(Integer.parseInt(video.getVideoTime())%60==0)?"00":(Integer.parseInt(video.getVideoTime())%60)<10?"0"+Integer.parseInt(video.getVideoTime())%60:Integer.parseInt(video.getVideoTime())%60+"";
        String minute=(Integer.parseInt(video.getVideoTime())/60<10)?"0"+Integer.parseInt(video.getVideoTime())/60:Integer.parseInt(video.getVideoTime())/60+"";
        Glide.with(mActivity).load(video.getVideoImage())
                .error(R.drawable.ic_baseline_broken_image_gray)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.imgVideo);
        String totalView=formatToTal(video.getTotalViews());
        holder.txvNameChannel.setText(video.getChannel().getChannelName()+" - "+totalView+" Lượt xem");
        holder.txvTiTle.setText(video.getVideoTitle());
        Glide.with(mActivity).load(video.getChannel().getChannelAvatar())
                .error(R.drawable.ic_baseline_broken_image_gray)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.imgChannel);
        holder.txvTime.setText(minute+":"+second);

        holder.imgChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity, ChannelActivity.class);
                intent.putExtra("id_channel",video.getChannelId());
                mActivity.startActivity(intent);
            }
        });

        holder.layout_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity, VideoActivity.class);
                intent.putExtra("id_video",video.getId());
                intent.putExtra("activity",mActive);
                intent.putExtra("id_user",idUser);
                if(mActive.equals("search")) intent.putExtra("key",mKeySearch);
                intent.putExtra("type",mType);
                mActivity.startActivity(intent);
            }
        });

    }

    private String formatToTal(int data){
        return (data>=1000000)?((data/1000000)+"M"):((data<1000)? data+"":(data/1000)+"N");
    }

    @Override
    public int getItemCount() {
        if(mListVideo!=null) return mListVideo.size();
        return 0;
    }
    public class  VideoViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgChannel,imgVideo;
        private LinearLayout layout_video;
        private TextView txvTiTle,txvNameChannel,txvTime;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            txvTime=itemView.findViewById(R.id.tv_time);
            layout_video=itemView.findViewById(R.id.ln_video);
            imgVideo=itemView.findViewById(R.id.imgv_video);
            imgChannel=itemView.findViewById(R.id.imgv_avatar);
            txvTiTle=itemView.findViewById(R.id.tv_title);
            txvNameChannel=itemView.findViewById(R.id.tv_name_of_channel);

        }
    }


}

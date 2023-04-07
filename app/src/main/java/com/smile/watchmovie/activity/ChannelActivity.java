package com.smile.watchmovie.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.ViewPagerChannelAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.api.ApiServiceVideo;
import com.smile.watchmovie.database.Database;
import com.smile.watchmovie.databinding.ActivityChannelBinding;
import com.smile.watchmovie.model.Channel;
import com.smile.watchmovie.model.ChannelDetail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelActivity extends AppCompatActivity {

    ActivityChannelBinding binding;
    Database database;

    private int mCurrentPage=0;
    private boolean isFollow=false,isClick=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=new Database(ChannelActivity.this,"youmedia.sqlite",null,1);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewPagerChannelAdapter viewChannelAdapter=new ViewPagerChannelAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        int IdChannel=getIntent().getIntExtra("id_channel",0);
        clickCallApiChannel(IdChannel);
        viewChannelAdapter.setIdChannel(IdChannel);
        binding.viewPager.setAdapter(viewChannelAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        //isFollow=database.checkChannelFollowed(IdChannel);
        if(isFollow){
            binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border_v2));
            binding.btnFollow.setText("Hủy theo dõi");
            binding.btnFollow.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }else{
            binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border));
            binding.btnFollow.setText("Theo dõi");
            binding.btnFollow.setTextColor(ContextCompat.getColor(this, R.color.color_key));
        }

        binding.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFollow){
                    isFollow=true;
                    binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border_v2));
                    binding.btnFollow.setText("Hủy theo dõi");
                    binding.btnFollow.setTextColor(ContextCompat.getColor(ChannelActivity.this, R.color.gray));
                    isClick=true;
                }else{
                    isFollow=false;
                    binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border));
                    binding.btnFollow.setText("Theo dõi");
                    binding.btnFollow.setTextColor(ContextCompat.getColor(ChannelActivity.this, R.color.color_key));
                    isClick=false;
                }
            }
        });

    }

    private void clickCallApiChannel(int idChannel){
        //http://videoapi.kakoak.tls.tl/video-service/v1/channel/328/info?msisdn=%2B67075615473&timestamp=1611796455960&security=&clientType=Android&revision=15511
        ApiServiceVideo.apiServiceVideo.getChannelById(idChannel,"text","text","%2B67075615473","1611796455960","", "Android","15511").enqueue(new Callback<ChannelDetail>() {
            @Override
            public void onResponse(Call<ChannelDetail> call, Response<ChannelDetail> response) {
                ChannelDetail channelDetail =response.body();
                if(channelDetail!=null) {
                    Channel channel=channelDetail.getData();
                    binding.txtNameChannel.setText(channel.getChannelName());
                    Glide.with(ChannelActivity.this).load(channel.getChannelAvatar())
                            .error(R.drawable.ic_baseline_broken_image_gray)
                            .placeholder(R.drawable.ic_baseline_image_gray)
                            .into(binding.imvAvatarChannel);
                    String totalFollowChannel=(channel.getNumFollows()>=1000000)?((channel.getNumFollows()/1000000)+"M"):((channel.getNumFollows()<1000)? channel.getNumFollows()+"":(channel.getNumFollows()/1000)+"N");
                    String totalVideoChannel=(channel.getNumVideos()>=1000000)?((channel.getNumVideos()/1000000)+"M"):((channel.getNumVideos()<1000)? channel.getNumVideos()+"":(channel.getNumVideos()/1000)+"N");
                    binding.txtInfo.setText(totalFollowChannel+" Người theo dõi - "+totalVideoChannel+" Lượt xem");
                    TextView txtDes=findViewById(R.id.txt_body_describe_in_channeldetail);
                    txtDes.setText(channel.getDescription());
                    Glide.with(ChannelActivity.this).load(channel.getHeaderBanner()).into(binding.imgBanner);
                    Glide.with(ChannelActivity.this).load(channel.getHeaderBanner())
                            .error(R.drawable.ic_baseline_broken_image_gray)
                            .placeholder(R.drawable.ic_baseline_image_gray)
                            .into(binding.imgBanner);
                    if(isFollow && isClick){
                        database.FOLLOW(channel);
                    }
                }

            }

            @Override
            public void onFailure(Call<ChannelDetail> call, Throwable t) {
                Toast.makeText(ChannelActivity.this, "Link API Error", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
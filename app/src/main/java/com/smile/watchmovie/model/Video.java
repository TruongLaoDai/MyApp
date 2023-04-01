package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Video {

    @SerializedName("id")
    @Expose
    private int id;
    //id
    @SerializedName("cateId")
    @Expose
    private int cateId;
    @SerializedName("channelId")
    @Expose
    //idC channel
    private int channelId;
    @SerializedName("videoTitle")
    @Expose
    //tiêu đề video
    private String videoTitle;
    @SerializedName("videoDesc")
    @Expose
    //miêu tả video
    private String videoDesc;
    @SerializedName("videoMedia")
    @Expose
    //link video
    private String videoMedia;
    @SerializedName("videoImage")
    @Expose
    //link ảnh của video
    private String videoImage;
    @SerializedName("imageThumb")
    @Expose
    //thumb của ảnh
    private String imageThumb;
    @SerializedName("imageSmall")
    @Expose
    //link ảnh
    private String imageSmall;
    @SerializedName("videoTime")
    @Expose
    // thời gian của video
    private String videoTime;
    @SerializedName("publishTime")
    @Expose
    //thời gian xuất bản
    private Long publishTime;
    @SerializedName("totalViews")
    @Expose
    //tổng số lượt xem
    private int totalViews;
    @SerializedName("totalLikes")
    @Expose
    //tổng số lượt like
    private int totalLikes;
    @SerializedName("totalShares")
    @Expose
    //tổng lượt chia sẻ
    private int totalShares;
    @SerializedName("totalComments")
    @Expose
    //tổng bình luận
    private int totalComments;
    @SerializedName("resolution")
    @Expose
    private int resolution;
    @SerializedName("aspecRatio")
    @Expose
    //tỉ lệ khung hình
    private String aspecRatio;
    @SerializedName("isAdaptive")
    @Expose
    private int isAdaptive;
    @SerializedName("isLike")
    @Expose
    //trạng thái like
    private int isLike;
    @SerializedName("status")
    @Expose
    //trạng thái
    private int status;
    @SerializedName("cate")
    @Expose
    private List<Cate> cate;
    @SerializedName("channel")
    @Expose
    private Channel channel;
    @SerializedName("url")
    @Expose
    // link video
    private String url;
    @SerializedName("hashId")
    @Expose
    private String hashId;
    @SerializedName("list_resolution")
    @Expose
    private List<Resolution> listResolution;

    public Video() {
    }

    public Video(int id, int cateId, int channelId, String videoTitle, String videoDesc, String videoMedia, String videoImage, String imageThumb, String imageSmall, String videoTime, Long publishTime, int totalViews, int totalLikes, int totalShares, int totalComments, int resolution, String aspecRatio, int isAdaptive, int isLike, int status, List<Cate> cate, Channel channel, String url, String hashId, List<Resolution> listResolution) {
        this.id = id;
        this.cateId = cateId;
        this.channelId = channelId;
        this.videoTitle = videoTitle;
        this.videoDesc = videoDesc;
        this.videoMedia = videoMedia;
        this.videoImage = videoImage;
        this.imageThumb = imageThumb;
        this.imageSmall = imageSmall;
        this.videoTime = videoTime;
        this.publishTime = publishTime;
        this.totalViews = totalViews;
        this.totalLikes = totalLikes;
        this.totalShares = totalShares;
        this.totalComments = totalComments;
        this.resolution = resolution;
        this.aspecRatio = aspecRatio;
        this.isAdaptive = isAdaptive;
        this.isLike = isLike;
        this.status = status;
        this.cate = cate;
        this.channel = channel;
        this.url = url;
        this.hashId = hashId;
        this.listResolution = listResolution;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    public String getVideoMedia() {
        return videoMedia;
    }

    public void setVideoMedia(String videoMedia) {
        this.videoMedia = videoMedia;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }



    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public String getAspecRatio() {
        return aspecRatio;
    }

    public void setAspecRatio(String aspecRatio) {
        this.aspecRatio = aspecRatio;
    }

    public int getIsAdaptive() {
        return isAdaptive;
    }

    public void setIsAdaptive(int isAdaptive) {
        this.isAdaptive = isAdaptive;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Cate> getCate() {
        return cate;
    }

    public void setCate(List<Cate> cate) {
        this.cate = cate;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public  List<Resolution> getListResolution() {
        return listResolution;
    }

    public void setListResolution( List<Resolution> listResolution) {
        this.listResolution = listResolution;
    }
}

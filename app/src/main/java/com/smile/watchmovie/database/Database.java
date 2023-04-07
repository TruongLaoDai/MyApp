package com.smile.watchmovie.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.smile.watchmovie.model.Channel;
import com.smile.watchmovie.model.User;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public void INSERT_USER(User user){

        SQLiteDatabase database=getWritableDatabase();
        String sql="INSERT INTO User VALUES(null,?,?,?,?)";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindBlob(1,user.getAvatar());
        statement.bindString(2,user.getName());
        statement.bindString(3,user.getUsername());
        statement.bindString(4,user.getPassword());
        statement.executeInsert();

    }

    public void FOLLOW(Channel channel){

        SQLiteDatabase database=getWritableDatabase();
        String sql="INSERT INTO ChannelFollowed VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1,channel.getId());
        statement.bindString(2,(channel.getChannelName()!=null)?channel.getChannelName():"");
        statement.bindString(3,(channel.getChannelAvatar()!=null)?channel.getChannelAvatar():"");
        statement.bindString(4,(channel.getHeaderBanner()!=null)?channel.getHeaderBanner():"");
        statement.bindString(5,(channel.getDescription()!=null)?channel.getDescription():"");
        statement.bindLong(6,channel.getNumFollows());
        statement.bindLong(7,channel.getNumVideos());
        statement.bindLong(8,channel.getOfficial());
        statement.bindString(9,(channel.getCreatedFrom()!=null)?channel.getCreatedFrom():"");
        statement.bindLong(10,channel.getFollow());
        statement.bindLong(11,channel.getOwner());
        statement.bindString(12,(channel.getUrl()!=null)?channel.getUrl():"");
        statement.bindString(13,(channel.getState()!=null)?channel.getState():"");
        statement.executeInsert();
    }


    public void FOLLOWTEST(int idChannel){
        SQLiteDatabase database=getWritableDatabase();
        String sql="INSERT INTO ChannelFollowed VALUES(null,?)";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1,idChannel);
        statement.executeInsert();
    }

    public void LIKE(int idUser,int idVideo){

        SQLiteDatabase database=getWritableDatabase();
        String sql="INSERT INTO VideoLiked VALUES(null,?,?)";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1,idUser);
        statement.bindLong(2,idVideo);
        statement.executeInsert();

    }

    public boolean ISLIKE(int idUser,int idVideo){
        String sql="SELECT * FROM VideoLiked WHERE Id_user="+idUser+" AND Id_video="+idVideo;
        Cursor data=GetData(sql);
        while (data.moveToNext()) {
            return true;
        }
        return false;
    }

    public User checkAccount(String username){
        String sql="SELECT * FROM User WHERE username='"+username+"'";
        Cursor data=GetData(sql);
        User user = null;
        while (data.moveToNext()) {
            user=new User(data.getInt(0),data.getBlob(1),data.getString(2),data.getString(3),data.getString(4));
        }
        return user;
    }

    public List<Channel> getAllChannelFollowed(){
        String sql="SELECT * FROM ChannelFollowed";
        Cursor data=GetData(sql);
        List<Channel> listChannel=new ArrayList<>();
        while (data.moveToNext()) {
            Channel channel = new Channel(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getInt(5),
                    data.getInt(6),data.getInt(7),data.getString(8),data.getInt(9),data.getInt(10),data.getString(11),data.getString(12));
            listChannel.add(channel);
        }
        return listChannel;
    }

    public Boolean checkChannelFollowed(int idChannel){
        String sql="SELECT * FROM ChannelFollowed WHERE Id_channel="+idChannel;
        Cursor data=GetData(sql);
        while (data.moveToNext()) {
            return true;
        }
        return false;
    }

    public User getUser(int id){
        String sql="SELECT * FROM User WHERE Id_user="+id;
        Cursor data=GetData(sql);
        User user = null;
        while (data.moveToNext()) {
            user=new User(data.getInt(0),data.getBlob(1),data.getString(2),data.getString(3),data.getString(4));
        }
        return user;
    }

    public void UNFOLLOW(int idChannel){
        SQLiteDatabase database=getWritableDatabase();
        String sql="DELETE FROM ChannelFollowed WHERE Id_channel="+idChannel;
        SQLiteStatement statement=database.compileStatement(sql);
        statement.executeInsert();
    }

    public void UPDATE_USER(User user){
        SQLiteDatabase database=getWritableDatabase();
        String sql="UPDATE User SET avatar=?,full_name=?,username=?,password=? WHERE Id_user=?";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindBlob(1,user.getAvatar());
        statement.bindString(2,user.getName());
        statement.bindString(3,user.getUsername());
        statement.bindString(4,user.getPassword());
        statement.bindLong(5,user.getiD());
        statement.executeInsert();

    }

    public void QueryData(String sql){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL(sql);
    }

    public Cursor GetData(String sql){
        SQLiteDatabase database=getReadableDatabase();
        return database.rawQuery(sql,null);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

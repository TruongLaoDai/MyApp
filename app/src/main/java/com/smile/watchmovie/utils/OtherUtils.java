package com.smile.watchmovie.utils;

public class OtherUtils {
    public void getDuration(String duration){
        String sl[] = duration.split("<br>");
        String dr = "";
        for(int i = 0; i < sl[sl.length - 1].length(); i++){
            char a = sl[sl.length - 1].charAt(i);
            if('9' >= a && a >= '0') {

            }
        }
    }
}

package com.smile.watchmovie.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public String formatCommonTime(String dateUpVip, String type_vip) {
        String[] date = dateUpVip.split(" ");
        String data[] = date[0].split("-");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate today = LocalDate.now();
            LocalDate dateUpVIp = LocalDate.of(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            long daysBetween = ChronoUnit.DAYS.between(today, dateUpVIp);
            if (type_vip.equals("1")) {
                if(daysBetween > 30) {
                    return "Hết hạn";
                } else {
                    return "Hoạt động";
                }
            } else {
                if(daysBetween > 365) {
                    return "Hết hạn";
                } else {
                    return "Hoạt động";
                }
            }
        }
        return null;
    }
}

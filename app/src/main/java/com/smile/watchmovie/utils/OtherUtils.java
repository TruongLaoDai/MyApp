package com.smile.watchmovie.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class OtherUtils {
    public String formatCommonTime(String dateUpVip, String type_vip) {
        String[] data = dateUpVip.split("-");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate today = LocalDate.now(); // năm - tháng - ngày
            LocalDate dateUpVIp = LocalDate.of(Integer.parseInt(data[2]), Integer.parseInt(data[1]), Integer.parseInt(data[0]));
            long daysBetween = ChronoUnit.DAYS.between(dateUpVIp, today);

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

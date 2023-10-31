package com.smile.watchmovie.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public class OtherUtils {
    public String formatCommonTime(String dateUpVip, String type_vip) {
        String[] data = dateUpVip.split("-");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate today = LocalDate.now(); // năm - tháng - ngày
            LocalDate dateUpVIp = LocalDate.of(Integer.parseInt(data[2]), Integer.parseInt(data[1]), Integer.parseInt(data[0]));
            long daysBetween = ChronoUnit.DAYS.between(dateUpVIp, today);

            if (type_vip.equals("1")) {
                if (daysBetween > 30) {
                    return "Hết hạn";
                } else {
                    return "Hoạt động";
                }
            } else {
                if (daysBetween > 365) {
                    return "Hết hạn";
                } else {
                    return "Hoạt động";
                }
            }
        }
        return null;
    }

    public String formatTime(String date) {
        String[] data = date.split("T");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date1 = format.parse(data[0]);
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return format1.format(date1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

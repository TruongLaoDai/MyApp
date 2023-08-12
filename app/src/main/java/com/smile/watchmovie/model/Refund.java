package com.smile.watchmovie.model;

import com.smile.watchmovie.utils.AppInfo;
import com.smile.watchmovie.zalo.Helper.Helpers;
import com.smile.watchmovie.zalo.HttpProvider;

import org.json.JSONObject; // https://mvnrepository.com/artifact/org.json/json

import okhttp3.FormBody;
import okhttp3.RequestBody;
import java.text.SimpleDateFormat;
import java.util.*;

public class Refund {
    private static class RefundWhenUpdateVipError {
        String m_refund_id;
        String app_id;
        String zp_trans_id;
        String amount;
        String time;
        String Description;
        String Mac;

        private RefundWhenUpdateVipError(String amount, String zp_trans_id) throws Exception {
            app_id = String.valueOf(AppInfo.APP_ID);
            Random rand = new Random();
            long timestamp = System.currentTimeMillis(); // miliseconds
            String uid = timestamp + "" + (111 + rand.nextInt(888)); // unique id
            time = getCurrentTimeString(timestamp+"");
            m_refund_id = getCurrentTimeString("yyMMdd") +"_"+ app_id +"_"+uid;
            this.amount = amount;
            this.zp_trans_id = zp_trans_id;
            Description = "Refund for update vip error";
            String inputHMac = String.format("%s|%s|%s|%s|%s",
                    this.app_id,
                    this.zp_trans_id,
                    this.amount,
                    this.Description,
                    this.time);

            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac);
        }
    }

    private static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public JSONObject refundWhenUpdateVipError(String amount, String zp_trans_id) throws Exception {
        Refund.RefundWhenUpdateVipError input = new Refund.RefundWhenUpdateVipError(amount, zp_trans_id);

        RequestBody formBody = new FormBody.Builder()
                .add("m_refund_id", input.m_refund_id)
                .add("app_id", input.app_id)
                .add("zp_trans_id", input.zp_trans_id)
                .add("amount", input.amount)
                .add("timestamp", input.time)
                .add("mac", input.Mac)
                .add("description", input.Description)
                .build();

        return HttpProvider.sendPost(AppInfo.URL_REFUND, formBody);
    }
}

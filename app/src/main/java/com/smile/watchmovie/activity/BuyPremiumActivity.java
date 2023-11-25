package com.smile.watchmovie.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityChoosePaymentBinding;
import com.smile.watchmovie.eventbus.EventNotifyBuyVipSuccess;
import com.smile.watchmovie.model.CreateOrder;
import com.smile.watchmovie.model.HistoryUpVip;
import com.smile.watchmovie.model.Refund;
import com.smile.watchmovie.utils.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class BuyPremiumActivity extends AppCompatActivity {
    private ActivityChoosePaymentBinding binding;
    private String token;
    private String idUser, nameUser, is_vip;
    private String payId;
    private String price, type_vip;
    private SharedPreferences.Editor editor;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChoosePaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeData();
        showData();
        setUpFireBase();
        handleEventClick();
    }

    private void showData() {
        /* Hiển thị thông tin người dùng */
        binding.tvNameAccount.setText(nameUser);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            Glide.with(this).load(signInAccount.getPhotoUrl()).into(binding.ivAccount);
        }
    }

    private void initializeData() {
        price = "47000";
        type_vip = "1";

        /* Khởi tạo ZaloPay */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.NAME_DATABASE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /* Lấy thông tin user trong DB */
        idUser = sharedPreferences.getString(Constant.ID_USER, "");
        nameUser = sharedPreferences.getString(Constant.NAME_USER, "");
        is_vip = sharedPreferences.getString(Constant.IS_VIP, "");
    }

    private void handleEventClick() {
        binding.toolBar.setNavigationOnClickListener(view -> finish());

        binding.ivHistoryPay.setOnClickListener(v -> startActivity(new Intent(BuyPremiumActivity.this, HistoryBuyPremiumActivity.class)));

        /* Mua nhấn mua theo tháng */
        binding.loutBuyMonthly.setOnClickListener(v -> {
            price = "47000";
            type_vip = "1";
            binding.loutBuyMonthly.setBackground(getDrawable(R.drawable.bg_premium));
            binding.loutBuyYearly.setBackground(getDrawable(R.drawable.bg_premium_unselected));
            binding.tvDesTypePremium.setText(getString(R.string.des_type_premium_month));
        });

        /* Mua nhấn theo năm */
        binding.loutBuyYearly.setOnClickListener(v -> {
            price = "439000";
            type_vip = "2";
            binding.loutBuyYearly.setBackground(getDrawable(R.drawable.bg_premium));
            binding.loutBuyMonthly.setBackground(getDrawable(R.drawable.bg_premium_unselected));
            binding.tvDesTypePremium.setText(getString(R.string.des_type_premium_year));
        });

        /* Nhấn mua */
        binding.btnBuy.setOnClickListener(v -> {
            if ((is_vip.equals("1") && type_vip.equals("1")) || (is_vip.equals("2") && type_vip.equals("2"))) {
                new AlertDialog.Builder(this)
                        .setTitle("Bạn đã mua gói")
                        .setMessage("Bạn đã mua gói trước đây. Hãy đợi đến khi gói hết hạn để mua tiếp")
                        .setPositiveButton("OK", (dialog, which) -> {
                        }).show();
            } else if (is_vip.equals("1") || is_vip.equals("2")) {
                new AlertDialog.Builder(this)
                        .setTitle("Bạn có chắc chắn mua?")
                        .setMessage("Bạn đã là thành viên vip. Nếu bạn mua tiếp, gói trước sẽ bị mất hiệu lực!")
                        .setPositiveButton("OK", (dialog, which) -> payForUpVip()).setNegativeButton("Hủy", null).show();
            } else {
                payForUpVip();
            }
        });
    }

    private void setUpFireBase() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(Constant.FirebaseFiretore.NAME_DATABASE);
    }

    private void payForUpVip() {
        /* Tạo đơn hàng */
        createOrder(price);

        ZaloPaySDK.getInstance().payOrder(BuyPremiumActivity.this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                runOnUiThread(() -> {
                    payId = transactionId;
                    callApiUpdateUserToVip(idUser, type_vip);
                });
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                new AlertDialog.Builder(BuyPremiumActivity.this)
                        .setTitle("Bạn đã hủy thanh toán")
                        .setMessage("Hãy thực hiện thanh toán để xem nhiều phim hơn!")
                        .setPositiveButton("OK", null).show();
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                new AlertDialog.Builder(BuyPremiumActivity.this)
                        .setTitle("Thanh toán lỗi")
                        .setMessage("Có một vài vấn đề trong khi thanh toán. Bạn hãy thử lại sau nhé!")
                        .setPositiveButton("OK", null).show();
            }
        });
    }

    private void callApiUpdateUserToVip(String id_user, String is_vip) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        HistoryUpVip historyUpVip = new HistoryUpVip(is_vip, format.format(date));

        collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_BUY_PREMIUM)
                .collection("user" + id_user)
                .add(historyUpVip)
                .addOnCompleteListener(task1 -> {
                    /* Lưu thông tin mua vip vào DB */
                    editor.putString(Constant.IS_VIP, type_vip);
                    editor.apply();

                    /* Phát thông báo mua vip thành công */
                    EventBus.getDefault().post(new EventNotifyBuyVipSuccess());

                    new AlertDialog.Builder(this)
                            .setTitle("Thanh toán thành công")
                            .setMessage("Bây giờ bạn có thể trải nghiệm toàn bộ các bộ phim trong ứng dụng của mình.")
                            .setPositiveButton("Ok", (dialog, which) -> {})
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error update", Toast.LENGTH_SHORT).show();
                    refundWhenUpdateVipError(price, payId);
                });
    }

    public void createOrder(String price) {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(price);
            String code = data.getString("return_code");

            if (code.equals("1")) {
                Toast.makeText(getApplicationContext(), getString(R.string.create_payment_success), Toast.LENGTH_LONG).show();
                token = data.getString("zp_trans_token");
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.create_payment_unsuccess), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refundWhenUpdateVipError(String price, String transactionId) {
        Refund refund = new Refund();

        try {
            JSONObject data = refund.refundWhenUpdateVipError(price, transactionId);
            String code = data.getString("return_code");

            if (code.equals("1") || code.equals("3")) {
                Toast.makeText(getApplicationContext(), getString(R.string.refund_success), Toast.LENGTH_SHORT).show();
                token = data.getString("return_message");
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.refund_unsucces), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
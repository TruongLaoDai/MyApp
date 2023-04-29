package com.smile.watchmovie.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityChoosePaymentBinding;
import com.smile.watchmovie.model.CreateOrder;
import com.smile.watchmovie.model.HistoryUpVip;
import com.smile.watchmovie.model.Refund;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ChoosePaymentActivity extends AppCompatActivity {

    private ActivityChoosePaymentBinding binding;
    private String token;
    private String idUser, documentId;
    private String payId;
    private String price, type_vip;
    private SharedPreferences.Editor editor;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_payment);

        binding = ActivityChoosePaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        price = "47000";
        type_vip = "1";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        idUser = sharedPreferences.getString("idUser", "");
        String nameUser = sharedPreferences.getString("name", "");
        String is_vip = sharedPreferences.getString("isVip", "");
        documentId = sharedPreferences.getString("documentId", "");

        setUpFireBase();

        binding.tvNameAccount.setText(nameUser);

        binding.ivBack.setOnClickListener(v -> onBackPressed());

        ZaloPaySDK.init(2553, Environment.SANDBOX);

        binding.loutBuyMonthly.setOnClickListener(v -> {
            price = "47000";
            type_vip = "1";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.loutBuyMonthly.setBackground(getDrawable(R.drawable.bg_premium));
                binding.loutBuyYearly.setBackground(getDrawable(R.drawable.bg_premium_unselected));
                binding.tvDesTypePremium.setText(getString(R.string.des_type_premium_month));
            }
        });

        binding.loutBuyYearly.setOnClickListener(v -> {
            price = "439000";
            type_vip = "2";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.loutBuyYearly.setBackground(getDrawable(R.drawable.bg_premium));
                binding.loutBuyMonthly.setBackground(getDrawable(R.drawable.bg_premium_unselected));
                binding.tvDesTypePremium.setText(getString(R.string.des_type_premium_year));
            }
        });

        if (idUser.equals("")) {
            openDialogRequireLoginUser();
        }
        binding.btnBuy.setEnabled(!idUser.equals(""));
        binding.ivHistoryPay.setEnabled(!idUser.equals(""));

        binding.ivHistoryPay.setOnClickListener(v -> startActivity(new Intent(ChoosePaymentActivity.this, HistoryBuyPremiumActivity.class)));

        binding.btnBuy.setOnClickListener(v ->
                {
                    if ((is_vip.equals("1") && type_vip.equals("1")) || (is_vip.equals("2") && type_vip.equals("2"))) {
                        new AlertDialog.Builder(ChoosePaymentActivity.this)
                                .setTitle("Bạn đã mua gói")
                                .setMessage("Bạn đã mua gói trước đây hãy đợi khi gói hết hạn để mua tiếp")
                                .setPositiveButton("OK", (dialog, which) -> {
                                }).show();
                    } else if (is_vip.equals("1") || is_vip.equals("2")) {
                        new AlertDialog.Builder(ChoosePaymentActivity.this)
                                .setTitle("Bạn có chắc chắn mua?")
                                .setMessage("Bạn đã là thành viên vip nếu bạn mua tiếp gói trước sẽ mất hiệu lực!")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    payForUpVip();
                                }).setNegativeButton("Hủy", null).show();
                    } else {
                        payForUpVip();
                    }
                }
        );
        //demo refund
        binding.ivAccount.setOnClickListener(v -> {
            if(type_vip.equals("1"))
                refundWhenUpdateVipError("47000", payId);
            else
                refundWhenUpdateVipError("439000", payId);
        });
    }

    private void setUpFireBase() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("WatchFilm");
    }

    private void payForUpVip() {
        createOrder(price);
        ZaloPaySDK.getInstance().payOrder(ChoosePaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                runOnUiThread(() -> {
                    payId = transactionId;
                    callApiUpdateUserToVip(idUser, type_vip);
                });
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                new AlertDialog.Builder(ChoosePaymentActivity.this)
                        .setTitle("Bạn đã hủy thanh toán")
                        .setMessage("Hãy thực hiện thanh toán để xem nhiều phim hơn!")
                        .setPositiveButton("OK", null).show();
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                new AlertDialog.Builder(ChoosePaymentActivity.this)
                        .setTitle("Thanh toán lỗi")
                        .setMessage("Có một vài vấn đề trong khi thanh toán bạn hãy thử lại!")
                        .setPositiveButton("OK", null).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void openDialogRequireLoginUser() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_watch_film_from_time);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        TextView tv_at_time = dialog.findViewById(R.id.tv_at_time);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);

        tv_at_time.setText("Bạn cần đăng nhập để thực hiện thanh toán!");

        btn_yes.setOnClickListener(v -> {
            Intent intent = new Intent(ChoosePaymentActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog.dismiss();
        });

        btn_no.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        dialog.show();
    }

    private void callApiUpdateUserToVip(String id_user, String is_vip) {
        collectionReference.document("tbluser").collection("user" + id_user).document(documentId)
                .update("is_vip", is_vip)
                .addOnCompleteListener(task -> {
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    HistoryUpVip historyUpVip = new HistoryUpVip(is_vip, format.format(date));
                    collectionReference.document("tblhistoryupvip").collection("user" + id_user)
                            .add(historyUpVip)
                            .addOnCompleteListener(task1 -> {
                                editor.putString("isVip", type_vip);
                                editor.apply();
                                new AlertDialog.Builder(ChoosePaymentActivity.this)
                                        .setTitle("Thanh toán thành công")
                                        .setMessage("Bây giờ bạn có thể trải nghiệm tất cả phim miễn phí!")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                        }).show();
                            })
                            .addOnFailureListener(e -> {
                                refundWhenUpdateVipError(price, payId);
                                Toast.makeText(ChoosePaymentActivity.this, "Error update", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    refundWhenUpdateVipError(price, payId);
                    Toast.makeText(ChoosePaymentActivity.this, "Error update", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), getString(R.string.refund_success), Toast.LENGTH_LONG).show();
                token = data.getString("return_message");
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.refund_unsucces), Toast.LENGTH_LONG).show();
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
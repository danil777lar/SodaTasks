package com.larje.taskmanager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.larje.taskmanager.optionDialogs.RebootDialog;

import java.util.ArrayList;
import java.util.List;

public class MyBilling {

    private Context context;
    private FragmentManager fm;

    private BillingClient billingClient;
    private String subId = "sku_id_1";
    private ArrayMap<String, SkuDetails> skuDetails = new ArrayMap<>();

    private Boolean checkResult = null;
    private View adView;
    private Boolean subDialog;

    public MyBilling(Context context, FragmentManager fm){
        this.context  = context;
        this.fm = fm;
        makeBillingInstance();
    }

    public MyBilling(Context context, FragmentManager fm, View adView){
        this.context  = context;
        this.fm = fm;
        this.adView = adView;
        makeBillingInstance();
    }

    public MyBilling(Context context, FragmentManager fm, Boolean subDialog){
        this.context  = context;
        this.fm = fm;
        this.subDialog = subDialog;
        makeBillingInstance();
    }

    private void makeBillingInstance(){
        billingClient = BillingClient.newBuilder(context).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
//              Покупка сделана
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                    RebootDialog reboot = new RebootDialog();
                    reboot.setCancelable(false);
                    reboot.show(fm, "s");
                    billingClient.endConnection();
                }
            }
        }).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
//              Узнаем о покупках
                if (billingResult.getResponseCode() == 0){
                    getSkuInfo();
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                    checkResult = false;
                    for (int i = 0; i < purchases.size(); i++) {
                        String pId = purchases.get(i).getSku();
                        if (TextUtils.equals(subId, pId)) {
                            checkResult = true;
                        }
                    }
                } else {
                    checkResult = false;
                }
                sendResult(checkResult);
            }

            @Override
            public void onBillingServiceDisconnected() {
//              Шото не так
                billingClient.startConnection(this);
            }
        });
    }

    private void getSkuInfo(){
        SkuDetailsParams.Builder skuBuilder = SkuDetailsParams.newBuilder();
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add(subId);
        skuBuilder.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(skuBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == 0){
                    for (SkuDetails details : list){
                        skuDetails.put(details.getSku(), details);
                    }
                }
            }
        });
    }

    private void sendResult(Boolean checkResult){
        if (!checkResult){
            if (adView != null){
                adView.setVisibility(View.VISIBLE);
            } else if (subDialog == true){
                SubDialog dlg = new SubDialog(this);
                dlg.show(fm, "s");
            }
        }
    }

    public void makeSub(){
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails.get(subId))
                .build();
        billingClient.launchBillingFlow((Activity)context, billingFlowParams);
    }


}
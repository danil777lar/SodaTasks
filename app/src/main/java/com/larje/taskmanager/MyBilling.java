package com.larje.taskmanager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBilling {

    private Context context;

    private BillingClient billingClient;
    private String subId = "sku_id_1";
    private Map<String, SkuDetails> skuDetails = new HashMap<>();

    public MyBilling(Context context){
        this.context  = context;
        makeBillingInstance();
    }

    private void makeBillingInstance(){
        billingClient = BillingClient.newBuilder(context).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
//              Покупка сделана
                System.exit(0);
            }
        }).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
//              Узнаем о покупках
                getSkuInfo();
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
                Log.d("billing", ""+billingResult.getResponseCode());
                Log.d("billing", billingResult.getDebugMessage());
                if (billingResult.getResponseCode() == 0){
                    for (SkuDetails details : list){
                        skuDetails.put(details.getSku(), details);
                    }
                }
            }
        });
    }

    public boolean checkSub(){
        List<Purchase> purchases = ((Purchase.PurchasesResult) billingClient.queryPurchases(BillingClient.SkuType.SUBS)).getPurchasesList();

        if (purchases != null){
            for(int i = 0; i < purchases.size(); i++){
                String pId = purchases.get(i).getSku();
                if (TextUtils.equals(subId, pId)){
                    return true;
                }
            }
        }
        return false;
    }

    public void makeSub(){
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails.get(subId))
                .build();
        billingClient.launchBillingFlow((Activity)context, billingFlowParams);
    }


}

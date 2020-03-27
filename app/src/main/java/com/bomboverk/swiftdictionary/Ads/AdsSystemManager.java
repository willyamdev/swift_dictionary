package com.bomboverk.swiftdictionary.Ads;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public final class AdsSystemManager {

    //OFICIAL
    private String AppID = "ca-app-pub-6089547548003012~2631014498";
    //ID DE TESTE
    //private String AppID = "ca-app-pub-3940256099942544~3347511713";

    //BANNER TESTE
    //ca-app-pub-3940256099942544/6300978111
    //BANER OFICIAL
    //ca-app-pub-6089547548003012/7451741112

    public AdsSystemManager(Context context) {
        MobileAds.initialize(context, AppID);
    }

    public void createBanner(AdView view){
        AdRequest adRequest = new AdRequest.Builder().build();
        view.loadAd(adRequest);

        view.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }
}

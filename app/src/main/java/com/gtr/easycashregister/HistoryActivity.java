package com.gtr.easycashregister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class HistoryActivity extends AppCompatActivity {

    String phoneNumber;
    TextView phoneTextShow;

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        phoneNumber = getIntent().getStringExtra("mainNumber");
        phoneTextShow= findViewById(R.id.phoneTextShow);

        phoneTextShow.setText(phoneNumber);

        /*mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");

        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                phoneTextShow.setText(phoneNumber);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });*/
    }
}
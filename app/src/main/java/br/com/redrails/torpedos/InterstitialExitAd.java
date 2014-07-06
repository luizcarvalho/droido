package br.com.redrails.torpedos;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * An {@link Activity} that requests and can display an InterstitialAd.
 */
public class InterstitialExitAd extends Activity {
    private static final String AD_UNIT_ID = "ca-app-pub-6662263716470706/5987486187";
    private InterstitialAd interstitialAd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.interstitial);


        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AD_UNIT_ID);
        Button continuar = (Button) findViewById(R.id.continuar_button);
        Button sair = (Button) findViewById(R.id.sair_button);

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InterstitialExitAd.this, MainActivity.class));
            }
        });
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = getErrorReason(errorCode);
                Toast.makeText(InterstitialExitAd.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        loadInterstitial();
    }
    public void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("502D84F4ECF3CB4A69829D7372AA0B5B")
                .build();

        // Load the interstitial ad.
        interstitialAd.loadAd(adRequest);
    }

    /** Called when the Show Interstitial button is clicked. */
    public void showInterstitial() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(InterstitialExitAd.this,  "Interstitial ad was not ready to be shown.", Toast.LENGTH_SHORT).show();
        }
    }
    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch(errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

}
package com.example.rui.eventgps;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

/**
 * Created by ray on 2018/6/7.
 */

//@see <a href="https://github.com/ViksaaSkool/AwesomeSplash">AwesomeSplash</a>
public class LaunchActivity extends AwesomeSplash{
    private static final String TAG = "EventGPS";
    private static final int LOC_PERMISSION_REQUEST_CODE = 9002;
    private Boolean mLocationPermission;
    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(R.color.colorPrimary);
        configSplash.setAnimCircularRevealDuration(500);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        configSplash.setLogoSplash(R.raw.logo_final); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000);
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn);

        configSplash.setTitleSplash("EventGPS");
        configSplash.setTitleTextColor(R.color.green);
        configSplash.setTitleTextSize(28f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.FadeInDown);
    }

    @Override
    public void animationsFinished() {
        checkGoogleService();
        getLoactionPermission();
//        startActivity(new Intent(LaunchActivity.this, MapEventActivity.class));
//        finishAfterTransition();//Make sure the launch screen only showed once.
    }

    @Override
    public void onBackPressed() {
    }

    private boolean checkGoogleService() {
        Log.d(TAG, "Check google services version");
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)) {
            //an error occured but we can resolve it
            Log.d(TAG, "UserResolvableError");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, isAvailable, 9001);
            dialog.show();
        } else {
            Toast.makeText(this, "Google Play Service Unavailable", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLoactionPermission() {
        String[] locationPermission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermission = true;
                startActivity(new Intent(LaunchActivity.this, MapEventActivity.class));
                finishAfterTransition();//Make sure the launch screen only showed once.
            } else {
                ActivityCompat.requestPermissions(this, locationPermission, LOC_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, locationPermission, LOC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermission = false;

        switch (requestCode) {
            case LOC_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermission = false;
                            return;
                        }
                    }
                    mLocationPermission = true;
                    startActivity(new Intent(LaunchActivity.this, MapEventActivity.class));
                    finishAfterTransition();//Make sure the launch screen only showed once.
                }
            }
        }
    }
}

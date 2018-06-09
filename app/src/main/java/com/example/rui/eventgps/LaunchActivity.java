package com.example.rui.eventgps;

import android.content.Intent;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

/**
 * Created by ray on 2018/6/7.
 */

//@see <a href="https://github.com/ViksaaSkool/AwesomeSplash">AwesomeSplash</a>
public class LaunchActivity extends AwesomeSplash{
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
        startActivity(new Intent(LaunchActivity.this, MapEventActivity.class));
        finishAfterTransition();//Make sure the launch screen only showed once.
    }

    @Override
    public void onBackPressed() {
    }

}

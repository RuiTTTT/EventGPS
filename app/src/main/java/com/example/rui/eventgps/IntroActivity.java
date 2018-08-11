package com.example.rui.eventgps;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughActivity;
import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 2018/8/10.
 */

public class IntroActivity extends FancyWalkthroughActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FancyWalkthroughCard introPage1 = new FancyWalkthroughCard("Welcome", "EventGPS notifies you possible traffic caused by events and suggests alternative routes.",R.drawable.ic_icons_car);
        introPage1.setBackgroundColor(R.color.colorIntro);
        introPage1.setTitleTextSize(dpToPixels(10, this));
        introPage1.setIconLayoutParams(800,800,200,0,0,0);

        FancyWalkthroughCard introPage2 = new FancyWalkthroughCard("Event", "Event page allows you to check all the events happening in dublin on the date selected.",R.drawable.ic_logo_cal);
        introPage2.setBackgroundColor(R.color.colorIntro);
        introPage2.setTitleTextSize(dpToPixels(10, this));
        introPage2.setIconLayoutParams(800,800,200,0,0,0);

        FancyWalkthroughCard introPage3 = new FancyWalkthroughCard("Map", "Map page allows you to get real time event information by entering your source and destination.It also suggest alternative routes.",R.drawable.ic_logo_map);
        introPage3.setBackgroundColor(R.color.colorIntro);
        introPage3.setTitleTextSize(dpToPixels(10, this));
        introPage3.setIconLayoutParams(800,800,200,0,0,0);

        List<FancyWalkthroughCard> pages = new ArrayList<>();
        pages.add(introPage1);
        pages.add(introPage2);
        pages.add(introPage3);

        setFinishButtonTitle("Get Started");
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        showNavigationControls(true);
        setColorBackground(R.color.colorSignBackground);
        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
//        Toast.makeText(this, "Finish Pressed", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(IntroActivity.this, MapEventActivity.class));
    }
}

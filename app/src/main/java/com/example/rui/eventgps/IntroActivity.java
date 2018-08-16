package com.example.rui.eventgps;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughActivity;
import com.shashank.sony.fancywalkthroughlib.FancyWalkthroughCard;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for showing a brief user instruction by clicking the help button in the menu.
 * The library this class use is from FancyWalkthrough-Android github repository,
 * author Shashank02051997.
 * @link https://github.com/Shashank02051997/FancyWalkthrough-Android
 * Created by rui on 2018/8/10.
 */

public class IntroActivity extends FancyWalkthroughActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //The first page
        FancyWalkthroughCard introPage1 = new FancyWalkthroughCard("Welcome", "EventGPS notifies you possible traffic caused by events and suggests alternative routes.",R.drawable.ic_icons_car);
        introPage1.setBackgroundColor(R.color.colorIntro);
        introPage1.setTitleTextSize(dpToPixels(10, this));
        introPage1.setIconLayoutParams(800,800,200,0,0,0);

        //The second page
        FancyWalkthroughCard introPage2 = new FancyWalkthroughCard("Event", "Event page allows you to check all the events happening in dublin on the date selected.",R.drawable.ic_logo_cal);
        introPage2.setBackgroundColor(R.color.colorIntro);
        introPage2.setTitleTextSize(dpToPixels(10, this));
        introPage2.setIconLayoutParams(800,800,200,0,0,0);

        //The third page
        FancyWalkthroughCard introPage3 = new FancyWalkthroughCard("Map", "Map page allows you to get real time event information by entering your source and destination.It also suggest alternative routes.",R.drawable.ic_logo_map);
        introPage3.setBackgroundColor(R.color.colorIntro);
        introPage3.setTitleTextSize(dpToPixels(10, this));
        introPage3.setIconLayoutParams(800,800,200,0,0,0);

        List<FancyWalkthroughCard> pages = new ArrayList<>();
        pages.add(introPage1);
        pages.add(introPage2);
        pages.add(introPage3);

        //A finish button at the last page.
        setFinishButtonTitle("Get Started");
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        showNavigationControls(true);
        setColorBackground(R.color.colorSignBackground);
        setOnboardPages(pages);
    }

    /**
     * Method handling the event after clicking the finish button.
     * Navigate to the map page of the app.
     */
    @Override
    public void onFinishButtonPressed() {
//        Toast.makeText(this, "Finish Pressed", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(IntroActivity.this, MapEventActivity.class));
    }
}

package com.example.rui.eventgps;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The class for navigation drawer layout. By tap or slide the left side, a drawer menu will
 * come out.
 * The menu is consist of two parts.
 * The first part displaying basic information for the user logged in, like user icon, name and
 * email address.
 * The second part containing four menu items, event, map, sign out and help.
 */
public class MapEventActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView userIcon;
    private TextView userName;
    private TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_event);
        //Set the toolbar on the top of the app with a name and small logo.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("EventGPS");
        toolbar.setLogo(R.drawable.ic_logo_icon_action);
        setSupportActionBar(toolbar);

        //Lock the screen from rotating
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //The default fragment to display
        MapFragment mMapFragment = new MapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mMapFragment);
        fragmentTransaction.commit();

        //Get the logged in user info and display it.
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        View mView =  navigationView.inflateHeaderView(R.layout.nav_header_map_event);
        userIcon = (ImageView) mView.findViewById(R.id.nav_imageView);
        userName = (TextView) mView.findViewById(R.id.nav_user_name);
        userEmail = (TextView) mView.findViewById(R.id.nav_user_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            userName.setText(name);
            userEmail.setText(email);
            //Glide is used for quickly load photo
            if(photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .fitCenter()
                                .override(280,280)
                                .circleCrop())
                        .into(userIcon);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * The method handling back button press action.
     * The drawer will closed if it is opened already.
     * If not, handle as normal.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //If event item is selected, navigate to the event page.
        if (id == R.id.nav_event) {
            // Handle the camera action
            EventFragment mEventFragment = new EventFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mEventFragment);
            fragmentTransaction.commit();

            //If map item is selected, navigate to the map page.
        } else if (id == R.id.nav_map) {
            MapFragment mMapFragment = new MapFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mMapFragment);
            fragmentTransaction.commit();

            //If sign out item is selected, check user status first.
            //If there's a user logged in, sign out the user and change the text to sign in
            //If no user logged in now, navigate to the login page instead,
        } else if (id == R.id.nav_sign) {
            item.setCheckable(false);
            if(item.getTitle() == "Sign In") {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                Toast.makeText(getBaseContext(), "Successfully Sign Out", Toast.LENGTH_SHORT).show();
                            }
                        });
                //Set user information displayed as default after user sign out.
                userName.setText("User Name");
                userEmail.setText("user.name@eventgps.com");
                userIcon.setImageResource(R.mipmap.ic_launcher_round);
                item.setTitle("Sign In");
            }

            //If help item is selected, navigate to the help page.
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(this, IntroActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

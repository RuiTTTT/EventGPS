package com.example.rui.eventgps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

/**
 * The class for user login and sign up functionality.
 * Use Google Firebase platform for the authentication.
 * Our app allows login through google accountant or an email address.
 * If sign up with a new email accountant, a verify email will send from the platform.
 * Created by rui on 2018/7/12.
 */
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setLogo(R.drawable.ic_logo_sign)
                        .setTheme(R.style.SignTheme)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * The method handling login response.
     * @param requestCode
     * @param resultCode The result code for different actions
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in, navigate to the map page
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                startActivity(new Intent(LoginActivity.this, MapEventActivity.class));
                finish();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, R.string.sign_in_cancelled, Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }
}

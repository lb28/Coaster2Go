package de.uulm.dbis.coaster2go;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // user signed in?
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in, redirect to park overview
            Intent intent = new Intent(this, ParkOverviewActivity.class);
            startActivity(intent);
        } else {
            // No user is signed in
        }

        setTitle("Coaster2Go");

        //Startimplementierung der Verbindung
        try {
            mClient = new MobileServiceClient(
                    "https://coaster2go.azurewebsites.net",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //Buttonklickmethode für Data Test Activity:
    public void clickTestButton(View view) {
        Intent intent = new Intent(this, TestDataActivity.class);
        startActivity(intent);
    }

    public void verifyLogin(View view) {

    }

    public void goToRegisterActivity(View view) {

    }

    public void goToMessageActivity(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

    private void showSnackbar(int resId) {
        Snackbar.make(
                findViewById(R.id.coordinatorLayout_MainActivity),
                resId,
                Snackbar.LENGTH_LONG
        );
    }

    public void goToParkOverview(View view) {
        Intent intent = new Intent(this, ParkOverviewActivity.class);
        startActivity(intent);
    }
}

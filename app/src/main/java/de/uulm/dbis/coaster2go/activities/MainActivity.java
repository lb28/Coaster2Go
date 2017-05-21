package de.uulm.dbis.coaster2go.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.uulm.dbis.coaster2go.R;

public class MainActivity extends AppCompatActivity {

    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // facebook keyhash for login (needed only once?)
        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "de.uulm.dbis.coaster2go",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
            Log.i("KeyHash", "could not be generated");
        }
        */

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


    public void goToParkOverview(View view) {
        Intent intent = new Intent(this, ParkOverviewActivity.class);
        startActivity(intent);
    }
}

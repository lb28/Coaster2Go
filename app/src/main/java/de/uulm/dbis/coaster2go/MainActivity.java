package de.uulm.dbis.coaster2go;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void goToMessageActivity(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }
}

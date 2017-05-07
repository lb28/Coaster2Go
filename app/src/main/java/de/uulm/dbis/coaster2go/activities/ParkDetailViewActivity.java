package de.uulm.dbis.coaster2go.activities;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Park;

public class ParkDetailViewActivity extends BaseActivity {

    private String parkId;

    private List<Park> parkList;
    private Park park;

    TextView parkName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail_view);

        parkName = (TextView) findViewById(R.id.park_detail_parkname);

        parkId = getIntent().getStringExtra("parkId");

        System.out.println("ParkID: " + parkId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AzureDBManager azureDBManager = new AzureDBManager(ParkDetailViewActivity.this);

                parkList = azureDBManager.getParkById(parkId);
            }
        });


        park = parkList.get(0);
        parkName.setText(park.getName());



        Toast.makeText(this, "ParkId: " + parkId, Toast.LENGTH_SHORT).show();
    }

}



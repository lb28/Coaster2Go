package de.uulm.dbis.coaster2go.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.*;

public class TestDataActivity extends AppCompatActivity {
    //Daten zur Verwaltung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data);

        //Test if Animated Gif does get shown
        ImageView imageView = (ImageView) findViewById(R.id.gifView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.small_loader).into(imageViewTarget);


    //Thread testet die neusten Methoden des AzureDBManagers
        new Thread(new Runnable() {
            public void run() {
                System.out.println("--------------------------- AzureDBManager Test Start");

                AzureDBManager dbManager = new AzureDBManager(TestDataActivity.this);
                AzureExampleData dbExample = new AzureExampleData(TestDataActivity.this);


                //Fill Database with example Data:
                //dbExample.fillDatabase();

                //Fill Database with new WaitingTimes
                //Important for different Hours in the Statistics
                //dbExample.fillDatabaseWaitingTimes();

                //System.out.println(new JsonManager(TestDataActivity.this).deleteFile(JsonManager.JSON_FILENAME_ATTRACTIONS_FAVORITES));

                if(dbManager.hasActiveInternetConnection()){
                    System.out.println("Internet connection");
                }else{
                    System.out.println("NO Internet connection");
                }

                System.out.println("--------------------------- AzureDBManager Test Ende");

            }
        }).start();




    }
}

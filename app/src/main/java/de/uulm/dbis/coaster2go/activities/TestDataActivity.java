package de.uulm.dbis.coaster2go.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.*;

public class TestDataActivity extends AppCompatActivity {
    //Daten zur Verwaltung
    private TextView testText;
    WaitingTime testTimeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data);

        testTimeZone = new WaitingTime("", "Timezone test", "Timezone test", 25);

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
                dbExample.fillDatabaseWaitingTimes();


                /*
                //Date Test
                List<Park> parkList = dbManager.getParkList();
                String parkId = "";
                for(Park p : parkList){
                    if(p.getLocation().equals("Rust")){
                        parkId = p.getId();
                    }
                }

                List<Attraction> attractionList = dbManager.getAttractionList(parkId);
                String attractionId = attractionList.get(0).getId();
                Date now = new Date();
                WaitingTime resultTimeZone = dbManager.createWaitingTime(testTimeZone);
                WaitingTime resultTimeZone2 = dbManager.getPartOfWaitingTimeList(attractionId, 0).get(0);
                System.out.println("Current phone hour: "+now.getHours()+
                        "\nReturned createdAt hour: "+resultTimeZone.getCreatedAt().getHours()+
                        "\nRead createdAtHour: "+resultTimeZone2.getCreatedAt().getHours()+
                        "\nAll the same?");


                 */
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

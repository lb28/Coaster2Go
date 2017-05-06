package de.uulm.dbis.coaster2go.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.*;

public class TestDataActivity extends AppCompatActivity {
    //Variablen für Datenbankverbindungen
    private MobileServiceClient mClient;
    MobileServiceTable<Park> mParkTable;
    //Daten zur Verwaltung
    private TextView testText;
    Park testPark, testPark2, testPark3;
    Attraction testAttraction, testAttraction2, testAttraction3, testAttraction4;
    Review testReview, testReview2, testReview3, testReview4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data);

        /*
        //Startimplementierung der Verbindung
        try {
            mClient = new MobileServiceClient(
                    "https://coaster2go.azurewebsites.net",
                    this
            );

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //"TableHandler" erstellen
        mParkTable = mClient.getTable(Park.class);

        */

        //Test Daten erstellen

        testPark = new Park("Europa Park", "Rust", "schöner Park", 48.266015, 7.721972,
                "http://www.mehrdrauf.de/cm/sparkasse-staufen-breisach/images/Europa-Park/EP2016_300x200.jpg",
                2, 4.5, "admin");

        testPark2 = new Park("Pripyat", "Tschernobyl", "Geschlossen.", 51.408246, 30.055386,
                "https://f1.blick.ch/img/incoming/origs3669981/9972533768-w1280-h960/tschernobyl00010.jpg",
                1, 1.0, "admin");
        testPark3 = new Park("Handschuhwelt", "Bikini Bottom", "Luft anhalten.", 11.644220, 165.376451,
                "http://en.spongepedia.org/images/9/90/Gloverworld.jpg",
                3, 5.0, "admin");

        testAttraction = new Attraction("Euro Mir", "Achterbahn", "Die Euro-Mir ist eine Achterbahn," +
                " die im Europa-Park in Rust steht. Sie steht im russischen Themenbereich und wurde 1997 " +
                "nach dreijähriger Bauzeit eröffnet.\n" +
                "Höhe:28 m\n" +
                "Höchstgeschwindigkeit:80 km/h\n" +
                "Züge:9 Züge, 4 Wagen/Zug, 2 Sitzreihen/Wagen,", 48.264880, 7.719965,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Euro-Mir_in_Europa-Park_Rust.JPG/220px-Euro-Mir_in_Europa-Park_Rust.JPG",
                2, 5, 2, 45, 1, 30, 30, "");

        testAttraction2 = new Attraction("Blue Fire", "Achterbahn", "Der blue fire Megacoaster powered " +
                "by GAZPROM im Europa-Park ist eine Stahlachterbahn vom Modell Launched Coaster – " +
                "Mega Coaster des Herstellers Mack Rides, die am 4. April 2009 eröffnet wurde.\n" +
                "Max. Beschleunigung:3,8 g\n" +
                "Höchstgeschwindigkeit:100 km/h\n" +
                "g-Kraft:3,8 g", 48.262391, 7.718368,
                "http://www.gazprom.de/f/posts/49/836540/katapultachterbahn-blue-fire.jpg",
                2, 3.5, 2, 50, 1, 25, 25, "");

        testAttraction3 = new Attraction("FoodLoop", "Restuarant", "Essen wird hier auf Achterbahnen " +
                "mit Looping zu den Gästen gebracht", 48.264373, 7.720820,
                "https://media.holidaycheck.com/data/urlaubsbilder/mittel/41/1162565089.jpg",
                1, 3, 1, 20, 1, 20, 20, "");

        testAttraction4 = new Attraction("Mül Müls Karussell", "Kinderbahn", "Kleines Karussell für Kinder",
                48.264128, 7.724011,
                "http://www.ep-fans.info/bilder/4283/1418589009.jpg",
                1, 4, 1, 5, 1, 5, 5, "");

        testReview = new Review("", "Peter Lustig", "Peter Lustig ID", 5, "Finde den Park lustig.");
        testReview2 = new Review("", "Peter Unlustig", "Peter Unlustig ID", 2, "Finde den Park nicht lustig.");
        testReview3 = new Review("", "Peter Lustig", "Peter Lustig ID", 5, "Finde die Attraktion lustig.");
        testReview4 = new Review("", "Peter Lustig", "Peter Unlustig ID", 1, "Finde die Attraktion nicht lustig.");





/*
        //Test Park in Datenbank ladem:
        mClient.getTable(Park.class).insert(testPark, new TableOperationCallback<Park>() {
            public void onCompleted(Park entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                } else {
                    exception.printStackTrace();
                }
            }
        });
*/

    //Thread testet die neusten Methoden des AzureDBManagers
        new Thread(new Runnable() {
            public void run() {
                System.out.println("--------------------------- AzureDBManager Test Start");
                AzureDBManager.test();

                AzureDBManager dbManager = new AzureDBManager(TestDataActivity.this);

                /*
                Park res = dbManager.createPark(testPark);
                Park res2 = dbManager.createPark(testPark2);
                Park res3 = dbManager.createPark(testPark3);
                System.out.println(res.toString()+res2.toString()+res3.toString());
                List<Park> parkList = dbManager.getParkList();
                System.out.println(parkList.toString());
                res3.setAdmin("Test Update");
                Park resUpdate = dbManager.updatePark(res3);
                System.out.println(resUpdate.toString());
                System.out.println(dbManager.getParkById(res.getId()));
                */

                /*
                List<Park> parkList = dbManager.getParkList();
                String parkId = parkList.get(0).getId();
                testAttraction.setParkId(parkId);
                testAttraction2.setParkId(parkId);
                testAttraction3.setParkId(parkId);
                testAttraction4.setParkId(parkId);

                Attraction resAtt = dbManager.createAttraction(testAttraction);
                Attraction resAtt2 = dbManager.createAttraction(testAttraction2);
                Attraction resAtt3 = dbManager.createAttraction(testAttraction3);
                Attraction resAtt4 = dbManager.createAttraction(testAttraction4);
                System.out.println(testAttraction.toString());
                resAtt2.setName("Blue Fire Megacoaster");
                resAtt = dbManager.updateAttraction(resAtt2);
                System.out.println(resAtt.toString());
                resAtt = dbManager.getAttractionById(resAtt3.getId()).get(0);
                System.out.println(resAtt.toString());

                List<Attraction> attractionList = dbManager.getParkList(parkId);
                System.out.println(attractionList.toString());

                */

                List<Park> parkList = dbManager.getParkList();
                String parkId = parkList.get(0).getId();
                testReview.setReviewedId(parkId);

                List<Attraction> attractionList = dbManager.getParkList(parkId);
                String attractionId = attractionList.get(0).getId();

                testReview.setReviewedId(parkId);
                testReview2.setReviewedId(parkId);
                testReview3.setReviewedId(attractionId);
                testReview4.setReviewedId(attractionId);

                Review resultReview = dbManager.createReview(testReview, false);
                Review resultReview2 = dbManager.createReview(testReview2, false);
                Review resultReview3 = dbManager.createReview(testReview3, true);
                Review resultReview4 = dbManager.createReview(testReview4, true);
                resultReview3.setNumberOfStars(4);
                Review resultReview5 = dbManager.updateReview(resultReview3, true);
                System.out.println(resultReview.toString()+resultReview3.toString()+resultReview5.toString());
                System.out.println("--------------------------------------------------");
                List<Review> reviewList = dbManager.getReviewList(parkId);
                System.out.println(reviewList.toString());

                Review lustigReview = dbManager.getReviewOfUser(attractionId, "Peter Lustig ID").get(0);
                System.out.println(lustigReview.toString());

                System.out.println("--------------------------- AzureDBManager Test Ende");

            }
        }).start();




    }
}

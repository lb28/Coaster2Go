package de.uulm.dbis.coaster2go;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import de.uulm.dbis.coaster2go.data.Park;

public class TestDataActivity extends AppCompatActivity {
    //Variablen für Datenbankverbindungen
    private MobileServiceClient mClient;
    MobileServiceTable<Park> mParkTable;
    //Daten zur Verwaltung
    private TextView testText;
    Park testPark, testPark2, testPark3;
    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data);

        System.out.println("--------------------------- start testklasse");

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

        //Test Park erstellen
        testPark = new Park("Europa Park", "Rust", "schöner Park", 48.266015, 7.721972,
                "http://www.mehrdrauf.de/cm/sparkasse-staufen-breisach/images/Europa-Park/EP2016_300x200.jpg",
                2, 4.5, "admin");

        testPark2 = new Park("Pripyat", "Tschernobyl", "Geschlossen.", 51.408246, 30.055386,
                "https://f1.blick.ch/img/incoming/origs3669981/9972533768-w1280-h960/tschernobyl00010.jpg",
                1, 1.0, "admin");
        testPark3 = new Park("Handschuhwelt", "Bikini Bottom", "Luft anhalten.", 11.644220, 165.376451,
                "http://en.spongepedia.org/images/9/90/Gloverworld.jpg",
                3, 5.0, "admin");


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

    //Zweite (wahrscheinlich bessere Variante Park in Datenbank laden und zu lesen:
        new Thread(new Runnable() {
            public void run() {
                try {
                    //Park hochladen
                    Park resultPark = mParkTable.insert(testPark).get();
                    Park resultPark2 = mParkTable.insert(testPark2).get();
                    Park resultPark3 = mParkTable.insert(testPark3).get();
                    System.out.println("--------------------------- nach hochladen der Daten");

                    //Liste aller Parks zurück geben:
                    List<Park> results = mParkTable.execute().get();
                    System.out.println("--------------------------- nach runterladen der Daten");

                    //Parks anzeigen
                    resultText = "Gelesene Daten:\n ";
                    for(Park r: results){
                        resultText += r.toString();
                    }
                    System.out.println(resultText);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            testText = (TextView) findViewById(R.id.testDataTextView);
                            testText.setText(resultText);
                        }
                    });


                } catch (MobileServiceException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();




    }
}

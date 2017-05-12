package de.uulm.dbis.coaster2go.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Park;

public class ParkDetailViewActivity extends BaseActivity {

    private String parkId;
    private Park park;

    TextView parkName, parkLocation, parkRatingAvg, parkDescription;
    RatingBar ratingBar;
    ImageView parkImage;
    ImageButton buttonFav, buttonMaps;
    Button buttonAttractions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail_view);

        parkName = (TextView) findViewById(R.id.park_detail_parkname);
        parkLocation = (TextView) findViewById(R.id.park_detail_location);
        parkRatingAvg = (TextView) findViewById(R.id.park_detail_average);
        parkDescription = (TextView) findViewById(R.id.park_detail_description);
        ratingBar = (RatingBar) findViewById(R.id.park_detail_ratingbar);
        parkImage = (ImageView) findViewById(R.id.park_detail_image);
        buttonFav = (ImageButton) findViewById(R.id.park_detail_button_favorite);
        buttonMaps = (ImageButton) findViewById(R.id.park_detail_button_maps);
        buttonAttractions = (Button) findViewById(R.id.park_detail_button_attractions);

        //get parkID from intent (clicked list item from previous activity)
        parkId = getIntent().getStringExtra("parkId");

        //get all data from db and fill in all information
        new LoadParkAsync().execute();

        buttonAttractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAttractionOverview(null);
            }
        });

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call redirect method to rating activity
            }
        });

        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // creates an Intent that will load a map of the park
                                Uri gmmIntentUri = Uri.parse("geo:" + park.getLat() + ", " + park.getLon());
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                //check if there is a suitable app to execute
                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                //dialog for forwarding to google maps
                AlertDialog.Builder builder = new AlertDialog.Builder(ParkDetailViewActivity.this);
                builder.setMessage("Sie werden jetzt zu Google Maps weitergeleitet").setPositiveButton("OK", dialogClickListener)
                        .setNegativeButton("Zurück", dialogClickListener).show();
            }
        });

        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save park as favorite
            }
        });


    }

    public void goToAttractionOverview(View view) {
        Intent intent = new Intent(this, AttractionOverviewActivity.class);
        intent.putExtra("parkId", parkId);
        startActivity(intent);
    }


    public class LoadParkAsync extends AsyncTask<Void, Void, Park> {

        @Override
        protected void onPreExecute() {
            ParkDetailViewActivity.this.progressBar.show();
        }

        @Override
        protected Park doInBackground(Void... params) {
            return new AzureDBManager(ParkDetailViewActivity.this).getParkById(parkId);
        }

        @Override
        protected void onPostExecute(Park park2) {
            if (park2 == null) {
                Log.e("", "LoadParkAsync.onPostExecute: parkList was null!");
            } else {
                park = park2;
                Picasso.with(ParkDetailViewActivity.this).load(park2.getImage()).into(parkImage);
                parkName.setText(park2.getName());
                ratingBar.setRating((float) park2.getAverageReview());
                DecimalFormat df = new DecimalFormat("#.#");
                parkRatingAvg.setText(df.format(park2.getAverageReview()));
                parkLocation.setText(park2.getLocation());
                parkDescription.setText(park2.getDescription());
            }
            ParkDetailViewActivity.this.progressBar.hide();
        }
    }

}



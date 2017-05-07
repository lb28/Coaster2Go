package de.uulm.dbis.coaster2go.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

        //get parkID from intent (clicked list item from previous activity)
        parkId = getIntent().getStringExtra("parkId");

        //get all data from db and fill in all information
        new LoadParkAsync().execute();

        //TODO maps button, favorite button, attraction button, ratingbar clickable, image scale


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
        protected void onPostExecute(Park park) {
            if (park == null) {
                Log.e("", "LoadParkAsync.onPostExecute: parkList was null!");
            } else {
                Picasso.with(ParkDetailViewActivity.this).load(park.getImage()).into(parkImage);
                parkName.setText(park.getName());
                parkRatingAvg.setText("" + park.getAverageReview());
                ratingBar.setRating((float) park.getAverageReview());
                parkLocation.setText(park.getLocation());
                parkDescription.setText(park.getDescription());
            }
            ParkDetailViewActivity.this.progressBar.hide();
        }
    }

}



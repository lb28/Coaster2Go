package de.uulm.dbis.coaster2go.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Attr;

import java.text.DecimalFormat;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.Attraction;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Park;

public class AttractionDetailViewActivity extends BaseActivity {

    private String attrID;
    private Attraction attr;

    ImageView attrImage;
    TextView attrName, attrAvgRating;
    EditText enterTime;
    RatingBar attrRatingbar;
    ImageButton buttonFav, buttonInfo, buttonMap;
    Button buttonSave, currentWait, todayWait, alltimeWait;
    BarChart barChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail_view);

        attrImage = (ImageView) findViewById(R.id.attr_detail_image);
        buttonFav = (ImageButton) findViewById(R.id.attr_detail_button_fav);
        attrName = (TextView) findViewById(R.id.attr_detail_name);
        attrRatingbar = (RatingBar) findViewById(R.id.attr_detail_ratingbar);
        attrAvgRating = (TextView) findViewById(R.id.attr_detail_ratingavg);
        buttonInfo = (ImageButton) findViewById(R.id.attr_detail_button_info);
        buttonMap = (ImageButton) findViewById(R.id.attr_detail_button_map);

        currentWait = (Button) findViewById(R.id.attr_detail_wait_current);
        todayWait = (Button) findViewById(R.id.attr_detail_wait_today);
        alltimeWait = (Button) findViewById(R.id.attr_detail_wait_alltime);

        barChart = (BarChart) findViewById(R.id.attr_detail_barchart);

        enterTime = (EditText) findViewById(R.id.attr_detail_time_edit);
        buttonSave = (Button) findViewById(R.id.attr_detail_button_save_time);

        //TODO get attraction ID from intent

        new LoadAttrAsync().execute();

        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO favorites
            }
        });

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attrDesc = attr.getDescription();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_NEUTRAL:
                                //Ok button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                //dialog for showing the attraction description
                AlertDialog.Builder builder = new AlertDialog.Builder(AttractionDetailViewActivity.this);
                builder.setMessage(attrDesc).setNeutralButton("Schließen", dialogClickListener).show();

            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // creates an Intent that will load a map of the park
                                Uri gmmIntentUri = Uri.parse("geo:" + attr.getLat() + ", " + attr.getLon());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AttractionDetailViewActivity.this);
                builder.setMessage("Sie werden jetzt zu Google Maps weitergeleitet").setPositiveButton("OK", dialogClickListener)
                        .setNegativeButton("Zurück", dialogClickListener).show();
            }
        });

        //TODO wating time buttons onclick








    }

    public class LoadAttrAsync extends AsyncTask<Void, Void, Attraction> {

        @Override
        protected void onPreExecute() {
            AttractionDetailViewActivity.this.progressBar.show();
        }

        @Override
        protected Attraction doInBackground(Void... params) {
            return new AzureDBManager(AttractionDetailViewActivity.this).getAttractionById(attrID);
        }

        @Override
        protected void onPostExecute(Attraction attr2) {
            if (attr2 == null) {
                Log.e("", "LoadAttrAsync.onPostExecute: Attraction was null!");
            } else {
                attr = attr2;
                Picasso.with(AttractionDetailViewActivity.this).load(attr2.getImage()).into(attrImage);
                attrName.setText(attr2.getName());
                attrRatingbar.setRating((float) attr2.getAverageReview());
                DecimalFormat df = new DecimalFormat("#.#");
                attrAvgRating.setText(df.format(attr2.getAverageReview()));

                //waiting times text
                currentWait.setText(df.format(attr2.getCurrentWaitingTime()));
                todayWait.setText(df.format(attr2.getAverageTodayWaitingTime()));
                alltimeWait.setText(df.format(attr2.getAverageWaitingTime()));

                //SUPER ALOGRITHMUS ZUR BERECHNUNG WANN WARTEZEIT GRÜN/GELB/ROT
                //GRÜN: Zeit < 70% Gesamtdurchschnitt
                //GELB: 70% Gesamtdurchschnitt < Zeit < 130% Gesamtdurchschnitt
                //ROT: 130% Gesamtdurchschnitt < Zeit

                //gesamtdurchschnitt immer gelb
                alltimeWait.setBackgroundColor(Color.YELLOW);
                //heute-durchschnitt
                if(attr.getAverageTodayWaitingTime() < attr.getAverageWaitingTime()*0.7){
                    todayWait.setBackgroundColor(Color.GREEN);
                } else if(attr.getAverageTodayWaitingTime() > attr.getAverageWaitingTime()*1.3){
                    todayWait.setBackgroundColor(Color.RED);
                } else {
                    todayWait.setBackgroundColor(Color.YELLOW);
                }
                //aktuell (letzte 3) durchschnitt
                if(attr.getCurrentWaitingTime() < attr.getAverageWaitingTime()*0.7){
                    currentWait.setBackgroundColor(Color.GREEN);
                } else if(attr.getCurrentWaitingTime() > attr.getAverageWaitingTime()*1.3){
                    currentWait.setBackgroundColor(Color.RED);
                } else {
                    currentWait.setBackgroundColor(Color.YELLOW);
                }




                //TODO barchart


            }
            AttractionDetailViewActivity.this.progressBar.hide();
        }
    }


}

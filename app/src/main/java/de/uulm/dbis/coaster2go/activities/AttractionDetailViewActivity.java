package de.uulm.dbis.coaster2go.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.Attraction;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.JsonManager;
import de.uulm.dbis.coaster2go.data.WaitingTime;


public class AttractionDetailViewActivity extends BaseActivity {

    private static final String TAG = AttractionDetailViewActivity.class.getSimpleName();
    private String attrID;
    private String parkId;
    private Attraction attr;
    private FirebaseUser user;

    private WaitingTime wt;

    private HashMap<Integer, Integer> hashMap;

    private boolean isFave = false;

    ImageView attrImage;
    TextView attrName, attrAvgRating, labelMinutes;
    EditText enterTime;
    RatingBar attrRatingbar;
    ImageButton buttonFav, buttonInfo, buttonMap;
    Button buttonSave;
    BarChart barChart;

    TextView currentWait, todayWait, alltimeWait;
    FloatingActionButton buttonCurrentWait, buttonTodayWait, buttonAlltimeWait;

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

        currentWait = (TextView) findViewById(R.id.attr_detail_wait_current);
        todayWait = (TextView) findViewById(R.id.attr_detail_wait_today);
        alltimeWait = (TextView) findViewById(R.id.attr_detail_wait_alltime);

        buttonCurrentWait = (FloatingActionButton) findViewById(R.id.button_detail_wait_current);
        buttonTodayWait = (FloatingActionButton) findViewById(R.id.button_detail_wait_today);
        buttonAlltimeWait = (FloatingActionButton) findViewById(R.id.button_detail_wait_alltime);

        barChart = (BarChart) findViewById(R.id.attr_detail_barchart);

        enterTime = (EditText) findViewById(R.id.attr_detail_time_edit);
        labelMinutes = (TextView) findViewById(R.id.attr_detail_label_minutes);
        buttonSave = (Button) findViewById(R.id.attr_detail_button_save_time);

        // disable buttons
        buttonInfo.setEnabled(false);
        buttonMap.setEnabled(false);
        buttonFav.setEnabled(false);

        // get attraction ID from intent
        attrID = getIntent().getStringExtra("attrId");

        new LoadAttrAsync().execute();

        //check if a user is signed in
        //if so -> allowed to enter a waitingtime
        //if not -> view elements are set to invisible
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            enterTime.setVisibility(View.VISIBLE);
            labelMinutes.setVisibility(View.VISIBLE);
            buttonSave.setVisibility(View.VISIBLE);

            //checks if user is allowed to enter a new waitingtime
            //if so -> gui elements enabled
            //if not -> gui elements disabled
            new CheckWaitingtimeAllowedAsync().execute();

        } else {
            // No user is signed in
            enterTime.setVisibility(View.INVISIBLE);
            labelMinutes.setVisibility(View.INVISIBLE);
            buttonSave.setVisibility(View.INVISIBLE);
        }


        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetFaveAsync().execute();
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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO check for illegal character in entered time

                wt = new WaitingTime(attrID, user.getDisplayName(), user.getUid(),
                        Integer.parseInt(enterTime.getText().toString()));

                //create a new waiting time with the entered minutes
                //then set the gui elements disabled
                new CreateWaitingTimeAsync().execute();
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttractionDetailViewActivity.this, MapViewActivity.class);
                intent.putExtra("lon", attr.getLon());
                intent.putExtra("lat", attr.getLat());
                intent.putExtra("name", attr.getName());
                startActivity(intent);
            }
        });
    }

    public void goToWaitingTimeOverview(View view) {
        Intent intent = new Intent(this, WaitingTimesActivity.class);
        intent.putExtra("attrId", attrID);
        startActivity(intent);
    }

    public void showAttrRatings(View view) {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("reviewedId", attrID);
        intent.putExtra("reviewedName", attr.getName());
        intent.putExtra("isAttraction", true);
        startActivity(intent);
    }

    public class LoadAttrAsync extends AsyncTask<Void, Void, Attraction> {

        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected Attraction doInBackground(Void... params) {
            //Load offline data because it got updated right before in the Attraction Overview:
            //return new AzureDBManager(AttractionDetailViewActivity.this).getAttractionById(attrID);
            return new JsonManager(AttractionDetailViewActivity.this).getAttractionById(attrID);
        }

        @Override
        protected void onPostExecute(Attraction attr2) {
            if (attr2 == null) {
                Log.e("", "LoadAttrAsync.onPostExecute: Attraction was null!");
            } else {
                attr = attr2;
                parkId = attr.getParkId();
                //fave
                new LoadFaveAsync().execute();
                //bar chart
                new LoadBarChartDataAsync().execute();

                Picasso.with(AttractionDetailViewActivity.this).load(attr2.getImage()).into(attrImage);
                attrName.setText(attr2.getName());
                attrRatingbar.setRating((float) attr2.getAverageReview());
                DecimalFormat df = new DecimalFormat("#.#");
                attrAvgRating.setText(df.format(attr2.getAverageReview()));

                // enable buttons
                buttonInfo.setEnabled(true);
                buttonMap.setEnabled(true);
                buttonFav.setEnabled(true);

                //waiting times text
                currentWait.setText(df.format(attr2.getCurrentWaitingTime()));
                todayWait.setText(df.format(attr2.getAverageTodayWaitingTime()));
                alltimeWait.setText(df.format(attr2.getAverageWaitingTime()));

                //SUPER ALOGRITHMUS ZUR BERECHNUNG WANN WARTEZEIT GRÜN/GELB/ROT
                //GRÜN: Zeit < 70% Gesamtdurchschnitt
                //GELB: 70% Gesamtdurchschnitt < Zeit < 130% Gesamtdurchschnitt
                //ROT: 130% Gesamtdurchschnitt < Zeit

                //gesamtdurchschnitt immer gelb
                buttonAlltimeWait.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                //heute-durchschnitt
                if(attr.getAverageTodayWaitingTime() < attr.getAverageWaitingTime()*0.7 || attr.getAverageTodayWaitingTime() <= 10){
                    buttonTodayWait.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    todayWait.setTextColor(Color.WHITE); //Black or white text color for green background?
                } else if(attr.getAverageTodayWaitingTime() > attr.getAverageWaitingTime()*1.3 || attr.getAverageTodayWaitingTime() >= 90){
                    buttonTodayWait.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    todayWait.setTextColor(Color.WHITE);
                } else {
                    buttonTodayWait.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                }
                //aktuell (letzte 3) durchschnitt
                if(attr.getCurrentWaitingTime() < attr.getAverageWaitingTime()*0.7 || attr.getCurrentWaitingTime() <= 10){
                    buttonCurrentWait.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    currentWait.setTextColor(Color.WHITE); //Black or white text color for green background?
                } else if(attr.getCurrentWaitingTime() > attr.getAverageWaitingTime()*1.3 || attr.getCurrentWaitingTime() >= 90){
                    buttonCurrentWait.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    currentWait.setTextColor(Color.WHITE);
                } else {
                    buttonCurrentWait.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                }
            }
            AttractionDetailViewActivity.this.progressBar.hide();
        }
    }

    public class LoadBarChartDataAsync extends AsyncTask<Void, Void, HashMap<Integer, Integer>> {

        @Override
        protected void onPreExecute() {AttractionDetailViewActivity.this.progressBar.show();
        }

        @Override
        protected HashMap<Integer, Integer> doInBackground(Void... params) {
            return new AzureDBManager(AttractionDetailViewActivity.this).waitTimeHourStatistic(attrID);
        }

        @Override
        protected void onPostExecute(HashMap<Integer, Integer> hMap) {
            if (hMap == null) {
                Log.e("", "LoadBarChartAsync.onPostExecute: barChartData was null!");
            } else {
                hashMap = hMap;

                ArrayList<BarEntry> yVals1 = new ArrayList<>();
                yVals1.add(new BarEntry(0, 0));
                for(int i=8; i<21; i++){
                    yVals1.add(new BarEntry(i-6, hashMap.get(i)));
                }
                yVals1.add(new BarEntry(17, 0));

                BarDataSet barDataSet = new BarDataSet(yVals1, "values");

                ArrayList<String> labels = new ArrayList<>();
                for(int i=6; i<23; i++){
                    labels.add("" + i + " Uhr");
                }

                BarData barData = new BarData(barDataSet);

                barChart.setData(barData);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate();
            }
            progressBar.hide();
        }
    }

    public class CheckWaitingtimeAllowedAsync extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {AttractionDetailViewActivity.this.progressBar.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return new AzureDBManager(AttractionDetailViewActivity.this).isCreateWaitingTimeAllowed(attrID, user.getUid());
        }

        @Override
        protected void onPostExecute(Boolean b) {
            // check if user has not entered a waiting time within the last our

            if (b == true){
                //allowed to enter a waiting time
            } else {
                enterTime.setText("Letzter Eintrag vor unter 1h");
                enterTime.setEnabled(false);
                buttonSave.setEnabled(false);
            }

            progressBar.hide();
        }
    }

    public class CreateWaitingTimeAsync extends AsyncTask<Void, Void, WaitingTime> {

        @Override
        protected void onPreExecute() {AttractionDetailViewActivity.this.progressBar.show();
        }

        @Override
        protected WaitingTime doInBackground(Void... params) {
            return new AzureDBManager(AttractionDetailViewActivity.this).createWaitingTime(wt);
        }

        @Override
        protected void onPostExecute(WaitingTime w) {
            if (w == null) {
                Log.e("", "CreateWaitingTimeAsync.onPostExecute: w was null!");
            } else {
                Toast.makeText(AttractionDetailViewActivity.this, "Wartezeit eingetragen", Toast.LENGTH_SHORT).show();

                enterTime.setEnabled(false);
                buttonSave.setEnabled(false);
            }

            progressBar.hide();

            recreate();
        }
    }

    public class LoadFaveAsync extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return new JsonManager(AttractionDetailViewActivity.this).isAttractionFavorite(parkId, attrID);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b){
                buttonFav.setImageResource(R.drawable.ic_favorite_red_24dp);
                isFave = true;
            }else{
                buttonFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                isFave = false;
            }
        }
    }

    public class SetFaveAsync extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if(isFave){
                return new JsonManager(AttractionDetailViewActivity.this).deleteAttractionFromFavorites(parkId, attrID);
            }else{
                return new JsonManager(AttractionDetailViewActivity.this).putIntoAttractionFavorites(parkId, attrID);
            }

        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b){
                if(!isFave){
                    buttonFav.setImageResource(R.drawable.ic_favorite_red_24dp);
                    isFave = true;
                }else{
                    buttonFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    isFave = false;
                }
            }else{
                Log.e("", "Put Attraction into favorites didn't work!");
            }
        }
    }


}

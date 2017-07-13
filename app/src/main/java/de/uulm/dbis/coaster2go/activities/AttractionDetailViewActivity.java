package de.uulm.dbis.coaster2go.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.controller.InputFilterMinMax;
import de.uulm.dbis.coaster2go.data.Attraction;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.JsonManager;
import de.uulm.dbis.coaster2go.data.WaitingTime;


public class AttractionDetailViewActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * the gps permission request code
     */
    private static final int RC_PERM_GPS = 502;

    private static final String TAG = AttractionDetailViewActivity.class.getSimpleName();

    /**
     * The maximum distance (in meters) the user is allowed to be away from an attraction
     * while still being able to enter a waiting time
     */
    private static final float MAX_DISTANCE_TO_ATTR_METERS = 250 * 1000; //Enough km to rate the EP from here

    /**
     * The maximum number of minutes a user is allowed to enter
     */
    private static final int MAX_NUMBER_MINUTES = 999;
    public final int millisecondsOfADay = 86400000;
    private String attrID;
    private String parkId;
    private Attraction attr;
    private FirebaseUser user;
    private static boolean isParkAdmin;
    private static String parkAdminId;

    /**
     * caution: may be null! (is null at the beginning)
     */
    private Location currentUserLocation;

    // GoogleApiClient for location service
    private GoogleApiClient mGoogleApiClient;

    private HashMap<Integer, Integer> hashMap;

    private boolean isFave = false;

    ImageView attrImage;
    TextView attrName, attrAvgRating, labelMinutes, dateToday;
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
        dateToday = (TextView) findViewById(R.id.attr_detail_today_date);

        buttonCurrentWait = (FloatingActionButton) findViewById(R.id.button_detail_wait_current);
        buttonTodayWait = (FloatingActionButton) findViewById(R.id.button_detail_wait_today);
        buttonAlltimeWait = (FloatingActionButton) findViewById(R.id.button_detail_wait_alltime);

        buttonCurrentWait.setCompatElevation(0);
        buttonTodayWait.setCompatElevation(0);
        buttonAlltimeWait.setCompatElevation(0);

        barChart = (BarChart) findViewById(R.id.attr_detail_barchart);

        enterTime = (EditText) findViewById(R.id.attr_detail_time_edit);
        labelMinutes = (TextView) findViewById(R.id.attr_detail_label_minutes);
        buttonSave = (Button) findViewById(R.id.attr_detail_button_save_time);

        enterTime.setFilters(new InputFilter[]{ new InputFilterMinMax(0, MAX_NUMBER_MINUTES)});

        // disable buttons
        buttonSave.setEnabled(false);
        buttonInfo.setEnabled(false);
        buttonMap.setEnabled(false);
        buttonFav.setEnabled(false);

        // get attraction ID from intent
        attrID = getIntent().getStringExtra("attrId");
        isParkAdmin = getIntent().getBooleanExtra("isParkAdmin", false);
        parkAdminId = getIntent().getStringExtra("parkAdminId");

        new LoadAttrAsync().execute();

        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetFaveAsync().execute();
            }
        });

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog for showing the attraction description
                DialogFragment infoDialog = new AttractionInfoDialogFragment();
                Bundle infoArgs = new Bundle();
                infoArgs.putString("attrName", attr.getName());
                infoArgs.putString("attrDesc", attr.getDescription());
                String[] attrTypes = TextUtils.split(attr.getType(), ",");
                infoArgs.putStringArray("attrTypes", attrTypes);
                infoDialog.setArguments(infoArgs);
                infoDialog.show(getSupportFragmentManager(), "attrInfoDialog");
            }
        });

        buttonSave.setOnClickListener(new CreateWaitingTimeClickListener());

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

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        attrRatingbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showAttrRatings(view);
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private class CreateWaitingTimeClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {

                if (currentUserLocation == null || attr == null) {
                    return;
                }

                Location attrLocation = new Location("");
                attrLocation.setLatitude(attr.getLat());
                attrLocation.setLongitude(attr.getLon());

                if (attrLocation.distanceTo(currentUserLocation) > MAX_DISTANCE_TO_ATTR_METERS) {
                    Snackbar.make(findViewById(R.id.coordinatorLayout_AttrDetailview),
                            "Zu weit von Attraktion entfernt", Snackbar.LENGTH_SHORT).show();
                    enterTime.setEnabled(false);
                    buttonSave.setEnabled(false);
                } else {
                    WaitingTime wt = new WaitingTime(attrID, user.getDisplayName(), user.getUid(),
                            Integer.parseInt(enterTime.getText().toString()));

                    new CreateWaitingTimeAsync().execute(wt);

                    enterTime.setEnabled(false);
                    buttonSave.setEnabled(false);
                }


            } catch (NumberFormatException nfe) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_AttrDetailview),
                        "Geben Sie eine gültige Zahl ein", Snackbar.LENGTH_SHORT).show();
            }
        }
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
        intent.putExtra("parkAdminId", parkAdminId);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                    "Standortberechtigung nicht erteilt",
                    Snackbar.LENGTH_SHORT);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERM_GPS);

            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // update the location
        currentUserLocation = lastLocation;

    }

    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                "Standort: Verbindung unterbrochen",
                Snackbar.LENGTH_SHORT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                "Standort konnte nicht ermittelt werden",
                Snackbar.LENGTH_SHORT);
    }

    /**
     * updates the GUI to match the location (i.e. distance too big?)
     */
    private void updateLocationGUI(boolean lastEntryAllowed) {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.d(TAG, "------ updateLocationGUI: gpsEnabled: " + gpsEnabled);

        // check if GPS Service is enabled
        if (!gpsEnabled) {
            enterTime.setHint("GPS nicht verfügbar");
            enterTime.setEnabled(false);
            buttonSave.setEnabled(false);
            return;
        }


        if (lastEntryAllowed) {
            if (currentUserLocation != null) {
                Location attrLocation = new Location("");
                attrLocation.setLatitude(attr.getLat());
                attrLocation.setLongitude(attr.getLon());

                if (attrLocation.distanceTo(currentUserLocation) > MAX_DISTANCE_TO_ATTR_METERS) {
                    // too far away
                    enterTime.setHint("Zu weit entfernt");
                    enterTime.setEnabled(false);
                    buttonSave.setEnabled(false);
                } else {

                    enterTime.setHint("Minuten eingeben");
                    enterTime.setEnabled(true);
                    buttonSave.setEnabled(true);
                }

                Log.d(TAG, "updateLocationGUI: ACCURACY " + currentUserLocation.getAccuracy() + "m");
            }

        } else {
            if(user == null){
                enterTime.setHint("Bitte zuerst einloggen");
            }else{
                enterTime.setHint("Letzter Eintrag vor unter 1h");
            }
            enterTime.setEnabled(false);
            buttonSave.setEnabled(false);
        }

    }

    private class LoadAttrAsync extends AsyncTask<Void, Void, Attraction> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Attraction doInBackground(Void... params) {
            //Load offline data because it got updated right before in the Attraction Overview:
            return new AzureDBManager(AttractionDetailViewActivity.this).getAttractionById(attrID);
            //return new JsonManager(AttractionDetailViewActivity.this).getAttractionById(attrID);
        }

        @Override
        protected void onPostExecute(Attraction attr2) {
            if (attr2 == null) {
                Log.e("", "LoadAttrAsync.onPostExecute: Attraction was null!");
            } else {
                attr = attr2;

                parkId = attr.getParkId();

                setTitle(attr.getName());

                //fave
                new LoadFaveAsync().execute();

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
                    enterTime.setVisibility(View.VISIBLE);
                    enterTime.setHint("Bitte einloggen");
                    enterTime.setEnabled(false);
                    labelMinutes.setVisibility(View.INVISIBLE);
                    buttonSave.setVisibility(View.VISIBLE);
                    buttonSave.setEnabled(false);
                }

                //bar chart
                new LoadBarChartDataAsync().execute();

                if (attr2.getImage() != null && !attr2.getImage().isEmpty()) {
                    Picasso.with(AttractionDetailViewActivity.this).load(attr2.getImage()).into(attrImage);
                } else {
                    attrImage.setImageDrawable(ContextCompat.getDrawable(
                            AttractionDetailViewActivity.this, R.drawable.ic_theme_park));
                }
                attrName.setText(attr2.getName());
                attrName.setSelected(true);
                attrRatingbar.setRating((float) attr2.getAverageReview());
                DecimalFormat df = new DecimalFormat("#.#");
                attrAvgRating.setText("⌀ " + df.format(attr2.getAverageReview()));

                // enable buttons
                buttonSave.setEnabled(true);
                buttonInfo.setEnabled(true);
                buttonMap.setEnabled(true);
                buttonFav.setEnabled(true);

                //waiting times text
                currentWait.setText(df.format(attr2.getCurrentWaitingTime()));
                todayWait.setText(df.format(attr2.getAverageTodayWaitingTime()));
                alltimeWait.setText(df.format(attr2.getAverageWaitingTime()));

                //TAGES BESTIMMUNG LAST UPDATED
                int lastYear = attr.getLastUpdated().getYear();
                int lastMonth = attr.getLastUpdated().getMonth();
                Date now = new Date();
                if((now.getYear() == lastYear) &&  (now.getMonth() == lastMonth)){
                    if(DateUtils.isToday(attr.getLastUpdated().getTime())){
                        //Date lastUpdated = attr.getLastUpdated();
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
                        //String dateString = dateFormat.format(lastUpdated);
                        dateToday.setText("Heute");
                    }else if(now.getTime() - attr.getLastUpdated().getTime() < 2*millisecondsOfADay){
                        dateToday.setText("Gestern");
                    }else if(now.getTime() - attr.getLastUpdated().getTime() < 3*millisecondsOfADay){
                        dateToday.setText("Vorgestern");
                    }else{
                        Date lastUpdated = attr.getLastUpdated();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
                        String dateString = dateFormat.format(lastUpdated);
                        dateToday.setText(dateString);
                    }
                }else {
                    Date lastUpdated = attr.getLastUpdated();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
                    String dateString = dateFormat.format(lastUpdated);
                    dateToday.setText(dateString);
                }

                //SUPER ALOGRITHMUS ZUR BERECHNUNG WANN WARTEZEIT GRÜN/GELB/ROT
                //GRÜN: Zeit < 70% Gesamtdurchschnitt
                //GELB: 70% Gesamtdurchschnitt < Zeit < 130% Gesamtdurchschnitt
                //ROT: 130% Gesamtdurchschnitt < Zeit

                //gesamtdurchschnitt immer gelb
                buttonAlltimeWait.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,248,72)));
                //heute-durchschnitt
                if(attr.getAverageTodayWaitingTime() < attr.getAverageWaitingTime()*0.7 || attr.getAverageTodayWaitingTime() <= 10){
                    buttonTodayWait.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(66,232,72)));
                    todayWait.setTextColor(Color.WHITE); //Black or white text color for green background?
                } else if(attr.getAverageTodayWaitingTime() > attr.getAverageWaitingTime()*1.3 || attr.getAverageTodayWaitingTime() >= 90){
                    buttonTodayWait.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,74,58)));
                    todayWait.setTextColor(Color.WHITE);
                } else {
                    buttonTodayWait.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,248,72)));
                    todayWait.setTextColor(Color.BLACK);
                }
                //aktuell (letzte 3) durchschnitt
                if(attr.getCurrentWaitingTime() < attr.getAverageWaitingTime()*0.7 || attr.getCurrentWaitingTime() <= 10){
                    buttonCurrentWait.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(66,232,72)));
                    currentWait.setTextColor(Color.WHITE); //Black or white text color for green background?
                } else if(attr.getCurrentWaitingTime() > attr.getAverageWaitingTime()*1.3 || attr.getCurrentWaitingTime() >= 90){
                    buttonCurrentWait.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,74,58)));
                    currentWait.setTextColor(Color.WHITE);
                } else {
                    buttonCurrentWait.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,248,72)));
                    currentWait.setTextColor(Color.BLACK);
                }
            }
            //AttractionDetailViewActivity.this.progressBar.setVisibility(View.GONE);
        }
    }

    public class LoadBarChartDataAsync extends AsyncTask<Void, Void, HashMap<Integer, Integer>> {

        @Override
        protected void onPreExecute() {AttractionDetailViewActivity.this.progressBar.setVisibility(View.VISIBLE);
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
                    yVals1.add(new BarEntry(i-7, hashMap.get(i)));
                }
                yVals1.add(new BarEntry(14, 0));

                BarDataSet barDataSet = new BarDataSet(yVals1, "values");

                ArrayList<String> labels = new ArrayList<>();
                for(int i=7; i<22; i++){
                    labels.add("" + i + " Uhr");
                }

                BarData barData = new BarData(barDataSet);

                barChart.setData(barData);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

                Description d = new Description();
                //falls wir doch noch eine Beschreibung wollten
                //d.setText("Durchschnittliche Wartezeit in Minuten");
                d.setEnabled(false);
                barChart.setDescription(d);

                Legend l = barChart.getLegend();
                l.setEnabled(false);

                barChart.invalidate();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public class CheckWaitingtimeAllowedAsync extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {AttractionDetailViewActivity.this.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return new AzureDBManager(AttractionDetailViewActivity.this).isCreateWaitingTimeAllowed(attrID, user.getUid());
        }

        @Override
        protected void onPostExecute(Boolean b) {
            // check if user has not entered a waiting time within the last our

            updateLocationGUI(b);


            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * create a new waiting time with the entered minutes,
     * then set the gui elements disabled
     */
    private class CreateWaitingTimeAsync extends AsyncTask<WaitingTime, Void, WaitingTime> {

        @Override
        protected void onPreExecute() {
            AttractionDetailViewActivity.this.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected WaitingTime doInBackground(WaitingTime... params) {
            WaitingTime wt = params[0];
            if (wt == null) {
                Log.e(TAG, "CreateWaitingTimeAsync.doInBackground: wt was null");
                return null;
            }
            return new AzureDBManager(AttractionDetailViewActivity.this).createWaitingTime(wt);
        }

        @Override
        protected void onPostExecute(WaitingTime wt) {
            if (wt == null) {
                Log.e(TAG, "CreateWaitingTimeAsync.onPostExecute: wt was null!");
                Snackbar.make(findViewById(R.id.coordinatorLayout_AttrDetailview),
                        "Wartezeit konnte nicht eingetragen werden", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(findViewById(R.id.coordinatorLayout_AttrDetailview),
                        "Wartezeit eingetragen", Snackbar.LENGTH_LONG).show();

                enterTime.setEnabled(false);
                buttonSave.setEnabled(false);
            }

            progressBar.setVisibility(View.GONE);

            new LoadAttrAsync().execute();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == RC_PERM_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                onConnected(null);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}

package de.uulm.dbis.coaster2go.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Park;

public class EditParkActivity extends BaseActivity {

    static final int PLACE_PICKER_REQUEST = 3461;

    EditText editTextParkName;
    EditText editTextParkLocationName;
    EditText editTextParkLat;
    EditText editTextParkLon;
    EditText editTextParkDescription;
    ImageView imageViewPark;

    String parkImageUrl;
    String parkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_park);

        parkId = getIntent().getStringExtra("parkId");

        editTextParkName = (EditText) findViewById(R.id.editTextParkName);
        editTextParkLocationName = (EditText) findViewById(R.id.editTextParkLocationName);
        editTextParkLat = (EditText) findViewById(R.id.editTextParkLat);
        editTextParkLon = (EditText) findViewById(R.id.editTextParkLon);
        editTextParkDescription = (EditText) findViewById(R.id.editTextParkDescription);
        imageViewPark = (ImageView) findViewById(R.id.imageViewEditPark);

        if (parkId != null) {
            new LoadParkTask().execute();
        }
    }

    /**
     * gets all the values from the ui, creates a park object
     * and saves it in the database
     */
    public void savePark(View view) {
        String name = editTextParkName.getText().toString();
        String location = editTextParkLocationName.getText().toString();
        String descr = editTextParkDescription.getText().toString();
        String lat = editTextParkLat.getText().toString();
        String lon = editTextParkLon.getText().toString();

        new SaveParkTask().execute(name, location, descr, lat, lon);
    }

    /**
     * sends a place picker intent. the picked location is received in onActivityResult
     */
    public void startPlacePicker(View view) {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void addParkImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_image);
        final EditText editTextParkImageUrl = new EditText(this);
        editTextParkImageUrl.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

        if (parkImageUrl != null) {
            editTextParkImageUrl.setText(parkImageUrl);
        }

        builder.setView(editTextParkImageUrl);

        // Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parkImageUrl = editTextParkImageUrl.getText().toString();
                if (parkImageUrl.isEmpty()) {
                    Picasso.with(EditParkActivity.this)
                            .load(R.mipmap.ic_launcher).into(imageViewPark);
                } else {
                    Picasso.with(EditParkActivity.this).load(parkImageUrl).into(imageViewPark);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                editTextParkLat.setText(String.valueOf(place.getLatLng().latitude));
                editTextParkLon.setText(String.valueOf(place.getLatLng().longitude));
                editTextParkLocationName.setText(String.valueOf(place.getName()));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class LoadParkTask extends AsyncTask<Void, Void, Park> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Park doInBackground(Void... params) {
            if (parkId == null) return null;
            return new AzureDBManager(EditParkActivity.this).getParkById(parkId);
        }

        @Override
        protected void onPostExecute(Park park) {
            if (park != null) {
                if (!(park.getImage() == null || park.getImage().isEmpty())) {
                    Picasso.with(EditParkActivity.this).load(park.getImage()).into(imageViewPark);
                    parkImageUrl = park.getImage();
                } else {
                    Picasso.with(EditParkActivity.this).load(R.mipmap.ic_launcher).into(imageViewPark);
                    parkImageUrl = "";
                }
                editTextParkName.setText(park.getName());
                editTextParkLocationName.setText(park.getLocation());
                editTextParkLat.setText(String.valueOf(park.getLat()));
                editTextParkLon.setText(String.valueOf(park.getLon()));
                editTextParkDescription.setText(park.getDescription());
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private class SaveParkTask extends AsyncTask<String, Void, Park> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * @param params name, location, descr, lat, lon
         */
        @Override
        protected Park doInBackground(String... params) {

            String name = params[0];
            String location = params[1];
            String descr = params[2];
            double lat = 0;
            double lon = 0;
            try {
                lat = Double.parseDouble(params[3]);
                lon = Double.parseDouble(params[4]);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditPark),
                        "Überprüfen Sie die Koordinaten", Snackbar.LENGTH_SHORT).show();
                cancel(true);
            }

            AzureDBManager dbManager = new AzureDBManager(EditParkActivity.this);

            Park park = new Park(name, location, descr, lat, lon, parkImageUrl, 0, 0, user.getUid());

            // did the park already exist?
            if (parkId == null) {
                return dbManager.createPark(park);
            } else {
                park.setId(parkId);
                return dbManager.updatePark(park);
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Park resultPark) {
            progressBar.setVisibility(View.GONE);
            if (resultPark == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditPark),
                        "Park konnte nicht gespeichert werden",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(EditParkActivity.this, ParkOverviewActivity.class);
                startActivity(intent);
            }
        }
    }
}

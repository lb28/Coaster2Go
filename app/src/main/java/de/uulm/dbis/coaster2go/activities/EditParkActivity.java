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
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_park);

        editTextParkName = (EditText) findViewById(R.id.editTextParkName);
        editTextParkLocationName = (EditText) findViewById(R.id.editTextParkLocationName);
        editTextParkLat = (EditText) findViewById(R.id.editTextParkLat);
        editTextParkLon = (EditText) findViewById(R.id.editTextParkLon);
        editTextParkDescription = (EditText) findViewById(R.id.editTextParkDescription);
        imageViewPark = (ImageView) findViewById(R.id.imageViewEditPark);

        // triggers placeholder
        Picasso.with(EditParkActivity.this).load(parkImageUrl).into(imageViewPark);
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

        if (parkImageUrl == null) {
            parkImageUrl = "";
        }

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
        builder.setTitle(R.string.add_park_image);
        final EditText editTextParkImageUrl = new EditText(this);
        editTextParkImageUrl.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        builder.setView(editTextParkImageUrl);

        // Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parkImageUrl = editTextParkImageUrl.getText().toString();
                if (parkImageUrl.isEmpty()) {
                    parkImageUrl = null;
                }
                Picasso.with(EditParkActivity.this).load(parkImageUrl).into(imageViewPark);
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
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                editTextParkLat.setText(String.valueOf(place.getLatLng().latitude));
                editTextParkLon.setText(String.valueOf(place.getLatLng().longitude));
                editTextParkLocationName.setText(String.valueOf(place.getName()));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SaveParkTask extends AsyncTask<String, Void, Park> {

        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        /**
         * @param params name, location, descr, lat, lon
         */
        @Override
        protected Park doInBackground(String... params) {

            String name = params[0];
            String location = params[1];
            String descr = params[2];
            double lat = Double.parseDouble(params[3]);
            double lon = Double.parseDouble(params[4]);

            Park park = new Park(name, location, descr, lat, lon, parkImageUrl, 0, 0, user.getUid());

            AzureDBManager dbManager = new AzureDBManager(EditParkActivity.this);
            return dbManager.createPark(park);
        }

        @Override
        protected void onPostExecute(Park resultPark) {
            progressBar.hide();
            if (resultPark == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditPark),
                        "Park konnte nicht gespeichert werden",
                        Snackbar.LENGTH_SHORT);
            } else {
                Intent intent = new Intent(EditParkActivity.this, ParkDetailViewActivity.class);
                intent.putExtra("parkId", resultPark.getId());
                startActivity(intent);
            }
        }
    }
}

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
import de.uulm.dbis.coaster2go.data.Attraction;
import de.uulm.dbis.coaster2go.data.AzureDBManager;

public class EditAttractionActivity extends BaseActivity {
    static final int PLACE_PICKER_REQUEST = 8;

    EditText editTextAttrName;
    EditText editTextAttrTypes;
    EditText editTextAttrLat;
    EditText editTextAttrLon;
    EditText editTextAttrDescription;
    ImageView imageViewAttr;

    String attrImageUrl;
    String attrId;
    String parkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attraction);

        attrId = getIntent().getStringExtra("attrId");
        parkId = getIntent().getStringExtra("parkId");

        editTextAttrName = (EditText) findViewById(R.id.editTextAttrName);
        editTextAttrTypes = (EditText) findViewById(R.id.editTextAttrTypes);
        editTextAttrLat = (EditText) findViewById(R.id.editTextAttrLat);
        editTextAttrLon = (EditText) findViewById(R.id.editTextAttrLon);
        editTextAttrDescription = (EditText) findViewById(R.id.editTextAttrDescription);
        imageViewAttr = (ImageView) findViewById(R.id.imageViewEditAttr);

        if (attrId != null) {
            new LoadAttrTask().execute();
        }
    }

    /**
     * gets all the values from the ui, creates a park object
     * and saves it in the database
     */
    public void saveAttraction(View view) {
        String name = editTextAttrName.getText().toString();
        String types = editTextAttrTypes.getText().toString();
        String descr = editTextAttrDescription.getText().toString();
        String lat = editTextAttrLat.getText().toString();
        String lon = editTextAttrLon.getText().toString();

        new SaveAttrTask().execute(name, types, descr, lat, lon);
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

    public void addAttrImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_image);
        final EditText editTextAttrImageUrl = new EditText(this);
        editTextAttrImageUrl.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

        if (attrImageUrl != null) {
            editTextAttrImageUrl.setText(attrImageUrl);
        }

        builder.setView(editTextAttrImageUrl);

        // Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attrImageUrl = editTextAttrImageUrl.getText().toString();
                if (attrImageUrl.isEmpty()) {
                    Picasso.with(EditAttractionActivity.this)
                            .load(R.mipmap.ic_launcher).into(imageViewAttr);
                } else {
                    Picasso.with(EditAttractionActivity.this).load(attrImageUrl).into(imageViewAttr);
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

                editTextAttrName.setText(String.valueOf(place.getName()));
                editTextAttrLat.setText(String.valueOf(place.getLatLng().latitude));
                editTextAttrLon.setText(String.valueOf(place.getLatLng().longitude));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class LoadAttrTask extends AsyncTask<Void, Void, Attraction> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Attraction doInBackground(Void... params) {
            if (attrId == null) return null;
            return new AzureDBManager(EditAttractionActivity.this).getAttractionById(attrId);
        }

        @Override
        protected void onPostExecute(Attraction attr) {
            if (attr != null) {
                if (!(attr.getImage() == null || attr.getImage().isEmpty())) {
                    Picasso.with(EditAttractionActivity.this).load(attr.getImage()).into(imageViewAttr);
                    attrImageUrl = attr.getImage();
                } else {
                    Picasso.with(EditAttractionActivity.this).load(R.mipmap.ic_launcher).into(imageViewAttr);
                    attrImageUrl = "";
                }
                editTextAttrName.setText(attr.getName());
                editTextAttrTypes.setText(attr.getType());
                editTextAttrLat.setText(String.valueOf(attr.getLat()));
                editTextAttrLon.setText(String.valueOf(attr.getLon()));
                editTextAttrDescription.setText(attr.getDescription());
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private class SaveAttrTask extends AsyncTask<String, Void, Attraction> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * @param params name, types, descr, lat, lon
         */
        @Override
        protected Attraction doInBackground(String... params) {

            String name = params[0];
            String types = params[1];
            String descr = params[2];
            double lat = 0;
            double lon = 0;
            try {
                lat = Double.parseDouble(params[3]);
                lon = Double.parseDouble(params[4]);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditAttr),
                        "Überprüfen Sie die Koordinaten", Snackbar.LENGTH_SHORT).show();
                cancel(true);
            }

            AzureDBManager dbManager = new AzureDBManager(EditAttractionActivity.this);

            Attraction attraction = new Attraction(name, types, descr, lat, lon, attrImageUrl,
                    0, 0, 0, 0, 0, 0, 0, parkId);

            // did the attraction already exist?
            if (attrId == null) {
                return dbManager.createAttraction(attraction);
            } else {
                attraction.setId(attrId);
                return dbManager.updateAttraction(attraction);
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Attraction resultAttraction) {
            progressBar.setVisibility(View.GONE);
            if (resultAttraction == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditPark),
                        "Park konnte nicht gespeichert werden",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(EditAttractionActivity.this, ParkOverviewActivity.class);
                startActivity(intent);
            }
        }
    }
}

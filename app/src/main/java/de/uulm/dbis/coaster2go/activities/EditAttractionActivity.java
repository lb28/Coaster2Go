package de.uulm.dbis.coaster2go.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cloudinary.Cloudinary;
import com.cloudinary.ProgressCallback;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pchmn.materialchips.ChipView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.Attraction;
import de.uulm.dbis.coaster2go.data.AzureDBManager;

import static de.uulm.dbis.coaster2go.activities.AttractionOverviewActivity.ATTRACTION_TYPES;

public class EditAttractionActivity extends BaseActivity {

    private static final String TAG = "EditAttractionActivity";
    EditText editTextAttrName;
    LinearLayout chipsLayoutAttrTypes;
    EditText editTextAttrLat;
    EditText editTextAttrLon;
    EditText editTextAttrDescription;
    ImageView imageViewAttr;

    ProgressDialog progressDialog;

    String attrImageUrl;
    String attrId;
    String parkId;

    private List<ChipView> typeChips;
    private List<String> selectedTypes;

    private Attraction attraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attraction);

        attrId = getIntent().getStringExtra("attrId");
        parkId = getIntent().getStringExtra("parkId");

        editTextAttrName = (EditText) findViewById(R.id.editTextAttrName);
        chipsLayoutAttrTypes = (LinearLayout) findViewById(R.id.chipsLayoutAttrTypes);
        editTextAttrLat = (EditText) findViewById(R.id.editTextAttrLat);
        editTextAttrLon = (EditText) findViewById(R.id.editTextAttrLon);
        editTextAttrDescription = (EditText) findViewById(R.id.editTextAttrDescription);
        imageViewAttr = (ImageView) findViewById(R.id.imageViewEditAttr);

        typeChips = new ArrayList<>(ATTRACTION_TYPES.size());
        selectedTypes = new ArrayList<>();

        for (final String type : ATTRACTION_TYPES) {
            final ChipView chip = new ChipView(this);
            chip.setLabel(type);
            chip.setOnChipClicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do we have to select or deselect the chip?
                    if (selectedTypes.contains(type)) {
                        selectedTypes.remove(type);
                        chip.setChipBackgroundColor(ContextCompat.getColor(
                                EditAttractionActivity.this, R.color.colorChipViewBackground));
                    } else {
                        selectedTypes.add(type);
                        chip.setChipBackgroundColor(ContextCompat.getColor(
                                EditAttractionActivity.this, R.color.colorPrimary));
                    }
                }
            });
            typeChips.add(chip);
        }

        if (attrId == null) {
            addChips();
        } else {
            new LoadAttrTask().execute();
        }
    }

    /**
     * gets all the values from the ui, creates a park object
     * and saves it in the database
     */
    public void saveAttraction(View view) {
        String name = editTextAttrName.getText().toString();
        String typesString = TextUtils.join(",", selectedTypes);
        String descr = editTextAttrDescription.getText().toString();
        String lat = editTextAttrLat.getText().toString();
        String lon = editTextAttrLon.getText().toString();

        Log.d(TAG, "saveAttraction: types: " + typesString);

        new SaveAttrTask().execute(name, typesString, descr, lat, lon);
    }

    /**
     * sends a place picker intent. the picked location is received in onActivityResult
     */
    public void startPlacePicker(View view) {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), RC_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void addAttrImageDialog(View view) {
        String[] options = {"Foto aufnehmen", "Aus Galerie wählen", "URL eingeben"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Bild hinzufügen");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    launchCamera();
                } else if (which == 1) {
                    launchGallery();
                } else if (which == 2) {
                    launchURLImageDialog();
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

    private void launchURLImageDialog() {

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
                    imageViewAttr.setImageDrawable(ContextCompat.getDrawable(
                            EditAttractionActivity.this, R.drawable.ic_theme_park));
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

    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE);
        }
    }

    public void launchGallery() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditAttr),
                        "Berechtigung zum Lesen der Bilder benötigt",
                        Snackbar.LENGTH_LONG).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        RC_READ_EXTERNAL_STORAGE);

            }

            return;
        }

        Intent intent;
        intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, RC_PICK_IMAGE);

    }

    /**
     * adds the chips to the layout
     */
    private void addChips() {
        // add all the types to the chips layout
        for (ChipView chip : typeChips) {
            // is this type selected?
            if (selectedTypes.contains(chip.getLabel())) {
                chip.setChipBackgroundColor(ContextCompat.getColor(
                        EditAttractionActivity.this, R.color.colorPrimary));
            } else {
                chip.setChipBackgroundColor(ContextCompat.getColor(
                        EditAttractionActivity.this, R.color.colorChipViewBackground));
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 5, 10);

            chipsLayoutAttrTypes.addView(chip, layoutParams);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    Intent intent;
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");

                    startActivityForResult(intent, RC_PICK_IMAGE);


                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                if (editTextAttrName.getText().toString().isEmpty()) {
                    editTextAttrName.setText(String.valueOf(place.getName()));
                }

                editTextAttrLat.setText(String.valueOf(place.getLatLng().latitude));
                editTextAttrLon.setText(String.valueOf(place.getLatLng().longitude));
            }
        } else if (requestCode == RC_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap picture = (Bitmap) data.getExtras().get("data");

                // upload the picture with cloudinary
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                if (picture == null) {
                    Snackbar.make(findViewById(R.id.coordinatorLayout_EditAttr),
                            "Bild konnte nicht übertragen werden", Snackbar.LENGTH_LONG).show();
                    return;
                }
                picture.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                new CloudinaryUploadTask().execute((Object) bos.toByteArray());
            }
        } else if (requestCode == RC_PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                final Uri uri = data.getData();
                if (uri == null) {
                    Snackbar.make(findViewById(R.id.coordinatorLayout_EditAttr),
                            "Bild wurde nicht gefunden", Snackbar.LENGTH_LONG).show();
                    return;
                }

                File imageFile = getFileFromURI(uri);
                new CloudinaryUploadTask().execute(imageFile);
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

            attraction = attr;

            if (attr != null) {
                if (attr.getImage() == null || attr.getImage().isEmpty()) {
                    imageViewAttr.setImageDrawable(ContextCompat.getDrawable(
                            EditAttractionActivity.this, R.drawable.ic_theme_park));
                    attrImageUrl = "";
                } else {
                    Picasso.with(EditAttractionActivity.this).load(attr.getImage()).into(imageViewAttr);
                    attrImageUrl = attr.getImage();
                }
                editTextAttrName.setText(attr.getName());
                editTextAttrLat.setText(String.valueOf(attr.getLat()));
                editTextAttrLon.setText(String.valueOf(attr.getLon()));
                editTextAttrDescription.setText(attr.getDescription());

                List<String> selectedTypesRaw = Arrays.asList(TextUtils.split(attr.getType(), ","));
                for (String potentialType : selectedTypesRaw) {
                    // only add known types, rest will be discarded when saving
                    if (ATTRACTION_TYPES.contains(potentialType)) {
                        selectedTypes.add(potentialType);
                    }
                }

                Log.d(TAG, "onPostExecute: type string: \"" + attr.getType() + "\"");
                Log.d(TAG, "onPostExecute: attraction has type " + Arrays.toString(selectedTypes.toArray()));
                addChips();

            }
            findViewById(R.id.buttonSaveAttr).setEnabled(true);
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

            // did the attraction already exist?
            if (attrId == null) {
                // create a new attraction
                attraction = new Attraction(name, types, descr, lat, lon, attrImageUrl,
                        0, 0, 0, 0, 0, 0, 0, parkId);
                return dbManager.createAttraction(attraction);
            } else {
                // update attraction
                attraction.setName(name);
                attraction.setType(types);
                attraction.setDescription(descr);
                attraction.setLat(lat);
                attraction.setLon(lon);
                attraction.setImage(attrImageUrl);
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
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditAttr),
                        "Park konnte nicht gespeichert werden",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

    private class CloudinaryUploadTask extends AsyncTask<Object, Integer, Map> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditAttractionActivity.this);
            progressDialog.setTitle("Bild wird hochgeladen");
            progressDialog.setMessage("asdasd");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.show();
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancel(true);
                }
            });
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });
        }

        @Override
        protected Map doInBackground(Object... params) {
            Cloudinary cloudinary = new Cloudinary(getString(R.string.cloudinary_conn_url));
            Map uploadResult = null;

            try {
                Object file = params[0];
                uploadResult = cloudinary.uploader().upload(
                        file,
                        ObjectUtils.asMap("transformation",
                        new Transformation().width(2000).height(2000).crop("limit")),
                        new ProgressCallback() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {
                                int percent = (int) (bytesUploaded * 100 / totalBytes);
                                publishProgress(percent);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "doInBackground: params empty", e);
            }
            return uploadResult;
        }

        @Override
        protected void onProgressUpdate(Integer... percentValues) {
            progressDialog.setProgress(percentValues[0]);
        }

        @Override
        protected void onPostExecute(Map uploadResult) {
            if (uploadResult == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_EditAttr),
                        "Upload fehlgeschlagen", Snackbar.LENGTH_SHORT).show();
            } else {
                // update the Url and the visible picture
                attrImageUrl = (String) uploadResult.get("secure_url");
                Picasso.with(EditAttractionActivity.this).load(attrImageUrl).into(imageViewAttr);
            }

            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}

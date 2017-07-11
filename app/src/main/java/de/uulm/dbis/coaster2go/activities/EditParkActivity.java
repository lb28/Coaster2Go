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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cloudinary.Cloudinary;
import com.cloudinary.ProgressCallback;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Park;

public class EditParkActivity extends BaseActivity {

    static final int PLACE_PICKER_REQUEST = 3461;

    private static final String TAG = "EditParkActivity";
    EditText editTextParkName;
    EditText editTextParkLocationName;
    EditText editTextParkLat;
    EditText editTextParkLon;
    EditText editTextParkDescription;
    ImageView imageViewPark;

    ProgressDialog progressDialog;

    String parkImageUrl;
    String parkId;

    Park park;

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
                    imageViewPark.setImageDrawable(ContextCompat.getDrawable(
                            EditParkActivity.this, R.drawable.ic_theme_park));
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
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                if (editTextParkLocationName.getText().toString().isEmpty()) {
                    editTextParkLocationName.setText(String.valueOf(place.getName()));
                }
                editTextParkLat.setText(String.valueOf(place.getLatLng().latitude));
                editTextParkLon.setText(String.valueOf(place.getLatLng().longitude));
            }
        }else if (requestCode == RC_IMAGE_CAPTURE) {
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
        protected void onPostExecute(Park p) {

            park = p;

            if (p != null) {
                if (p.getImage() == null || p.getImage().isEmpty()) {
                    imageViewPark.setImageDrawable(ContextCompat.getDrawable(
                            EditParkActivity.this, R.drawable.ic_theme_park));
                    parkImageUrl = "";
                } else {
                    Picasso.with(EditParkActivity.this).load(p.getImage()).into(imageViewPark);
                    parkImageUrl = p.getImage();
                }
                editTextParkName.setText(p.getName());
                editTextParkLocationName.setText(p.getLocation());
                editTextParkLat.setText(String.valueOf(p.getLat()));
                editTextParkLon.setText(String.valueOf(p.getLon()));
                editTextParkDescription.setText(p.getDescription());
            }
            findViewById(R.id.buttonSavePark).setEnabled(true);
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

            // did the park already exist?
            if (parkId == null) {
                park = new Park(name, location, descr, lat, lon, parkImageUrl, 0, 0, user.getUid());
                return dbManager.createPark(park);
            } else {
                park.setName(name);
                park.setLocation(location);
                park.setDescription(descr);
                park.setLat(lat);
                park.setLon(lon);
                park.setImage(parkImageUrl);
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
                finish();
            }
        }
    }

    private class CloudinaryUploadTask extends AsyncTask<Object, Integer, Map> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditParkActivity.this);
            progressDialog.setTitle("Bild wird hochgeladen");
            progressDialog.setMessage("...");
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
                parkImageUrl = (String) uploadResult.get("secure_url");
                Picasso.with(EditParkActivity.this).load(parkImageUrl).into(imageViewPark);
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

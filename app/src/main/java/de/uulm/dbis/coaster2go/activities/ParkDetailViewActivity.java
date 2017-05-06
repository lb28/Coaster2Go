package de.uulm.dbis.coaster2go.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import de.uulm.dbis.coaster2go.R;

public class ParkDetailViewActivity extends BaseActivity {

    private String parkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail_view);

        parkId = getIntent().getStringExtra("parkId");

        Toast.makeText(this, "ParkId: " + parkId, Toast.LENGTH_SHORT).show();
    }
}

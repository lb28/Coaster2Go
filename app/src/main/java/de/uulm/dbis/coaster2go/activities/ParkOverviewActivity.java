package de.uulm.dbis.coaster2go.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.uulm.dbis.coaster2go.R;

public class ParkOverviewActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_overview);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_park);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.park_overview_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO handle menu item clicks

        switch (id) {
            case R.id.action_sort_abc:
                Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                        "TODO sort abc", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_sort_rating:
                return true;
            case R.id.action_sort_distance:
                return  true;
            case R.id.action_refresh:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



}

package de.uulm.dbis.coaster2go.activities;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.controller.WaitingTimeListAdapter;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.WaitingTime;

public class WaitingTimesActivity extends BaseActivity {

    private static final String TAG = "WaitingTimesActivity";
    private String attrId;
    private WaitingTimeListAdapter waitingTimeListAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_times);

        progressBar.setVisibility(View.VISIBLE);

        attrId = getIntent().getStringExtra("attrId");

        // empty list at the beginning
        waitingTimeListAdapter = new WaitingTimeListAdapter(new ArrayList<WaitingTime>());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewWaitingTimes);
        recyclerView.setAdapter(waitingTimeListAdapter);

        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_waitingtimes);
        new RefreshWaitingTimesTask().execute();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.VISIBLE);
                new RefreshWaitingTimesTask().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.waitingtime_list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handle menu item clicks
        switch (id) {
            case R.id.action_sort_abc:
                waitingTimeListAdapter.changeSort(WaitingTimeListAdapter.SortMode.NAME);
                return true;
            case R.id.action_sort_minutes:
                waitingTimeListAdapter.changeSort(WaitingTimeListAdapter.SortMode.MINUTES);
                return true;
            case R.id.action_sort_date:
                waitingTimeListAdapter.changeSort(WaitingTimeListAdapter.SortMode.DATE);
                return  true;
            case R.id.action_refresh:
                swipeRefresh.setRefreshing(true);
                new RefreshWaitingTimesTask().execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class RefreshWaitingTimesTask extends AsyncTask<Void, Void, List<WaitingTime>>{
        @Override
        protected List<WaitingTime> doInBackground(Void... params) {
            AzureDBManager dbManager = new AzureDBManager(WaitingTimesActivity.this);
            return dbManager.getWaitingTimeList(attrId);
        }

        @Override
        protected void onPostExecute(List<WaitingTime> waitingTimes) {
            if (waitingTimes == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_waitingTimes),
                        "Wartezeiten konnten nicht geladen werden",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                waitingTimeListAdapter.setWaitingTimeList(waitingTimes);
                waitingTimeListAdapter.notifyDataSetChanged();
            }

            //swipeRefresh.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }
    }
}

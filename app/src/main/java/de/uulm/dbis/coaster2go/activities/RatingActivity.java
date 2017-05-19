package de.uulm.dbis.coaster2go.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.controller.RatingListAdapter;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Review;

public class RatingActivity extends BaseActivity {

    public static final String TAG = "RatingActivity";

    String reviewedId; // can be either park or attraction
    RatingListAdapter ratingListAdapter;

    SwipeRefreshLayout swipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        reviewedId = getIntent().getStringExtra("reviewedId");

        String reviewedName = getIntent().getStringExtra("reviewedName");
        setTitle(reviewedName);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_ratings);
        swipeRefresh.setRefreshing(true);

        new RefreshRatingsTask().execute();

        ratingListAdapter = new RatingListAdapter(this, new ArrayList<Review>());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRatings);

        recyclerView.setAdapter(ratingListAdapter);

        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // add the swipe-to-refresh functionality
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                new RefreshRatingsTask().execute();
            }
        });
    }

    /**
     * shows a dialog to create a rating or edit his rating if the user already created one
     */
    public void editRating(View view) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_review, null);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RatingBar ratingBar = (RatingBar)
                               dialogView.findViewById(R.id.rating_dialog_rating);
                        EditText ratingComment = (EditText)
                                dialogView.findViewById(R.id.rating_dialog_comment);

                        // TODO save / update the review
                        Log.i(TAG, "TODO save: " + ratingBar.getRating() + ", "
                                + ratingComment.getText());
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private class RefreshRatingsTask extends AsyncTask<Void, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(Void... params) {
            AzureDBManager dbManager = new AzureDBManager(RatingActivity.this);
            return dbManager.getReviewList(reviewedId);
        }

        @Override
        protected void onPostExecute(List<Review> ratingsList) {
            ratingListAdapter.setRatingsList(ratingsList);
            ratingListAdapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        }
    }
}

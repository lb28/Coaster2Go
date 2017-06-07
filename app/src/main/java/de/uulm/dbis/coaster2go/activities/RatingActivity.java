package de.uulm.dbis.coaster2go.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    FloatingActionButton fabEditReview;

    /**
     * null if the user has no review of the park/attraction
     * otherwise, this is the user's review of the place
     */
    private Review usersReview;
    private boolean isAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        progressBar.setVisibility(View.VISIBLE);

        reviewedId = getIntent().getStringExtra("reviewedId");

        usersReview = null;

        isAttraction = getIntent().getBooleanExtra("isAttraction", false);

        String reviewedName = getIntent().getStringExtra("reviewedName");
        setTitle(reviewedName);

        ratingListAdapter = new RatingListAdapter(new ArrayList<Review>());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRatings);
        recyclerView.setAdapter(ratingListAdapter);

        fabEditReview = (FloatingActionButton) findViewById(R.id.fab_edit_rating);

        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_ratings);

        new RefreshRatingsTask().execute();

        // add the swipe-to-refresh functionality
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                swipeRefresh.setRefreshing(false);
                new RefreshRatingsTask().execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rating_list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handle menu item clicks
        switch (id) {
            case R.id.action_sort_abc:
                ratingListAdapter.changeSort(RatingListAdapter.SortMode.NAME);
                return true;
            case R.id.action_sort_rating:
                ratingListAdapter.changeSort(RatingListAdapter.SortMode.RATING);
                return true;
            case R.id.action_sort_date:
                ratingListAdapter.changeSort(RatingListAdapter.SortMode.DATE);
                return  true;
            case R.id.action_refresh:
                swipeRefresh.setRefreshing(true);
                new RefreshRatingsTask().execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * shows a dialog to create a rating or edit his rating if the user already created one
     */
    public void editRating(View view) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_review, null);

        final RatingBar ratingBar = (RatingBar)
                dialogView.findViewById(R.id.rating_dialog_rating);
        final EditText ratingComment = (EditText)
                dialogView.findViewById(R.id.rating_dialog_comment);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Review newReview = new Review(
                                reviewedId,
                                user.getDisplayName(),
                                user.getUid(),
                                Math.round(ratingBar.getRating()),
                                ratingComment.getText().toString()
                        );
                        new SaveReviewTask().execute(newReview);
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        if (usersReview != null) {
            ratingBar.setRating(usersReview.getNumberOfStars());
            ratingComment.setText(usersReview.getComment());
        }

        builder.show();
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
            if (user == null) {
                // hide the floating action button
                fabEditReview.setVisibility(View.GONE);
            } else {
                // show the button and see whether the user already has a review
                usersReview = null;
                for (Review review : ratingsList) {
                    if (review.getUserId().equals(user.getUid())) {
                        usersReview = review;
                        break;
                    }
                }


                fabEditReview.setVisibility(View.VISIBLE);
            }
            //swipeRefresh.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    class SaveReviewTask extends AsyncTask<Review, Void, Review> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Review doInBackground(Review... params) {
            AzureDBManager dbManager = new AzureDBManager(RatingActivity.this);
            Review newReview = params[0];

            // does the user already have a review?
            if (usersReview != null) {
                // update the review
                usersReview.setNumberOfStars(newReview.getNumberOfStars());
                usersReview.setComment(newReview.getComment());
                return dbManager.updateReview(usersReview, isAttraction);
            } else {
                // create a new review
                return dbManager.createReview(newReview, isAttraction);
            }

        }

        @Override
        protected void onPostExecute(Review updatedReview) {
            progressBar.setVisibility(View.GONE);
            if (updatedReview == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_Ratings),
                        "Speichern fehlgeschlagen", Snackbar.LENGTH_SHORT);
            } else {
                //swipeRefresh.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                new RefreshRatingsTask().execute();
            }
        }
    }
}

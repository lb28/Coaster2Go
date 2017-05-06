package de.uulm.dbis.coaster2go.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.uulm.dbis.coaster2go.OnParkItemClickListener;
import de.uulm.dbis.coaster2go.ParkListAdapter;
import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Park;

public class ParkOverviewActivity extends BaseActivity {

    private static final String TAG = "ParkOverviewActivity";
    public static final String MODE_ALL = "all";
    public static final String MODE_FAVS = "favs";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_overview);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Create the adapter that will return a fragment for each of the two
        // sections (all vs. favorites) of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewPagerParkTabs);
        Log.i(TAG, "onCreate: viewPager: " + mViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Hook the TabLayout up to the viewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutPark);
        tabLayout.setupWithViewPager(mViewPager);

        // Set up the floatingActionButton
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

    /**
     * A fragment containing a list of parks
     */
    public static class ParkListFragment extends Fragment {
        ParkListAdapter parkListAdapter;

        public ParkListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ParkListFragment newInstance(int tabIndex) {
            ParkListFragment fragment = new ParkListFragment();
            Bundle args = new Bundle();
            switch (tabIndex) {
                case 0:
                    args.putString("mode", MODE_ALL);
                    break;
                case 1:
                    args.putString("mode", MODE_FAVS);
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            /*
            MobileServiceClient mClient;
            MobileServiceTable<Park> mParkTable = null;

            try {
                mClient = new MobileServiceClient(
                        "https://coaster2go.azurewebsites.net",
                        getContext()
                );
                mParkTable = mClient.getTable(Park.class);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            */
/*
            Park testPark = new Park("id1", "Europa Park", "Rust", "schöner Park", 48.266015, 7.721972,
                    "http://www.mehrdrauf.de/cm/sparkasse-staufen-breisach/images/Europa-Park/EP2016_300x200.jpg",
                    2, 4.5, "admin");

            Park testPark2 = new Park("id2", "Pripyat", "Tschernobyl", "Geschlossen.", 51.408246, 30.055386,
                    "https://f1.blick.ch/img/incoming/origs3669981/9972533768-w1280-h960/tschernobyl00010.jpg",
                    1, 1.0, "admin");
            Park testPark3 = new Park("id3", "Handschuhwelt", "Bikini Bottom", "Luft anhalten.", 11.644220, 165.376451,
                    "http://en.spongepedia.org/images/9/90/Gloverworld.jpg",
                    3, 5.0, "admin");

            List<Park> testParks = Arrays.asList(testPark, testPark2, testPark3);
*/

            new LoadParksTask().execute();

            List<Park> parkList = new ArrayList<>(); // empty list before it is loaded

            parkListAdapter = new ParkListAdapter(getContext(), parkList, new OnParkItemClickListener() {
                @Override
                public void onParkItemClick(Park park) {
                    Intent intent = new Intent(getContext(), ParkDetailViewActivity.class);
                    intent.putExtra("parkId", park.getId());
                    startActivity(intent);
                }
            });

            View rootView = inflater.inflate(R.layout.fragment_parklist, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewParkList);
            textView.setText("mode: " + getArguments().getString("mode")); // for testing
            recyclerView.setAdapter(parkListAdapter);
            // Set layout manager to position the items
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            return rootView;
        }


        public class LoadParksTask extends AsyncTask<Void, Void, List<Park>> {

            @Override
            protected void onPreExecute() {
                ((ParkOverviewActivity) getActivity()).progressBar.show();
            }

            @Override
            protected List<Park> doInBackground(Void... params) {
                return new AzureDBManager(getContext()).getParkList();
            }

            @Override
            protected void onPostExecute(List<Park> parkList) {
                if (parkList == null) {
                    Log.e(TAG, "LoadParksTask.onPostExecute: parkList was null!");
                } else {
                    parkListAdapter.setParkList(parkList);
                    parkListAdapter.notifyItemRangeInserted(0, parkList.size());
                }
                ((ParkOverviewActivity) getActivity()).progressBar.hide();
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ParkListFragment (defined as a static inner class below).
            return ParkListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.all_page_title);
                case 1:
                    return getString(R.string.favs_page_title);
                default:
                    return null;
            }
        }
    }


}

package de.uulm.dbis.coaster2go.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.controller.OnParkItemClickListener;
import de.uulm.dbis.coaster2go.controller.OnParkItemLongClickListener;
import de.uulm.dbis.coaster2go.controller.ParkListAdapter;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.JsonManager;
import de.uulm.dbis.coaster2go.data.Park;

public class ParkOverviewActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ParkOverviewActivity";
    public static final String MODE_ALL = "MODE_ALL";
    public static final String MODE_FAVS = "MODE_FAVS";
    private static final int RC_PERM_GPS = 502;

    private SectionsPagerAdapter tabsPagerAdapter;

    Location lastLocation;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager tabsViewPager;

    private int currentFragmentIndex;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_overview);

        progressBar.setVisibility(View.VISIBLE);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Create the adapter that will return a fragment for each of the two
        // sections (all vs. favorites) of the activity.
        tabsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        tabsViewPager = (ViewPager) findViewById(R.id.viewPagerParkTabs);
        tabsViewPager.setAdapter(tabsPagerAdapter);

        currentFragmentIndex = 0;

        // Hook the TabLayout up to the viewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutPark);
        tabLayout.setupWithViewPager(tabsViewPager);

        tabsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentFragmentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

        switch (id) {
            case R.id.action_sort_abc:
                changeParkListSort(ParkListAdapter.SortMode.NAME);
                return true;
            case R.id.action_sort_rating:
                changeParkListSort(ParkListAdapter.SortMode.RATING);
                return true;
            case R.id.action_sort_distance:
                changeParkListSort(ParkListAdapter.SortMode.DISTANCE);
                return  true;
            case R.id.action_refresh:
                refreshParkList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param sortMode one of the sort modes available in {@link ParkListAdapter}
     */
    public void changeParkListSort(ParkListAdapter.SortMode sortMode) {
        ParkListFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.parkListAdapter != null) {
            currentFragment.parkListAdapter.changeSort(sortMode);
        }
    }

    private void refreshParkList() {
        ParkListFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.parkListAdapter != null) {
            currentFragment.progressBar.setVisibility(View.VISIBLE);
            currentFragment.refreshParkList();
        }
    }

    private void deleteParkGui(Park park) {
        ParkListFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.parkListAdapter != null) {
            int pos = currentFragment.parkListAdapter.getPositionOf(park);
            if (pos != -1) {
                currentFragment.parkListAdapter.removeAt(pos);
            } else {
                Log.e(TAG, "deleteParkGui: park not in list");
            }
        }
    }

    private ParkListFragment getCurrentFragment() {
        try {
            return tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "getCurrentFragment: No fragments in list", e);
            return null;
        }
    }

    private void updateDistances() {
        if (lastLocation != null) {
            try {
                for (ParkListFragment fragment : tabsPagerAdapter.fragmentList) {
                    if (fragment != null && fragment.parkListAdapter != null) {
                        fragment.parkListAdapter.setLastLocation(lastLocation);
                        fragment.parkListAdapter.notifyDataSetChanged();
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "getCurrentFragment: No fragments in list", e);
            }
        }
    }

    /**
     * calls the add park activity (called on click of the "add" button)
     */
    public void addPark(View view) {
        Intent intent = new Intent(this, EditParkActivity.class);
        startActivity(intent);
    }

    // LOCATION STUFF
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "location service connected");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                    "Standortberechtigung nicht erteilt",
                    Snackbar.LENGTH_LONG);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERM_GPS);

            return;
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // use the location
            updateDistances();
        }

    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                "Standort: Verbindung unterbrochen",
                Snackbar.LENGTH_LONG);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                "Standort konnte nicht ermittelt werden",
                Snackbar.LENGTH_LONG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == RC_PERM_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                onConnected(null);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * PARK LIST FRAGMENT (there is one for each tab)
     */
    public static class ParkListFragment extends Fragment {
        ParkListAdapter parkListAdapter;
        SwipeRefreshLayout swipeRefreshLayout;
        ImageView progressBar;

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
            View rootView = inflater.inflate(R.layout.fragment_parklist, container, false);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(
                    R.id.swiperefresh_parkList);
            progressBar = ((ParkOverviewActivity) getActivity()).progressBar;

            progressBar.setVisibility(View.VISIBLE);
            refreshParkList();

            List<Park> parkList = new ArrayList<>(); // empty list before it is loaded

            parkListAdapter = new ParkListAdapter(getContext(),
                    parkList,
                    new OnParkItemClickListener() {
                        @Override
                        public void onParkItemClick(Park park) {
                            Intent intent = new Intent(getContext(), ParkDetailViewActivity.class);
                            intent.putExtra("parkId", park.getId());
                            if (parkListAdapter.getLastLocation() != null) {
                                // put park distance in the intent
                                Location parkLoc = new Location("");
                                parkLoc.setLongitude(park.getLon());
                                parkLoc.setLatitude(park.getLat());

                                float distance = parkListAdapter.getLastLocation().distanceTo(parkLoc);

                                intent.putExtra("distance", distance);
                            }
                            startActivity(intent);
                        }
                    }, new OnParkItemLongClickListener() {

                        @Override
                        public boolean onParkItemLongClick(final Park park) {
                            // open context menu for edit, delete, ...

                            FirebaseUser user = ((ParkOverviewActivity) getActivity()).user;

                            if (user != null && park.getAdmin().equals(user.getUid())) {
                                String[] menuOptions = {
                                        "Park bearbeiten",
                                        "Park löschen"};
                                android.app.AlertDialog.Builder builder =
                                        new android.app.AlertDialog.Builder(getContext());
                                builder.setTitle(park.getName())
                                        .setItems(menuOptions, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        Intent intent = new Intent(getContext(),
                                                                EditParkActivity.class);
                                                        intent.putExtra("parkId", park.getId());
                                                        startActivity(intent);
                                                        break;
                                                    case 1:
                                                        showDeleteDialog(park);
                                                        break;
                                                }
                                            }
                                        });
                                Dialog d = builder.create();
                                d.show();
                            }

                            return true;
                        }
                    });

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(
                    R.id.recyclerViewParkList);

            recyclerView.setAdapter(parkListAdapter);
            // Set layout manager to position the items
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                    recyclerView.getContext(),
                    layoutManager.getOrientation()
            );
            recyclerView.addItemDecoration(dividerItemDecoration);

            // add the swipe-to-refresh functionality
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                    refreshParkList();
                }
            });

            return rootView;
        }

        void refreshParkList() {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.VISIBLE);

            if(getArguments().getString("mode").equals(MODE_FAVS)){
                refreshParkListFaves();
            }else{
                refreshParkListAll();
            }
        }

        void refreshParkListAll() {
            //Load offline data first because it is faster, then online data
            new RefreshParksOfflineTask().execute();
        }

        void refreshParkListFaves(){
            //Load offline data first because it is faster, then online data
            new RefreshFaveParksOfflineTask().execute();
        }

        class RefreshParksOfflineTask extends AsyncTask<Void, Void, List<Park>> {

            @Override
            protected List<Park> doInBackground(Void... params) {
                return new JsonManager((getContext())).getParkList();
            }

            @Override
            protected void onPostExecute(List<Park> parkList) {
                if (parkList == null) {
                    Log.e(TAG, "RefreshParksTask.onPostExecute: parkList was null!");
                } else {
                    parkListAdapter.setParkList(parkList);
                    parkListAdapter.notifyDataSetChanged();
                }
                // experimentally leaving the spinner active while loading the online data
                //swipeRefreshLayout.setRefreshing(false);
                //progressBar.setVisibility(View.GONE);
                new RefreshParksTask().execute(); //Load real online data when offline data is loaded
            }
        }

        class RefreshParksTask extends AsyncTask<Void, Void, List<Park>> {

            @Override
            protected List<Park> doInBackground(Void... params) {
                return new AzureDBManager(getContext()).getParkList();
            }

            @Override
            protected void onPostExecute(List<Park> parkList) {
                if (parkList == null) {
                    Log.e(TAG, "RefreshParksTask.onPostExecute: parkList was null!");
                } else {
                    parkListAdapter.setParkList(parkList);
                    parkListAdapter.notifyDataSetChanged();
                }
                //swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }

        class RefreshFaveParksOfflineTask extends AsyncTask<Void, Void, List<Park>> {

            @Override
            protected List<Park> doInBackground(Void... params) {
                return new JsonManager(getContext()).getFavoriteParks(new JsonManager(getContext()).getParkList());
            }

            @Override
            protected void onPostExecute(List<Park> parkList) {
                if (parkList == null) {
                    Log.e(TAG, "RefreshFavesParksTask.onPostExecute: parkList was null!");
                } else {
                    parkListAdapter.setParkList(parkList);
                    parkListAdapter.notifyDataSetChanged();
                }
                // experimentally leaving the spinner active while loading the online data
                //swipeRefreshLayout.setRefreshing(false);
                //progressBar.setVisibility(View.GONE);
                new RefreshFaveParksTask().execute(); //Load online data when offline data is loaded
            }
        }

        class RefreshFaveParksTask extends AsyncTask<Void, Void, List<Park>> {

            @Override
            protected List<Park> doInBackground(Void... params) {
                return new JsonManager(getContext()).getFavoriteParks(new AzureDBManager(getContext()).getParkList());
            }

            @Override
            protected void onPostExecute(List<Park> parkList) {
                if (parkList == null) {
                    Log.e(TAG, "RefreshFavesParksTask.onPostExecute: parkList was null!");
                } else {
                    parkListAdapter.setParkList(parkList);
                    parkListAdapter.notifyDataSetChanged();
                }
                //swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }

        void showDeleteDialog(final Park park) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Park löschen");
            builder.setMessage("Wollen Sie den Park \"" + park.getName() + "\" wirklich löschen?");
            builder.setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ParkOverviewActivity) getActivity()).new DeleteParkTask().execute(park.getId());
                }
            });
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<ParkListFragment> fragmentList;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentList = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem called for position " + position);
            // getItem is called to instantiate the fragment for the given page.
            // Return a ParkListFragment (defined as a static inner class).
            if (position >= fragmentList.size() || fragmentList.get(position) == null) {
                fragmentList.add(ParkListFragment.newInstance(position));
                updateDistances();
            }
            return fragmentList.get(position);
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

    private class DeleteParkTask extends AsyncTask<String, Void, Park> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Park doInBackground(String... params) {
            String parkId = params[0];
            if (parkId == null || parkId.isEmpty()) {
                return null;
            }
            return new AzureDBManager(ParkOverviewActivity.this).deletePark(parkId);
        }

        @Override
        protected void onPostExecute(Park deletedPark) {
            progressBar.setVisibility(View.GONE);

            if (deletedPark == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                        "Löschen Fehlgeschlagen", Snackbar.LENGTH_SHORT).show();
            } else {
                // update the view
                deleteParkGui(deletedPark);
            }

        }
    }

}

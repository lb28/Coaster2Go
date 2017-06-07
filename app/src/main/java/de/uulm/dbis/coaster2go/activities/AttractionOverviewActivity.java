package de.uulm.dbis.coaster2go.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.controller.AttractionListAdapter;
import de.uulm.dbis.coaster2go.controller.OnAttractionItemClickListener;
import de.uulm.dbis.coaster2go.data.Attraction;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.JsonManager;

public class AttractionOverviewActivity extends BaseActivity {

    private static final String TAG = "AttrOverviewActivity";
    public static final String MODE_ALL = "all";
    public static final String MODE_FAVS = "favs";

    private SectionsPagerAdapter tabsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager tabsViewPager;

    private int currentFragmentIndex;
    private String parkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_overview);

        progressBar.setVisibility(View.VISIBLE);

        parkId = getIntent().getStringExtra("parkId");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Create the adapter that will return a fragment for each of the two
        // sections (all vs. favorites) of the activity.
        tabsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        tabsViewPager = (ViewPager) findViewById(R.id.viewPagerAttractionTabs);
        tabsViewPager.setAdapter(tabsPagerAdapter);

        currentFragmentIndex = 0;

        // Hook the TabLayout up to the viewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutAttr);
        tabLayout.setupWithViewPager(tabsViewPager);

        tabsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float offs, int posOffsPx) {}

            @Override
            public void onPageSelected(int position) {
                currentFragmentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.attraction_overview_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handle menu item clicks
        switch (id) {
            case R.id.action_sort_abc:
                changeAttractionListSort(AttractionListAdapter.SortMode.NAME);
                return true;
            case R.id.action_sort_rating:
                changeAttractionListSort(AttractionListAdapter.SortMode.RATING);
                return true;
            case R.id.action_sort_waittime:
                changeAttractionListSort(AttractionListAdapter.SortMode.WAIT_TIME);
                return  true;
            case R.id.action_refresh:
                refreshAttractionList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param sortMode one of the sort modes available in {@link AttractionListAdapter}
     */
    public void changeAttractionListSort(AttractionListAdapter.SortMode sortMode) {
        AttractionListFragment currentFragment = tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
        if (currentFragment != null && currentFragment.attractionListAdapter != null) {
            currentFragment.attractionListAdapter.changeSort(sortMode);
        }
    }

    public void refreshAttractionList() {
        AttractionListFragment currentFragment = tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
        if (currentFragment != null && currentFragment.attractionListAdapter != null) {
            currentFragment.progressBar.setVisibility(View.VISIBLE);
            currentFragment.refreshAttrList();
        }
    }

    public void addAttraction(View view) {
        Snackbar.make(view, "TODO Add Attraction Activity", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    /**
     * A fragment containing a list of parks
     */
    public static class AttractionListFragment extends Fragment {
        AttractionListAdapter attractionListAdapter;
        SwipeRefreshLayout swipeRefresh;
        ImageView progressBar;

        public AttractionListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AttractionListFragment newInstance(int tabIndex, String parkId) {
            AttractionListFragment fragment = new AttractionListFragment();
            Bundle args = new Bundle();
            switch (tabIndex) {
                case 0:
                    args.putString("mode", MODE_ALL);
                    break;
                case 1:
                    args.putString("mode", MODE_FAVS);
                    break;
            }
            args.putString("parkId", parkId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_parklist, container, false);

            swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_parkList);
            progressBar = ((AttractionOverviewActivity) getActivity()).progressBar;

            progressBar.setVisibility(View.VISIBLE);
            refreshAttrList();

            List<Attraction> attractionList = new ArrayList<>(); // empty list before it is loaded

            attractionListAdapter = new AttractionListAdapter(getContext(), attractionList, new OnAttractionItemClickListener() {
                @Override
                public void onAttractionItemClick(Attraction attraction) {
                    Intent intent = new Intent(getContext(), AttractionDetailViewActivity.class);
                    intent.putExtra("attrId", attraction.getId());
                    startActivity(intent);
                }
            });

            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewParkList);
            //textView.setText("mode: " + getArguments().getString("mode")); // for testing
            recyclerView.setAdapter(attractionListAdapter);
            // Set layout manager to position the items
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            // add the swipe-to-refresh functionality
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshAttrList();
                }
            });

            return rootView;
        }

        void refreshAttrList() {
            swipeRefresh.setRefreshing(false);
            progressBar.setVisibility(View.VISIBLE);

            if(getArguments().getString("mode").equals(MODE_FAVS)){
                refreshAttrListFaves();
            }else{
                refreshAttrListAll();
            }
        }

        void refreshAttrListAll() {
            //Load offline data first because it is faster, then online data
            new RefreshAttractionsOfflineTask().execute();
        }

        void refreshAttrListFaves() {
            //Load offline data first because it is faster, then online data
            new RefreshAttractionsFavesOfflineTask().execute();
        }

        public class RefreshAttractionsOfflineTask extends AsyncTask<Void, Void, List<Attraction>> {

            @Override
            protected List<Attraction> doInBackground(Void... params) {
                String parkId = getArguments().getString("parkId");
                return new JsonManager(getContext()).getAttractionList(parkId);
            }

            @Override
            protected void onPostExecute(List<Attraction> attractionList) {
                if (attractionList == null) {
                    Log.e(TAG, "RefreshParksTask.onPostExecute: parkList was null!");
                } else {
                    attractionListAdapter.setAttractionList(attractionList);
                    attractionListAdapter.notifyDataSetChanged();
                }
                //swipeRefresh.setRefreshing(false);
                //progressBar.setVisibility(View.GONE);
                new RefreshAttractionsTask().execute(); //Load online data when offline data is loaded
            }
        }

        public class RefreshAttractionsTask extends AsyncTask<Void, Void, List<Attraction>> {

            @Override
            protected List<Attraction> doInBackground(Void... params) {
                String parkId = getArguments().getString("parkId");
                return new AzureDBManager(getContext()).getAttractionList(parkId);
            }

            @Override
            protected void onPostExecute(List<Attraction> attractionList) {
                if (attractionList == null) {
                    Log.e(TAG, "RefreshParksTask.onPostExecute: parkList was null!");
                } else {
                    attractionListAdapter.setAttractionList(attractionList);
                    attractionListAdapter.notifyDataSetChanged();
                }
                //swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }

        public class RefreshAttractionsFavesOfflineTask extends AsyncTask<Void, Void, List<Attraction>> {

            @Override
            protected List<Attraction> doInBackground(Void... params) {
                String parkId = getArguments().getString("parkId");
                return new JsonManager(getContext()).getFavoriteAttractions(parkId, new JsonManager(getContext()).getAttractionList(parkId));
            }

            @Override
            protected void onPostExecute(List<Attraction> attractionList) {
                if (attractionList == null) {
                    Log.e(TAG, "RefreshParksTask.onPostExecute: parkList was null!");
                } else {
                    attractionListAdapter.setAttractionList(attractionList);
                    attractionListAdapter.notifyDataSetChanged();
                }
                //swipeRefresh.setRefreshing(false);
                //progressBar.setVisibility(View.GONE);
                new RefreshAttractionsFavesTask().execute(); //Load online data when offline data is loaded
            }
        }

        public class RefreshAttractionsFavesTask extends AsyncTask<Void, Void, List<Attraction>> {

            @Override
            protected List<Attraction> doInBackground(Void... params) {
                String parkId = getArguments().getString("parkId");
                return new JsonManager(getContext()).getFavoriteAttractions(parkId, new AzureDBManager(getContext()).getAttractionList(parkId));
            }

            @Override
            protected void onPostExecute(List<Attraction> attractionList) {
                if (attractionList == null) {
                    Log.e(TAG, "RefreshParksTask.onPostExecute: parkList was null!");
                } else {
                    attractionListAdapter.setAttractionList(attractionList);
                    attractionListAdapter.notifyDataSetChanged();
                }
                //swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<AttractionListFragment> fragmentList;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentList = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ParkListFragment (defined as a static inner class).
            if (position >= fragmentList.size() || fragmentList.get(position) == null) {
                AttractionListFragment newFragment = AttractionListFragment.newInstance(position, parkId);
                fragmentList.add(newFragment);
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


}

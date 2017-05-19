package de.uulm.dbis.coaster2go.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.coaster2go.controller.OnParkItemClickListener;
import de.uulm.dbis.coaster2go.controller.ParkListAdapter;
import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.AzureDBManager;
import de.uulm.dbis.coaster2go.data.Park;

public class ParkOverviewActivity extends BaseActivity {

    private static final String TAG = "ParkOverviewActivity";
    public static final String MODE_ALL = "all";
    public static final String MODE_FAVS = "favs";

    private SectionsPagerAdapter tabsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager tabsViewPager;

    private int currentFragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_overview);

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
                changeParkListSort(ParkListAdapter.SORT_MODE_NAME);
                return true;
            case R.id.action_sort_rating:
                changeParkListSort(ParkListAdapter.SORT_MODE_RATING);
                return true;
            case R.id.action_sort_distance:
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
    public void changeParkListSort(String sortMode) {
        ParkListFragment currentFragment = tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
        if (currentFragment != null && currentFragment.parkListAdapter != null) {
            currentFragment.parkListAdapter.changeSort(sortMode);
        }
    }

    private void refreshParkList() {
        ParkListFragment currentFragment = tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
        if (currentFragment != null && currentFragment.parkListAdapter != null) {
            currentFragment.swipeRefreshLayout.setRefreshing(true);
            currentFragment.refreshParkList();
        }
    }

    /**
     * calls the add park activity (called on click of the "add" button)
     */
    public void addPark(View view) {
        Intent intent = new Intent(this, EditParkActivity.class);
        startActivity(intent);
    }

    /**
     * A fragment containing a list of parks
     */
    public static class ParkListFragment extends Fragment {
        ParkListAdapter parkListAdapter;
        SwipeRefreshLayout swipeRefreshLayout;

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

            swipeRefreshLayout.setRefreshing(true);
            new RefreshParksTask().execute();

            List<Park> parkList = new ArrayList<>(); // empty list before it is loaded

            parkListAdapter = new ParkListAdapter(getContext(), parkList, new OnParkItemClickListener() {
                @Override
                public void onParkItemClick(Park park) {
                    Intent intent = new Intent(getContext(), ParkDetailViewActivity.class);
                    intent.putExtra("parkId", park.getId());
                    startActivity(intent);
                }
            });

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(
                    R.id.recyclerViewParkList);
            textView.setText("mode: " + getArguments().getString("mode")); // for testing
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

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
        }

        void refreshParkList() {
            new RefreshParksTask().execute();
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
                swipeRefreshLayout.setRefreshing(false);
            }
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a ParkListFragment (defined as a static inner class).
            if (position >= fragmentList.size() || fragmentList.get(position) == null) {
                fragmentList.add(ParkListFragment.newInstance(position));
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

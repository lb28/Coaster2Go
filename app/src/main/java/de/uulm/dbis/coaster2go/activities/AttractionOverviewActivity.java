package de.uulm.dbis.coaster2go.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.controller.AttractionListAdapter;
import de.uulm.dbis.coaster2go.controller.OnAttractionItemClickListener;
import de.uulm.dbis.coaster2go.controller.OnAttractionItemLongClickListener;
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
    private static boolean isParkAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_overview);

        progressBar.setVisibility(View.VISIBLE);

        isParkAdmin = getIntent().getBooleanExtra("isParkAdmin", false);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attraction_overview_actions, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.attraction_search_box).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView. setSubmitButtonEnabled(true); //Shows an extra "Search Button"
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                try{
                    AttractionListFragment currentFragment = tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
                    currentFragment.attractionListAdapter.filterList(newText);
                }catch(Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "Attractionsearch: Probably no current fragment");
                }

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                try{
                    AttractionListFragment currentFragment = tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
                    currentFragment.attractionListAdapter.filterList(query);
                }catch(Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "Attractionsearch: Probably no current fragment");
                }
                return true;
            }
        });

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

    private void deleteAttrGui(Attraction attr) {
        AttractionListFragment currentFragment = tabsPagerAdapter.fragmentList.get(currentFragmentIndex);
        if (currentFragment != null && currentFragment.attractionListAdapter != null) {
            int pos = currentFragment.attractionListAdapter.getPositionOf(attr);
            if (pos != -1) {
                currentFragment.attractionListAdapter.removeAt(pos);
            } else {
                Log.e(TAG, "deleteAttrGui: attraction not in list");
            }
        }
    }

    public void addAttraction(View view) {
        Intent intent = new Intent(this, EditAttractionActivity.class);
        intent.putExtra("parkId", parkId);

        startActivityForResult(intent, RC_ADD_ATTR);
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

            attractionListAdapter = new AttractionListAdapter(
                    getContext(),
                    attractionList,
                    new OnAttractionItemClickListener() {
                        @Override
                        public void onAttractionItemClick(Attraction attraction) {
                            Intent intent = new Intent(getContext(), AttractionDetailViewActivity.class);
                            intent.putExtra("attrId", attraction.getId());
                            intent.putExtra("isParkAdmin", isParkAdmin);
                            startActivity(intent);
                        }
                    }, new OnAttractionItemLongClickListener() {
                        @Override
                        public boolean onAttractionItemLongClick(final Attraction attr) {
                            // open context menu for edit, delete, ...

                            if (isParkAdmin) {
                                String[] menuOptions = {
                                        "Attraktion bearbeiten",
                                        "Attraktion löschen"};
                                android.app.AlertDialog.Builder builder =
                                        new android.app.AlertDialog.Builder(getContext());
                                builder.setTitle(attr.getName())
                                        .setItems(menuOptions, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        Intent intent = new Intent(getContext(),
                                                                EditAttractionActivity.class);
                                                        intent.putExtra("parkId", attr.getParkId());
                                                        intent.putExtra("attrId", attr.getId());
                                                        startActivity(intent);
                                                        break;
                                                    case 1:
                                                        showDeleteDialog(attr);
                                                        break;
                                                }
                                            }
                                        });
                                builder.create().show();
                            }

                            return true;
                        }
                    });

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewParkList);
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

        /**
         * refreshes the attraction list.
         * the caller has to ensure the swipeRefresh spinner or the progressbar has been activated.
         */
        void refreshAttrList() {
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

        void showDeleteDialog(final Attraction attr) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Park löschen");
            builder.setMessage("Wollen Sie den Park \"" + attr.getName() + "\" wirklich löschen?");
            builder.setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((AttractionOverviewActivity) getActivity()).new DeleteAttrTask().execute(attr.getId());
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

        public class RefreshAttractionsOfflineTask extends AsyncTask<Void, Void, List<Attraction>> {

            @Override
            protected List<Attraction> doInBackground(Void... params) {
                String parkId = getArguments().getString("parkId");
                return new JsonManager(getContext()).getAttractionList(parkId);
            }

            @Override
            protected void onPostExecute(List<Attraction> attractionList) {
                if (attractionList == null) {
                    Log.e(TAG, "RefreshAttractionsOfflineTask.onPostExecute: attrList was null!");
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
                    Log.e(TAG, "RefreshAttractionsTask.onPostExecute: attrList was null!");
                } else {
                    attractionListAdapter.setAttractionList(attractionList);
                    attractionListAdapter.notifyDataSetChanged();
                }
                swipeRefresh.setRefreshing(false);
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
                    Log.e(TAG, "RefreshAttractionsFavesOfflineTask.onPostExecute: attrList was null!");
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
                    Log.e(TAG, "RefreshAttractionsFavesTask.onPostExecute: attrList was null!");
                } else {
                    attractionListAdapter.setAttractionList(attractionList);
                    attractionListAdapter.notifyDataSetChanged();
                }
                swipeRefresh.setRefreshing(false);
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
            // Return a AttractionListFragment (defined as a static inner class).
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

    private class DeleteAttrTask extends AsyncTask<String, Void, Attraction> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Attraction doInBackground(String... params) {
            String attrId = params[0];
            if (attrId == null || attrId.isEmpty()) {
                return null;
            }
            return new AzureDBManager(AttractionOverviewActivity.this).deleteAttraction(parkId, attrId);
        }

        @Override
        protected void onPostExecute(Attraction deletedAttr) {
            progressBar.setVisibility(View.GONE);

            if (deletedAttr == null) {
                Snackbar.make(findViewById(R.id.coordinatorLayout_ParkOverview),
                        "Löschen Fehlgeschlagen", Snackbar.LENGTH_SHORT).show();
            } else {
                // update the view
                deleteAttrGui(deletedAttr);
            }

        }
    }
}

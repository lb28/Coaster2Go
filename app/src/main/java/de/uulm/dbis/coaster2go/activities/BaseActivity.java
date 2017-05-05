package de.uulm.dbis.coaster2go.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import de.uulm.dbis.coaster2go.R;

/**
 * A base activity that provides the menu drawer, has to be extended by
 * the other activities that need a menu drawer
 *
 * adapted from <a href='http://stackoverflow.com/questions/4922641/sliding-drawer-appear-in-all-activities'>
 *     http://stackoverflow.com/questions/4922641/sliding-drawer-appear-in-all-activities</a>
 */
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "BaseActivity";

    FirebaseUser user;

    protected DrawerLayout baseLayout;
    protected FrameLayout actContent;

    private ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    ContentLoadingProgressBar progressBar;
    TextView textViewUserName;

    @Override
    public void setContentView(final int layoutResID) {

        baseLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        actContent = (FrameLayout) baseLayout.findViewById(R.id.activity_content);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(baseLayout);

        progressBar = (ContentLoadingProgressBar) findViewById(R.id.progressBar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // user signed in?
        user = FirebaseAuth.getInstance().getCurrentUser();
        textViewUserName = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.nav_header_subtitle);
        if (user != null) {
            // User is signed in
            setMenuItemsSignedIn();
            textViewUserName.setText(user.getDisplayName());
        } else {
            // No user is signed in
            setMenuItemsSignedOut();
            textViewUserName.setText(R.string.not_signed_in);
        }

        configureToolbar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        progressBar.hide();

    }

    private void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.i(TAG, "configureToolbar: toolbar == null");
        }
        // changes made here, try to find if getSupportActionBar() is null or not after setting it - setSupportActionBar

        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setMenuItemsSignedIn() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_sign_in).setVisible(false);
        nav_Menu.findItem(R.id.nav_sign_out).setVisible(true);
        nav_Menu.findItem(R.id.nav_my_account).setVisible(true);
    }

    private void setMenuItemsSignedOut() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_sign_in).setVisible(true);
        nav_Menu.findItem(R.id.nav_sign_out).setVisible(false);
        nav_Menu.findItem(R.id.nav_my_account).setVisible(false);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks
        int id = item.getItemId();
        if (id == R.id.nav_sign_in) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                            ))
                            .setTosUrl("https://github.com/lb28/Coaster2Go/tree/master#terms-of-service")
                            .build(),
                    RC_SIGN_IN);
        }
        else if (id == R.id.nav_park_overview) {
            startActivity(new Intent(this, ParkOverviewActivity.class));
        } else if (id == R.id.nav_my_account) {
            startActivity(new Intent(this, MyAccountActivity.class));
        } else if (id == R.id.drawer_switch) {
            Switch offlineSwitch = (Switch) findViewById(R.id.drawer_switch);
            if (offlineSwitch.isChecked()) {
                Toast.makeText(this, "offline mode activated (TODO)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "online mode (TODO)", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Intent restart = getIntent();
                            finish();
                            startActivity(restart);
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {

                Intent intent = getIntent();
                finish();
                startActivity(intent);
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showSnackbar(int resId) {
        Snackbar.make(
                findViewById(R.id.coordinatorLayout_ParkOverview),
                resId,
                Snackbar.LENGTH_LONG
        );
    }



}

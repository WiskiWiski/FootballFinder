package online.findfootball.android.game.football.screen.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import online.findfootball.android.BuildConfig;
import online.findfootball.android.MainActivity;
import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.app.NavDrawerActivity;
import online.findfootball.android.game.football.screen.create.CreateGameActivity;
import online.findfootball.android.game.football.screen.find.FindGameActivity;
import online.findfootball.android.game.football.screen.my.archived.ArchivedGamesFragment;
import online.findfootball.android.game.football.screen.my.upcoming.UpcomingGamesFragment;

public class MyGamesActivity extends NavDrawerActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NavDrawerActivity.OnRootActivity {

    private static final String TAG = App.G_TAG + ":MyGamesAct";

    private UpcomingGamesFragment upcomingGamesFragment;
    private ArchivedGamesFragment archivedGamesFragment;

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setToolbarTitle(String title) {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_games);
        initToolbar();
        if (savedInstanceState == null) {
            // регистрируем главный NavDraw.ItemClickListener и передаем menu-id главного экрана
            registerRootActivity(this);
            addRootActivityChildes(R.id.nav_upcoming_games, R.id.nav_archived_games);
            setDefaultMenuItemId(R.id.nav_upcoming_games);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_version) {
            Toast.makeText(getApplicationContext(), "VERSION_NAME: " + BuildConfig.VERSION_NAME + "\n"
                    + " VERSION_CODE: " + BuildConfig.VERSION_CODE, Toast.LENGTH_SHORT).show();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        // Получаем menuItem из Navigation Drawer текущей активити, а не той, откуда он прищел
        menuItem = getMenuItemById(itemId);

        closeDrawer();
        if (getCurrentMenuItemId() == itemId) {
            return false;
        } else {
            super.updateMenuItemSelection(menuItem); // Обновляем выделение в Navigation Drawer
            super.updateMenuItemId(itemId); // Обновляем current menu item id

            switch (itemId) {
                case R.id.nav_main:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;

                case R.id.nav_find_game:
                    startActivity(new Intent(getApplicationContext(), FindGameActivity.class));
                    break;

                case R.id.nav_upcoming_games:
                    setUpcomingGamesFragment();
                    break;

                case R.id.nav_archived_games:
                    setArchivedGamesFragment();
                    break;

                case R.id.nav_create_game:
                    startActivity(new Intent(getApplicationContext(), CreateGameActivity.class));
                    break;

                default:
                    return false;
            }
            return true;
        }
    }

    private void setUpcomingGamesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.root_container, getUpcomingGamesFragment(), UpcomingGamesFragment.F_TAG)
                .commitAllowingStateLoss();
        setToolbarTitle(getString(R.string.my_games_activity_title_upcoming_games));
    }

    private void setArchivedGamesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.root_container, getArchivedGamesFragment(), ArchivedGamesFragment.F_TAG)
                .commitAllowingStateLoss();
        setToolbarTitle(getString(R.string.my_games_activity_title_archived_games));
    }

    private UpcomingGamesFragment getUpcomingGamesFragment() {
        if (getIntent().getExtras() != null) {
            upcomingGamesFragment = (UpcomingGamesFragment) getSupportFragmentManager()
                    .findFragmentByTag(UpcomingGamesFragment.F_TAG);
        }
        if (upcomingGamesFragment == null) {
            upcomingGamesFragment = UpcomingGamesFragment.newInstance();
        }
        return upcomingGamesFragment;
    }

    private ArchivedGamesFragment getArchivedGamesFragment() {
        if (getIntent().getExtras() != null) {
            archivedGamesFragment = (ArchivedGamesFragment) getSupportFragmentManager()
                    .findFragmentByTag(ArchivedGamesFragment.F_TAG);
        }
        if (archivedGamesFragment == null) {
            archivedGamesFragment = ArchivedGamesFragment.newInstance();
        }
        return archivedGamesFragment;
    }

    @Override
    public int getCurrentViewItemId() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_container);
        if (fragment instanceof UpcomingGamesFragment) {
            return R.id.nav_upcoming_games;
        } else if (fragment instanceof ArchivedGamesFragment) {
            return R.id.nav_archived_games;
        } else {
            return NavDrawerActivity.getCurrentMenuItemId();
        }
    }
}

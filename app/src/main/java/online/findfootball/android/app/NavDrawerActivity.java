package online.findfootball.android.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.HashSet;

import online.findfootball.android.R;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.user.AppUser;
import online.findfootball.android.user.ProfileActivity;
import online.findfootball.android.user.auth.AuthUiActivity;

/**
 * Created by WiskiW on 16.03.2017.
 */

public class NavDrawerActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        AppUser.UserStateListener {

    private static final String TAG = App.G_TAG + ":NavDrawerActivity";

    private View navDrawHeaderLayout;
    private TextView nameView;
    private TextView emailView;
    private ImageView photoView;

    private static int currentMenuItemId;
    private static int defaultMenuItemId;

    private static OnRootActivity rootActivity;
    private static HashSet<Integer> rootChildes;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    protected MenuItem getMenuItemById(int id) {
        return navigationView.getMenu().findItem(id);
    }

    public void registerRootActivity(OnRootActivity rootActivity) {
        NavDrawerActivity.rootActivity = rootActivity;
    }

    public void setDefaultMenuItemId(int itemId) {
        uncheckedMenu();
        updateMenuItemId(0);
        NavDrawerActivity.defaultMenuItemId = itemId;
        rootActivity.onNavigationItemSelected(getMenuItemById(getDefaultMenuItemId()));
        //updateMenuItemId(itemId);
        //getMenuItemById(itemId).setChecked(true);
    }

    private void uncheckedMenu() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    public int getDefaultMenuItemId() {
        return defaultMenuItemId;
    }

    public static void addRootActivityChildes(Integer... itemIds) {
        if (rootChildes == null) {
            rootChildes = new HashSet<>();
        }
        for (int id : itemIds) {
            rootChildes.add(id);
        }
    }

    private boolean isRootChild(int id) {
        if (rootChildes != null) {
            for (int child : rootChildes) {
                if (id == child) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    public static int getCurrentMenuItemId() {
        return currentMenuItemId;
    }

    protected void updateMenuItemId(int clickedMenuItemId) {
        currentMenuItemId = clickedMenuItemId;
    }

    protected void updateMenuItemSelection(MenuItem clickedMenuItem) {
        // Убирает выделение текущего меню-элемента
        getMenuItemById(currentMenuItemId).setChecked(false);

        // Устанавливает выделение на нажатом меню-элементе
        getMenuItemById(clickedMenuItem.getItemId()).setChecked(true);
    }

    @Override
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        FrameLayout activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUpNavigationHeader();
        navigationView.setNavigationItemSelectedListener(this);
        getMenuItemById(currentMenuItemId).setChecked(true);
    }

    private void updateAppUserData(AppUser appUser) {
        nameView.setText(appUser.getDisplayName());
        emailView.setText(appUser.getEmail());
        if (photoView != null && appUser.getPhotoUrl() != null) {
            Glide
                    .with(getApplicationContext())
                    .load(appUser.getPhotoUrl())
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(photoView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            photoView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }


    @Override
    public void onLogin(final AppUser appUser) {
        if (appUser == null) {
            return;
        }
        if (appUser.hasUnpacked()) {
            updateAppUserData(appUser);
        } else {
            appUser.load(new DatabaseLoader.OnLoadListener() {
                @Override
                public void onComplete(DataInstanceResult result, DatabasePackable packable) {
                    updateAppUserData((AppUser) packable);
                }
            });
        }
        navDrawHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(NavDrawerActivity.this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.INTENT_USER_KEY, (Parcelable) appUser);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onSignOut() {
        navDrawHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUiActivity.requestAuth(getApplicationContext());
            }
        });
        photoView.setImageDrawable(null);
        nameView.setText(null);
        emailView.setText(null);
    }


    private void setUpNavigationHeader() {
        navDrawHeaderLayout = navigationView.getHeaderView(0); // 0-index header
        nameView = (TextView) navDrawHeaderLayout.findViewById(R.id.user_display_name);
        emailView = (TextView) navDrawHeaderLayout.findViewById(R.id.user_email);
        photoView = (ImageView) navDrawHeaderLayout.findViewById(R.id.user_photo);

        AppUser.setUserStateListener(this);
        AppUser appUser = AppUser.getUser(this, false);
        if (appUser == null) {
            onSignOut();
        } else {
            onLogin(appUser);
        }
    }

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(Gravity.START);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            drawerLayout.closeDrawers();
            return;
        } else if (!isRootChild(currentMenuItemId)) {
            MenuItem menuItem = getMenuItemById(rootActivity.getCurrentViewItemId());
            updateMenuItemSelection(menuItem);
            rootActivity.onNavigationItemSelected(menuItem);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        int id = menuItem.getItemId();
        if (getCurrentMenuItemId() == id) {
            // Если нажали то, что сейчас открыто
            return false;
        } else {
            if (!isRootChild(currentMenuItemId)) {
                finish();
            }
            return rootActivity.onNavigationItemSelected(menuItem);
        }
    }

    @Override
    public void finish() {
        if (!isRootChild(currentMenuItemId)) {
            MenuItem menuItem = getMenuItemById(rootActivity.getCurrentViewItemId());
            updateMenuItemSelection(menuItem);
            rootActivity.onNavigationItemSelected(menuItem);
        }
        super.finish();
    }

    public interface OnRootActivity {
        int getCurrentViewItemId();

        boolean onNavigationItemSelected(MenuItem menuItem);
    }

}

package ru.sigmadigital.recipes.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.asynctasks.GetProfileTask;
import ru.sigmadigital.recipes.fragment.ArchiveMenuFragment;
import ru.sigmadigital.recipes.fragment.PreparedMenuFragment;
import ru.sigmadigital.recipes.fragment.ProductListFragment;
import ru.sigmadigital.recipes.fragment.ProfileFragment;
import ru.sigmadigital.recipes.fragment.RadialMenuFragment;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.response.ProfileResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;
import ru.sigmadigital.recipes.util.SettingHelper;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProfileFragment.OnProfileInfoChanger {

    private static final String PREFERENCES_NAME = "settings";

    private int FRAGMENT_PROFILE = 1;
    private int FRAGMENT_ARCHIVE_MENU = 2;
    private int currentFragment = 0;


    //actionbar
    private ImageView imvActionButton;
    private ImageView imvSecondActionButton;
    private LinearLayout btnBackLayout;
    private LinearLayout secondActionButtonLayout;
    private CircleImageView actionBarAvatar;

    //drawer
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private ActionBarDrawerToggle toggle;
    private TextView headerName;
    private TextView headerEmail;
    private ImageView headerAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (savedInstanceState == null || !savedInstanceState.containsKey("restart")) {
            loadFragment(RadialMenuFragment.newInstance(), getResources().getString(R.string.recipe_diary), true);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        int pos = getSupportFragmentManager().getBackStackEntryCount() - 1;
                        setTitle(getSupportFragmentManager().getBackStackEntryAt(pos).getName());

                        if (currentFragment != 0) {
                            currentFragment = 0;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() {
        //drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View v = navigationView.findViewById(R.id.logout_drawer_button);
        v.setOnClickListener(this);
        headerName = navigationView.getHeaderView(0).findViewById(R.id.name);
        headerEmail = navigationView.getHeaderView(0).findViewById(R.id.email);
        headerAvatar = navigationView.getHeaderView(0).findViewById(R.id.avatar);
        headerAvatar.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        //actionbar
        Toolbar toolbar = findViewById(R.id.my_action_bar);
        imvActionButton = findViewById(R.id.imv_action_btn);
        imvSecondActionButton = findViewById(R.id.imv_second_action_btn);
        btnBackLayout = findViewById(R.id.button_back_layout);
        secondActionButtonLayout = findViewById(R.id.second_action_button_layout);
        actionBarAvatar = findViewById(R.id.bar_avatar);
        actionBarAvatar.setOnClickListener(this);
        btnBackLayout.setOnClickListener(this);

        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ImageView ivVk = navigationView.getHeaderView(0).findViewById(R.id.bt_vk);
        ImageView ivFb = navigationView.getHeaderView(0).findViewById(R.id.bt_fb);
        ImageView ivInst = navigationView.getHeaderView(0).findViewById(R.id.bt_inst);

        ivFb.setOnClickListener(this);
        ivVk.setOnClickListener(this);
        ivInst.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        setUserData();
        setProfileAvatar();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setUserData();
        setProfileAvatar();
    }

    private void setUserData() {
        final User user = SettingHelper.getUser();

        new GetProfileTask(new GetProfileTask.OnGetProfileListener() {
            @Override
            public void onGetProfile(ProfileResponse response) {
                if (response != null && user != null) {
                    user.setName(response.getName());
                    user.setBirthday(response.getBdate());
                    user.setCity(response.getCity());
                    user.setGender(response.getGender());

                    SettingHelper.setUser(user);

                    headerName.setText(user.getName());
                    headerEmail.setText(user.getEmail());
                    setProfileAvatar();
                }
            }
        }).execute();
    }

    private void setProfileAvatar() {
        PicassoUtil.getPicasso()
                .load(Network.BASE_URL + "api/profile/photo")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(headerAvatar);
        PicassoUtil.getPicasso()
                .load(Network.BASE_URL + "api/profile/photo")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(actionBarAvatar);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_menu_saved: {
                if (currentFragment != FRAGMENT_ARCHIVE_MENU) {
                    loadFragment(ArchiveMenuFragment.newInstance(), getResources().getString(R.string.archive_menu), true);
                    currentFragment = FRAGMENT_ARCHIVE_MENU;
                }
                break;
            }

            /*case R.id.nav_menu_timer:
                break;*/

            case R.id.nav_menu_profile:
                if (currentFragment != FRAGMENT_PROFILE) {
                    loadFragment(ProfileFragment.newInstance(this), getResources().getString(R.string.profile), true);
                    currentFragment = FRAGMENT_PROFILE;
                }
                break;
        }

        navigationView.setCheckedItem(menuItem);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_drawer_button:

                drawer.closeDrawer(GravityCompat.START);

                getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit()
                        .clear()
                        .apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                MainActivity.this.finish();
                break;

            case R.id.button_back_layout:
                onBackPressed();
                break;

            case R.id.bt_vk:
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse("https://vk.com/autorecipe"));
                startActivity(i1);
                break;

            case R.id.bt_inst:
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse("https://www.instagram.com/auto.recipe/"));
                startActivity(i2);
                break;

            case R.id.bt_fb:
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse("https://www.facebook.com/autorecipe/"));
                startActivity(i3);
                break;

            case R.id.bar_avatar:
                if (currentFragment != FRAGMENT_PROFILE) {
                    loadFragment(ProfileFragment.newInstance(this), getResources().getString(R.string.profile), true);
                    currentFragment = FRAGMENT_PROFILE;
                }
                break;

            case R.id.avatar:
                drawer.closeDrawer(GravityCompat.START);
                if (currentFragment != FRAGMENT_PROFILE) {
                    loadFragment(ProfileFragment.newInstance(this), getResources().getString(R.string.profile), true);
                    currentFragment = FRAGMENT_PROFILE;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getFragments().size() != 0 && getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1) instanceof PreparedMenuFragment) {
            if (((PreparedMenuFragment) getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1)).isEditMode()) {
                ((PreparedMenuFragment) getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1)).exitEditMode();
            } else {
                super.onBackPressed();
            }
        } else {
            if (getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1).getClass().equals(ProductListFragment.class)) {
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }

        }

    }


    //actionbar
    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence titleString) {
        super.setTitle(titleString);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(titleString);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("restart", true);
    }

    public ImageView getActionButton() {
        return imvActionButton;
    }

    public void setActionButtonImage(Drawable drawable) {
        imvActionButton.setImageDrawable(drawable);
    }

    public void setActionButtonClickListener(View.OnClickListener listener) {
        imvActionButton.setOnClickListener(listener);
    }

    public LinearLayout getSecondActionButtonLayout() {
        return secondActionButtonLayout;
    }

    public void setSecondActionButtonImage(Drawable drawable) {
        imvSecondActionButton.setImageDrawable(drawable);
    }

    public void setSecondActionButtonClickListener(View.OnClickListener listener) {
        imvSecondActionButton.setOnClickListener(listener);
    }

    public void hideAvatar(boolean hide) {
        if (hide)
            actionBarAvatar.setVisibility(View.GONE);
        else
            actionBarAvatar.setVisibility(View.VISIBLE);
    }

    public LinearLayout getBtnBackLayout() {
        return btnBackLayout;
    }


    //drawer
    public DrawerLayout getDrawer() {
        return drawer;
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
    }

    @Override
    public void onInfoChange(String name, String email) {
        headerName.setText(name);
        headerEmail.setText(email);
    }


    @Override
    public void onPhotoChange() {
        setProfileAvatar();
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }

    @Override
    protected void loadFragment(Fragment fragment, String title, boolean stack) {
        if (getSupportFragmentManager().getFragments().size() == 0) {
            super.loadFragment(fragment, title, stack);
        } else {
            Fragment currFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1);
            if (!currFragment.getClass().equals(fragment.getClass())) {
                super.loadFragment(fragment, title, stack);
            } else {
            }

        }
    }
}

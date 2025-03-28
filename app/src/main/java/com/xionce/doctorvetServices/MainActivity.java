package com.xionce.doctorvetServices;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.Users_permissions;
import com.xionce.doctorvetServices.data.Vet;
import com.xionce.doctorvetServices.utilities.HelperClass;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomSheetDialog.BottomSheetListener2 {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private ViewPager mViewPager;

    MenuItem moreMenuItem;
    MenuItem searchMenuItem;
    NavigationView navigationView;

    public enum TAB_NAV_PANELS {HOME, PETS, OWNERS, AGENDA, DAILY_CASH, REPORTS}

    private TAB_NAV_PANELS tabInUse;

    //to prevent fast taps
    private long CLICK_TIME_INTERVAL = 1000;
    private long mLastClickTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.main_fab);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        //pets and owners naming
        navigationView.getMenu().getItem(TAB_NAV_PANELS.OWNERS.ordinal()).setTitle(DoctorVetApp.get().getOwnerNamingPlural());
        navigationView.getMenu().getItem(TAB_NAV_PANELS.PETS.ordinal()).setTitle(DoctorVetApp.get().getPetNamingPlural());

        //vet
        final CoordinatorLayout nav_header_vet = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_veterinaria);
        ConstraintLayout vet_layout = nav_header_vet.findViewById(R.id.nav_header_constraint_layout);
        LoadVet(DoctorVetApp.get().getVet(), vet_layout);
        vet_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(MainActivity.this, ViewVetActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.VET_ID.name(), DoctorVetApp.get().getVet().getId());
                startActivity(activity);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //user
        final CoordinatorLayout nav_header_usr = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_user);
        ConstraintLayout user_layout = nav_header_usr.findViewById(R.id.nav_header_constraint_layout);
        LoadUser(DoctorVetApp.get().getUser(), user_layout);
        user_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(MainActivity.this, ViewUserActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.USER_ID.name(), DoctorVetApp.get().getUser().getId());
                //view users activity has "password change" that "restart" app.
                startActivityForResult(activity, 1);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        if (DoctorVetApp.get().getUser().getMultivet() > 1) {
            ImageView img_change_vet = navigationView.getHeaderView(0).findViewById(R.id.img_change_vet);
            img_change_vet.setVisibility(View.VISIBLE);
            img_change_vet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ChangeVetActivity.class);
                    startActivity(intent);
                }
            });
        }

        //about
        MenuItem nav_about = navigationView.getMenu().findItem(R.id.nav_about);
        nav_about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL)
                    return;

                mLastClickTime = now;
                //showBottomSheetDialog();
                showBottomSheetDialog();
            }
        });

        tabInUse = TAB_NAV_PANELS.HOME;
        getSupportActionBar().setTitle(R.string.inicio);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //tabInUse = position;
                tabInUse = TAB_NAV_PANELS.values()[position];

                showMoreItem(false);
                showSearchItem(false);
                toolbar.setSubtitle("");

                switch (tabInUse) {
                    case HOME:
                        toolbar.setTitle(R.string.inicio);
                        break;
                    case OWNERS:
                        toolbar.setTitle(DoctorVetApp.get().getOwnerNamingPlural());
                        showMoreItem(true);
                        showSearchItem(true);
                        break;
                    case PETS:
                        toolbar.setTitle(DoctorVetApp.get().getPetNamingPlural());
                        showSearchItem(true);
                        break;
                    case AGENDA:
                        toolbar.setTitle(DoctorVetApp.get().getAgendaTitle());
                        break;
                    case DAILY_CASH:
                        toolbar.setTitle(DoctorVetApp.get().getDailyCashTitle());
                        break;
                    case REPORTS:
                        toolbar.setTitle(getString(R.string.reportes));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //users custom main - ver 16
        Users_permissions usersPermissions = DoctorVetApp.get().getUser().getPermissions();
        if (usersPermissions == null) {
            DoctorVetApp.get().setUsersPermissionsDueToVer16(new DoctorVetApp.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result)
                        setMainUIBasedOnPermissions(DoctorVetApp.get().getUser().getPermissions());
                }
            });
        } else {
            setMainUIBasedOnPermissions(usersPermissions);
        }

        //FCM messaging
        if (!DoctorVetApp.get().existsLocalUserNotificationToken()) {
            DoctorVetApp.get().firebaseGetNotificationToken();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }

        //push messages permission
        askNotificationPermission();

        // Handle possible data accompanying notification message.
        if (existsFCMMessageWithPetID()) {
            Intent intent = new Intent(this, ViewPetActivity.class);
            intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), getFCMMessagePetID());
            getIntent().removeExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name());
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        moreMenuItem = menu.findItem(R.id.action_more);
        searchMenuItem = menu.findItem(R.id.action_search);

        if (tabInUse != TAB_NAV_PANELS.OWNERS)
            moreMenuItem.setVisible(false);

        switch (tabInUse) {
            case HOME:
            case DAILY_CASH:
            case REPORTS:
            case AGENDA:
                searchMenuItem.setVisible(false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_compacto) {
            getMainTabOwnersFragment().changeAdapter(DoctorVetApp.Adapter_types.COMPACT);
            //mainTabOwnersFragment.changeAdapter(DoctorVetApp.Adapter_types.COMPACT);
            return true;
        } else if (id == R.id.action_extendido) {
            getMainTabOwnersFragment().changeAdapter(DoctorVetApp.Adapter_types.EXTENDED);
            //mainTabOwnersFragment.changeAdapter(DoctorVetApp.Adapter_types.EXTENDED);
            return true;
        }

        if (id == R.id.action_search) {
            Intent intent = null;

            switch (tabInUse) {
                case OWNERS:
                    intent = new Intent(MainActivity.this, SearchOwnerActivity.class);
                    intent.putExtra(DoctorVetApp.INTENT_SEARCH_VIEW, true);
                    break;
                case PETS:
                    intent = new Intent(MainActivity.this, SearchPetActivity.class);
                    intent.putExtra(DoctorVetApp.INTENT_SEARCH_VIEW, true);
                    break;
            }

            if (intent != null)
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private MainTabOwnersFragment getMainTabOwnersFragment() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + TAB_NAV_PANELS.OWNERS.ordinal()  /*mViewPager.getCurrentItem()*/);
        return (MainTabOwnersFragment) page;
    }
    public MainTabReportsFragment getMainTabReportsFragment() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + TAB_NAV_PANELS.REPORTS.ordinal()  /*mViewPager.getCurrentItem()*/);
        return (MainTabReportsFragment) page;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            mViewPager.setCurrentItem(TAB_NAV_PANELS.HOME.ordinal());
        } else if (id == R.id.nav_owners) {
            mViewPager.setCurrentItem(TAB_NAV_PANELS.OWNERS.ordinal());
        } else if (id == R.id.nav_pets) {
            mViewPager.setCurrentItem(TAB_NAV_PANELS.PETS.ordinal());
        } else if (id == R.id.nav_agenda) {
            mViewPager.setCurrentItem(TAB_NAV_PANELS.AGENDA.ordinal());
        } else if (id == R.id.nav_daily_cash) {
            mViewPager.setCurrentItem(TAB_NAV_PANELS.DAILY_CASH.ordinal());
        } else if (id == R.id.nav_reportes) {
            mViewPager.setCurrentItem(TAB_NAV_PANELS.REPORTS.ordinal());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mViewPager.getCurrentItem() != TAB_NAV_PANELS.HOME.ordinal()) {
                mViewPager.setCurrentItem(TAB_NAV_PANELS.HOME.ordinal(), false);
                navigationView.setCheckedItem(R.id.nav_home);
            } else {
                super.onBackPressed();
            }
        }
    }

    private void LoadVet(final Vet vet, ConstraintLayout vet_layout) {
        ImageView img_thumb = vet_layout.findViewById(R.id.img_thumb);
        TextView title = vet_layout.findViewById(R.id.txt_title);
        TextView sub_title = vet_layout.findViewById(R.id.txt_subtitle);
        title.setText(vet.getName());
        sub_title.setText(vet.getEmail());
        DoctorVetApp.get().setThumb(vet.getThumb_url(), img_thumb, R.drawable.ic_store_holo_dark);
    }
    private void LoadUser(final User usr, ConstraintLayout user_layout) {
        ImageView img_thumb = user_layout.findViewById(R.id.img_thumb);
        TextView title = user_layout.findViewById(R.id.txt_title);
        TextView sub_title = user_layout.findViewById(R.id.txt_subtitle);
        title.setText(usr.getName());
        sub_title.setText(usr.getEmail());
        DoctorVetApp.get().setThumb(usr.getThumb_url(), img_thumb, R.drawable.ic_account_circle_dark);
    }
    private void showMoreItem(boolean b) {
        if (moreMenuItem != null)
            moreMenuItem.setVisible(b);
    }
    private void showSearchItem(boolean b) {
        if (searchMenuItem != null)
            searchMenuItem.setVisible(b);
    }
    public void setTitle(String text) {
        toolbar.setTitle(text);
    }
    public Toolbar getToolbar() {
        return toolbar;
    }
    public void set_view_pager_current_item(MainActivity.TAB_NAV_PANELS tab) {
        mViewPager.setCurrentItem(tab.ordinal(), false);
        if (tab.ordinal() == TAB_NAV_PANELS.REPORTS.ordinal()) {
//            getMainTabReportsFragment().hideRecyclerView();
            navigationView.setCheckedItem(R.id.nav_reportes);
        }
    }
    private void setMainUIBasedOnPermissions(Users_permissions permissions) {
        //navigationView.getMenu().getItem(TAB_NAV_PANELS.AGENDA.ordinal()).setTitle(DoctorVetApp.get().getAgendaTitle());
        navigationView.getMenu().getItem(TAB_NAV_PANELS.DAILY_CASH.ordinal()).setTitle(DoctorVetApp.get().getDailyCashTitle());
    }
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, getClass().getSimpleName());
        bottomSheetDialog.show(getSupportFragmentManager(), null);
    }
    private void askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean notificationPermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;

            if (!notificationPermissionGranted) {
                // ask for the permission
                //requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.POST_NOTIFICATIONS }, 100);
            }
        }
    }
    private boolean existsFCMMessageWithPetID() {
        //FCM message
        if (getIntent().getExtras() != null) {
            Object id_pet = getIntent().getExtras().get(DoctorVetApp.INTENT_VALUES.PET_ID.name());
            if (id_pet != null)
                return true;
        }

        return false;
    }
    private Integer getFCMMessagePetID() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, MainActivity.this);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TAB_NAV_PANELS tab = TAB_NAV_PANELS.values()[position];
            switch (tab){
                case HOME:
                    return new MainTabHomeFragment();
                case OWNERS:
                    return new MainTabOwnersFragment();
                case PETS:
                    return new MainTabPetsFragment();
                case AGENDA:
                    return new MainTabAgendaFragment();
                case DAILY_CASH:
                    return new MainTabDailyCashFragment();
                case REPORTS:
                    return new MainTabReportsFragment();
                default:
                    throw new RuntimeException("Fragment not exists");
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            TAB_NAV_PANELS tab = TAB_NAV_PANELS.values()[position];
            switch (tab) {
                case HOME:
                    return getString(R.string.inicio);
                case OWNERS:
                    return getString(R.string.owners);
                case PETS:
                    return getString(R.string.pets);
                case AGENDA:
                    return getString(R.string.agenda);
                case DAILY_CASH:
                    return getString(R.string.daily_cash);
                case REPORTS:
                    return getString(R.string.reportes);
            }
            return "";
        }
    }

}

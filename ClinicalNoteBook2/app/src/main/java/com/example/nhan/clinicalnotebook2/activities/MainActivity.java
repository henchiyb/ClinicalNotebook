package com.example.nhan.clinicalnotebook2.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.nhan.clinicalnotebook2.R;
import com.example.nhan.clinicalnotebook2.events.EventOpenListNote;
import com.example.nhan.clinicalnotebook2.events.EventShowSearchIcon;
import com.example.nhan.clinicalnotebook2.fragments.FragmentListFolder;
import com.example.nhan.clinicalnotebook2.fragments.FragmentListNote;
import com.example.nhan.clinicalnotebook2.fragments.FragmentMain;
import com.example.nhan.clinicalnotebook2.fragments.FragmentWithSearch;
import com.example.nhan.clinicalnotebook2.managers.FragmentType;
import com.example.nhan.clinicalnotebook2.managers.ScreenManager;
import com.example.nhan.clinicalnotebook2.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PATH = 1;
    private ScreenManager screenManager;
    private SearchView searchView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FragmentType currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                };

        if (!Utils.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
                NoteActivity.activityType = ActivityType.ADD_NOTE;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeFirstView();
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    private void initializeFirstView() {
        screenManager = new ScreenManager(this.getSupportFragmentManager(), R.id.container);
        currentFragment = ScreenManager.getCurrentFragment();
        if (currentFragment == FragmentType.MAIN) {
            screenManager.openFragment(FragmentMain.create(this.screenManager), false);
            getSupportActionBar().hide();
        }
    }

    @Subscribe
    public void openListNoteFragment(EventOpenListNote event) {
        switch (event.getFragmentType()){
            case LIST_NOTE:
                ScreenManager.setCurrentFragment(FragmentType.LIST_NOTE);
                screenManager.openFragment(FragmentListNote.create(this.screenManager), true);
                break;
            case LIST_NOTE_ONLY_IMAGE:
                ScreenManager.setCurrentFragment(FragmentType.LIST_NOTE_ONLY_IMAGE);
                screenManager.openFragment(FragmentListNote.create(this.screenManager), true);
                break;
            case LIST_NOTE_ONLY_RECORD:
                ScreenManager.setCurrentFragment(FragmentType.LIST_NOTE_ONLY_RECORD);
                screenManager.openFragment(FragmentListNote.create(this.screenManager), true);
                break;
        }

    }

    @Subscribe
    public void showSearchIcon(EventShowSearchIcon event){
        getSupportActionBar().show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (getFragmentManager().getBackStackEntryCount() == 0
                && ScreenManager.getCurrentFragment() != FragmentType.MAIN){
            if (ScreenManager.getCurrentFragment() == FragmentType.LIST_NOTE_ONLY_IMAGE) {
                screenManager.openFragment(FragmentListFolder.create(screenManager), false);
                ScreenManager.setCurrentFragment(FragmentType.FOLDER_IMAGE);
            } else if (ScreenManager.getCurrentFragment() == FragmentType.LIST_NOTE) {
                screenManager.openFragment(FragmentListFolder.create(screenManager), false);
                ScreenManager.setCurrentFragment(FragmentType.FOLDER_NOTE);
            } else if (ScreenManager.getCurrentFragment() == FragmentType.LIST_NOTE_ONLY_RECORD) {
                screenManager.openFragment(FragmentListFolder.create(screenManager), false);
                ScreenManager.setCurrentFragment(FragmentType.FOLDER_RECORD);
            } else if (ScreenManager.getCurrentFragment() == FragmentType.FOLDER_NOTE ||
                    ScreenManager.getCurrentFragment() == FragmentType.FOLDER_IMAGE ||
                    ScreenManager.getCurrentFragment() == FragmentType.FOLDER_RECORD) {
                screenManager.openFragment(FragmentMain.create(screenManager), false);
                ScreenManager.setCurrentFragment(FragmentType.MAIN);
            }
        }
        else {
            super.onBackPressed();
        }
        Log.d("dkm", "onBackPressed: " + ScreenManager.getCurrentFragment());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private MenuItemImpl menuItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuItem = (MenuItemImpl) menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                closeSearch();
            }
        });
        initSearchView(searchView);
        return true;
    }

    private void initSearchView(SearchView searchView) {
        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
        } catch (Exception e) {
        }
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                MainActivity.this.doSearch(editable.toString());
            }
        });
    }

    private void closeSearch() {
        FragmentWithSearch fragmentWithSearch = (FragmentWithSearch) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentWithSearch != null) {
            fragmentWithSearch.closeSearch();
        }
    }

    private void doSearch(String searchString) {
        FragmentWithSearch fragmentWithSearch = (FragmentWithSearch) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentWithSearch != null) {
            fragmentWithSearch.doSearch(searchString);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_folder:
                screenManager.openFragment(FragmentListFolder.create(this.screenManager), true);
                ScreenManager.setCurrentFragment(FragmentType.FOLDER_NOTE);
                break;
            case R.id.nav_image:
                screenManager.openFragment(FragmentListFolder.create(this.screenManager), true);
                ScreenManager.setCurrentFragment(FragmentType.FOLDER_IMAGE);
                break;
            case R.id.nav_record:
                screenManager.openFragment(FragmentListFolder.create(this.screenManager), true);
                ScreenManager.setCurrentFragment(FragmentType.FOLDER_RECORD);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

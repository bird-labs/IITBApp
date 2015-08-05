package com.gymkhana.iitbapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gymkhana.iitbapp.activity.LoginActivity;
import com.gymkhana.iitbapp.feed.RSSFeedService;
import com.gymkhana.iitbapp.fragment.DrawerFragment;
import com.gymkhana.iitbapp.fragment.GenericListFragment;
import com.gymkhana.iitbapp.fragment.HomeFragment;
import com.gymkhana.iitbapp.fragment.MenuFragment;
import com.gymkhana.iitbapp.fragment.RSSFragment;
import com.gymkhana.iitbapp.fragment.RSSSubscriptionFragment;
import com.gymkhana.iitbapp.items.GenericItem;
import com.gymkhana.iitbapp.util.AuthFunctions;
import com.gymkhana.iitbapp.util.Functions;
import com.gymkhana.iitbapp.util.ListContent;
import com.gymkhana.iitbapp.util.SharedPreferenceManager;

public class MainActivity extends ActionBarActivity implements DrawerFragment.OnFragmentInteractionListener {

    public static String FRAME_TYPE_KEY = "FRAME_TYPE";
    public static Integer SHOW_HOME = 0;
    public static Integer SHOW_EVENTS = 1;
    public static Integer SHOW_NEWS = 2;
    public static Integer SHOW_TIMETABLE = 3;
    public static Integer SHOW_ABOUT = 4;
    public static Integer SHOW_INFORMATION = 5;
    public static Integer SHOW_DEVELOPERS = 6;
    public static Integer SHOW_NOTICES = 7;
    public static Integer SHOW_FEED = 8;
    public static Integer SHOW_FEED_PREFERENCES = 9;

    private Context mContext;
    private DrawerFragment mDrawerFragment;
    private Integer mFragmentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        firstTimeSetup();
        ListContent.resetVariables();

        Functions.setActionBar(this);
        Functions.setActionBarTitle(this, getString(R.string.title_home));

        mDrawerFragment =
                (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_bar);
        mDrawerFragment.setup((DrawerLayout) findViewById(R.id.drawer_layout));
        ListView lv = mDrawerFragment.setupListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GenericItem glvi = (GenericItem) parent.getItemAtPosition(position);
                if (glvi.tag == DrawerFragment.DRAWER_TAG_HOME) {
                    displayFragment(SHOW_HOME);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_NEWS) {
                    displayFragment(SHOW_NEWS);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_NOTICES) {
                    displayFragment(SHOW_NOTICES);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_EVENTS) {
                    displayFragment(SHOW_EVENTS);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_INFORMATION) {
                    displayFragment(SHOW_INFORMATION);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_FEED) {
                    displayFragment(SHOW_FEED_PREFERENCES);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_TIMETABLE) {
                    displayFragment(SHOW_TIMETABLE);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_ABOUT) {
                    displayFragment(SHOW_ABOUT);
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_LOGIN) {
                    Intent loginIntent = new Intent(mContext, LoginActivity.class);
                    startActivity(loginIntent);
                    ((Activity) mContext).finish();
                } else if (glvi.tag == DrawerFragment.DRAWER_TAG_LOGOUT) {
                    AuthFunctions.logout(mContext);
                }
            }
        });

        if (AuthFunctions.isUserLoggedIn(mContext)) {
            displayFragment(MainActivity.SHOW_HOME);
        } else {
            displayFragment(MainActivity.SHOW_INFORMATION);
        }
    }

    public void displayFragment(int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        mFragmentPosition = position;

        if (position == SHOW_HOME) {
            fragment = new HomeFragment();
        } else if (position == SHOW_NEWS) {
            fragment = new MenuFragment();
            bundle.putInt(FRAME_TYPE_KEY, SHOW_NEWS);
        } else if (position == SHOW_EVENTS) {
            fragment = new MenuFragment();
            bundle.putInt(FRAME_TYPE_KEY, SHOW_EVENTS);
        } else if (position == SHOW_NOTICES) {
            fragment = new MenuFragment();
            bundle.putInt(FRAME_TYPE_KEY, SHOW_NOTICES);
        } else if (position == SHOW_INFORMATION) {
            ListContent.InformationFragmentData(mContext);
            GenericListFragment.mTitle = getString(R.string.drawer_information);
            GenericListFragment.mList = ListContent.mInformationList;
            GenericListFragment.mOnItemClickListener =
                    ListContent.mInformationOnItemClickListener;
            fragment = new GenericListFragment();
        } else if (position == SHOW_TIMETABLE) {
            GenericListFragment.mTitle = getString(R.string.drawer_timetable);
            ListContent.TimetableFragmentData(mContext);
            GenericListFragment.mList = ListContent.mTimetableList;
            GenericListFragment.mOnItemClickListener =
                    ListContent.mTimetableOnItemClickListener;
            fragment = new GenericListFragment();
        } else if (position == SHOW_ABOUT) {
            GenericListFragment.mTitle = getString(R.string.drawer_about);
            ListContent.SettingsFragmentData(mContext);
            GenericListFragment.mList = ListContent.mSettingsList;
            GenericListFragment.mOnItemClickListener =
                    ListContent.mSettingsOnItemClickListener;
            fragment = new GenericListFragment();
        } else if (position == SHOW_DEVELOPERS) {
            ListContent.DeveloperFragmentData(mContext);
            GenericListFragment.mTitle = getString(R.string.settings_about_us_title);
            GenericListFragment.mList = ListContent.mDeveloperList;
            GenericListFragment.mOnItemClickListener = null;
            fragment = new GenericListFragment();
        } else if (position == SHOW_FEED) {
            fragment = new RSSFragment();
        } else if (position == SHOW_FEED_PREFERENCES) {
            fragment = new RSSSubscriptionFragment();
        }

        fragment.setArguments(bundle);

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_fragment, fragment)
                    .commit();
            mDrawerFragment.mDrawerLayout.closeDrawers();
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mFragmentPosition == SHOW_DEVELOPERS) {
            displayFragment(SHOW_ABOUT);
        } else if (mFragmentPosition == SHOW_FEED) {
            displayFragment(SHOW_FEED_PREFERENCES);
        } else if (mFragmentPosition != SHOW_HOME) {
            displayFragment(SHOW_HOME);
        } else {
            finish();
        }
    }

    public void firstTimeSetup() {
        if (!(AuthFunctions.isUserLoggedIn(mContext) || AuthFunctions.isUserOffline(mContext))) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (!SharedPreferenceManager.load(
                mContext,
                SharedPreferenceManager.Tags.FIRST_TIME).contentEquals(getString(R.string.app_version))) {
            // This is the first time setup
            setupRecurringAlarm();
        }
    }

    public void setupRecurringAlarm() {
        if (SharedPreferenceManager.load(mContext, SharedPreferenceManager.Tags.ALARM)
                .contentEquals(SharedPreferenceManager.Tags.TRUE)) {
            return;
        }

        int INTERVAL = 60 * 60 * 1000;
        Intent intent = new Intent(mContext, RSSFeedService.class);
        PendingIntent pending_intent = PendingIntent.getService(mContext, 0, intent, 0);

        AlarmManager alarm_mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarm_mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime(), INTERVAL, pending_intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mDrawerFragment.mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerFragment.mDrawerLayout.closeDrawers();
            } else {
                mDrawerFragment.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

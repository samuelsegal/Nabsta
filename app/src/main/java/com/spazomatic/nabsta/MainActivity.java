package com.spazomatic.nabsta;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.spazomatic.nabsta.actionBar.NabstaActionProvider;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.fragments.NavigationDrawerFragment;
import com.spazomatic.nabsta.fragments.NewProjectDialog;
import com.spazomatic.nabsta.fragments.OpenProjectDialog;
import com.spazomatic.nabsta.fragments.Studio;
import com.spazomatic.nabsta.receivers.BatteryLevelReceiver;

public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        Studio.OnFragmentInteractionListener,
        NewProjectDialog.OnNewSongListener,
        OpenProjectDialog.OnOpenSongListener
{

    private NavigationDrawerFragment navigationDrawerFragment;
    private CharSequence title;
    private BatteryLevelReceiver batteryLevelReceiver;
    private IntentFilter batteryChanged;
    private NabstaActionProvider shareActionProvider;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //Hardware buttons setting to adjust the media

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_main);
        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        batteryLevelReceiver = new BatteryLevelReceiver();
        batteryChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        /*
        Fragment fragment;

        switch(position){
            case 0:
                fragment = Studio.newInstance("Default",1L);
                break;
            case 1:
                fragment = Studio.newInstance("Temp",1L);
                break;
            default:
                fragment = Studio.newInstance("Song",1L);
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
                */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            //MenuItem menuItem = menu.findItem(R.id.action_project);
            //shareActionProvider = (NabstaActionProvider) MenuItemCompat.getActionProvider(menuItem);
            // Set history different from the default before getting the action
            // view since a call to MenuItemCompat.getActionView() calls
            // onCreateActionView() which uses the backing file name. Omit this
            // line if using the default share history file is desired.
            //shareActionProvider.setShareHistoryFileName("custom_share_history.xml");

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(NabstaApplication.LOG_TAG, "onResume called...");
        NabstaApplication.activityResumed();
        //TODO Think of best solution for battery monitoring when has most all features developed.
        Log.d(NabstaApplication.LOG_TAG,"Register Batter receiver dynamically...");
        registerReceiver(batteryLevelReceiver, batteryChanged);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(NabstaApplication.LOG_TAG, "onPause called...");
        NabstaApplication.activityPaused();
        Log.d(NabstaApplication.LOG_TAG, "UnRegister Batter receiver dynamically...");
        unregisterReceiver(batteryLevelReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(NabstaApplication.LOG_TAG, "onStop called...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(NabstaApplication.LOG_TAG, "onDestroy called...");
    }

    @Override
    public void onNewSong(Song song) {
        openSong(song);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(NabstaApplication.LOG_TAG,"----------Studio is Calling---------------");
    }

    @Override
    public void onOpenSong(Song song) {
        openSong(song);
    }
    private void openSong(Song song){
        Log.d(NabstaApplication.LOG_TAG,String.format(
                "----------Opening Project %s---------------",
                song.getName()));
        getSupportActionBar().setTitle(song.getName());
        Fragment fragment = Studio.newInstance(song.getName(),song.getId());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
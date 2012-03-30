package com.projectsexception.mzdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.fragments.MainFragment;
import com.projectsexception.mzdroid.util.CustomLog;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener, View.OnClickListener {

    private static final int REQUEST_READ = 1;
    private static final int REQUEST_CONFIG = 2;
    
    private static final int[] ID_FRAGMENTS = {
        R.id.standings_fragment, 
        R.id.matches_next_fragment, 
        R.id.matches_played_fragment
    };
    
    private static final int[] ID_FIELDS = {
        R.id.more_button_standings, 
        R.id.row_standings, 
        R.id.more_button_matches,
        R.id.row_matches
    };
    
    public static interface ReloadFragment {
        
        public void reloadFragment(boolean cacheOnly);
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.more_button_standings).setOnClickListener(this);
        findViewById(R.id.more_button_matches).setOnClickListener(this);
        
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ab.setTitle(getString(R.string.app_name));
        
        showHideFragment();
    }
    
    private void showHideFragment() { 
        DBAdapter dbAdapter = DBAdapter.getInstance(this);
//        FragmentManager fm = getSupportFragmentManager();
        boolean hide = dbAdapter.readUserData() == null;
//        boolean changes = false;
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment fragment;
//        for (int i = 0; i < ID_FRAGMENTS.length; i++) {
//            fragment = fm.findFragmentById(ID_FRAGMENTS[i]);
//            if (fragment != null) {
//                if (fragment.isHidden() && !hide) {
//                    changes = true;
//                    ft.hide(fragment);
//                } else if (!fragment.isHidden() && hide) {
//                    changes = true;
//                    ft.show(fragment);
//                }
//            }
//        }
//        if (changes) {
//            ft.commit();
//        }
        
        int visiblility;
        if (hide) {
            visiblility = View.GONE;
        } else {
            visiblility = View.VISIBLE;
        }
        for (int i = 0; i < ID_FRAGMENTS.length; i++) {
            findViewById(ID_FRAGMENTS[i]).setVisibility(visiblility);
        }
        for (int i = 0; i < ID_FIELDS.length; i++) {
            findViewById(ID_FIELDS[i]).setVisibility(visiblility);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        ActionBar ab = getSupportActionBar();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.sections, R.layout.abs__list_menu_item_layout);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ab.setListNavigationCallbacks(list, this);
    }
    
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition == 1) {
            startActivity(new Intent(this, PlayersActivity.class));
        } else if (itemPosition == 2) {
            startActivity(new Intent(this, TrainingActivity.class));
        } else if (itemPosition == 3) {
            startActivity(new Intent(this, StandingsActivity.class));
        } else if (itemPosition == 4) {
            startActivity(new Intent(this, TacticsActivity.class));
        }
        return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_read_data:
            startActivityForResult(new Intent(this, ReaderActivity.class), REQUEST_READ);
            break;
        case R.id.menu_settings:
            startActivityForResult(new Intent(this, MZPreferenceActivity.class), REQUEST_CONFIG);
            break;
        }
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_READ) {
            switch (resultCode) {
            case RESULT_CANCELED:
                CustomLog.debug("MainActivity", "RESULT_CANCELED");               
                break;
            case RESULT_OK:
                CustomLog.debug("MainActivity", "RESULT_OK");
                break;
            case ReaderActivity.RESULT_ERROR:
                CustomLog.debug("MainActivity", "RESULT_ERROR");
                break;
            default:
                break;
            }
            reloadFragments();
            showHideFragment();
        } else if (requestCode == REQUEST_CONFIG) {
            if (resultCode == MZPreferenceActivity.DATABASE_CHANGED) {
                reloadFragments();
                showHideFragment();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadFragments() {
        FragmentManager fm = getSupportFragmentManager();
        ((MainFragment) fm.findFragmentById(R.id.main_fragment)).populateUserView(this);
        for (int i = 0; i < ID_FRAGMENTS.length; i++) {
            ((ReloadFragment) fm.findFragmentById(ID_FRAGMENTS[i])).reloadFragment(true);
        }        
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, StandingsActivity.class));
    }

}

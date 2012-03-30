package com.projectsexception.mzdroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.projectsexception.about.R;
import com.projectsexception.mzdroid.fragments.MatchesFragment;
import com.projectsexception.mzdroid.fragments.StandingsFragment;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class StandingsActivity extends SherlockFragmentActivity {
    
    private static final int[] TITLES = {
        R.string.standings,
        R.string.played_matches,
        R.string.next_matches
    };
    
    private static final int NUM_ITEMS = 3;
    
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private MainActivity.ReloadFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        //This has to be called before setContentView and you must use the
        //class in android.support.v4.view and NOT android.view      
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.standings);
        
        fragments = new MainActivity.ReloadFragment[3];
        
        mAdapter = new MyAdapter(this, getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        
        
        // Parte nueva: Añadimos el Adapter al indicador
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        titleIndicator.setViewPager(mPager);
        
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
        ab.setTitle("Standings & Matches");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.reload, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menu_reload) {
            int currentItem = mPager.getCurrentItem();
            if (currentItem < fragments.length && fragments[currentItem] != null) {
                fragments[mPager.getCurrentItem()].reloadFragment(false);
            }
        }
        return true;
    }

    public class MyAdapter extends FragmentPagerAdapter implements TitleProvider {
        
        private Context context;
        
        public MyAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            this.context = ctx;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments[position] == null) {
                if (position == 0) {
                    fragments[position] = new StandingsFragment();
                } else {
                    Bundle data = new Bundle();
                    if (position == 1) {
                        data.putString(MatchesFragment.TAG_MATCHES, context.getString(R.string.tag_matches_next));
                    } else if (position == 2) {
                        data.putString(MatchesFragment.TAG_MATCHES, context.getString(R.string.tag_matches_played));
                    }
                    MatchesFragment f = new MatchesFragment();
                    f.setArguments(data);
                    fragments[position] = f;
                }
            }
            return (Fragment) fragments[position];
        }

        @Override
        public String getTitle(int position) {
            return context.getString(TITLES[position]);
        }
    }

}

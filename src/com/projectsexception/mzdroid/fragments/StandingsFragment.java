package com.projectsexception.mzdroid.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projectsexception.mz.htmlapi.HTMLParser;
import com.projectsexception.mz.htmlapi.model.LeagueTeam;
import com.projectsexception.mz.htmlapi.model.Team;
import com.projectsexception.mzdroid.MainActivity;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.db.Serializator;
import com.projectsexception.mzdroid.util.CustomLog;

public class StandingsFragment extends Fragment implements MainActivity.ReloadFragment {
    
    private static final int NUM_TEAMS_BEFORE = 2;
    private static final int NUM_TEAMS_AFTER = 2;
    private static final String LEAGUE_STATE = "leagueState";
    
    private LayoutInflater inflater;
    private ReadStandingsTask task;
    private Team userTeam;
    private List<LeagueTeam> league;
    private boolean mainActivity;
    
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = getActivity().getClass().equals(MainActivity.class);
        
        userTeam = DBAdapter.getInstance(getActivity()).readUserTeam();
        
        if (userTeam == null) {
            if (!mainActivity) {
                getActivity().finish();
            }
            return;
        }      
        
        if (savedInstanceState != null) {
            league = (List<LeagueTeam>) savedInstanceState.getSerializable(LEAGUE_STATE);
        }
        
        if (league == null) {
            reloadFragment(mainActivity);
        } else {
            populateView();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater; 
        View view = inflater.inflate(R.layout.standings_fragment, null);
        return view;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LEAGUE_STATE, (Serializable) league);
    }
    
    protected void populateView() {

        TextView textView = (TextView) getView().findViewById(R.id.standings_title);
        textView.setText(getActivity().getString(R.string.league_title, userTeam.getSeriesName())); 
        
        View teamView;
        int realPosition = 0;
        int leagueSize = league.size();
        List<LeagueTeam> standingsLeague;
        if (mainActivity) {
            standingsLeague = new ArrayList<LeagueTeam>();
            realPosition = shrinkLeague(standingsLeague);
            getView().findViewById(R.id.standings_titles).setVisibility(View.GONE);
        } else {
            standingsLeague = league;
            getView().findViewById(R.id.standings_titles).setVisibility(View.VISIBLE);
        }
        ViewGroup standings = (ViewGroup) getView().findViewById(R.id.standings_table);
        int position = 1;
        int max = standings.getChildCount();
        for (LeagueTeam team : standingsLeague) {
            if ((position) < max) {
                // En ese caso la tabla tiene elementos y el de esta posición
                // puede modificarse
                teamView = standings.getChildAt(position);
            } else {
                teamView = inflater.inflate(R.layout.standings_fragment_team, null);
                standings.addView(teamView);
            }
            teamView.setTag(team);
            textView = (TextView) teamView.findViewById(R.id.team_position);
            textView.setText(Integer.toString(realPosition + 1));
            int color = colorPosition(realPosition, leagueSize);
            if (color > 0) {
                textView.setBackgroundColor(getResources().getColor(color));
            }
            
            textView = (TextView) teamView.findViewById(R.id.team_name);
            textView.setText(team.getTeamName());
            if (team.getTeamId() == userTeam.getTeamId()) {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                textView.setTypeface(Typeface.DEFAULT);
            }
            
            ((TextView) teamView.findViewById(R.id.team_points)).setText(Integer.toString(team.getPoints()));
            position++;
            realPosition++;
        }
        if ((position) < max) {
            // Aún quedan elementos que no deberían estar aquí
            for (int i = position ; i < max ;  i++) {
                standings.removeViewAt(position);
            }
        }
    }

    @Override
    public void reloadFragment(boolean cacheOnly) {
        if (userTeam == null) {
            userTeam = DBAdapter.getInstance(getActivity()).readUserTeam();
        }
        if (userTeam != null) {
            
            TextView textView = (TextView) getView().findViewById(R.id.standings_title);
            textView.setText(getActivity().getString(R.string.league_title, userTeam.getSeriesName())); 
            
            if (task == null || task.getStatus() == AsyncTask.Status.FINISHED || task.isCancelled()) {
                task = new ReadStandingsTask();
                int cache = cacheOnly ? 1 : 0;
                task.execute(userTeam.getSeriesId(), cache);
            } else {
                CustomLog.info("StandingsFragment", "There are another task running");
            }
        }
    }
    
    private int shrinkLeague(List<LeagueTeam> standingsLeague) {
        int position = 0;
        int playerTeamPos = league.size();
        for (LeagueTeam team : league) {                
            standingsLeague.add(team);            
            if (team.getTeamId() == userTeam.getTeamId()) {
                playerTeamPos = position;
            } else if ((playerTeamPos + NUM_TEAMS_AFTER) == position) {
                break;
            }
            position++;
        }
        position = 0;
        if (playerTeamPos > NUM_TEAMS_BEFORE) {
            position = playerTeamPos - NUM_TEAMS_BEFORE;
            for (int i = 0 ; i < playerTeamPos - NUM_TEAMS_BEFORE ; i++) {
                standingsLeague.remove(0);
            }
        }
        return position;
    }
    
    private int colorPosition(int position, int leagueSize) {
        if (position == 0) {
            return R.color.standing_1;
        } else if (position == 1 || position == 2) {
            return R.color.standing_2;
        } else if (position == leagueSize - 4 || position == leagueSize - 5) {
            return R.color.standing_3;
        } else if (position == leagueSize - 1 || position == leagueSize - 2  || position == leagueSize - 3) {
            return R.color.standing_4;
        } else {
            return R.color.standing_5;
        }
    }
    
    class ReadStandingsTask extends AsyncTask<Integer, Void, Void> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getView().findViewById(R.id.standings_progress).setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(Integer... params) {
            // Primero intentamos cargar de caché
            league = (List<LeagueTeam>) Serializator.loadObject(getActivity(), Serializator.Type.LEAGUE_TEAM);
            if (params[1] == 0 || league == null) {
                publishProgress(new Void[0]);
                league = HTMLParser.league(params[0]);
                if (league != null) {
                    Serializator.saveObject(getActivity().getApplicationContext(), (Serializable) league, Serializator.Type.LEAGUE_TEAM);
                }
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (league != null) {                
                populateView();
            }
            getView().findViewById(R.id.standings_progress).setVisibility(View.GONE);
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (league != null) {
                populateView();
            }
        }
        
    }

}

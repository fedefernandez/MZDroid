package com.projectsexception.mzdroid.fragments;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projectsexception.mz.htmlapi.HTMLParser;
import com.projectsexception.mz.htmlapi.model.Match;
import com.projectsexception.mz.htmlapi.model.Team;
import com.projectsexception.mzdroid.MainActivity;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.db.Serializator;

public class MatchesFragment extends ListFragment implements MainActivity.ReloadFragment {
    
    public static final String TAG_MATCHES = "tag_matches";
    
    private static final String MATCH_STATE = "matchState";
    
    private LayoutInflater inflater;
    private ReadMatchesTask task;
    private Team userTeam;
    private List<Match> matches;
    private boolean mainApp;
    private boolean played;
    private SimpleDateFormat formatter;
    
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        
        mainApp = getActivity().getClass().equals(MainActivity.class);
        
        played = true;
        if (getView() != null && getView().getTag() != null) {            
            played = getString(R.string.tag_matches_next).equals(getView().getTag());
        } else {
            if (getArguments().containsKey(TAG_MATCHES)) {
                String param = getArguments().getString(TAG_MATCHES);
                played = getString(R.string.tag_matches_next).equals(param);
            }
        }
        
        userTeam = DBAdapter.getInstance(getActivity()).readUserTeam();
        
        if (userTeam == null) {
            if (mainApp) {
                return;
            } else {
                getActivity().finish();
            }
        }
        
        if (savedInstanceState != null) {
            matches = (List<Match>) savedInstanceState.getSerializable(MATCH_STATE);
        }
        
        if (mainApp) {
            int title;
            if (played) {
                title = R.string.played_match;
            } else {
                title = R.string.next_match;
            }
            ((TextView) getView().findViewById(R.id.matches_title)).setText(title);
        } else {
            getView().findViewById(R.id.matches_title).setVisibility(View.GONE);
        }
        
        
        setListAdapter(new MatchesAdapter());
        ListView listView = getListView();
        listView.setVerticalScrollBarEnabled(false);
        
        if (matches == null) {
            reloadFragment(false);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater; 
        View view = inflater.inflate(R.layout.matches_fragment, null);
        return view;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MATCH_STATE, (Serializable) matches);
    }
    
    public void populateView() {
        if (getListAdapter() == null) {
            setListAdapter(new MatchesAdapter());
        } else {
            ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }
    
    @Override
    public void reloadFragment(boolean cacheOnly) {
        if (userTeam == null) {
            userTeam = DBAdapter.getInstance(getActivity()).readUserTeam();
        }
        if (task == null || task.getStatus() == AsyncTask.Status.FINISHED || task.isCancelled()) {
            task = new ReadMatchesTask();
            int cache = cacheOnly ? 1 : 0;
            task.execute(userTeam.getTeamId(), cache);
        } else {
            Toast.makeText(getActivity(), "Recarga actualmente en ejecución", Toast.LENGTH_SHORT).show();
        }
    }
    
    class MatchesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (matches != null) {
                return matches.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (position < getCount()) {
                return matches.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {            
            View view = convertView;            
            if (view == null) {
                view = inflater.inflate(R.layout.matches_fragment_row, null);
            }
            
            Match match = (Match) getItem(position);
            
            TextView textView = (TextView) view.findViewById(R.id.date);
            textView.setText(formatter.format(match.getDate()));            
            
            textView = (TextView) view.findViewById(R.id.team1_name);
            textView.setText(match.getHomeTeam().getTeamName());
            if (userTeam.getTeamId() == match.getHomeTeam().getTeamId()) {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                textView.setTypeface(Typeface.DEFAULT);
            }
            
            String score;
            if (played) {
                score = getString(R.string.match_score, match.getHomeGoals(), match.getAwayGoals());
            } else {
                score = "-";
            }
            ((TextView) view.findViewById(R.id.score)).setText(score);
            
            textView = (TextView) view.findViewById(R.id.team2_name);
            textView.setText(match.getAwayTeam().getTeamName());
            if (userTeam.getTeamId() == match.getAwayTeam().getTeamId()) {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                textView.setTypeface(Typeface.DEFAULT);
            }
            
            return view;
        }
        
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
        
        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        
    }
    
    class ReadMatchesTask extends AsyncTask<Integer, Void, Void> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getView().findViewById(R.id.matches_progress).setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(Integer... params) {
            // Primero intentamos cargar de caché
            matches = (List<Match>) Serializator.loadObject(getActivity(), Serializator.Type.NEXT_MATCHES);
            if (params[1] == 0 || matches == null) {
                publishProgress(new Void[0]);
                int numMatches = 50;
                if (mainApp) {
                    numMatches = 1;
                }
                matches = HTMLParser.matches(params[0], played, numMatches, null);
                if (matches != null) {
                    Serializator.saveObject(getActivity(), (Serializable) matches, Serializator.Type.NEXT_MATCHES);
                }
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (matches != null) {
                populateView();
            }
            if (getView() != null) {
                getView().findViewById(R.id.matches_progress).setVisibility(View.GONE);
            }
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (matches != null) {
                populateView();
            }
        }
        
    }

}

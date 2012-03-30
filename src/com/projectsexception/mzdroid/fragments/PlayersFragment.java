package com.projectsexception.mzdroid.fragments;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.projectsexception.mz.htmlapi.model.Player;
import com.projectsexception.mzdroid.PlayerDetailsActivity;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.util.ViewUtil;

public class PlayersFragment extends ListFragment {
    
    private static final int DETAILS_ID = R.id.fragment_layout;
    
    private boolean dualPane;
    private int currentPosition;
    
    protected List<Player> players;
    
    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        DBAdapter adapter = DBAdapter.getInstance(getActivity());
        players = adapter.readPlayers(false);
        
        setListAdapter(new PlayerAdapter(getActivity()));
        ListView listView = getListView();
        listView.setVerticalScrollBarEnabled(false);
        listView.setCacheColorHint(0);
        
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(DETAILS_ID);
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedState != null) {
            // Restore last state for checked position.
            currentPosition = savedState.getInt("curChoice", 0);
        }

        if (dualPane) {
            // In dual-pane mode, list view highlights selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(currentPosition);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", currentPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        showDetails(pos);
    }
    
    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        currentPosition = index;

        Player player = (Player) getListView().getAdapter().getItem(index); 
        
        if (dualPane) {
            // We can display everything in-place with fragments.
            // Have the list highlight this item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is shown, replace if needed.
            PlayerDetailsFragment details = (PlayerDetailsFragment) getFragmentManager().findFragmentById(DETAILS_ID);
            if (details == null || details.getPlayerId() != player.getId()) {
                // Make new fragment to show this selection.
                details = PlayerDetailsFragment.newInstance(player.getId());

                // Execute a transaction, replacing any existing
                // fragment with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(DETAILS_ID, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerDetailsActivity.class);
            intent.putExtra(PlayerDetailsFragment.PLAYER_ID_ARG, player.getId());
            startActivity(intent);
        }
    }
    
    class PlayerAdapter extends BaseAdapter {
        
        private LayoutInflater inflater;
        
        public PlayerAdapter(Context ctx) {
            inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) {
            return players.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            
            if (view == null) {
                view = inflater.inflate(R.layout.player_item, null);
            }
            
            Drawable drawable = getResources().getDrawable(R.drawable.shirt);
            drawable.setColorFilter(Color.RED, Mode.MULTIPLY);
            
            view.findViewById(R.id.player_shirt).setBackgroundDrawable(drawable);
            
            Player player = (Player) getItem(position);
            ((TextView) view.findViewById(R.id.player_number)).setText(Integer.toString(player.getNumber()));
            ((TextView) view.findViewById(R.id.player_name)).setText(player.getName());
            
            
            
            ((TextView) view.findViewById(R.id.player_age)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_age, player.getAge()));
            ((TextView) view.findViewById(R.id.player_height)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_height, player.getHeight()));
            ((TextView) view.findViewById(R.id.player_weight)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_weight, player.getWeight()));
            ((TextView) view.findViewById(R.id.player_value)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_value, player.getValue()));
            ((TextView) view.findViewById(R.id.player_salary)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_salary, player.getSalary()));
            
            return view;
        }
        
    }

}

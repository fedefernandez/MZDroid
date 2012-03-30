package com.projectsexception.mzdroid.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.TrainingDetailsActivity;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.util.CalendarUtil;

public class TrainingFragment extends ListFragment {
    
    private static final String STATE_POSITION = "position";
    private static final String STATE_WEEK = "week";
    private static final String STATE_DAY = "day";

    private static final int DETAILS_ID = R.id.training_details;
    
    private boolean dualPane;
    private int currentPosition;
    private String currentWeek;
    private int currentDay;
    
    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        DBAdapter adapter = DBAdapter.getInstance(getActivity());
        Cursor cursor = adapter.readTrainingDaysCursor();
        
        setListAdapter(new TrainingAdapter(getActivity(), cursor, false));
        ListView listView = getListView();
        listView.setVerticalScrollBarEnabled(false);
        listView.setCacheColorHint(0);
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(DETAILS_ID);
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedState != null) {
            // Restore last state for checked position.
            currentPosition = savedState.getInt(STATE_POSITION, 0);
            currentWeek = savedState.getString(STATE_WEEK);
            currentDay = savedState.getInt(STATE_DAY, 1);
        }

        if (dualPane) {
            // In dual-pane mode, list view highlights selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(currentPosition, currentWeek, currentDay);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_fragment, null);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_POSITION, currentPosition);
        outState.putString(STATE_WEEK, currentWeek);
        outState.putInt(STATE_DAY, currentDay);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        String week = (String) v.getTag(R.id.training_week_tag);
        int day = (Integer) v.getTag(R.id.training_day_tag);
        showDetails(pos, week, day);
    }
    
    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index, String week, int day) {
        currentPosition = index;
        currentWeek = week;
        currentDay = day;
        
        if (dualPane) {
            // We can display everything in-place with fragments.
            // Have the list highlight this item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is shown, replace if needed.
            TrainingDetailsFragment details = (TrainingDetailsFragment) getFragmentManager().findFragmentById(DETAILS_ID);
            if (details == null || !equals(week, details.getWeek()) || details.getDay() != day) {
                // Make new fragment to show this selection.
                details = TrainingDetailsFragment.newInstance(week, day);

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
            intent.setClass(getActivity(), TrainingDetailsActivity.class);
            intent.putExtra(TrainingDetailsFragment.WEEK_ARG, week);
            intent.putExtra(TrainingDetailsFragment.DAY_ARG, day);
            startActivity(intent);
        }
    }
    
    public boolean equals(String a, String b) {
        if (a != null) {
            return a.equals(b);
        }
        return false;
    }
    
    class TrainingAdapter extends CursorAdapter {
        
        private LayoutInflater inflater;
        private SimpleDateFormat formatter;

        public TrainingAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            formatter = new SimpleDateFormat(context.getString(R.string.training_format_date));
        }

        @Override
        public void bindView(View view, Context ctx, Cursor cursor) {
            String week = cursor.getString(1);
            int day = cursor.getInt(2);
            view.setTag(R.id.training_week_tag, week);
            view.setTag(R.id.training_day_tag, day);
            
            String text;
            Date date = CalendarUtil.parseWeekAndDay(week, day);
            if (date == null) {
                text = getActivity().getString(R.string.training_unknown_date);
            } else {
                text = formatter.format(date);
            }
            
            TextView trainingDate = (TextView) view.findViewById(R.id.training_date);
            trainingDate.setText(text);
        }

        @Override
        public View newView(Context ctx, Cursor cursor, ViewGroup root) {
            return inflater.inflate(R.layout.training_item, null);
        }
        
        @Override
        public long getItemId(int position) {            
            return super.getItemId(position);
        }
        
    }

}

package com.projectsexception.mzdroid.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.projectsexception.mz.htmlapi.model.Training;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.util.CustomLog;
import com.projectsexception.mzdroid.util.MZUtil;

public class PlayerTrainingFragment extends ListFragment {
    
    public static final String PLAYER_ID_ARG = "playerId";
    
//    private LayoutInflater inflater;
    
    public static PlayerTrainingFragment newInstance(int playerId) {
        PlayerTrainingFragment f = new PlayerTrainingFragment();

        Bundle args = new Bundle();
        args.putInt(PLAYER_ID_ARG, playerId);
        f.setArguments(args);

        return f;
    }

    public int getPlayerId() {
        return getArguments().getInt(PLAYER_ID_ARG, 0);
    }
    
    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        if (getPlayerId() == 0) {
            return;
        }
        
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Cursor cursor = DBAdapter.getInstance(getActivity()).readTrainingsCursor(getPlayerId());
        
        setListAdapter(new TrainingAdapter(inflater, getActivity(), cursor, true));
        ListView listView = getListView();
        listView.setVerticalScrollBarEnabled(false);
        listView.setCacheColorHint(0);
    }
    
    class TrainingAdapter extends CursorAdapter {
        
        final String format = "yyyy_MM_dd";
        final Calendar calendar = GregorianCalendar.getInstance();

        private LayoutInflater inflater;
        private SimpleDateFormat formatter;

        public TrainingAdapter(LayoutInflater inflater, Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            this.inflater = inflater;
            this.formatter = new SimpleDateFormat();
        }
        
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
        
        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Training t = DBAdapter.parseCursorTraining(cursor);
            String date = t.getWeek();
            int day = t.getDay();
            try {
                formatter.applyPattern(format);
                calendar.setTime(formatter.parse(date));
                if (day > 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, day - 1);
                }
                formatter.applyPattern("dd/MM/yyyy");
                date = formatter.format(calendar.getTime());
            } catch (ParseException e) {
                CustomLog.error("PlayerDetailsFragment", "Error parseando fecha: " + date);
                date = "";
            }
            ((TextView) view.findViewById(R.id.training_date)).setText(date);
            if (t.getSkill() != null) {
                ((TextView) view.findViewById(R.id.training_skill)).setText(MZUtil.SKILL_TITLES.get(t.getSkill()));
            }
            String level = Integer.toString(t.getLevel());
            if (t.isBall()) {
                level = level + "*";
            }
            ((TextView) view.findViewById(R.id.training_level)).setText(level);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(R.layout.dialog_training, null);
        }
        
    }

}

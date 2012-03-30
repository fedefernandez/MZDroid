package com.projectsexception.mzdroid.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.projectsexception.mz.htmlapi.model.Player;
import com.projectsexception.mz.htmlapi.model.Training;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.util.CalendarUtil;
import com.projectsexception.mzdroid.util.MZUtil;

public class TrainingDetailsFragment extends ListFragment {
    
    public static final String WEEK_ARG = "week";
    public static final String DAY_ARG = "day";
    
    private Map<Integer, Player> players;
    
    public static TrainingDetailsFragment newInstance(String week, int day) {
        TrainingDetailsFragment f = new TrainingDetailsFragment();
        
        Bundle args = new Bundle();
        args.putString(WEEK_ARG, week);
        args.putInt(DAY_ARG, day);
        f.setArguments(args);
        
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_fragment, container, false);
        TextView textView = (TextView) view.findViewById(R.id.training_title);
        String week = getWeek();
        int day = getDay();
        if (week == null || day == 0) {
            textView.setText(R.string.training_details_problem);
        } else {            
            SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.training_format_date));
            String text;
            Date date = CalendarUtil.parseWeekAndDay(week, day);
            if (date == null) {
                text = getActivity().getString(R.string.training_unknown_date);
            } else {
                text = formatter.format(date);
            }
            textView.setText(text);
        }
        return view;
    }
    
    public TrainingDetailsFragment() {
        super();
        players = new HashMap<Integer, Player>();
    }
    
    public String getWeek() {
        return getArguments().getString(WEEK_ARG);
    }
    
    public int getDay() {
        return getArguments().getInt(DAY_ARG);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String week = getWeek();
        int day = getDay();
        if (week == null || day == 0) {
            return;
        }
      
        DBAdapter dbAdapter = DBAdapter.getInstance(getActivity());
        Cursor cursor = dbAdapter.readTrainingsCursor(week, day, 0);
        
        setListAdapter(new TrainingDetailsAdapter(getActivity(), cursor, false));
        ListView listView = getListView();
        listView.setVerticalScrollBarEnabled(false);
        listView.setCacheColorHint(0);
    }
    
    class TrainingDetailsAdapter extends CursorAdapter {
        
        private LayoutInflater inflater;

        public TrainingDetailsAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void bindView(View view, Context ctx, Cursor cursor) {
            Training t = DBAdapter.parseCursorTraining(cursor);
            Player player = players.get(t.getPlayerId());
            if (player == null && t.getPlayerId() > 0) {
                DBAdapter dbAdapter = DBAdapter.getInstance(getActivity());
                player = dbAdapter.readPlayer(t.getPlayerId());
                players.put(t.getPlayerId(), player);
            }
            
            Drawable drawable = getResources().getDrawable(R.drawable.shirt);
            drawable.setColorFilter(Color.RED, Mode.MULTIPLY);
            
            view.findViewById(R.id.player_shirt).setBackgroundDrawable(drawable);
            
            if (player == null) {
                ((TextView) view.findViewById(R.id.player_number)).setText("-");
                ((TextView) view.findViewById(R.id.player_name)).setText("-");
            } else {
                ((TextView) view.findViewById(R.id.player_number)).setText(Integer.toString(player.getNumber()));
                ((TextView) view.findViewById(R.id.player_name)).setText(player.getName());
            }
            
            if (t.getSkill() == null) {
                ((TextView) view.findViewById(R.id.player_skill)).setText("-");
            } else {
                ((TextView) view.findViewById(R.id.player_skill)).setText(MZUtil.SKILL_TITLES.get(t.getSkill()));
            }
            
//            TextView textView = (TextView) view.findViewById(R.id.skill_level);
//            textView.setText(Integer.toString(t.getLevel()));
        }

        @Override
        public View newView(Context ctx, Cursor cursor, ViewGroup root) {
            return inflater.inflate(R.layout.training_details, null);
        }
        
    }

}

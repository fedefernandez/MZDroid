package com.projectsexception.mzdroid.fragments;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectsexception.mz.htmlapi.model.Player;
import com.projectsexception.mz.htmlapi.model.Skill;
import com.projectsexception.mzdroid.PlayerTrainingActivity;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.model.Position;
import com.projectsexception.mzdroid.util.MZUtil;
import com.projectsexception.mzdroid.util.ViewUtil;

public class PlayerDetailsFragment extends Fragment implements View.OnClickListener {
    
    public static final String PLAYER_ID_ARG = "playerId";
    
//    private LayoutInflater inflater;
    
    public static PlayerDetailsFragment newInstance(int playerId) {
        PlayerDetailsFragment f = new PlayerDetailsFragment();

        Bundle args = new Bundle();
        args.putInt(PLAYER_ID_ARG, playerId);
        f.setArguments(args);

        return f;
    }

    public int getPlayerId() {
        return getArguments().getInt(PLAYER_ID_ARG, 0);
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        this.inflater = inflater;
        
        if (container == null || getPlayerId() == 0) {
            // Currently in a layout without a container, so no
            // reason to create our view.
            return null;
        }
        
        final int playerId = getPlayerId();

        DBAdapter dbAdapter = DBAdapter.getInstance(getActivity());
        final Player player = dbAdapter.readPlayer(playerId);
        
        final View playerDetails = inflater.inflate(R.layout.player_details, null);
        
        ((TextView) playerDetails.findViewById(R.id.player_name)).setText(player.getName());
        
        View playerMain = playerDetails.findViewById(R.id.player_main);
        if (playerMain != null) {
            // Rellenamos
            ((TextView) playerMain.findViewById(R.id.player_age)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_age, player.getAge()));
            ((TextView) playerMain.findViewById(R.id.player_height)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_height, player.getHeight()));
            ((TextView) playerMain.findViewById(R.id.player_weight)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_weight, player.getWeight()));
            ((TextView) playerMain.findViewById(R.id.player_value)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_value, player.getValue()));
            ((TextView) playerMain.findViewById(R.id.player_salary)).setText(
                    ViewUtil.createSpannable(getActivity(), R.string.players_salary, player.getSalary()));
        }
        
        
        if (player.getSkills() != null) {
            ViewGroup viewGroup = (ViewGroup) playerDetails.findViewById(R.id.player_skills);            
            for (Skill skill : player.getSkills()) {
                viewGroup.addView(createSkillView(inflater, skill));
            }
        }
        
        ((TextView) playerDetails.findViewById(R.id.more_text)).setText("Ver entrenamiento"); 
        playerDetails.findViewById(R.id.more_training).setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PlayerTrainingActivity.class);
                intent.putExtra(PlayerDetailsFragment.PLAYER_ID_ARG, player.getId());
                startActivity(intent);
            }
        });
        
        View button = playerDetails.findViewById(R.id.player_positions_button);
        button.setOnClickListener(new View.OnClickListener() {                
            @Override
            public void onClick(View v) {
                showPositions(inflater, player.getSkills());
            }
        });
        
        final View notesView = playerDetails.findViewById(R.id.player_notes);
        String notes = dbAdapter.readPlayerNotes(playerId);        
        ((TextView) notesView.findViewById(R.id.player_notes_text)).setText(notes);
        
        playerDetails.findViewById(R.id.player_notes_show).setTag(playerId);
        playerDetails.findViewById(R.id.player_notes_button).setOnClickListener(this);
        playerDetails.findViewById(R.id.player_notes_ok).setOnClickListener(this);
        playerDetails.findViewById(R.id.player_notes_cancel).setOnClickListener(this);
        
        return playerDetails;
    }

    private View createSkillView(LayoutInflater inflater, final Skill skill) {
        View skillView = inflater.inflate(R.layout.player_details_skill, null);
        TextView skillTitle = (TextView) skillView.findViewById(R.id.skill_title);
        skillTitle.setText(MZUtil.SKILL_TITLES.get(skill.getType()));
        final ViewGroup group = (ViewGroup) skillView.findViewById(R.id.skill_value);
        populateBalls(group, skill.getValue(), skill.isMaxed());
        
        skillView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(MZUtil.SKILL_TITLES.get(skill.getType()));
                if (skill.isMaxed()) {
                    builder.setMessage("La habilidad parece capada, ¿es cierto?");
                } else {
                    builder.setMessage("¿Marcar la habilidad como capada?");
                }
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        skill.setMaxed(true);
                        group.removeAllViews();
                        populateBalls(group, skill.getValue(), skill.isMaxed());
                        if (skill.getId() != 0) {
                            DBAdapter dbAdapter = DBAdapter.getInstance(getActivity());
                            dbAdapter.updateSkill(skill);
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        skill.setMaxed(false);
                        group.removeAllViews();
                        populateBalls(group, skill.getValue(), skill.isMaxed());
                        if (skill.getId() != 0) {
                            DBAdapter dbAdapter = DBAdapter.getInstance(getActivity());
                            dbAdapter.updateSkill(skill);
                        }
                    }
                });
                builder.create().show();
            }
        });
        
        return skillView;
    }
    
    protected void populateBalls(ViewGroup group, int level, boolean maxed) {
        ImageView imageView;
        int ball;
        if (maxed) {
            ball = R.drawable.ball_maxed;
        } else {
            ball = R.drawable.ball;
        }
        for (int i = 0 ; i < level ; i++) {
            imageView = new ImageView(getActivity());
            imageView.setImageResource(ball);
            group.addView(imageView);
        }
    }
    
    protected void showPositions(LayoutInflater inflater, List<Skill> skills) {
        if (getView() == null) {
            return;
        }
        ViewGroup positionsTable = (ViewGroup) getView().findViewById(R.id.player_positions_table);
        ImageView image = (ImageView) getView().findViewById(R.id.player_positions_arrow);
        if (positionsTable != null) {
            if (positionsTable.getVisibility() == View.VISIBLE) {
                positionsTable.setVisibility(View.GONE);
                image.setImageResource(R.drawable.ic_more_arrow_down);
            } else {
                positionsTable.removeAllViews();
                final List<Position> positions = MZUtil.calculatePositions(skills, true);
                if (positions != null) {
                    for (Position pos : positions) {
                        View posView = inflater.inflate(R.layout.player_position, null);
                        ((TextView) posView.findViewById(R.id.position_title)).setText(MZUtil.POSITIONS_TITLES.get(pos.getType()));
                        ((TextView) posView.findViewById(R.id.position_value)).setText(Integer.toString(pos.getValue()));
                        positionsTable.addView(posView);
                    }
                }
                image.setImageResource(R.drawable.ic_more_arrow_up);
                positionsTable.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        View notesShow = getView().findViewById(R.id.player_notes_show);
        int playerId = (Integer) notesShow.getTag();
        View notesEdit = getView().findViewById(R.id.player_notes_edit);
        if (view.getId() == R.id.player_notes_button) {
            String notes = ((TextView) notesShow.findViewById(R.id.player_notes_text)).getText().toString();
            notesShow.setVisibility(View.GONE);
            ((EditText) notesEdit.findViewById(R.id.player_notes_edittext)).setText(notes);
            notesEdit.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.player_notes_ok) {
            String notes = ((EditText) notesEdit.findViewById(R.id.player_notes_edittext)).getText().toString();
            DBAdapter adapter = DBAdapter.getInstance(getActivity());
            adapter.updatePlayerNotes(playerId, notes);
            notesEdit.setVisibility(View.GONE);
            ((TextView) notesShow.findViewById(R.id.player_notes_text)).setText(notes);
            notesShow.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.player_notes_cancel) {
            notesEdit.setVisibility(View.GONE);
            notesShow.setVisibility(View.VISIBLE);            
        }
    }
    
//    class TrainingAdapter extends CursorAdapter {
//        
//        final String format = "yyyy_MM_dd";
//        final Calendar calendar = GregorianCalendar.getInstance();
//
//        private LayoutInflater inflater;
//        private SimpleDateFormat formatter;
//
//        public TrainingAdapter(LayoutInflater inflater, Context context, Cursor c, boolean autoRequery) {
//            super(context, c, autoRequery);
//            this.inflater = inflater;
//            this.formatter = new SimpleDateFormat();
//        }
//        
//        @Override
//        public boolean areAllItemsEnabled() {
//            return false;
//        }
//        
//        @Override
//        public boolean isEnabled(int position) {
//            return false;
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            Training t = DBAdapter.parseCursorTraining(cursor);
//            String date = t.getWeek();
//            int day = t.getDay();
//            try {
//                formatter.applyPattern(format);
//                calendar.setTime(formatter.parse(date));
//                if (day > 0) {
//                    calendar.add(Calendar.DAY_OF_YEAR, day - 1);
//                }
//                formatter.applyPattern("dd/MM/yyyy");
//                date = formatter.format(calendar.getTime());
//            } catch (ParseException e) {
//                CustomLog.error("PlayerDetailsFragment", "Error parseando fecha: " + date);
//                date = "";
//            }
//            ((TextView) view.findViewById(R.id.training_date)).setText(date);
//            if (t.getSkill() != null) {
//                ((TextView) view.findViewById(R.id.training_skill)).setText(MZUtil.SKILL_TITLES.get(t.getSkill()));
//            }
//            String level = Integer.toString(t.getLevel());
//            if (t.isBall()) {
//                level = level + "*";
//            }
//            ((TextView) view.findViewById(R.id.training_level)).setText(level);
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            return inflater.inflate(R.layout.dialog_training, null);
//        }
//        
//    }

}

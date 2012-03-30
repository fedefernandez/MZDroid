package com.projectsexception.mzdroid.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.projectsexception.mz.htmlapi.model.SKILL_TYPE;
import com.projectsexception.mz.htmlapi.model.Skill;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.model.Position;

public class MZUtil {

    private static final Map<Position.Type, int[]> porcentajes;
    private static final Map<SKILL_TYPE, Integer> index;
    public static final Map<SKILL_TYPE, Integer> SKILL_TITLES;
    public static final Map<Position.Type, Integer> POSITIONS_TITLES;

    static {
        SKILL_TITLES = new HashMap<SKILL_TYPE, Integer>();
        SKILL_TITLES.put(SKILL_TYPE.SPEED, R.string.skill_speed);
        SKILL_TITLES.put(SKILL_TYPE.STAMINA, R.string.skill_stamina);
        SKILL_TITLES.put(SKILL_TYPE.PLAY_INTELLIGENCE, R.string.skill_intel);
        SKILL_TITLES.put(SKILL_TYPE.PASSING, R.string.skill_passing);
        SKILL_TITLES.put(SKILL_TYPE.SHOOTING, R.string.skill_shooting);
        SKILL_TITLES.put(SKILL_TYPE.HEADING, R.string.skill_heading);
        SKILL_TITLES.put(SKILL_TYPE.KEEPING, R.string.skill_keeping);
        SKILL_TITLES.put(SKILL_TYPE.BALL_CONTROL, R.string.skill_ball_control);
        SKILL_TITLES.put(SKILL_TYPE.TACKLING, R.string.skill_tackling);
        SKILL_TITLES.put(SKILL_TYPE.AERIAL_PASSING, R.string.skill_aerial_passing);
        SKILL_TITLES.put(SKILL_TYPE.SET_PLAYS, R.string.skill_set_plays);
        SKILL_TITLES.put(SKILL_TYPE.EXPERIENCE, R.string.skill_experience);
        SKILL_TITLES.put(SKILL_TYPE.FORM, R.string.skill_form);
        
        POSITIONS_TITLES = new HashMap<Position.Type, Integer>();
        POSITIONS_TITLES.put(Position.Type.GOAL_KEEPER, R.string.pos_goal_keeper);
        POSITIONS_TITLES.put(Position.Type.CENTRAL_DEFENDER, R.string.pos_central_defender);
        POSITIONS_TITLES.put(Position.Type.WING_DEFENDER, R.string.pos_wing_defender);
        POSITIONS_TITLES.put(Position.Type.DEFENSIVE_MIDFIELDER, R.string.pos_defensive_midfielder);
        POSITIONS_TITLES.put(Position.Type.ATTACKING_MIDFIELDER, R.string.pos_attacking_midfielder);
        POSITIONS_TITLES.put(Position.Type.WING_DEFENSIVE_MIDFIELDER, R.string.pos_wing_defensive_midfielder);
        POSITIONS_TITLES.put(Position.Type.WING_ATTACKING_MIDFIELDER, R.string.pos_wing_attacking_midfielder);
        POSITIONS_TITLES.put(Position.Type.CENTRE_FORWARD, R.string.pos_centre_forward);
        POSITIONS_TITLES.put(Position.Type.CENTRE_STRIKER, R.string.pos_centre_striker);
        POSITIONS_TITLES.put(Position.Type.WING_FORWARD, R.string.pos_wing_forward);
        POSITIONS_TITLES.put(Position.Type.WING_STRIKER, R.string.pos_wing_striker);
        
        porcentajes = new LinkedHashMap<Position.Type, int[]>();
        porcentajes.put(Position.Type.GOAL_KEEPER, new int[] { 5, 5, 5, 0, 0, 65, 5, 5, 5, 5 });
        porcentajes.put(Position.Type.CENTRAL_DEFENDER, new int[] { 13, 10, 9, 0, 10, 0, 5, 45, 8, 0 });
        porcentajes.put(Position.Type.WING_DEFENDER, new int[] { 17, 14, 10, 0, 5, 0, 5, 42, 7, 0 });
        porcentajes.put(Position.Type.DEFENSIVE_MIDFIELDER, new int[] { 10, 10, 15, 2, 9, 0, 16, 26, 12, 0 });
        porcentajes.put(Position.Type.ATTACKING_MIDFIELDER, new int[] { 9, 17, 17, 15, 1, 0, 27, 3, 11, 0 });
        porcentajes.put(Position.Type.WING_DEFENSIVE_MIDFIELDER, new int[] { 12, 12, 20, 1, 2, 0, 12, 26, 15, 0 });
        porcentajes.put(Position.Type.WING_ATTACKING_MIDFIELDER, new int[] { 13, 15, 19, 10, 1, 0, 20, 5, 17, 0 });
        porcentajes.put(Position.Type.CENTRE_STRIKER, new int[] { 12, 13, 10, 30, 7, 0, 20, 1, 7, 0 });
        porcentajes.put(Position.Type.CENTRE_FORWARD, new int[] { 9, 12, 7, 40, 12, 0, 17, 0, 3, 0 });
        porcentajes.put(Position.Type.WING_STRIKER, new int[] { 15, 15, 15, 27, 2, 0, 15, 1, 10, 0 });
        porcentajes.put(Position.Type.WING_FORWARD, new int[] { 10, 13, 13, 36, 4, 0, 15, 1, 8, 0 });
        
        index = new HashMap<SKILL_TYPE, Integer>();
        index.put(SKILL_TYPE.SPEED, 0);
        index.put(SKILL_TYPE.PLAY_INTELLIGENCE, 1);
        index.put(SKILL_TYPE.PASSING, 2);
        index.put(SKILL_TYPE.SHOOTING, 3);
        index.put(SKILL_TYPE.HEADING, 4);
        index.put(SKILL_TYPE.KEEPING, 5);
        index.put(SKILL_TYPE.BALL_CONTROL, 6);
        index.put(SKILL_TYPE.TACKLING, 7);
        index.put(SKILL_TYPE.AERIAL_PASSING, 8);
        index.put(SKILL_TYPE.SET_PLAYS, 9);
    }

    public static List<Position> calculatePositions(List<Skill> skills, boolean withForm) {
    	double form = 9;
    	double stamina = 0;
    	double experience = 0;
    	Skill skill;
    	for (Iterator<Skill> it = skills.iterator(); it.hasNext();) {
            skill = it.next();
            if (skill.getType() == SKILL_TYPE.FORM) {
                form = skill.getValue();
                it.remove();
            } else if (skill.getType() == SKILL_TYPE.STAMINA) {
                stamina = skill.getValue();
                it.remove();
            } else if (skill.getType() == SKILL_TYPE.EXPERIENCE) {
                experience = skill.getValue();
                it.remove();
            }
        }
    	
    	if (!withForm) {
    	    form = 9;
    	}
    	
        stamina = (stamina * 5 + form * 5) / 10;
        stamina = 4 + stamina * 6 / 10;
        experience = 8.1 + experience * 1.9 / 10;
        double extra = stamina * experience;
        extra *= 0.01;

        List<Position> positions = new ArrayList<Position>();
        
        for (Position.Type type : Position.Type.values()) {
            double num = 0;
            for (Iterator<Skill> it = skills.iterator(); it.hasNext();) {
                skill = it.next();
                num += skill.getValue() * porcentajes.get(type)[index.get(skill.getType())];
            }
            num *= extra;
            num = num - (num % 1);
            positions.add(new Position(type, (int) num));
        }
        Collections.sort(positions);
        return positions;
    }

}

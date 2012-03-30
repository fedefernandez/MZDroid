package com.projectsexception.mzdroid.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.projectsexception.mz.htmlapi.model.Player;
import com.projectsexception.mz.htmlapi.model.SKILL_TYPE;
import com.projectsexception.mz.htmlapi.model.Skill;
import com.projectsexception.mz.htmlapi.model.Team;
import com.projectsexception.mz.htmlapi.model.Training;
import com.projectsexception.mz.htmlapi.model.UserData;
import com.projectsexception.mzdroid.model.PlayerComparator;
import com.projectsexception.mzdroid.util.CalendarUtil;

public class DBAdapter {
    
    private static DBAdapter dbAdapter;

    private DBHelper dBHelper;

    private DBAdapter(Context ctx) {
        dBHelper = new DBHelper(ctx);
    }
    
    public static DBAdapter getInstance(Context ctx) {
        if (dbAdapter == null) {
            dbAdapter = new DBAdapter(ctx);
        }
        return dbAdapter;
    }
    
    public static void cierraRecursos() {
        if (dbAdapter != null) {
            if (dbAdapter.dBHelper != null) {
                dbAdapter.dBHelper.close();
            }
            dbAdapter = null;
        }        
    }
    
    public void upgradeDatabase() {
        dBHelper.getWritableDatabase();
    }
    
    public void checkDatabase() {
        dBHelper.getReadableDatabase();
    }
    
    public UserData readUserData() {
        SQLiteDatabase db = dBHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.USER_TABLE, 
                new String[] {DBHelper.ID, DBHelper.NAME, DBHelper.COUNTRY, DBHelper.IMAGE}, 
                null, null, null, null, null);
        UserData userData = null;
        if (c != null) {
            if (c.moveToNext()) {
                userData = new UserData();
                userData.setUserId(c.getInt(0));
                userData.setUsername(c.getString(1));
                userData.setCountryShortname(c.getString(2));
                userData.setUserImage(c.getString(3));                
                cierraCursor(c);
                userData.setTeam(readUserTeam());
            }
        }
        return userData;
    }
    
    public Team readUserTeam() {
        SQLiteDatabase db = dBHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TEAM_TABLE, 
                new String[] {DBHelper.ID, DBHelper.NAME, 
                DBHelper.SHORT_NAME, DBHelper.RANK_POINTS, 
                DBHelper.RANK_POSITION, DBHelper.TEAM_SPORT,
                DBHelper.TEAM_COUNTRY, DBHelper.CURRENCY,
                DBHelper.SERIES_ID, DBHelper.SERIES_NAME, 
                DBHelper.SERIES_START_DATE, DBHelper.SPONSOR}, 
                null, null, null, null, null);
        Team userTeam = null;
        if (c != null) {
            if (c.moveToNext()) {
                userTeam = new Team();
                userTeam.setTeamId(c.getInt(0));
                userTeam.setTeamName(c.getString(1));
                userTeam.setNameShort(c.getString(2));
                userTeam.setRankPoints(c.getInt(3));
                userTeam.setRankPos(c.getInt(4));
                userTeam.setSport(c.getString(5));
                userTeam.setCountryShortname(c.getString(6));
                userTeam.setCurrency(c.getString(7));
                userTeam.setSeriesId(c.getInt(8));
                userTeam.setSeriesName(c.getString(9));
                userTeam.setStartDate(new Date(c.getLong(10)));
                userTeam.setSponsor(c.getString(11));
            }
            cierraCursor(c);
        }
        return userTeam;
    }
    
    public void updateUserData(UserData userData) {
        if (userData != null) {
            SQLiteDatabase db = getSqLiteDatabase();
            db.delete(DBHelper.USER_TABLE, null, null);
            ContentValues values = new ContentValues();
            values.put(DBHelper.ID, userData.getUserId());
            values.put(DBHelper.NAME, userData.getUsername());
            values.put(DBHelper.COUNTRY, userData.getCountryShortname());
            values.put(DBHelper.IMAGE, userData.getUserImage());
            db.insert(DBHelper.USER_TABLE, null, values);
            if (userData.getTeam() != null) {
                db.delete(DBHelper.TEAM_TABLE, null, null);
                values = new ContentValues();
                values.put(DBHelper.ID, userData.getTeam().getTeamId());
                values.put(DBHelper.NAME, userData.getTeam().getTeamName());
                values.put(DBHelper.SHORT_NAME, userData.getTeam().getNameShort());
                values.put(DBHelper.RANK_POINTS, userData.getTeam().getRankPoints());
                values.put(DBHelper.RANK_POSITION, userData.getTeam().getRankPos());
                values.put(DBHelper.TEAM_SPORT, userData.getTeam().getSport());
                values.put(DBHelper.TEAM_COUNTRY, userData.getTeam().getCountryShortname());
                values.put(DBHelper.CURRENCY, userData.getTeam().getCurrency());
                values.put(DBHelper.SERIES_ID, userData.getTeam().getSeriesId());
                values.put(DBHelper.SERIES_NAME, userData.getTeam().getSeriesName());
                values.put(DBHelper.SERIES_START_DATE, userData.getTeam().getStartDate().getTime());
                values.put(DBHelper.SPONSOR, userData.getTeam().getSponsor());
                db.insert(DBHelper.TEAM_TABLE, null, values);
            }
        }
    }
    
    public void deletePlayers(Collection<Player> players) {
        if (players != null && !players.isEmpty()) {
            SQLiteDatabase db = getSqLiteDatabase();
            StringBuilder where = new StringBuilder();
            boolean first = true;
            for (Player player : players) {
                if (!first) {
                    where.append(" OR ");
                }
                where.append(DBHelper.ID);
                where.append("=");
                where.append(player.getId());
                first = false;
            }
            db.delete(DBHelper.PLAYERS_TABLE, where.toString(), null);
            db.delete(DBHelper.SKILLS_TABLE, where.toString(), null);
        }
    }
    
    public void updatePlayers(Collection<Player> players) {
        if (players != null && !players.isEmpty()) {
            SQLiteDatabase db = getSqLiteDatabase();
            for (Player player : players) {
                updatePlayer(db, player, false);
            }
        }
    }
    
    public void insertPlayers(Collection<Player> players) {
        if (players != null && !players.isEmpty()) {
            SQLiteDatabase db = getSqLiteDatabase();
            for (Player player : players) {
                updatePlayer(db, player, true);
            }
        }
    }
    
    public List<Player> readPlayers(boolean readSkills) {
        List<Player> players = new ArrayList<Player>(readPlayersMap(readSkills).values());
        Collections.sort(players, new PlayerComparator());
        return players;
    }
    
    public Map<Integer, Player> readPlayersMap(boolean readSkills) {
        Map<Integer, Player> players = new HashMap<Integer, Player>();
        SQLiteDatabase db = dBHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.PLAYERS_TABLE, 
                new String[] {DBHelper.ID, DBHelper.NUMBER, DBHelper.NAME, 
                DBHelper.AGE, DBHelper.BORN, DBHelper.HEIGHT, DBHelper.WEIGHT, 
                DBHelper.VALUE, DBHelper.SALARY}, 
                null, null, null, null, null);
        Player player = null;
        if (c != null) {
            while (c.moveToNext()) {
                player = new Player();
                player.setId(c.getInt(0));
                player.setNumber(c.getInt(1));
                player.setName(c.getString(2));
                player.setAge(c.getInt(3));
                player.setBorn(c.getInt(4));
                player.setHeight(c.getInt(5));
                player.setWeight(c.getInt(6));
                player.setValue(c.getInt(7));
                player.setSalary(c.getInt(8));
                if (readSkills) {                    
                    player.setSkills(readPlayerSkills(db, player.getId()));
                }
                players.put(player.getId(), player);
            }
            cierraCursor(c);
        }
        return players;
    }
    
    public List<Skill> readPlayerSkills(int playerId) {
        SQLiteDatabase db = dBHelper.getReadableDatabase();
        return readPlayerSkills(db, playerId);
    }
    
    public Player readPlayer(int playerId) {
        return readPlayer(playerId, true);
    }
    
    public Player readPlayer(int playerId, boolean readSkills) {
        Player player = null;
        SQLiteDatabase db = dBHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.PLAYERS_TABLE, 
                new String[] {DBHelper.ID, DBHelper.NUMBER, DBHelper.NAME, 
                DBHelper.AGE, DBHelper.BORN, DBHelper.HEIGHT, DBHelper.WEIGHT, 
                DBHelper.VALUE, DBHelper.SALARY}, 
                DBHelper.ID + "=?", new String[] { Integer.toString(playerId) }, 
                null, null, null);
        if (c != null) {
            if (c.moveToNext()) {
                player = new Player();
                player.setId(c.getInt(0));
                player.setNumber(c.getInt(1));
                player.setName(c.getString(2));
                player.setAge(c.getInt(3));
                player.setBorn(c.getInt(4));
                player.setHeight(c.getInt(5));
                player.setWeight(c.getInt(6));
                player.setValue(c.getInt(7));
                player.setSalary(c.getInt(8));
                if (readSkills) {
                    player.setSkills(readPlayerSkills(db, player.getId()));
                }
            }
            cierraCursor(c);
        }
        return player;
    }
    
    private List<Skill> readPlayerSkills(SQLiteDatabase db, int playerId) {
        List<Skill> skills = new ArrayList<Skill>();
        Cursor cSkills = db.query(DBHelper.SKILLS_TABLE, 
                new String[] {DBHelper.ID, DBHelper.SKILL_TYPE, DBHelper.VALUE, DBHelper.MAXED_SKILL}, 
                DBHelper.PLAYER_ID + "=?", new String[] { Integer.toString(playerId) }, 
                null, null, null);
        if (cSkills != null) {
            Skill skill;
            while(cSkills.moveToNext()) {
                skill = new Skill(
                        SKILL_TYPE.valueOf(cSkills.getString(1)), 
                        cSkills.getInt(2),
                        Boolean.parseBoolean(cSkills.getString(3)));
                skill.setId(cSkills.getLong(0));
                skills.add(skill);
            }
            cSkills.close();
        }
        return skills;
    }
    
    public Collection<Integer> weekTrainingDays() {
        String weekDate = CalendarUtil.calculateWeek();
        Set<Integer> days = new HashSet<Integer>();
        SQLiteDatabase db = dBHelper.getReadableDatabase();
        Cursor cursor = db.query(true, DBHelper.TRAINING_TABLE, 
                new String[] {DBHelper.DAY}, 
                DBHelper.WEEK_DATE + "=?", new String[] { weekDate }, 
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                days.add(cursor.getInt(0));
            }
            cierraCursor(cursor);
        }
        return days;
    }
    
    public void insertTrainings(Collection<Training> trainings, String week, int day) {
        if (trainings != null && !trainings.isEmpty()) {
            SQLiteDatabase db = getSqLiteDatabase();            
            ContentValues values = new ContentValues();
            for (Training training : trainings) {
                if (week != null) {
                    training.setWeek(week);
                }
                if (day != 0) {
                    training.setDay(day);
                }
                values.put(DBHelper.WEEK_DATE, training.getWeek());
                values.put(DBHelper.DAY, training.getDay());
                if (training.getSkill() == null) {
                    values.remove(DBHelper.SKILL_TYPE);
                } else {
                    values.put(DBHelper.SKILL_TYPE, training.getSkill().name());
                }
                values.put(DBHelper.LEVEL, training.getLevel());
                values.put(DBHelper.UP, Boolean.toString(training.isBall()));
                values.put(DBHelper.TRAINING_CAMP, Boolean.toString(training.isTc()));
                values.put(DBHelper.PLAYER_ID, training.getPlayerId());
                db.insert(DBHelper.TRAINING_TABLE, null, values);
            }
        }
    }
    
    public void insertTrainings(Collection<Training> trainings) {
        insertTrainings(trainings, null, 0);
    }
    
    public boolean trainingAvailable(String week, int day) {
        SQLiteDatabase db = getSqLiteDatabase();
        Cursor c = db.query(DBHelper.TRAINING_TABLE, 
                new String[] { DBHelper.ID }, 
                DBHelper.WEEK_DATE + "=? AND " + DBHelper.DAY + "=?", 
                new String[] { week, Integer.toString(day) }, 
                null, null, null);
        boolean available = false;
        if (c != null) {
            available = c.moveToNext();
            cierraCursor(c);
        }
        return available;
    }
    
    public List<Training> readTrainings(String week) {
        return readTrainings(week, 0, 0);
    }
    
    public List<Training> readTrainings(String week, int day) {
        return readTrainings(week, day, 0);
    }
    
    public List<Training> readTrainings(int playerId) {
        return readTrainings(null, 0, playerId);
    }
    
    public Cursor readTrainingsCursor(int playerId) {
        return readTrainingsCursor(null, 0, playerId);
    }
    
    public List<Training> readTrainings(String week, int day, int playerId) {
        List<Training> trainings = new ArrayList<Training>();
        
        Cursor c = readTrainingsCursor(week, day, playerId);
        if (c != null) {
            while (c.moveToNext()) {
                trainings.add(parseCursorTraining(c));
            }
            cierraCursor(c);
        }
        
        return trainings;
    }
    
    public Cursor readTrainingsCursor(String week, int day, int playerId) {        
        SQLiteDatabase db = getSqLiteDatabase();
        
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<String>();
        if (week != null) {
            selection.append(DBHelper.WEEK_DATE);
            selection.append(" =? ");
            selectionArgs.add(week);
        }
        if (day > 0) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(DBHelper.DAY);
            selection.append(" =? ");
            selectionArgs.add(Integer.toString(day));
        }
        if (playerId > 0) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(DBHelper.PLAYER_ID);
            selection.append(" =? ");
            selectionArgs.add(Integer.toString(playerId));
        }
        
        Cursor c = db.query(DBHelper.TRAINING_TABLE, 
                new String[] { 
                    DBHelper.ID, DBHelper.WEEK_DATE, DBHelper.DAY,
                    DBHelper.SKILL_TYPE, DBHelper.LEVEL, 
                    DBHelper.UP, DBHelper.TRAINING_CAMP, DBHelper.PLAYER_ID 
                }, 
                selection.toString(), selectionArgs.toArray(new String[0]), 
                null, null, DBHelper.WEEK_DATE + ", " + DBHelper.DAY); 
        
        return c;
    }
    
    public Cursor readTrainingDaysCursor() {        
        SQLiteDatabase db = getSqLiteDatabase();        
        Cursor c = db.query(true, DBHelper.TRAINING_TABLE, 
                new String[] { DBHelper.WEEK_DATE + "||" + DBHelper.DAY + " AS _id", DBHelper.WEEK_DATE, DBHelper.DAY  }, 
                null, null, null, null, 
                DBHelper.WEEK_DATE + " DESC, " + DBHelper.DAY + " DESC", null);
        
        return c;
    }
    
    public String readPlayerNotes(int playerId) {        
        SQLiteDatabase db = getSqLiteDatabase();
        String notes = null;
        Cursor c = db.query(DBHelper.NOTES_TABLE, 
                new String[] { DBHelper.NOTES}, 
                DBHelper.NOTES_PLAYER_ID + " = ?", new String[] { Integer.toString(playerId) }, 
                null, null, null);
        if (c != null) {
            if (c.moveToNext()) {
                notes = c.getString(0);
            }
            cierraCursor(c);
        }
        return notes;
    }
    
    public void updatePlayerNotes(int playerId, String notes) {        
        SQLiteDatabase db = getSqLiteDatabase();
        db.execSQL("INSERT OR REPLACE INTO " + DBHelper.NOTES 
                + " (" 
                + DBHelper.NOTES_PLAYER_ID + ", " + DBHelper.NOTES 
                + ") VALUES (" + playerId + ", '" + notes + "')");
    }
    
    public static Training parseCursorTraining(Cursor c) {
        Training t = new Training();
        t.setWeek(c.getString(1));
        t.setDay(c.getInt(2));
        String skillType = c.getString(3);
        if (skillType != null) {
            t.setSkill(SKILL_TYPE.valueOf(skillType));
        }
        t.setLevel(c.getInt(4));
        t.setBall(Boolean.parseBoolean(c.getString(5)));
        t.setTc(Boolean.parseBoolean(c.getString(6)));
        t.setPlayerId(c.getInt(7));
        return t;
    }
    
    public void updateSkill(Skill skill) {
        if (skill != null && skill.getId() > 0) {
            SQLiteDatabase db = getSqLiteDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.VALUE, skill.getValue());
            values.put(DBHelper.MAXED_SKILL, Boolean.toString(skill.isMaxed()));
            db.update(DBHelper.SKILLS_TABLE, values, 
                    DBHelper.ID + " = ?", new String[] { Long.toString(skill.getId()) });
        }
    }

    private void updatePlayer(SQLiteDatabase db, Player player, boolean insert) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.NUMBER, player.getNumber());
        values.put(DBHelper.NAME, player.getName());
        values.put(DBHelper.AGE, player.getAge());
        values.put(DBHelper.HEIGHT, player.getHeight());
        values.put(DBHelper.WEIGHT, player.getWeight());
        values.put(DBHelper.VALUE, player.getValue());
        values.put(DBHelper.SALARY, player.getSalary());
        if (insert) {
            values.put(DBHelper.ID, player.getId());
            db.insert(DBHelper.PLAYERS_TABLE, null, values);
            insertSkills(db, player.getId(), player.getSkills());
        } else {
            db.update(DBHelper.PLAYERS_TABLE, values, 
                    DBHelper.ID + "=?", new String[] {Integer.toString(player.getId())});
            updateSkills(db, player.getId(), player.getSkills());
        }
    }

    private void updateSkills(SQLiteDatabase db, int playerId, List<Skill> skills) {
        if (skills != null && !skills.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.PLAYER_ID, playerId);
            for (Skill skill : skills) {
                values.put(DBHelper.SKILL_TYPE, skill.getType().name());
                values.put(DBHelper.VALUE, skill.getValue());
                try {
                    db.update(DBHelper.SKILLS_TABLE, values, 
                            DBHelper.PLAYER_ID + " = ? AND " + DBHelper.SKILL_TYPE + " = ?", 
                            new String[] {Integer.toString(playerId), skill.getType().name()});
                } catch (Exception e) {
                    values.put(DBHelper.MAXED_SKILL, Boolean.toString(skill.isMaxed()));                    
                    db.insert(DBHelper.SKILLS_TABLE, null, values);
                }
            }
        }
    }

    private void insertSkills(SQLiteDatabase db, int playerId, List<Skill> skills) {
        if (skills != null && !skills.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.PLAYER_ID, playerId);
            for (Skill skill : skills) {
                values.put(DBHelper.SKILL_TYPE, skill.getType().name());
                values.put(DBHelper.VALUE, skill.getValue());
                values.put(DBHelper.MAXED_SKILL, Boolean.toString(skill.isMaxed()));                    
                long id = db.insert(DBHelper.SKILLS_TABLE, null, values);
                skill.setId(id);
            }
        }
    }

    public SQLiteDatabase getSqLiteDatabase() {
        synchronized (dBHelper) {
            return dBHelper.getWritableDatabase();
        }
    }
    
    private void cierraCursor(Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                Log.w("MZDroid", "Error closing cursor");
            }
        }
    }

}

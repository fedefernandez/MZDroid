package com.projectsexception.mzdroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    
    public static final String DATABASE_NAME = "mzdroid.db";
    
    private static final int DATABASE_VERSION = 2;
    
    public static final String USER_TABLE = "user";
    public static final String TEAM_TABLE = "team";
    public static final String PLAYERS_TABLE = "players";
    public static final String SKILLS_TABLE = "skills";
    public static final String TRAINING_TABLE = "training";
    public static final String NOTES_TABLE = "notes";
    
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String COUNTRY = "country";
    public static final String IMAGE = "image";
    public static final String SHORT_NAME = "short_name";
    public static final String RANK_POINTS = "rank_points";
    public static final String RANK_POSITION = "rank_position";
    public static final String SERIES_ID = "series_id";
    public static final String SERIES_NAME = "series_name";
    public static final String SPONSOR = "sponsor";
    public static final String NUMBER = "number";
    public static final String AGE = "age";
    public static final String BORN = "born";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String VALUE = "value";
    public static final String SALARY = "salary";
    public static final String SKILL_TYPE = "type";
    public static final String PLAYER_ID = "player_id";
    public static final String WEEK_DATE = "week_date";
    public static final String DAY = "day";
    public static final String LEVEL = "level";
    public static final String UP = "up";
    public static final String TRAINING_CAMP = "training_camp";
    public static final String TRAINING_ID = "training_id";
    public static final String TEAM_SPORT = "team_sport";
    public static final String TEAM_COUNTRY = "team_country";
    public static final String CURRENCY = "currency";
    public static final String SERIES_START_DATE = "start_date";
    public static final String MAXED_SKILL = "maxed";
    public static final String NOTES = "notes";
    public static final String NOTES_PLAYER_ID = "_id";
    
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(USER_TABLE);
        sb.append(" (");
        sb.append(ID);
        sb.append(" INTEGER PRIMARY KEY, ");
        sb.append(NAME);
        sb.append(" TEXT NOT NULL, ");
        sb.append(COUNTRY);
        sb.append(" TEXT NOT NULL, ");
        sb.append(IMAGE);
        sb.append(" TEXT NOT NULL);");
        db.execSQL(sb.toString());
        
        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(TEAM_TABLE);
        sb.append(" (");
        sb.append(ID);
        sb.append(" INTEGER PRIMARY KEY, ");
        sb.append(NAME);
        sb.append(" TEXT NOT NULL, ");
        sb.append(SHORT_NAME);
        sb.append(" TEXT NOT NULL, ");
        sb.append(RANK_POINTS);
        sb.append(" INTEGER, ");
        sb.append(RANK_POSITION);
        sb.append(" INTEGER, ");
        sb.append(TEAM_SPORT);
        sb.append(" TEXT NOT NULL, ");
        sb.append(TEAM_COUNTRY);
        sb.append(" TEXT NOT NULL, ");
        sb.append(CURRENCY);
        sb.append(" TEXT NOT NULL, ");
        sb.append(SERIES_ID);
        sb.append(" INTEGER, ");
        sb.append(SERIES_NAME);
        sb.append(" TEXT NOT NULL, ");
        sb.append(SERIES_START_DATE);
        sb.append(" INTEGER, ");
        sb.append(SPONSOR);
        sb.append(" TEXT);");
        db.execSQL(sb.toString());
        
        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(PLAYERS_TABLE);
        sb.append(" (");
        sb.append(ID);
        sb.append(" INTEGER PRIMARY KEY, ");
        sb.append(NUMBER);
        sb.append(" INTEGER, ");
        sb.append(NAME);
        sb.append(" TEXT NOT NULL, ");
        sb.append(AGE);
        sb.append(" INTEGER, ");
        sb.append(BORN);
        sb.append(" INTEGER, ");
        sb.append(HEIGHT);
        sb.append(" INTEGER, ");
        sb.append(WEIGHT);
        sb.append(" INTEGER, ");
        sb.append(VALUE);
        sb.append(" INTEGER, ");
        sb.append(SALARY);
        sb.append(" INTEGER);");
        db.execSQL(sb.toString());
        
        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(SKILLS_TABLE);
        sb.append(" (");
        sb.append(ID);
        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(SKILL_TYPE);
        sb.append(" TEXT NOT NULL, ");
        sb.append(VALUE);
        sb.append(" INTEGER, ");
        sb.append(MAXED_SKILL);
        sb.append(" TEXT NOT NULL, ");
        sb.append(PLAYER_ID);
        sb.append(" INTEGER);");
        db.execSQL(sb.toString());
        
        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(TRAINING_TABLE);
        sb.append(" (");
        sb.append(ID);
        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(WEEK_DATE);
        sb.append(" TEXT NOT NULL, ");
        sb.append(DAY);
        sb.append(" INTEGER, ");
        sb.append(SKILL_TYPE);
        sb.append(" TEXT, ");
        sb.append(LEVEL);
        sb.append(" INTEGER, ");
        sb.append(UP);
        sb.append(" TEXT NOT NULL, ");
        sb.append(TRAINING_CAMP);
        sb.append(" TEXT NOT NULL, ");
        sb.append(PLAYER_ID);
        sb.append(" INTEGER);");
        db.execSQL(sb.toString());
        
        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(NOTES_TABLE);
        sb.append(" (");
        sb.append(NOTES_PLAYER_ID);
        sb.append(" INTEGER PRIMARY KEY, ");
        sb.append(NOTES);
        sb.append(" TEXT);");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

}

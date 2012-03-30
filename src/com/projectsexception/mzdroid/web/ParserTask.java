package com.projectsexception.mzdroid.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;

import com.projectsexception.mz.htmlapi.HTMLParser;
import com.projectsexception.mz.htmlapi.ManagerZoneClient;
import com.projectsexception.mz.htmlapi.model.Player;
import com.projectsexception.mz.htmlapi.model.Training;
import com.projectsexception.mz.htmlapi.model.UserData;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.ReaderActivity;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.util.CalendarUtil;
import com.projectsexception.mzdroid.util.IOUtil;
import com.projectsexception.mzdroid.util.CustomLog;

public class ParserTask extends AsyncTask<String, Object, Integer> {
    
    public static enum TYPE {
        START(R.string.console_title_start),
        TEAM(R.string.console_title_team),
        PLAYERS(R.string.console_title_players),
        TRAINING(R.string.console_title_training);
        
        private int title;
        
        private TYPE(int title) {
            this.title = title;
        }

        public int getTitle() {
            return title;
        }
    }
    
    private ReaderActivity activity;
    
    public ParserTask(ReaderActivity activity) {
        this.activity = activity;
    }
    
    public void setReaderActivity(ReaderActivity activity) {
        this.activity = activity;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.progressVisivility(Boolean.TRUE);
    }

    @Override
    protected Integer doInBackground(String... params) {
        ManagerZoneClient client = new ManagerZoneClient();
        publishProgress(TYPE.START);
        // Connection
        if (!IOUtil.checkConnection(activity)) {
            publishProgress(activity.getString(R.string.console_msg_no_connection));
            return ReaderActivity.RESULT_ERROR;
        }
        
        // Login
        boolean result = client.login(params[0], params[1]);
        if (!result) {
            return ReaderActivity.RESULT_ERROR;
        }
        publishProgress(activity.getString(R.string.console_msg_login));
        // Team
        publishProgress(TYPE.TEAM);
        String content = client.getTeam();
        if (!content.contains("Logged in as")) {
            return ReaderActivity.RESULT_ERROR_CREDENTIALS;
        }
        if (!readTeam(params[0])) {
            client.logout();
            return ReaderActivity.RESULT_ERROR;
        }
        // Players
        publishProgress(TYPE.PLAYERS);
        content = client.getPlayers();
        if (!readPlayers(content)) {
            client.logout();
            return ReaderActivity.RESULT_ERROR;
        }
        // Training
        publishProgress(TYPE.TRAINING);
        Calendar c = GregorianCalendar.getInstance();
        int today = CalendarUtil.calendarToMZDay(c.get(Calendar.DAY_OF_WEEK));
        String week = CalendarUtil.calculateWeek();
        DBAdapter dbAdapter = DBAdapter.getInstance(activity);
        Collection<Integer> days = dbAdapter.weekTrainingDays();
        List<Integer> daysToRead = new ArrayList<Integer>();
        for (int i = 1 ; i <= today ; i++) {
            if (!days.contains(i)) {
                daysToRead.add(i);
            }
        }
        if (daysToRead.isEmpty()) {
            client.logout();
            return ReaderActivity.RESULT_OK;
        }
        for (Integer day : daysToRead) {
            content = client.getTraining(day);
            readTraining(content, week, day);
        }
        client.logout();
        client.closeConnection();
        return ReaderActivity.RESULT_OK;
    }
    
    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (values[0] instanceof TYPE) {
            TYPE phaseType = (TYPE) values[0];
            activity.startPhase(phaseType);
        } else if (values[0] instanceof String) {
            String msg = (String) values[0];
            activity.printConsoleMessage(msg);
        }
    }
    
    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);        
        activity.progressVisivility(Boolean.FALSE);
        activity.endParsing(result);
    }
    
    private boolean readTeam(String username) {
        CustomLog.debug("PlayersTask", "readTeam");
        UserData userData = HTMLParser.readUserData(username);
        if (userData == null || userData.getTeam() == null || 
                userData.getTeam().getTeamName() == null) {
            return false;
        } else {
            publishProgress(activity.getString(R.string.console_msg_team, userData.getTeam().getTeamName()));
            DBAdapter dbAdapter = DBAdapter.getInstance(activity);
            dbAdapter.updateUserData(userData);
            return true;
        }
    }
    
    private boolean readPlayers(String html) {
        CustomLog.debug("PlayersTask", "readPlayers");
        List<Player> players = HTMLParser.readPlayers(html);
        if (players == null || players.isEmpty()) {
            return false;
        } else {
            DBAdapter dbAdapter = DBAdapter.getInstance(activity);
            List<Player> existing = dbAdapter.readPlayers(false);
            List<Player> newPlayers = new ArrayList<Player>();
            
            Player player;
            int pos;
            // Vamos a dejar en existing los que hay que eliminar
            // En players los que vamos a editar (existían)
            // En newPlayers los nuevos
            for (Iterator<Player> it = players.iterator(); it.hasNext();) {
                player = it.next();
                pos = existing.indexOf(player);
                if (pos >= 0) {
                    // El jugador estaba en la base de datos
                    existing.remove(pos);
                } else {
                    // Es un jugador nuevo
                    publishProgress(activity.getString(R.string.console_msg_new_player, player.getName()));
                    newPlayers.add(player);
                    it.remove();
                }
                
            }
            dbAdapter.deletePlayers(existing);
            if (newPlayers.isEmpty()) {
                publishProgress(R.string.console_msg_no_players);
            } else {
                dbAdapter.insertPlayers(newPlayers);
            }
            dbAdapter.updatePlayers(players);
            return true;
        }
    }
    
    private void readTraining(String html, String week, int day) {
        CustomLog.debug("PlayersTask", "readTraining");
        publishProgress(activity.getString(R.string.console_msg_new_training, activity.getString(CalendarUtil.mzDayTitle(day))));
        List<Training> trainings = HTMLParser.readTraining(html);
        if (trainings == null || trainings.isEmpty()) {
            publishProgress(activity.getString(R.string.console_msg_no_training));
        } else {
            DBAdapter dbAdapter = DBAdapter.getInstance(activity);
            dbAdapter.insertTrainings(trainings, week, day);
        }
    }

}

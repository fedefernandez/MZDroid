package com.projectsexception.mzdroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.projectsexception.about.AboutActivity;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.db.DBHelper;
import com.projectsexception.mzdroid.util.IOUtil;

public class MZPreferenceActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    
    public static final int DATABASE_CHANGED = 1;
    
    private static final String MZDROID = "mzdroid";
    private static final String DB_PATH = "/data/data/com.projectsexception.mzdroid/databases/";
    
    private BackupRestoreTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference myPref = (Preference) findPreference(getString(R.string.pref_backup_key));
        myPref.setOnPreferenceClickListener(this);
        myPref = (Preference) findPreference(getString(R.string.pref_restore_key));
        myPref.setOnPreferenceClickListener(this);
        myPref = (Preference) findPreference(getString(R.string.pref_about_key));
        myPref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        boolean br = false;
        boolean param = true;
        if (getString(R.string.pref_backup_key).equals(preference.getKey())) {
            br = true;
            param = true;
        } else if (getString(R.string.pref_restore_key).equals(preference.getKey())) {
            br = true;
            param = false;
        }
        
        if (br) {
            if (isTaskRunning()) {
                Toast.makeText(this, "There are other task running", Toast.LENGTH_SHORT).show();
            } else {
                task = new BackupRestoreTask();
                task.execute(param);
            }
        } else if (getString(R.string.pref_about_key).equals(preference.getKey())) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        
        
        return true;
    }
    
    class BackupRestoreTask extends AsyncTask<Boolean, Void, String> {
        
        private boolean backup;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MZPreferenceActivity.this, "Iniciando exportación", Toast.LENGTH_SHORT).show();
        }
        
        @Override
        protected String doInBackground(Boolean... params) {
            if (isExternalStorage()) {
                backup = params[0];
                File from = new File(DB_PATH + DBHelper.DATABASE_NAME);
                
                File sdcard = Environment.getExternalStorageDirectory();
                File mzdroidDir = new File(sdcard, MZDROID);
                if (!mzdroidDir.exists()) {
                    if (!mzdroidDir.mkdir()) {
                        return "Unable to write on " + mzdroidDir.getAbsolutePath();
                    }
                }                
                File to = new File(mzdroidDir, DBHelper.DATABASE_NAME);
                
                if (backup) {
                    return copyDataBase(from, to);
                } else {
                    String error = copyDataBase(to, from);
                    if (error == null) {
                        // Hay que hacer que se cargue la base de datos
                        try {
                            DBAdapter.getInstance(MZPreferenceActivity.this).upgradeDatabase();
                        } catch (Exception e) {
                            error = "Error";
                        }
                    }
                    return error;
                }
            } else {
                return "Unable to read SDCARD";
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                setResult(DATABASE_CHANGED);
                if (backup) {
                    result = "Database exported successfully";
                } else {
                    result = "Database imported successfully";
                }
            }
            Toast.makeText(MZPreferenceActivity.this, result, Toast.LENGTH_SHORT).show();
        }
        
    }
    
    class BackupRestoreException extends Exception {

        private static final long serialVersionUID = -8957183705301917587L;
        
        public BackupRestoreException(String msg) {
            super(msg);
        }
        
    }
    
    protected boolean isTaskRunning() {
        return task != null && task.getStatus() == AsyncTask.Status.RUNNING && !task.isCancelled();
    }
    
    protected boolean isExternalStorage() {
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageWriteable = true;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageWriteable = false;
        }
        
        return mExternalStorageWriteable;
    }
    
    protected String copyDataBase(File from, File to) {        
        
        InputStream myInput;
        try {
            myInput = new FileInputStream(from);
        } catch (IOException e) {
            return "Error opening database application";
        }
        
        File sdcard = Environment.getExternalStorageDirectory();
        File mzdroidDir = new File(sdcard, MZDROID);
        if (!mzdroidDir.exists()) {
            if (!mzdroidDir.mkdir()) {
                return "Unable to write on " + mzdroidDir.getAbsolutePath();
            }
        }
        
        OutputStream myOutput;
        try {
            myOutput = new FileOutputStream(to);
        } catch (FileNotFoundException e) {
            return "Error creating file " + to.getAbsolutePath();
        }
                
        String msg = null;
        try {
            IOUtil.copyFiles(myInput, myOutput);            
        } catch (IOException e) {
            msg = "Error writting on destination file";
        }
        
        return msg;
    }

}

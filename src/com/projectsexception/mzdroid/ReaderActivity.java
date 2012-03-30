package com.projectsexception.mzdroid;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.projectsexception.mzdroid.util.CustomLog;
import com.projectsexception.mzdroid.web.ParserTask;

public class ReaderActivity extends SherlockFragmentActivity {
    
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    public static final int RESULT_ERROR = 1;
    public static final int RESULT_ERROR_CREDENTIALS = 2;
    
    private ViewGroup consoleLayout;
    private View phase;
    private LayoutInflater inflater;
    private Dialog dialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        //This has to be called before setContentView and you must use the
        //class in android.support.v4.view and NOT android.view      
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.console);
        consoleLayout = (ViewGroup) findViewById(R.id.console_layout);
        phase = consoleLayout.findViewById(R.id.team_phase);
        
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        preReading(sp);
        
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        ab.setTitle("Read data");
        
    }
    
    @Override
    protected void onStop() {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                // No problem
            }
        }
        super.onStop();
    }

    private void preReading(SharedPreferences sp) {
        String username = sp.getString(getString(R.string.pref_username_key), null);
        String password = sp.getString(getString(R.string.pref_password_key), null);
        if (!isEmpty(username) && !isEmpty(password)) {
            startReading(username, password);
        } else {
            final Editor e = sp.edit();
            View form = inflater.inflate(R.layout.dialog_reader, null);
            final CheckBox checkBox = (CheckBox) form.findViewById(R.id.remember);
            final TextView userView = (TextView) form.findViewById(R.id.username);
            final TextView passView = (TextView) form.findViewById(R.id.password);
            if (username != null) {
                userView.setText(username);
            }
            dialog = new Dialog(this);
            dialog.setTitle("Enter credentials");
            dialog.setContentView(form);
            Button button = (Button) form.findViewById(R.id.button_ok);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user = getText(userView);
                    String pass = getText(passView);
                    if (user == null || pass == null) {
                        Toast.makeText(ReaderActivity.this, R.string.error_userpass_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        e.putString(USERNAME, user);
                        if (checkBox.isChecked()) {
                            e.putString(PASSWORD, pass);
                        }
                        e.commit();
                        dialog.dismiss();
                        startReading(user, pass);
                    }
                }
            });
            button = (Button) form.findViewById(R.id.button_cancel);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ReaderActivity.this.finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }
    
    private boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    private void startReading(String username, String password) {
        new ParserTask(this).execute(username, password);
    }
    
    private String getText(TextView userView) {
        String value = null;
        if (userView.getText() != null) {
            value = userView.getText().toString().trim();
        }
        if (value == null || value.length() == 0) {
            value = null;
        }
        return value;
    }
    
    public void endParsing(int result) {
        if (result == RESULT_ERROR_CREDENTIALS) {
            Toast.makeText(this, R.string.error_userpass, Toast.LENGTH_SHORT).show();
            SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);        
            preReading(sp);
        } else {
            setResult(result);
            finish();
        }
    }

    public void startPhase(final ParserTask.TYPE type) {
        CustomLog.debug("ReaderActivity", "startPhase(" + type.toString() + ")");
        if (type != ParserTask.TYPE.START) {
            phase.findViewById(R.id.separator).setVisibility(View.VISIBLE);
            phase = inflater.inflate(R.layout.console_phase, null);
            consoleLayout.addView(phase);
        }
        ((TextView) phase.findViewById(R.id.title)).setText(type.getTitle());
    }

    
    public void printConsoleMessage(final String msg) {
        CustomLog.debug("ReaderActivity", "printConsoleMessage(" + msg + ")");
        final TextView textView = (TextView) phase.findViewById(R.id.msg);
        if (textView.getText().length() > 0) {
            textView.append("\n");
        }
        textView.append(msg);
    }
    
    public void progressVisivility(Boolean visible) {
        //Notice how we *MUST* pass TRUE/FALSE objects rather than the native
        //true/false values.
        CustomLog.debug("ReaderActivity", "progressVisivility(" + visible + ")");
        setProgressBarIndeterminateVisibility(visible);
    }
}
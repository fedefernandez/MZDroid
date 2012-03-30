package com.projectsexception.mzdroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.projectsexception.mz.htmlapi.model.Player;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.model.Position;
import com.projectsexception.mzdroid.model.Position.Type;
import com.projectsexception.mzdroid.util.CalculateTacticTask;
import com.projectsexception.mzdroid.util.FieldUtil;
import com.projectsexception.mzdroid.util.Valor;
import com.projectsexception.mzdroid.view.PlayersView;

public class TacticsActivity extends Activity implements View.OnTouchListener, CalculateTacticTask.ResultListener {
    
    private static final int WIDTH_PLAYER = 30;
    private static final int HEIGHT_PLAYER = 30;
    private static final int WIDTH_BUTTON = 10;
    private static final int HEIGHT_BUTTON = 20;
    private static final int ZONE_DEFENDERS = 2;
    private static final int ZONE_MIDFIELDERS = 1;
    private static final int ZONE_FORWARDS = 0;
    
    private LayoutInflater inflater;
    private LineUpData lineUpData;
    private GestureDetector gestureDetector;
    protected int idZone;
    protected boolean vertical;
    private Map<Integer, Player> players;
    
    class LineUpData {        
        int[] lineUp;
        int defenders;
        List<Type[]> possibleDefenders;
        int midfielders;
        List<Type[]> possibleMidfielders;
        int forwards;
        List<Type[]> possibleForwards;
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        idZone = v.getId();
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tactics);
        
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        
        Display display = getWindowManager().getDefaultDisplay();
        vertical = display.getWidth() < display.getHeight();

        final Button buttonChange = (Button) findViewById(R.id.tactic_change);
        final Button buttonApply = (Button) findViewById(R.id.tactic_apply);
        if (!vertical) {
            buttonApply.setText("");
            buttonChange.setText("");
        }
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
        
        findViewById(R.id.players_defenses).setOnTouchListener(this);
        findViewById(R.id.players_midfields).setOnTouchListener(this);
        findViewById(R.id.players_forwards).setOnTouchListener(this);        
        
        Object retained = this.getLastNonConfigurationInstance();
        if(retained == null) {
            lineUpData = new LineUpData();
            changeTactic(buttonChange);
        } else {
            lineUpData = (LineUpData) retained;
            buttonChange.setText(tacticToString(lineUpData.lineUp, true));
        }
    }
    
    public void changeTactic(View view) {
        final Button textView = (Button) view;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Elige una táctica");
        int num = FieldUtil.LINEUPS.length;
        final int firstLineup;
        if (lineUpData != null && lineUpData.lineUp != null) {
            num++;
            firstLineup = 1;
        } else {
            firstLineup = 0;
        }
        final String[] items = new String[num];
        if (firstLineup > 0) {
            items[0] = "Táctica actual";
        }
        int[] lineUp;
        for (int i = firstLineup; i < num; i++) {
            lineUp = FieldUtil.LINEUPS[i - firstLineup];
            items[i] = tacticToString(lineUp, false);
        }
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {                
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int[] lineUp;
                if (which == 0 && firstLineup > 0) {
                    lineUp = lineUpData.lineUp;
                } else {
                    lineUp = FieldUtil.LINEUPS[which - firstLineup];
                }
                putLineUp(lineUp);
                textView.setText(tacticToString(lineUp, true));
                findViewById(R.id.tactic_apply).setEnabled(true);
            }
        });
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {                
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {            
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        dialogBuilder.create().show();
    }
    
    public void applyTactic(View view) {
        if (lineUpData == null) {
            return;
        }
        view.setEnabled(false);
        DBAdapter dbAdapter = DBAdapter.getInstance(TacticsActivity.this);
        players = dbAdapter.readPlayersMap(true);
        Type[] positions = new Type[11];
        positions[0] = Type.GOAL_KEEPER;
        int cont = 1;
        Type[] tmp = lineUpData.possibleDefenders.get(lineUpData.defenders);
        for (int i = 0; i < tmp.length; i++) {
            positions[cont + i] = tmp[i];                    
        }
        cont += tmp.length;
        
        tmp = lineUpData.possibleMidfielders.get(lineUpData.midfielders);
        for (int i = 0; i < tmp.length; i++) {
            positions[cont + i] = tmp[i];                    
        }
        cont += tmp.length;
        
        tmp = lineUpData.possibleForwards.get(lineUpData.forwards);
        for (int i = 0; i < tmp.length; i++) {
            positions[cont + i] = tmp[i];                    
        }
        cont += tmp.length;
        new CalculateTacticTask(players.values(), this).execute(positions);
    }

    @Override
    public void onResult(List<Valor> result) {
        if (result != null) {
            List<View> copyLst = getAllPlayerViews();
            Player player;
            for (Valor valor : result) {
                View playerView = findNextView(copyLst, valor.posicion);
                player = players.get(valor.playerId);
                if (playerView != null && player != null) {
                    ((TextView) playerView.findViewById(R.id.tactic_player_number)).setText("" + player.getNumber());
                }
            }
        }
        findViewById(R.id.tactic_apply).setEnabled(true);
    }
    
    private List<View> getAllPlayerViews() {
        List<View> playerViewList = new ArrayList<View>();
        View playerView;
        Object tag;
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.players_defenses);
        for (int i = 0 ; i < viewGroup.getChildCount() ; i++) {
            playerView = viewGroup.getChildAt(i);
            tag = playerView.getTag();
            if (tag != null && tag instanceof Type) {
                playerViewList.add(playerView);
            }
        }
        viewGroup = (ViewGroup) findViewById(R.id.players_midfields);
        for (int i = 0 ; i < viewGroup.getChildCount() ; i++) {
            playerView = viewGroup.getChildAt(i);
            tag = playerView.getTag();
            if (tag != null && tag instanceof Type) {
                playerViewList.add(playerView);
            }
        }
        viewGroup = (ViewGroup) findViewById(R.id.players_forwards);
        for (int i = 0 ; i < viewGroup.getChildCount() ; i++) {
            playerView = viewGroup.getChildAt(i);
            tag = playerView.getTag();
            if (tag != null && tag instanceof Type) {
                playerViewList.add(playerView);
            }
        }
        return playerViewList;
    }
    
    private View findNextView(List<View> lst, Type type) {
        View playerView = null;
        for (Iterator<View> it = lst.iterator(); it.hasNext();) {
            View view = (View) it.next();
            if (view.getTag() != null && view.getTag() == type) {
                playerView = view;
                it.remove();
                break;
            }
        }
        return playerView;
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int zone = -1;
            if (idZone == R.id.players_defenses) {
                zone = ZONE_DEFENDERS;
            } else if (idZone == R.id.players_midfields) {
                zone = ZONE_MIDFIELDERS;
            } else if (idZone == R.id.players_forwards) {
                zone = ZONE_FORWARDS;
            }
            
            // 0 no es válido, 1 avanza 2 retrocede
            int movement = 0;
            float velocity;
            // Actuamos en función de si estamos en vertical u horizontal
            float current;
            float started;
            if (vertical) {
                velocity = Math.abs(velocityX);
                current = e1.getX();
                started = e2.getX();
            } else {
                velocity = Math.abs(velocityY);
                current = e2.getY();
                started = e1.getY();
            }
            if(Math.abs(velocity) > SWIPE_THRESHOLD_VELOCITY && current - started > SWIPE_MIN_DISTANCE) {
                // Derecha a izquierda o Arriba a abajo
                movement = 1;
            } else if (Math.abs(velocity) > SWIPE_THRESHOLD_VELOCITY && started - current > SWIPE_MIN_DISTANCE) {
                // Izquierda a derecha o abajo a arriba
                movement = 2;
            }
            
            if (movement > 0) {
                changeZone(zone, movement == 1);
            }
            
            return true;
        }
    }
    
    protected void changeZone(int zone, boolean next) {
        if (lineUpData != null 
                && lineUpData.possibleDefenders != null 
                && lineUpData.possibleMidfielders != null 
                && lineUpData.possibleForwards != null) {
            boolean valid = false;
            if (zone == ZONE_DEFENDERS) {
                if (next && lineUpData.possibleDefenders.size() - 1 > lineUpData.defenders) {
                    // En este caso podemos avanzar
                    lineUpData.defenders++;
                    valid = true;
                } else if (!next && lineUpData.defenders > 0) {
                    // podemos retroceder
                    lineUpData.defenders--;
                    valid = true;
                }
            } else if (zone == ZONE_MIDFIELDERS) {
                if (next && lineUpData.possibleMidfielders.size() - 1 > lineUpData.midfielders) {
                    // En este caso podemos avanzar
                    lineUpData.midfielders++;
                    valid = true;
                } else if (!next && lineUpData.midfielders > 0) {
                    // podemos retroceder
                    lineUpData.midfielders--;
                    valid = true;
                }
            } else if (zone == ZONE_FORWARDS) {
                if (next && lineUpData.possibleForwards.size() - 1 > lineUpData.forwards) {
                    // En este caso podemos avanzar
                    lineUpData.forwards++;
                    valid = true;
                } else if (!next && lineUpData.forwards > 0) {
                    // podemos retroceder
                    lineUpData.forwards--;
                    valid = true;
                }
            }
            
            if (valid) {
                PlayersView container = (PlayersView) findViewById(R.id.players_view);
                putPlayers(container, zone);
            }
        }
    }
    
    private String tacticToString(int[] lineUp, boolean checkOrientation) {
        if (checkOrientation && !vertical) {
            return "";
        } else {
            return lineUp[0] + "-" + lineUp[1] + "-" + lineUp[2];
        }        
    }
    
    private void putPlayer(RelativeLayout layout, int x, int y, Type type) {
        View view = inflater.inflate(R.layout.tactics_player, null);
        view.setTag(type);
        int[] dimens = calculatePlayerDimensions();
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(dimens[0], dimens[1]);
        p.leftMargin = x;
        p.topMargin = y;
        layout.addView(view, p);
    }
    
    private int[] calculatePlayerDimensions() {
        final float scale = getResources().getDisplayMetrics().density;
        return new int[] {(int) (WIDTH_PLAYER * scale + 0.5f), (int) (HEIGHT_PLAYER * scale + 0.5f)};
    }
    
    private void putButtons(RelativeLayout layout, int zone) {
        boolean prev = false;
        boolean next = false;
        if (zone == 2) {
            prev = lineUpData.defenders > 0;
            next = lineUpData.defenders < lineUpData.possibleDefenders.size() - 1;
        } else if (zone == 1) {
            prev = lineUpData.midfielders > 0;
            next = lineUpData.midfielders < lineUpData.possibleMidfielders.size() - 1;
        } else {
            prev = lineUpData.forwards > 0;
            next = lineUpData.forwards < lineUpData.possibleForwards.size() - 1;
        }
        int leftMargin;
        int topMargin;
        if (prev) {
            if (vertical) {
                leftMargin = 0;
                topMargin = (layout.getHeight() / 2) - (HEIGHT_BUTTON / 2);
            } else {
                leftMargin = (layout.getWidth() / 2) - (HEIGHT_BUTTON / 2);
                topMargin = layout.getHeight() - WIDTH_BUTTON;
            }
            putButton(layout, leftMargin, topMargin, false, zone);
        }
        
        if (next) {
            if (vertical) {
                leftMargin = layout.getWidth() - WIDTH_BUTTON;
                topMargin = (layout.getHeight() / 2) - (HEIGHT_BUTTON / 2);
            } else {
                leftMargin = (layout.getWidth() / 2) - (HEIGHT_BUTTON / 2);
                topMargin = 0;
            }
            putButton(layout, leftMargin, topMargin, true, zone);
        }
    }
    
    private void putButton(RelativeLayout layout, int leftMargin, int topMargin, final boolean next, final int zone) {
        View view = new View(this);
        view.setBackgroundColor(Color.BLUE);
        view.setTag(next);
        RelativeLayout.LayoutParams p;
        if (vertical) {
            p = new RelativeLayout.LayoutParams(WIDTH_BUTTON, HEIGHT_BUTTON);
        } else {
            p = new RelativeLayout.LayoutParams(HEIGHT_BUTTON, WIDTH_BUTTON);
        }
        p.leftMargin = leftMargin;
        p.topMargin = topMargin;
        layout.addView(view, p);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeZone(zone, next);
            }
        });
    }
    
    private void putLineUp(int[] lineUp) {
        boolean newTactic = false;
        if (lineUpData.lineUp == null  || !Arrays.equals(lineUpData.lineUp, lineUp)) {
            newTactic = true;
            lineUpData.lineUp = lineUp;
        }
        
        if (newTactic || lineUpData.possibleDefenders == null) {
            lineUpData.possibleDefenders = FieldUtil.defenders(lineUp[0]);
            lineUpData.defenders = 0;
        }
        if (newTactic || lineUpData.possibleMidfielders == null) {
            lineUpData.possibleMidfielders = FieldUtil.midfielders(lineUp[1]);
            lineUpData.midfielders = 0;
        }
        if (newTactic || lineUpData.possibleForwards == null) {
            lineUpData.possibleForwards = FieldUtil.forwards(lineUp[2]);
            lineUpData.forwards = 0;
        }        
        PlayersView container = (PlayersView) findViewById(R.id.players_view);        
        putPlayers(container, ZONE_DEFENDERS);
        putPlayers(container, ZONE_MIDFIELDERS);
        putPlayers(container, ZONE_FORWARDS);
        
    }
    
    private void putPlayers(PlayersView container, int zone) {
        Type[] types;
        int[] pos;
        int[] coord;
        RelativeLayout field;
        switch (zone) {
        case 2:
            types = lineUpData.possibleDefenders.get(lineUpData.defenders);
            field = (RelativeLayout) container.findViewById(R.id.players_defenses);
            field.removeAllViews();
            // Ponemos el portero
            pos = FieldUtil.position(Position.Type.GOAL_KEEPER, container.getFieldW(), container.getFieldH());
            coord = calculateCoord(pos, container, 3);
            putPlayer(field, coord[0], coord[1], Type.GOAL_KEEPER);            
            break;
        case 1:
            types = lineUpData.possibleMidfielders.get(lineUpData.midfielders);
            field = (RelativeLayout) container.findViewById(R.id.players_midfields);
            field.removeAllViews();
            break;
        default:
            types = lineUpData.possibleForwards.get(lineUpData.forwards);
            field = (RelativeLayout) container.findViewById(R.id.players_forwards);
            field.removeAllViews();
            break;
        }
        Map<Type, Integer> mapTypes = FieldUtil.calculateTypes(types);
        List<int[]> positions;
        int num;
        for (Type type : mapTypes.keySet()) {
            positions = FieldUtil.positions(type, container.getFieldW(), container.getFieldH());
            num = mapTypes.get(type);
            if (num == 1 && positions.size() == 3) {
                pos = positions.get(2);
                coord = calculateCoord(pos, container, zone);
                putPlayer(field, coord[0], coord[1], type);
            } else {
                // Como mucho habrá tres
                while (num > 0) {                
                    num--;
                    pos = positions.get(num);
                    coord = calculateCoord(pos, container, zone);
                    putPlayer(field, coord[0], coord[1], type);
                }
            }
        }
        putButtons(field, zone);
    }
    
    private int[] calculateCoord(int[] pos, PlayersView container, int zone) {
        int[] coord = new int[2];
        int x;
        int y;
        int left = 0;
        if (zone > 0) {
            left = container.getSidesSize();
            if (zone > 1) {
                left += container.getCentralSize();
            }
        }
        
        int[] dimens = calculatePlayerDimensions();
        
        if (container.isVertical()) {
            x = pos[1];
            y = container.getFieldH() - pos[0];
            y = y - left;
            x = x - dimens[1] / 2;
            y = y - dimens[0];
        } else {
            x = container.getFieldH() - pos[0];
            y = pos[1];
            x = x - left;
            x = x - dimens[1];
            y = y - dimens[0] / 2;
        }
        coord[0] = x;
        coord[1] = y;
        return coord;
    }

}

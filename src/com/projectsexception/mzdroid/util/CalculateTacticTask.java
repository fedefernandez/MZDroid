package com.projectsexception.mzdroid.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;
import android.util.Log;

import com.projectsexception.mz.htmlapi.model.Player;
import com.projectsexception.mzdroid.model.Position;
import com.projectsexception.mzdroid.model.Position.Type;

public class CalculateTacticTask extends AsyncTask<Type[], Void, List<Valor>> {
    
    /*
     * Alineaciones posibles
     *  Máx y Mín Jugadores por posición 
     *      Defensas: 3 - 5 (3 - 7)
     *      Medios: 2 - 5 (2 - 6)
     *      Delanteros: 1 - 5
     *  
     *  Combinaciones
         1 - CENTRAL_DEFENDER 
         2 - WING_DEFENDER,        
         3 - DEFENSIVE_MIDFIELDER,
         4 - ATTACKING_MIDFIELDER,
         5 - WING_DEFENSIVE_MIDFIELDER,
         6 - WING_ATTACKING_MIDFIELDER,
         7 - CENTRE_FORWARD,
         8 - CENTRE_STRIKER,
         9 - WING_FORWARD,
        10 - WING_STRIKER
        
            Defensas:
                3: 1,2,2
                4: 1,1,2,2
                5: 1,1,1,2,2
        
            Medios
                2: 3,3       | 4,4       | 3,4       | 5,5       | 6,6
                3: 3,3,4     | 3,4,4     | 3,5,5     | 3,6,6     | 4,5,5   | 4,6,6
                4: 3,3,5,5   | 3,3,6,6   | 3,4,5,5   | 3,4,6,6   | 4,4,5,5 | 4,4,6,6
                5: 3,3,4,5,5 | 3,3,4,6,6 | 3,4,4,5,5 | 3,4,4,6,6 |
            
            Delanteros:
                1: 7         | 8
                2: 7,7       | 7,8         | 8,8       | 7,9         | 7,10 | 8,9 | 8,10
                3: 7,9,9     | 7,10,10     | 8,9,9     | 8,10,10 
                4: 7,7,9,9   | 7,7,10,10   | 7,8,9,9   | 7,8,10,10   | 8,8,9,9 | 8,8,10,10 
                5: 7,7,8,9,9 | 7,7,8,10,10 | 7,8,8,9,9 | 7,8,8,10,10
     */
    
    public static interface ResultListener {
        void onResult(List<Valor> result);
    }
    
    private Collection<Player> players;
    private ResultListener listener;
    
    public CalculateTacticTask(Collection<Player> players, ResultListener listener) {
        this.players = players;
        this.listener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("Pruebas", "INICIO");
    }

    @Override
    protected List<Valor> doInBackground(Type[]... params) {
        Map<Integer, List<Position>> mapPositions = new HashMap<Integer, List<Position>>();
        for (Player player : players) {
            mapPositions.put(player.getId(), MZUtil.calculatePositions(player.getSkills(), true));            
        }
        
        Type[] alineacion = params[0];

        return calculaVoraz(alineacion, mapPositions);
    }
    
    @Override
    protected void onPostExecute(List<Valor> result) {
        super.onPostExecute(result);
        if (result != null) {
            Log.d("Pruebas", "Alineación elegida con valor " + getTotal(result));
        }
        listener.onResult(result);
    }
    
    private List<Valor> calculaVoraz(Type[] alineacion, Map<Integer, List<Position>> mapPositions) {
        List<Valor> voraz;
        Map<Integer, List<Position>> copiaMap;
        List<Valor> mejor = null;
        int mejorValor = 0;
        List<List<Integer>> listas = calculaArrays(alineacion.length, 200);
        for (List<Integer> list : listas) {
            voraz = new ArrayList<Valor>();
            copiaMap = new HashMap<Integer, List<Position>>(mapPositions);
            for (Integer i : list) {
                Type posicion = alineacion[i];
                Valor v = getMejor(posicion, copiaMap);
                voraz.add(v);
                copiaMap.remove(v.playerId);
            }
            int valorVoraz = getTotal(voraz);
            if (valorVoraz > mejorValor) {
                mejorValor = valorVoraz;
                mejor = voraz;
            }
        }
        return mejor;
    }
    
    private List<List<Integer>> calculaArrays(int tamanyo, int num) {
        List<List<Integer>> listas = new ArrayList<List<Integer>>();
        Set<String> elegidos = new HashSet<String>();
        List<Integer> lst = new ArrayList<Integer>();
        for (int i = 0 ; i < tamanyo ; i++) {
            lst.add(i);
        }
        elegidos.add(lst.toString());
        listas.add(new ArrayList<Integer>(lst));
        while (listas.size() < num) {
            Collections.shuffle(lst);
            if (!elegidos.contains(lst.toString())) {
                elegidos.add(lst.toString());
                listas.add(new ArrayList<Integer>(lst));
            }
        }
        return listas;
    }
    
    public static int getTotal(List<Valor> valores) {
        int total = 0;
        for (Valor valor : valores) {
            total += valor.valor;
        }
        return total;
    }
    
    private Valor getMejor(Type posicion, Map<Integer, List<Position>> mapPositions) {
        int maxPlayerId = 0;
        int maxValor = Integer.MIN_VALUE;
        for (Integer playerId : mapPositions.keySet()) {
            int valor = getValor(posicion, mapPositions.get(playerId));
            if (valor > maxValor) {
                maxValor = valor;
                maxPlayerId = playerId;
            }
        }
        Valor v = new Valor();
        v.playerId = maxPlayerId;
        v.valor = maxValor;
        v.posicion = posicion;
        return v;
    }
    
    private int getValor(Type posicion, List<Position> posiciones) {
        for (Position p : posiciones) {
            if (p.getType() == posicion) {
                return p.getValue();
            }
        }
        return 0;
    }

}

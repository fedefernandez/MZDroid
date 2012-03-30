package com.projectsexception.mzdroid.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.projectsexception.mzdroid.model.Position.Type;


public class FieldUtil {
    
    public static final double CENTRAL = 9.15;
    public static final double CENTRAL_SIZE = 30;
    public static final int MARGIN = 10;
    public static final int FIELD_WIDTH = 80;
    public static final int FIELD_HEIGHT = 100;
    public static final int AREA_WIDTH = 40;
    public static final int AREA_HEIGHT = 20;
    public static final int SMALL_AREA_WIDTH = 18;
    public static final int SMALL_AREA_HEIGHT = 7;
    public static final int PENAL = 14;
    public static final int AREA_ARC_RADIO = 10;
    public static final int CORNER_RECT_SIZE = 3;
    
    private static final Map<Type, Integer> POSITIONS_X;
    private static final Map<Type, int[]> POSITIONS_Y;
    
    private static final Type[][] LINEUPS_DEFENDER = {
        {Type.WING_DEFENDER, Type.CENTRAL_DEFENDER, Type.WING_DEFENDER},
        {Type.CENTRAL_DEFENDER, Type.CENTRAL_DEFENDER, Type.CENTRAL_DEFENDER},
        {Type.WING_DEFENDER, Type.CENTRAL_DEFENDER, Type.CENTRAL_DEFENDER, Type.WING_DEFENDER},
        {Type.WING_DEFENDER, Type.CENTRAL_DEFENDER, Type.CENTRAL_DEFENDER, Type.CENTRAL_DEFENDER, Type.WING_DEFENDER}
    };
    
    private static final Type[][] LINEUPS_MIDFIELDER = {
        /* 2 medios */
        {Type.DEFENSIVE_MIDFIELDER, Type.DEFENSIVE_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER},
        {Type.ATTACKING_MIDFIELDER, Type.ATTACKING_MIDFIELDER},
        {Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER},
        /* 2 medios */
        
        /* 3 medios */
        {Type.DEFENSIVE_MIDFIELDER, Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.ATTACKING_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER},
        {Type.ATTACKING_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER},
        /* 3 medios */
        
        /* 4 medios */
        {Type.DEFENSIVE_MIDFIELDER, Type.DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.DEFENSIVE_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER},
        {Type.ATTACKING_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.ATTACKING_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER},
        /* 4 medios */
        
        /* 5 medios */
        {Type.DEFENSIVE_MIDFIELDER, Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER, Type.WING_DEFENSIVE_MIDFIELDER},
        {Type.DEFENSIVE_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER, Type.WING_ATTACKING_MIDFIELDER}
        /* 5 medios */
    };
    
    private static final Type[][] LINEUPS_FORWARDS = {
        /* 1 atacante */
        {Type.CENTRE_FORWARD},
        {Type.CENTRE_STRIKER},
        /* 1 atacante */
        
        /* 2 atacante */
        {Type.CENTRE_FORWARD, Type.CENTRE_FORWARD},
        {Type.CENTRE_FORWARD, Type.CENTRE_STRIKER},
        {Type.CENTRE_STRIKER, Type.CENTRE_STRIKER},
        {Type.CENTRE_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_FORWARD, Type.WING_STRIKER},
        {Type.CENTRE_STRIKER, Type.WING_FORWARD},
        {Type.CENTRE_STRIKER, Type.WING_STRIKER},
        /* 2 atacante */
        
        /* 3 atacante */
        {Type.CENTRE_FORWARD, Type.WING_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_FORWARD, Type.WING_STRIKER, Type.WING_STRIKER},
        {Type.CENTRE_STRIKER, Type.WING_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_STRIKER, Type.WING_STRIKER, Type.WING_STRIKER},
        /* 3 atacante */
        
        /* 4 atacante */
        {Type.CENTRE_FORWARD, Type.CENTRE_FORWARD, Type.WING_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_FORWARD, Type.CENTRE_FORWARD, Type.WING_STRIKER, Type.WING_STRIKER},
        {Type.CENTRE_FORWARD, Type.CENTRE_STRIKER, Type.WING_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_FORWARD, Type.CENTRE_STRIKER, Type.WING_STRIKER, Type.WING_STRIKER},
        {Type.CENTRE_STRIKER, Type.CENTRE_STRIKER, Type.WING_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_STRIKER, Type.CENTRE_STRIKER, Type.WING_STRIKER, Type.WING_STRIKER},
        /* 4 atacante */
        
        /* 5 atacante */
        {Type.CENTRE_FORWARD, Type.CENTRE_FORWARD, Type.CENTRE_STRIKER, Type.WING_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_FORWARD, Type.CENTRE_FORWARD, Type.CENTRE_STRIKER, Type.WING_STRIKER, Type.WING_STRIKER},
        {Type.CENTRE_FORWARD, Type.CENTRE_STRIKER, Type.CENTRE_STRIKER, Type.WING_FORWARD, Type.WING_FORWARD},
        {Type.CENTRE_FORWARD, Type.CENTRE_STRIKER, Type.CENTRE_STRIKER, Type.WING_STRIKER, Type.WING_STRIKER}
        /* 5 atacante */
        
    };    
    
    public static final int[][] LINEUPS = {
        {3,2,5}, {3,3,4}, {3,4,3}, {3,5,2},
        {4,2,4}, {4,3,3}, {4,4,2}, {4,5,1},
        {5,2,3}, {5,3,2}, {5,4,1}
    };
    
    static {
        POSITIONS_X = new HashMap<Type, Integer>();
        POSITIONS_X.put(Type.GOAL_KEEPER, 0);
        POSITIONS_X.put(Type.CENTRAL_DEFENDER, 20);
        POSITIONS_X.put(Type.WING_DEFENDER, 20);
        POSITIONS_X.put(Type.DEFENSIVE_MIDFIELDER, 40);
        POSITIONS_X.put(Type.ATTACKING_MIDFIELDER, 55);
        POSITIONS_X.put(Type.WING_DEFENSIVE_MIDFIELDER, 40);
        POSITIONS_X.put(Type.WING_ATTACKING_MIDFIELDER, 55);
        POSITIONS_X.put(Type.CENTRE_FORWARD, 70);
        POSITIONS_X.put(Type.CENTRE_STRIKER, 85);
        POSITIONS_X.put(Type.WING_FORWARD, 70);
        POSITIONS_X.put(Type.WING_STRIKER, 85);
        POSITIONS_Y = new HashMap<Type, int[]>();
        POSITIONS_Y.put(Type.GOAL_KEEPER, new int[] {40});
        POSITIONS_Y.put(Type.CENTRAL_DEFENDER, new int[] {25,55,40});
        POSITIONS_Y.put(Type.WING_DEFENDER, new int[] {10,70});
        POSITIONS_Y.put(Type.DEFENSIVE_MIDFIELDER, new int[] {25,55,40});
        POSITIONS_Y.put(Type.ATTACKING_MIDFIELDER, new int[] {25,55,40});
        POSITIONS_Y.put(Type.WING_DEFENSIVE_MIDFIELDER, new int[] {10,70});
        POSITIONS_Y.put(Type.WING_ATTACKING_MIDFIELDER, new int[] {10,70});
        POSITIONS_Y.put(Type.CENTRE_FORWARD, new int[] {25,55,40});
        POSITIONS_Y.put(Type.CENTRE_STRIKER, new int[] {25,55,40});
        POSITIONS_Y.put(Type.WING_FORWARD, new int[] {10,70});
        POSITIONS_Y.put(Type.WING_STRIKER, new int[] {10,70});
    }
    
    public static int[] calculateFieldDimmensions(int parentWidth, int parentHeight) {
        int fieldContainerW;
        int fieldContainerH;
        // Este es el espacio del que dispongo
        if (parentWidth > parentHeight) {
            // Apaisado
            fieldContainerH = parentWidth - (2 * MARGIN);
            fieldContainerW = parentHeight - (2 * MARGIN);
        } else {
            // Vertical
            fieldContainerW = parentWidth - (2 * MARGIN);
            fieldContainerH = parentHeight - (2 * MARGIN);
        }
        
        int[] dimensions = new int[2];
        
        // Vamos a calcular el tamaño del campo (sumamos 2 para que quepan las líneas)
        dimensions[0] = (fieldContainerH * FIELD_WIDTH) / FIELD_HEIGHT + 2;
        if (dimensions[0] > fieldContainerW) {
            // Las proporciones en función del alto no caben, lo hacemos en función del ancho
            dimensions[1] = (fieldContainerW * FIELD_HEIGHT) / FIELD_WIDTH + 2;
            dimensions[0] = (dimensions[1] * FIELD_WIDTH) / FIELD_HEIGHT + 2;
        } else {
            dimensions[1] = (dimensions[0] * FIELD_HEIGHT) / FIELD_WIDTH + 2;
        }
        
        return dimensions;
    }
    
    public static int calculateCenterDimension(int width, int height) {
        return (int) ((width * CENTRAL_SIZE) / FIELD_WIDTH);
    }
    
    public static List<Type[]> defenders(int num) {
        List<Type[]> defenders = new ArrayList<Type[]>();
        for (int i = 0; i < LINEUPS_DEFENDER.length; i++) {
            if (LINEUPS_DEFENDER[i].length == num) {
                defenders.add(LINEUPS_DEFENDER[i]);
            }
        }
        return defenders;
    }
    
    public static List<Type[]> midfielders(int num) {
        List<Type[]> midfielders = new ArrayList<Type[]>();
        for (int i = 0; i < LINEUPS_MIDFIELDER.length; i++) {
            if (LINEUPS_MIDFIELDER[i].length == num) {
                midfielders.add(LINEUPS_MIDFIELDER[i]);
            }
        }
        return midfielders;
    }
    
    public static List<Type[]> forwards(int num) {
        List<Type[]> forwards = new ArrayList<Type[]>();
        for (int i = 0; i < LINEUPS_FORWARDS.length; i++) {
            if (LINEUPS_FORWARDS[i].length == num) {
                forwards.add(LINEUPS_FORWARDS[i]);
            }
        }
        return forwards;
    }
    
    public static Map<Type, Integer> calculateTypes(Type[] types) {
        Map<Type, Integer> mapTypes = new HashMap<Type, Integer>();
        
        int value;
        for (int i = 0; i < types.length; i++) {
            if (mapTypes.containsKey(types[i])) {
                value = mapTypes.get(types[i]);
            } else {
                value = 0;
            }
            mapTypes.put(types[i], value + 1);
        }
        
        return mapTypes;
    }
    
    public static List<int[]> positions(Type position, int width, int height) {
        List<int[]> positions = new ArrayList<int[]>();
        Integer x = POSITIONS_X.get(position);
        int arrayY[] = POSITIONS_Y.get(position);
        if (x != null && arrayY != null) {
            x = (width * x) / FIELD_WIDTH;
            int y;
            for (int i = 0; i < arrayY.length; i++) {
                y = (width * arrayY[i]) / FIELD_WIDTH;
                positions.add(new int[] {x, y});
            }
        }
        return positions;
    }
    
    public static int[] position(Type position, int width, int height) {
        List<int[]> positions = positions(position, width, height);
        if (positions.size() > 0) {
            return positions.get(0);
        }
        return null;
    }

}

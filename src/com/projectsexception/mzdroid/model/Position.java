package com.projectsexception.mzdroid.model;

public class Position implements Comparable<Position> {
    
    public static enum Type {
        GOAL_KEEPER,
        CENTRAL_DEFENDER,
        WING_DEFENDER,        
        DEFENSIVE_MIDFIELDER,
        ATTACKING_MIDFIELDER,
        WING_DEFENSIVE_MIDFIELDER,
        WING_ATTACKING_MIDFIELDER,
        CENTRE_FORWARD,
        CENTRE_STRIKER,
        WING_FORWARD,
        WING_STRIKER;
        
        public boolean isDefender() {
            switch (this) {
            case CENTRAL_DEFENDER:
            case WING_DEFENDER:
                return true;
            default:
                return false;
            }
        }
        
        public boolean isMidfielder() {
            switch (this) {
            case DEFENSIVE_MIDFIELDER:
            case ATTACKING_MIDFIELDER:
            case WING_DEFENSIVE_MIDFIELDER:
            case WING_ATTACKING_MIDFIELDER:
                return true;
            default:
                return false;
            }
        }
        
        public boolean isForward() {
            switch (this) {
            case CENTRE_FORWARD:
            case CENTRE_STRIKER:
            case WING_FORWARD:
            case WING_STRIKER:
                return true;
            default:
                return false;
            }
        }
    }
    
    private Position.Type type;
    private int value;
    
    public Position(Type type, int value) {
        super();
        this.type = type;
        this.value = value;
    }

    public Position.Type getType() {
        return type;
    }
    
    public void setType(Position.Type type) {
        this.type = type;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Position another) {
        if (another != null) {
            return another.getValue() - value;
        }
        return 0;
    }

}

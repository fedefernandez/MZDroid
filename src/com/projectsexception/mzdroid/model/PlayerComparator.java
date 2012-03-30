package com.projectsexception.mzdroid.model;

import java.util.Comparator;

import com.projectsexception.mz.htmlapi.model.Player;

public class PlayerComparator implements Comparator<Player> {

    @Override
    public int compare(Player object1, Player object2) {        
        return object1.getNumber() - object2.getNumber();
    }

}

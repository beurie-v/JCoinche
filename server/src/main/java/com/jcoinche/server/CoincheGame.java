package com.jcoinche.server;

import java.util.ArrayList;

public class CoincheGame {
    public String               name;
    public ArrayList<Player>    players = new ArrayList();
    public CoincheReferee       referee = null;

    public CoincheGame () {
    }

    public CoincheGame(String gameName, Player owner) {
        name = gameName;
        players.add(owner);
        owner.game = this;
    }
}

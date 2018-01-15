package com.jcoinche.server;

public class        Card {
    public enum     Color { SPADES, CLUBS, HEARTS, DIAMONDS, ALL, NONE }
    public enum     Type { ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN  }

    private Color   color;
    private Type    type;
    private Integer defaultPts;
    private Integer trumpPts;
    private String  player;

    public Card() {
        this.color = Color.NONE;
        this.type = Type.ACE;
        this.defaultPts = 0;
        this.trumpPts = 0;
        this.player = "";
    }

    public Card(Color color, Type type, Integer defaultPts, Integer trumpPts, Player player) {
        this.color = color;
        this.type = type;
        this.defaultPts = defaultPts;
        this.trumpPts = trumpPts;
        this.player = new String(player.getNickname());
    }

    public Color    getColor() {
        return this.color;
    }

    public Type     getType() {
        return this.type;
    }

    public Integer  getDefaultPts() {
        return this.defaultPts;
    }

    public Integer  getTrumpPts() {
        return this.trumpPts;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = new String(player.getNickname());
    }
}

package com.jcoinche.server;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class    Bid {
    private Card.Color  color;
    private Player      player;
    private Integer     score;

    public Bid() {
    }

    public Bid(Card.Color color, Player player, Integer score) {
        this.color = color;
        this.player = player;
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }

    public Player getPlayer() {
        return player;
    }

    public Card.Color getColor() {
        return color;
    }

    public void     increaseScore() {
        if (score < 160) {
            score += 10;
        }
    }

    public void     decreaseScore() {
        if (score > 80) {
            score -= 10;
        }
    }

    public void     changeColor() {
        Card.Color              initColors[] = { Card.Color.SPADES, Card.Color.CLUBS, Card.Color.HEARTS,
                Card.Color.DIAMONDS, Card.Color.ALL, Card.Color.NONE };
        ArrayList<Card.Color>   colors = new ArrayList<Card.Color>(Arrays.asList(initColors));

        Integer     index = colors.indexOf(color);
        color = initColors[(index + 1) % initColors.length];
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

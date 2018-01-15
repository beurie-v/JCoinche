package com.jcoinche.server;

import java.util.ArrayList;
import java.util.List;

public class                Player {
    private List<Card>      hand;
    private String          nickname;
    private Boolean         inGame;
    private Boolean         isBidding;
    private Boolean         isPlaying;
    transient CoincheGame   game;

    public              Player () {
        this.nickname = "";
        this.hand = new ArrayList<Card>();
        this.inGame = false;
        this.isBidding = false;
        this.isPlaying = false;
        this.game = null;
    }

    public              Player(String nickname) {
        this.nickname = nickname;
        this.hand = new ArrayList<Card>();
        this.inGame = false;
        this.isBidding = false;
        this.isPlaying = false;
        this.game = null;
    }

    public List<Card>   getHand() {
        return hand;
    }

    public void         resetHand() {
        this.hand = new ArrayList<Card>();
    }

    public void         addCard(Card card) {
        this.hand.add(card);
    }

    public String       getNickname() {
        return nickname;
    }

    public Boolean      isInGame() {
        return inGame;
    }

    public void         setInGame(Boolean isInGame) {
        inGame = isInGame;
    }

    public Boolean      isBidding() {
        return isBidding;
    }

    public void         setBidding(Boolean bidding) {
        isBidding = bidding;
    }

    public Boolean      isPlaying() {
        return isPlaying;
    }

    public void         setPlaying(Boolean playing) {
        isPlaying = playing;
    }

    public void         setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void         removeCard(Card cardToRemove) {
        for (Card card : hand) {
            if (card.getColor() == cardToRemove.getColor() && card.getType() == cardToRemove.getType()) {
                hand.remove(card);
                return;
            }
        }
    }
}

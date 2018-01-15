package com.jcoinche.server;

public class    CardFactory {

    public      CardFactory() {
    }

    public Card createAce(Card.Color color, Player player) {
        return new Card(color, Card.Type.ACE, 19, 7, player);
    }

    public Card createKing(Card.Color color, Player player) {
        return new Card(color, Card.Type.KING, 4, 3, player);
    }

    public Card createQueen(Card.Color color, Player player) {
        return new Card(color, Card.Type.QUEEN, 3, 2, player);
    }

    public Card createJack(Card.Color color, Player player) {
        return new Card(color, Card.Type.JACK, 2, 14, player);
    }

    public Card createTen(Card.Color color, Player player) {
        return new Card(color, Card.Type.TEN, 5, 10, player);
    }

    public Card createNine(Card.Color color, Player player) {
        return new Card(color, Card.Type.NINE, 0, 9, player);
    }

    public Card createEight(Card.Color color, Player player) {
        return new Card(color, Card.Type.EIGHT, 0, 0, player);
    }

    public Card createSeven(Card.Color color, Player player) {
        return new Card(color, Card.Type.SEVEN, 0, 0, player);
    }
}

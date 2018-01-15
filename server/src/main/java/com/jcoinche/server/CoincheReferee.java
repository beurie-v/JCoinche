package com.jcoinche.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class                    CoincheReferee {
    private Card.Color          trumpColor;
    private Card.Color          turnColor;
    private ArrayList<Player>   players;
    private ArrayList<Card>     board;
    private Bid                 highBid;
    private Integer             playing;
    private Integer             roundScore[] = { 0 , 0 };
    private Integer             scoreTeam[] = { 0, 0 };
    private Integer             pass;
    private Integer             biddingRound;
    private Integer             currStartingPlayer;
    private Integer             startingPlayer;
    private Integer             winner;

    public Player   getPlayerFromNickname(String nickname) {
        for (Player player : players) {
            if (nickname.equals(player.getNickname())) {
                return player;
            }
        }
        return null;
    }

    public Boolean  isValidPlay(Card card) {
        if (turnColor == Card.Color.NONE)
            return true;
        List<Card> playerHand = this.getPlayerFromNickname(card.getPlayer()).getHand();
        Card.Color color = card.getColor();
        Boolean hasTrump = false;
        Boolean hasTurn = false;

        for (Card currCard : playerHand) {
            if (currCard.getColor() == trumpColor)
                hasTrump = true;
            if (currCard.getColor() == turnColor)
                hasTurn = true;
        }
        return color == turnColor || !hasTurn && (color == trumpColor || !hasTrump);
    }

    public void     endGame(Integer winner) {
        this.winner = winner;
    }

    public void     endBattle() {
        Integer     biddingTeam = players.indexOf(highBid.getPlayer()) % 2 == 0 ? 0 : 1;
        Integer     defendingTeam = biddingTeam == 0 ? 1 : 0;
        Integer     scoreToWin = 701;

        if (roundScore[biddingTeam] >= highBid.getScore()) {
            scoreTeam[biddingTeam] += roundScore[biddingTeam] + highBid.getScore();
            scoreTeam[defendingTeam] += roundScore[defendingTeam];
        } else {
            scoreTeam[biddingTeam] += 0;
            scoreTeam[defendingTeam] += 162 + highBid.getScore();
        }
        if (scoreTeam[0] >= scoreToWin) {
            if (scoreTeam[1] >= scoreToWin) {
                if (scoreTeam[0] > scoreTeam[1])
                    this.endGame(1);
                else if (scoreTeam[0] < scoreTeam[1])
                    this.endGame(2);
                else
                    this.startRound();
            } else {
                this.endGame(1);
            }
        } else if (scoreTeam[1] >= scoreToWin) {
            this.endGame(2);
        } else {
            this.startRound();
        }
    }

    public Integer  getBoardValue() {
        Integer     res = 0;

        for (Card card : board) {
            if (card.getColor() == trumpColor)
                res += card.getTrumpPts();
            else
                res += card.getDefaultPts();
        }
        return res;
    }

    public void     newCard(Card card) {
        if (card == null || getPlayerFromNickname(card.getPlayer()) != players.get(playing) ||
                !this.isValidPlay(card))
            return;
        board.add(card);
        if (turnColor == Card.Color.NONE)
            turnColor = card.getColor();
        players.get(playing).removeCard(card);
        players.get(playing).setPlaying(false);
        playing = (playing + 1) % 4;
        if (board.size() == 4) {
            Card    winningCard = board.get(0);
            Integer boardValue = this.getBoardValue();

            for (int i = 1; i < board.size(); ++i) {
                winningCard = this.compareCards(winningCard, board.get(i));
            }
            if (players.indexOf(getPlayerFromNickname(winningCard.getPlayer())) % 2 == 0)
                roundScore[0] += boardValue;
            else
                roundScore[1] += boardValue;
            currStartingPlayer = players.indexOf(getPlayerFromNickname(winningCard.getPlayer()));
            if (players.get(currStartingPlayer).getHand().isEmpty())
                this.endBattle();
            else {
                this.startBattle();
            }
        } else {
            players.get(playing).setPlaying(true);
        }
    }

    public void     startBattle() {
        for (Player player : players) {
            player.setBidding(false);
            player.setPlaying(false);
        }
        if (highBid != null)
            trumpColor = highBid.getColor();
        board = new ArrayList<Card>();
        turnColor = Card.Color.NONE;
        players.get(currStartingPlayer).setPlaying(true);
        playing = currStartingPlayer;
    }

    public CoincheReferee() {
    }

    public CoincheReferee(ArrayList<Player> players) {
        this.trumpColor = Card.Color.NONE;
        this.turnColor = Card.Color.NONE;
        this.players = players;
        this.highBid = null;
        this.playing = 0;
        this.pass = 0;
        this.biddingRound = 0;
        this.startingPlayer = 0;
        this.currStartingPlayer = 0;
        this.board = new ArrayList<Card>();
        this.winner = 0;
    }

    public void     newBid(Bid bid) {
        if (bid == null || !bid.getPlayer().getNickname().equals(players.get(playing).getNickname()) ||
                (highBid != null && bid.getScore() != 0 && bid.getScore() < highBid.getScore())
                || bid.getScore() > 160) {
            return;
        }
        if ((highBid == null && bid.getScore() >= 80) ||
                (highBid != null && bid.getScore() > highBid.getScore())) {
            highBid = bid;
            pass = 0;
            if (bid.getScore() == 160) {
                this.startBattle();
                return;
            }
        } else {
            pass += 1;
        }
        players.get(playing).setBidding(false);
        if (biddingRound == 0 && pass == 4) {
            startingPlayer = currStartingPlayer;
            this.startRound();
            return;
        } else if (highBid != null && pass == 3) {
            this.startBattle();
            return;
        }
        playing = (playing + 1) % 4;
        if (playing.equals(currStartingPlayer)) {
            biddingRound += 1;
        }
        if (biddingRound > 2) {
            this.startBattle();
            return;
        }
        players.get(playing).setBidding(true);
    }

    public void     setTrumpColor(Card.Color color) {
        this.trumpColor = color;
    }

    public void     setTurnColor(Card.Color color) {
        this.turnColor = color;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    private List<Card>  createDeck() {
        Player          tmpPlayer = new Player("tmp");
        CardFactory     factory = new CardFactory();
        List<Card>      deck = new ArrayList<Card>();
        Card.Color      colors[] = { Card.Color.CLUBS, Card.Color.DIAMONDS,
                Card.Color.SPADES, Card.Color.HEARTS };

        for (Card.Color color : colors) {
            deck.add(factory.createAce(color, tmpPlayer));
            deck.add(factory.createKing(color, tmpPlayer));
            deck.add(factory.createQueen(color, tmpPlayer));
            deck.add(factory.createJack(color, tmpPlayer));
            deck.add(factory.createTen(color, tmpPlayer));
            deck.add(factory.createNine(color, tmpPlayer));
            deck.add(factory.createEight(color, tmpPlayer));
            deck.add(factory.createSeven(color, tmpPlayer));
        }
        return deck;
    }

    public void         distribute() {
        Random          rand = new Random();
        List<Card>      deck = this.createDeck();
        Integer         cardsPerRound[] = { 3, 2, 3 };

        for (Integer nbr : cardsPerRound) {
            for (Player player : this.players) {
                for (int k = 0; k < nbr; ++k) {
                    Card card = deck.get(rand.nextInt(deck.size()));

                    card.setPlayer(player);
                    player.addCard(card);
                    deck.remove(card);
                }
            }
        }
    }

    public void     startGame() {
        for (Player player : this.players) {
            player.setInGame(true);
            player.setBidding(false);
            player.setPlaying(false);
        }
        this.startRound();
    }

    public void     startRound() {
        for (Player player : players) {
            player.resetHand();
            player.setBidding(false);
            player.setPlaying(false);
        }
        this.distribute();
        roundScore[0] = 0;
        roundScore[1] = 0;
        players.get(startingPlayer).setBidding(true);
        currStartingPlayer = startingPlayer;
        playing = startingPlayer;
        startingPlayer = (startingPlayer + 1) % 4;
        biddingRound = 0;
        highBid = null;
    }

    public Card     compareCards(Card c1, Card c2) {
        Card.Color  colors[] = { c1.getColor(), c2.getColor() };
        Card.Type   types[] = { c1.getType(), c2.getType() };
        Integer     defPts[] = { c1.getDefaultPts(), c2.getDefaultPts()};
        Integer     trpPts[] = { c1.getTrumpPts(), c2.getTrumpPts() };

        if (colors[0] == this.trumpColor) {
            if (colors[1] != this.trumpColor)
                return c1;
            if (trpPts[0].equals(trpPts[1])) {
                return types[0] == Card.Type.EIGHT ? c1 : c2;
            } else {
                return trpPts[0] > trpPts[1] ? c1 : c2;
            }
        }
        if (colors[1] == this.trumpColor)
            return c2;

        if (colors[0] == this.turnColor) {
            if (colors[1] != this.turnColor)
                return c1;
            if (defPts[0].equals(defPts[1])) {
                if (types[0] == Card.Type.NINE || types[1] == Card.Type.NINE)
                    return types[0] == Card.Type.NINE ? c1 : c2;
                return types[0] == Card.Type.EIGHT ? c1 : c2;
            } else {
                return defPts[0] > defPts[1] ? c1 : c2;
            }
        }

        if (colors[1] == this.turnColor)
            return c2;
        return c1;
    }

    public Integer  getScoreTeam(Integer team) {
        return scoreTeam[team - 1];
    }

    public Integer  getWinner() {
        return winner;
    }

    public Player   getPlayerTurn() {
        return winner == 0 ? this.players.get(this.playing) : null;
    }

    public ArrayList<Card> getBoard() {
        return board;
    }

    public Bid getHighBid() {
        return highBid;
    }

    public void setHighBid(Bid bid) {
        this.highBid = bid;
    }

    public Card.Color getTurnColor() {
        return turnColor;
    }

    public void setScoreTeam(Integer team, Integer score) {
        this.scoreTeam[team - 1] = score;
    }

    public Integer getRoundScore(Integer team) {
        return roundScore[team - 1];
    }
}

package com.jcoinche.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.esotericsoftware.kryonet.Client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoincheServerTest {

    @Test
    public void     testServerReceiveRegister() throws IOException {
        CoincheServer server = new CoincheServer();

        final Client client = new Client();
        client.start();
        Network.register(client);
        client.addListener(new Listener() {
            public void connected (Connection connection) {
                Network.Data register = new Network.Data();
                register.code = 0;
                register.body = "Test";
                client.sendTCP(register);
            }

            public void received (Connection c, Object o) {
                ArrayList<CoincheGame> games = ((Network.CoincheGames) o).games;
                assertEquals(games.size(), 0);
            }
        });
        client.connect(5000, "127.0.0.1", Network.port);
        try {
            Thread.sleep(500);
            client.stop();
            server.Stop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void     testCardColor() {
        Player      player = new Player("test");
        Card        c = new Card(Card.Color.CLUBS, Card.Type.ACE,
                19, 8, player);

        Card.Color  expected = Card.Color.CLUBS;
        Card.Color  actual = c.getColor();
        assertEquals(expected, actual);
    }

    @Test
    public void     testCardType() {
        Player      player = new Player("test");
        Card        c = new Card(Card.Color.CLUBS, Card.Type.ACE,
                19, 8, player);

        Card.Type  expected = Card.Type.ACE;
        Card.Type  actual = c.getType();
        assertEquals(expected, actual);
    }

    @Test
    public void     testCardDefaultPts() {
        Player      player = new Player("test");
        Card        c = new Card(Card.Color.CLUBS, Card.Type.ACE,
                19, 8, player);

        Integer     expected = 19;
        Integer     actual = c.getDefaultPts();
        assertEquals(expected, actual);
    }

    @Test
    public void     testCardTrumpPts() {
        Player      player = new Player("test");
        Card        c = new Card(Card.Color.CLUBS, Card.Type.ACE,
                19, 8, player);

        Integer     expected = 8;
        Integer     actual = c.getTrumpPts();
        assertEquals(expected, actual);
    }

    @Test
    public void     testCardPlayer() {
        Player      player = new Player("test");
        Card        c = new Card(Card.Color.CLUBS, Card.Type.ACE,
                19, 8, player);

        String      expected = "test";
        String      actual = c.getPlayer();
        assertEquals(expected, actual);
    }

    @Test
    public void     testCompareCards() {
        CardFactory factory = new CardFactory();
        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>());
        Player      player = new Player("test");
        Card        expected;
        Card        actual;
        Card        c1;
        Card        c2;

        ref.setTrumpColor(Card.Color.CLUBS);
        ref.setTurnColor(Card.Color.DIAMONDS);

        c1 = factory.createSeven(Card.Color.CLUBS, player);
        c2 = factory.createAce(Card.Color.HEARTS, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        ref.setTrumpColor(Card.Color.SPADES);
        ref.setTurnColor(Card.Color.CLUBS);

        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c2 = factory.createEight(Card.Color.CLUBS, player);

        expected = c2;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        ref.setTrumpColor(Card.Color.CLUBS);

        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c2 = factory.createSeven(Card.Color.CLUBS, player);
        c1 = factory.createEight(Card.Color.CLUBS, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c2 = factory.createEight(Card.Color.CLUBS, player);
        c1 = factory.createNine(Card.Color.CLUBS, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c2 = factory.createJack(Card.Color.CLUBS, player);

        expected = c2;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        ref.setTrumpColor(Card.Color.CLUBS);
        ref.setTurnColor(Card.Color.DIAMONDS);

        c1 = factory.createSeven(Card.Color.DIAMONDS, player);
        c2 = factory.createAce(Card.Color.SPADES, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c1 = factory.createAce(Card.Color.SPADES, player);
        c2 = factory.createSeven(Card.Color.DIAMONDS, player);

        expected = c2;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c2 = factory.createJack(Card.Color.CLUBS, player);

        expected = c2;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c2 = factory.createJack(Card.Color.SPADES, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c1 = factory.createJack(Card.Color.DIAMONDS, player);
        c2 = factory.createSeven(Card.Color.DIAMONDS, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c1 = factory.createKing(Card.Color.DIAMONDS, player);
        c2 = factory.createAce(Card.Color.DIAMONDS, player);

        expected = c2;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c1 = factory.createNine(Card.Color.DIAMONDS, player);
        c2 = factory.createEight(Card.Color.DIAMONDS, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c1 = factory.createSeven(Card.Color.DIAMONDS, player);
        c2 = factory.createNine(Card.Color.DIAMONDS, player);

        expected = c2;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);

        c1 = factory.createEight(Card.Color.DIAMONDS, player);
        c2 = factory.createSeven(Card.Color.DIAMONDS, player);

        expected = c1;
        actual = ref.compareCards(c1, c2);
        assertEquals(expected, actual);
    }

    @Test
    public void     testDistribution() {
        Player          players[] = { new Player("test1"),
                new Player("test2"),
                new Player("test3"),
                new Player("test4") };
        ArrayList<Player>   playersForRef = new ArrayList<Player>();

        playersForRef.add(players[0]);
        playersForRef.add(players[1]);
        playersForRef.add(players[2]);
        playersForRef.add(players[3]);

        CoincheReferee  ref = new CoincheReferee(playersForRef);

        ref.distribute();

        Integer expected = 8;
        List<Card>      playersDeck = new ArrayList<Card>();
        for (Player player : players) {
            Integer actual = player.getHand().size();

            assertEquals(expected, actual);
            for (Card card : player.getHand()) {
                for (Card pCard : playersDeck) {
                    assertNotEquals(pCard, card);
                    playersDeck.add(card);
                }
            }
        }
    }

    @Test
    public void     testColorEnum() {
        Card.Color  expected = Card.Color.SPADES;
        Card.Color  actual = Card.Color.valueOf("SPADES");
        assertEquals(expected, actual);
    }

    @Test
    public void     testTypeEnum() {
        Card.Type  expected = Card.Type.ACE;
        Card.Type  actual = Card.Type.valueOf("ACE");
        assertEquals(expected, actual);
    }

    @Test
    public void     testBidColor() {
        Player      player = new Player("test");
        Bid         bid = new Bid(Card.Color.ALL, player, 300);

        Card.Color  expectedColor = Card.Color.ALL;
        Card.Color  actualColor = bid.getColor();
        assertEquals(expectedColor, actualColor);
    }

    @Test
    public void     testBidPlayer() {
        Player      player = new Player("test");
        Bid         bid = new Bid(Card.Color.ALL, player, 300);

        Player      expectedPlayer = player;
        Player      actualPlayer = bid.getPlayer();
        assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    public void     testBidScore() {
        Player      player = new Player("test");
        Bid         bid = new Bid(Card.Color.ALL, player, 300);

        Integer     expectedScore = 300;
        Integer     actualScore = bid.getScore();
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void     testPlayerNickname() {
        Player      player = new Player("test");

        String      expected = "test";
        String      actual = player.getNickname();
        assertEquals(expected, actual);
    }

    @Test
    public void     testPlayerIsInGame() {
        Player      player = new Player("test");

        Boolean     expected = false;
        Boolean     actual = player.isInGame();
        assertEquals(expected, actual);

        player.setInGame(true);

        expected = true;
        actual = player.isInGame();
        assertEquals(expected, actual);
    }

    @Test
    public void     testPlayerIsPlaying() {
        Player      player = new Player("test");

        Boolean     expected = false;
        Boolean     actual = player.isPlaying();
        assertEquals(expected, actual);

        player.setPlaying(true);

        expected = true;
        actual = player.isPlaying();
        assertEquals(expected, actual);
    }

    @Test
    public void     testPlayerIsBidding() {
        Player      player = new Player();

        player.setNickname("test");
        Boolean     expected = false;
        Boolean     actual = player.isBidding();
        assertEquals(expected, actual);

        player.setBidding(true);

        expected = true;
        actual = player.isBidding();
        assertEquals(expected, actual);
    }

    @Test
    public void     testPlayerResetHand() {
        Player      player = new Player("test");
        Card        card = new Card(Card.Color.NONE, Card.Type.ACE, 0, 0, player);

        player.addCard(card);
        player.addCard(card);
        player.addCard(card);

        Integer     expected = 3;
        Integer     actual = player.getHand().size();
        assertEquals(expected, actual);

        player.resetHand();

        expected = 0;
        actual = player.getHand().size();
        assertEquals(expected, actual);
    }

    @Test
    public void     testRefereeGetPlayers() {
        Player              initPlayers[] = { new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),  };
        ArrayList<Player>   players = new ArrayList<Player>(Arrays.asList(initPlayers));
        CoincheReferee      ref = new CoincheReferee(players);

        ArrayList<Player>   resPlayers = ref.getPlayers();

        for (int i = 0; i < resPlayers.size(); ++i) {
            Player      expected = initPlayers[i];
            Player      actual = resPlayers.get(i);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void     testRefereeStartGame() {
        Player initPlayers[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};
        ArrayList<Player> players = new ArrayList<Player>(Arrays.asList(initPlayers));
        CoincheReferee ref = new CoincheReferee(players);

        ref.startGame();

        for (Player player : ref.getPlayers()) {
            Boolean     expected = true;
            Boolean     actual = player.isInGame();
            assertEquals(expected, actual);
        }
    }

    @Test
    public void     testIsValidPlay() {
        CardFactory factory = new CardFactory();
        Player      initPlayers[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        ArrayList<Player>   players = new ArrayList<Player>(Arrays.asList(initPlayers));
        CoincheReferee  ref = new CoincheReferee(players);
        Boolean         expected;
        Boolean         actual;
        Card            hearts = factory.createAce(Card.Color.HEARTS, initPlayers[0]);
        Card            clubs = factory.createAce(Card.Color.CLUBS, initPlayers[0]);

        initPlayers[0].addCard(hearts);
        initPlayers[0].addCard(clubs);

        ref.setTurnColor(Card.Color.NONE);
        ref.setTrumpColor(Card.Color.NONE);

        expected = true;
        actual = ref.isValidPlay(hearts);
        assertEquals(expected, actual);

        ref.setTurnColor(Card.Color.HEARTS);

        actual = ref.isValidPlay(hearts);
        assertEquals(expected, actual);

        expected = false;
        actual = ref.isValidPlay(clubs);
        assertEquals(expected, actual);

        ref.setTrumpColor(Card.Color.CLUBS);
        ref.setTurnColor(Card.Color.DIAMONDS);

        expected = true;
        actual = ref.isValidPlay(clubs);
        assertEquals(expected, actual);

        expected = false;
        actual = ref.isValidPlay(hearts);
        assertEquals(expected, actual);

        ref.setTurnColor(Card.Color.CLUBS);

        actual = ref.isValidPlay(hearts);
        assertEquals(expected, actual);

        ref.setTrumpColor(Card.Color.SPADES);

        actual = ref.isValidPlay(hearts);
        assertEquals(expected, actual);

        ref.setTurnColor(Card.Color.DIAMONDS);

        expected = true;
        actual = ref.isValidPlay(hearts);
        assertEquals(expected, actual);
    }

    @Test
    public void     testRefereeNewBattle() {
        CardFactory factory = new CardFactory();
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));

        ref.startBattle();
        ref.newCard(null);

        players[0].addCard(factory.createAce(Card.Color.HEARTS, players[0]));
        players[1].addCard(factory.createJack(Card.Color.SPADES, players[1]));
        players[2].addCard(factory.createJack(Card.Color.DIAMONDS, players[2]));
        players[3].addCard(factory.createJack(Card.Color.CLUBS, players[3]));

        ref.newCard(factory.createJack(Card.Color.HEARTS, players[1]));

        ref.setTurnColor(Card.Color.DIAMONDS);
        ref.setTrumpColor(Card.Color.HEARTS);

        ref.newCard(factory.createJack(Card.Color.SPADES, players[0]));

        ref.newCard(factory.createJack(Card.Color.HEARTS, players[0]));

        assertEquals(players[1], ref.getPlayerTurn());

        ref.newCard(factory.createKing(Card.Color.SPADES, players[1]));
        ref.newCard(factory.createKing(Card.Color.DIAMONDS, players[2]));

        ArrayList<Card> board = ref.getBoard();

        assertEquals(3, board.size());
        assertEquals(Card.Color.SPADES, board.get(1).getColor());
        assertEquals(Card.Type.JACK, board.get(0).getType());

        ref.newCard(factory.createSeven(Card.Color.SPADES, players[3]));

        assertEquals(Card.Color.NONE, ref.getTurnColor());
        Integer expectedScore = 22;
        assertEquals(expectedScore, ref.getRoundScore(1));
        ref.setTrumpColor(Card.Color.SPADES);

        ref.newCard(factory.createSeven(Card.Color.HEARTS, players[0]));
        ref.newCard(factory.createSeven(Card.Color.SPADES, players[1]));
        ref.newCard(factory.createEight(Card.Color.HEARTS, players[2]));
        ref.newCard(factory.createNine(Card.Color.HEARTS, players[3]));

        ref.setTrumpColor(Card.Color.NONE);

        ref.newCard(players[1].getHand().get(0));
        assertEquals(true, players[1].getHand().isEmpty());

        ref.newCard(players[2].getHand().get(0));
        assertEquals(true, players[2].getHand().isEmpty());

        ref.newCard(players[3].getHand().get(0));
        assertEquals(true, players[3].getHand().isEmpty());

        ref.setHighBid(new Bid(Card.Color.NONE, players[0], 20));

        ref.newCard(players[0].getHand().get(0));
        ref.endGame(1);
    }

    @Test
    public void     testRefereeEndBattle() {
        CardFactory factory = new CardFactory();
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));
        ref.startBattle();
        ref.newCard(null);

        players[0].addCard(factory.createAce(Card.Color.HEARTS, players[0]));
        players[1].addCard(factory.createJack(Card.Color.SPADES, players[1]));
        players[2].addCard(factory.createJack(Card.Color.DIAMONDS, players[2]));
        players[3].addCard(factory.createJack(Card.Color.CLUBS, players[3]));

        ref.setHighBid(new Bid(Card.Color.NONE, players[1], 300));
        ref.startBattle();

        ref.newCard(players[0].getHand().get(0));
        ref.newCard(players[1].getHand().get(0));
        ref.newCard(players[2].getHand().get(0));
        ref.newCard(players[3].getHand().get(0));

        Integer     expected = 462;
        Integer     actual = ref.getScoreTeam(1);
        assertEquals(expected, actual);

        expected = 0;
        actual = ref.getScoreTeam(2);
        assertEquals(expected, actual);
    }

    @Test
    public void     testRefereeTeam2Win() {
        CardFactory factory = new CardFactory();
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));
        ref.startBattle();
        ref.newCard(null);

        players[0].addCard(factory.createAce(Card.Color.HEARTS, players[0]));
        players[1].addCard(factory.createJack(Card.Color.SPADES, players[1]));
        players[2].addCard(factory.createJack(Card.Color.DIAMONDS, players[2]));
        players[3].addCard(factory.createJack(Card.Color.CLUBS, players[3]));

        ref.setHighBid(new Bid(Card.Color.NONE, players[1], 300));
        ref.startBattle();

        ref.newCard(players[0].getHand().get(0));
        ref.newCard(players[1].getHand().get(0));
        ref.newCard(players[2].getHand().get(0));
        ref.setScoreTeam(2, 701);
        ref.newCard(players[3].getHand().get(0));
    }

    @Test
    public void     testRefereeTeam1Win() {
        CardFactory factory = new CardFactory();
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));
        ref.startBattle();
        ref.newCard(null);

        players[0].addCard(factory.createAce(Card.Color.HEARTS, players[0]));
        players[1].addCard(factory.createJack(Card.Color.SPADES, players[1]));
        players[2].addCard(factory.createJack(Card.Color.DIAMONDS, players[2]));
        players[3].addCard(factory.createJack(Card.Color.CLUBS, players[3]));

        ref.setHighBid(new Bid(Card.Color.NONE, players[0], 300));
        ref.startBattle();

        ref.newCard(players[0].getHand().get(0));
        ref.newCard(players[1].getHand().get(0));
        ref.newCard(players[2].getHand().get(0));
        ref.setScoreTeam(1, 701);
        ref.newCard(players[3].getHand().get(0));

        Integer     expected = 1;
        Integer     actual = ref.getWinner();
        assertEquals(expected, actual);
        assertEquals(null, ref.getPlayerTurn());
    }

    @Test
    public void     testRefereeTeam1WinBothOver700() {
        CoincheReferee  nullRef = new CoincheReferee();
        Bid             nullBid = new Bid();
        Card            nullCard = new Card();
        CardFactory factory = new CardFactory();
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));
        ref.startBattle();
        ref.newCard(null);

        players[0].addCard(factory.createAce(Card.Color.HEARTS, players[0]));
        players[1].addCard(factory.createJack(Card.Color.SPADES, players[1]));
        players[2].addCard(factory.createJack(Card.Color.DIAMONDS, players[2]));
        players[3].addCard(factory.createJack(Card.Color.CLUBS, players[3]));

        ref.setHighBid(new Bid(Card.Color.NONE, players[0], 300));
        ref.startBattle();

        ref.newCard(players[0].getHand().get(0));
        ref.newCard(players[1].getHand().get(0));
        ref.newCard(players[2].getHand().get(0));
        ref.setScoreTeam(1, 761);
        ref.setScoreTeam(2, 298);
        ref.newCard(players[3].getHand().get(0));

        Integer     expected = 1;
        Integer     actual = ref.getWinner();
        assertEquals(expected, actual);
    }

    @Test
    public void     testRefereeTeam2WinBothOver700() {
        CardFactory factory = new CardFactory();
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));
        ref.startBattle();
        ref.newCard(null);

        players[0].addCard(factory.createAce(Card.Color.HEARTS, players[0]));
        players[1].addCard(factory.createJack(Card.Color.SPADES, players[1]));
        players[2].addCard(factory.createJack(Card.Color.DIAMONDS, players[2]));
        players[3].addCard(factory.createJack(Card.Color.CLUBS, players[3]));

        ref.setHighBid(new Bid(Card.Color.NONE, players[0], 300));
        ref.startBattle();

        ref.newCard(players[0].getHand().get(0));
        ref.newCard(players[1].getHand().get(0));
        ref.newCard(players[2].getHand().get(0));
        ref.setScoreTeam(1, 761);
        ref.setScoreTeam(2, 300);
        ref.newCard(players[3].getHand().get(0));

        Integer     expected = 2;
        Integer     actual = ref.getWinner();
        assertEquals(expected, actual);
    }

    @Test
    public void     testRefereeTie() {
        CardFactory factory = new CardFactory();
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};

        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));
        ref.startBattle();
        ref.newCard(null);

        players[0].addCard(factory.createAce(Card.Color.HEARTS, players[0]));
        players[1].addCard(factory.createJack(Card.Color.SPADES, players[1]));
        players[2].addCard(factory.createJack(Card.Color.DIAMONDS, players[2]));
        players[3].addCard(factory.createJack(Card.Color.CLUBS, players[3]));

        ref.setHighBid(new Bid(Card.Color.NONE, players[0], 300));
        ref.startBattle();

        ref.newCard(players[0].getHand().get(0));
        ref.newCard(players[1].getHand().get(0));
        ref.newCard(players[2].getHand().get(0));
        ref.setScoreTeam(1, 761);
        ref.setScoreTeam(2, 299);
        ref.newCard(players[3].getHand().get(0));

        Integer     expected = 0;
        Integer     actual = ref.getWinner();
        assertEquals(expected, actual);
    }

    @Test
    public void     testRefereeBid() {
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};
        Integer     expected;
        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));

        ref.startGame();

        ref.newBid(null);
        assertEquals(players[0], ref.getPlayerTurn());
        ref.newBid(new Bid(Card.Color.NONE, players[1], 20));
        assertEquals(players[0], ref.getPlayerTurn());
        ref.newBid(new Bid(Card.Color.NONE, players[0], 170));
        assertEquals(players[0], ref.getPlayerTurn());
        expected = 80;
        ref.newBid(new Bid(Card.Color.SPADES, players[0], 80));
        assertEquals(players[1], ref.getPlayerTurn());
        assertEquals(expected, ref.getHighBid().getScore());
        ref.newBid(new Bid(Card.Color.SPADES, players[1], 79));
        assertEquals(players[1], ref.getPlayerTurn());
        ref.newBid(new Bid(Card.Color.SPADES, players[1], 90));
        assertEquals(players[2], ref.getPlayerTurn());
        ref.newBid(new Bid(Card.Color.SPADES, players[2], 0));
        assertEquals(players[3], ref.getPlayerTurn());
        ref.newBid(new Bid(Card.Color.SPADES, players[3], 0));
        assertEquals(players[0], ref.getPlayerTurn());
        ref.newBid(new Bid(Card.Color.SPADES, players[0], 100));
        ref.newBid(new Bid(Card.Color.SPADES, players[1], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[2], 110));
        ref.newBid(new Bid(Card.Color.SPADES, players[3], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[0], 120));
        ref.newBid(new Bid(Card.Color.SPADES, players[1], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[2], 130));
        ref.newBid(new Bid(Card.Color.SPADES, players[3], 0));
    }

    @Test
    public void     testRefereeBidOf160() {
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};
        Integer     expected;
        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));
        Bid         bid = new Bid(Card.Color.NONE, players[0], 160);
        ref.startGame();

        ref.newBid(bid);
        assertEquals(bid, ref.getHighBid());
        assertEquals(false, players[0].isBidding());
        assertEquals(true, players[0].isPlaying());
    }

    @Test
    public void     testRefereeBidFullPass() {
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};
        Integer     expected;
        Integer     actual;
        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));

        ref.startGame();

        ref.newBid(new Bid(Card.Color.SPADES, players[0], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[1], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[2], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[3], 0));
        expected = 8;
        actual = players[0].getHand().size();
        assertEquals(expected, actual);
    }

    @Test
    public void     testRefereeBidFullPassAfterABid() {
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};
        Integer     expected;
        Integer     actual;
        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));

        ref.startGame();

        ref.newBid(new Bid(Card.Color.SPADES, players[0], 90));
        expected = 90;
        actual = ref.getHighBid().getScore();
        assertEquals(expected, actual);
        ref.newBid(new Bid(Card.Color.SPADES, players[1], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[2], 0));
        ref.newBid(new Bid(Card.Color.SPADES, players[3], 0));
        assertEquals(true, players[0].isPlaying());
    }

    @Test
    public void     testBidIncreaseScore() {
        Bid         bid = new Bid(Card.Color.NONE, null, 150);
        Integer     expected;

        expected = 150;
        assertEquals(expected, bid.getScore());
        bid.increaseScore();
        expected = 160;
        assertEquals(expected, bid.getScore());
        bid.increaseScore();
        assertEquals(expected, bid.getScore());
    }

    @Test
    public void     testBidDecreaseScore() {
        Bid         bid = new Bid(Card.Color.NONE, null, 100);
        Integer     expected;

        expected = 100;
        assertEquals(expected, bid.getScore());
        bid.decreaseScore();
        expected = 90;
        assertEquals(expected, bid.getScore());
        bid.setScore(80);
        bid.decreaseScore();
        expected = 80;
        assertEquals(expected, bid.getScore());
    }

    @Test
    public void     testBidSetPlayer() {
        Bid         bid = new Bid(Card.Color.NONE, null, 100);
        Player      player = new Player("test");

        assertEquals(null, bid.getPlayer());
        bid.setPlayer(player);
        assertEquals(player, bid.getPlayer());
    }

    @Test
    public void     testBidChangeColor() {
        Bid         bid = new Bid(Card.Color.ALL, null, 0);

        assertEquals(Card.Color.ALL, bid.getColor());
        bid.changeColor();
        assertEquals(Card.Color.NONE, bid.getColor());
        bid.changeColor();
        assertEquals(Card.Color.SPADES, bid.getColor());
    }

    @Test
    public void     testRefereeGetFromNicknameBadName() {
        Player      players[] = {new Player("test1"), new Player("test2"),
                new Player("test3"), new Player("test4"),};
        CoincheReferee  ref = new CoincheReferee(new ArrayList<Player>(Arrays.asList(players)));

        assertEquals(null, ref.getPlayerFromNickname("badName"));
    }

    @Test
    public void     testPlayerRemoveCard() {
        Player      player = new Player("test");
        CardFactory factory = new CardFactory();
        Card        card = factory.createNine(Card.Color.SPADES, player);

        player.addCard(card);
        assertEquals(1, player.getHand().size());
        player.removeCard(card);
        assertEquals(true, player.getHand().isEmpty());
    }
}

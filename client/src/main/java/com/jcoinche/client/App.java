package com.jcoinche.client;

import com.jcoinche.server.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class App {
    Client client = new Client();
    String me;
    ArrayList<CoincheGame> games = null;
    CoincheGame myGame = null;
    JFrame frame;
    int i;
    Bid newBid = new Bid(Card.Color.SPADES, null, -1);

    public App() throws IOException {
        client.start();
        Network.register(client);
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof Network.CoincheGames) {
                    games = ((Network.CoincheGames) object).games;
                    displayMenu();
                }
                if (object instanceof CoincheGame) {
                    myGame = (CoincheGame)object;
                    if (myGame.referee != null && myGame.referee.getWinner() == 0) {
                        try {
                            if (myGame.referee.getPlayerTurn().isBidding()) {
                                displayBet(new Bid(Card.Color.SPADES, null, -1));
                            } else {
                                displayGame();
                            }
                        } catch (Exception exception) {
                            System.out.println(exception.getMessage());
                        }
                    } else {
                        if (myGame.referee != null && myGame.referee.getWinner() != 0) {
                            displayEndGameScreen();
                        } else {
                            displayWaitingScreen();
                        }
                    }
                }
            }
        });
        client.connect(5000, "127.0.0.1", Network.port);

        frame = new JFrame("JCoinche");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(1100, 900));
        frame.getContentPane().setBackground(new Color(255,255,255));
        frame.setVisible(true);

        displayConnect();
    }

    private void displayEndGameScreen() {
        frame.getContentPane().removeAll();

        JButton      button = null;
        for(Player player : myGame.referee.getPlayers()) {
            if (me.equals(player.getNickname())) {
                if (myGame.referee.getPlayers().indexOf(player) % 2 == 0) {
                    if (myGame.referee.getWinner() == 1) {
                        button = new JButton("Victory");
                    } else {
                        button = new JButton("Defeat");
                    }
                } else {
                    if (myGame.referee.getWinner() == 2) {
                        button = new JButton("Victory");
                    } else {
                        button = new JButton("Defeat");
                    }
                }
            }
        }
        if (button == null)
            button = new JButton("");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Network.Data data = new Network.Data();
                data.code = 3;
                data.body = myGame.name;
                client.sendTCP(data);
            }
        });
        frame.getContentPane().add(button);

        frame.pack();
    }

    private void displayWaitingScreen() {
        frame.getContentPane().removeAll();

        JLabel  label = new JLabel("Waiting for players");
        frame.getContentPane().add(label);
        frame.pack();
    }

    private void displayConnect () {
        frame.getContentPane().removeAll();

        frame.getContentPane().setLayout(new GridLayout(3, 0));
        JLabel label = new JLabel("Enter your name:");
        frame.getContentPane().add(label);
        final JTextField textField = new JTextField();
        frame.getContentPane().add(textField);
        JButton button = new JButton("Connect");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().isEmpty()) {
                    Network.Data register = new Network.Data();
                    register.code = 0;
                    register.body = textField.getText();
                    client.sendTCP(register);
                    me = textField.getText();
                }
            }
        });
        frame.getContentPane().add(button);
        frame.pack();
    }

    private void displayMenu () {
        frame.getContentPane().removeAll();

        frame.getContentPane().setLayout(new GridLayout(0, 1));
        for (final CoincheGame game : games) {
            if (game.referee == null) {
                JButton button = new JButton(game.name + " (" + game.players.size() +  " / 4)");
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Network.Data data = new Network.Data();
                        data.code = 2;
                        data.body = game.name;
                        client.sendTCP(data);
                    }
                });
                frame.getContentPane().add(button);
            }
        }
        JLabel label = new JLabel("New game name:");
        frame.getContentPane().add(label);
        final JTextField textField = new JTextField();
        frame.getContentPane().add(textField);
        JButton button = new JButton("Create");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().isEmpty()) {
                    Network.Data data = new Network.Data();
                    data.code = 1;
                    data.body = textField.getText();
                    client.sendTCP(data);
                }
            }
        });
        frame.getContentPane().add(button);
        frame.pack();
    }

    private void displayBet(final Bid newBid) throws IOException {
        frame.getContentPane().removeAll();

        frame.getContentPane().setLayout(new GridLayout(0, 10));
        for (final Player player : myGame.referee.getPlayers()) {
            frame.getContentPane().add(new JLabel(player.getNickname()));
            if (player.getNickname().equals(myGame.referee.getPlayerTurn().getNickname())) {
                frame.getContentPane().add(new JLabel("Your turn"));
            } else {
                frame.getContentPane().add(new JLabel(""));
            }
            i = 0;
            while (i < 8) {
                if (i < player.getHand().size()) {
                    if (me.equals(player.getNickname())) {
                        JButton button;
                        try {
                            BufferedImage buttonIcon = ImageIO.read(new File("target/classes/images/" + player.getHand().get(i).getColor().name() + "_" +  player.getHand().get(i).getType().name() + ".png"));
                            button = new JButton(new ImageIcon(buttonIcon));
                        } catch (IOException exception) {
                            button = new JButton(player.getHand().get(i).getColor().name() + " " +  player.getHand().get(i).getType().name());
                        }
                        frame.getContentPane().add(button);
                    } else {
                        frame.getContentPane().add(new JButton(""));
                    }
                } else {
                    JButton button = new JButton("");
                    button.setEnabled(false);
                    frame.getContentPane().add(button);
                }
                i++;
            }

        }

        if (me.equals(myGame.referee.getPlayerTurn().getNickname())) {
            if (newBid.getPlayer() == null) {
                newBid.setPlayer(getSelfPlayer());
                if (myGame.referee.getHighBid() != null) {
                    newBid.setScore(myGame.referee.getHighBid().getScore() + 10);
                } else {
                    newBid.setScore(80);
                }
            }
            JButton     decrease = new JButton("Decrease score");
            if (newBid.getScore() - 10 < 80 || (myGame.referee.getHighBid() != null &&
                    newBid.getScore() - 10 <= myGame.referee.getHighBid().getScore()))
                decrease.setEnabled(false);
            decrease.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newBid.decreaseScore();
                    try {
                        displayBet(newBid);
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                    }
                }
            });
            frame.getContentPane().add(decrease);

            JButton     changeColor = new JButton("Change color");
            changeColor.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newBid.changeColor();
                    try {
                        displayBet(newBid);
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                    }
                }
            });
            frame.getContentPane().add(changeColor);

            JButton     increase = new JButton("Increase score");
            if (newBid.getScore() + 10 > 160)
                increase.setEnabled(false);
            increase.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newBid.increaseScore();
                    try {
                        displayBet(newBid);
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                    }
                }
            });
            frame.getContentPane().add(increase);

            JButton     pass = new JButton("Pass");
            pass.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    client.sendTCP(new Bid(Card.Color.NONE, getSelfPlayer(), 0));
                }
            });
            frame.getContentPane().add(pass);


            JButton     send = new JButton("Send");
            send.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    client.sendTCP(newBid);
                }
            });
            frame.getContentPane().add(send);


            frame.getContentPane().add(new JLabel("       Your bid:"));
            JLabel myBidLabel= new JLabel();
            myBidLabel.setLayout(new BorderLayout());
            myBidLabel.add(BorderLayout.NORTH, new Label(newBid.getPlayer().getNickname()));
            myBidLabel.add(BorderLayout.CENTER, new Label(newBid.getScore().toString()));
            myBidLabel.add(BorderLayout.SOUTH, new Label(newBid.getColor().name()));
            frame.getContentPane().add(myBidLabel);
            frame.getContentPane().add(new JLabel(" "));
        } else {
            frame.getContentPane().add(new JLabel(""));
            frame.getContentPane().add(new JLabel(""));
            frame.getContentPane().add(new JLabel(""));
            frame.getContentPane().add(new JLabel(""));
            frame.getContentPane().add(new JLabel(""));
            frame.getContentPane().add(new JLabel(""));
            frame.getContentPane().add(new JLabel(""));
            frame.getContentPane().add(new JLabel(""));
        }

        frame.getContentPane().add(new JLabel("       High bid:"));
        JLabel highBidLabel = new JLabel();
        highBidLabel.setLayout(new BorderLayout());
        if (myGame.referee.getHighBid() != null) {
            highBidLabel.add(BorderLayout.NORTH, new Label(myGame.referee.getHighBid().getPlayer().getNickname()));
            highBidLabel.add(BorderLayout.CENTER, new Label(myGame.referee.getHighBid().getScore().toString()));
            highBidLabel.add(BorderLayout.SOUTH, new Label(myGame.referee.getHighBid().getColor().name()));
        }
        frame.getContentPane().add(highBidLabel);

        frame.pack();
    }

    private void displayGame () throws IOException {
        frame.getContentPane().removeAll();

        frame.getContentPane().setLayout(new GridLayout(0, 10));
        for (final Player player : myGame.referee.getPlayers()) {
            frame.getContentPane().add(new JLabel(player.getNickname()));
            if (player.getNickname().equals(myGame.referee.getPlayerTurn().getNickname())) {
                frame.getContentPane().add(new JLabel("Your turn"));
            } else {
                frame.getContentPane().add(new JLabel(""));
            }
            i = 0;
            while (i < 8) {
                if (i < player.getHand().size()) {
                    if (me.equals(player.getNickname())) {
                        JButton button;
                        try {
                            BufferedImage buttonIcon = ImageIO.read(new File("target/classes/images/" + player.getHand().get(i).getColor().name() + "_" +  player.getHand().get(i).getType().name() + ".png"));
                            button = new JButton(new ImageIcon(buttonIcon));
                        } catch (IOException exception) {
                            button = new JButton(player.getHand().get(i).getColor().name() + " " +  player.getHand().get(i).getType().name());
                        }
                        if (!myGame.referee.isValidPlay(player.getHand().get(i)))
                            button.setEnabled(false);
                        if (me.equals(myGame.referee.getPlayerTurn().getNickname())) {
                            button.addActionListener(new ActionListener() {
                                Card selectedCard = player.getHand().get(i);

                                public void actionPerformed(ActionEvent e) {
                                    if (me.equals(myGame.referee.getPlayerTurn().getNickname())) {
                                        client.sendTCP(selectedCard);
                                    }
                                }
                            });
                        }
                        frame.getContentPane().add(button);
                    } else {
                        frame.getContentPane().add(new JButton(""));
                    }
                } else {
                    JButton button = new JButton("");
                    button.setEnabled(false);
                    frame.getContentPane().add(button);
                }
                i++;
            }
        }
        JLabel teamScoreLabel = new JLabel();
        teamScoreLabel.setLayout(new BorderLayout());
        if (myGame.referee.getHighBid() != null) {
            teamScoreLabel.add(BorderLayout.NORTH, new Label("Total scores:"));
            teamScoreLabel.add(BorderLayout.CENTER, new Label("Team 1: " + myGame.referee.getScoreTeam(1)));
            teamScoreLabel.add(BorderLayout.SOUTH, new Label("Team 2: " + myGame.referee.getScoreTeam(2)));
        }
        frame.getContentPane().add(teamScoreLabel);

        JLabel roundScoreLabel = new JLabel();
        roundScoreLabel.setLayout(new BorderLayout());
        if (myGame.referee.getHighBid() != null) {
            roundScoreLabel.add(BorderLayout.NORTH, new Label("Round scores:"));
            roundScoreLabel.add(BorderLayout.CENTER, new Label("Team 1: " + myGame.referee.getRoundScore(1)));
            roundScoreLabel.add(BorderLayout.SOUTH, new Label("Team 2: " + myGame.referee.getRoundScore(2)));
        }
        frame.getContentPane().add(roundScoreLabel);

        frame.getContentPane().add(new JLabel(""));
        for (Card card : myGame.referee.getBoard()) {
            JButton currentCard = new JButton();
            currentCard.setLayout(new BorderLayout());
            currentCard.add(BorderLayout.NORTH, new JLabel(card.getPlayer()));
            try {
                BufferedImage buttonIcon = ImageIO.read(new File("target/classes/images/" + card.getColor().name() + "_" +  card.getType().name() + ".png"));
                currentCard.add(BorderLayout.CENTER, new JLabel(new ImageIcon(buttonIcon)));
            } catch (IOException exception) {
                currentCard.add(BorderLayout.CENTER, new JLabel(card.getColor().name() + " " +  card.getType().name()));
            }
            frame.getContentPane().add(currentCard);
        }

        for (int i = 0; i < 4 - myGame.referee.getBoard().size() + 1; ++i) {
            frame.getContentPane().add(new JLabel(""));
        }

        frame.getContentPane().add(new JLabel("       High bid:"));
        JLabel highBidLabel = new JLabel();
        highBidLabel.setLayout(new BorderLayout());
        if (myGame.referee.getHighBid() != null) {
            highBidLabel.add(BorderLayout.NORTH, new Label(myGame.referee.getHighBid().getPlayer().getNickname()));
            highBidLabel.add(BorderLayout.CENTER, new Label(myGame.referee.getHighBid().getScore().toString()));
            highBidLabel.add(BorderLayout.SOUTH, new Label(myGame.referee.getHighBid().getColor().name()));
        }
        frame.getContentPane().add(highBidLabel);

        frame.pack();
    }

    private Player getSelfPlayer() {
        for (Player player : myGame.referee.getPlayers()) {
            if (me.equals(player.getNickname())) {
                return player;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        new App();
    }
}

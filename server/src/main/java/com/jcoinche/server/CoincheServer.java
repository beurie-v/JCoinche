package com.jcoinche.server;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class CoincheServer {
    Server server;
    ArrayList<ServerClient> clients = new ArrayList();
    ArrayList<CoincheGame> games = new ArrayList();

    public CoincheServer () throws IOException {
        server = new Server() {
            protected Connection newConnection() {
                ServerClient connection = new ServerClient();
                connection.player = new Player("");
                clients.add(connection);
                return connection;
            }
        };

        Network.register(server);

        server.addListener(new Listener() {
            public void received (Connection c, Object object) {
                ServerClient connection = (ServerClient) c;
                if (object instanceof Network.Data) {
                    int code = ((Network.Data) object).code;
                    Network.Data data = new Network.Data();
                    Network.CoincheGames dataGames = new Network.CoincheGames();
                    switch (code) {
                        case 0: //Register name
                            connection.player.setNickname(((Network.Data) object).body);
                            dataGames.games = games;
                            connection.sendTCP(dataGames);
                            break;
                        case 1: //Create game
                            games.add(new CoincheGame(((Network.Data) object).body, connection.player));
                            connection.sendTCP(connection.player.game);
                            break;
                        case 2: //Join game
                            for (CoincheGame game : games) {
                                if (game.name.equals(((Network.Data) object).body)) {
                                    connection.player.game = game;
                                    game.players.add(connection.player);
                                    if (game.players.size() == 4) {
                                        game.referee = new CoincheReferee(game.players);
                                        game.referee.startGame();
                                    }
                                    for (Player client : game.players) {
                                        getFromPlayer(client).sendTCP(game);
                                    }
                                    break;
                                }
                            }
                            break;
                        case 3: //Leave game
                            for (CoincheGame game : games) {
                                if (game.name.equals(((Network.Data) object).body)) {
                                    game.players.remove(connection);
                                    if (game.players.size() == 0) {
                                        games.remove(game);
                                    } else {
                                        for (Player client : game.players) {
                                            getFromPlayer(client).sendTCP(game);
                                        }
                                    }
                                    dataGames.games = games;
                                    connection.sendTCP(dataGames);
                                    break;
                                }
                            }
                            break;
                        default:
                            data.code = -1;
                            data.body = "Undefined code";
                            connection.sendTCP(data);
                            break;
                    }
                }
                if (object instanceof Bid) {
                    connection.player.game.referee.newBid((Bid) object);
                    for (Player client : connection.player.game.players) {
                        getFromPlayer(client).sendTCP(connection.player.game);
                    }
                }
                if (object instanceof Card) {
                    connection.player.game.referee.newCard((Card) object);
                    for (Player client : connection.player.game.players) {
                        getFromPlayer(client).sendTCP(connection.player.game);
                    }
                }
            }

            public void disconnected (Connection connection) {
                ServerClient client = (ServerClient) connection;
                if (client.player.game != null) {
                    client.player.game.players.remove(client.player);
                    if (client.player.game.players.size() == 0) {
                        games.remove(client.player.game);
                    } else {
                        if (client.player.game.referee != null && client.player.game.referee.getWinner() == 0) {
                            client.player.game.referee = null;
                        }
                        for (Player player : client.player.game.players) {
                            getFromPlayer(player).sendTCP(client.player.game);
                        }
                    }
                }
                clients.remove(client);
            }
        });
        server.bind(Network.port);
        server.start();
    }

    public void Stop () {
        server.stop();
    }

    private ServerClient getFromPlayer(Player player) {
        for (ServerClient client : clients) {
            if (client.player == player)
                return client;
        }
        return null;
    }
}

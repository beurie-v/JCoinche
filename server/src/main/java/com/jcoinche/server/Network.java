package com.jcoinche.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;
import java.util.List;

public class Network {
    static public final int port = 42424;

    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Data.class);
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(Integer[].class);
        kryo.register(Card.Color.class);
        kryo.register(Card.Type.class);
        kryo.register(Card.class);
        kryo.register(Bid.class);
        kryo.register(Player.class);
        kryo.register(CoincheReferee.class);
        kryo.register(CoincheGame.class);
        kryo.register(CoincheGames.class);
    }

    static public class CoincheGames {
        public ArrayList<CoincheGame> games;
    }

    static public class Data {
        public int code;
        public String body;
    }
}
package fr.rappyfr.sql;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoAccess {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> playersCollection;
    private final MongoCollection<Document> playersDataCollection;

    public MongoAccess(){
        this.mongoClient = MongoClients.create("mongodb+srv://rappyfr:mabichette59@spigotcluster.svh4vth.mongodb.net/?retryWrites=true&w=majority");
        this.mongoDatabase = mongoClient.getDatabase("mcranked");
        this.playersCollection = mongoDatabase.getCollection("players");
        this.playersDataCollection = mongoDatabase.getCollection("playersData");
    }

    public MongoCollection<Document> getPlayersCollection() {
        return playersCollection;
    }

    public MongoClient getMongoClient(){
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase(){
        return mongoDatabase;
    }

    public MongoCollection<Document> getPlayersDataCollection() {
        return playersDataCollection;
    }
}

package fr.rappyfr.manager;

import com.mongodb.client.MongoCollection;
import fr.rappyfr.MCRankedAPI;
import fr.rappyfr.model.Accounts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class MongoManager {

    private final MCRankedAPI plugin;
    private final MongoCollection<Document> playersCollection;

    public MongoManager(MCRankedAPI plugin){
        this.plugin = plugin;
        playersCollection = plugin.mongoAccess.getPlayersCollection();
    }

    public  void createPlayerDB(UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        if(p == null)return;
        String ip = p.getAddress().getAddress().getHostAddress();
        Document playerDoc = playersCollection.find(new Document("uuid", uuid.toString())).first();
        if(playerDoc != null){
            if(!playerDoc.getString("lastAddressIP").equals(ip)){
                // set current ip to the current and kick player if not the same
                Bson currentIpValue = new Document("currentAddressIP", ip);
                Bson updateValue = new Document("$set", currentIpValue);
                playersCollection.updateOne(playerDoc, updateValue);
                p.kickPlayer("§cVotre address ip n'est pas la meme que la derniere connection.");
                return;
            }
            bddToAccounts(uuid);
            return;
        }
        //create player first connection
        Document doc = new Document("uuid", uuid.toString())
                .append("username", p.getName())
                .append("currentAddressIP", ip)
                .append("lastAddressIP", ip)
                .append("playerKills", 0)
                .append("playerDeaths", 0)
                .append("gameWin", 0)
                .append("gameLoose", 0);

        playersCollection.insertOne(doc);

    }

    //get playerStats async
    public CompletableFuture<Integer> getPlayerStats(final UUID uuid, String stats) {

        // "Créer" le nouveau thread, exécute le code entre les {}, puis complète le future
        // en disant au code qui récupère le CF qu'on a trouvé la valeur
        return CompletableFuture.supplyAsync(() -> {
            Integer playerStats = null;
            Document playerDoc = playersCollection.find(new Document("uuid", uuid.toString())).first();
            if(stats.equalsIgnoreCase("playerKills")){
                playerStats = playerDoc.getInteger("playerKills");
            }
            if(stats.equalsIgnoreCase("playerDeaths")){
                playerStats = playerDoc.getInteger("playerDeaths");
            }
            if(stats.equalsIgnoreCase("gameWin")){
                playerStats = playerDoc.getInteger("gameWin");
            }
            if(stats.equalsIgnoreCase("gameLoose")){
                playerStats = playerDoc.getInteger("gameLoose");
            }
            return playerStats;
        });
    }

    public void bddToAccounts(UUID uuid){
        Accounts account = plugin.accountsManager.getAccountByUUID(uuid);
        if(account == null){
            plugin.accountsManager.getAccounts().add(new Accounts(uuid));
            bddToAccounts(uuid);
            return;
        }
        //stockage des values bdd to Player Account
        // "kills" représente le "playerRank" qu'on a retourné dans la fonction
        this.getPlayerStats(uuid, "playerKills") // Retournes un CompletableFuture
                .thenAcceptAsync(account::setPlayerKills)
                .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                    plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des kills.", err);
                    return null; // On oublie pas de retourner null parce que la méthode le demande
                });
        // "kills" représente le "playerRank" qu'on a retourné dans la fonction
        this.getPlayerStats(uuid, "playerDeaths") // Retournes un CompletableFuture
                .thenAcceptAsync(account::setPlayerDeaths)
                .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                    plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des morts.", err);
                    return null; // On oublie pas de retourner null parce que la méthode le demande
                });
        // "kills" représente le "playerRank" qu'on a retourné dans la fonction
        this.getPlayerStats(uuid, "gameWin") // Retournes un CompletableFuture
                .thenAcceptAsync(account::setGameWin)
                .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                    plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des gameWin.", err);
                    return null; // On oublie pas de retourner null parce que la méthode le demande
                });
        // "kills" représente le "playerRank" qu'on a retourné dans la fonction
        this.getPlayerStats(uuid, "gameLoose") // Retournes un CompletableFuture
                .thenAcceptAsync(account::setGameLoose)
                .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                    plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des gameLoose.", err);
                    return null; // On oublie pas de retourner null parce que la méthode le demande
                });
    }

    //getPlayerStats from Accounts async
    public CompletableFuture<Integer> getPlayerStats(final Accounts account, String stats) {

        // "Créer" le nouveau thread, exécute le code entre les {}, puis complète le future
        // en disant au code qui récupère le CF qu'on a trouvé la valeur
        return CompletableFuture.supplyAsync(() -> {
            Integer playerStats = null;

            if(stats.equalsIgnoreCase("playerKills")){
                playerStats = account.getPlayerKills();
            }
            if(stats.equalsIgnoreCase("playerDeaths")){
                playerStats = account.getPlayerDeaths();
            }
            if(stats.equalsIgnoreCase("gameWin")){
                playerStats = account.getGameWin();
            }
            if(stats.equalsIgnoreCase("gameLoose")){
                playerStats = account.getGameLoose();
            }
            return playerStats;
        });
    }

    //async update bdd from all accounts
    public void updateDBFromAccounts(){
        for(Accounts accounts : plugin.accountsManager.getAccounts()){
            playersCollection.find(new Document("uuid", accounts.getUuid().toString())).forEach((Consumer<Document>) playerDoc -> {
                getPlayerStats(accounts, "playerKills")
                        .thenAcceptAsync(playerKills -> {
                            Bson newPlayerKills = new Document("playerKills", playerKills);
                            Bson updateValue = new Document("$set", newPlayerKills);
                            playersCollection.updateOne(playerDoc, updateValue);
                        })
                        .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                            plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des playerKills.", err);
                            return null; // On oublie pas de retourner null parce que la méthode le demande
                        });
                getPlayerStats(accounts, "playerDeaths")
                        .thenAcceptAsync(playerDeaths -> {
                            Bson newPlayerDeaths = new Document("playerDeaths", playerDeaths);
                            Bson updateValue2 = new Document("$set", newPlayerDeaths);
                            playersCollection.updateOne(playerDoc, updateValue2);
                        })
                        .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                            plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des playerDeaths.", err);
                            return null; // On oublie pas de retourner null parce que la méthode le demande
                        });
                getPlayerStats(accounts, "gameWin")
                        .thenAcceptAsync(gameWin -> {
                            Bson newPlayerGameWin = new Document("gameWin", gameWin);
                            Bson updateValue3 = new Document("$set", newPlayerGameWin);
                            playersCollection.updateOne(playerDoc, updateValue3);
                        })
                        .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                            plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des gameWin.", err);
                            return null; // On oublie pas de retourner null parce que la méthode le demande
                        });
                getPlayerStats(accounts, "gameLoose")
                        .thenAcceptAsync(gameLoose -> {
                            Bson newPlayerGameLoose = new Document("gameLoose", gameLoose);
                            Bson updateValue4 = new Document("$set", newPlayerGameLoose);
                            playersCollection.updateOne(playerDoc, updateValue4);
                        })
                        .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                            plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des gameLoose.", err);
                            return null; // On oublie pas de retourner null parce que la méthode le demande
                        });
            });
        }
    }

    public void AccountToBDD(UUID uuid){
        Accounts account = plugin.accountsManager.getAccountByUUID(uuid);
        if(account == null) {
            System.out.println("Probleme d'account");
            return;
        }
        Document playerDoc = playersCollection.find(new Document("uuid", uuid.toString())).first();
        if(playerDoc != null){
            //update playerKills
            getPlayerStats(account, "playerKills")
                    .thenAcceptAsync(playerKills -> {
                        Bson newPlayerKills = new Document("playerKills", playerKills);
                        Bson updateValue = new Document("$set", newPlayerKills);
                        playersCollection.updateOne(playerDoc, updateValue);
                    })
                    .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                        plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des playerKills.", err);
                        return null; // On oublie pas de retourner null parce que la méthode le demande
                    });
            getPlayerStats(account, "playerDeaths")
                    .thenAcceptAsync(playerDeaths -> {
                        Bson newPlayerDeaths = new Document("playerDeaths", playerDeaths);
                        Bson updateValue2 = new Document("$set", newPlayerDeaths);
                        playersCollection.updateOne(playerDoc, updateValue2);
                    })
                    .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                        plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des playerDeaths.", err);
                        return null; // On oublie pas de retourner null parce que la méthode le demande
                    });
            getPlayerStats(account, "gameWin")
                    .thenAcceptAsync(gameWin -> {
                        Bson newPlayerGameWin = new Document("gameWin", gameWin);
                        Bson updateValue3 = new Document("$set", newPlayerGameWin);
                        playersCollection.updateOne(playerDoc, updateValue3);
                    })
                    .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                        plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des gameWin.", err);
                        return null; // On oublie pas de retourner null parce que la méthode le demande
                    });
            getPlayerStats(account, "gameLoose")
                    .thenAcceptAsync(gameLoose -> {
                        Bson newPlayerGameLoose = new Document("gameLoose", gameLoose);
                        Bson updateValue4 = new Document("$set", newPlayerGameLoose);
                        playersCollection.updateOne(playerDoc, updateValue4);
                    })
                    .exceptionally(err -> { // Cette méthode est call si une exception est levée dans la méthode via un throw (les try-catch gère seuls leurs exceptions)
                        plugin.getLogger().log(Level.SEVERE, "Un problème est survenue lors du chargement des gameLoose.", err);
                        return null; // On oublie pas de retourner null parce que la méthode le demande
                    });
        }

    }
}

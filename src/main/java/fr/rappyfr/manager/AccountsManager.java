package fr.rappyfr.manager;

import fr.rappyfr.MCRankedAPI;
import fr.rappyfr.model.Accounts;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AccountsManager {

    private MCRankedAPI plugin;
    public AccountsManager(MCRankedAPI plugin){
        this.plugin = plugin;
    }

    private final List<Accounts> accounts = new ArrayList<>();

    public Accounts getAccountByUUID(UUID uuid){
        for(Accounts accounts : this.accounts){
            if(accounts.getUuid().equals(uuid)){
                return accounts;
            }
        }
        return null;
    }

    public List<Accounts> getAccounts() {
        return accounts;
    }

    //set playerStats async

}

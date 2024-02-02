package fr.rappyfr.manager;

import fr.rappyfr.MCRanked;
import fr.rappyfr.model.Accounts;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AccountsManager {

    private MCRanked plugin;
    public AccountsManager(MCRanked plugin){
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

package fr.rappyfr;


import fr.rappyfr.manager.AccountsManager;
import fr.rappyfr.manager.PlayerManager;
import fr.rappyfr.sql.MongoAccess;
import fr.rappyfr.manager.MongoManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class MCRankedAPI extends JavaPlugin implements Listener {

    public MongoAccess mongoAccess;
    public MongoManager mongoManager;
    public AccountsManager accountsManager;
    public PlayerManager playerManager;

    private MCRankedAPI instance;



    @Override
    public void onEnable() {
        instance = this;
        mongoAccess = new MongoAccess();
        mongoManager = new MongoManager(this);
        accountsManager = new AccountsManager(this);
        playerManager = new PlayerManager(this, mongoManager, accountsManager);
        Bukkit.getPluginManager().registerEvents(this, this);
        super.onEnable();
    }

    //event in all server
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        Player p = e.getPlayer();
        playerManager.playerJoin(uuid);

    }

    //event in all server
    @EventHandler
    public void onJoin(PlayerQuitEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        mongoManager.AccountToBDD(uuid);

    }

    @Override
    public void onDisable() {
        mongoManager.updateDBFromAccounts();
        super.onDisable();
    }

}
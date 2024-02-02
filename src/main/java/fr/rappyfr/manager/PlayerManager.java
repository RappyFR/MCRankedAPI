package fr.rappyfr.manager;

import fr.rappyfr.MCRanked;
import fr.rappyfr.model.Accounts;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerManager {

    private MCRanked plugin;
    private MongoManager mongoManager;
    private AccountsManager accountsManager;

    public PlayerManager(MCRanked plugin, MongoManager mongoManager, AccountsManager accountsManager){
        this.plugin = plugin;
        this.mongoManager = mongoManager;
        this.accountsManager = accountsManager;
    }

    public void playerJoin(UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        if(p == null)return;
        mongoManager.createPlayerDB(uuid);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Accounts account = accountsManager.getAccountByUUID(uuid);
            account.setPlayerDeaths(account.getPlayerDeaths() + 5);
            p.sendMessage("§fAccountsManager: ");
            p.sendMessage("§d ");
            p.sendMessage("§7playerKills: §f" + account.getPlayerKills());
            p.sendMessage("§7playerDeaths: §f" + account.getPlayerDeaths());
            p.sendMessage("§7gameWin: §f" + account.getGameWin());
            p.sendMessage("§7gameLoose: §f" + account.getGameLoose());
            p.sendMessage("");
        },20L);
    }


}

package fr.rappyfr.model;

import java.util.List;
import java.util.UUID;

public class Accounts {

    private final UUID uuid;
    private Integer playerKills;
    private Integer playerDeaths;
    private Integer gameWin;
    private Integer gameLoose;
    private List<UUID> playerFriends;

    public Accounts(UUID uuid){
        this.uuid = uuid;
        this.playerKills = 0;
        this.playerDeaths = 0;
        this.gameWin = 0;
        this.gameLoose = 0;
    }

    public Integer getPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(Integer playerKills) {
        this.playerKills = playerKills;
    }

    public Integer getPlayerDeaths() {
        return playerDeaths;
    }

    public void setPlayerDeaths(Integer playerDeaths) {
        this.playerDeaths = playerDeaths;
    }

    public Integer getGameWin() {
        return gameWin;
    }

    public void setGameWin(Integer gameWin) {
        this.gameWin = gameWin;
    }

    public Integer getGameLoose() {
        return gameLoose;
    }

    public void setGameLoose(Integer gameLoose) {
        this.gameLoose = gameLoose;
    }

    public void setPlayerFriends(List<UUID> playerFriends) {
        this.playerFriends = playerFriends;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getPlayerFriends() {
        return playerFriends;
    }
}
